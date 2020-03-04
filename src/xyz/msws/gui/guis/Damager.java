package xyz.msws.gui.guis;

import org.bukkit.inventory.ItemStack;

public interface Damager {
    ItemStack damage(ItemStack item, int damage);

    int getDamage(ItemStack item);
}
