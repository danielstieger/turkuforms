package org.modellwerkstatt.turkuforms.auth;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.app.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.app.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.ParamInfo;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import static org.modellwerkstatt.turkuforms.app.MPreisAppConfig.*;

public class SimpleIPAuthenticator extends HorizontalLayout implements BeforeEnterObserver {
    protected ParamInfo paramInfo;
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
            AuthUtil.forwareToLogin(paramInfo);
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

        paramInfo = new ParamInfo(beforeEnterEvent.getLocation().getQueryParameters());
        AuthUtil.ensureLoginPresent(DefaultLoginWindow.class);

        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        VaadinSession vaadinSession = VaadinSession.getCurrent();
        IGenAppUiModule appUiModule = servlet.getAppBehaviour();
        ITurkuAppFactory factory = servlet.getUiFactory();

        appName.setText(appUiModule.getShortAppName() + " " + appUiModule.getApplicationVersion());
        loginButton.setText(factory.translateButtonLabel(factory.getSystemLabel(-1, MoWareTranslations.Key.LOGIN_BUTTON), OK_HOKTEY));


        if (AuthUtil.isLogout(beforeEnterEvent)) {
            // no auto login via ip.
            String msg = factory.getSystemLabel(-1, MoWareTranslations.Key.LOGOUT_SUCCESS);
            messageDiv.setText(msg);

        } else {
            // try to log in then ... in case DefaultLoginWindow.class above set principal
            UserPrincipal userPrincipal = UserPrincipal.getUserPrincipal(vaadinSession);
            if (userPrincipal == null) {
                userPrincipal = new UserPrincipal( vaadinSession.getBrowser().getAddress(), "");
            }

            UserEnvironmentInformation environment = new UserEnvironmentInformation();
            String msg = AuthUtil.loginViaLoginCrtl(servlet, vaadinSession, environment, userPrincipal.getUserName(), userPrincipal.getPassword());

            if (msg == null) {
                // ok, access to app given ..
                Workarounds.setUserEnvForUi(environment);
                AuthUtil.ensureAppRoutPresentAndForward(beforeEnterEvent, paramInfo);

            } else {
                messageDiv.setText(msg);
            }
        }
    }


}
