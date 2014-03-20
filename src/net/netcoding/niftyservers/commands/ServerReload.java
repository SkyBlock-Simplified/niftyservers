package net.netcoding.niftyservers.commands;

import java.sql.SQLException;

import net.netcoding.niftybukkit.minecraft.BukkitCommand;
import net.netcoding.niftyservers.cache.Cache;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerReload extends BukkitCommand {

	public ServerReload(JavaPlugin plugin) {
		super(plugin, false);
	}

	@Override
	public void command(CommandSender sender, String alias, String[] args) throws SQLException, Exception {
		if (isConsole(sender) || this.hasPermissions(sender, "niftyservers", "reload")) {
			Cache.Config.reload();
			Cache.Servers.reload();
			this.getLog().message(sender, "Config reloaded.");
		}
	}

}