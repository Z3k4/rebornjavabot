package com.mezkay.bot.manager;

import com.mezkay.bot.players.RebornDiscordPlayer;
import com.mezkay.bot.utility.RoleChecker;
import com.mezkay.bot.utility.claim.ClaimRewards;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.internal.entities.MessageMentionsImpl;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class ClaimManager implements EventListener {
    private ClaimRewards claimRewards;

    public ClaimManager() {
        this.claimRewards = new ClaimRewards();
    }
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof MessageReceivedEvent) {
            try {
                consumeCommand(((MessageReceivedEvent) event).getMessage());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void consumeCommand(Message message) throws SQLException {

        if(!message.getMember().getUser().isBot()) {
            RebornDiscordPlayer p = new RebornDiscordPlayer(message.getMember());
            String rawContent = message.getContentRaw();

            if (rawContent.indexOf("!rclaim") == 0) {
                    /*int randomValue = (int)(Math.random() * 10);
                    p.addPoints(randomValue);
                    message.reply("Vous avez gagné " + randomValue + " points en jeu").complete();*/
                message.reply(claimRewards.claim(p)).complete();
            }

            if(rawContent.indexOf("!resclaim") == 0 && RoleChecker.playerIsAdmin(message.getMember())) {

                if(message.getMentions().getMembers().size() > 0) {
                    String allReseted = "Timer réinitialisé pour\n```";
                    String unkownUser = "Impossible de trouver de compte pour\n```";
                    int unvalidAccount = 0;
                    for (Member member : message.getMentions().getMembers()) {
                        RebornDiscordPlayer rbPlayer = new RebornDiscordPlayer(member);
                        if (rbPlayer.isValid()) {

                            claimRewards.resetPlayerClaimTime(rbPlayer);
                            allReseted += member.getEffectiveName() + "\n";
                        } else {
                            unvalidAccount++;
                            unkownUser += member.getEffectiveName()+ "\n";
                        }
                    }
                    String finalMessage = allReseted + "```";
                    if(unvalidAccount > 0) {
                        finalMessage += unkownUser + "```";
                    }

                    message.reply(finalMessage).complete();
                } else {
                    message.reply("Aucuns comptes précisés").complete();
                }

            }
        }
    }
}
