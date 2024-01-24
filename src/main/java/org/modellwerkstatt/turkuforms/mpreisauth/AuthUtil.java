package org.modellwerkstatt.turkuforms.mpreisauth;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.dataux.runtime.core.LoginController;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.app.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.app.TurkuServlet;


@SuppressWarnings("unchecked")
@Deprecated
public class AuthUtil {
    public static final int TWO_WEEKS = 14;
    public static final int ONE_WEEK = 7;
    public static final int TWENTYFOUR_HOURS_MILLIS = 86400;
    public static final String LOGOUT_POSTFIX = "/logout";





    public static void removeLoginRoute() {
        RouteConfiguration.forSessionScope().removeRoute("login");
    }



    public static void ensureLoginPresent(Class<? extends Component> loginComponent) {
        // no double registration for route !
        if (! RouteConfiguration.forSessionScope().getRoute("login").isPresent()) {
            RouteConfiguration.forSessionScope().setRoute("login", loginComponent);
        }
    }





    public static void setStartupInfoInBrowser(String value) {
        UI.getCurrent().getPage().executeJs("window.turku.setTurkuCookie", value, TWO_WEEKS);
    }

    public static void getStartupInfoInBrowser(SerializableConsumer myConsumer) {
        UI.getCurrent().getPage().executeJs("window.turku.getTurkuCookie").then(myConsumer);
    }
}
