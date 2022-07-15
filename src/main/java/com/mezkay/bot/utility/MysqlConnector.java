package com.mezkay.bot.utility;

import java.sql.*;
import java.util.HashMap;

public class MysqlConnector {
    private Connection con;
    private final static HashMap<String, String> dbLists = new HashMap();


    public static void init() {
        dbLists.put("ps2", "jdbc:mysql://51.210.177.120:3306/pointshop2,pointshop2,4o54eVvjkGbQar6Jvj1p");
        dbLists.put("bot", "jdbc:mysql://51.210.177.120:3306/rebornbot,rebornbot,cO5kyFpqlne09C96FIZj");

    }

    public MysqlConnector() {
    }

    public Connection getCon(String db) {
        String[] dbLog = dbLists.get(db).split(",");
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(dbLog[0],dbLog[1],dbLog[2]);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public void disconnect() throws SQLException {

    }



}
