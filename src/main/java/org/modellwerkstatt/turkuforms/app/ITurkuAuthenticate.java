package org.modellwerkstatt.turkuforms.app;

import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;

public interface ITurkuAuthenticate {

    public String authenticate(ITurkuFactory factory, String guessedServerName, String userName, IGenAppUiModule appUiModule);

    public UserEnvironmentInformation getEnvironment();


}
