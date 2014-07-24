package net.netcoding.niftyservers.commands;

import net.netcoding.niftybukkit.minecraft.BukkitCommand;
import net.netcoding.niftyservers.cache.Cache;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerReload extends BukkitCommand {

	public ServerReload(JavaPlugin plugin) {
		super(plugin, "sreload");
		this.setMinimumArgsLength(0);
	}

	@Override
	public void onCommand(CommandSender sender, String alias, String[] args) throws Exception {
		Cache.Config.reload();
		Cache.Servers.reload();
		this.getLog().message(sender, "{0} reloaded.", this.getPluginDescription().getName());
	}

}