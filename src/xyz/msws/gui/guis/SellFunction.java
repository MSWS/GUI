package xyz.msws.gui.guis;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.msws.gui.utils.Eco;
import xyz.msws.gui.utils.MSG;

import java.util.List;

public class SellFunction implements ItemFunction {

    public SellFunction() {

    }

    @Override
    public void execute(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        if (inv == null)
            return;
        Player player = (Player) event.getWhoClicked();

        double value = 0;
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (i == event.getRawSlot())
                continue;
            if (item == null || item.getType() == Material.AIR)
                continue;

            // double v = Eco.getSellPrice(item);
            value += Eco.getSellPrice(item);
        }

        if (value == 0) {
            MSG.tell(player, "Shop", "There aren't any sellable items!");
            return;
        }

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (i == event.getRawSlot())
                continue;
            if (item == null || Eco.getSellPrice(item.getType()) == 0)
                continue;
            inv.setItem(i, new ItemStack(Material.AIR));
        }

        List<ItemStack> items = Eco.makeChange(value);

        for (ItemStack item : items)
            player.getInventory().addItem(item);
    }
}
