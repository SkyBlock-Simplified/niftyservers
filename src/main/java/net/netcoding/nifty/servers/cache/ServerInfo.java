package net.netcoding.nifty.servers.cache;

import net.netcoding.nifty.common.Nifty;
import net.netcoding.nifty.common.minecraft.inventory.item.ItemStack;
import net.netcoding.nifty.core.util.RegexUtil;
import net.netcoding.nifty.core.util.StringUtil;
import net.netcoding.nifty.core.yaml.YamlMap;

public class ServerInfo extends YamlMap {

	private static final transient ItemStack DEFAULT_ITEM;

	static {
		DEFAULT_ITEM = Nifty.getItemDatabase().get("GRASS", 1);
	}

	private boolean restricted = false;
	private boolean hidden = false;
	private ItemStack item = DEFAULT_ITEM;

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