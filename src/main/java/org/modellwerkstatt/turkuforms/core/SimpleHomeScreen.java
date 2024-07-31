package org.modellwerkstatt.turkuforms.core;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import static org.modellwerkstatt.turkuforms.core.MPreisAppConfig.HOME_REDIRECT_PREFIX_LABEL;
import static org.modellwerkstatt.turkuforms.core.MPreisAppConfig.OK_HOKTEY;

public class SimpleHomeScreen extends VerticalLayout implements HasDynamicTitle {

    protected String appName;

    public SimpleHomeScreen() {

        appName = Workarounds.getCurrentTurkuServlet().getAppNameVersion();
        String locationToForward = Workarounds.getCurrentTurkuServlet().getActualServletUrl() + TurkuServlet.LOGIN_ROUTE;

        Span loginIdentityImage = new Span();
        loginIdentityImage.addClassName("DefaultLoginLogo");
        add(loginIdentityImage);
        setAlignSelf(Alignment.CENTER, loginIdentityImage);

        H1 appNameH1 = new H1();
        appNameH1.setText(appName);
        add(appNameH1);
        setAlignSelf(Alignment.CENTER, appNameH1);


        String buttonLabel = HOME_REDIRECT_PREFIX_LABEL + " " + appName + " (" + OK_HOKTEY + ")";
        Button button = new Button (buttonLabel , event -> {
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
