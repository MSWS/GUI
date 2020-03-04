package xyz.msws.gui.guis;

import com.google.common.base.Preconditions;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ConventionalDamager implements Damager {
    @Override
    public ItemStack damage(ItemStack item, int damage) {
        Preconditions.checkArgument(item.getItemMeta() instanceof Damageable, "Item is not damagerable ", item);
        Damageable dmg = (Damageable) item.getItemMeta();
        dmg.setDamage(damage);
        item.setItemMeta((ItemMeta) dmg);
        return item;
    }

    @Override
    public int getDamage(ItemStack item) {
        if (!(item.getItemMeta() instanceof Damageable))
            return 0;
        Preconditions.checkArgument(item.getItemMeta() instanceof Damageable, "Item is not damagerable ", item);
        Damageable dmg = (Damageable) item.getItemMeta();
        return dmg.getDamage();
    }
}
