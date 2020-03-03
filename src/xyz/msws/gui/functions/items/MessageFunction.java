package xyz.msws.gui.functions.items;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.msws.gui.utils.MSG;

import java.util.Arrays;
import java.util.List;

public class MessageFunction implements ItemFunction {

    private List<String> messages;

    public MessageFunction(String... messages) {
        this.messages = Arrays.asList(messages);
    }

    @Override
    public void execute(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        MSG.tell(player, messages);
    }
}
