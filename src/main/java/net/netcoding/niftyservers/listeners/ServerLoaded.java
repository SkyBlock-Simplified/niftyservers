package net.netcoding.niftyservers.listeners;

import net.netcoding.niftybukkit.NiftyBukkit;
import net.netcoding.niftybukkit.minecraft.BukkitListener;
import net.netcoding.niftybukkit.minecraft.events.BungeeLoadedEvent;
import net.netcoding.niftybukkit.minecraft.events.BungeeServerLoadedEvent;
import net.netcoding.niftyservers.NiftyServers;

import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerLoaded extends BukkitListener {

	public ServerLoaded(JavaPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onBungeeLoaded(BungeeLoadedEvent event) {
		NiftyServers.getServersConfig().save();
		NiftyServers.getFakeInventory().setTotalSlots(NiftyBukkit.getBungeeHelper().getServers().size());
	}

	@EventHandler
	public void onBungeeServerLoaded(BungeeServerLoadedEvent event) {
		NiftyServers.getServersConfig().addServer(event.getServer().getName());
	}

}