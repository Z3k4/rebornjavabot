package com.mezkay.bot.manager;

import com.mezkay.bot.players.RebornDiscordPlayer;
import com.mezkay.bot.utility.RoleChecker;
import com.mezkay.bot.utility.server.RebornStatistics;
import com.mezkay.bot.players.SteamPlayer;
import com.mezkay.bot.tools.steam.SteamUtils;
import com.mezkay.bot.utility.workshop.WorkshopDownloader;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;

public class MessageManager implements EventListener {


    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof MessageReceivedEvent) {
            try {
                exec(((MessageReceivedEvent) event).getMessage());
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void exec(Message message) throws SQLException, IOException {
        if(!message.getMember().getUser().isBot()) {
            String rawContent = message.getContentRaw();
            RebornDiscordPlayer p = new RebornDiscordPlayer(message.getMember());

            String[] data = rawContent.split(" ");

            if (rawContent.indexOf("!rpoints") == 0) {
                if (p.isValid()) {
                    message.reply("Vous avez\n" + p.getPoints() + " points\n" + p.getPremiumPoints() + " points premium").complete();
                } else {
                    message.reply("Compte non lié, utilisez ```!rlink votresteamid64```pour votre compte discord à un compte steam").complete();//System.out.println(RetrievePoints.getPointsOf(message.getMember()));
                }
            }

            if (rawContent.indexOf("!rprofile") == 0) {

                message.reply("Votre steamID64: " + p.getSteamID()).complete();
            }

            if (rawContent.indexOf("!topp") == 0) {
                RebornStatistics rb = new RebornStatistics();
                RebornDiscordPlayer player = rb.getTopPoints();
                message.reply(String.format("Le joueur ayant le plus de points est %s\n%s points\n%s points premium",
                        player.getName(), player.getPoints(), player.getPremiumPoints())).complete();
            }

            if (rawContent.indexOf("!rlink") == 0) {
                if (data.length > 1) {
                    SteamPlayer sPlayer = SteamUtils.retrievePlayer(data[1]);
                    if (sPlayer.getValidUser()) {
                        p.setSteamID(data[1]);
                        message.reply("Compte rattaché à " + sPlayer.getName() + "\n" + sPlayer.getProfileURL()).complete();
                    } else {
                        message.reply("Ce compte steam n'existe pas").complete();
                    }
                } else {
                    RebornDiscordPlayer rb = new RebornDiscordPlayer(message.getMember());
                    if(rb.isValid()) {
                        SteamPlayer sPlayer = SteamUtils.retrievePlayer(new RebornDiscordPlayer(message.getMember()).getSteamID());
                        if (sPlayer.getValidUser()) {
                            message.reply("Compte rattaché à " + sPlayer.getName() + "\n" + sPlayer.getProfileURL()).complete();
                        }
                    } else {
                        message.reply("Aucuns compte rattaché, utilisez !rlink votresteamid64 pour en lié un").complete();
                    }
                }
            }

            if(rawContent.indexOf("!dl") == 0 && RoleChecker.playerIsAdmin(message.getMember())) {
                String workshopID = data[1];
                if(data[1].contains("?id=")) {
                    workshopID = data[1].substring(data[1].indexOf("?id=") + 4);
                }


                WorkshopDownloader wkD = new WorkshopDownloader(message, workshopID);
                //WorkshopDownloader.DownloadItem(data[1]);
            }
        }
    }
}
