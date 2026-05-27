package org.modellwerkstatt.turkuforms.core2;


import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.dataux.runtime.core.*;
import org.modellwerkstatt.dataux.runtime.genspecification.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.genspecification.MenuAction;
import org.modellwerkstatt.dataux.runtime.genspecification.Tile;
import org.modellwerkstatt.dataux.runtime.telemetrics.Dux;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_CommandContainerUi;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_MainWindow;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.*;
import org.modellwerkstatt.objectflow.sdservices.BaseSerdes;
import org.modellwerkstatt.objectflow.serdes.*;
import org.modellwerkstatt.turkuforms.auth.NavigationUtil;
import org.modellwerkstatt.turkuforms.auth.ParamInfo;
import org.modellwerkstatt.turkuforms.auth.UserPrincipal;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.core.SessionUtil;
import org.modellwerkstatt.turkuforms.core.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.*;
import org.modellwerkstatt.turkuforms.views.*;

import java.util.List;

import static org.modellwerkstatt.turkuforms.core.ITurkuAppFactory.TURKU_PORTJ;

/*
 * No Preserve on refresh, since I was not able to get any mechanism working to check
 * if the pagehide -> beacon stems from a reload...
 *
 */
@SuppressWarnings("unchecked")
public class TurkuMainWin2 extends Mainwindow implements IToolkit_MainWindow, ShortcutEventListener, BeforeEnterObserver {
    private TurkuAppCrtl2 applicationController;
    private IOFXUserEnvironment userEnvironment;
    private ITurkuMainTab mainTabImpl;
    private ParamInfo initialStartupParams;



    public TurkuMainWin2() {
        Dux.hl("Main window initialized, heartbeat interval " + VaadinService.getCurrent().getDeploymentConfiguration().getHeartbeatInterval());

    }


    private void startupAppCrtl(OFXUrlParams urlParams){

        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        VaadinSession vaadinSession = VaadinSession.getCurrent();

        IGenAppUiModule appUiModule = servlet.getAppBehaviour();
        ITurkuAppFactory factory = servlet.getUiFactory();
        String remoteAddr = factory.getRemoteAddr();

        userEnvironment = NavigationUtil.getAndClearUserEnvFromUi();
        Dux.hl("Starting application controller for " + userEnvironment);

        if (userEnvironment == null) {
            UserPrincipal userPrincipal = SessionUtil.getUserPrincipal(vaadinSession);
            UserEnvironmentInformation tempEnv = new UserEnvironmentInformation();
            String msg = NavigationUtil.loginViaLoginCrtl(servlet, vaadinSession, tempEnv, userPrincipal.getUserName(), userPrincipal.getPassword());

            if (msg == null) {
                userEnvironment = tempEnv;

            } else {
                quickUserInfo(msg);

            }
        }

        if (userEnvironment != null) {
            init(servlet.getUiFactory(),  userEnvironment.isCompactMode() || factory.isCompactMode(), appUiModule.getShortAppName() + appUiModule.getApplicationVersion(), userEnvironment.getBrandingId());

            if (appInCompactMode) {
                mainTabImpl = new TabSheetFake(drawerToggle);
            } else {
                mainTabImpl = new TabSheetMDI();
            }

            userEnvironment.adjustDeviceId("" + vaadinSession.hashCode() + " / " + this.hashCode());
            applicationController = new TurkuAppCrtl2(factory, this, appUiModule, servlet.getJmxRegistration(), IOFXCoreReporter.MoWarePlatform.MOWARE_TURKU);
            // url overwrite not supported with turku
            applicationController.initializeApplication(urlParams, servlet.getGuessedServerName(), userEnvironment, remoteAddr, "");

            applicationController.registerOnSessionSetTimeout(vaadinSession, userEnvironment.getUserName(), remoteAddr);
        }

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        String requestedUrl =  beforeEnterEvent.getLocation().getPath();
        OFXUrlParams params = new OFXUrlParams();
        params.parse(requestedUrl);


        if (applicationController == null) {
            startupAppCrtl(params);

        } else {
            Turku.logWithServlet(TurkuMainWin2.class.getName(), "This can not happen. We got a BeforeEnter without a appCrtl (it is null).", null);

        }

        if (params.hasCmdName()) {
            applicationController.execAfterStartupOrNow(() -> {

                IOFXCmdModule.CmdUrlDefaults def = applicationController.getUrlDefaultFor(turkuFactory.getAllCmdUrlDefaults(), params);
                if (def != null) {
                    BasisCmdStart start = applicationController.cmdStartForUrlDefault(def, params, applicationController.getMainWindowEvalSession());

                    if (start == null) {
                        quickUserInfo(String.format(turkuFactory.getSystemLabel(userEnvironment.getLangIndex(), MoWareTranslations.Key.CMD_NOT_ENABLED), def.url));

                    } else {
                        applicationController.receiveAndProcess(start);
                    }

                } else {
                    quickUserInfo(turkuFactory.getSystemLabel(userEnvironment.getLangIndex(), MoWareTranslations.Key.CMD_NOT_AVAILABLE2));
                }

            });
        }

        Dux.hl("Navigation params: " + params.getCmdName() + " / " + params.numParams() + ", command running=" + applicationController.isCommandRunning());

        Dux.hl("Before enter finished for " + applicationController);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        Dux.hl("Attach started.");

        super.onAttach(attachEvent);

        if (Workarounds.getCurrentTurkuServlet().isDisableBrowserContextMenu()) {
            this.getElement().executeJs("turku.disableBrowserContextMenu()");
        }

        String servletUrl = Workarounds.getCurrentTurkuServlet().getActualServletUrl();
        this.getElement().executeJs("turku.installBeacon($0, $1)", servletUrl, UI.getCurrent().getUIId());

        if (turkuFactory.getDefaultScaling() != null) {
            this.getElement().executeJs("turku.checkForDefaultScaling($0)", turkuFactory.getDefaultScaling().toPlainString());
        }
    }

    @Override
    public void installCloseConfirmQuestion(boolean installOrRemove) {
        this.getElement().executeJs("turku.installCloseConfirm($0)", installOrRemove);
    }

    @Override
    public void closeApplicationAndExit() {
        // This is basically the logout? Unclear if we want to set the principal null
        Dux.hl("Closing application " + applicationController + ", attached=" + isAttached());

        applicationController.logMowareTracing("","", TURKU_PORTJ, "User initiated a closeAppAndExit()","");
        applicationController.unregisterFromSessionTryInvalidate(VaadinSession.getCurrent(), false);

        if (isAttached()) {
            String redirectTo = Workarounds.getCurrentTurkuServlet().getUiFactory().getOnLogoutMainLandingPath() + "?" + NavigationUtil.WAS_ACTIVE_LOGOUT_PARAM;
            getUI().get().getPage().setLocation(redirectTo);
            getUI().get().close();
        }
    }

    @Override
    public void parDeploymentForwardNow() {
        boolean wasLastCrtl = applicationController.unregisterFromSessionTryInvalidate(VaadinSession.getCurrent(), true);
        // leads to valueUnbound() in turn closing app crtl

        Dux.hl("Parallel deployment forward, invalidated=" + wasLastCrtl);

        if (! wasLastCrtl) {
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
        // Dux.hl("T2App.showDialog() " + OFXConsoleHelper._____organizeCurrentStacktrace_____());

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
    public void setAppInfo(String appName, String version, String dynTitle, int brandingId) {
        super.setSysInfo(appName+ " " + version);
        super.setNavbarTitleDiv(appName + " " + dynTitle);
        super.setUserInfo(userEnvironment.getUserName());
        super.adjustBranding(brandingId);
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
    public void setMenuAndInit(int langIndex, MenuAction start, MenuAction extra, MenuAction help) {
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
        mainTabImpl.addTabSelectedChangeListener( i ->

                applicationController.onTabChangeEvent(i));

    }

    @Override
    public void showTiles(List<Tile> tilesList, boolean resetUrl) {
        Dux.hl("Showing tiles, resetUrl=" + resetUrl);

        if (mainTabImpl.hasOpenTabs()) {
            throw new RuntimeException("We do have open tabs but requested to show tiles?");
        }

        if (resetUrl) {
            UI.getCurrent().getPage().getHistory().replaceState(null, "");
        }

        setContent(updateTiles(tilesList));
    }

    @Override
    public void addTab(IToolkit_CommandContainerUi cmdUiTab) {
        Dux.hl("Adding tab.");
        CmdUiTab tab = (CmdUiTab) cmdUiTab;
        if (this.getContent() != mainTabImpl.getAsComponent()) {
            this.setContent(mainTabImpl.getAsComponent());
        }
        tab.setMainTabForAdjustments(mainTabImpl);

        mainTabImpl.addTab(tab);
        setOptionalTabTitleInNavbar(mainTabImpl.getTitleForNavbar());
    }

    @Override
    public void focusTab(IToolkit_CommandContainerUi cmdUiTab) {
        Dux.hl("Focusing tab.");
        CmdUiTab tab = (CmdUiTab) cmdUiTab;
        mainTabImpl.focusTab(tab);
        setOptionalTabTitleInNavbar(mainTabImpl.getTitleForNavbar());
    }

    @Override
    public void ensureTabClosed(IToolkit_CommandContainerUi cmdUiTab) {
        Dux.hl("Ensuring tab is closed.");
        CmdUiTab tab = (CmdUiTab) cmdUiTab;
        mainTabImpl.closeTab(tab);
        setOptionalTabTitleInNavbar("");
    }

    @Override
    public void onShortcut(ShortcutEvent event) {
        String keyName;
        Dux.hl("Shortcut received: " + event.getKeyModifiers() + " " + event.getKey().getKeys());
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
        Turku.logWithServlet(TurkuMainWin2.class.getName(), "Foreground / Background processing not supported by Turkuforms", ise);
        throw ise;
    }

    @Override
    public void execEventInForeground(ICommandContainer iCommandContainer, UxEvent uxEvent) {
        IllegalStateException ise = new IllegalStateException("Foreground / Background processing not supported by Turkuforms");
        Turku.logWithServlet(TurkuMainWin2.class.getName(), "Foreground / Background processing not supported by Turkuforms", ise);
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

    public TurkuAppCrtl2 getApplicationController() { return applicationController; }

    @Override
    public void distributeTermEventOnOtherInstance(GlobalCmdTermEvent globalCmdTermEvent) {
        applicationController.distributeTermEventOnOtherCrtls(globalCmdTermEvent);
    }

    protected void quickUserInfo(String msg) {
        UI.getCurrent().access(() -> {

            Notification notification = new Notification();
            notification.setPosition(Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            notification.setDuration(20000);


            Div content = new Div();
            Div text = new Div();
            text.setText(Workarounds.getCurrentTurkuServlet().getAppNameVersion());
            text.addClassName("QuickInfoHeader");
            content.add(text);

            text = new Div();
            text.setText(msg);
            text.addClassName("QuickInfoMsg");
            content.add(text);

            Button closeButton = new Button(new Icon("lumo", "cross"));
            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
            closeButton.getElement().setAttribute("aria-label", "Close");
            closeButton.addClickListener(event -> {
                notification.close();
            });

            HorizontalLayout layout = new HorizontalLayout(content, closeButton);
            layout.setAlignItems(FlexComponent.Alignment.CENTER);

            notification.add(layout);
            notification.open();

        });
    }

}
