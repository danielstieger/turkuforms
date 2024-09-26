package org.modellwerkstatt.turkuforms.authmpreis;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.modellwerkstatt.turkuforms.util.Peculiar;

import static org.modellwerkstatt.turkuforms.core.MPreisAppConfig.OK_HOKTEY;

public class SimpleMessageCmpt extends HorizontalLayout {

    private H1 appName;
    private Div messageDiv;
    private VerticalLayout innerLayout;

    public SimpleMessageCmpt() {
        Span loginIdentityImage = new Span();
        loginIdentityImage.addClassName("DefaultLoginLogo");

        appName = new H1();

        messageDiv = new Div();
        messageDiv.addClassName("TurkuErrorDiv");
        messageDiv.addClassName("DefaultLoginContentWidth");

        innerLayout = new VerticalLayout();
        innerLayout.add(loginIdentityImage, appName, messageDiv);
        innerLayout.setAlignSelf(Alignment.CENTER, loginIdentityImage);
        innerLayout.setAlignSelf(Alignment.CENTER, appName);
        innerLayout.setAlignSelf(Alignment.CENTER, messageDiv);

        add(innerLayout);
        setAlignSelf(Alignment.CENTER, innerLayout);
        setSizeFull();
    }


    public SimpleMessageCmpt(String appNameVersion, String buttonText, String msg, Todo process) {
        this();

        setAppNameMsg(appNameVersion, msg);

        if (buttonText != null) {
            addButton(buttonText, process);
        }

    }

    public void setAppNameMsg(String appNameVersion, String msg){
        appName.setText(appNameVersion);
        messageDiv.setText(msg);
    }

    public void addButton(String label, Todo proc){
        Button loginButton = new Button(label, event -> {
            proc.process();
        });

        Peculiar.useButtonShortcutHk(loginButton, OK_HOKTEY);
        loginButton.addClassName("DefaultLoginContentWidth");
        innerLayout.add(loginButton);
        innerLayout.setAlignSelf(Alignment.CENTER,loginButton);
    }

    public interface Todo {
        void process();
    }
}
