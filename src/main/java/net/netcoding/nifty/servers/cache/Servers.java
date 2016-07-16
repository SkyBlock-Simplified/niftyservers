package net.netcoding.nifty.servers.cache;

import net.netcoding.nifty.common.api.plugin.MinecraftPlugin;
import net.netcoding.nifty.common.yaml.BukkitConfig;
import net.netcoding.nifty.core.util.concurrent.Concurrent;
import net.netcoding.nifty.core.util.concurrent.ConcurrentMap;

public class Servers extends BukkitConfig {

	private final ConcurrentMap<String, ServerInfo> serverList = Concurrent.newMap();

	public Servers(MinecraftPlugin plugin) {
		super(plugin.getDataFolder(), "servers");
	}

	public void addServer(String serverName) {
		this.getServerList().put(serverName, new ServerInfo());
	}

	public ServerInfo getServer(String serverName) {
		return this.getServerList().get(serverName);
	}

	public ConcurrentMap<String, ServerInfo> getServerList() {
		return this.serverList;
	}

}