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

        // TODO - GET URLS FROM here ...
        // servlet.getAllCmdUrlDefaults().stream().forEach(cmdUrlDefaults -> System.err.println("WE HAVE " + cmdUrlDefaults.url + " for " + cmdUrlDefaults.cmdFqName));

        String msg = null;
        if (params.hasCmdName()) {
            msg = appCrtl.startCommand(this, params);
        }

        if (!params.hasCmdName() || msg != null) {
            landingPage = new LandingPage(servlet.getAppNameVersion());
            landingPage.setAvailableCommands(appCrtl.getMenu());
            if (msg != null) { landingPage.setMessage(msg); }
            add(landingPage);
        }
    }

    @Override
    public void showDialog(IToolkit_MainWindow.DlgType dlgType, String text, IApplication.DlgRunnable dlgRunnable) {
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
        navbarTitle = uiTab.getWindowTitle();
        removeAll();
        add(uiTab);
    }

    @Override
    public void focusTab(IToolkit_CommandContainerUi iToolkit_commandContainerUi) {

    }

    @Override
    public void ensureTabClosed(IToolkit_CommandContainerUi iToolkit_commandContainerUi) {
        CmdUiTab uiTab = (CmdUiTab) iToolkit_commandContainerUi;
        removeAll();

        navbarTitle = Workarounds.getCurrentTurkuServlet().getAppNameVersion();
        UI.getCurrent().getPage().setTitle(navbarTitle);
        if (landingPage == null) {
            landingPage = new LandingPage(navbarTitle);
            landingPage.setAvailableCommands(appCrtl.getMenu());
        }
        add(landingPage);
        // this.getElement().executeJs("turku.closeTab()");
    }

    @Override
    public void setCurrentTabModal(boolean b) {

    }

    @Override
    public void setToastMessage(String s) {
        Notification.show(s, 4000, Notification.Position.TOP_CENTER);
    }

    public IAppCrtlAccess getApplicationController() { return appCrtl; }
}
