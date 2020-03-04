package xyz.msws.gui.guis;

import com.sun.istack.internal.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.msws.gui.functions.items.ItemFunction;
import xyz.msws.gui.utils.Eco;
import xyz.msws.gui.utils.MSG;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@SerializableAs("CItem")
public class CItem implements ConfigurationSerializable {
    private ItemStack item;
    private ItemMeta meta;
    private List<ItemFunction> functions = new ArrayList<>();
    private Map<String, Object> data = new HashMap<>();
    private Damager damager;

    public CItem() {
        String ver = Bukkit.getBukkitVersion().substring(0, Bukkit.getBukkitVersion().indexOf("-"));
        switch (ver) {
            case "1.13":
            case "1.13.1":
            case "1.13.2":
            case "1.14":
            case "1.14.1":
            case "1.14.2":
            case "1.14.3":
            case "1.14.4":
            case "1.15":
            case "1.15.1":
            case "1.15.2":
                damager = new ConventionalDamager();
                break;
            case "1.8.8":
            case "1.9.4":
            case "1.10.2":
            case "1.11.2":
            case "1.12.2":
                damager = new LegacyDamager();
                break;
            default:
                MSG.log("Unknown Bukkit Version: " + ver + " defaulting to 1.8 - 1.12 item handling.");
                damager = new LegacyDamager();
                break;
        }
    }

    public CItem(ItemStack item) {
        this();
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public CItem(Material mat) {
        this(new ItemStack(mat));
        this.meta = item.getItemMeta();
    }

    public CItem(String s) {
        List<String> values = new ArrayList<>();

        Collections.addAll(values, s.split(":"));
        Material mat = Material.DIRT;
        try {
            mat = Material.valueOf(values.get(0));
        } catch (IllegalArgumentException e) {
            MSG.error("Invalid Material: " + values.get(0));
        }
        int amo = (values.size() > 1) ? (values.get(1).equals("") ? 1 : Integer.parseInt(values.get(1))) : 1;
        int damage = (values.size() > 2) ? (values.get(2).equals("") ? 0 : Integer.parseInt(values.get(2))) : 0;

        item = new ItemStack(mat);
        meta = item.getItemMeta();
        amount(amo);
        if (damage != 0)
            damage(damage);
        if (values.size() > 3) {
            String name = values.get(3);
            if (!name.isEmpty())
                name(name);
        }
        if (values.size() > 4)
            lore(values.get(4).split("\\|"));
        if (values.contains("unbreakable")) {
            unbreakable(true);
            values.remove("unbreakable");
        }
        if (values.size() > 5) {
            for (int i = 5; i < values.size(); i++) {
                try {
                    ItemFlag flag = ItemFlag.valueOf(values.get(i));
                    itemFlag(flag);
                    continue;
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

                Enchantment ench = Enchantment.getByKey(
                        NamespacedKey.minecraft(values.get(i).substring(0, values.get(i).indexOf("=")).toLowerCase()));

                enchantment(ench, Integer.parseInt(values.get(i).substring(values.get(i).indexOf("=") + 1)));
            }
        }
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    public CItem(Map<String, Object> data) {
        this();
        Material mat = Material.DIRT;
        try {
            mat = Material.valueOf((String) data.get("Icon"));
        } catch (IllegalArgumentException e) {
            MSG.error("Invalid Material: " + data.get("Icon"));
        }
        this.item = new ItemStack(mat);
        this.meta = item.getItemMeta();
        if (data.containsKey("Amount"))
            amount((int) data.get("Amount"));
        if (data.containsKey("Damage"))
            damage((int) data.get("Damage"));
        if (data.containsKey("Name"))
            name((String) data.get("Name"));
        if (data.containsKey("Lore"))
            lore((List<String>) data.get("Lore"));
        if (data.containsKey("ItemFlags")) {
            for (String flag : (List<String>) data.get("ItemFlags"))
                itemFlag(ItemFlag.valueOf(flag.toUpperCase()));
        }
        if (data.containsKey("Unbreakable"))
            unbreakable((boolean) data.get("Unbreakable"));
        if (data.containsKey("Enchantments")) {
            for (String e : (List<String>) data.get("Enchantments")) {
                Enchantment ench = Enchantment.getByName(e.split(":")[0].toUpperCase());
                enchantment(ench, Integer.parseInt(e.split(":")[1]));
            }
        }

        this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public boolean hasData(String key) {
        return data.containsKey(key);
    }

    public String getName() {
        return meta.hasDisplayName() ? meta.getDisplayName() : MSG.camelCase(item.getType() + "");
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(item.getType()).append(":");
        result.append(item.getAmount()).append(":");
        if (item instanceof Damageable)
            result.append(((Damageable) item).getDamage()).append(":");
        result.append(meta.hasDisplayName() ? meta.getDisplayName() : "").append(":");
        result.append(meta.hasLore() ? String.join("|", meta.getLore()) : "").append(":");
        if (meta.isUnbreakable())
            result.append("unbreakable:");
        for (ItemFlag flag : ItemFlag.values()) {
            if (item.hasItemFlag(flag))
                result.append(flag.toString()).append(":");
        }
        for (Entry<Enchantment, Integer> ench : item.getEnchantments().entrySet())
            result.append(ench.getKey().getKey().getKey().toLowerCase()).append("=").append(ench.getValue()).append(":");

        return result.toString().substring(0, result.length() - 2);
    }

    @SuppressWarnings("deprecation")
    @Override
    @NotNull
    public Map<String, Object> serialize() {
        Map<String, Object> data = this.data;
        data.put("Icon", item.getType().toString());
        data.put("Damage", (meta instanceof Damageable) ? ((Damageable) meta).getDamage() : 0);
        data.put("Amount", item.getAmount());
        if (meta.hasDisplayName())
            data.put("Name", meta.getDisplayName());
        if (meta.hasLore())
            data.put("Lore", meta.getLore());
        data.put("ItemFlags",
                meta.getItemFlags().parallelStream().map(Enum::toString).collect(Collectors.toSet()));
        data.put("Unbreakable", meta.isUnbreakable());

        List<String> enchantments = new ArrayList<>();
        for (Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet())
            enchantments.add(entry.getKey().getName() + ":" + entry.getValue());

        data.put("Enchantments", enchantments);
        return data;
    }

    public CItem name(String name) {
        meta.setDisplayName(MSG.color("&r" + MSG.papi(null, name)));
        return this;
    }

    public CItem lore(List<String> lore) {
        meta.setLore(lore.stream().map(s -> MSG.color("&r" + format(s))).collect(Collectors.toList()));
        return this;
    }

    private String format(String s) {
        return s.replace("%sell_price%", (int) Eco.getSellPrice(item) + "")
                .replace("%buy_price%", (int) Eco.getBuyPrice(item) + "")
                .replace("%price%", (int) Eco.getPrice(item) + "");
    }

    public CItem lore(String... lore) {
        lore(Arrays.asList(lore));
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }

    public CItem amount(int amo) {
        item.setAmount(amo);
        return this;
    }

    public CItem damage(int damage) {
        this.item = damager.damage(item, damage);
        return this;
    }

    public int getDamage() {
        return damager.getDamage(this.item);
    }

    public CItem itemFlag(ItemFlag... flag) {
        meta.addItemFlags(flag);
        return this;
    }

    public CItem unbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    public CItem enchantment(Enchantment ench, int level) {
        item.addUnsafeEnchantment(ench, level);
        return this;
    }

    public List<ItemFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<ItemFunction> functions) {
        this.functions = functions;
    }

    public void clearFunctions() {
        this.functions.clear();
    }

    public void addFunction(ItemFunction function) {
        this.functions.add(function);
    }

}
