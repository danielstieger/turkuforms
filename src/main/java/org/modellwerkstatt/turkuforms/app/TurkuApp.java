package org.modellwerkstatt.turkuforms.app;


import com.vaadin.flow.component.*;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.*;
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
import org.modellwerkstatt.turkuforms.auth.AuthUtil;
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

        userEnvironment = Workarounds.getAndClearUserEnvFromUi();
        Turku.l("TurkuApp.constructor() - userEnvironment is " + userEnvironment);

        if (userEnvironment == null) {
            Notification notification = new Notification("API error! Sorry, the application can not be accessed directly via this url . . .");
            notification.setPosition(Notification.Position.MIDDLE);
            notification.open();

        } else {

            if (factory.isCompactMode()) {
                mainTabImpl = new FakeTabSheet();
            } else {
                mainTabImpl = new MainwindowTabSheet();
            }

            init(servlet.getUiFactory(), appUiModule.getShortAppName() + appUiModule.getApplicationVersion());

            String remoteAddr = vaadinSession.getBrowser().getAddress();
            applicationController = new TurkuApplicationController(factory, this, appUiModule, servlet.getJmxRegistration(), IOFXCoreReporter.MoWarePlatform.MOWARE_TURKU);
            applicationController.initializeApplication(servlet.getGuessedServerName(), userEnvironment, remoteAddr, "");

            applicationController.registerOnSession(vaadinSession, userEnvironment.getUserName(), remoteAddr);

            addDetachListener(detachEvent -> {
                if (Workarounds.closedByMissingHearbeat()) {
                    Turku.l("TurkuApp.valueUnbound(): shutdown in progress (" + applicationController.inShutdownMode() + ") or shutdown now.");
                    if (!applicationController.inShutdownMode()) {
                        applicationController.internal_immediatelyShutdown();
                        applicationController.unregisterFromSession(vaadinSession);
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
        applicationController.internal_immediatelyShutdown();
        applicationController.unregisterFromSession(VaadinSession.getCurrent());
        // This requires a re-login
        UserPrincipal.setUserPrincipal(VaadinSession.getCurrent(), null);

        String redirectTo = Workarounds.getCurrentTurkuServlet().getUiFactory().getRedirectAfterLogoutPath() + AuthUtil.LOGOUT_POSTFIX;
        UI.getCurrent().getPage().setLocation(redirectTo);
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
        Turku.l("TurkuApp.lockInterface() " + b);
    }

    @Override
    public void setCurrentTabModal(boolean modal) {

        if (mainmenuBar != null) { mainmenuBar.setEnabled(! modal); }
        drawerToggle.setEnabled(! modal);
        if (modal) { setDrawerOpened(false); }
        mainTabImpl.setModal(modal);
    }

    @Override
    public void setMenuAndInit(int langIndex, Menu start, Menu extra, Menu help) {

        addDrawerMenu(start.getAllItems());

        if (!turkuFactory.isCompactMode()) {
            SubMenu startMenu = addToMainMenu(start, turkuFactory.getSystemLabel(langIndex, MoWareTranslations.Key.START));
            Component vaadinPowerOff = Workarounds.createIconWithCollection(turkuFactory.translateIconName("mainmenu_logout"));
            startMenu.addItem(vaadinPowerOff, event -> { exitRequestedFromMenu(); });

            addToMainMenu(extra, turkuFactory.getSystemLabel(langIndex, MoWareTranslations.Key.EXTRA));
            addToMainMenu(help, turkuFactory.getSystemLabel(langIndex, MoWareTranslations.Key.HELP));
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
        Turku.l("TurkuApp.onShortcut() " + event);
        if (event.matches(Key.F6, KeyModifier.SHIFT)) { keyName = "DBG_SESSION"; }
        else if (event.matches(Key.F5, KeyModifier.SHIFT)) { keyName = "DBG_GRAPH"; }
        else { keyName = HkTranslate.trans(event.getKey()); }

        applicationController.onKeyPressEvent(new KeyEvent(Defs.hkNeedsCrtl(keyName), keyName));
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
