package xyz.msws.gui.utils;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.msws.gui.GUIPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Lang {
    GLOBAL_PREFIX("Global.%prefix%", "&9GUI> &7"),
    COMMANDS_HELP("Commands.Help", "&9GUI> &7List of avaialble commands:", "&7/gui reload - &cReloads the plugin and the GUIs", "&7/gui list - Lists all current GUIs", "&7/gui version - Gets the GUI version", "&7/gui [GUI] - Opens the specified GUI"),
    COMMANDS_RELOAD_SUCCESS("Commands.Reload.Success", "%prefix%&aSuccessfully reloaded."),
    COMMANDS_RELOAD_FAILURE("Commands.Reload.Failure", "%prefix%&cThere was an error trying to reload."),
    COMMANDS_RELOAD_NOTYET("Commands.Reload.NotYet", "%prefix%&cThis feature is not yet supported :(."),
    COMMANDS_LIST("Commands.List", "%prefix%List of valid GUIs: &e%guis%"),
    COMMANDS_VERSION("Commands.Version", "&9Version> &7This server is running GUI version &e%ver%"),
    COMMANDS_GUI_UNKNOWN("Commands.GUI.Unknown", "%prefix%&cUnknown GUI."),
    COMMANDS_GUI_NOTPLAYER("Commands.GUI.NotPlayer", "%prefix%&cYou must be a player to open a GUI."),
    FUNCTIONS_BUY_NOTENOUGH("Functions.Buy.NotEnough", "%prefix%&cYou have insufficient gold!"),
    FUNCTIONS_BUY_SUCCESS("Functions.Buy.Success", "%prefix%&aSuccessfully purchased."),
    FUNCTIONS_SELL_NOITEMS("Functions.Sell.NoItems", "%prefix%There aren't any sellable items!");

    private String path;
    private Object value;

    Lang(String path, Object... values) {
        this.path = path;

        if (values.length == 1) {
            this.value = values[0];
        } else {
            List<Object> objects = new ArrayList<Object>();
            Collections.addAll(objects, values);
            if (objects.get(objects.size() - 1) == null)
                objects.remove(objects.size() - 1);
            this.value = objects.toArray();
        }
    }

    public static void updateValues(FileConfiguration file) {
        for (Lang e : Lang.values()) {
            if (file.contains(e.getPath()))
                e.setValue(file.get(e.getPath()));
        }
    }

    public String getPath() {
        return path;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        if (value instanceof ArrayList) {
            List<String> list = (List<String>) value;
            for (int i = 0; i < list.size(); i++) {
                String v = list.get(i);
                if (GUIPlugin.getPlugin().getLang().isConfigurationSection("Global")) {
                    Map<String, Object> holders = GUIPlugin.getPlugin().getLang().getConfigurationSection("Global").getValues(false);
                    for (Map.Entry<String, Object> entry : holders.entrySet())
                        v = v.replace(entry.getKey(), (String) entry.getValue());
                }
                list.set(i, v);
            }
            this.value = list;
            return;
        }
        if (value instanceof String) {
            String v = (String) value;
            if (GUIPlugin.getPlugin().getLang().isConfigurationSection("Global")) {
                Map<String, Object> holders = GUIPlugin.getPlugin().getLang().getConfigurationSection("Global").getValues(false);
                for (Map.Entry<String, Object> entry : holders.entrySet())
                    v = v.replace(entry.getKey(), (String) entry.getValue());
            }
            this.value = v;
            return;
        }

        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(Class<T> cast) {
        if (cast.equals(Particle.class))
            return (T) Particle.valueOf(getValue(String.class));
        if (cast.equals(Sound.class))
            return (T) Sound.valueOf(getValue(String.class));
        if (cast.equals(Material.class))
            return (T) Material.valueOf(getValue(String.class));
        return cast.cast(this.value);
    }

    public void send(CommandSender sender) {
        MSG.tell(sender, this.getValue());
    }

    public void send(CommandSender sender, Replace... replaces) {
        Object value = getValue();
        if (!(value instanceof String) && !(value instanceof ArrayList))
            send(sender);

        String st;
        List<String> list;

        if (value instanceof String) {
            st = (String) value;
            for (Replace r : replaces)
                st = r.format(st);
            MSG.tell(sender, st);
        } else {
            list = (List<String>) value;
            for (Replace r : replaces)
                list = r.format(list);
            MSG.tell(sender, list);
        }

    }

    public static class Replace {
        private String key;
        private String value;

        public Replace(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public String format(String s) {
            return s.replace(key, value);
        }

        public List<String> format(List<String> string) {
            return string.stream().map(this::format).collect(Collectors.toList());
        }
    }

}
