package org.modellwerkstatt.turkuforms.auth;

import org.modellwerkstatt.dataux.runtime.core.LoginController;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.app.ITurkuFactory;

public class TestLogin {
    private UserEnvironmentInformation environment;

    public TestLogin() {
    }

    public String authenticate(ITurkuFactory factory, String guessedServerName, String userName, IGenAppUiModule  appUiModule) {
        LoginController crtl = new LoginController(IOFXCoreReporter.MoWarePlatform.MOWARE_TURKU, guessedServerName);
        environment = new UserEnvironmentInformation();
        environment.setDevice("WebApp", "", "?some ip info?");

        String msg = crtl.checkLoginPrepareUserEnv(userName, "", environment, appUiModule, factory );
        return msg;
    }

    public UserEnvironmentInformation getEnvironment() {
        return environment;
    }
}
