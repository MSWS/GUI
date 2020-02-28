package xyz.msws.shop.shops;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import xyz.msws.shop.utils.Eco;
import xyz.msws.shop.utils.MSG;

public class BuyFunction implements GUIFunction {

	private double cost = 0;

	private CItem[] items;

	public BuyFunction(Number value, CItem... items) {
		cost = value.doubleValue();
		this.items = items;
	}

	@Override
	public void execute(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		double gold = Eco.getGold(player.getInventory());

		if (gold < this.cost) {
			MSG.tell(player, "Shop", "You have insufficient gold! (" + (int) gold + "/" + (int) this.cost + ")");
			return;
		}

		MSG.tell(player, "Shop", MSG.SUCCESS + "Successfully purchased");
		player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 2, 1);
		Eco.deduct(player.getInventory(), cost);
		for (CItem item : items)
			player.getInventory().addItem(item.build());
	}
}
