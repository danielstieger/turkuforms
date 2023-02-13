package org.modellwerkstatt.turkuforms.app;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PreserveOnRefresh;
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
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.*;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;
import org.modellwerkstatt.turkuforms.views.CmdUiTab;
import org.modellwerkstatt.turkuforms.views.Mainwindow;
import org.modellwerkstatt.turkuforms.views.MainwindowTabSheet;

import java.util.List;


@PreserveOnRefresh
public class TurkuApp extends Mainwindow implements IToolkit_Application {
    private ApplicationController applicationController;
    private IOFXUserEnvironment userEnvironment;
    private MainwindowTabSheet mainTabImpl;


    public TurkuApp() {
        TurkuServlet servlet = (TurkuServlet) VaadinServlet.getCurrent();
        VaadinSession session = UI.getCurrent().getSession();
        userEnvironment = new UserEnvironmentInformation();

        IGenAppUiModule appUiModule = servlet.getAppBehaviour();

        // TODO: constructing basis ui later?
        init(servlet.getUiFactory(), appUiModule.getShortAppName() + appUiModule.getApplicationVersion());

        mainTabImpl = new MainwindowTabSheet();


        applicationController = new ApplicationController(servlet.getUiFactory(), this, appUiModule, servlet.getJmxRegistration(), IOFXCoreReporter.MoWarePlatform.MOWARE_VAADIN);
        applicationController.initializeApplication(servlet.getGuessedServerName(), userEnvironment, session.getBrowser().getAddress(),"");
    }

    @Override
    public void closeWindowAndExit() {
        Turku.l("TurkuApp.closeWindowAndExit()");
    }

    @Override
    public void showDialog(DlgType dlgType, String s, IApplicationController.DlgRunnable dlgRunnable) {
        Turku.l("TurkuApp.showDialog() " + s);
    }

    @Override
    public void showProblemsDialog(List<IOFXProblem> list, IApplicationController.DlgRunnable dlgRunnable) {
        Turku.l("TurkuApp.()" + OFXConsoleHelper.asSimpleString(list));
    }

    @Override
    public void showGraphDebugger(List<Object> list, String s) {
        Turku.l("TurkuApp.showGraphDebugger()");
    }

    @Override
    public void addStatusInformation(String s) {
        Notification.show(s, 3000, Notification.Position.BOTTOM_START);
    }

    @Override
    public void setToastMessage(String s) {
        Turku.l("TurkuApp.setToastMessage() " + s);
    }

    @Override
    public void setAppInfo(String appName, String version, String dynTitle) {
        super.setSysInfo(appName+ " " + version);
        super.setNavbarTitle(appName + " " + dynTitle);
        super.setUserInfo(userEnvironment.getUserName());
    }

    @Override
    public void lockInterface(boolean b) {
        Turku.l("TurkuApp.lockInterface() " + b);
    }

    @Override
    public void setCurrentTabModal(boolean b) {
        Turku.l("TurkuApp.setCurrentModal() " + b);
    }

    @Override
    public void setMenuAndInit(int langIndex, MenuSub start, MenuSub extra, MenuSub help) {
        SubMenu startMenu = addToMainMenu(start, turkuFactory.getSystemLabel(langIndex, MoWareTranslations.Key.START));

        Icon vaadinPowerOff = Workarounds.createIconWithCollection(turkuFactory.translateIconName("logout"));
        vaadinPowerOff.addClassName("TurkulayoutMenuIcon");
        startMenu.addItem(vaadinPowerOff, event -> {super.exitRequestedFromMenu();});
        addDrawerMenu(start.items);

        addToMainMenu(extra, turkuFactory.getSystemLabel(langIndex, MoWareTranslations.Key.EXTRA));
        addToMainMenu(help, turkuFactory.getSystemLabel(langIndex, MoWareTranslations.Key.HELP));
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
        if (this.getContent()!= mainTabImpl) {
            this.setContent(mainTabImpl);
        }
        mainTabImpl.addTab(tab);
    }

    @Override
    public void focusTab(IToolkit_CommandContainerUI cmdUiTab) {
        Turku.l("TurkuApp.focusTab()");
        CmdUiTab tab = (CmdUiTab) cmdUiTab;
        mainTabImpl.focusTab(tab);
    }

    @Override
    public void ensureTabClosed(IToolkit_CommandContainerUI cmdUiTab) {
        Turku.l("TurkuApp.ensureTabClosed()");
        CmdUiTab tab = (CmdUiTab) cmdUiTab;
        mainTabImpl.closeTab(tab);
    }

    @Override
    public void ensureHotkeyAvailable(List<String> list) {
        Turku.l("TurkuApp.ensureHotkeyAvailable() ");
    }

    @Override
    public void execEventInBackground(ICommandContainer iCommandContainer, Runnable runnable) {
        Turku.l("TurkuApp.execEventInBackground() " + iCommandContainer.toString());
    }

    @Override
    public void execEventInForeground(ICommandContainer iCommandContainer, UxEvent uxEvent) {
        Turku.l("TurkuApp.execEventInForeground() "+ iCommandContainer.toString());
    }

    @Override
    public boolean inUiThread() {
        return true;
    }
}
