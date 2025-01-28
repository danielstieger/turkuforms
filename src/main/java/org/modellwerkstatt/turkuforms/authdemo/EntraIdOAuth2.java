package org.modellwerkstatt.turkuforms.authdemo;


import elemental.json.Json;
import elemental.json.JsonObject;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.modellwerkstatt.objectflow.runtime.DeprecatedServerDateProvider;
import org.modellwerkstatt.turkuforms.auth.ExtAuthProvider;

import java.io.IOException;
import java.util.HashMap;

import static org.modellwerkstatt.turkuforms.authdemo.GoogleOAuth2.httpConnection;

public class EntraIdOAuth2 implements ExtAuthProvider {
    private String AUTHINIT_ENDPOINT ="https://login.microsoftonline.com/-TENANT-/oauth2/v2.0/authorize";
    private String TOKEN_ENDPOINT ="https://login.microsoftonline.com/-TENANT-/oauth2/v2.0/token";
    private String SCOPE="User.Read";


    private String CLIENT_ID="not set";
    private String REDIRECT_URI="not set";
    private String CLIENT_SECRET="not set";
    private String USERNAME_FIELD_TO_USE="not set";
    public String USERINFO_ENDPOINT="https://graph.microsoft.com/v1.0/me";
    private LocalDate expirationDate;





    public EntraIdOAuth2(String tenant, String client_id, String client_secret, String redirect_to, String query_url, String username_field)  {
        AUTHINIT_ENDPOINT = AUTHINIT_ENDPOINT.replace("-TENANT-", tenant);
        TOKEN_ENDPOINT = TOKEN_ENDPOINT.replace("-TENANT-", tenant);
        CLIENT_ID = client_id;
        CLIENT_SECRET = client_secret;
        REDIRECT_URI =redirect_to;
        USERINFO_ENDPOINT = query_url;
        USERNAME_FIELD_TO_USE = username_field;
    }

    public void setExpirationDate(String val) {
        try {
            // FORMAT is yyyy-MM-dd
            expirationDate = new LocalDate(val);

        } catch (Exception ex) {
            expirationDate = null;
            ex.printStackTrace();

        }
    }

    public String getExpirationDate() {
        return "" + expirationDate;
    }

    @Override
    public LocalDate getNullOrCredentialExpirationDate() {
        return expirationDate;
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

        // System.err.println("EntraIdOAuth2 " + object.toString());
        content = object.get(USERNAME_FIELD_TO_USE).asString();

        return content;
    }
}
