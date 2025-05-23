package org.modellwerkstatt.turkuforms.authdemo;

import elemental.json.Json;
import elemental.json.JsonObject;
import org.joda.time.LocalDate;
import org.modellwerkstatt.turkuforms.auth.ExtAuthProvider;

import java.io.IOException;
import java.util.HashMap;

import static org.modellwerkstatt.turkuforms.authdemo.GoogleOAuth2.httpConnection;

public class LinkedinOAuth2 implements ExtAuthProvider {

    public String AUTHINIT_ENDPOINT ="https://www.linkedin.com/oauth/v2/authorization";
    public String TOKEN_ENDPOINT ="https://www.linkedin.com/oauth/v2/accessToken";

    public String USERINFO_ENDPOINT="https://api.linkedin.com/v2/userinfo";
    public String SCOPE="profile email openid";


    private String CLIENT_ID="not set";
    private String REDIRECT_URI="not set";
    private String CLIENT_SECRET="not set";




    public LinkedinOAuth2(String client_id, String client_secret, String redirect_to)  {
        CLIENT_ID = client_id;
        CLIENT_SECRET = client_secret;
        REDIRECT_URI =redirect_to;
    }



    public String initialRedirect(String state) {
        String result = AUTHINIT_ENDPOINT + "?" + "client_id=" + CLIENT_ID + "&redirect_uri=" +
                REDIRECT_URI + "&response_type=code&scope=" + SCOPE + "&state=" + state;

        return result;
    }

    public String retrieveUserWithAccessToken(String code) throws IOException {

        String request = "code=" + code + "&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET +
                "&redirect_uri=" + REDIRECT_URI + "&grant_type=authorization_code";

        String content = httpConnection(TOKEN_ENDPOINT + "?" + request, null, null);

        if (content == null) { return null; }

        JsonObject object = Json.parse(content);
        if (!object.hasKey("access_token")) { return null; }
        String token = object.get("access_token").asString();

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer " + token);

        content = httpConnection(USERINFO_ENDPOINT, headerMap, null);

        if (content == null) { return null; }

        object = Json.parse(content);

        if (!object.hasKey("email")) { return null; }
        content = object.get("email").asString();

        return content;
    }


    @Override
    public String getAuthProviderName() {
        return "LinkedIn";
    }

    @Override
    public LocalDate getNullOrCredentialExpirationDate() {
        return null;
    }
}
