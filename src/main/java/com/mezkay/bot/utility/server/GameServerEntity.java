package com.mezkay.bot.utility.server;

public class GameServerEntity {
    private String ip;
    private String thumbnailURL;
    private String collection;
    private String connectionURL;

    private String channelID;
    private String messageID;
    private String rawIP;

    public GameServerEntity(String ip, String rawIP, String thumbnailURL, String collection, String connectionURL) {
        this.ip = ip;
        this.thumbnailURL = thumbnailURL;
        this.collection = collection;
        this.connectionURL = connectionURL;
        this.rawIP = rawIP;
    }

    public void setChannelID(String id) {
        this.channelID = id;
    }

    public void setMessageID(String id) {
        this.messageID = id;
    }

    public String getIp() {
        return ip;
    }

    public String getRawIP() {
        return this.rawIP;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public String getCollection() {
        return collection;
    }

    public String getConnectionURL() {
        return connectionURL;
    }

    public String getChannelID() {
        return this.channelID;
    }

    public String getMessageID() {
        return this.messageID;
    }
}
