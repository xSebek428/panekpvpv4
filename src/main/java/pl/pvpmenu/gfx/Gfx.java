package pl.pvpmenu.gfx;

import net.minecraft.client.gui.DrawContext;
import pl.pvpmenu.config.PvpConfig;

/** Własne prymitywy renderowania: zaokrąglone prostokąty, gradienty, interpolacja kolorów. */
public final class Gfx {
    private Gfx() {}

    /** Promień zaokrąglenia: 0, gdy opcja "Zaokrąglone ramki" jest wyłączona. */
    public static int radius(int base) {
        return PvpConfig.get().roundedCorners ? base : 0;
    }

    /** Zaokrąglony prostokąt rysowany liniami poziomymi (tanie dla małych promieni). */
    public static void fillRound(DrawContext c, int x, int y, int w, int h, int r, int color) {
        r = Math.min(r, Math.min(w, h) / 2);
        if (r <= 0) {
            c.fill(x, y, x + w, y + h, color);
            return;
        }
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int inset = r - (int) Math.round(Math.sqrt(r * r - dy * dy));
            c.fill(x + inset, y + i, x + w - inset, y + i + 1, color);
            c.fill(x + inset, y + h - i - 1, x + w - inset, y + h - i, color);
        }
        c.fill(x, y + r, x + w, y + h - r, color);
    }

    /** Płynny gradient poziomy (fillGradient z Minecrafta jest tylko pionowy). */
    public static void hGradient(DrawContext c, int x, int y, int w, int h, int c1, int c2) {
        for (int i = 0; i < w; i++) {
            c.fill(x + i, y, x + i + 1, y + h, lerp(c1, c2, w <= 1 ? 0 : i / (float) (w - 1)));
        }
    }

    public static int lerp(int a, int b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int) (((a >> 16) & 255) * (1 - t) + ((b >> 16) & 255) * t);
        int g = (int) (((a >> 8) & 255) * (1 - t) + ((b >> 8) & 255) * t);
        int bl = (int) ((a & 255) * (1 - t) + (b & 255) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | bl;
    }

    /** Animacja: przybliża wartość do celu co klatkę (płynne przejścia). */
    public static float approach(float cur, float target) {
        return Math.abs(target - cur) < 0.01f ? target : cur + (target - cur) * 0.25f;
    }
}
