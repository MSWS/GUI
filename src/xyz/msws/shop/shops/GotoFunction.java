package xyz.msws.shop.shops;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.shop.ShopPlugin;

public class GotoFunction implements GUIFunction {

	private String page;

	public GotoFunction(String page) {
		this.page = page;
	}

	@Override
	public void execute(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		player.setMetadata("ignoreClose", new FixedMetadataValue(ShopPlugin.getPlugin(), 2));
		new BukkitRunnable() {

			@Override
			public void run() {
				ShopPlugin.getPlugin().getShop().open((Player) event.getWhoClicked(), page);

			}
		}.runTaskLater(ShopPlugin.getPlugin(), 1);
	}

}
