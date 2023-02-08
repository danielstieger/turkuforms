package org.modellwerkstatt.turkuforms.app;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.dataux.runtime.core.ApplicationController;
import org.modellwerkstatt.dataux.runtime.core.IApplicationController;
import org.modellwerkstatt.dataux.runtime.core.ICommandContainer;
import org.modellwerkstatt.dataux.runtime.core.UxEvent;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.genspecifications.MenuSub;
import org.modellwerkstatt.dataux.runtime.genspecifications.TileAction;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Application;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_CommandContainerUI;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.views.Mainwindow;

import java.util.List;



public class TurkuApp extends Mainwindow implements IToolkit_Application {
    private ApplicationController applicationController;


    public TurkuApp() {
        TurkuServlet servlet = (TurkuServlet) VaadinServlet.getCurrent();
        VaadinSession session = UI.getCurrent().getSession();

        IGenAppUiModule appUiModule = servlet.getAppBehaviour();
        ITurkuFactory factory = servlet.getUiFactory();

        // TODO: constructing basis ui later?
        init(appUiModule.getShortAppName() + appUiModule.getApplicationVersion());

        applicationController = new ApplicationController(factory, this, appUiModule, servlet.getJmxRegistration(), IOFXCoreReporter.MoWarePlatform.MOWARE_VAADIN);
        applicationController.initializeApplication(servlet.getGuessedServerName(), new UserEnvironmentInformation(), session.getBrowser().getAddress(),"");
    }

    @Override
    public void closeWindowAndExit() {

    }

    @Override
    public void showDialog(DlgType dlgType, String s, IApplicationController.DlgRunnable dlgRunnable) {

    }

    @Override
    public void showProblemsDialog(List<IOFXProblem> list, IApplicationController.DlgRunnable dlgRunnable) {

    }

    @Override
    public void showGraphDebugger(List<Object> list, String s) {

    }

    @Override
    public void addStatusInformation(String s) {

    }

    @Override
    public void setToastMessage(String s) {

    }

    @Override
    public void setAppInfo(String s, String s1, String s2) {

    }

    @Override
    public void lockInterface(boolean b) {

    }

    @Override
    public void setCurrentTabModal(boolean b) {

    }

    @Override
    public void setMenuAndInit(int i, MenuSub menuSub, MenuSub menuSub1, MenuSub menuSub2) {

    }

    @Override
    public void showTiles(List<TileAction> list) {

    }

    @Override
    public void addTab(IToolkit_CommandContainerUI iToolkit_commandContainerUI) {

    }

    @Override
    public void focusTab(IToolkit_CommandContainerUI iToolkit_commandContainerUI) {

    }

    @Override
    public void ensureTabClosed(IToolkit_CommandContainerUI iToolkit_commandContainerUI) {

    }

    @Override
    public void ensureHotkeyAvailable(List<String> list) {

    }

    @Override
    public void execEventInBackground(ICommandContainer iCommandContainer, Runnable runnable) {

    }

    @Override
    public void execEventInForeground(ICommandContainer iCommandContainer, UxEvent uxEvent) {

    }

    @Override
    public boolean inUiThread() {
        return true;
    }
}
