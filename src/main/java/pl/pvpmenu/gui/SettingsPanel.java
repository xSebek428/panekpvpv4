package pl.pvpmenu.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import pl.pvpmenu.config.PvpConfig;
import pl.pvpmenu.config.Theme;
import pl.pvpmenu.gfx.Gfx;
import pl.pvpmenu.gui.widget.*;
import pl.pvpmenu.module.PvpModule;

import java.util.ArrayList;
import java.util.List;

/** Zawartość zakładki "Ustawienia": przewijana lista kafelków. */
public final class SettingsPanel {
    private static final class Row {
        final String label;
        final UiWidget widget; // null = nagłówek sekcji
        int y, h;
        Row(String label, UiWidget widget) { this.label = label; this.widget = widget; }
    }

    private final List<Row> rows = new ArrayList<>();
    private int scroll, contentH, vx, vy, vw, vh;

    public SettingsPanel() {
        rows.add(new Row("Kolor GUI", new HexColorWidget()));
        rows.add(new Row("Zaokrąglone ramki", new ToggleWidget(() -> PvpConfig.get().roundedCorners, v -> PvpConfig.get().roundedCorners = v)));
        rows.add(new Row("Dźwięki menu", new ToggleWidget(() -> PvpConfig.get().menuSounds, v -> PvpConfig.get().menuSounds = v)));
        rows.add(new Row("Cień tekstu u menu i HUD", new ToggleWidget(() -> PvpConfig.get().textShadow, v -> PvpConfig.get().textShadow = v)));
        rows.add(new Row("Motyw", new ThemeSelectorWidget()));
        rows.add(new Row("Bindy modułów (ESC = wyczyść)", null));
        for (PvpModule m : PvpModule.values()) rows.add(new Row(m.label, new KeybindWidget(m)));
    }

    private void layout(int vx, int vy, int vw, int vh) {
        this.vx = vx; this.vy = vy; this.vw = vw; this.vh = vh;
        int tileW = vw - 8;
        int y = vy - scroll;
        for (Row r : rows) {
            r.h = r.widget == null ? 20 : Math.max(26, r.widget.h + 10);
            r.y = y;
            if (r.widget != null) r.widget.setPos(vx + tileW - 8 - r.widget.w, y + (r.h - r.widget.h) / 2);
            y += r.h + 6;
        }
        contentH = y + scroll - vy - 6;
        scroll = Math.max(0, Math.min(scroll, Math.max(0, contentH - vh)));
    }

    private boolean inView(double mx, double my) {
        return mx >= vx && mx < vx + vw && my >= vy && my < vy + vh;
    }

    public void render(DrawContext c, int mx, int my, int vx, int vy, int vw, int vh) {
        layout(vx, vy, vw, vh);
        PvpConfig cfg = PvpConfig.get();
        Theme t = cfg.theme;
        int accent = cfg.accentColor();
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        int tileW = vw - 8;

        c.enableScissor(vx, vy, vx + vw, vy + vh);
        for (Row r : rows) {
            if (r.y + r.h < vy || r.y > vy + vh) continue;
            if (r.widget == null) {
                c.drawText(tr, r.label, vx + 2, r.y + 6, accent, true);
                Gfx.hGradient(c, vx, r.y + r.h - 2, tileW, 1, accent, t.bg);
                continue;
            }
            boolean hov = inView(mx, my) && mx < vx + tileW && my >= r.y && my < r.y + r.h;
            Gfx.fillRound(c, vx, r.y, tileW, r.h, Gfx.radius(8), hov ? t.tileHover : t.tile);
            c.fill(vx + 4, r.y + 6, vx + 6, r.y + r.h - 6, accent);
            c.drawText(tr, r.label, vx + 14, r.y + (r.h - 8) / 2, t.text, true);
            r.widget.render(c, mx, my);
        }
        if (contentH > vh) {
            int bh = Math.max(20, vh * vh / contentH);
            int by = vy + (int) ((vh - bh) * (scroll / (float) (contentH - vh)));
            c.fill(vx + vw - 3, by, vx + vw, by + bh, t.toggleOff);
        }
        c.disableScissor();
    }

    public boolean mouseClicked(double mx, double my, int button) {
        boolean inside = inView(mx, my);
        double cx = inside ? mx : -10000, cy = inside ? my : -10000;
        boolean used = false;
        for (Row r : rows) if (r.widget != null && r.widget.mouseClicked(cx, cy, button)) used = true;
        return used;
    }

    public boolean mouseDragged(double mx, double my, int button) {
        boolean used = false;
        for (Row r : rows) if (r.widget != null && r.widget.mouseDragged(mx, my, button)) used = true;
        return used;
    }

    public void mouseReleased() {
        for (Row r : rows) if (r.widget != null) r.widget.mouseReleased();
    }

    public boolean mouseScrolled(double mx, double my, double vAmount) {
        if (!inView(mx, my)) return false;
        scroll = Math.max(0, Math.min(scroll - (int) (vAmount * 14), Math.max(0, contentH - vh)));
        return true;
    }

    public boolean keyPressed(int key, int scan, int mods) {
        for (Row r : rows) if (r.widget != null && r.widget.keyPressed(key, scan, mods)) return true;
        return false;
    }

    public boolean charTyped(char ch) {
        for (Row r : rows) if (r.widget != null && r.widget.charTyped(ch)) return true;
        return false;
    }
}
