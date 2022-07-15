package com.mezkay.bot.utility.server;

import com.mezkay.bot.utility.MysqlConnector;
import com.mezkay.bot.players.RebornDiscordPlayer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RebornStatistics {
    private MysqlConnector dbGame;

    public RebornStatistics() {
    }
    public RebornDiscordPlayer getTopPoints() throws SQLException {
        RebornDiscordPlayer player = null;
        Connection con = new MysqlConnector().getCon("ps2");
        Statement stmtOne = con.createStatement();
        ResultSet res = stmtOne.executeQuery("SELECT steam64 FROM `ps2_wallet` as p INNER JOIN libk_player as l ON p.ownerId = l.id  ORDER BY `points` DESC LIMIT 1");

        while(res.next()) {
            player = new RebornDiscordPlayer(res.getString("steam64"));
        }


        return player;
    }
}
