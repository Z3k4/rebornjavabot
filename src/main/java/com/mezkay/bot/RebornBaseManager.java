package com.mezkay.bot;

import com.mezkay.bot.manager.ClaimManager;
import com.mezkay.bot.manager.MessageManager;
import com.mezkay.bot.manager.ModerationManager;
import com.mezkay.bot.utility.server.ServerInfo;
import com.mezkay.bot.utility.workshop.DeleteFolder;
import com.mezkay.bot.utility.workshop.WorkshopDownloader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;

public class RebornBaseManager implements EventListener {
    private ServerInfo serverInfo;
    private ClaimManager claimManager;
    private MessageManager messageManager;
    private ModerationManager moderationManager;
    private JDA jda;


    public void initialize() {
        serverInfo = new ServerInfo(jda);
        claimManager = new ClaimManager();
        messageManager = new MessageManager();
        moderationManager = new ModerationManager();

        WorkshopDownloader.LoadPaths();
        DeleteFolder.startScanning();
    }




    public ServerInfo getServerInfo() {
        return this.serverInfo;

    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof ReadyEvent) {
            jda = event.getJDA();
            this.initialize();
        }

        if(event instanceof MessageReceivedEvent) {
            try {
               claimManager.consumeCommand(((MessageReceivedEvent) event).getMessage());
               messageManager.exec(((MessageReceivedEvent) event).getMessage());
               moderationManager.runCommand(((MessageReceivedEvent) event).getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
