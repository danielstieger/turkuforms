package org.modellwerkstatt.turkuforms.auth;

import org.joda.time.LocalDate;

import java.io.IOException;

public interface ExtAuthProvider {

    String getAuthProviderName();

    LocalDate getNullOrCredentialExpirationDate();

    String initialRedirect(String state);

    String retrieveUserWithAccessToken(String code) throws IOException;

}
