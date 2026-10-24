package pl.pvpmenu;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pvpmenu.config.PvpConfig;
import pl.pvpmenu.gui.PvpMenuScreen;
import pl.pvpmenu.hud.PvpHud;
import pl.pvpmenu.module.PlaytimeTracker;
import pl.pvpmenu.module.PvpModuleManager;

public final class PvpMenuMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("pvpmenu");
    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        PvpConfig.load();

        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pvpmenu.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X, "category.pvpmenu"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                client.setScreen(new PvpMenuScreen());
            }
            PvpModuleManager.tick(client);
            PlaytimeTracker.tick(client);
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(PvpModuleManager::applyOnStart);
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> PvpConfig.save());
        PvpHud.register();
    }
}
