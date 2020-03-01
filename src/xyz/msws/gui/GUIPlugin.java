package xyz.msws.gui;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.msws.gui.commands.ShopCommand;
import xyz.msws.gui.shops.GUIManager;

public class GUIPlugin extends JavaPlugin implements Listener {
	private static GUIPlugin plugin;

//	private Shop shop;
	private GUIManager shops;

	@Override
	public void onEnable() {
		saveResource("shops.yml", false);
		saveResource("prices.yml", false);
		plugin = this;

//		shops = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "shops.yml"));
		shops = new GUIManager(this);
		shops.loadShops(YamlConfiguration.loadConfiguration(new File(getDataFolder(), "shops.yml")));

		new ShopCommand(this);

//		shop = new Shop(shops.getConfigurationSection("DefaultShop").getValues(false));

		Bukkit.getPluginManager().registerEvents(this, this);
	}

	public static GUIPlugin getPlugin() {
		return plugin;
	}

	public GUIManager getShopManager() {
		return shops;
	}
}
