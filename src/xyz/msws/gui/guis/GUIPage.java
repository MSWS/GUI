package xyz.msws.gui.guis;

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
import xyz.msws.gui.GUIPlugin;
import xyz.msws.gui.functions.items.ItemFunction;
import xyz.msws.gui.functions.items.ItemFunction.Type;
import xyz.msws.gui.functions.pages.PageFunction;
import xyz.msws.gui.utils.MSG;
import xyz.msws.gui.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class GUIPage implements Listener {

    private String id;

    private Map<String, Object> data;

    private Map<Integer, CItem> items;

    private List<PageFunction> functions = new ArrayList<>();

    private Inventory inv;

    public GUIPage(String id, Map<String, Object> data) {
        this.data = data;
        this.id = id;
        loadItems();
        loadFunctions();

        Bukkit.getPluginManager().registerEvents(this, GUIPlugin.getPlugin());
    }

    public GUIPage(String id, ConfigurationSection data) {
        this(id, data.getValues(true));
    }

    private void loadFunctions() {
        if (!data.containsKey("Functions"))
            return;
        List<String> functions = (List<String>) data.get("Functions");
        for (String f : functions)
            this.functions.add(GUIPlugin.getPlugin().getGUIManager().getPageFunction(f));
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

            CItem item = new CItem(itemValues);
            if (itemValues.containsKey("Functions")) {
                Map<String, Object> values = Utils.mapValues(itemValues.get("Functions"), true);
                for (Entry<String, Object> fv : values.entrySet()) {
                    Map<String, Object> fValues = Utils.mapValues(fv.getValue(), true);
                    if (fValues == null)
                        continue;
                    Object val = fValues.get("Value");
                    ItemFunction function;
                    if (val instanceof ArrayList<?>) {
                        ArrayList<?> ar = (ArrayList<?>) val;
                        String type = (String) fValues.get("Type");
                        function = Type.valueOf(type).createFunction(ar.toArray());
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
        for (PageFunction f : functions)
            f.onCreate(this, inv);
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
        GUIManager manager = GUIPlugin.getPlugin().getGUIManager();
        if (manager.getGUI(player) == null)
            return;
        if (!this.equals(manager.getGUI(player).getPlayerPage(player)))
            return;
        for (PageFunction func : functions)
            func.onClick(this, event);
        int slot = event.getRawSlot();
        if (data.containsKey("AllowClicking") && (boolean) data.get("AllowClicking") && !items.containsKey(slot))
            return;
        event.setCancelled(true);
        if (!items.containsKey(slot))
            return;
        CItem item = items.get(slot);
        item.getFunctions().forEach(f -> f.execute(event));
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        GUIManager manager = GUIPlugin.getPlugin().getGUIManager();
        if (manager.getGUI(player) == null)
            return;
        if (manager.getGUI(player).getPlayerPage(player) != this)
            return;
        if (player.hasMetadata("ignoreClose")) {
            player.removeMetadata("ignoreClose", GUIPlugin.getPlugin());
            return;
        }
        if (GUIPlugin.getPlugin().getGUIManager().getGUI(player) != null)
            GUIPlugin.getPlugin().getGUIManager().close(player,
                    GUIPlugin.getPlugin().getGUIManager().getGUI(player));
        for (PageFunction f : functions)
            f.onClose(this, event);
    }
}
