package net.netcoding.nifty.servers.listeners;

import net.netcoding.nifty.common.Nifty;
import net.netcoding.nifty.common.api.plugin.Event;
import net.netcoding.nifty.common.api.plugin.MinecraftListener;
import net.netcoding.nifty.common.api.plugin.MinecraftPlugin;
import net.netcoding.nifty.common.minecraft.event.bungee.BungeeLoadedEvent;
import net.netcoding.nifty.common.minecraft.event.bungee.BungeeServerLoadedEvent;
import net.netcoding.nifty.servers.NiftyServers;

public class ServerLoaded extends MinecraftListener {

	public ServerLoaded(MinecraftPlugin plugin) {
		super(plugin);
	}

	@Event
	public void onBungeeLoaded(BungeeLoadedEvent event) {
		NiftyServers.getServersConfig().save();
		NiftyServers.getFakeInventory().setTotalSlots(Nifty.getBungeeHelper().getServers().size());
	}

	@Event
	public void onBungeeServerLoaded(BungeeServerLoadedEvent event) {
		NiftyServers.getServersConfig().addServer(event.getServer().getName());
	}

}