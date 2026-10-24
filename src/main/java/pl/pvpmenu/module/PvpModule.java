package pl.pvpmenu.module;

import pl.pvpmenu.config.PvpConfig;

/** Moduły PvP, do których można przypisać klawisz. */
public enum PvpModule {
    NO_SHADOWS("Brak cieni"),
    TEXT_SHADOWS("Cienie tekstu"),
    HUD("HUD");

    public final String label;

    PvpModule(String label) {
        this.label = label;
    }

    public String id() {
        return name().toLowerCase();
    }

    public boolean isEnabled(PvpConfig c) {
        return switch (this) {
            case NO_SHADOWS -> c.noShadows;
            case TEXT_SHADOWS -> c.textShadow;
            case HUD -> c.hudEnabled;
        };
    }

    public void setEnabled(PvpConfig c, boolean v) {
        switch (this) {
            case NO_SHADOWS -> c.noShadows = v;
            case TEXT_SHADOWS -> c.textShadow = v;
            case HUD -> c.hudEnabled = v;
        }
    }
}
