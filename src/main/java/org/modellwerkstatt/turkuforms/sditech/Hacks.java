package org.modellwerkstatt.turkuforms.sditech;

import com.vaadin.flow.server.VaadinSession;

public class Hacks {

    public static SdiAppCrtl getCrtl() {
        return (SdiAppCrtl) VaadinSession.getCurrent().getSession().getAttribute("SdiAppCrtl");
    }

    public static void setCrtl(SdiAppCrtl crtl) {
        VaadinSession.getCurrent().getSession().setAttribute("SdiAppCrtl", crtl);
    }
}
