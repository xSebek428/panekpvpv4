package pl.pvpmenu.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import pl.pvpmenu.config.PvpConfig;

public final class UiSound {
    private UiSound() {}

    public static void click() {
        if (!PvpConfig.get().menuSounds) return;
        MinecraftClient.getInstance().getSoundManager()
                .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }
}
