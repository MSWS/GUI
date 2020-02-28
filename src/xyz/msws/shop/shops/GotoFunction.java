package xyz.msws.shop.shops;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import xyz.msws.shop.ShopPlugin;
import xyz.msws.shop.utils.MSG;

public class GotoFunction implements GUIFunction {

	private String page;

	public GotoFunction(String page) {
		this.page = page;
	}

	@Override
	public void execute(InventoryClickEvent event) {
		MSG.tell(event.getWhoClicked(), "Shop", "You clicked a button");
		ShopPlugin.getPlugin().getShop().open((Player) event.getWhoClicked(), page);
	}

}
