package org.modellwerkstatt.turkuforms.auth;


import elemental.json.Json;
import elemental.json.JsonObject;
import elemental.json.JsonString;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class GoogleOAuth2 {
    public final static String SERVICE_URL="https://accounts.google.com/o/oauth2/auth";
    public final static String CLIENT_ID="938810109726-3lo3a5ebkq8u8lqli5prjq3609bkkn6h.apps.googleusercontent.com";
    public final static String REDIRECT_URI="http://localhost:8080/simpleone/login";
    public final static String SCOPE="https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/userinfo.profile";

    public final static String TOKEN_URL="https://oauth2.googleapis.com/token";
    public final static String CLIENT_SECRET="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";


    public final static String USERINFO_ENDPOINT="https://www.googleapis.com/oauth2/v3/userinfo";

    public GoogleOAuth2() {

    }

    public String initialRedirect(String state) {
        String result = SERVICE_URL + "?" + "client_id=" + CLIENT_ID + "&redirect_uri=" +
                REDIRECT_URI + "&response_type=code&scope=" + SCOPE + "&state=" + state;

        return result;
    }

    public String retrieveUserWithAccessToken(String code)  {

        String request = "code=" + code + "&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET +
                "&redirect_uri=" + REDIRECT_URI + "&grant_type=authorization_code";

        String content = httpConnection(TOKEN_URL, request);

        if (content == null) { return null; }

        JsonObject object = Json.parse(content);
        if (!object.hasKey("access_token")) { return null; }

        String token = object.get("access_token").asString();

        content = httpConnection(USERINFO_ENDPOINT + "?access_token=" + token, null);

        if (content == null) { return null; }

        object = Json.parse(content);
        if (!object.hasKey("email")) { return null; }

        content = object.get("email").asString();

        return content;
    }


    public static String httpConnection(String targetUrl, String request) {
        HttpURLConnection con = null;

        try {
            URL url = new URL(targetUrl);
            con = (HttpURLConnection) url.openConnection();

            con.setConnectTimeout(3000);
            con.setReadTimeout(2000);
            con.setInstanceFollowRedirects(false);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


            if (request != null) {
                con.setRequestMethod("POST");
                con.setDoOutput(true);

                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                out.writeBytes(request);
                out.flush();
                out.close();

            } else {
                con.setRequestMethod("GET");
            }

            int status = con.getResponseCode();

            if (status >= 200 && status < 300) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                return content.toString();

            }

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
