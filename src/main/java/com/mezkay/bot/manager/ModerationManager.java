package com.mezkay.bot.manager;

import com.mezkay.bot.players.RebornDiscordPlayer;
import com.mezkay.bot.utility.RebornPlayerUtil;
import com.mezkay.bot.utility.RoleChecker;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class ModerationManager implements EventListener {

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof MessageReceivedEvent) {
            try {


                runCommand(((MessageReceivedEvent) event).getMessage());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void runCommand(Message message) throws SQLException {
        String content = message.getContentRaw();
        String[] args = content.split(" ");

        if(content.indexOf("!rgive") >= 0) {
            if (RoleChecker.playerIsAdmin(message.getMember())) {
                if(args.length > 1) {
                    try {
                        int points = Integer.parseInt(args[1]);
                        if(message.getMentions().getMembers().size() > 0) {
                            String msg = "```";
                            String signe = "+";
                            for (Member member : message.getMentions().getMembers()) {
                                RebornDiscordPlayer rb = new RebornDiscordPlayer(member);

                                if(points < 0)
                                    signe = "-";
                                if(rb.isValid()) {
                                    rb.addPoints(points);
                                    msg += String.format("%s : %s %s %s -> %s points \n", member.getEffectiveName(), rb.getPoints(), signe, Math.abs(points), rb.getPoints() + points);
                                }
                                else {
                                    msg += member.getNickname() + " : Compte non lié\n";
                                }
                            }

                            message.reply(msg + "```").complete();
                        }
                    }
                    catch(Exception e) {

                    }

                }
            } else {
                message.reply("```Vous n'avez pas accès à cette commande (dommage)```").complete();
            }
        }
    }
}
