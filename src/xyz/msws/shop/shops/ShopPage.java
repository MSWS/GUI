package xyz.msws.shop.shops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import xyz.msws.shop.ShopPlugin;
import xyz.msws.shop.shops.GUIFunction.Type;
import xyz.msws.shop.utils.MSG;
import xyz.msws.shop.utils.Utils;

public class ShopPage implements Listener {

	private String id;

	private Map<String, Object> data;

	private Map<Integer, CItem> items;

	private Inventory inv;

	public ShopPage(Map<String, Object> data) {
		this.data = data;
		this.id = (String) data.get("Name");
		loadItems();

		Bukkit.getPluginManager().registerEvents(this, ShopPlugin.getPlugin());
	}

	public ShopPage(ConfigurationSection data) {
		this(data.getValues(true));
	}

	private void loadItems() {
		items = new HashMap<Integer, CItem>();
		if (data == null) {
			MSG.log("ShopPage created without any data");
			return;
		}
		for (Entry<String, Object> key : data.entrySet()) {
			Map<String, Object> itemValues = Utils.mapValues(key.getValue(), false);

			if (itemValues == null)
				continue;
			if (!itemValues.containsKey("Icon") || !itemValues.containsKey("Slot"))
				continue;

			MSG.log("Item: " + itemValues.get("Icon"));
			CItem item = new CItem(itemValues);
			if (itemValues.containsKey("Functions")) {
				Map<String, Object> values = Utils.mapValues(itemValues.get("Functions"), true);
				for (Entry<String, Object> fv : values.entrySet()) {
					Map<String, Object> fValues = Utils.mapValues(fv.getValue(), true);
					if (fValues == null)
						continue;
					Object val = fValues.get("Value");
					GUIFunction function;
					if (val instanceof ArrayList<?>) {
						function = Type.valueOf((String) fValues.get("Type"))
								.createFunction(((ArrayList<?>) val).toArray());
					} else {
						function = Type.valueOf((String) fValues.get("Type")).createFunction(val);
					}

					item.addFunction(function);
				}
			}
			items.put((Integer) itemValues.get("Slot"), item);
		}
	}

	public Inventory create() {
		this.inv = Bukkit.createInventory(null, (int) data.get("Size"), MSG.color((String) data.get("Name")));
		for (Entry<Integer, CItem> entry : items.entrySet()) {
			inv.setItem(entry.getKey(), entry.getValue().build());
		}
		if (data.containsKey("BACKGROUND")) {
			CItem bg = new CItem(Utils.mapValues(data.get("BACKGROUND"), false));
			for (int i = 0; i < inv.getSize(); i++) {
				ItemStack item = inv.getItem(i);
				if (item != null && item.getType() != Material.AIR)
					continue;
				inv.setItem(i, bg.build());
			}
		}
		return inv;
	}

	public String getID() {
		return id;
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;
		Player player = (Player) event.getWhoClicked();
		if (!ShopPlugin.getPlugin().getShop().getPlayerPage(player).equals(this))
			return;
		int slot = event.getRawSlot();
		event.setCancelled(true);
		if (!items.containsKey(slot))
			return;
		CItem item = items.get(slot);
		item.getFunctions().forEach(f -> f.execute(event));
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {

	}
}
