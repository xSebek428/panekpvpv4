package pl.pvpmenu.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import pl.pvpmenu.config.PvpConfig;

public final class PvpModuleManager {
    private static final boolean[] wasDown = new boolean[PvpModule.values().length];

    private PvpModuleManager() {}

    /** Wywoływane co tick: wykrywa wciśnięcie przypisanych klawiszy (tylko w grze, nie w GUI). */
    public static void tick(MinecraftClient client) {
        if (client.currentScreen != null || client.player == null) return;
        PvpConfig cfg = PvpConfig.get();
        long window = client.getWindow().getHandle();

        for (PvpModule m : PvpModule.values()) {
            int key = cfg.getBind(m);
            boolean down = key >= 0 && InputUtil.isKeyPressed(window, key);
            if (down && !wasDown[m.ordinal()]) {
                boolean now = !m.isEnabled(cfg);
                setEnabled(m, now, client);
                client.player.sendMessage(Text.literal(m.label + ": ")
                        .append(Text.literal(now ? "ON" : "OFF").formatted(now ? Formatting.GREEN : Formatting.RED)), true);
            }
            wasDown[m.ordinal()] = down;
        }
    }

    public static void setEnabled(PvpModule m, boolean value, MinecraftClient client) {
        PvpConfig cfg = PvpConfig.get();
        m.setEnabled(cfg, value);
        if (m == PvpModule.NO_SHADOWS) {
            // wyłączenie cieni encji = mniej draw-calli
            client.options.getEntityShadows().setValue(!value);
        }
        PvpConfig.save();
    }

    public static void applyOnStart(MinecraftClient client) {
        if (PvpConfig.get().noShadows) client.options.getEntityShadows().setValue(false);
    }
}
