package pl.pvpmenu.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;
import pl.pvpmenu.config.PvpConfig;
import pl.pvpmenu.gfx.Gfx;

import java.awt.Color;

/** Selektor koloru: pole HEX + podgląd + suwak odcienia (gradient). */
public final class HexColorWidget extends UiWidget {
    private static final int BOX_W = 66;
    private boolean focused, dragging, fresh;
    private String text = "";

    public HexColorWidget() {
        this.w = 170;
        this.h = 34;
    }

    @Override
    public void render(DrawContext c, int mx, int my) {
        PvpConfig cfg = PvpConfig.get();
        int accent = cfg.accentColor();
        String shown = focused ? text : cfg.guiColor;

        // pole tekstowe z obramowaniem
        Gfx.fillRound(c, x, y, BOX_W, 16, Gfx.radius(4), focused ? accent : cfg.theme.toggleOff);
        Gfx.fillRound(c, x + 1, y + 1, BOX_W - 2, 14, Gfx.radius(3), cfg.theme.sidebar);
        String caret = focused && (System.currentTimeMillis() / 500 % 2 == 0) ? "_" : "";
        c.drawText(font(), shown + caret, x + 5, y + 4, cfg.theme.text, true);

        // podgląd
        Integer parsed = PvpConfig.parseHex(shown);
        int preview = parsed != null ? 0xFF000000 | parsed : accent;
        Gfx.fillRound(c, x + BOX_W + 6, y, 16, 16, Gfx.radius(4), preview);

        // suwak odcienia
        int sy = y + 24;
        for (int i = 0; i < w; i++) {
            c.fill(x + i, sy, x + i + 1, sy + 8, Color.HSBtoRGB(i / (float) w, 1f, 1f));
        }
        float hue = Color.RGBtoHSB((accent >> 16) & 255, (accent >> 8) & 255, accent & 255, null)[0];
        int hx = x + Math.round(hue * (w - 2));
        c.fill(hx - 1, sy - 2, hx + 2, sy + 10, 0xFFFFFFFF);
        c.fill(hx, sy - 1, hx + 1, sy + 9, 0xFF000000);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && mx >= x && mx < x + BOX_W && my >= y && my < y + 16) {
            if (!focused) {
                focused = true;
                fresh = true;
                text = PvpConfig.get().guiColor;
            }
            return true;
        }
        if (button == 0 && mx >= x && mx < x + w && my >= y + 22 && my < y + 34) {
            dragging = true;
            setHue(mx);
            return true;
        }
        focused = false;
        return false;
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button) {
        if (!dragging) return false;
        setHue(mx);
        return true;
    }

    @Override
    public void mouseReleased() {
        if (dragging) {
            dragging = false;
            PvpConfig.save();
        }
    }

    private void setHue(double mx) {
        int accent = PvpConfig.get().accentColor();
        float hue = (float) Math.max(0, Math.min(1, (mx - x) / (w - 1)));
        float[] hsb = Color.RGBtoHSB((accent >> 16) & 255, (accent >> 8) & 255, accent & 255, null);
        float s = hsb[1] < 0.05f ? 0.8f : hsb[1];
        float v = hsb[2] < 0.2f ? 1f : hsb[2];
        PvpConfig.get().guiColor = String.format("#%06X", Color.HSBtoRGB(hue, s, v) & 0xFFFFFF);
    }

    private void tryApply() {
        if (PvpConfig.parseHex(text) != null) {
            PvpConfig.get().guiColor = text.toUpperCase();
            PvpConfig.save();
        }
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (!focused) return false;
        switch (key) {
            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (fresh) { text = ""; fresh = false; }
                else if (!text.isEmpty()) text = text.substring(0, text.length() - 1);
                tryApply();
            }
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER, GLFW.GLFW_KEY_ESCAPE -> focused = false;
            case GLFW.GLFW_KEY_V -> {
                if (Screen.hasControlDown()) {
                    String clip = MinecraftClient.getInstance().keyboard.getClipboard().trim();
                    if (!clip.startsWith("#")) clip = "#" + clip;
                    if (clip.length() <= 7) {
                        text = clip;
                        fresh = false;
                        tryApply();
                    }
                }
            }
            default -> {}
        }
        return true;
    }

    @Override
    public boolean charTyped(char ch) {
        if (!focused) return false;
        if (fresh) { text = ""; fresh = false; }
        if (text.length() >= 7) return true;
        if (text.isEmpty() && ch != '#') text = "#";
        if (ch == '#' && !text.isEmpty()) return true;
        if (Character.digit(ch, 16) >= 0) text += ch;
        tryApply();
        return true;
    }
}
