package xyz.msws.gui.guis;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import xyz.msws.gui.GUIPlugin;
import xyz.msws.gui.utils.MSG;
import xyz.msws.gui.utils.Utils;

import java.util.*;
import java.util.Map.Entry;

public class GUI implements Listener {
    @SuppressWarnings("unused")
    private Map<String, Object> data;
    private Map<UUID, GUIPage> playerPages;

    private List<GUIPage> pages;

    private String id;

    public GUI(String id, Map<String, Object> data) {
        this.id = id;
        this.data = data;
        pages = new ArrayList<>();
        playerPages = new HashMap<>();

        for (Entry<String, Object> page : data.entrySet()) {
            Map<String, Object> d = Utils.mapValues(page.getValue(), false);
            GUIPage p = new GUIPage(page.getKey(), d);
            pages.add(p);
        }

        Bukkit.getPluginManager().registerEvents(this, GUIPlugin.getPlugin());
    }

    public GUI(String id, ConfigurationSection section) {
        this(id, section.getValues(true));
    }

    public String getId() {
        return id;
    }

    public void open(Player player) {
        open(player, pages.get(0).getID());
    }

    public void open(Player player, String page) {
        GUIPage p = getPage(page);
        if (p == null) {
            MSG.log("Attempted to open null page (" + page + ")");
            MSG.printStackTrace();
            return;
        }
        playerPages.put(player.getUniqueId(), p);
        player.openInventory(p.create(player));
    }

    public void close(Player player) {
        playerPages.remove(player.getUniqueId());
    }

    public GUIPage getPlayerPage(Player player) {
        return playerPages.get(player.getUniqueId());
    }

    public GUIPage getPage(String name) {
        return pages.stream().filter(p -> p.getID().equals(name)).findFirst().orElse(null);
    }

}
