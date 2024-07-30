package org.modellwerkstatt.turkuforms.sdicore;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import org.modellwerkstatt.dataux.runtime.core.IApplication;
import org.modellwerkstatt.dataux.runtime.core.ICommandContainer;
import org.modellwerkstatt.dataux.runtime.core.UxEvent;
import org.modellwerkstatt.dataux.runtime.genspecifications.Menu;
import org.modellwerkstatt.dataux.runtime.genspecifications.TileAction;
import org.modellwerkstatt.dataux.runtime.sdicore.Params;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_CommandContainerUi;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_MainWindow;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.objectflow.sdservices.BaseSerdes;
import org.modellwerkstatt.objectflow.serdes.CONV;
import org.modellwerkstatt.objectflow.serdes.IConvSerdes;
import org.modellwerkstatt.turkuforms.core.TurkuApp;
import org.modellwerkstatt.turkuforms.core.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;
import org.modellwerkstatt.turkuforms.views.PromptWindow;

import java.util.List;


/*
 * Zum Zeitpunkt des "annavigierens" ist ja noch unklar, ob das das MainWindow wird, oder
 * ein Tab für eine Applikation ..
 *
 * Was wen userEnv null? Not Logged in ..
 */
public class TurkuBrowserTab extends SdiLayout implements IToolkit_MainWindow, BeforeEnterObserver {


    protected TurkuSdiAppCrtl appCrtl;
    protected UserEnvironmentInformation userEnvironment;


    public TurkuBrowserTab() {
        super();

        setWidthFull();
        setHeightFull();
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Turku.l("TurkuBrowserTab.beforeEnter() " + event);
        //  check if we are logged in.
        turkuFactory = Workarounds.getCurrentTurkuServlet().getUiFactory();
        userEnvironment = SdiWorkarounds.getUserEnv();
        Params p = new Params(event.getLocation().getSegments());

        if (userEnvironment == null) {
            String msg = "API error! Sorry, the application can not be accessed directly via this url (login first?).";
            TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
            servlet.logOnPortJTrace(TurkuBrowserTab.class.getName(), servlet.getUiFactory().getRemoteAddr(), msg);
            quickUserInfo(msg);

        } else {
            appCrtl = SdiWorkarounds.getOrCreateAppCrtl(userEnvironment, this);
            appCrtl.startCommand(this, p);

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
    public void closeApplicationAndExit() {

    }

    @Override
    public void parDeploymentForwardNow() {

    }

    @Override
    public void showTiles(List<TileAction> list) {

    }

    @Override
    public void ensureHotkeyAvailable(List<String> list) {

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
    public void addTab(IToolkit_CommandContainerUi iToolkit_commandContainerUi) {
        add((Component) iToolkit_commandContainerUi);
    }

    @Override
    public void focusTab(IToolkit_CommandContainerUi iToolkit_commandContainerUi) {

    }

    @Override
    public void ensureTabClosed(IToolkit_CommandContainerUi iToolkit_commandContainerUi) {
        remove((Component) iToolkit_commandContainerUi);
        this.getElement().executeJs("turku.closeTab()");
    }

    @Override
    public void setCurrentTabModal(boolean b) {

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
    public void setAppInfo(String s, String s1, String s2) {
        checkForMainMenu();
        navbarTitle = s + " " + s1 + " " + s2;
        navbarTitleDiv.setText(navbarTitle);
    }

    @Override
    public void setMenuAndInit(int langIndex, Menu start, Menu extra, Menu help) {
        checkForMainMenu();

        SubMenu startMenu = addToMainMenu(start, turkuFactory.getSystemLabel(langIndex, MoWareTranslations.Key.START));
        Component vaadinPowerOff = Workarounds.createIconWithCollection(turkuFactory.translateIconName("mainmenu_logout"), false);
        startMenu.addItem(vaadinPowerOff, event -> { exitRequestedFromMenu(); });

        addToMainMenu(extra, turkuFactory.getSystemLabel(langIndex, MoWareTranslations.Key.EXTRA));
        SubMenu helpMenu = addToMainMenu(help, turkuFactory.getSystemLabel(langIndex, MoWareTranslations.Key.HELP));
        helpMenu.addItem(turkuFactory.getSystemLabel(langIndex, MoWareTranslations.Key.ABOUT), event -> {
            String text = appCrtl.appUserSystemVersionInfo() + "\n\n" + getTurkuVersionInfo();
            showDialog(DlgType.INFO_SMALL, text, null);
        });
    }

    protected void exitRequestedFromMenu() {
        // TODO: How do we handle the user requested shutdown.
    }

    @Override
    public void installCloseConfirmQuestion(boolean b) {

    }

    protected void quickUserInfo(String msg) {
        Notification notification = new Notification();
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        notification.setDuration(20000);

        Text text = new Text(msg);

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();
    }
}
