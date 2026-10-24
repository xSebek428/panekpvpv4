package pl.pvpmenu.mixin;

import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import pl.pvpmenu.config.PvpConfig;

/** Globalny przełącznik cienia tekstu (menu + HUD + reszta UI). */
@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @ModifyVariable(
            method = {
                    "drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I",
                    "drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I",
                    "drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;IIIZ)I"
            },
            at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private boolean pvpmenu$globalShadow(boolean shadow) {
        return shadow && PvpConfig.get().textShadow;
    }
}
