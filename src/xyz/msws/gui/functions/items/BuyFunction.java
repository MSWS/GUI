package xyz.msws.gui.functions.items;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.msws.gui.guis.CItem;
import xyz.msws.gui.utils.Eco;
import xyz.msws.gui.utils.Lang;

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
			Lang.FUNCTIONS_BUY_NOTENOUGH.send(player, new Lang.Replace("%current%", (int) gold + ""), new Lang.Replace("%required%", (int) cost + ""));
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, .5f, 1f);
			return;
		}

		Lang.FUNCTIONS_BUY_SUCCESS.send(player);
		player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 2, 1);
		Eco.deduct(player.getInventory(), cost);
		for (CItem item : items)
			player.getInventory().addItem(item.build());
	}
}
