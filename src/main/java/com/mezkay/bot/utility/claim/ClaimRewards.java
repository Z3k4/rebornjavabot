package com.mezkay.bot.utility.claim;

import com.mezkay.bot.utility.MysqlConnector;
import com.mezkay.bot.players.RebornDiscordPlayer;
import net.dv8tion.jda.api.entities.Message;

import java.sql.*;

public class ClaimRewards {
    int minutes = 60 * 24;

    private Connection dbBot;

    public ClaimRewards() {

    }

    public static void claimPoints() {

    }

    public static void claimCrates() {

    }

    public boolean canClaim(String discordID) throws SQLException {
        //Timestamp time
        Connection con = new MysqlConnector().getCon("bot");

        boolean canClaim = false;
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp lastClaim;
        Statement stmtOne = con.createStatement();
        ResultSet res = stmtOne.executeQuery("SELECT lastclaim FROM users WHERE discordid = '" + discordID + "'");
        while(res.next()) {
            lastClaim = res.getTimestamp("lastclaim");
            if(lastClaim == null) {
                canClaim = true;
            } else {
                canClaim =  now.after(new Timestamp(lastClaim.getTime() + (minutes * 60) * 1000));
            }
        }

        return canClaim;

    }

    public String getNextClaimTime(RebornDiscordPlayer player) throws SQLException {
        Connection con = new MysqlConnector().getCon("bot");
        long time = 0;

        PreparedStatement stmt = con.prepareStatement("SELECT lastclaim FROM users WHERE discordid = ?");
        stmt.setInt(1, minutes);
        stmt.setString(1, player.getDiscordID());
        ResultSet res = stmt.executeQuery();


        while(res.next())  {
            time =  (res.getTimestamp("lastclaim").getTime() + (minutes * 60) * 1000)   - new Timestamp(System.currentTimeMillis()).getTime() ;
        }


        int secondes = (int)(time / 1000);
        int minutes = secondes / 60;
        int hours = minutes / 60;

        int formatSec = secondes - (secondes/60) * 60 ;
        int formatMin = minutes - (minutes/60) * 60;

        return String.format("Vous devez attendre encore %s heures %s minutes et %s secondes", hours, formatMin, formatSec);

        //message.reply("Vous devez attendre " + hours "h"  + " avant de pouvoir réclamer des points").complete();
    }

    public String claim(RebornDiscordPlayer player) throws SQLException {
        String claimMessage = "";
        Connection con = new MysqlConnector().getCon("bot");
        if(player.isValid()) {
            if (this.canClaim(player.getDiscordID())) {
                int max = 5000;
                int maxPremium = 500;

                int randomPoints = (int) (Math.random() * max);
                int randomPremiumPoints = (int) (Math.random() * maxPremium);


                PreparedStatement stmt = con.prepareStatement("UPDATE users SET lastclaim = ? WHERE discordid = ?");
                stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                stmt.setString(2, player.getDiscordID());
                stmt.executeUpdate();


                String msg = "";
                if (player.isNitroBooster()) {
                    /*msg = randomPoints + " + " + (int)((randomPoints * 1.5) - randomPoints) + "\n" +
                            "ainsi que\n" + randomPremiumPoints + " points premiums en tant que nitro booster";
                    */
                    msg = "```Vous gagnez " + randomPoints + " points en jeu \n[Bonus Nitro]\n" + "Points supplémentaire : " +  (int)((randomPoints * 1.5) - randomPoints)
                    +" points + " + randomPremiumPoints + " points premium"
                            + "```";
                    randomPoints *= 1.5;
                    player.addPremiumPoint((int)randomPremiumPoints);
                }
                else {
                    msg = "```Vous gagnez " + randomPoints + " points en jeu (booster le serveur pour obtenir un bonus)```";
                }

                player.addPoints((int) randomPoints);

                claimMessage = msg;


            } else {
                claimMessage = this.getNextClaimTime(player);
            }
        } else {
                claimMessage = "Aucun compte liée";
        }

        return claimMessage;
    }

    public void resetPlayerClaimTime(RebornDiscordPlayer player) throws SQLException {

        if(player.isValid()) {
            dbBot = new MysqlConnector().getCon("bot");
            PreparedStatement stmt = dbBot.prepareStatement("UPDATE users SET lastclaim = NULL WHERE discordid = ?");
            stmt.setString(1, player.getDiscordID());
            stmt.executeUpdate();



        }
    }
}
