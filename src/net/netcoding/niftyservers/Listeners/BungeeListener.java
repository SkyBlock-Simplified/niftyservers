package net.netcoding.niftyservers.Listeners;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.netcoding.niftyservers.NiftyServers;
import net.netcoding.niftyservers.Managers.ServerChestInterface;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeListener implements PluginMessageListener {

    private NiftyServers plugin;

    public BungeeListener(NiftyServers plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(String arg0, Player arg1, byte[] arg2) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(arg2));
        String channel;
        String servers = null;
        String arg = NiftyServers.PlayerArg.get(arg1);

        try {
            channel = in.readUTF();
            if (channel.equals("GetServers")) {
                servers = in.readUTF();
                if (arg.equalsIgnoreCase("list")) { // Show List
                    showList(arg1, servers);
                } else if (servers.contains(arg)) { // Direct Connect
                    directConnect(arg1, servers, arg);
                } else if (arg.equalsIgnoreCase("null")) {  // If null, try chest, otherwise, list
                    if (arg1.hasPermission("serverchest.use.chest")) {
                        new ServerChestInterface(plugin, servers, arg1);
                    } else {
                        showList(arg1, servers);
                    }
                } else {    // Incorrect Usage
                    arg1.sendMessage(ChatColor.RED + "Usage: /" + plugin.getConfig().getString("game-command") + " [list|<server>] ");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BungeeListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void directConnect(Player arg1, String servers, String arg) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        if (arg1.hasPermission("serverchest.use.*") || arg1.hasPermission("serverchest.use.direct")) {
            if (arg1.hasPermission("serverchest.server.*") || arg1.hasPermission("serverchest.server." + arg)) {
                Boolean notrestricted;
                try {
                    notrestricted = !Boolean.valueOf(plugin.getConfig().getConfigurationSection("servers." + arg).get("restricted").toString());
                } catch (Exception e) {
                    notrestricted = true;
                }
                if (notrestricted) {
                    try {
                        out.writeUTF("Connect");
                        out.writeUTF(arg);
                        arg1.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
                    } catch (IOException e) {
                    }
                } else {
                    arg1.sendMessage(ChatColor.DARK_RED + "You do not have access to restricted servers.");
                }
            } else {
                arg1.sendMessage(ChatColor.DARK_RED + "You do not have access to that server.");
            }
        } else {
            arg1.sendMessage(ChatColor.DARK_RED + "You do not have access to direct teleportation.");
        }
    }

    public void showList(Player arg1, String servers) {
        if (arg1.hasPermission("serverchest.use.list")) {
            String defined = "";
            String undefined = "";

            Boolean notrestricted;
            for (String server : servers.split(", ")) {
                try {
                    notrestricted = !Boolean.valueOf(plugin.getConfig().getConfigurationSection("servers." + server).get("restricted").toString());
                } catch (Exception e) {
                    notrestricted = true;
                }
                if (notrestricted || arg1.hasPermission("serverchest.use.restricted")) {
                    try {
                        defined += this.plugin.getConfig().getConfigurationSection("servers." + server).get("name").toString() + "&6, ";
                    } catch (Exception e) {
                        undefined += "&6" + server + ", ";
                    }
                }
            }
            if (undefined.length() <= 1) {
                arg1.sendMessage(("&6Servers: " + defined + undefined).replaceAll("(?i)&([a-fk-or0-9])", "§$1"));
            } else {
                arg1.sendMessage(("&6Servers: " + defined + undefined.substring(0, undefined.length() - 2)).replaceAll("(?i)&([a-fk-or0-9])", "§$1"));
            }
        } else {
            arg1.sendMessage(ChatColor.DARK_RED + "You do not have access to that command.");
        }
    }
}
