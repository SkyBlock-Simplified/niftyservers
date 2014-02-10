package net.netcoding.niftyservers.Listeners;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.netcoding.niftyservers.NiftyServers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    private NiftyServers plugin;

    public CommandListener(NiftyServers plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {      
        System.out.print(event.getMessage());
        Configuration config = plugin.getConfig();
        String cmd;
        String args;
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        if (event.getMessage().indexOf(" ") == -1) {
            cmd = event.getMessage().substring(1);
            args = "null";
        } else {
            cmd = event.getMessage().substring(0, event.getMessage().indexOf(" ")).substring(1);
            args = event.getMessage().substring(event.getMessage().indexOf(" ") + 1);
        }

        if (cmd.equalsIgnoreCase(config.getString("game-command"))) {
            event.setCancelled(true);
            try {
                out.writeUTF("GetServers");
                event.getPlayer().sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
            } catch (IOException e) {
            }
            NiftyServers.PlayerArg.put(event.getPlayer(), args);
        }
    }
   
}