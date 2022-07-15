package com.mezkay.bot.players;

import com.mezkay.bot.utility.MysqlConnector;
import com.mezkay.bot.utility.RebornPlayerUtil;
import net.dv8tion.jda.api.entities.Member;

import java.sql.*;

public class RebornDiscordPlayer {

    private String steamID;
    private Member member;

    private String name;


    private String discordID;
    private boolean isNitroBooster;


    //Breach data

    private int points;
    private int premiumPoints;

    public RebornDiscordPlayer(Member member) throws SQLException {
        this.member = member;
        this.points = -1;
        this.premiumPoints = -1;
        this.steamID = "null";
        this.discordID = member.getId();


        checkIntialized();

        if(member.getTimeBoosted() == null) {
            this.isNitroBooster = false;
        } else {
            this.isNitroBooster = true;
        }


        Connection con = new MysqlConnector().getCon("bot");
        Statement stmtOne = con.createStatement();
        ResultSet res = stmtOne.executeQuery("SELECT * FROM users WHERE discordid = '" + member.getId() + "\'");
        while(res.next()) {
            steamID = res.getString("steamid64");
        }


        retrieveBreachInfo();

    }

    public RebornDiscordPlayer(String steamID) throws SQLException {
        this.member = null;
        this.points = -1;
        this.premiumPoints = -1;
        this.steamID = steamID;

        retrieveBreachInfo();
    }

    public void retrieveBreachInfo() throws SQLException {
        RebornPlayerUtil.fetchRebornBreachInfo(this);
    }

    public void setDataPoints(int value) {
        this.points = value;
    }

    public void setDataPremiumPoints(int value) {
        this.premiumPoints = value;
    }

    public void setDataName(String name) {
        this.name = name;
    }

    public boolean isValid() {
        return this.steamID != null;
    }

    public void checkIntialized() throws SQLException {
        RebornPlayerUtil.initializePlayerByID(this.member.getId());
    }

    public int getPoints() {
        return this.points;
    }

    public int getPremiumPoints() {
        return this.premiumPoints;
    }

    public String getName() {
        return name;
    }

    public String getDiscordID() {
        return this.discordID;
    }

    public void addPremiumPoint(int premiumPoints) throws SQLException {
        RebornPlayerUtil.addPremiumPointsToSteamID(getSteamID(), premiumPoints);
    }

    public void addPoints(int points) throws SQLException {
        RebornPlayerUtil.addPointsToSteamID(getSteamID(), points);
    }

    public String getSteamID() {
        return steamID;
    }


    public void setSteamID(String steamIDToSet) throws SQLException {
        RebornPlayerUtil.setSteamIDOfDiscordID(getDiscordID(), steamIDToSet);
    }

    public boolean isNitroBooster() {
        return this.isNitroBooster;
    }

    @Override
    public String toString() {
        return "PlayerSettings{" +
                "steamID='" + steamID + '\'' +
                '}';
    }


}
