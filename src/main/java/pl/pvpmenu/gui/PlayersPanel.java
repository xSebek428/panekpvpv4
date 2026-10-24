package pl.pvpmenu.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import pl.pvpmenu.config.PvpConfig;
import pl.pvpmenu.config.Theme;
import pl.pvpmenu.gfx.Gfx;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Zakładka "Gracze": liczba graczy na serwerze + lista z głowami i pingiem. */
public final class PlayersPanel {
    private static final int CARD_H = 34, ROW_H = 22, GAP = 3;
    private int scroll, listH, listTop, vx, vw, vBottom;

    public void render(DrawContext c, int mx, int my, int vx, int vy, int vw, int vh) {
        MinecraftClient mc = MinecraftClient.getInstance();
        PvpConfig cfg = PvpConfig.get();
        Theme t = cfg.theme;
        int accent = cfg.accentColor();
        TextRenderer tr = mc.textRenderer;

        ClientPlayNetworkHandler nh = mc.getNetworkHandler();
        List<PlayerListEntry> list = nh == null ? new ArrayList<>() : new ArrayList<>(nh.getPlayerList());
        list.sort(Comparator.comparing(e -> e.getProfile().getName(), String.CASE_INSENSITIVE_ORDER));

        this.vx = vx;
        this.vw = vw;
        this.vBottom = vy + vh;
        this.listTop = vy + CARD_H + 8;
        int viewH = vBottom - listTop;
        listH = list.size() * (ROW_H + GAP);
        scroll = Math.max(0, Math.min(scroll, Math.max(0, listH - viewH)));
        int tileW = vw - 8;

        // karta z podsumowaniem
        Gfx.fillRound(c, vx, vy, tileW, CARD_H, Gfx.radius(8), t.tile);
        c.fill(vx + 4, vy + 6, vx + 6, vy + CARD_H - 6, accent);
        c.drawText(tr, "Gracze online", vx + 14, vy + 6, t.sub, true);
        c.drawText(tr, String.valueOf(list.size()), vx + 14, vy + 19, accent, true);

        ServerInfo si = mc.getCurrentServerEntry();
        String where = si != null ? si.address : (mc.isInSingleplayer() ? "Świat jednoosobowy" : "Brak połączenia");
        c.drawText(tr, where, vx + tileW - 10 - tr.getWidth(where), vy + 13, t.text, true);

        // lista
        c.enableScissor(vx, listTop, vx + vw, vBottom);
        if (list.isEmpty()) {
            c.drawText(tr, "Brak danych o graczach.", vx + 4, listTop + 6, t.sub, true);
        }
        int y = listTop - scroll;
        for (PlayerListEntry e : list) {
            if (y + ROW_H >= listTop && y <= vBottom) {
                boolean me = mc.player != null && e.getProfile().getId().equals(mc.player.getUuid());
                boolean hov = mx >= vx && mx < vx + tileW && my >= y && my < y + ROW_H && my >= listTop && my < vBottom;
                Gfx.fillRound(c, vx, y, tileW, ROW_H, Gfx.radius(6), hov ? t.tileHover : t.tile);
                if (me) c.fill(vx + 3, y + 5, vx + 5, y + ROW_H - 5, accent);

                PlayerSkinDrawer.draw(c, e.getSkinTextures(), vx + 10, y + 3, 16);
                c.drawText(tr, e.getProfile().getName(), vx + 32, y + 7, me ? accent : t.text, true);

                int ping = e.getLatency();
                int pc = ping <= 0 ? t.sub : ping < 80 ? 0xFF4ADE80 : ping < 150 ? 0xFFFACC15 : 0xFFF87171;
                String ps = ping <= 0 ? "-- ms" : ping + " ms";
                c.drawText(tr, ps, vx + tileW - 10 - tr.getWidth(ps), y + 7, pc, true);
            }
            y += ROW_H + GAP;
        }
        c.disableScissor();
    }

    public boolean mouseScrolled(double mx, double my, double vAmount) {
        if (mx < vx || mx >= vx + vw || my < listTop || my >= vBottom) return false;
        scroll = Math.max(0, Math.min(scroll - (int) (vAmount * 14), Math.max(0, listH - (vBottom - listTop))));
        return true;
    }
}
