package net.netcoding.niftyservers;

import net.netcoding.niftybukkit.inventory.FakeInventory;
import net.netcoding.niftybukkit.minecraft.BukkitPlugin;
import net.netcoding.niftybukkit.minecraft.BungeeHelper;
import net.netcoding.niftyservers.cache.Config;
import net.netcoding.niftyservers.cache.Servers;
import net.netcoding.niftyservers.cache.Cache;
import net.netcoding.niftyservers.commands.ServerReload;
import net.netcoding.niftyservers.commands.Server;
import net.netcoding.niftyservers.listeners.ServerInventory;

public class NiftyServers extends BukkitPlugin {

	@Override
	public void onEnable() {
		this.getLog().console("Loading Cache");
		Cache.BungeeHelper = new BungeeHelper(this);
		Cache.Servers = new Servers(this);
		Cache.Servers.init();
		Cache.Config = new Config(this);
		Cache.Config.init();

		this.getLog().console("Registering Commands");
		new Server(this);
		new ServerReload(this);

		this.getLog().console("Registering Listeners");
		Cache.Inventory = new FakeInventory(this, new ServerInventory(this), true);
	}

}