package com.mezkay.bot;


import com.mezkay.bot.manager.ClaimManager;
import com.mezkay.bot.manager.MessageManager;
import com.mezkay.bot.utility.MysqlConnector;
import com.mezkay.bot.utility.claim.ClaimRewards;
import com.mezkay.bot.utility.server.ServerInfo;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

public class Bot {




    public  final static String STEAM_KEY = "9A961C8C71C0FF295FD87A33E86DF16D";
    public static void main(String[] args) throws LoginException {


        String token = "OTgzMDcwNTcyNzM0OTkyMzg0.GO0kVD.Hpb9Bl7L5c_Xw1n-NQcPNArGVK5DTuptu8zvs4";

        //Initialize db
        MysqlConnector.init();

        EnumSet<GatewayIntent> intents = EnumSet.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_MEMBERS
        );


        JDABuilder builder = JDABuilder.createDefault(token, intents);
        builder.addEventListeners(new RebornBaseManager());
        builder.build();


        /*try {
            ServerInfo.printServerStatus();
        }catch (Exception exception) {
            System.out.println(exception);
        }*/

    }



}
