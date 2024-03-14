package org.modellwerkstatt.turkuforms.auth;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import static org.modellwerkstatt.turkuforms.app.MPreisAppConfig.HOME_REDIRECT_PREFIX;
import static org.modellwerkstatt.turkuforms.app.MPreisAppConfig.OK_HOKTEY;

public class HomeRedirect extends VerticalLayout implements HasDynamicTitle {

    protected String appName;

    public HomeRedirect() {

        appName = Workarounds.getCurrentTurkuServlet().getAppNameVersion();
        String locationToForward = Workarounds.getCurrentTurkuServlet().getActualServletUrl();

        Span loginIdentityImage = new Span();
        loginIdentityImage.addClassName("DefaultLoginLogo");
        add(loginIdentityImage);
        setAlignSelf(Alignment.CENTER, loginIdentityImage);

        H1 appNameH1 = new H1();
        appNameH1.setText(appName);
        add(appNameH1);
        setAlignSelf(Alignment.CENTER, appNameH1);

        Button button = new Button (HOME_REDIRECT_PREFIX + " " + appName, event -> {
            VaadinSession.getCurrent().getSession().invalidate();
            UI.getCurrent().getPage().setLocation(locationToForward);
        });
        Peculiar.useButtonShortcutHk(button, OK_HOKTEY);
        button.addClassName("DefaultLoginContentWidth");
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
