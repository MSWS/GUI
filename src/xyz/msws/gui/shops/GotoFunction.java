package xyz.msws.gui.shops;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.gui.GUIPlugin;

public class GotoFunction implements GUIFunction {

	private String page;

	public GotoFunction(String page) {
		this.page = page;
	}

	@Override
	public void execute(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		player.setMetadata("ignoreClose", new FixedMetadataValue(GUIPlugin.getPlugin(), 2));
		new BukkitRunnable() {

			@Override
			public void run() {
				GUIPlugin.getPlugin().getShopManager().open(player,
						GUIPlugin.getPlugin().getShopManager().getShop(player), page);
			}
		}.runTaskLater(GUIPlugin.getPlugin(), 1);
	}

}
