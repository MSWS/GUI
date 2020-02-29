package xyz.msws.shop;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.msws.shop.commands.ShopCommand;
import xyz.msws.shop.shops.ShopManager;

public class ShopPlugin extends JavaPlugin implements Listener {
	private static ShopPlugin plugin;

//	private Shop shop;
	private ShopManager shops;

	@Override
	public void onEnable() {
		saveResource("shops.yml", false);
		plugin = this;

//		shops = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "shops.yml"));
		shops = new ShopManager(this);
		shops.loadShops(YamlConfiguration.loadConfiguration(new File(getDataFolder(), "shops.yml")));

		new ShopCommand(this);

//		shop = new Shop(shops.getConfigurationSection("DefaultShop").getValues(false));

		Bukkit.getPluginManager().registerEvents(this, this);
	}

	public static ShopPlugin getPlugin() {
		return plugin;
	}

	public ShopManager getShopManager() {
		return shops;
	}
}
