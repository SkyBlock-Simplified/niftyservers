package net.netcoding.niftyservers.cache;

import net.netcoding.niftybukkit.NiftyBukkit;
import net.netcoding.niftybukkit.util.RegexUtil;
import net.netcoding.niftybukkit.util.StringUtil;

import org.bukkit.inventory.ItemStack;

public class ServerInfo {

	private static final transient ItemStack DEFAULT_ITEM;
	private boolean restricted = false;
	private boolean hidden = false;
	private ItemStack item = DEFAULT_ITEM;

	static {
		DEFAULT_ITEM = NiftyBukkit.getItemDatabase().get("GRASS", 1);
	}

	public String getDisplayName() {
		return this.getDisplayName(false);
	}

	public String getDisplayName(boolean stripVanilla) {
		String displayName = this.getItem().getItemMeta().getDisplayName();
		return StringUtil.notEmpty(displayName) ? (stripVanilla ? RegexUtil.strip(displayName, RegexUtil.VANILLA_PATTERN) : displayName) : "";
	}

	public ItemStack getItem() {
		return (this.item == null ? DEFAULT_ITEM : this.item).clone();
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

	public void setItem(ItemStack obj) {
		this.item = obj;
	}

}