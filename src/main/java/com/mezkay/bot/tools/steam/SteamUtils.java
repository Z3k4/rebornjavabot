package com.mezkay.bot.tools.steam;

import com.mezkay.bot.players.SteamPlayer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;

import static com.mezkay.bot.Bot.STEAM_KEY;

public class SteamUtils {
    public static SteamPlayer retrievePlayer(String steamID) throws IOException {

        JSONObject json = getJSON("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + STEAM_KEY+ "&steamids=" + steamID);

        JSONArray players = json.getJSONArray("players");
        if(players.length() > 0) {
            JSONObject player = players.getJSONObject(0);
            return new SteamPlayer(player.getString("personaname"), player.getString("avatar"),
                    player.getString("steamid"), player.getString("profileurl"));
        } else {
            return new SteamPlayer(false);
        }
    }

    public static JSONObject getJSON(String dataURL) throws IOException {
        URL url =  new URL(dataURL);

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("accept", "application/json");

        InputStream responseStream = connection.getInputStream();
        InputStreamReader iReader = new InputStreamReader(responseStream);
        BufferedReader buff = new BufferedReader(iReader);
        StringBuffer sb = new StringBuffer();
        String str;

        while((str = buff.readLine()) != null) {
            sb.append(str);
        }

        return new JSONObject(sb.toString()).getJSONObject("response");
    }
}
