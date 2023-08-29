package org.modellwerkstatt.turkuforms.auth;

import com.vaadin.flow.component.Component;
import org.modellwerkstatt.turkuforms.app.TurkuServlet;

public interface ILoginWindow {

    Component init(TurkuServlet servlet, IAuthenticateUiCallback callback);

    public interface IAuthenticateUiCallback {
        void authenticate(String userName, String password);
    }
}
