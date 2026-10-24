package pl.pvpmenu.module;

import net.minecraft.client.MinecraftClient;
import pl.pvpmenu.config.PvpConfig;

/** Liczy czas gry z modem (w świecie/na serwerze), zapisuje do configu co minutę. */
public final class PlaytimeTracker {
    private static int ticks;
    private static long sessionSeconds;

    private PlaytimeTracker() {}

    public static void tick(MinecraftClient client) {
        if (client.player == null) return;
        if (++ticks < 20) return;
        ticks = 0;
        sessionSeconds++;
        PvpConfig cfg = PvpConfig.get();
        cfg.playtimeSeconds++;
        if (cfg.playtimeSeconds % 60 == 0) PvpConfig.save();
    }

    public static long sessionSeconds() {
        return sessionSeconds;
    }
}
