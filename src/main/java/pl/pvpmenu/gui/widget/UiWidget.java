package pl.pvpmenu.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public abstract class UiWidget {
    public int x, y, w, h;

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void render(DrawContext c, int mx, int my);

    public boolean mouseClicked(double mx, double my, int button) { return false; }
    public boolean mouseDragged(double mx, double my, int button) { return false; }
    public void mouseReleased() {}
    public boolean keyPressed(int key, int scan, int mods) { return false; }
    public boolean charTyped(char ch) { return false; }

    protected boolean contains(double mx, double my) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    protected static TextRenderer font() {
        return MinecraftClient.getInstance().textRenderer;
    }
}
