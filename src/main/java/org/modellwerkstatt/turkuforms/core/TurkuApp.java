package org.modellwerkstatt.turkuforms.core;


import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.dataux.runtime.core.IApplication;
import org.modellwerkstatt.dataux.runtime.core.ICommandContainer;
import org.modellwerkstatt.dataux.runtime.core.KeyEvent;
import org.modellwerkstatt.dataux.runtime.core.UxEvent;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.genspecifications.Menu;
import org.modellwerkstatt.dataux.runtime.genspecifications.TileAction;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_CommandContainerUi;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_MainWindow;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXUserEnvironment;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.objectflow.sdservices.BaseSerdes;
import org.modellwerkstatt.objectflow.serdes.*;
import org.modellwerkstatt.turkuforms.auth.NavigationUtil;
import org.modellwerkstatt.turkuforms.auth.ParamInfo;
import org.modellwerkstatt.turkuforms.auth.UserPrincipal;
import org.modellwerkstatt.turkuforms.util.*;
import org.modellwerkstatt.turkuforms.views.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.modellwerkstatt.turkuforms.core.TurkuApplicationController.TURKU_PORTJ;


@PreserveOnRefresh
@SuppressWarnings("unchecked")
public class TurkuApp extends Mainwindow implements IToolkit_MainWindow, ShortcutEventListener, BeforeEnterObserver {
    private TurkuApplicationController applicationController;
    private IOFXUserEnvironment userEnvironment;
    private ITurkuMainTab mainTabImpl;
    private ParamInfo initialStartupParams;



    public TurkuApp() {
        // Turku.l("TurkuApp.constructor() - start");
        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        VaadinSession vaadinSession = VaadinSession.getCurrent();

        IGenAppUiModule appUiModule = servlet.getAppBehaviour();
        ITurkuAppFactory factory = servlet.getUiFactory();
        String remoteAddr = factory.getRemoteAddr();

        userEnvironment = NavigationUtil.getAndClearUserEnvFromUi();
        // Turku.l("TurkuApp.constructor() - userEnvironment is " + userEnvironment);

        if (userEnvironment == null) {
            String msg = "UserEnvironment to pick up was null, redirecting to /login";
            servlet.logOnPortJTrace(TurkuApp.class.getName(), remoteAddr, msg, null);
            NavigationUtil.absolutNavi(TurkuServlet.LOGIN_ROUTE);

        } else if (servlet.getJmxRegistration().getAppTelemetrics().isParDeploymentForwardGracefully() || servlet.getJmxRegistration().getAppTelemetrics().isParDeploymentForwardImmediate()) {
            String msg = "API error! Sorry, the application is marked as an old version. You should have been redirected to the newer one... ";
            servlet.logOnPortJTrace(TurkuApp.class.getName(), remoteAddr, msg, null);

            quickUserInfo(msg);
            Turku.l("TurkuApp.constructor() par deploy forward pls. " + msg);

        } else {

            init(servlet.getUiFactory(),  userEnvironment.isCompactMode() || factory.isCompactMode(), appUiModule.getShortAppName() + appUiModule.getApplicationVersion());

            if (appInCompactMode) {
                mainTabImpl = new TabSheetFake(drawerToggle);
            } else {
                mainTabImpl = new TabSheetMDI();
            }

            userEnvironment.adjustDeviceId("" + vaadinSession.hashCode());
            applicationController = new TurkuApplicationController(factory, this, appUiModule, servlet.getJmxRegistration(), IOFXCoreReporter.MoWarePlatform.MOWARE_TURKU);
            applicationController.initializeApplication(servlet.getGuessedServerName(), userEnvironment, remoteAddr, "");

            applicationController.registerOnSessionSetTimeout(vaadinSession, userEnvironment.getUserName(), remoteAddr);


        }

        Turku.l("TurkuApp.constructor() - done");
    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

        // start a command?
        if (initialStartupParams == null) {
            // Turku.l("TurkuApp.beforeEnter() path=" + beforeEnterEvent.getLocation().getPath() + " and queryString " + beforeEnterEvent.getLocation().getQueryParameters().getQueryString());
            QueryParameters query = beforeEnterEvent.getLocation().getQueryParameters();
            if (query.getParameters().size() == 0) {
                // check :path variable
                String queryInPath = beforeEnterEvent.getRouteParameters().get("path").orElse("");
                if (queryInPath.startsWith("?")) {
                    query = QueryParameters.fromString(queryInPath.substring(1));
                }
            }


            initialStartupParams = new ParamInfo(query);
            if (applicationController != null && initialStartupParams.hasCommandToStartLegacy()) {
                UI.getCurrent().access(() -> {
                    applicationController.startCommandByUriAndParam(initialStartupParams.getCommandToStartLegacy(), initialStartupParams.getFirstParamLegacy());

                });
            }

        } else if (applicationController != null) {
            // controller closed by beacon - typically
            UI.getCurrent().removeAll();
            applicationController.internal_immediatelyShutdown();
            quickUserInfo("Reloading this web page does not work. The application was destroyed.");
        }

        Turku.l("TurkuApp.beforeEnter() done for " + applicationController);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        if (Workarounds.getCurrentTurkuServlet().isDisableBrowserContextMenu()) {
            this.getElement().executeJs("turku.disableBrowserContextMenu()");
        }

        String servletUrl = Workarounds.getCurrentTurkuServlet().getActualServletUrl();
        this.getElement().executeJs("turku.installBeacon($0, $1)", servletUrl, UI.getCurrent().getUIId());
    }

    @Override
    public void installCloseConfirmQuestion(boolean installOrRemove) {
        this.getElement().executeJs("turku.installCloseConfirm($0)", installOrRemove);
    }


    /*
     * No longer using heart beat mechanism, since missing heartbeats are no longer possible.
     * We are relying on the beacon api.
     *
    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);

        boolean closedByHeartBeat = Workarounds.closedByMissingHearbeat();
        Turku.l("TurkuApp.onDetach(): closedByHeartBeat "+ closedByHeartBeat);

        if (closedByHeartBeat && applicationController != null) {
            // might be null in case app was not initialized at all
            applicationController.closeAppCrtlMissingHearbeatOrBeacon(VaadinSession.getCurrent());
        }
    } */

    @Override
    public void closeApplicationAndExit() {
        // This is basically the logout? Unclear if we want to set the principal null
        Turku.l("TurkuApp.closeApplicationAndExit()");

        applicationController.logMowareTracing("","", TURKU_PORTJ, "User initiated a closeAppAndExit()","");
        applicationController.unregisterFromSessionTryInvalidate(VaadinSession.getCurrent(), false);

        String redirectTo = Workarounds.getCurrentTurkuServlet().getUiFactory().getOnLogoutMainLandingPath() + "?" + NavigationUtil.WAS_ACTIVE_LOGOUT_PARAM;

        UI.getCurrent().getPage().setLocation(redirectTo);
        UI.getCurrent().close();
    }
    
    @Override
    public void parDeploymentForwardNow() {
        boolean invalidated = applicationController.unregisterFromSessionTryInvalidate(VaadinSession.getCurrent(), true);
        // leads to valueUnbound() in turn closing app crtl

        Turku.l("parDeploymentForwardNow() invalidated is " + invalidated);

        if (! invalidated) {
            NavigationUtil.absolutNavi(NavigationUtil.OTHER_TABS_OPEN);
            UI.getCurrent().close();

        } else {
            String userNameParam = initialStartupParams.hasUsername() ? initialStartupParams.getOnlyUsernameParam() : "";
            NavigationUtil.absolutNavi(TurkuServlet.LOGIN_ROUTE + userNameParam);
            UI.getCurrent().close();

        }
    }
    
    @Override
    public void showDialog(DlgType dlgType, String text, IApplication.DlgRunnable dlgRunnable) {
        // Turku.l("TurkuApp.showDialog() " + OFXConsoleHelper._____organizeCurrentStacktrace_____());

        PromptWindow window = new PromptWindow(turkuFactory, userEnvironment.getLangIndex());
        window.simplePrompt(dlgType, text, dlgRunnable);
    }

    @Override
    public void showProblemsDialog(List<IOFXProblem> list, IApplication.DlgRunnable dlgRunnable) {
        PromptWindow window = new PromptWindow(turkuFactory, userEnvironment.getLangIndex());
        window.simpleProblemDialog(list, dlgRunnable);
    }

    @Override
    public void showGraphDebugger(List<Object> list, String s) {
        String content;
        if (list.size() > 0) {
            IConvFormatOptions myoptions = new ConvStdFormatters(new ConvFormatOptions("hh:mm:ss dd.MM.yy",
                    "dd.MM.yy",
                    "#0.00",
                    "en",
                    new IConvFormatOptions.Mode[]{IConvFormatOptions.Mode.ALL_PROPERTIES_NECESSARY, IConvFormatOptions.Mode.PRETTY}
            ));

            IConvSerdes serdes = CONV.jsonSerDes(list.get(0).getClass(), myoptions);
            ((BaseSerdes) serdes).expectArrayAtRoot();
            Object[] asArray = list.toArray();
            content = serdes.ser(asArray);

        } else {
            content = "The current graph does not contain any object. Bound list size is 0.";
        }

        PromptWindow window = new PromptWindow(turkuFactory, userEnvironment.getLangIndex());
        window.simplePrompt(DlgType.INFO_LARGE, content, null);
    }

    @Override
    public void addStatusInformation(String s) {
        Notification.show(s, 3000, Notification.Position.BOTTOM_START);
    }

    @Override
    public void setToastMessage(String s) {
        Notification.show(s, 4000, Notification.Position.TOP_CENTER);
    }

    @Override
    public void setAppInfo(String appName, String version, String dynTitle) {
        super.setSysInfo(appName+ " " + version);
        super.setNavbarTitleDiv(appName + " " + dynTitle);
        super.setUserInfo(userEnvironment.getUserName());
    }

    @Override
    public void setCurrentTabModal(boolean modal) {
        if (! appInCompactMode) {
            mainmenuBar.setEnabled(!modal);
        } else {
            drawerToggle.setEnabled(!modal);
            if (modal) { setDrawerOpened(false); }
        }

        mainTabImpl.setModal(modal);
    }

    @Override
    public void setMenuAndInit(int langIndex, Menu start, Menu extra, Menu help) {
        String advancedInfo = applicationController.appUserSystemVersionInfo() + "\n\n" + getTurkuVersionInfo();

        if (appInCompactMode) {
            addDrawerMenu(start.getAllItems());
            Tooltip.forComponent(userInfoLabel).setText(advancedInfo);

        } else {
            SubMenu startMenu = addToMainMenu(start, turkuFactory.getSystemLabel(langIndex, MoWareTranslations.Key.START));
            Component vaadinPowerOff = Workarounds.createIconWithCollection(turkuFactory.translateIconName("mainmenu_logout"), false);
            startMenu.addItem(vaadinPowerOff, event -> { exitRequestedFromMenu(); });

            addToMainMenu(extra, turkuFactory.getSystemLabel(langIndex, MoWareTranslations.Key.EXTRA));
            SubMenu helpMenu = addToMainMenu(help, turkuFactory.getSystemLabel(langIndex, MoWareTranslations.Key.HELP));
            helpMenu.addItem(turkuFactory.getSystemLabel(langIndex, MoWareTranslations.Key.ABOUT), event -> {
                showDialog(DlgType.INFO_SMALL, advancedInfo, null);
            });
        }

        // initialize other stuff
        mainTabImpl.addTabSelectedChangeListener( i -> applicationController.onTabChangeEvent(i));

    }

    @Override
    public void showTiles(List<TileAction> tilesList) {
        Turku.l("TurkuApp.showTiles()");
        if (mainTabImpl.hasOpenTabs()) {
            throw new RuntimeException("We do have open tabs but requested to show tiles?");
        }
        setContent(updateTiles(tilesList));
    }

    @Override
    public void addTab(IToolkit_CommandContainerUi cmdUiTab) {
        Turku.l("TurkuApp.addTab()");
        CmdUiTab tab = (CmdUiTab) cmdUiTab;
        if (this.getContent() != mainTabImpl.getAsComponent()) {
            this.setContent(mainTabImpl.getAsComponent());
        }
        tab.setMainTabForColorAdjustments(mainTabImpl);

        mainTabImpl.addTab(tab);
        setOptionalTabTitleInNavbar(mainTabImpl.getTitleForNavbar());
    }

    @Override
    public void focusTab(IToolkit_CommandContainerUi cmdUiTab) {
        Turku.l("TurkuApp.focusTab()");
        CmdUiTab tab = (CmdUiTab) cmdUiTab;
        mainTabImpl.focusTab(tab);
        setOptionalTabTitleInNavbar(mainTabImpl.getTitleForNavbar());
    }

    @Override
    public void ensureTabClosed(IToolkit_CommandContainerUi cmdUiTab) {
        Turku.l("TurkuApp.ensureTabClosed()");
        CmdUiTab tab = (CmdUiTab) cmdUiTab;
        mainTabImpl.closeTab(tab);
        setOptionalTabTitleInNavbar("");
    }

    @Override
    public void onShortcut(ShortcutEvent event) {
        String keyName;
        Turku.l("TurkuApp.onShortcut() " + event.getKeyModifiers()+ " " + event.getKey().getKeys());
        if (event.matches(Key.F6, KeyModifier.SHIFT)) { keyName = "DBG_SESSION"; }
        else if (event.matches(Key.F5, KeyModifier.SHIFT)) { keyName = "DBG_GRAPH"; }
        else { keyName = HkTranslate.trans(event.getKey()); }

        if (mainTabImpl.hasOpenTabs() && appInCompactMode) {
            // do not forward global hk (start cmd) when cmd is running
        } else {
            applicationController.onKeyPressEvent(new KeyEvent(Defs.hkNeedsCrtl(keyName), keyName));
        }
    }

    @Override
    public void ensureHotkeyAvailable(List<String> list) {
        for (String hk: list){
            Peculiar.useGlobalShortcutHk(this, hk, this);
        }
        Peculiar.installMowareAddonHotkeys(this, this);
    }

    @Override
    public void execEventInBackground(ICommandContainer iCommandContainer, Runnable runnable) {
        IllegalStateException ise = new IllegalStateException("Foreground / Background processing not supported by Turkuforms");
        Turku.logWithServlet(TurkuApp.class.getName(), "Foreground / Background processing not supported by Turkuforms", ise);
        throw ise;
    }

    @Override
    public void execEventInForeground(ICommandContainer iCommandContainer, UxEvent uxEvent) {
        IllegalStateException ise = new IllegalStateException("Foreground / Background processing not supported by Turkuforms");
        Turku.logWithServlet(TurkuApp.class.getName(), "Foreground / Background processing not supported by Turkuforms", ise);
        throw ise;
    }

    @Override
    public boolean inUiThread() {
        return true;
    }

    @Override
    protected void exitRequestedFromMenu() {
        applicationController.onExitRequested(false);
    }

    public TurkuApplicationController getApplicationController() { return applicationController; }

    protected void quickUserInfo(String msg) {
        UI.getCurrent().access(() -> {

            Notification notification = new Notification();
            notification.setPosition(Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            notification.setDuration(20000);

            Text text = new Text(msg);

            Button closeButton = new Button(new Icon("lumo", "cross"));
            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
            closeButton.getElement().setAttribute("aria-label", "Close");
            closeButton.addClickListener(event -> {
                notification.close();
            });

            HorizontalLayout layout = new HorizontalLayout(text, closeButton);
            layout.setAlignItems(FlexComponent.Alignment.CENTER);

            notification.add(layout);
            notification.open();

        });
    }

}
