package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinServlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.telemetrics.AppJmxRegistration;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.manmap.runtime.MMStaticAccessHelper;
import org.modellwerkstatt.objectflow.runtime.DeprecatedServerDateProvider;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;
import org.modellwerkstatt.objectflow.runtime.OFXStringFormatter2;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.io.IOException;


public class TurkuServlet extends VaadinServlet {
    public final static String APPCRTL_SESSIONATTRIB_NAME = "org.modelwerkstatt.MoWareApplicationController";
    private String guessedServerName;
    private IGenAppUiModule genApplication;
    private ITurkuFactory appFactory;
    private AppJmxRegistration jmxRegistration;

    public ITurkuFactory getUiFactory() {
        return appFactory;
    }
    public IGenAppUiModule getAppBehaviour() {
        return genApplication;
    }

    public String getGuessedServerName() {
        return guessedServerName;
    }
    public AppJmxRegistration getJmxRegistration() {
        return jmxRegistration;
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        Turku.clearAndDelete();

        String servletPath = this.getServletContext().getContextPath();
        //  - main app behavior class will be given via servlet confg
        String appBehaviorFqName = getInitParameter("applicationFqName");
        String xmlConfigFile = getInitParameter("xmlConfigFile");
        // as well as home screen
        String homeScreenParam = getInitParameter("homeScreenPath");
        if (homeScreenParam != null) {
            appFactory.setRedirectAfterLogoutPath(homeScreenParam);
        }


        guessedServerName = System.getProperty("server.instancename");
        jmxRegistration = new AppJmxRegistration(appBehaviorFqName, servletPath, servletPath);

        try {
            //  - okay, wire up everything
            ApplicationContext appContext = new ClassPathXmlApplicationContext(xmlConfigFile);
            appFactory = ((ITurkuFactory) appContext.getBean(IToolkit_UiFactory.class));

            ClassLoader classLoader = this.getClass().getClassLoader();
            Class<?> appBehaviorClass = classLoader.loadClass(appBehaviorFqName);
            genApplication = ((IGenAppUiModule) appContext.getAutowireCapableBeanFactory().createBean(appBehaviorClass));

        } catch (ClassNotFoundException | BeansException e) {
            throw new RuntimeException(e);

        }

        appFactory.getEventBus().setSysInfo("" + IOFXCoreReporter.MoWarePlatform.MOWARE_VAADIN + " " + guessedServerName + ": " + genApplication.getShortAppName() + " " + genApplication.getApplicationVersion());

        jmxRegistration.registerAppTelemetrics(appFactory, appBehaviorFqName, genApplication.getShortAppName() + " / " + genApplication.getApplicationVersion(), appFactory.getSystemLabel(-1, MoWareTranslations.Key.MOWARE_VERSION) + " / " + Turku.INTERNAL_VERSION, guessedServerName);
    }

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        RouteConfiguration.forApplicationScope().setRoute("", TurkuApp.class);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        long startOfRequest = System.currentTimeMillis();

        // Turku.l("TurkuServlet.service " + Turku.requestToString(request));

        super.service(request, response);

        boolean isVaadinHeartBeat = request.getContentLength() == 0;
        /* Vaadin heartbeats touch the session, therefore extending the TTL. Thus,
         * setting the session-timeout to e.g. 5 min and the heartberat to 1 min let
         * tomcat close the session, if a browser window is closed unexpectedly after 5 mins.
         */

        HttpSession session = request.getSession(false);
        if (session != null && !isVaadinHeartBeat) {
            TurkuApplicationController appCrtl = Workarounds.getAppCrtlFromSession(session);

            if (appCrtl != null) {
                String remoteAddr = "" + session.getAttribute("remoteAddr");
                String userName = "" + session.getAttribute("userName");
                jmxRegistration.getAppTelemetrics().servedRequest(remoteAddr, userName, "some interaction", startOfRequest);
            }
        }
    }


    @Override
    public void destroy() {
        super.destroy();

        appFactory.getEventBus().close();
        jmxRegistration.gcClean();

        DeprecatedServerDateProvider.shutdownAndGcClean();
        MMStaticAccessHelper.shutdownAndGcClean();
        OFXStringFormatter2.GLOBAL_INSTANCE_DEFAULT_LANG = null;
        Turku.l("TurkuServlet.destroy(): SEAMS WE ARE DONE HERE.");
    }
}
