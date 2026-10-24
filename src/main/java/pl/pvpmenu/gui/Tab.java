package pl.pvpmenu.gui;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public enum Tab {
    MOD_MENU("Mod Menu", Items.CRAFTING_TABLE),
    PLAYERS("Gracze", Items.PLAYER_HEAD),
    SETTINGS("Ustawienia", Items.REPEATER),
    MODS("Mody", Items.CHEST),
    PROFILE("Profil", Items.NAME_TAG);

    public final String title;
    private final Item item;
    private ItemStack stack;

    Tab(String title, Item item) {
        this.title = title;
        this.item = item;
    }

    public ItemStack icon() {
        if (stack == null) stack = new ItemStack(item);
        return stack;
    }
}
