package net.netcoding.nifty.servers;

import net.netcoding.nifty.common.api.inventory.FakeInventory;
import net.netcoding.nifty.common.api.plugin.MinecraftPlugin;
import net.netcoding.nifty.servers.cache.Config;
import net.netcoding.nifty.servers.cache.Servers;
import net.netcoding.nifty.servers.commands.Server;
import net.netcoding.nifty.servers.listeners.ServerInventory;
import net.netcoding.nifty.servers.listeners.ServerLoaded;

public class NiftyServers extends MinecraftPlugin {

	private static transient Config PLUGIN_CONFIG;
	private static transient Servers SERVERS_CONFIG;
	private static transient FakeInventory SERVERS_INVENTORY;

	@Override
	public void onEnable() {
		this.getLog().console("Loading Configs");
		try {
			(PLUGIN_CONFIG = new Config(this)).init();
			PLUGIN_CONFIG.startWatcher();
			(SERVERS_CONFIG = new Servers(this)).init();
			SERVERS_CONFIG.startWatcher();
		} catch (Exception ex) {
			this.getLog().console("Unable to monitor plugin config files! Changes will require a restart!", ex);
		}

		this.getLog().console("Registering Commands");
		new Server(this);

		this.getLog().console("Registering Listeners");
		new ServerLoaded(this);
		SERVERS_INVENTORY = new FakeInventory(this, new ServerInventory(this));
		SERVERS_INVENTORY.setAutoCenter(getPluginConfig().isItemsCentered());
		SERVERS_INVENTORY.setTitle(getPluginConfig().getTitle());
		SERVERS_INVENTORY.setItemOpener(getPluginConfig().getItemOpener());
	}

	@Override
	public void onDisable() {
		if (SERVERS_INVENTORY != null)
			SERVERS_INVENTORY.closeAll();
	}

	public static FakeInventory getFakeInventory() {
		return SERVERS_INVENTORY;
	}

	public static Config getPluginConfig() {
		return PLUGIN_CONFIG;
	}

	public static Servers getServersConfig() {
		return SERVERS_CONFIG;
	}

}