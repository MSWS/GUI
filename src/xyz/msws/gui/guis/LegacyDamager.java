package xyz.msws.gui.guis;

import org.bukkit.inventory.ItemStack;

public class LegacyDamager implements Damager {
    @Override
    public ItemStack damage(ItemStack item, int damage) {
        item.setDurability((short) damage);
        return item;
    }

    @Override
    public int getDamage(ItemStack item) {
        return item.getDurability();
    }
}
