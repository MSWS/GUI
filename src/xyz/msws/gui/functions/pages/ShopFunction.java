package xyz.msws.gui.functions.pages;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.msws.gui.GUIPlugin;
import xyz.msws.gui.guis.CItem;
import xyz.msws.gui.guis.GUIPage;
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

        Inventory uInv = player.getOpenInventory().getTopInventory();
        Inventory lInv = player.getOpenInventory().getBottomInventory();

        if (event.getClickedInventory() == null) {
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, .5f);
            return;
        }
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1.5f);

        new BukkitRunnable() {
            @Override
            public void run() {
                // Lower inventory
                for (int i = 0; i < lInv.getSize(); i++) {
                    ItemStack item = lInv.getItem(i);
                    lInv.setItem(i, removePriceValue(item));
                }

                // Upper inventory
                for (int i = 0; i < uInv.getSize(); i++) {
                    ItemStack item = uInv.getItem(i);
                    uInv.setItem(i, addPriceValue(item, false));
                }


                new BukkitRunnable() {
                    List<String> lore = new ArrayList<>(Arrays.asList("", "&7Sell All Items", "&eWorth: &6%total% Gold"));
                    CItem ctotal = new CItem(Material.HOPPER).name("&c&lSell All").lore(lore);

                    @Override
                    public void run() {
                        double total = 0;

                        for (int i = 0; i < uInv.getSize(); i++) {
                            ItemStack item = uInv.getItem(i);
                            if (item == null || item.getType() == Material.AIR)
                                continue;
                            double v = Eco.getSellPrice(item);
                            total += v;
                        }

                        double finalTotal = total;
                        lore = lore.stream().map(s -> s.replace("%total%", finalTotal + "")).collect(Collectors.toList());

                        ctotal.lore(lore);
                        uInv.setItem(uInv.getSize() - 5, ctotal.build());
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
            if (i == inv.getSize() - 5) // TODO
                continue;
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR)
                continue;
            player.getInventory().addItem(removePriceValue(item));
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 2);
        }
    }

    private ItemStack addPriceValue(ItemStack item, boolean ignore) {
        if (item == null || item.getType() == Material.AIR)
            return item;
        double v = Eco.getSellPrice(item);
        if (v == 0)
            return item;
        ItemMeta meta = item.getItemMeta();
        List<String> l = meta.hasLore() && meta.getLore() != null ? meta.getLore() : new ArrayList<>();
        if (l.contains(MSG.color("&eSell Value")) && !ignore)
            return item;
        l.add("");
        l.add(MSG.color("&eSell Value"));
        l.add(MSG.color("&6" + (int) v + " Gold"));
        item.setLore(l);
        return item;
    }

    private ItemStack removePriceValue(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return item;

        ItemMeta meta = item.getItemMeta();
        List<String> l = meta.hasLore() && meta.getLore() != null ? meta.getLore() : new ArrayList<>();
        l.removeIf(s -> s.contains("Sell Value") || s.contains(" Gold") || s.equals(""));
        meta.setLore(l);
        item.setItemMeta(meta);
        return item;
    }

}
