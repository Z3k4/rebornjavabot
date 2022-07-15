package com.mezkay.bot.utility.server;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ServerInfo {

    private JDA jda;
    private Timer serverTimer;


    public ServerInfo(JDA jda) {
        int minutes = 2;
        Timer timer = new Timer();
        this.jda = jda;

        this.rebEntity =  new GameServerEntity(
                "game-fr-24.mtxserv.com&port=27100",
                "37.187.190.153:27100",
                "https://content.communautereborn.fr/images/breach.png",
                "https://steamcommunity.com/sharedfiles/filedetails/?id=2688863895",
                "http://connectbreach.communautereborn.fr/"
        );

        rebEntity.setChannelID("945350277047914496");
        rebEntity.setMessageID("983826597385736243");

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    printServerStatus();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1500 * (60 * minutes));
    }


    private String breachData = "game-fr-24.mtxserv.com&port=27100";

    private GameServerEntity rebEntity;


    public JSONObject fetchServerInfo(String serverID) throws IOException {
        URL url =  new URL("https://mtxserv.com/api/v1/viewers/game?type=gmod&ip=" + serverID);

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("accept", "application/json");
        System.out.println();

        InputStream responseStream = connection.getInputStream();
        InputStreamReader iReader = new InputStreamReader(responseStream);
        BufferedReader buff = new BufferedReader(iReader);
        StringBuffer sb = new StringBuffer();
        String str;

        while((str = buff.readLine()) != null) {
            sb.append(str);
        }

        return new JSONObject(sb.toString());
    }

    public void printServerStatus() throws IOException {

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.decode("0xf77d04"));
        embed.setThumbnail(rebEntity.getThumbnailURL());
        embed.addField("IP", rebEntity.getRawIP(), false);
        embed.addField("Collection", rebEntity.getCollection(), false);


        try {
            JSONObject serverInfo = fetchServerInfo(rebEntity.getIp());

            if (serverInfo.getBoolean("is_online")) {


                JSONObject params = serverInfo.getJSONObject("params");
                String mode = params.getString("game");
                String hostname = params.getString("host_name");
                String map = params.getString("map");
                int slots = Integer.parseInt(params.getString("max_slots"));
                JSONArray players = params.getJSONArray("players");


                embed.setTitle(hostname);
                embed.addField("Mode", mode, false);
                embed.addField("Carte", map, false);
                embed.addField("Joueurs", players.length() + " / " + slots, false);

                String firstPart = "";
                String secondPart = "";
                if (players.length() > 0) {
                    firstPart += "```";
                    for (int i = 0; i < (slots / 2); i++) {
                        if (i < players.length()) {
                            firstPart += ((JSONObject) players.get(i)).getString("name") + "\n";
                        }
                    }
                    embed.addField("\u200b", firstPart + "```", true);


                    if (players.length() > slots / 2) {
                        secondPart += "```";
                        for (int i = slots / 2; i < slots; i++) {
                            if (i < players.length()) {
                                secondPart += ((JSONObject) players.get(0)).getString("name") + "\n";
                            }
                        }

                        embed.addField("\u200b", secondPart + "```", true);
                    }
                }


            } else {
                embed.setTitle("Hors ligne - de mire (hahaha)");
            }


            embed.setTimestamp(new Date().toInstant());
            MessageEmbed msgEmbed = embed.build();

            jda.getTextChannelById(rebEntity.getChannelID()).retrieveMessageById(rebEntity.getMessageID()).queue((message -> {
                message.editMessageEmbeds(msgEmbed).queue();
            }));
        } catch(JSONException jsonException) {
            System.out.println("Error when parsing JSON result");
        }


    }


 }
