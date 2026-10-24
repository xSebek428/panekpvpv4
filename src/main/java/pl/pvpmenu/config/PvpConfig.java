package pl.pvpmenu.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.glfw.GLFW;
import pl.pvpmenu.PvpMenuMod;
import pl.pvpmenu.module.PvpModule;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** Konfiguracja zapisywana do .minecraft/config/pvpmenu.json */
public final class PvpConfig {
    public static final int NO_KEY = GLFW.GLFW_KEY_UNKNOWN; // -1

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("pvpmenu.json");
    private static PvpConfig instance = new PvpConfig();

    public String guiColor = "#3B82F6";
    public boolean roundedCorners = true;
    public boolean menuSounds = true;
    public boolean textShadow = true;
    public Theme theme = Theme.DARK;
    public boolean noShadows = false;
    public boolean hudEnabled = true;
    public long playtimeSeconds = 0;
    public Map<String, Integer> binds = new LinkedHashMap<>();

    public static PvpConfig get() {
        return instance;
    }

    public static void load() {
        if (Files.exists(FILE)) {
            try (Reader r = Files.newBufferedReader(FILE)) {
                PvpConfig loaded = GSON.fromJson(r, PvpConfig.class);
                if (loaded != null) instance = loaded;
            } catch (Exception e) {
                PvpMenuMod.LOGGER.error("Nie udało się wczytać pvpmenu.json, używam domyślnych", e);
            }
        }
        instance.sanitize();
        save();
    }

    public static void save() {
        try (Writer w = Files.newBufferedWriter(FILE)) {
            GSON.toJson(instance, w);
        } catch (Exception e) {
            PvpMenuMod.LOGGER.error("Nie udało się zapisać pvpmenu.json", e);
        }
    }

    private void sanitize() {
        if (theme == null) theme = Theme.DARK;
        if (parseHex(guiColor) == null) guiColor = "#3B82F6";
        if (binds == null) binds = new LinkedHashMap<>();
        for (PvpModule m : PvpModule.values()) binds.putIfAbsent(m.id(), NO_KEY);
    }

    public int getBind(PvpModule m) {
        return binds.getOrDefault(m.id(), NO_KEY);
    }

    public void setBind(PvpModule m, int key) {
        binds.put(m.id(), key);
    }

    /** Zwraca 0xRRGGBB albo null, gdy tekst nie jest poprawnym #RRGGBB. */
    public static Integer parseHex(String s) {
        if (s == null || !s.matches("#[0-9a-fA-F]{6}")) return null;
        return Integer.parseInt(s.substring(1), 16);
    }

    public int accentColor() {
        Integer v = parseHex(guiColor);
        return 0xFF000000 | (v == null ? 0x3B82F6 : v);
    }
}
