package xyz.msws.gui.guis;

import com.google.common.base.Preconditions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.msws.gui.GUIPlugin;
import xyz.msws.gui.functions.pages.PageFunction;
import xyz.msws.gui.functions.pages.ShopFunction;
import xyz.msws.gui.utils.MSG;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIManager {
    private Map<UUID, GUI> playerShops;
    private Map<String, GUI> shops;
    private Map<String, PageFunction> functions;

    @SuppressWarnings("unused")
    private GUIPlugin plugin;

    public GUIManager(GUIPlugin plugin) {
        this.plugin = plugin;

        this.playerShops = new HashMap<>();
        this.shops = new HashMap<>();
        this.functions = new HashMap<>();

        functions.put("ShopFunction", new ShopFunction());
    }

    public PageFunction getPageFunction(String f) {
        return functions.get(f);
    }

    public void loadGUIs() {
        File shops = new File(plugin.getDataFolder(), "guis/");
        if (!shops.exists()) {
            shops.mkdir();
            InputStream input = plugin.getResource("shop.yml");
            try {
                File target = new File(plugin.getDataFolder(), "guis/shop.yml");
                Files.copy(input, target.toPath());
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (shops.listFiles() == null) {
            MSG.warn("No shops found");
            return;
        }
        for (File f : shops.listFiles()) {
            YamlConfiguration shopYml = YamlConfiguration.loadConfiguration(f);
            addGUI(new GUI(f.getName().replace(".yml", ""), shopYml.getValues(false)));
        }
    }

    @Deprecated
    public void loadGUIs(ConfigurationSection shops) {
        for (String shopKey : shops.getKeys(false)) {
            ConfigurationSection sec = shops.getConfigurationSection(shopKey);
            if (sec == null)
                continue;
            addGUI(new GUI(shopKey, sec));
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
        GUI s = getGUI(shop);
        s.open(player);
        this.playerShops.put(player.getUniqueId(), s);
    }

    public void close(Player player, GUI shop) {
        this.playerShops.remove(player.getUniqueId());
        shop.close(player);
    }

    public void addGUI(GUI shop) {
        MSG.log("Adding shop: " + shop.getId());
        this.shops.put(shop.getId(), shop);
    }

    public GUI getGUI(String id) {
        Preconditions.checkArgument(shops.containsKey(id), "GUIs does not contain " + id);
        return shops.get(id);
    }

    public GUI getGUI(Player player) {
        return getGUI(player.getUniqueId());
    }

    public GUI getGUI(UUID uuid) {
        return playerShops.get(uuid);
    }

}
