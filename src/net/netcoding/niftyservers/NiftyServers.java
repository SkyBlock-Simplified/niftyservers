package net.netcoding.niftyservers;

import net.netcoding.niftybukkit.inventory.FakeInventory;
import net.netcoding.niftybukkit.minecraft.BukkitPlugin;
import net.netcoding.niftyservers.cache.Cache;
import net.netcoding.niftyservers.cache.Config;
import net.netcoding.niftyservers.cache.Servers;
import net.netcoding.niftyservers.commands.Server;
import net.netcoding.niftyservers.commands.ServerReload;
import net.netcoding.niftyservers.listeners.ServerInventory;
import net.netcoding.niftyservers.listeners.ServerLoaded;

public class NiftyServers extends BukkitPlugin {

	@Override
	public void onEnable() {
		this.getLog().console("Loading Cache");
		Cache.Config = new Config(this);
		Cache.Config.init();
		Cache.Servers = new Servers(this);
		Cache.Servers.init();

		this.getLog().console("Registering Commands");
		new Server(this);
		new ServerReload(this);

		this.getLog().console("Registering Listeners");
		new ServerLoaded(this);
		Cache.Inventory = new FakeInventory(this, new ServerInventory(this));
		Cache.Inventory.setAutoCancelled();
		Cache.Inventory.setAutoCenter(Cache.Config.isItemsCentered());
		Cache.Inventory.setTitle(Cache.Config.getTitle());
		Cache.Inventory.setItemOpener(Cache.Config.getItemOpener());
	}

	@Override
	public void onDisable() {
		Cache.Inventory.destroy();
	}

}