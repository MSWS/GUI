package xyz.msws.shop;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.msws.shop.shops.Shop;

public class ShopPlugin extends JavaPlugin implements Listener {
	private static ShopPlugin plugin;

	private Shop shop;

	private FileConfiguration shops;

	@Override
	public void onEnable() {
		saveResource("shops.yml", false);
		plugin = this;

		shops = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "shops.yml"));

		shop = new Shop(shops.getConfigurationSection("DefaultShop").getValues(false));

		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onSneak(PlayerToggleSneakEvent event) {
		if (event.isSneaking())
			return;
		shop.open(event.getPlayer());
	}

	public Shop getShop() {
		return shop;
	}

	public static ShopPlugin getPlugin() {
		return plugin;
	}
}
