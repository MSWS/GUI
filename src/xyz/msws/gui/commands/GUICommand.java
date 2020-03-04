package xyz.msws.gui.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import xyz.msws.gui.GUIPlugin;
import xyz.msws.gui.guis.GUI;
import xyz.msws.gui.utils.Lang;
import xyz.msws.gui.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GUICommand implements CommandExecutor, TabCompleter {

    private GUIPlugin plugin;
    private PluginCommand cmd;

    public GUICommand(GUIPlugin plugin) {
        this.plugin = plugin;
        cmd = plugin.getCommand("gui");
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
        cmd.setPermission("gui.command.gui");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Utils.has(sender, cmd.getPermission(), true))
            return true;
        if (args.length == 0)
            return false;

        Player player = null;
        if (sender instanceof Player)
            player = (Player) sender;
        Map<String, GUI> guis = plugin.getGUIManager().getGUIs();

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!Utils.has(sender, cmd.getPermission() + ".reload", true))
                    return true;
                Lang.COMMANDS_RELOAD_NOTYET.send(sender);
                break;
            case "list":
                if (!Utils.has(sender, cmd.getPermission() + ".list", true))
                    return true;
                Lang.COMMANDS_LIST.send(sender, new Lang.Replace("%guis%", String.join("&7, &e", guis.keySet())));
                break;
            case "help":
                if (!Utils.has(sender, cmd.getPermission() + ".help", true))
                    return true;
                Lang.COMMANDS_HELP.send(sender);
                break;
            case "version":
                if (!Utils.has(sender, cmd.getPermission() + ".version", true))
                    return true;
                Lang.COMMANDS_VERSION.send(sender, new Lang.Replace("%ver%", plugin.getDescription().getVersion()));
                break;
/*            case "enum":
                ConfigurationSection lang = plugin.getLang();
                Map<String, Object> values = lang.getValues(true);
                StringBuilder message = new StringBuilder("\n");
                for (Map.Entry<String, Object> entry : values.entrySet()) {
                    if (lang.isConfigurationSection(entry.getKey()))
                        continue;
                    if (entry.getValue() instanceof MemorySection)
                        continue;

                    String key = entry.getKey().toUpperCase().replace(".", "_");

                    if (entry.getValue() instanceof String) {
                        message.append(key).append("(\"").append(entry.getKey()).append("\", \"").append(entry.getValue()).append("\"),\n");
                    } else if (entry.getValue() instanceof ArrayList) {
                        List<String> list = (List<String>) entry.getValue();
                        message.append(key).append("(\"").append(entry.getKey()).append("\", \"");
                        for (String e : list)
                            message.append(e).append("\", \"");
                        message = new StringBuilder(message.substring(0, message.length() - 3));
                        message.append("),\n");
                    } else {
                        message.append(key).append("(\"").append(entry.getKey()).append("\", ").append(entry.getValue()).append("),\n");
                    }
                }

                String msg1 = message.toString().replace("\", [", "\", \"");
                msg1 = msg1.replace("]),", "\", null),");

                player.sendMessage(msg1);
                break;*/
            default:
                if (!Utils.has(sender, "gui.open." + args[0], true))
                    return true;
                if (!guis.containsKey(args[0])) {
                    Lang.COMMANDS_GUI_UNKNOWN.send(sender);
                    return true;
                }
                if (player == null) {
                    Lang.COMMANDS_GUI_NOTPLAYER.send(sender);
                    return true;
                }
                plugin.getGUIManager().open(player, guis.get(args[0]));
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (String res : new String[]{"help", "list", "reload", "version"}) {
                if (res.toLowerCase().startsWith(args[0].toLowerCase()))
                    result.add(res);
            }

            Set<String> names = plugin.getGUIManager().getGUIs().keySet();
            for (String res : names) {
                if (!sender.hasPermission("gui.open." + res))
                    continue;
                if (res.toLowerCase().startsWith(args[0].toLowerCase()))
                    result.add(res);
            }
        }
        return result;
    }
}
