package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinServlet;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.telemetrics.AppJmxRegistration;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.manmap.runtime.MMStaticAccessHelper;
import org.modellwerkstatt.objectflow.runtime.DeprecatedServerDateProvider;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;
import org.modellwerkstatt.objectflow.runtime.OFXStringFormatter2;
import org.modellwerkstatt.turkuforms.experiment.LoginView;
import org.modellwerkstatt.turkuforms.experiment.StaticView;
import org.modellwerkstatt.turkuforms.experiment.TestView;
import org.modellwerkstatt.turkuforms.views.TurkuLayout;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class TurkuServlet extends VaadinServlet {
    private String appBehaviorFqName;
    private String appPackageFqName;
    private String xmlConfigFile;
    private String servletPath;
    private String guessedServerName;


    private ApplicationContext appContext;
    private IGenAppUiModule genApplication;
    private ITurkuFactory appFactory;
    private AppJmxRegistration jmxRegistration;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        Turku.clearAndDelete();

        servletPath = this.getServletContext().getContextPath();
        //  - main app behavior class will be given via servlet confg
        appBehaviorFqName = getInitParameter("applicationFqName");
        xmlConfigFile = getInitParameter("xmlConfigFile");
        // as well as home screen
        String homeScreenParam = getInitParameter("homeScreenPath");
        if (homeScreenParam != null) {
            appFactory.setRedirectAfterLogoutPath(homeScreenParam);
        }


        guessedServerName = System.getProperty("server.instancename");
        jmxRegistration = new AppJmxRegistration(appBehaviorFqName, servletPath, servletPath);

        try {
            //  - okay, wire up everything
            appContext = new ClassPathXmlApplicationContext(xmlConfigFile);
            appFactory = ((ITurkuFactory) appContext.getBean(IToolkit_UiFactory.class));

            ClassLoader classLoader = this.getClass().getClassLoader();
            Class<?> appBehaviorClass = classLoader.loadClass(appBehaviorFqName);
            genApplication = ((IGenAppUiModule) appContext.getAutowireCapableBeanFactory().createBean(appBehaviorClass));


        } catch (ClassNotFoundException | BeansException e) {
            throw new RuntimeException(e);

        }

        appFactory.getEventBus().setSysInfo("" + IOFXCoreReporter.MoWarePlatform.MOWARE_VAADIN + " " + guessedServerName + ": " + genApplication.getShortAppName() + " " + genApplication.getApplicationVersion());

        jmxRegistration.registerAppTelemetrics(appFactory, appBehaviorFqName, genApplication.getShortAppName() + " / " + genApplication.getApplicationVersion(), appFactory.getSystemLabel(-1, MoWareTranslations.Key.MOWARE_VERSION) + " / " + Turku.VERSION, guessedServerName);
    }

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        Turku.l("Turkuservlet.servletInitializer() .... ");

        RouteConfiguration.forApplicationScope().setRoute("static", StaticView.class);
        RouteConfiguration.forApplicationScope().setRoute("view", TestView.class);
        RouteConfiguration.forApplicationScope().setRoute("login", LoginView.class);
        RouteConfiguration.forApplicationScope().setRoute("", TurkuLayout.class);



    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // No access here to UI.getCurrent()

        super.service(request, response);

        boolean isVaadinHeartBeat = request.getContentLength() == 0;
        /* Vaadin heartbeats touch the session, therefore extending the TTL. Thus,
         * setting the session-timeout to e.g. 5 min and the heartberat to 1 min let
         * tomcat close the session, if a browser window is closed unexpectedly after 5 mins.
         */
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
