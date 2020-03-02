package xyz.msws.gui.guis;

import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SoundFunction implements ItemFunction {

    private Sound sound;

    private float volume, pitch;

    public SoundFunction(String sound, Number volume, Number pitch) {
        this.sound = Sound.valueOf(sound);
        this.volume = volume.floatValue();
        this.pitch = pitch.floatValue();
    }

    @Override
    public void execute(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        player.getWorld().playSound(player.getLocation(), sound, volume, pitch);
    }

}
