package net.netcoding.niftyservers.listeners;

import net.netcoding.niftybukkit.minecraft.BukkitListener;
import net.netcoding.niftybukkit.minecraft.events.BungeeLoadedEvent;
import net.netcoding.niftybukkit.minecraft.events.BungeeServerLoadedEvent;
import net.netcoding.niftyservers.cache.Cache;

import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerLoaded extends BukkitListener {

	public ServerLoaded(JavaPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onBungeeLoaded(BungeeLoadedEvent event) {
		Cache.Servers.save();
	}

	@EventHandler
	public void onBungeeServerLoaded(BungeeServerLoadedEvent event) {
		Cache.Servers.addServer(event.getServer().getName());
	}

}