package xyz.msws.gui.shops;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import xyz.msws.gui.GUIPlugin;

public class GUIManager {
	private Map<UUID, GUI> playerShops;
	private Map<String, GUI> shops;

	@SuppressWarnings("unused")
	private GUIPlugin plugin;

	public GUIManager(GUIPlugin plugin) {
		this.plugin = plugin;

		this.playerShops = new HashMap<UUID, GUI>();
		this.shops = new HashMap<>();
	}

	public void loadShops(ConfigurationSection shops) {
		for (String shopKey : shops.getKeys(false)) {
			ConfigurationSection sec = shops.getConfigurationSection(shopKey);
			addShop(new GUI(shopKey, sec));
		}
	}

	public void open(Player player, GUI shop) {
		shop.open(player);
		this.playerShops.put(player.getUniqueId(), shop);
	}

	public void open(Player player, GUI shop, String page) {
		shop.open(player, page);
		this.playerShops.put(player.getUniqueId(), shop);
	}

	public void open(Player player, String shop) {
		GUI s = getShop(shop);
		s.open(player);
		this.playerShops.put(player.getUniqueId(), s);
	}

	public void close(Player player, GUI shop) {
		this.playerShops.remove(player.getUniqueId());
		shop.close(player);
	}

	public void addShop(GUI shop) {
		this.shops.put(shop.getId(), shop);
	}

	public GUI getShop(String id) {
		return shops.get(id);
	}

	public GUI getShop(Player player) {
		return getShop(player.getUniqueId());
	}

	public GUI getShop(UUID uuid) {
		return playerShops.get(uuid);
	}

}
