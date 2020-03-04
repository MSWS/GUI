package xyz.msws.gui.utils;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.msws.gui.GUIPlugin;
import xyz.msws.gui.guis.CItem;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

public class Eco {

    private static Map<Material, Double> goldValues = new HashMap<Material, Double>();

    private static Map<ItemStack, Double> values = new HashMap<>();

    private static double sellPrice = .8, buyPrice = 1.4;

    static {
        File priceFile = new File(GUIPlugin.getPlugin().getDataFolder(), "prices.yml");
        if (priceFile.exists()) {
            YamlConfiguration prices = YamlConfiguration.loadConfiguration(priceFile);
            for (Entry<String, Object> item : prices.getValues(false).entrySet()) {
                CItem i = new CItem(item.getKey());
                values.put(i.build(), ((Number) item.getValue()).doubleValue());
            }
        }

        if (!GUIPlugin.getPlugin().getConfig().isConfigurationSection("PurchaseWithItems")) {
            MSG.log("Unable to get shop prices from config.yml");
        } else {
            Map<String, Object> values = GUIPlugin.getPlugin().getConfig().getConfigurationSection("PurchaseWithItems").getValues(false);

            for (Entry<String, Object> e : values.entrySet())
                goldValues.put(Material.valueOf(e.getKey()), ((Number) e.getValue()).doubleValue());
        }


//        goldValues.put(Material.GOLD_NUGGET, 1.0);
//        goldValues.put(Material.GOLD_INGOT, 9.0);
//        goldValues.put(Material.GOLD_BLOCK, 9 * 9.0);
    }

    public static double getPrice(Material mat) {
        return values.getOrDefault(new ItemStack(mat), 0.0);
    }

    public static double getPrice(ItemStack item) {
        return values.getOrDefault(item, 0.0);
    }

    public static double getSellPrice(Material mat) {
        return getPrice(mat) * sellPrice;
    }

    public static double getSellPrice(ItemStack item) {
        double price = getPrice(item.getType()) * item.getAmount() * sellPrice;
        if (item.getType().getMaxDurability() == 0)
            return price;
        int dmg = new CItem(item).getDamage();
        double percent = (double) (item.getType().getMaxDurability() - dmg) / item.getType().getMaxDurability();
        price *= percent;
        return price;
    }

    public static double getBuyPrice(Material mat) {
        return getPrice(mat) * buyPrice;
    }

    public static double getBuyPrice(ItemStack item) {
        return getPrice(item) * buyPrice;
    }

    public static double getGold(Inventory inv) {
        return getGold(inv.getContents());
    }

    public static double getGold(ItemStack[] items) {
        return getGold(Arrays.asList(items));
    }

    public static double getGold(Iterable<ItemStack> items) {
        double total = 0;
        for (ItemStack item : items) {
            if (item == null || item.getType() == Material.AIR)
                continue;
            total += goldValues.getOrDefault(item.getType(), 0.0) * item.getAmount();
        }
        return total;
    }

    public static List<ItemStack> makeChange(double amo) {
        List<ItemStack> result = new ArrayList<ItemStack>();
        for (Entry<Material, Double> value : goldValues.entrySet()) {
            if (value.getValue() > amo)
                continue;
            int count = (int) Math.ceil(amo / value.getValue());
            amo -= count * value.getValue();
            result.add(new ItemStack(value.getKey(), count));
        }
        return result;
    }

    public static void deduct(Inventory inv, double amo) {
        List<ItemStack> remove = makeChange(amo, inv.getContents().clone(), true);

        double total = 0;

        for (ItemStack item : remove) {
            if (item == null || item.getType() == Material.AIR)
                continue;
            int left = item.getAmount();
            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack it = inv.getItem(i);
                if (it == null || it.getType() == Material.AIR || it.getType() != item.getType())
                    continue;

                if (it.getAmount() < left) {
                    left -= it.getAmount();
                    total += goldValues.get(it.getType()) * it.getAmount();
                    inv.setItem(i, new ItemStack(Material.AIR));
                } else {
                    ItemStack clone = new ItemStack(it);
                    total += goldValues.get(it.getType()) * left;
                    clone.setAmount(clone.getAmount() - left);
                    inv.setItem(i, clone);
                }
                if (left <= 0)
                    break;
            }
        }

        double diff = total - amo;
        for (ItemStack item : makeChange(diff)) {
            inv.addItem(item);
        }
    }

    public static List<ItemStack> makeChange(double amo, ItemStack[] contents, boolean small) {
        return makeChange(amo, Arrays.asList(contents), small);
    }

    public static List<ItemStack> makeChange(double amo, Collection<? extends ItemStack> options,
                                             boolean useSmallItems) {
        List<ItemStack> result = new ArrayList<ItemStack>();
        List<ItemStack> clone = new ArrayList<>();

        clone.addAll(options);
        clone.sort(value());
        if (useSmallItems)
            Collections.reverse(clone);

        for (ItemStack item : clone) {
            if (item == null || item.getType() == Material.AIR)
                continue;
            if (!goldValues.containsKey(item.getType()))
                continue;
            double value = goldValues.getOrDefault(item.getType(), 0.0);
            if (value == 0)
                continue;
            int count = (int) Math.min(Math.ceil(amo / value), item.getAmount());
            amo -= count * value;
            result.add(new ItemStack(item.getType(), count));
        }

        return result;
    }

    public static Comparator<Entry<Material, Double>> entryValue() {
        return new Comparator<Map.Entry<Material, Double>>() {
            @Override
            public int compare(Entry<Material, Double> o1, Entry<Material, Double> o2) {
                if (o2.getValue().equals(o2.getValue()))
                    return 0;
                return o1.getValue() > o2.getValue() ? -1 : 1;
            }
        };
    }

    public static Comparator<ItemStack> value() {
        return new Comparator<ItemStack>() {
            @Override
            public int compare(ItemStack arg0, ItemStack arg1) {
                if (arg0 == null && arg1 == null)
                    return 0;
                if (arg0 != null && arg1 != null)
                    if (arg0.equals(arg1))
                        return 0;
                return goldValues.getOrDefault(arg0 == null ? Material.AIR : arg0.getType(), 0.0) > goldValues
                        .getOrDefault(arg1 == null ? Material.AIR : arg1.getType(), 0.0) ? -1 : 1;
            }
        };
    }
}
