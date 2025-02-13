package org.modellwerkstatt.turkuforms.sdi;


import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import org.modellwerkstatt.dataux.runtime.core.IApplication;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_CommandContainerUi;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_MainWindow;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Window;
import org.modellwerkstatt.objectflow.runtime.*;
import org.modellwerkstatt.objectflow.sdservices.BaseSerdes;
import org.modellwerkstatt.objectflow.serdes.*;
import org.modellwerkstatt.turkuforms.auth.NavigationUtil;
import org.modellwerkstatt.turkuforms.core.ITurkuAppCrtlAccess;
import org.modellwerkstatt.turkuforms.core.TurkuApp;
import org.modellwerkstatt.turkuforms.core.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;
import org.modellwerkstatt.turkuforms.views.CmdUiTab;
import org.modellwerkstatt.turkuforms.views.PromptWindow;

import java.util.List;


public class BrowserTab extends StaticLandingPage implements IToolkit_Window, BeforeEnterObserver {

    protected IOFXUserEnvironment userEnvironment;
    protected OFXUrlParams params;
    protected SdiAppCrtl appCrtl;

    protected CmdUiTab currentTab;
    protected int numTabs = 0;
    protected BrowserTabType type;


    public BrowserTab() {
        super();
    }



    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Turku.l("BrowserTab.beforeEnter() on " + this.hashCode() + " on ui " + UI.getCurrent().hashCode());

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
                servlet.logOnPortJ(TurkuApp.class.getName(), turkuFactory.getRemoteAddr(), IOFXCoreReporter.LogPriority.ERROR, msg, null);
                SdiUtil.quickUserInfo(msg);
                return; // -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
            }

            appCrtl = SdiAppCrtl.createAppCrtlOnSession(userEnv);
        }

        userEnvironment = appCrtl.getUserEnvironment();

        params = new OFXUrlParams(event.getLocation().getSegments());

        String msg = null;
        if (appCrtl.wasPickupCmdThenStart(this, params)) {

            type = BrowserTabType.COMMAND_OPENER_TAB;
            Turku.l("BrowserTab.beforeEnter() did a pickup for the appCrtl");

        } else if (params.hasCmdName()) {
            Turku.l("BrowserTab.beforeEnter() starting command '" + params.getCmdName() + "'");
            msg = appCrtl.startCommandViaUrl(this, params);
            type = BrowserTabType.COMMAND_TAB;

        }

        if ((!params.hasCmdName() && type != BrowserTabType.COMMAND_OPENER_TAB) || msg != null) {

            type = BrowserTabType.LANDING_TAB;
            installLandingPage(turkuFactory, appCrtl.getAppVersionAndDyn(), msg, appCrtl.createLandingPageItems());

        }
    }

    @Override
    public void showDialog(IToolkit_MainWindow.DlgType dlgType, String text, IApplication.DlgRunnable dlgRunnable) {
        Turku.l("BrowserTab.showDialog() " + text);

        PromptWindow window = new PromptWindow(turkuFactory, userEnvironment.getLangIndex());
        window.simplePrompt(dlgType, text, dlgRunnable);
    }

    @Override
    public void showProblemsDialog(List<IOFXProblem> list, IApplication.DlgRunnable dlgRunnable) {
        Turku.l("BrowserTab.showProblemsDialog() " + OFXConsoleHelper.asSimpleString(list));

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
        window.simplePrompt(IToolkit_MainWindow.DlgType.INFO_LARGE, content, null);
    }

    @Override
    public void addTab(IToolkit_CommandContainerUi ui) {
        Turku.l("BrowserTab.addTab() " + ui);

        CmdUiTab uiTab = (CmdUiTab) ui;

        if (appCrtl.hasCrtlAwaitingPickup() && uiTab.getAdjustedUrl() != null) {
            String urlToOpen = uiTab.getAdjustedUrl();
            appCrtl.setUiForPickup(urlToOpen, uiTab);

            TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
            urlToOpen  = servlet.getActualServletUrl() + urlToOpen;
            Turku.l("BrowserTab.addTab() opening url " + urlToOpen);

            getElement().executeJs("turku.openNewWindow($0, $1)", uiTab.hashCode(), urlToOpen);

        } else {
            navbarTitle = uiTab.getWindowTitle();
            removeAll();
            numTabs ++;
            currentTab = uiTab;
            add(currentTab);

            getElement().executeJs("turku.installFocusHandler($0)", this);

            String servletUrl = Workarounds.getCurrentTurkuServlet().getActualServletUrl();
            this.getElement().executeJs("turku.installBeacon($0, $1)", servletUrl, UI.getCurrent().getUIId());

            if (uiTab.hasRwSessionToCommit()) {
                this.getElement().executeJs("turku.installCloseConfirm($0)", true);
            }
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        if (Workarounds.getCurrentTurkuServlet().isDisableBrowserContextMenu()) {
            this.getElement().executeJs("turku.disableBrowserContextMenu()");
        }
    }

    @Override
    public void focusTab(IToolkit_CommandContainerUi tab) {
        Turku.l("BrowserTab.focusTab() " + tab);
        this.removeAll();
        currentTab = (CmdUiTab) tab;
        add(currentTab);
    }

    @ClientCallable
    public void browserTabFocusReceived() {
        if (currentTab != null) {
            currentTab.delayedRequestFocus();
        }
    }

    @Override
    public void ensureTabClosed(IToolkit_CommandContainerUi ui) {
        Turku.l("BrowserTab.ensureTabClosed() " + ui);
        CmdUiTab uiTab = (CmdUiTab) ui;

        numTabs --;
        currentTab = null;
        removeAll();

        this.getElement().executeJs("return window.turku.openerCanAccessWindow($0)", uiTab.hashCode()).then(jsonValue -> {

                boolean canClose = jsonValue.asBoolean();

                if (canClose) {
                    this.getElement().executeJs("window.opener.turku.closeWindow($0)", uiTab.hashCode());

                } else {
                    UI.getCurrent().navigate("/");

                }
        });
    }

    @Override
    public void setCurrentTabModal(boolean b) {

    }


    @Override
    public void setToastMessage(String s) {
        Notification.show(s, 4000, Notification.Position.TOP_CENTER);
    }

    public ITurkuAppCrtlAccess getApplicationController() { return appCrtl; }


    enum BrowserTabType {
        LANDING_TAB,
        COMMAND_TAB,
        COMMAND_OPENER_TAB
    }
}
