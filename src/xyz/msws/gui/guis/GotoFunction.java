package xyz.msws.gui.guis;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.msws.gui.GUIPlugin;

public class GotoFunction implements ItemFunction {

    private String page;

    public GotoFunction(String page) {
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
                manager.open(player, manager.getGUI(player), page);
            }
        }.runTaskLater(GUIPlugin.getPlugin(), 1);
    }

}
