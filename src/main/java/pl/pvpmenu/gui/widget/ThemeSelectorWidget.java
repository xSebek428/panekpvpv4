package pl.pvpmenu.gui.widget;

import net.minecraft.client.gui.DrawContext;
import pl.pvpmenu.config.PvpConfig;
import pl.pvpmenu.config.Theme;
import pl.pvpmenu.gfx.Gfx;
import pl.pvpmenu.gui.UiSound;

/** Trzy sąsiadujące przyciski: Ciemny / Jasny / Podstawowy. */
public final class ThemeSelectorWidget extends UiWidget {
    private static final int BTN_W = 54, GAP = 2;
    private final float[] anim = new float[Theme.values().length];

    public ThemeSelectorWidget() {
        this.w = Theme.values().length * BTN_W + (Theme.values().length - 1) * GAP;
        this.h = 18;
    }

    @Override
    public void render(DrawContext c, int mx, int my) {
        PvpConfig cfg = PvpConfig.get();
        int accent = cfg.accentColor();
        Theme[] themes = Theme.values();
        for (int i = 0; i < themes.length; i++) {
            int bx = x + i * (BTN_W + GAP);
            boolean hov = mx >= bx && mx < bx + BTN_W && my >= y && my < y + h;
            float target = cfg.theme == themes[i] ? 1f : hov ? 0.35f : 0f;
            anim[i] = Gfx.approach(anim[i], target);

            int bg = Gfx.lerp(cfg.theme.sidebar, accent, anim[i]);
            Gfx.fillRound(c, bx, y, BTN_W, h, Gfx.radius(4), bg);

            String label = themes[i].label;
            int color = cfg.theme == themes[i] ? 0xFFFFFFFF : cfg.theme.text;
            c.drawText(font(), label, bx + (BTN_W - font().getWidth(label)) / 2, y + 5, color, true);
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button != 0) return false;
        Theme[] themes = Theme.values();
        for (int i = 0; i < themes.length; i++) {
            int bx = x + i * (BTN_W + GAP);
            if (mx >= bx && mx < bx + BTN_W && my >= y && my < y + h) {
                PvpConfig.get().theme = themes[i];
                UiSound.click();
                PvpConfig.save();
                return true;
            }
        }
        return false;
    }
}
