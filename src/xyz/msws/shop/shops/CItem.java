package xyz.msws.shop.shops;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import xyz.msws.shop.utils.MSG;

@SerializableAs("CItem")
public class CItem implements ConfigurationSerializable {
	private ItemStack item;

	private ItemMeta meta;

	private List<GUIFunction> functions = new ArrayList<>();

	private Map<String, Object> data = new HashMap<>();

	public CItem(ItemStack item) {
		this.item = item;
		this.meta = item.getItemMeta();
	}

	public CItem(Material mat) {
		this(new ItemStack(mat));
		this.meta = item.getItemMeta();
	}

	public CItem(String s) {
		List<String> values = new ArrayList<String>();

		for (String e : s.split(":"))
			values.add(e);

		Material mat = Material.valueOf(values.get(0));
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
				}

				Enchantment ench = Enchantment.getByKey(
						NamespacedKey.minecraft(values.get(i).substring(0, values.get(i).indexOf("=")).toLowerCase()));

				enchantment(ench, Integer.parseInt(values.get(i).substring(values.get(i).indexOf("=") + 1)));
			}
		}
	}

	public String getName() {
		return meta.hasDisplayName() ? meta.getDisplayName() : MSG.camelCase(item.getType() + "");
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public CItem(Map<String, Object> data) {
		this(Material.valueOf((String) data.get("Icon")));
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

	@SuppressWarnings("deprecation")
	@Override
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
				meta.getItemFlags().parallelStream().map(flag -> flag.toString()).collect(Collectors.toSet()));
		data.put("Unbreakable", meta.isUnbreakable());

		List<String> enchantments = new ArrayList<>();
		for (Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet())
			enchantments.add(entry.getKey().getName() + ":" + entry.getValue());

		data.put("Enchantments", enchantments);
		return data;
	}

	public CItem name(String name) {
		meta.setDisplayName(MSG.color("&r" + name));
		return this;
	}

	public CItem lore(List<String> lore) {
		meta.setLore(lore.stream().map(s -> MSG.color("&r" + s)).collect(Collectors.toList()));
		return this;
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
		if (damage == 0)
			return this;
		if (!(meta instanceof Damageable))
			throw new UnsupportedOperationException(item.getType() + " is not Damageable");
		((Damageable) meta).setDamage(damage);
		return this;
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

	public List<GUIFunction> getFunctions() {
		return functions;
	}

	public void addFunction(GUIFunction function) {
		this.functions.add(function);
	}

	public void clearFunctions() {
		this.functions.clear();
	}

	public void setFunctions(List<GUIFunction> functions) {
		this.functions = functions;
	}

}
