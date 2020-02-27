package xyz.msws.shop.utils;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class Eco {

	private static Map<Material, Double> goldValues;

	static {
		goldValues.put(Material.GOLD_INGOT, 1.0);
		goldValues.put(Material.GOLD_NUGGET, .1);
		goldValues.put(Material.GOLD_BLOCK, 9.0);
	}

	public static double getGold(HumanEntity player) {
		double total = 0;
		for (ItemStack item : player.getInventory()) {
			total += goldValues.getOrDefault(item.getType(), 0.0) * item.getAmount();
		}
		return total;
	}
}
