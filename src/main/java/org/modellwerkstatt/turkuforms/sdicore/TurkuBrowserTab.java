package org.modellwerkstatt.turkuforms.sdicore;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.dataux.runtime.core.IApplication;
import org.modellwerkstatt.dataux.runtime.core.ICommandContainer;
import org.modellwerkstatt.dataux.runtime.core.UxEvent;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.genspecifications.Menu;
import org.modellwerkstatt.dataux.runtime.genspecifications.TileAction;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_CommandContainerUi;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_MainWindow;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXUserEnvironment;
import org.modellwerkstatt.objectflow.sdservices.BaseSerdes;
import org.modellwerkstatt.objectflow.serdes.CONV;
import org.modellwerkstatt.objectflow.serdes.IConvSerdes;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.core.TurkuApp;
import org.modellwerkstatt.turkuforms.core.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;
import org.modellwerkstatt.turkuforms.views.CmdUiTab;
import org.modellwerkstatt.turkuforms.views.PromptWindow;

import java.util.List;

public class TurkuBrowserTab extends VerticalLayout implements IToolkit_MainWindow {

    protected Label topBar;
    protected CmdUiTab middle;
    protected Label bottomBar;

    protected ITurkuAppFactory turkuFactory;
    protected IOFXUserEnvironment userEnvironment;

    public TurkuBrowserTab() {
        Turku.l("TurkuApp.constructor() - start");
        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        VaadinSession vaadinSession = VaadinSession.getCurrent();

        IGenAppUiModule appUiModule = servlet.getAppBehaviour();
        turkuFactory = servlet.getUiFactory();
        String remoteAddr = turkuFactory.getRemoteAddr();

        userEnvironment = Workarounds.getAndClearUserEnvFromUi();
        // Turku.l("TurkuApp.constructor() - userEnvironment is " + userEnvironment);



        topBar = new Label();
        bottomBar = new Label();



    }

    @Override
    public void closeApplicationAndExit() {

    }

    @Override
    public void parDeploymentForwardNow() {

    }

    @Override
    public void addStatusInformation(String s) {
        bottomBar.setText(s);
    }

    @Override
    public void setAppInfo(String s, String s1, String s2) {
        topBar.setText(s + " / " + s1 + " / " + s2);
    }

    @Override
    public void setMenuAndInit(int i, Menu menu, Menu menu1, Menu menu2) {
        // no menu
    }

    @Override
    public void showTiles(List<TileAction> list) {
        // no tiles yet
    }

    @Override
    public void ensureHotkeyAvailable(List<String> list) {

    }


    @Override
    public boolean inUiThread() {
        return true;
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
    public void setToastMessage(String s) {

    }

    @Override
    public void addTab(IToolkit_CommandContainerUi iToolkit_commandContainerUi) {

    }

    @Override
    public void focusTab(IToolkit_CommandContainerUi iToolkit_commandContainerUi) {

    }

    @Override
    public void ensureTabClosed(IToolkit_CommandContainerUi iToolkit_commandContainerUi) {

    }

    @Override
    public void setCurrentTabModal(boolean b) {

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
}
