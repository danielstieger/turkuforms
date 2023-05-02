package org.modellwerkstatt.turkuforms.auth;

import org.modellwerkstatt.dataux.runtime.core.LoginController;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;
import org.modellwerkstatt.objectflow.runtime.IOFXUserEnvironment;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.app.ITurkuFactory;
import org.modellwerkstatt.turkuforms.app.ITurkuAuthenticate;

public class TestLogin implements ITurkuAuthenticate {
    private UserEnvironmentInformation environment;

    public TestLogin() {
    }

    @Override
    public String authenticate(ITurkuFactory factory, String guessedServerName, String userName, IGenAppUiModule  appUiModule) {
        LoginController crtl = new LoginController(IOFXCoreReporter.MoWarePlatform.MOWARE_VAADIN, guessedServerName);
        environment = new UserEnvironmentInformation();
        environment.setDevice("TURKU", "", "?some ip info?");

        String msg = crtl.checkLoginPrepareUserEnv(userName, "", environment, appUiModule, factory );
        return msg;
    }

    public UserEnvironmentInformation getEnvironment() {
        return environment;
    }
}
