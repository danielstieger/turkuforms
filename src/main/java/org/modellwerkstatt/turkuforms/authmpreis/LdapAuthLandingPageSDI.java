package org.modellwerkstatt.turkuforms.authmpreis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.dataux.runtime.sdicore.Params;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.IMoLdapService;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.auth.NavigationUtil;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.core.TurkuServlet;
import org.modellwerkstatt.turkuforms.sdicore.SdiWorkarounds;
import org.modellwerkstatt.turkuforms.sdicore.TurkuBrowserTab;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.util.List;
import java.util.Map;

import static org.modellwerkstatt.turkuforms.core.MPreisAppConfig.OK_HOKTEY;

/*
 * Test SDI Infra for the sdicore ...
 *
 */
public class LdapAuthLandingPageSDI extends HorizontalLayout implements BeforeEnterObserver, HasDynamicTitle {
    final static private String APP_IS_AT = "/home";
    private String title;

    public LdapAuthLandingPageSDI() {
        // default, not content in landing page
        // registered as / and /login
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        VaadinSession vaadinSession = VaadinSession.getCurrent();
        ITurkuAppFactory factory = servlet.getUiFactory();
        IMoLdapService ldapService = factory.getLdapServiceIfPresent();

        title = servlet.getAppNameVersion();
        String naviPath = event.getLocation().getPath();

        Params params;
        Map<String, List<String>> qp = event.getLocation().getQueryParameters().getParameters();
        if (qp.containsKey("reroute")) {
            params = new Params(qp.get("reroute").get(0));
        } else {
            params = new Params();
        }

        if ("logout".equals(naviPath)) {

            String buttonName;
            String message = factory.getSystemLabel(-1, MoWareTranslations.Key.LOGOUT_SUCCESS);

            buttonName = factory.translateButtonLabel(factory.getSystemLabel(-1, MoWareTranslations.Key.LOGIN_BUTTON), OK_HOKTEY);


            setAsRoot(new SimpleMessageCmpt(servlet.getAppNameVersion(), buttonName, message, () -> {
                    UI.getCurrent().navigate("/login");
            }));

        } else if (SdiWorkarounds.crtlPresent()) {

            // route should also be present ...
            event.forwardTo(APP_IS_AT + params.asUrl());

        } else {

            setAsRoot(new SimpleLoginFormCmpt("", (username, password) -> {

                if (ldapService == null) {
                    String message = "INTERNAL ERROR - NO LDAP SERVICE CONFIGURED! " + factory.getSystemLabel(-1, MoWareTranslations.Key.LOGIN_FAILED);
                    return message;
                }

                boolean thisAuthenticated = ldapService.authenticateUser(username, password);

                if (!thisAuthenticated) {
                    String message = factory.getSystemLabel(-1, MoWareTranslations.Key.LOGIN_FAILED);
                    return message;

                } else {

                    UserEnvironmentInformation environment = new UserEnvironmentInformation();
                    String msg = NavigationUtil.loginViaLoginCrtl(servlet, vaadinSession, environment, username, password);

                    if (msg == null) {
                        SdiWorkarounds.setUserEnv(environment);
                        String route = APP_IS_AT + "/:path*";
                        if (! RouteConfiguration.forSessionScope().getRoute(route).isPresent()) {
                            RouteConfiguration.forSessionScope().setRoute(route, TurkuBrowserTab.class);
                        }

                        UI.getCurrent().navigate(APP_IS_AT + params.asUrl());
                        return null;

                    } else {
                        // login was not possible ...
                        return msg;

                    }

                }
            }));


        }
    }

    @Override
    public String getPageTitle() {
        return title;
    }

    private void setAsRoot(Component c) {
        this.removeAll();
        this.add(c);
    }
}
