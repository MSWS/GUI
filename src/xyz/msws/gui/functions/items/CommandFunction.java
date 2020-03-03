package xyz.msws.gui.functions.items;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.msws.gui.utils.MSG;

public class CommandFunction implements ItemFunction {

    private CommandType type;
    private String[] commands;

    public CommandFunction(CommandType type, String... commands) {
        this.type = type;
        this.commands = commands;
    }

    @Override
    public void execute(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        for (String c : commands)
            runCommand(player, type, c);
    }

    private void runCommand(Player player, CommandType type, String cmd) {
        cmd = cmd.replace("%name%", player.getName())
                .replace("%world%", player.getWorld().getName())
                .replace("%uuid%", player.getUniqueId().toString());
        cmd = MSG.papi(player, cmd);

        // TODO PAPI
        switch (type) {
            case PLAYER:
                player.performCommand(cmd);
                break;
            case OP:
                boolean isOp = player.isOp();
                player.setOp(true);
                player.performCommand(cmd);
                player.setOp(isOp);
                break;
            case CONSOLE:
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                break;
        }
    }

    public enum CommandType {
        PLAYER, CONSOLE, OP
    }
}
