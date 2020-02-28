package xyz.msws.shop.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class Eco {

	private static Map<Material, Double> goldValues = new HashMap<Material, Double>();

	static {
		goldValues.put(Material.GOLD_INGOT, 1.0);
		goldValues.put(Material.GOLD_NUGGET, .1);
		goldValues.put(Material.GOLD_BLOCK, 9.0);
	}

	public static double getGold(HumanEntity player) {
		double total = 0;
		MSG.log("values: " + goldValues);
		for (ItemStack item : player.getInventory()) {
			if (item == null || item.getType() == Material.AIR)
				continue;
			total += goldValues.getOrDefault(item.getType(), 0.0) * item.getAmount();
		}
		return total;
	}
}
