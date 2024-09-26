package org.modellwerkstatt.turkuforms.auth;

public interface ExtAuthProvider {

    String getAuthProviderName();

    String initialRedirect(String state);

    String retrieveUserWithAccessToken(String code);

}
