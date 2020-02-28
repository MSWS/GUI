package xyz.msws.shop.shops;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;

import xyz.msws.shop.utils.Eco;
import xyz.msws.shop.utils.MSG;

public class BuyFunction implements GUIFunction {

	private double cost = 0;

	private CItem item;

	public BuyFunction(Number value, CItem item) {
		cost = value.doubleValue();
		this.item = item;
	}

	@Override
	public void execute(InventoryClickEvent event) {
		HumanEntity player = event.getWhoClicked();
		player.getInventory().addItem(item.build());

		if (Eco.getGold(player) < this.cost) {
			MSG.tell(player, "Shop", "You have insufficient gold! (" + this.cost + ")");
			return;
		}

		MSG.tell(player, "Shop", "You purchased this thingy mabob");
	}
}
