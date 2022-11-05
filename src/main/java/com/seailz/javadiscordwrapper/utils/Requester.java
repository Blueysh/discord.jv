package com.seailz.javadiscordwrapper.utils;

import com.seailz.javadiscordwrapper.DiscordJv;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Requester {

    // send get request
    public static JSONObject get(String url, DiscordJv djv) {
        try {
            url = URLS.BASE_URL + url;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "DiscordJv (www.seailz.com, 1.0)");
            con.setRequestProperty("Authorization", "Bot " + djv.getToken());
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println();
                return new JSONObject(response.toString());
            } else {
                System.out.println("Request failed with code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}