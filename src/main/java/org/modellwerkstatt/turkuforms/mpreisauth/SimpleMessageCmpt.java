package org.modellwerkstatt.turkuforms.mpreisauth;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.modellwerkstatt.turkuforms.util.NavigationUtil;
import org.modellwerkstatt.turkuforms.util.ParamInfo;
import org.modellwerkstatt.turkuforms.util.Peculiar;

import static org.modellwerkstatt.turkuforms.app.MPreisAppConfig.OK_HOKTEY;

public class SimpleMessageCmpt extends HorizontalLayout {

    public SimpleMessageCmpt(String appNameVersion, String buttonText, String msg, ParamInfo info) {
        Span loginIdentityImage = new Span();
        loginIdentityImage.addClassName("DefaultLoginLogo");

        H1 appName = new H1();
        appName.setText(appNameVersion);

        Div messageDiv = new Div();
        messageDiv.addClassName("TurkuErrorDiv");
        messageDiv.addClassName("DefaultLoginContentWidth");
        messageDiv.setText(msg);

        Button loginButton = new Button(buttonText, event -> {
            NavigationUtil.forwareToLogin(info);
        });
        Peculiar.useButtonShortcutHk(loginButton, OK_HOKTEY);
        loginButton.addClassName("DefaultLoginContentWidth");

        VerticalLayout innerLayout = new VerticalLayout();
        innerLayout.add(loginIdentityImage, appName, messageDiv, loginButton);
        innerLayout.setAlignSelf(Alignment.CENTER, loginIdentityImage);
        innerLayout.setAlignSelf(Alignment.CENTER, appName);
        innerLayout.setAlignSelf(Alignment.CENTER, messageDiv);
        innerLayout.setAlignSelf(Alignment.CENTER,loginButton);

        add(innerLayout);
        setAlignSelf(Alignment.CENTER, innerLayout);
        setHeightFull();
    }
}
