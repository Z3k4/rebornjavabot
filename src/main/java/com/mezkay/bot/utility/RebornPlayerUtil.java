package com.mezkay.bot.utility;

import com.mezkay.bot.players.RebornDiscordPlayer;

import java.sql.*;

public class RebornPlayerUtil {

    public static void fetchRebornBreachInfo(RebornDiscordPlayer rbPlayer) throws SQLException {

        if(rbPlayer.getSteamID() != null) {
            Connection con = new MysqlConnector().getCon("ps2");
            Statement stmtOne = con.createStatement();
            ResultSet res = stmtOne.executeQuery("SELECT name, points, premiumPoints FROM libk_player as u INNER JOIN ps2_wallet as w ON u.id = w.ownerid WHERE steam64 = '" + rbPlayer.getSteamID() + "\'");

            while (res.next()) {
                rbPlayer.setDataPoints(res.getInt("points"));
                rbPlayer.setDataPremiumPoints(res.getInt("premiumPoints"));
                rbPlayer.setDataName(res.getString("name"));
            }

        }
    }

    public static void initializePlayerByID(String id) throws SQLException {
        Connection con = new MysqlConnector().getCon("bot");
        Statement stmtOne = con.createStatement();
        ResultSet res = stmtOne.executeQuery("SELECT * FROM users WHERE discordid = '" + id + "'");

        if(!res.next()) {
            PreparedStatement stmt = con.prepareStatement("INSERT INTO users (discordID) VALUES (?) ");
            stmt.setString(1, id);
            stmt.executeUpdate();
            //db.execQuery("INSERT INTO users VALUES (NULL, '" + member.getId() + "', NULL)");
        }
    }

    public static void addPointsToSteamID(String steamid, int points) throws SQLException {
        Connection con = new MysqlConnector().getCon("ps2");
        PreparedStatement stmt = con.
                prepareStatement("UPDATE ps2_wallet as wallet JOIN libk_player as libk " +
                        "ON wallet.ownerId = libk.id SET wallet.points = wallet.points + "+
                        "? WHERE libk.steam64 = ?");
        stmt.setInt(1, points);
        stmt.setString(2, steamid);
        stmt.executeUpdate();

    }

    public static void addPremiumPointsToSteamID(String steamid, int premiumPoints) throws SQLException {
        Connection con = new MysqlConnector().getCon("ps2");
        PreparedStatement stmt = con.
                prepareStatement("UPDATE ps2_wallet as wallet JOIN libk_player as libk " +
                        "ON wallet.ownerId = libk.id SET wallet.premiumPoints = wallet.premiumPoints + " +
                        "? WHERE libk.steam64 = ?");
        stmt.setInt(1, premiumPoints);
        stmt.setString(2, steamid);
        stmt.executeUpdate();

    }

    public static void setSteamIDOfDiscordID(String discordID, String steamIDToSet) throws SQLException {
        Connection con = new MysqlConnector().getCon("bot");

        PreparedStatement stmt = con.prepareStatement("UPDATE users SET steamid64 = ? WHERE discordid = ?");
        stmt.setString(1, steamIDToSet);
        stmt.setString(2, discordID);
        stmt.executeUpdate();

    }
}
