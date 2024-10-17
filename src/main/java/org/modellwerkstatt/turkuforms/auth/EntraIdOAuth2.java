package org.modellwerkstatt.turkuforms.auth;


import elemental.json.Json;
import elemental.json.JsonObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.modellwerkstatt.turkuforms.auth.GoogleOAuth2.httpConnection;

public class EntraIdOAuth2 implements ExtAuthProvider {
    public String AUTHINIT_ENDPOINT ="https://login.microsoftonline.com/e68f2ef1-5ffd-426f-b5d2-cec606c8a21a/oauth2/v2.0/authorize";
    public String TOKEN_ENDPOINT ="https://login.microsoftonline.com/e68f2ef1-5ffd-426f-b5d2-cec606c8a21a/oauth2/v2.0/token";
    public String USERINFO_ENDPOINT="https://graph.microsoft.com/v1.0/me";
    public String SCOPE="offline_access user.read mail.read";


    private String CLIENT_ID="not set";
    private String REDIRECT_URI="not set";
    private String CLIENT_SECRET="not set";




    public EntraIdOAuth2(String client_id, String client_secret, String redirect_to)  {
        CLIENT_ID = client_id;
        CLIENT_SECRET = client_secret;
        REDIRECT_URI =redirect_to;
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

    public String retrieveUserWithAccessToken(String code)  {

        String request = "code=" + code + "&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET +
                "&redirect_uri=" + REDIRECT_URI + "&grant_type=authorization_code";

        String content = httpConnection(TOKEN_ENDPOINT, null, request);

        if (content == null) { return null; }

        JsonObject object = Json.parse(content);
        System.err.println("(1) Received " + object.toString());

        if (!object.hasKey("access_token")) { return null; }

        String token = object.get("access_token").asString();

        System.err.println("GOT ACCESS_TOKEN, trying to retrieve mail.");

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer " + token);


        content = httpConnection(USERINFO_ENDPOINT, headerMap,null);

        System.err.println("(2) ENTRAIDOAUTH2: received " + content);

        if (content == null) { return null; }

        object = Json.parse(content);
        if (!object.hasKey("email")) { return null; }

        content = object.get("email").asString();

        return content;
    }
}
