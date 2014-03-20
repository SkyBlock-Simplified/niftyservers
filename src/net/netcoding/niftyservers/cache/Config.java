package net.netcoding.niftyservers.cache;

import net.netcoding.niftybukkit.yaml.annotations.Comment;

import org.bukkit.plugin.java.JavaPlugin;

public class Config extends net.netcoding.niftybukkit.yaml.Config {

	@Comment("Inventory Title")
	private String title = "";

	@Comment("Center Inventory Items")
	private boolean centerItems = false;

	public Config(JavaPlugin plugin) {
		super(plugin, "config");
	}

	public String getTitle() {
		return this.title;
	}

	public boolean isItemsCentered() {
		return this.centerItems;
	}

}