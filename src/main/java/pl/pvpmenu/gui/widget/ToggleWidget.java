package pl.pvpmenu.gui.widget;

import net.minecraft.client.gui.DrawContext;
import pl.pvpmenu.config.PvpConfig;
import pl.pvpmenu.gfx.Gfx;
import pl.pvpmenu.gui.UiSound;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** Przełącznik z animowaną gałką i płynnym przejściem koloru. */
public final class ToggleWidget extends UiWidget {
    private final BooleanSupplier getter;
    private final Consumer<Boolean> setter;
    private float anim = -1f;

    public ToggleWidget(BooleanSupplier getter, Consumer<Boolean> setter) {
        this.getter = getter;
        this.setter = setter;
        this.w = 34;
        this.h = 16;
    }

    @Override
    public void render(DrawContext c, int mx, int my) {
        PvpConfig cfg = PvpConfig.get();
        float target = getter.getAsBoolean() ? 1f : 0f;
        if (anim < 0) anim = target;
        anim = Gfx.approach(anim, target);

        int track = Gfx.lerp(cfg.theme.toggleOff, cfg.accentColor(), anim);
        Gfx.fillRound(c, x, y, w, h, Gfx.radius(h / 2), track);

        int knob = h - 4;
        int kx = x + 2 + Math.round(anim * (w - knob - 4));
        Gfx.fillRound(c, kx, y + 2, knob, knob, Gfx.radius(knob / 2), 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && contains(mx, my)) {
            setter.accept(!getter.getAsBoolean());
            UiSound.click();
            PvpConfig.save();
            return true;
        }
        return false;
    }
}
