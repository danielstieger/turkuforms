package org.modellwerkstatt.turkuforms.sdi;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import org.modellwerkstatt.dataux.runtime.core.IApplication;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_CommandContainerUi;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_MainWindow;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Window;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXUserEnvironment;
import org.modellwerkstatt.objectflow.runtime.OFXConsoleHelper;
import org.modellwerkstatt.objectflow.runtime.OFXUrlParams;
import org.modellwerkstatt.objectflow.sdservices.BaseSerdes;
import org.modellwerkstatt.objectflow.serdes.CONV;
import org.modellwerkstatt.objectflow.serdes.IConvSerdes;
import org.modellwerkstatt.turkuforms.auth.NavigationUtil;
import org.modellwerkstatt.turkuforms.core.IAppCrtlAccess;
import org.modellwerkstatt.turkuforms.core.TurkuApp;
import org.modellwerkstatt.turkuforms.core.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;
import org.modellwerkstatt.turkuforms.views.CmdUiTab;
import org.modellwerkstatt.turkuforms.views.PromptWindow;

import java.util.List;


public class BrowserTab extends SdiLayout implements IToolkit_Window, BeforeEnterObserver {

    protected IOFXUserEnvironment userEnvironment;
    protected OFXUrlParams params;
    protected SdiAppCrtl appCrtl;
    protected CmdUiTab theTab;

    public BrowserTab() {
        super();

        setWidthFull();
        setHeightFull();
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Turku.l("BrowserTab.beforeEnter() " + event);

        //  check if we are logged id or redirect here to login ...
        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        turkuFactory = servlet.getUiFactory();

        navbarTitle = servlet.getAppNameVersion();
        appCrtl = SdiAppCrtl.getAppCrtl();

        if (appCrtl == null) {
            // user env to pick up after a login ?
            IOFXUserEnvironment userEnv = NavigationUtil.getAndClearUserEnvFromUi();

            if (userEnv == null) {
                // nope - not logged in ... this can not happen, routes are not configured correctly?
                String msg = "API error! The application was accessible via url, but user is not LOGGED IN!";
                servlet.logOnPortJTrace(TurkuApp.class.getName(), turkuFactory.getRemoteAddr(), msg);
                quickUserInfo(msg);
                return; // -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
            }

            appCrtl = SdiAppCrtl.createAppCrtl(userEnv);
        }

        userEnvironment = appCrtl.getUserEnvironment();

        params = new OFXUrlParams(event.getLocation().getSegments());

        String msg = null;
        if (appCrtl.wasPickupCmdThenStart(this, params)) {
            // nothing to do here
            Turku.l("BrowserTab.beforeEnter() did a pickup for the appCrtl");

        } else if (params.hasCmdName()) {
            Turku.l("BrowserTab.beforeEnter() starting command '" + params.getCmdName() + "'");
            msg = appCrtl.startCommand(this, params);
        }

        if (!params.hasCmdName() || msg != null) {
            landingPage = new LandingPage(servlet.getAppNameVersion());
            landingPage.setMainMenu(appCrtl.constructAndInitLandingMenu());
            if (msg != null) { landingPage.setMessage(msg); }
            add(landingPage);
        }
    }

    @Override
    public void showDialog(IToolkit_MainWindow.DlgType dlgType, String text, IApplication.DlgRunnable dlgRunnable) {
        Turku.l("TurkuApp.showDialog() " + text);

        PromptWindow window = new PromptWindow(turkuFactory, userEnvironment.getLangIndex());
        window.simplePrompt(dlgType, text, dlgRunnable);
    }

    @Override
    public void showProblemsDialog(List<IOFXProblem> list, IApplication.DlgRunnable dlgRunnable) {
        Turku.l("TurkuApp.showProblemsDialog() " + OFXConsoleHelper.asSimpleString(list));

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
        window.simplePrompt(IToolkit_MainWindow.DlgType.INFO_LARGE, content, null);
    }

    @Override
    public void addTab(IToolkit_CommandContainerUi iToolkit_commandContainerUi) {
        CmdUiTab uiTab = (CmdUiTab) iToolkit_commandContainerUi;

        if (theTab == null) {
            navbarTitle = uiTab.getWindowTitle();
            theTab = uiTab;
            removeAll();
            add(uiTab);

        } else {
            String urlToOpen = uiTab.getAdjustedUrl();
            appCrtl.setUiForPickup(urlToOpen, uiTab);

            TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
            urlToOpen  = servlet.getActualServletUrl() + urlToOpen;
            getElement().executeJs("turku.openNewWindow($0)", urlToOpen);
        }

        // UI.getCurrent().getPage().getHistory().replaceState(null, uiTab.getAdjustedUrl());
    }

    @Override
    public void focusTab(IToolkit_CommandContainerUi iToolkit_commandContainerUi) {

    }

    @Override
    public void ensureTabClosed(IToolkit_CommandContainerUi iToolkit_commandContainerUi) {
        CmdUiTab uiTab = (CmdUiTab) iToolkit_commandContainerUi;

        if (uiTab != theTab) { throw new RuntimeException("Current UiTab " + theTab + " is not the tab to close " + uiTab); }
        removeAll();

        navbarTitle = Workarounds.getCurrentTurkuServlet().getAppNameVersion();
        UI.getCurrent().getPage().setTitle(navbarTitle);
        if (landingPage == null) {
            landingPage = new LandingPage(navbarTitle);
            landingPage.setMainMenu(appCrtl.constructAndInitLandingMenu());
        }

        add(landingPage);
        // this.getElement().executeJs("turku.closeTab()");
    }

    @Override
    public void setCurrentTabModal(boolean b) {

    }

    @Override
    public void openNewWindow(String url) {



    }

    @Override
    public void setToastMessage(String s) {
        Notification.show(s, 4000, Notification.Position.TOP_CENTER);
    }

    public IAppCrtlAccess getApplicationController() { return appCrtl; }
}
