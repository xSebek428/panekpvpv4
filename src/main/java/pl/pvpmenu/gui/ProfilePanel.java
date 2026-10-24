package pl.pvpmenu.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import pl.pvpmenu.config.PvpConfig;
import pl.pvpmenu.config.Theme;
import pl.pvpmenu.gfx.Gfx;
import pl.pvpmenu.module.PlaytimeTracker;

/** Zakładka "Profil": skin gracza (model 3D) + czas gry z modem. */
public final class ProfilePanel {
    private static String fmt(long s) {
        long h = s / 3600, m = (s % 3600) / 60;
        if (h > 0) return h + " h " + m + " min";
        if (m > 0) return m + " min";
        return s + " s";
    }

    public void render(DrawContext c, int mx, int my, int vx, int vy, int vw, int vh) {
        MinecraftClient mc = MinecraftClient.getInstance();
        PvpConfig cfg = PvpConfig.get();
        Theme t = cfg.theme;
        int accent = cfg.accentColor();
        TextRenderer tr = mc.textRenderer;

        int tileW = vw - 8;
        int cardH = Math.min(vh, 140);

        Gfx.fillRound(c, vx, vy, tileW, cardH, Gfx.radius(8), t.tile);
        c.fill(vx + 4, vy + 8, vx + 6, vy + cardH - 8, accent);

        // skin gracza (model 3D patrzący za kursorem)
        int boxX = vx + 14, boxW = 74;
        Gfx.fillRound(c, boxX, vy + 8, boxW, cardH - 16, Gfx.radius(6), t.sidebar);
        if (mc.player != null) {
            InventoryScreen.drawEntity(c, boxX, vy + 8, boxX + boxW, vy + cardH - 8, 40, 0.0625f, mx, my, mc.player);
        } else {
            c.drawText(tr, "Brak skina", boxX + 10, vy + cardH / 2 - 4, t.sub, true);
        }

        // statystyki
        int sx = boxX + boxW + 16;
        String name = mc.getSession().getUsername();
        c.drawText(tr, name, sx, vy + 14, accent, true);
        Gfx.hGradient(c, sx, vy + 26, tileW - (sx - vx) - 12, 1, accent, t.tile);

        c.drawText(tr, "Czas gry z modem", sx, vy + 38, t.sub, true);
        c.drawText(tr, fmt(cfg.playtimeSeconds), sx, vy + 50, t.text, true);

        c.drawText(tr, "Ta sesja", sx, vy + 72, t.sub, true);
        c.drawText(tr, fmt(PlaytimeTracker.sessionSeconds()), sx, vy + 84, t.text, true);
    }
}
