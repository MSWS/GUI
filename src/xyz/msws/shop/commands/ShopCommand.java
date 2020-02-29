package xyz.msws.shop.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.msws.shop.ShopPlugin;
import xyz.msws.shop.shops.CItem;
import xyz.msws.shop.utils.MSG;

public class ShopCommand implements CommandExecutor, TabCompleter {

	private ShopPlugin plugin;

	public ShopCommand(ShopPlugin plugin) {
		PluginCommand command = plugin.getCommand("shop");
		command.setExecutor(this);
		command.setTabCompleter(this);
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = checkPlayer(sender, false);
		if (args.length == 0) {
			if (player != null) {
				MSG.log("Shop Manager: " + plugin.getShopManager());
				MSG.log("Shop: " + plugin.getShopManager().getShop("DefaultShop"));

				plugin.getShopManager().open(player, "DefaultShop");
				return true;
			} else {
				MSG.tell(sender, "Shop", "You must be a player to open the shop.");
				return true;
			}
		}

		switch (args[0].toLowerCase()) {
		case "getitem":
			if (player == null) {
				checkPlayer(sender, true);
				return true;
			}
			ItemStack item = player.getEquipment().getItemInMainHand();
			MSG.tell(player, "Shop", new CItem(item).toString());
			break;
		default:
			MSG.tell(sender, "Shop", "Unknown arguments.");
		}
		return true;
	}

	private Player checkPlayer(CommandSender sender, boolean verbose) {
		if (sender instanceof Player)
			return (Player) sender;
		if (!verbose)
			return null;
		MSG.tell(sender, "Shop", "You must be a player to run this command.");
		return null;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> result = new ArrayList<String>();
		if (args.length == 1)
			for (String res : new String[] { "getitem" }) {
				if (!sender.hasPermission("shop.command." + res))
					continue;
				if (res.toLowerCase().startsWith(args[0].toLowerCase()))
					result.add(res);
			}
		return result;
	}

}
