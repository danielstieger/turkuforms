package org.modellwerkstatt.turkuforms.auth;


import elemental.json.Json;
import elemental.json.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Map;

public class GoogleOAuth2 implements ExtAuthProvider {
    public String AUTHINIT_ENDPOINT ="https://accounts.google.com/o/oauth2/auth";
    public String TOKEN_ENDPOINT ="https://oauth2.googleapis.com/token";
    public String USERINFO_ENDPOINT="https://www.googleapis.com/oauth2/v3/userinfo";
    public String SCOPE="https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/userinfo.profile";


    private String CLIENT_ID="not set";
    private String REDIRECT_URI="not set";
    private String CLIENT_SECRET="not set";




    public GoogleOAuth2(String client_id, String client_secret, String redirect_to)  {
        CLIENT_ID = client_id;
        CLIENT_SECRET = client_secret;
        REDIRECT_URI =redirect_to;
    }

    @Override
    public String getAuthProviderName() {
        return "Google";
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
        if (!object.hasKey("access_token")) { return null; }

        String token = object.get("access_token").asString();

        content = httpConnection(USERINFO_ENDPOINT + "?access_token=" + token, null,null);

        if (content == null) { return null; }

        object = Json.parse(content);
        if (!object.hasKey("email")) { return null; }

        content = object.get("email").asString();

        return content;
    }


    public static String httpConnection(String targetUrl, Map<String, String> headers, String postRequest) {
        HttpURLConnection con = null;

        try {
            URL url = new URL(targetUrl);
            con = (HttpURLConnection) url.openConnection();

            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            con.setInstanceFollowRedirects(false);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Accept", "application/json");

            if (headers != null){
                for(String key: headers.keySet()) {
                    String encoded = new String(Base64.getEncoder().encode(headers.get(key).getBytes()));
                    System.err.println("ORIG: " + headers.get(key));
                    System.err.println("ENCD: " + encoded);
                    con.setRequestProperty(key, headers.get(key));

                }
            }

            if (postRequest != null) {
                con.setRequestMethod("POST");
                con.setDoOutput(true);

                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                out.writeBytes(postRequest);
                out.flush();
                out.close();

            } else {
                con.setRequestMethod("GET");
            }

            int status = con.getResponseCode();

            InputStream stream;
            if (status >= 200 && status < 300) {
                stream = con.getInputStream();
            } else {
                System.err.println("GoogleOAuth2.httpConnect() httpcode " + status + " - " + con.getResponseMessage());
                stream = con.getErrorStream();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            return content.toString();

        } catch (IOException e) {
            System.err.println(e.getClass().getSimpleName() + " while connecting to " + targetUrl);
            e.printStackTrace();

        } finally {
            if (con != null){
                con.disconnect();
            }
        }

        return null;
    }

}
