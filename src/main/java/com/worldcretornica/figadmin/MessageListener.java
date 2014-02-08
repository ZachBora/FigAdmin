package com.worldcretornica.figadmin;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lilypad.client.connect.api.event.EventListener;
import lilypad.client.connect.api.event.MessageEvent;

public class MessageListener {
    
    private FigAdmin plugin;
    
    public MessageListener(FigAdmin instance){
        plugin = instance;
    }
    
    @EventListener
    public void onMessage(MessageEvent event) {
        if(event.getChannel().equals(plugin.channelname)) {
            try {
                //plugin.getLogger().info("" + event.getMessageAsString());
                
                String[] tokens = event.getMessageAsString().split(";");
                
                String action = tokens[0];
                String name;
                String reason;
                String sender;
                String victimMsg;
                String logMsg;
                
                switch(action){
                case "KICK":
                    if(tokens.length >= 4) {
                        name = tokens[1];
                        reason = tokens[2];
                        sender = tokens[3];
                        victimMsg = plugin.getConfig().getString("messages.kickMsgVictim");
                        logMsg = "[FigAdmin] " + sender + " kicked player " + name + ". Reason: " + reason;
                        boolean foundit = kick(name, reason, sender, logMsg, victimMsg);
                        
                        if(foundit) {
                            String kickerMsgAll = plugin.getConfig().getString("messages.kickMsgBroadcast");
                            kickerMsgAll = kickerMsgAll.replaceAll("%player%", sender);
                            kickerMsgAll = kickerMsgAll.replaceAll("%reason%", reason);
                            kickerMsgAll = kickerMsgAll.replaceAll("%victim%", name);
                            plugin.requestGlobalMessage(kickerMsgAll);
                        }
                    }
                    break;
                case "BAN":
                    if(tokens.length >= 4) {
                        name = tokens[1];
                        reason = tokens[2];
                        sender = tokens[3];
                        victimMsg = plugin.getConfig().getString("messages.banMsgVictim");
                        logMsg = "[FigAdmin] " + sender + " banned player " + name + ". Reason: " + reason;
                        kick(name, reason, sender, logMsg, victimMsg);
                    }
                    break;
                case "TEMPBAN":
                    if(tokens.length >= 4) {
                        name = tokens[1];
                        reason = tokens[2];
                        sender = tokens[3];
                        victimMsg = plugin.getConfig().getString("messages.tempbanMsgVictim");
                        logMsg = "[FigAdmin] " + sender + " tempbanned player " + name + ".";
                        kick(name, reason, sender, logMsg, victimMsg);
                    }
                    break;
                case "MSG":
                    if(tokens.length >= 2) {
                        reason = tokens[1];
                        broadcast(reason);
                    }
                    break;
                }
                
            } catch (Exception e) {
                plugin.getLogger().info(e.getMessage());
            }
        }
    }
    
    private boolean kick(final String name, String reason, String kicker, String logMsg, String kickerMsg){
        Player victim = plugin.getServer().getPlayer(name);

        if (victim == null) {
            return false;            
        }

        // Log in console
        FigAdmin.log.log(Level.INFO, logMsg);

        // Send message to victim
        kickerMsg = kickerMsg.replaceAll("%player%", kicker);
        kickerMsg = kickerMsg.replaceAll("%reason%", reason);
        final String msg = plugin.formatMessage(kickerMsg);
        
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            public void run()
            {
                Player vic = Bukkit.getServer().getPlayer(name);
                if (vic != null) {
                    vic.kickPlayer(msg);
                }
            }
        });
        
        return true;
    }
    
    private void broadcast(String message) {
        plugin.getServer().broadcastMessage(plugin.formatMessage(message));
    }
}
