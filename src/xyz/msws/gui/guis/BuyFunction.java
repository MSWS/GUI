package xyz.msws.gui.guis;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.msws.gui.utils.Eco;
import xyz.msws.gui.utils.MSG;

public class BuyFunction implements ItemFunction {

	private double cost;

	private CItem[] items;

	public BuyFunction(Number value, CItem... items) {
		cost = value.doubleValue();

		double v = 0;
		if (cost == -1) {
			for (CItem i : items)
				v += Eco.getBuyPrice(i.build());
			cost = v;
		}

		this.items = items;
	}

	@Override
	public void execute(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		double gold = Eco.getGold(player.getInventory());

		if (gold < this.cost) {
			MSG.tell(player, "Shop", MSG.ERROR + "You have insufficient gold! " + MSG.FORMAT_SEPARATOR + "(" + MSG.MONEY
					+ (int) gold + MSG.FORMAT_SEPARATOR + "/&6" + +(int) this.cost + MSG.FORMAT_SEPARATOR + ")");
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, .5f, 1f);
			return;
		}

		MSG.tell(player, "Shop", MSG.SUCCESS + "Successfully purchased");
		player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 2, 1);
		Eco.deduct(player.getInventory(), cost);
		for (CItem item : items)
			player.getInventory().addItem(item.build());
	}
}
