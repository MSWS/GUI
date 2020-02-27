package xyz.msws.shop.shops;

import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

public class Shop {
	private Map<String, Object> data;

	public Shop(Map<String, Object> data) {
		this.data = data;
		Preconditions.checkArgument(verifyData(data), "Invalid data");
	}

	public Shop(ConfigurationSection section) {
		this.data = section.getValues(true);
		Preconditions.checkArgument(verifyData(data), "Invalid data");
	}

	public boolean verifyData(Map<String, Object> data) {
		if (data == null)
			return false;
		if (!data.containsKey("Name"))
			return false;
		if (!data.containsKey("Pages"))
			return false;
		return true;
	}

}
