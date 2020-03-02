package xyz.msws.gui.commands;

import com.sun.istack.internal.NotNull;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.msws.gui.GUIPlugin;
import xyz.msws.gui.guis.CItem;
import xyz.msws.gui.utils.MSG;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ShopCommand implements CommandExecutor, TabCompleter {

    private GUIPlugin plugin;

    public ShopCommand(GUIPlugin plugin) {
        PluginCommand command = plugin.getCommand("shop");
        if (command == null)
            return;
        command.setExecutor(this);
        command.setTabCompleter(this);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = checkPlayer(sender, false);
        if (args.length == 0) {
            if (player != null) {
                plugin.getGUIManager().open(player, "DefaultShop");
            } else {
                MSG.tell(sender, "Shop", "You must be a player to open the shop.");
            }
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "getitem":
                if (player == null) {
                    checkPlayer(sender, true);
                    return true;
                }
                if (player.getEquipment() == null) {
                    MSG.tell(player, "Shop", "You do not have an item in your hand.");
                    return true;
                }
                ItemStack item = player.getEquipment().getItemInMainHand();
                if (item.getType() == Material.AIR) {
                    MSG.tell(player, "Shop", "You do not have an item in your hand.");
                    return true;
                }
                MSG.tell(player, "Shop", new CItem(item).toString());
                break;
            default:
                MSG.tell(sender, "Shop", "Unknown arguments.");
        }
        return true;
    }

    @Nullable
    private Player checkPlayer(CommandSender sender, boolean verbose) {
        if (sender instanceof Player)
            return (Player) sender;
        if (!verbose)
            return null;
        MSG.tell(sender, "Shop", "You must be a player to run this command.");
        return null;
    }

    @Override
    @NotNull
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1)
            for (String res : new String[]{"getitem"}) {
                if (!sender.hasPermission("shop.command." + res))
                    continue;
                if (res.toLowerCase().startsWith(args[0].toLowerCase()))
                    result.add(res);
            }
        return result;
    }

}
