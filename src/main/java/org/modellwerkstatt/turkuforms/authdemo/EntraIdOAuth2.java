package org.modellwerkstatt.turkuforms.authdemo;


import elemental.json.Json;
import elemental.json.JsonObject;
import org.modellwerkstatt.turkuforms.auth.ExtAuthProvider;

import java.io.IOException;
import java.util.HashMap;

import static org.modellwerkstatt.turkuforms.authdemo.GoogleOAuth2.httpConnection;

public class EntraIdOAuth2 implements ExtAuthProvider {
    public String AUTHINIT_ENDPOINT ="https://login.microsoftonline.com/e68f2ef1-5ffd-426f-b5d2-cec606c8a21a/oauth2/v2.0/authorize";
    public String TOKEN_ENDPOINT ="https://login.microsoftonline.com/e68f2ef1-5ffd-426f-b5d2-cec606c8a21a/oauth2/v2.0/token";
    public String USERINFO_ENDPOINT="https://graph.microsoft.com/v1.0/me";
    public String SCOPE="User.Read";


    private String CLIENT_ID="not set";
    private String REDIRECT_URI="not set";
    private String CLIENT_SECRET="not set";
    private String USERNAME_FIELD_TO_USE="not set";




    public EntraIdOAuth2(String client_id, String client_secret, String redirect_to, String username_field)  {
        CLIENT_ID = client_id;
        CLIENT_SECRET = client_secret;
        REDIRECT_URI =redirect_to;
        USERNAME_FIELD_TO_USE = username_field;
    }

    @Override
    public String getAuthProviderName() {
        return "EntraID";
    }

    public String initialRedirect(String state) {
        String result = AUTHINIT_ENDPOINT + "?" + "client_id=" + CLIENT_ID + "&redirect_uri=" +
                REDIRECT_URI + "&response_type=code&scope=" + SCOPE + "&state=" + state;

        return result;
    }

    public String retrieveUserWithAccessToken(String code) throws IOException {

        String request = "code=" + code + "&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET +
                "&redirect_uri=" + REDIRECT_URI + "&grant_type=authorization_code";

        String content = httpConnection(TOKEN_ENDPOINT, null, request);

        JsonObject object = Json.parse(content);

        if (!object.hasKey("access_token")) { throw new RuntimeException("Did not receive a valid token: " + content); }

        String token = object.get("access_token").asString();

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer " + token);
        content = httpConnection(USERINFO_ENDPOINT, headerMap,null);

        object = Json.parse(content);
        if (!object.hasKey(USERNAME_FIELD_TO_USE)) { throw new RuntimeException("The field " + USERNAME_FIELD_TO_USE + " was not found in oauth2 servers return." + content); }

        content = object.get(USERNAME_FIELD_TO_USE).asString();

        return content;
    }
}
