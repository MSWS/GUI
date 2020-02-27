package xyz.msws.shop.shops;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import xyz.msws.shop.utils.MSG;

public class BuyFunction implements GUIFunction {

	private double cost = 0;

	public BuyFunction(double value) {
		cost = ((Number) value).doubleValue();
	}

	@Override
	public void execute(InventoryClickEvent event) {
		HumanEntity player = event.getWhoClicked();
		ItemStack item = event.getCursor();
		MSG.announce(player.getName() + " clicked " + item.getType());
	}

}
