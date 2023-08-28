package org.modellwerkstatt.turkuforms.auth;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.notification.Notification;
import org.modellwerkstatt.turkuforms.app.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.lang.reflect.InvocationTargetException;

public class ManualLoginAuthenticator extends Composite<Component> implements ILoginWindow.IAuthenticateUiCallback {
    public final static String DEFAULT_LOGINWINDOW = DefaultLoginWindow.class.getName();


    protected String loginWindowFqName = DEFAULT_LOGINWINDOW;
    protected ILoginWindow loginWindow;

    public ManualLoginAuthenticator() {
    }

    public ManualLoginAuthenticator(String theLoginWindowFqName) {
        loginWindowFqName = theLoginWindowFqName;
    }

    @Override
    protected Component initContent() {
        try {
            Class<?> loginWindowClass = Class.forName(loginWindowFqName);
            loginWindow = (ILoginWindow) loginWindowClass.getDeclaredConstructor().newInstance();

            TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
            return loginWindow.init(servlet, this);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void authenticate(String userName, String password) {
        Notification.show("AUTHENTICATE PLEASE " + userName + " / " + password);
    }
}
