package net.netcoding.niftyservers.cache;

import net.netcoding.niftybukkit.NiftyBukkit;
import net.netcoding.niftybukkit.util.RegexUtil;
import net.netcoding.niftybukkit.util.StringUtil;

import org.bukkit.inventory.ItemStack;

public class ServerInfo {

	private static final transient ItemStack DEFAULT_ITEM;
	private String displayName = "";
	private boolean restricted = false;
	private boolean hidden = false;
	private ItemStack item = DEFAULT_ITEM;

	static {
		DEFAULT_ITEM = NiftyBukkit.getItemDatabase().get("2", 1);
	}

	public ServerInfo(String serverName) {
		this.displayName = serverName;
	}

	public String getDisplayName() {
		return this.getDisplayName(false);
	}

	public String getDisplayName(boolean stripVanilla) {
		return StringUtil.notEmpty(this.displayName) ? (stripVanilla ? RegexUtil.strip(this.displayName, RegexUtil.VANILLA_PATTERN) : this.displayName) : "";
	}

	public ItemStack getItem() {
		return this.item == null ? DEFAULT_ITEM : this.item;
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public boolean isRestricted() {
		return this.restricted;
	}

	public void setRestricted(boolean value) {
		this.restricted = value;
	}

	public void setHidden(boolean value) {
		this.hidden = value;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

}