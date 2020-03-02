package xyz.msws.gui.guis;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import xyz.msws.gui.GUIPlugin;

public interface PageFunction extends Listener {
    default void register(GUIPage page) {
        Bukkit.getPluginManager().registerEvents(this, GUIPlugin.getPlugin());
    }

    void onCreate(GUIPage page, Inventory result);

    void onClick(GUIPage page, InventoryClickEvent event);

    void onClose(GUIPage page, InventoryCloseEvent event);

}
