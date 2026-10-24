package pl.pvpmenu.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import pl.pvpmenu.config.PvpConfig;
import pl.pvpmenu.config.Theme;
import pl.pvpmenu.gfx.Gfx;

/**
 * Własny układ: okno z górnym paskiem. Aktywna zakładka rozwija się w pigułkę z nazwą,
 * pozostałe są kompaktowymi ikonami (nazwa pojawia się po najechaniu).
 */
public final class PvpMenuScreen extends Screen {
    private static final int PAD = 6, HEADER_H = 30, PILL_H = 24, PILL_GAP = 4;

    private Tab active = Tab.SETTINGS;
    private final SettingsPanel settings = new SettingsPanel();
    private final PlayersPanel players = new PlayersPanel();
    private final ProfilePanel profile = new ProfilePanel();
    private final float[] tabAnim = new float[Tab.values().length];
    private final int[] tx = new int[Tab.values().length];
    private final int[] tw = new int[Tab.values().length];
    private int left, top, w, h;

    public PvpMenuScreen() {
        super(Text.literal("PvP Menu"));
        tabAnim[active.ordinal()] = 1f;
    }

    @Override
    protected void init() {
        w = Math.min(width - 20, 480);
        h = Math.min(height - 20, 290);
        left = (width - w) / 2;
        top = (height - h) / 2;
    }

    private int pillY() { return top + PAD + (HEADER_H - PILL_H) / 2; }

    private void layoutTabs() {
        Tab[] tabs = Tab.values();
        int total = 0;
        for (int i = 0; i < tabs.length; i++) {
            tw[i] = 26 + Math.round(tabAnim[i] * (textRenderer.getWidth(tabs[i].title) + 8));
            total += tw[i] + (i > 0 ? PILL_GAP : 0);
        }
        int x = left + w - PAD - 6 - total;
        for (int i = 0; i < tabs.length; i++) {
            tx[i] = x;
            x += tw[i] + PILL_GAP;
        }
    }

    private boolean in(double mx, double my, int x, int y, int ww, int hh) {
        return mx >= x && mx < x + ww && my >= y && my < y + hh;
    }

    @Override
    public void render(DrawContext c, int mx, int my, float delta) {
        PvpConfig cfg = PvpConfig.get();
        Theme t = cfg.theme;
        int accent = cfg.accentColor();

        c.fill(0, 0, width, height, 0x99000000);
        Gfx.fillRound(c, left, top, w, h, Gfx.radius(10), t.bg);

        // pasek górny
        Gfx.fillRound(c, left + PAD, top + PAD, w - 2 * PAD, HEADER_H, Gfx.radius(8), t.sidebar);
        int ty = top + PAD + (HEADER_H - 8) / 2;
        c.drawText(textRenderer, "PvP", left + PAD + 12, ty, accent, true);
        c.drawText(textRenderer, "Menu", left + PAD + 12 + textRenderer.getWidth("PvP ") , ty, t.text, true);

        // zakładki
        Tab[] tabs = Tab.values();
        int py = pillY();
        for (int i = 0; i < tabs.length; i++) {
            boolean hov = in(mx, my, tx[i], py, tw[i], PILL_H);
            tabAnim[i] = Gfx.approach(tabAnim[i], tabs[i] == active ? 1f : 0f);
        }
        layoutTabs();
        int hoverIdx = -1;
        for (int i = 0; i < tabs.length; i++) {
            boolean hov = in(mx, my, tx[i], py, tw[i], PILL_H);
            if (hov && tabs[i] != active) hoverIdx = i;
            float a = tabAnim[i];
            int bg = Gfx.lerp(t.sidebar, accent, Math.max(0.30f * a, hov ? 0.15f : 0f));
            Gfx.fillRound(c, tx[i], py, tw[i], PILL_H, Gfx.radius(6), bg);
            c.drawItem(tabs[i].icon(), tx[i] + 5, py + 4);

            if (a > 0.05f) {
                c.enableScissor(tx[i] + 24, py, tx[i] + tw[i] - 2, py + PILL_H);
                c.drawText(textRenderer, tabs[i].title, tx[i] + 26, py + 8, t.text, true);
                c.disableScissor();
            }
            if (a > 0.95f) {
                Gfx.hGradient(c, tx[i] + 6, py + PILL_H - 3, tw[i] - 12, 2, accent, Gfx.lerp(accent, 0xFFFFFFFF, 0.45f));
            }
        }

        // zawartość
        int cx = left + PAD, cy = top + PAD + HEADER_H + 8, cw = w - 2 * PAD, ch = h - 2 * PAD - HEADER_H - 8;
        switch (active) {
            case SETTINGS -> settings.render(c, mx, my, cx, cy, cw, ch);
            case PLAYERS -> players.render(c, mx, my, cx, cy, cw, ch);
            case PROFILE -> profile.render(c, mx, my, cx, cy, cw, ch);
            default -> {
                Gfx.fillRound(c, cx, cy, cw - 8, 40, Gfx.radius(8), t.tile);
                c.fill(cx + 4, cy + 8, cx + 6, cy + 32, accent);
                c.drawText(textRenderer, active.title, cx + 14, cy + 8, t.text, true);
                c.drawText(textRenderer, "Ta zakładka będzie dostępna wkrótce.", cx + 14, cy + 22, t.sub, true);
            }
        }

        // podpowiedź z nazwą zakładki
        if (hoverIdx >= 0) {
            String s = tabs[hoverIdx].title;
            int sw = textRenderer.getWidth(s) + 10;
            int sx = Math.min(tx[hoverIdx], left + w - sw - 2);
            Gfx.fillRound(c, sx, py + PILL_H + 4, sw, 14, Gfx.radius(4), t.tile);
            c.drawText(textRenderer, s, sx + 5, py + PILL_H + 7, t.text, true);
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        layoutTabs();
        Tab[] tabs = Tab.values();
        for (int i = 0; i < tabs.length; i++) {
            if (button == 0 && in(mx, my, tx[i], pillY(), tw[i], PILL_H)) {
                if (active != tabs[i]) UiSound.click();
                active = tabs[i];
                return true;
            }
        }
        if (active == Tab.SETTINGS && settings.mouseClicked(mx, my, button)) return true;
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (active == Tab.SETTINGS && settings.mouseDragged(mx, my, button)) return true;
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        settings.mouseReleased();
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hAmount, double vAmount) {
        if (active == Tab.SETTINGS && settings.mouseScrolled(mx, my, vAmount)) return true;
        if (active == Tab.PLAYERS && players.mouseScrolled(mx, my, vAmount)) return true;
        return super.mouseScrolled(mx, my, hAmount, vAmount);
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (active == Tab.SETTINGS && settings.keyPressed(key, scan, mods)) return true;
        return super.keyPressed(key, scan, mods); // ESC zamyka menu
    }

    @Override
    public boolean charTyped(char chr, int mods) {
        if (active == Tab.SETTINGS && settings.charTyped(chr)) return true;
        return super.charTyped(chr, mods);
    }

    @Override
    public void removed() {
        PvpConfig.save();
    }
}
