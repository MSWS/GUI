package xyz.msws.gui;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.msws.gui.commands.ShopCommand;
import xyz.msws.gui.guis.GUIManager;
import xyz.msws.gui.utils.Metrics;

import java.io.File;

public class GUIPlugin extends JavaPlugin implements Listener {
    private static GUIPlugin plugin;

    //	private Shop shop;
    private GUIManager guis;
    private YamlConfiguration config;

    public static GUIPlugin getPlugin() {
        return plugin;
    }

    private final int ID = 6683;

    @Override
    public void onEnable() {
        Metrics metrics = new Metrics(this, ID);

        if (!new File(getDataFolder(), "prices.yml").exists())
            saveResource("prices.yml", false);
        if (!new File(getDataFolder(), "config.yml").exists())
            saveResource("config.yml", false);
        plugin = this;

        this.config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));

        guis = new GUIManager(this);
        guis.loadGUIs();
        new ShopCommand(this);

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    public YamlConfiguration getConfig() {
        return this.config;
    }

    public GUIManager getGUIManager() {
        return guis;
    }
}
