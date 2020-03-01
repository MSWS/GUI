package xyz.msws.gui.shops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.google.common.base.Preconditions;

import xyz.msws.gui.GUIPlugin;
import xyz.msws.gui.utils.Utils;

public class GUI implements Listener {
	@SuppressWarnings("unused")
	private Map<String, Object> data;
	private Map<UUID, GUIPage> playerPages;

	private List<GUIPage> pages;

	private String id;

	public GUI(String id, Map<String, Object> data) {
		Preconditions.checkArgument(verifyData(data), "Invalid data");

		this.id = id;
		this.data = data;
		pages = new ArrayList<GUIPage>();
		playerPages = new HashMap<>();

		Map<String, Object> pageData = new HashMap<String, Object>();
		Object o = data.get("Pages");
		pageData = Utils.mapValues(o, false);

		for (Entry<String, Object> page : pageData.entrySet()) {
			GUIPage p = new GUIPage(Utils.mapValues(page.getValue(), true));
			pages.add(p);
		}

		Bukkit.getPluginManager().registerEvents(this, GUIPlugin.getPlugin());
	}

	public GUI(String id, ConfigurationSection section) {
		this(id, section.getValues(true));
	}

	public String getId() {
		return id;
	}

	public void open(Player player) {
		open(player, pages.get(0).getID());
	}

	public void open(Player player, String page) {
		GUIPage p = getPage(page);
		playerPages.put(player.getUniqueId(), p);
		player.openInventory(p.create());
	}

	public void close(Player player) {
		playerPages.remove(player.getUniqueId());
	}

	public GUIPage getPlayerPage(Player player) {
		return playerPages.get(player.getUniqueId());
	}

	public GUIPage getPage(String name) {
		return pages.stream().filter(p -> p.getID().equals(name)).findFirst().orElse(null);
	}

	public boolean verifyData(Map<String, Object> data) {
		if (data == null)
			return false;
		if (!data.containsKey("Pages"))
			return false;
		return true;
	}

}
