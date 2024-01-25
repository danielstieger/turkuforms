package org.modellwerkstatt.turkuforms.auth;

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;

public class UserPrincipal {
    public final static String USERPRINCIPAL_SESSIONATTRIB = "userPrincipal";

    protected String userName;
    protected String password;

    public UserPrincipal(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "UserPrincipal";
    }


    public static UserPrincipal getUserPrincipal(VaadinSession vaadinSession) {
        WrappedSession session = vaadinSession.getSession();
        return (UserPrincipal) session.getAttribute(USERPRINCIPAL_SESSIONATTRIB);
    }

    public static void setUserPrincipal(VaadinSession vaadinSession, UserPrincipal principal) {
        WrappedSession session = vaadinSession.getSession();
        session.setAttribute(USERPRINCIPAL_SESSIONATTRIB, principal);
    }


}
