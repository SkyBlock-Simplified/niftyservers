package net.netcoding.niftyservers.cache;

import java.util.HashMap;
import java.util.Map;

import net.netcoding.niftybukkit.yaml.Config;

import org.bukkit.plugin.java.JavaPlugin;

public class Servers extends Config {

	private Map<String, ServerInfo> serverList = new HashMap<>();

	public Servers(JavaPlugin plugin) {
		super(plugin, "servers");
	}

	public void addServer(String serverName) {
		this.serverList.put(serverName, new ServerInfo(serverName));
	}

	public ServerInfo getServer(String serverName) {
		return this.serverList.get(serverName);
	}

}