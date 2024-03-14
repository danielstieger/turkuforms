package org.modellwerkstatt.turkuforms.auth;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import static org.modellwerkstatt.turkuforms.app.MPreisAppConfig.HOME_REDIRECT_PREFIX;

public class HomeRedirect extends HorizontalLayout implements HasDynamicTitle {

    protected String appName;

    public HomeRedirect() {

        appName = Workarounds.getCurrentTurkuServlet().getAppNameVersion();
        String locationToForward = Workarounds.getCurrentTurkuServlet().getActualServletUrl();

        Button button = new Button (HOME_REDIRECT_PREFIX + " " + appName, event -> {
            VaadinSession.getCurrent().getSession().invalidate();
            UI.getCurrent().getPage().setLocation(locationToForward);
        });

        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(button);
        setAlignSelf(Alignment.CENTER, button);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
    }

    @Override
    public String getPageTitle() {
        return appName;
    }
}
