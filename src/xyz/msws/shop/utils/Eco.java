package xyz.msws.shop.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Eco {

	private static Map<Material, Double> goldValues = new HashMap<Material, Double>();

	static {
		goldValues.put(Material.GOLD_NUGGET, 1.0);
		goldValues.put(Material.GOLD_INGOT, 9.0);
		goldValues.put(Material.GOLD_BLOCK, 9 * 9.0);

		goldValues = goldValues.entrySet().stream().sorted(entryValue())
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
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
		List<ItemStack> clone = new ArrayList<ItemStack>();

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
				return o1.getValue() > o2.getValue() ? -1 : 1;
			}
		};
	}

	public static Comparator<ItemStack> value() {
		return new Comparator<ItemStack>() {
			@Override
			public int compare(ItemStack arg0, ItemStack arg1) {
				return goldValues.getOrDefault(arg0 == null ? Material.AIR : arg0.getType(), 0.0) > goldValues
						.getOrDefault(arg1 == null ? Material.AIR : arg1.getType(), 0.0) ? -1 : 1;
			}
		};
	}
}
