package pl.pvpmenu.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import pl.pvpmenu.config.PvpConfig;
import pl.pvpmenu.gfx.Gfx;

public final class PvpHud {
    private PvpHud() {}

    public static void register() {
        HudRenderCallback.EVENT.register(PvpHud::render);
    }

    private static void render(DrawContext c, RenderTickCounter tick) {
        PvpConfig cfg = PvpConfig.get();
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!cfg.hudEnabled || mc.options.hudHidden || mc.player == null) return;

        TextRenderer tr = mc.textRenderer;
        String fps = mc.getCurrentFps() + " FPS";
        String xyz = String.format("XYZ: %.0f %.0f %.0f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
        int accent = cfg.accentColor();

        int x = 6, y = 6, w = Math.max(tr.getWidth(fps), tr.getWidth(xyz)) + 12, h = 32;
        Gfx.fillRound(c, x, y, w, h, Gfx.radius(5), 0xC0101216);
        Gfx.hGradient(c, x + 4, y, w - 8, 1, accent, 0xFF101216);
        c.drawText(tr, fps, x + 6, y + 6, accent, true);
        c.drawText(tr, xyz, x + 6, y + 18, 0xFFFFFFFF, true);
    }
}
