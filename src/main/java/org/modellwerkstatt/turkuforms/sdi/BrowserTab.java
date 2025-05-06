package org.modellwerkstatt.turkuforms.sdi;


import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import org.modellwerkstatt.dataux.runtime.core.IApplication;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_CommandContainerUi;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_MainWindow;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Window;
import org.modellwerkstatt.objectflow.runtime.*;
import org.modellwerkstatt.objectflow.sdservices.BaseSerdes;
import org.modellwerkstatt.objectflow.serdes.*;
import org.modellwerkstatt.turkuforms.auth.NavigationUtil;
import org.modellwerkstatt.turkuforms.core.ITurkuAppCrtlAccess;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.core.TurkuApp;
import org.modellwerkstatt.turkuforms.core.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;
import org.modellwerkstatt.turkuforms.views.CmdUiTab;
import org.modellwerkstatt.turkuforms.views.ITurkuMainAdjust;
import org.modellwerkstatt.turkuforms.views.PromptWindow;

import java.util.List;


@PreserveOnRefresh
public class BrowserTab extends BrowserTabBase implements ITurkuMainAdjust, IToolkit_Window, BeforeEnterObserver, HasDynamicTitle {

    protected ITurkuAppFactory turkuFactory;
    protected IOFXUserEnvironment userEnvironment;
    protected OFXUrlParams params;
    protected SdiAppCrtl appCrtl;

    protected String navbarTitle;
    protected CmdUiTab currentTab;
    protected int numTabs = 0;

    protected StaticLandingPage landingPage;
    protected String urlToGoAfterClose;

    public BrowserTab() {
        Peculiar.shrinkSpace(this);
        setHeightFull();
        setWidthFull();
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
                servlet.logOnPortJ(TurkuApp.class.getName(), turkuFactory.getRemoteAddr(), IOFXCoreReporter.Type.APP_TRACE, IOFXCoreReporter.LogPriority.ERROR, msg, null);
                SdiUtil.quickUserInfo(msg);
                return; // -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
            }

            appCrtl = SdiAppCrtl.createAppCrtlOnSession(userEnv);
        }

        userEnvironment = appCrtl.getUserEnvironment();
        params = new OFXUrlParams(event.getLocation().getSegments());


        if (appCrtl.wasPickupCmdThenStart(this, params)) {
            Turku.l("BrowserTab.beforeEnter() did a pickup for the appCrtl");

        } else if (appCrtl.hasToRunStartupCmdAsModalFirst(params, this)) {
            if (!appCrtl.isStartupCmdRunning()) {
                // already terminated.
                Turku.l("BrowserTab.beforeEnter() startup cmd already terminated params is '" + params.asUrl() + "'");

                if (params.hasCmdName()) {
                    String msg = appCrtl.startCommandViaUrl(false,this, params);
                    if (msg != null) {
                        showLandingPageWithMessage(msg);
                    }
                } else {
                    showLandingPageWithMessage(null);
                }

            } else {
                if (params.hasCmdName()) {
                    urlToGoAfterClose = params.asUrl();
                } else {
                    urlToGoAfterClose = "/";
                }

                Turku.l("BrowserTab.beforeEnter() startup still running. UrlToGo is " + urlToGoAfterClose);

            }

        } else if (params.hasCmdName()) {
            Turku.l("BrowserTab.beforeEnter() starting command '" + params.getCmdName() + "'");
            String msg = appCrtl.startCommandViaUrl(false,this, params);
            if (msg != null) {
                showLandingPageWithMessage(msg);
            }

        } else {
            showLandingPageWithMessage(null);

        }
    }

    private void showLandingPageWithMessage(String msg) {
        removeAll();
        landingPage = new StaticLandingPage();
        landingPage.installInto(turkuFactory, this, appCrtl, msg);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        Turku.l("BrowserTab.onDetach() received on detach");
        appCrtl.external_closeBrowserTab(this);
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

        CmdUiTab uiTab = (CmdUiTab) ui;
        Turku.l("BrowserTab.addTab() with " + ui + " col " + uiTab.getColor() + " / " + uiTab.getWindowTitle());


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
            currentTab.setMainTabForAdjustments(this);
            add(currentTab);

            getElement().executeJs("turku.installFocusHandler($0)", this);

            String servletUrl = Workarounds.getCurrentTurkuServlet().getActualServletUrl();
            getElement().executeJs("turku.installBeacon($0, $1)", servletUrl, UI.getCurrent().getUIId());

            if (uiTab.hasRwSessionToCommit()) {
                getElement().executeJs("turku.installCloseConfirm($0)", true);
            }
            adjustTopBarColorOrNull(currentTab.getColor());
        }
    }

    @Override
    public void adjustTopBarColorOrNull(String col) {
        if (col != null) {
            getElement().getStyle().set("border-top", "6px solid " + col);
        }
    }

    @Override
    public void adjustTabStyle(CmdUiTab ui, String col) {
        adjustTopBarColorOrNull(col);
    }

    @Override
    public void adjustTitle() {
        navbarTitle = currentTab.getWindowTitle();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        if (Workarounds.getCurrentTurkuServlet().isDisableBrowserContextMenu()) {
            this.getElement().executeJs("turku.disableBrowserContextMenu()");
        }

        if (!"/".equals(urlToGoAfterClose) && landingPage == null && (currentTab != null && !currentTab.isModalTabWindow())) {
            this.getElement().executeJs("turku.setNotLandingPage()");
        }
    }

    @Override
    public void focusTab(IToolkit_CommandContainerUi tab) {
        CmdUiTab cmdUiTab = (CmdUiTab) tab;
        Turku.l("BrowserTab.focusTab() " + tab);

        this.removeAll();
        currentTab = cmdUiTab;
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

        this.getElement().executeJs("turku.installCloseConfirm($0)", false);

        if (urlToGoAfterClose == null) {
            // close window if possible
            this.getElement().executeJs("return window.turku.openerCanAccessWindow($0)", uiTab.hashCode()).then(jsonValue -> {

                boolean canClose = jsonValue.asBoolean();

                if (canClose) {
                    this.getElement().executeJs("window.opener.turku.closeWindow($0)", uiTab.hashCode());

                } else {
                    this.getElement().executeJs("if (window.turkuTampWindowClose) { window.turkuTampWindowClose(); }");
                }
            });

        } else {

            uiTab.containerRunLater("Open Link " + urlToGoAfterClose, () -> {
                UI.getCurrent().navigate(urlToGoAfterClose);
                urlToGoAfterClose = null;
            });
        }
    }

    @Override
    public void setCurrentTabModal(boolean lock) {
        // the naming of the method is strange here, it s not "current"r
        setEnabled(!lock);

        if (lock) {
            addClassName("ModalLockedUi");
        } else {
            removeClassName("ModalLockedUi");
        }
    }


    @Override
    public void setToastMessage(String s) {
        Notification.show(s, 4000, Notification.Position.TOP_CENTER);
    }

    public ITurkuAppCrtlAccess getApplicationController() { return appCrtl; }

    @Override
    public String getPageTitle() {
        return navbarTitle;
    }
}
