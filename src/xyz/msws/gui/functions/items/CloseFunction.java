package xyz.msws.gui.functions.items;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.msws.gui.GUIPlugin;

public class CloseFunction implements ItemFunction {
    @Override
    public void execute(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        GUIPlugin.getPlugin().getGUIManager().close(player, GUIPlugin.getPlugin().getGUIManager().getGUI(player));
        player.closeInventory();
    }
}
