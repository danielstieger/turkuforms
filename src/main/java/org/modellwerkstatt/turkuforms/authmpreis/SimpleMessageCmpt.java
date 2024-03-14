package org.modellwerkstatt.turkuforms.authmpreis;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.turkuforms.app.MPreisAppConfig;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import static org.modellwerkstatt.turkuforms.app.MPreisAppConfig.OK_HOKTEY;

public class SimpleMessageCmpt extends HorizontalLayout {

    public SimpleMessageCmpt(String appNameVersion, String buttonText, String msg, OnLogin process) {
        Span loginIdentityImage = new Span();
        loginIdentityImage.addClassName("DefaultLoginLogo");

        H1 appName = new H1();
        appName.setText(appNameVersion);

        Div messageDiv = new Div();
        messageDiv.addClassName("TurkuErrorDiv");
        messageDiv.addClassName("DefaultLoginContentWidth");
        messageDiv.setText(msg);

        VerticalLayout innerLayout = new VerticalLayout();
        innerLayout.add(loginIdentityImage, appName, messageDiv);
        innerLayout.setAlignSelf(Alignment.CENTER, loginIdentityImage);
        innerLayout.setAlignSelf(Alignment.CENTER, appName);
        innerLayout.setAlignSelf(Alignment.CENTER, messageDiv);

        if (buttonText != null) {
            Button loginButton = new Button(buttonText, event -> {
                process.process();
            });
            Peculiar.useButtonShortcutHk(loginButton, OK_HOKTEY);
            loginButton.addClassName("DefaultLoginContentWidth");
            innerLayout.add(loginButton);
            innerLayout.setAlignSelf(Alignment.CENTER,loginButton);
        }

        add(innerLayout);
        setAlignSelf(Alignment.CENTER, innerLayout);
        setSizeFull();
    }

    public interface OnLogin {
        void process();
    }
}
