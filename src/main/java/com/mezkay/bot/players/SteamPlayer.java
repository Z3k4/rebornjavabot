package com.mezkay.bot.players;

public class SteamPlayer {
    private String name;
    private String avatar;
    private String steamID;
    private String profileURL;
    private boolean validUser;

    public SteamPlayer(String name, String avatar, String steamID, String profileURL) {
        this.name = name;
        this.avatar = avatar;
        this.steamID = steamID;
        this.profileURL = profileURL;
        validUser = true;
    }

    public SteamPlayer(boolean unvalid) {
        validUser = false;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getSteamID() {
        return steamID;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public boolean getValidUser() {
        return validUser;
    }
}
