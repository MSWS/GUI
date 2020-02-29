package xyz.msws.shop.shops;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import xyz.msws.shop.ShopPlugin;

public class ShopManager {
	private Map<UUID, Shop> playerShops;
	private Map<String, Shop> shops;

	@SuppressWarnings("unused")
	private ShopPlugin plugin;

	public ShopManager(ShopPlugin plugin) {
		this.plugin = plugin;

		this.playerShops = new HashMap<UUID, Shop>();
		this.shops = new HashMap<>();
	}

	public void loadShops(ConfigurationSection shops) {
		for (String shopKey : shops.getKeys(false)) {
			ConfigurationSection sec = shops.getConfigurationSection(shopKey);
			addShop(new Shop(shopKey, sec));
		}
	}

	public void open(Player player, Shop shop) {
		shop.open(player);
		this.playerShops.put(player.getUniqueId(), shop);
	}

	public void open(Player player, Shop shop, String page) {
		shop.open(player, page);
		this.playerShops.put(player.getUniqueId(), shop);
	}

	public void open(Player player, String shop) {
		Shop s = getShop(shop);
		s.open(player);
		this.playerShops.put(player.getUniqueId(), s);
	}

	public void close(Player player, Shop shop) {
		this.playerShops.remove(player.getUniqueId());
		shop.close(player);
	}

	public void addShop(Shop shop) {
		this.shops.put(shop.getId(), shop);
	}

	public Shop getShop(String id) {
		return shops.get(id);
	}

	public Shop getShop(Player player) {
		return getShop(player.getUniqueId());
	}

	public Shop getShop(UUID uuid) {
		return playerShops.get(uuid);
	}

}
