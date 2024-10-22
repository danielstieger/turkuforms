package org.modellwerkstatt.turkuforms.auth;

import java.io.IOException;

public interface ExtAuthProvider {

    String getAuthProviderName();

    String initialRedirect(String state);

    String retrieveUserWithAccessToken(String code) throws IOException;

}
