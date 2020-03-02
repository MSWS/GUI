package xyz.msws.gui.guis;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.msws.gui.GUIPlugin;
import xyz.msws.gui.utils.Eco;
import xyz.msws.gui.utils.MSG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ShopFunction implements PageFunction {
    @Override
    public void onCreate(GUIPage page, Inventory inv) {

    }

    @Override
    public void onClick(GUIPage page, InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Inventory inv = event.getClickedInventory();
        if (inv == null) {
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, .5f);
            return;
        }
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1.5f);
        if (inv.getType() == InventoryType.PLAYER)
            return;
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < inv.getSize(); i++) {
                    ItemStack item = inv.getItem(i);
                    if (item == null || item.getType() == Material.AIR)
                        continue;
                    double v = Eco.getSellPrice(item);
                    if (v == 0)
                        continue;
                    ItemMeta meta = item.getItemMeta();
                    List<String> l = meta.hasLore() && meta.getLore() != null ? meta.getLore() : new ArrayList<>();
                    if (l.contains(MSG.color("&eSell Value")))
                        continue;
                    l.add("");
                    l.add(MSG.color("&eSell Value"));
                    l.add(MSG.color("&6" + v + " Gold"));
                    item.setLore(l);
                }

                new BukkitRunnable() {
                    List<String> lore = new ArrayList<>(Arrays.asList("", "&7Sell All Items", "&eWorth: &6%total% Gold"));
                    CItem ctotal = new CItem(Material.HOPPER).name("&c&lSell All").lore(lore);

                    @Override
                    public void run() {
                        double total = 0;

                        for (int i = 0; i < inv.getSize(); i++) {
                            ItemStack item = inv.getItem(i);
                            if (item == null || item.getType() == Material.AIR)
                                continue;
                            double v = Eco.getSellPrice(item);
                            total += v;
                        }

                        double finalTotal = total;
                        lore = lore.stream().map(s -> s.replace("%total%", finalTotal + "")).collect(Collectors.toList());

                        ctotal.lore(lore);
                        inv.setItem(49, ctotal.build());
                    }
                }.runTaskLater(GUIPlugin.getPlugin(), 1);

            }
        }.runTaskLater(GUIPlugin.getPlugin(), 1);

    }

    @Override
    public void onClose(GUIPage page, InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();

        for (int i = 0; i < inv.getSize(); i++) {
            if (i == 49) // TODO
                continue;
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR)
                continue;
            player.getInventory().addItem(item);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 2);
        }
    }
}
