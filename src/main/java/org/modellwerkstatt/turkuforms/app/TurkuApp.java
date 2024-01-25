package org.modellwerkstatt.turkuforms.app;


import com.vaadin.flow.component.*;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.dataux.runtime.core.IApplicationController;
import org.modellwerkstatt.dataux.runtime.core.ICommandContainer;
import org.modellwerkstatt.dataux.runtime.core.KeyEvent;
import org.modellwerkstatt.dataux.runtime.core.UxEvent;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.genspecifications.Menu;
import org.modellwerkstatt.dataux.runtime.genspecifications.TileAction;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Application;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_CommandContainerUI;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXUserEnvironment;
import org.modellwerkstatt.objectflow.sdservices.BaseSerdes;
import org.modellwerkstatt.objectflow.serdes.CONV;
import org.modellwerkstatt.objectflow.serdes.IConvSerdes;
import org.modellwerkstatt.turkuforms.auth.NavigationUtil;
import org.modellwerkstatt.turkuforms.auth.ParamInfo;
import org.modellwerkstatt.turkuforms.auth.UserPrincipal;
import org.modellwerkstatt.turkuforms.util.*;
import org.modellwerkstatt.turkuforms.views.*;

import java.util.List;


@PreserveOnRefresh
@SuppressWarnings("unchecked")
public class TurkuApp extends Mainwindow implements IToolkit_Application, ShortcutEventListener, BeforeEnterObserver {
    private TurkuApplicationController applicationController;
    private IOFXUserEnvironment userEnvironment;
    private ITurkuMainTab mainTabImpl;
    private ParamInfo initialStartupParams;



    public TurkuApp() {
        Turku.l("TurkuApp.constructor() - start");
        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        VaadinSession vaadinSession = VaadinSession.getCurrent();

        IGenAppUiModule appUiModule = servlet.getAppBehaviour();
        ITurkuAppFactory factory = servlet.getUiFactory();
        String remoteAddr = vaadinSession.getBrowser().getAddress();

        userEnvironment = Workarounds.getAndClearUserEnvFromUi();
        Turku.l("TurkuApp.constructor() - userEnvironment is " + userEnvironment);

        if (userEnvironment == null) {
            String msg = "API error! Sorry, the application can not be accessed directly via this url (login first?).";
            servlet.logOnPortJTrace(TurkuApp.class.getName(), remoteAddr, msg);
            Notification notification = new Notification(msg);
            notification.setPosition(Notification.Position.MIDDLE);
            notification.open();

        } else if (servlet.getJmxRegistration().getAppTelemetrics().isParDeploymentForwardAll() || servlet.getJmxRegistration().getAppTelemetrics().isParDeploymentForwardNotDirty()) {
            String msg = "API error! Sorry, the application is marked as an old version. You should have been redirected to the newer one... ";
            servlet.logOnPortJTrace(TurkuApp.class.getName(), remoteAddr, msg);
            Notification notification = new Notification(msg);
            notification.setPosition(Notification.Position.MIDDLE);
            notification.open();

        } else {

            init(servlet.getUiFactory(),  userEnvironment.isProlongedUserSession() || factory.isCompactMode(), appUiModule.getShortAppName() + appUiModule.getApplicationVersion());

            if (appInCompactMode) {
                mainTabImpl = new FakeTabSheet(drawerToggle);
            } else {
                mainTabImpl = new MainwindowTabSheet();
            }

            applicationController = new TurkuApplicationController(factory, this, appUiModule, servlet.getJmxRegistration(), IOFXCoreReporter.MoWarePlatform.MOWARE_TURKU);
            applicationController.initializeApplication(servlet.getGuessedServerName(), userEnvironment, remoteAddr, "");

            applicationController.registerOnSession(vaadinSession, userEnvironment.getUserName(), remoteAddr);

            addDetachListener(detachEvent -> {
                if (Workarounds.closedByMissingHearbeat()) {
                    Turku.l("TurkuApp.valueUnbound(): shutdown in progress (" + applicationController.inShutdownMode() + ") or shutdown now.");
                    if (!applicationController.inShutdownMode()) {
                        applicationController.internal_immediatelyShutdown();
                        applicationController.unregisterFromSessionTryInvalidate(vaadinSession, true);
                    }
                }
            });
        }
        Turku.l("TurkuApp.constructor() - done");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // start a command?
        if (initialStartupParams == null) {
            initialStartupParams = new ParamInfo(beforeEnterEvent.getLocation().getQueryParameters());
        } else {
            Notification.show("API Error! Sorry, reloading this application does not work . . .", 5000, Notification.Position.TOP_CENTER);
        }

        if (applicationController != null && initialStartupParams.hasCommandToStart()) {
            applicationController.startCommandByUriAndParam(initialStartupParams.getCommandToStart(), initialStartupParams.getFirstParam());
        }

        Turku.l("TurkuApp.beforeEnter() ");
    }

    @Override
    public void closeWindowAndExit() {
        // This is basically the logout? Unclear if we want to set the principal null
        Turku.l("TurkuApp.closeWindowAndExit()");

        applicationController.unregisterFromSessionTryInvalidate(VaadinSession.getCurrent(), true);

        // TODO?
        String redirectTo = Workarounds.getCurrentTurkuServlet().getUiFactory().getOnLogoutMainLandingPath() + "?" + NavigationUtil.WAS_ACTIVE_LOGOUT_PARAM;
        UI.getCurrent().close();
        UI.getCurrent().getPage().setLocation(redirectTo);
    }
    
    @Override
    public void parDeploymentForwardNow() {
        boolean others = applicationController.unregisterFromSessionTryInvalidate(VaadinSession.getCurrent(), false);
        UI.getCurrent().close();
        UI.getCurrent().getPage().setLocation("/");
    }
    
    @Override
    public void showDialog(DlgType dlgType, String text, IApplicationController.DlgRunnable dlgRunnable) {
        PromptWindow window = new PromptWindow(turkuFactory, userEnvironment.getLangIndex());
        window.simplePrompt(dlgType, text, dlgRunnable);
    }

    @Override
    public void showProblemsDialog(List<IOFXProblem> list, IApplicationController.DlgRunnable dlgRunnable) {
        PromptWindow window = new PromptWindow(turkuFactory, userEnvironment.getLangIndex());
        window.simpleProblemDialog(list, dlgRunnable);
    }

    @Override
    public void showGraphDebugger(List<Object> list, String s) {
        String content;
        if (list.size() > 0) {
            IConvSerdes serdes = CONV.stringSer(list.get(0).getClass(), CONV.CONV_DEFAULT_EN);
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
    public void lockInterface(boolean b) {

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

        if (appInCompactMode) {
            addDrawerMenu(start.getAllItems());

        } else {
            SubMenu startMenu = addToMainMenu(start, turkuFactory.getSystemLabel(langIndex, MoWareTranslations.Key.START));
            Component vaadinPowerOff = Workarounds.createIconWithCollection(turkuFactory.translateIconName("mainmenu_logout"));
            startMenu.addItem(vaadinPowerOff, event -> { exitRequestedFromMenu(); });

            addToMainMenu(extra, turkuFactory.getSystemLabel(langIndex, MoWareTranslations.Key.EXTRA));
            SubMenu helpMenu = addToMainMenu(help, turkuFactory.getSystemLabel(langIndex, MoWareTranslations.Key.HELP));
            helpMenu.addItem(turkuFactory.getSystemLabel(langIndex, MoWareTranslations.Key.ABOUT), event -> {
                String text = applicationController.appUserSystemVersionInfo() + "\n\n" + getTurkuVersionInfo();
                showDialog(DlgType.INFO_SMALL, text, null);
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
    public void addTab(IToolkit_CommandContainerUI cmdUiTab) {
        Turku.l("TurkuApp.addTab()");
        CmdUiTab tab = (CmdUiTab) cmdUiTab;
        if (this.getContent() != mainTabImpl.getAsComponent()) {
            this.setContent(mainTabImpl.getAsComponent());
        }
        mainTabImpl.addTab(tab);
        setOptionalTabTitleInNavbar(mainTabImpl.getTabTitle());
    }

    @Override
    public void focusTab(IToolkit_CommandContainerUI cmdUiTab) {
        Turku.l("TurkuApp.focusTab()");
        CmdUiTab tab = (CmdUiTab) cmdUiTab;
        mainTabImpl.focusTab(tab);
        setOptionalTabTitleInNavbar(mainTabImpl.getTabTitle());
    }

    @Override
    public void ensureTabClosed(IToolkit_CommandContainerUI cmdUiTab) {
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
        applicationController.onExitRequestedEvent(false);
    }

    public TurkuApplicationController getApplicationController() { return applicationController; }



}
