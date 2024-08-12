package org.modellwerkstatt.turkuforms.sdi;

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_MainWindow;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;
import org.modellwerkstatt.objectflow.runtime.IOFXUserEnvironment;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.core.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.Workarounds;

public class SdiWorkarounds {
    public static final String TURKUSDIAPPCRTL = "SdiAppCrtl";
    public static final String TURKUSDIUSERENV = "SdiUserEnv";



    public static void setUserEnv(UserEnvironmentInformation env){
        WrappedSession session = VaadinSession.getCurrent().getSession();
        session.setAttribute(TURKUSDIUSERENV, env);
        session.setAttribute("userName", env.getUserName());
    }

    public static UserEnvironmentInformation getUserEnv() {
        WrappedSession session = VaadinSession.getCurrent().getSession();
        return (UserEnvironmentInformation) session.getAttribute(TURKUSDIUSERENV);
    }

    public static boolean crtlPresent() {
        WrappedSession session = VaadinSession.getCurrent().getSession();
        return session.getAttribute(TURKUSDIAPPCRTL) != null;
    }

    public static TurkuSdiAppCrtl getOrCreateAppCrtl(IOFXUserEnvironment env, IToolkit_MainWindow window) {
        WrappedSession session = VaadinSession.getCurrent().getSession();
        TurkuSdiAppCrtl appCrtl = (TurkuSdiAppCrtl) session.getAttribute(TURKUSDIAPPCRTL);

        if (appCrtl == null) {
            TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();

            appCrtl = new TurkuSdiAppCrtl(servlet.getUiFactory(), window, servlet.getAppBehaviour(), servlet.getJmxRegistration(), IOFXCoreReporter.MoWarePlatform.MOWARE_TURKU);
            appCrtl.initializeApplication(servlet.getGuessedServerName(), env, servlet.getUiFactory().getRemoteAddr(), "");

            session.setAttribute(TURKUSDIAPPCRTL, appCrtl);
        }
        return appCrtl;
    }
}
