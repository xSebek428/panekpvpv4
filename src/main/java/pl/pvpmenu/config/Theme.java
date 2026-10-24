package pl.pvpmenu.config;

public enum Theme {
    DARK("Ciemny", 0xFF0F1115, 0xFF151922, 0xFF1C212B, 0xFF262C39, 0xFFE6E8EC, 0xFF8A93A3, 0xFF3A4150),
    LIGHT("Jasny", 0xFFF3F4F6, 0xFFE5E7EB, 0xFFFFFFFF, 0xFFEEF0F3, 0xFF111827, 0xFF6B7280, 0xFFC4C9D2),
    BASIC("Podstawowy", 0xFF3B3B3B, 0xFF2A2A2A, 0xFF565656, 0xFF686868, 0xFFFFFFFF, 0xFFB0B0B0, 0xFF303030);

    public final String label;
    public final int bg, sidebar, tile, tileHover, text, sub, toggleOff;

    Theme(String label, int bg, int sidebar, int tile, int tileHover, int text, int sub, int toggleOff) {
        this.label = label;
        this.bg = bg;
        this.sidebar = sidebar;
        this.tile = tile;
        this.tileHover = tileHover;
        this.text = text;
        this.sub = sub;
        this.toggleOff = toggleOff;
    }
}
