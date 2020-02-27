package xyz.msws.shop.shops;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;

import xyz.msws.shop.shops.GUIFunction.Type;

public class ShopPage {

	private Map<String, Object> data;

	private Map<Integer, CItem> items;

	public ShopPage(Map<String, Object> data) {
		this.data = data;
		loadItems();
	}

	public ShopPage(ConfigurationSection data) {
		this.data = data.getValues(true);
		loadItems();
	}

	@SuppressWarnings("unchecked")
	private void loadItems() {
		for (Entry<String, Object> key : data.entrySet()) {
			Map<String, Object> keyMap = (Map<String, Object>) key;

			CItem item = new CItem(keyMap);
			if (keyMap.containsKey("Function")) {
				Map<String, Object> values = (Map<String, Object>) keyMap.get("Function");
				GUIFunction function = Type.valueOf((String) values.get("Type")).createFunction(values.get("Value"));
				item.setFunction(function);
			}
			items.put((Integer) keyMap.get("Slot"), item);
		}
	}
}
