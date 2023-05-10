package org.modellwerkstatt.turkuforms.app;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class LoginApp extends VerticalLayout {
    public LoginApp() {

        add(new Button("Login change URL", event -> {
            UI.getCurrent().navigate("/");}));
    }


}
