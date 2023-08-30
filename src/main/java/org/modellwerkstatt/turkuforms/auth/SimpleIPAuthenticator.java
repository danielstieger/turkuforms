package org.modellwerkstatt.turkuforms.auth;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import org.modellwerkstatt.dataux.runtime.core.LoginController;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.app.ITurkuFactory;
import org.modellwerkstatt.turkuforms.app.TurkuApp;
import org.modellwerkstatt.turkuforms.app.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import static org.modellwerkstatt.turkuforms.app.MPreisAppConfig.*;

public class SimpleIPAuthenticator extends HorizontalLayout implements BeforeEnterObserver {

    protected VerticalLayout innerLayout;
    protected H1 appName;
    protected Image loginIdentityImage;
    protected Div messageDiv;
    protected Button loginButton;

    public SimpleIPAuthenticator() {
        loginIdentityImage = new Image(MANUAL_THEME_URL_PATH + MANUAL_THEME_LOGINIDENTITYIMG, "Identity Image");
        appName = new H1();

        messageDiv = new Div();
        messageDiv.addClassName("TurkuErrorDiv");
        messageDiv.setWidth(MANUAL_THEME_LOGINIDENTITYIMG_WIDTH);

        loginButton = new Button("login", event -> {
            UI.getCurrent().navigate("login");
        });
        Peculiar.useButtonShortcutHk(loginButton, OK_HOKTEY);
        loginButton.setWidth(MANUAL_THEME_LOGINIDENTITYIMG_WIDTH);

        innerLayout = new VerticalLayout();
        innerLayout.add(loginIdentityImage, appName, messageDiv, loginButton);
        innerLayout.setAlignSelf(Alignment.CENTER, loginIdentityImage);
        innerLayout.setAlignSelf(Alignment.CENTER, appName);
        innerLayout.setAlignSelf(Alignment.CENTER, messageDiv);
        innerLayout.setAlignSelf(Alignment.CENTER,loginButton);


        add(innerLayout);
        setAlignSelf(Alignment.CENTER, innerLayout);
        setHeightFull();

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // This is not really beforeEnter. This is after the initContent()
        // was processed, so the view is already constructed and entered - kind of

        // no double registration for route !
        if (! RouteConfiguration.forSessionScope().getRoute("login").isPresent()) {
            RouteConfiguration.forSessionScope().setRoute("login", DefaultLoginWindow.class);
        }

        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        VaadinSession vaadinSession = VaadinSession.getCurrent();
        IGenAppUiModule appUiModule = servlet.getAppBehaviour();
        ITurkuFactory factory = servlet.getUiFactory();

        appName.setText(appUiModule.getShortAppName() + " " + appUiModule.getApplicationVersion());
        loginButton.setText(factory.translateButtonLabel(factory.getSystemLabel(-1, MoWareTranslations.Key.LOGIN_BUTTON), OK_HOKTEY));


        UserPrincipal userPrincipal = UserPrincipal.getUserPrincipal(vaadinSession);
        if (userPrincipal == null) {
            userPrincipal = new UserPrincipal( vaadinSession.getBrowser().getAddress(), "");
        }

        UserEnvironmentInformation environment = new UserEnvironmentInformation();
        String msg = loginViaLoginCrtl(servlet, vaadinSession, environment, userPrincipal.getUserName(), userPrincipal.getPassword());

        if (msg == null) {
            // ok, access to app given ..

            if (! RouteConfiguration.forSessionScope().getRoute("app").isPresent()) {
                RouteConfiguration.forSessionScope().setRoute("app", TurkuApp.class);
            }
            Workarounds.setUserEnvForUi(environment);
            beforeEnterEvent.forwardTo("app");
        } else {
            messageDiv.setText(msg);
        }
    }

    public static String loginViaLoginCrtl(TurkuServlet servlet, VaadinSession vaadinSession, UserEnvironmentInformation info, String userName, String password) {
        IGenAppUiModule appUiModule = servlet.getAppBehaviour();
        ITurkuFactory factory = servlet.getUiFactory();

        LoginController crtl = new LoginController(IOFXCoreReporter.MoWarePlatform.MOWARE_TURKU, servlet.getGuessedServerName());

        info.setDevice("WebDesktop", "" + vaadinSession.getBrowser().toString(), "");

        String msg = crtl.checkLoginPrepareUserEnv(userName, password, info, appUiModule, factory);
        return msg;
    }
}
