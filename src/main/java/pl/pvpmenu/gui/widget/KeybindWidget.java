package pl.pvpmenu.gui.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import pl.pvpmenu.config.PvpConfig;
import pl.pvpmenu.gfx.Gfx;
import pl.pvpmenu.gui.UiSound;
import pl.pvpmenu.module.PvpModule;

/** Przycisk przypisania klawisza. Klik -> naciśnij klawisz; ESC czyści bind. */
public final class KeybindWidget extends UiWidget {
    private final PvpModule module;
    private boolean listening;
    private float anim;

    public KeybindWidget(PvpModule module) {
        this.module = module;
        this.w = 84;
        this.h = 16;
    }

    private String label() {
        if (listening) return "Naciśnij...";
        int key = PvpConfig.get().getBind(module);
        if (key < 0) return "Brak";
        return InputUtil.fromKeyCode(key, 0).getLocalizedText().getString();
    }

    @Override
    public void render(DrawContext c, int mx, int my) {
        PvpConfig cfg = PvpConfig.get();
        anim = Gfx.approach(anim, listening ? 1f : contains(mx, my) ? 0.3f : 0f);
        Gfx.fillRound(c, x, y, w, h, Gfx.radius(4), Gfx.lerp(cfg.theme.sidebar, cfg.accentColor(), anim));
        String s = label();
        c.drawText(font(), s, x + (w - font().getWidth(s)) / 2, y + 4, listening ? 0xFFFFFFFF : cfg.theme.text, true);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && contains(mx, my)) {
            listening = !listening;
            UiSound.click();
            return true;
        }
        listening = false; // klik gdzie indziej anuluje nasłuchiwanie
        return false;
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (!listening) return false;
        PvpConfig.get().setBind(module, key == GLFW.GLFW_KEY_ESCAPE ? PvpConfig.NO_KEY : key);
        listening = false;
        UiSound.click();
        PvpConfig.save();
        return true; // pochłania także ESC, więc menu się nie zamyka
    }
}
