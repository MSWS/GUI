package xyz.msws.gui.functions.items;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.msws.gui.GUIPlugin;
import xyz.msws.gui.guis.GUIManager;

public class OpenGUIFunction implements ItemFunction {

    private String gui, page;

    public OpenGUIFunction(String gui, String page) {
        this.gui = page;
        this.page = page;
    }

    @Override
    public void execute(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        player.setMetadata("ignoreClose", new FixedMetadataValue(GUIPlugin.getPlugin(), true));
        new BukkitRunnable() {

            @Override
            public void run() {
                GUIManager manager = GUIPlugin.getPlugin().getGUIManager();
                manager.open(player, manager.getGUI(gui), page);
            }
        }.runTaskLater(GUIPlugin.getPlugin(), 1);
    }

}
