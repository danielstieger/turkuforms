package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinServlet;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.telemetrics.AppJmxRegistration;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.manmap.runtime.MMStaticAccessHelper;
import org.modellwerkstatt.objectflow.runtime.*;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@PreserveOnRefresh
@SuppressWarnings("unchecked")
public class TurkuServlet extends VaadinServlet {
    private String guessedServerName;
    private String deployedAsVersion;
    private String appBehaviorFqName;
    private IGenAppUiModule genApplication;
    private ITurkuAppFactory appFactory;
    private AppJmxRegistration jmxRegistration;
    private Class authenticatorClass;
    private String appNameVersion;

    public ITurkuAppFactory getUiFactory() {
        return appFactory;
    }
    public IGenAppUiModule getAppBehaviour() {
        return genApplication;
    }

    public String getDeployedAsVersion() { return deployedAsVersion; }
    public String getGuessedServerName() {
        return guessedServerName;
    }
    public AppJmxRegistration getJmxRegistration() {
        return jmxRegistration;
    }

    public String getAppNameVersion() { return appNameVersion; }
    public Class getAuthenticatorClass() { return authenticatorClass; }



    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        Turku.clearAndDelete();

        super.init(servletConfig);
    }

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();

        String servletPath = this.getServletContext().getContextPath();
        //  - main app behavior class will be given via servlet confg
        appBehaviorFqName = getInitParameter("applicationFqName");
        String xmlConfigFile = getInitParameter("xmlConfigFile");

        guessedServerName = System.getProperty("server.instancename");

        String realPath = this.getServletContext().getRealPath("/");
        int startOfVersion = realPath.indexOf("##");
        if (startOfVersion > 0 && realPath.length() > startOfVersion + 2) {
            deployedAsVersion = realPath.substring(startOfVersion + 2);
            deployedAsVersion = deployedAsVersion.substring(0, deployedAsVersion.length()-1);
        }

        jmxRegistration = new AppJmxRegistration(appBehaviorFqName, deployedAsVersion, realPath, servletPath);
        deployedAsVersion = deployedAsVersion.replace("_", ".");

        try {
            //  - okay, wire up everything
            ApplicationContext appContext = new ClassPathXmlApplicationContext(xmlConfigFile);
            appFactory = ((ITurkuAppFactory) appContext.getBean(IToolkit_UiFactory.class));

            ClassLoader classLoader = this.getClass().getClassLoader();
            Class<?> appBehaviorClass = classLoader.loadClass(appBehaviorFqName);
            genApplication = ((IGenAppUiModule) appContext.getAutowireCapableBeanFactory().createBean(appBehaviorClass));
            appNameVersion = genApplication.getShortAppName() + " " + genApplication.getApplicationVersion();

            authenticatorClass = classLoader.loadClass(appFactory.getAuthenticatorClassFqName());

        } catch (ClassNotFoundException | BeansException e) {
            Turku.l("TurkuServlet.servletInitialized() " + e.getMessage() + "\n" + OFXConsoleHelper.stackTrace2String(e));
            throw new RuntimeException(e);

        }

        if (appFactory.isCheckDeployedVersion() && !genApplication.getApplicationVersion().equals(deployedAsVersion)) {
            throw new RuntimeException("Application deployed as '"+ deployedAsVersion + "' does not match app version '" + genApplication.getApplicationVersion() +"'.");
        }

        // as well as home screen
        String mainLandingPagePath = getInitParameter("mainLandingPagePath");
        if (mainLandingPagePath == null) {
            mainLandingPagePath = servletPath;

        } else if (mainLandingPagePath.charAt(0) != '/') {
            mainLandingPagePath = "/" + mainLandingPagePath;
        }
        appFactory.setOnLogoutMainLandingPath(mainLandingPagePath);

        appFactory.getEventBus().setSysInfo("" + IOFXCoreReporter.MoWarePlatform.MOWARE_VAADIN + " " + guessedServerName + ": " + appNameVersion);
        jmxRegistration.registerAppTelemetrics(appFactory, appBehaviorFqName, appNameVersion + " (par deployed as '"+ deployedAsVersion + "')", appFactory.getSystemLabel(-1, MoWareTranslations.Key.MOWARE_VERSION) + " / " + Turku.INTERNAL_VERSION, guessedServerName);

        RouteConfiguration.forApplicationScope().setRoute("/login", authenticatorClass);
        RouteConfiguration.forApplicationScope().setRoute("/logout", authenticatorClass);
        RouteConfiguration.forApplicationScope().setRoute("/", authenticatorClass);


        Turku.l("TurkuServlet.servletInitialized() done successfully.");
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean isVaadinHeartBeat = request.getContentLength() == 0;
        /* Vaadin heartbeats touch the session, therefore extending the TTL. Thus,
         * setting the session-timeout to e.g. 5 min and the heartberat to 1 min let
         * tomcat close the session, if a browser window is closed unexpectedly after 5 mins.
         */

        long startOfRequest = 0;
        TurkuApplicationController crtl = null;
        HttpSession httpSession = request.getSession(false);

        if (!isVaadinHeartBeat && httpSession != null) {
            crtl = Workarounds.getControllerFromRequest(request, httpSession);
            if (crtl != null) {
                startOfRequest = System.currentTimeMillis();
                crtl.startRequest();
            }
        }

        super.service(request, response);


        if (crtl != null && request.isRequestedSessionIdValid()) {
            // not invalidated by service()
            String remoteAddr = "" + httpSession.getAttribute(TurkuApplicationController.REMOTE_SESSIONATTRIB);
            String userName = "" + httpSession.getAttribute(TurkuApplicationController.USERNAME_SESSIONATTRIB);
            jmxRegistration.getAppTelemetrics().servedRequest(remoteAddr, userName, "some vaadin interaction", startOfRequest);
        }
    }



    public void logOnPortJTrace(String source, String ipAddr, String msg) {
        CoreReporterInfo info = new CoreReporterInfo(IOFXCoreReporter.Type.APP_TRACE, appBehaviorFqName, genApplication.getApplicationVersion(),
                source, "", "", IOFXCoreReporter.LogPriority.TRACE, 0, "", "", "",
                ipAddr, MoVersion.MOWARE_PLUGIN_VERSION, IOFXCoreReporter.MoWarePlatform.MOWARE_TURKU, guessedServerName, msg);
        appFactory.report(info);
    }

    @Override
    public void destroy() {
        super.destroy();

        appFactory.getEventBus().close();
        jmxRegistration.gcClean();

        DeprecatedServerDateProvider.shutdownAndGcClean();
        MMStaticAccessHelper.shutdownAndGcClean();
        OFXStringFormatter2.GLOBAL_INSTANCE_DEFAULT_LANG = null;
        Turku.l("TurkuServlet.destroy(): servled cleaned up and destroyed.");
    }
}
