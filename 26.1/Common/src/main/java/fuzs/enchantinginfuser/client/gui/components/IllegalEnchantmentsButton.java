package fuzs.enchantinginfuser.client.gui.components;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;

public class IllegalEnchantmentsButton extends Button {
    private final WidgetSprites sprites;
    private boolean toggled;

    public IllegalEnchantmentsButton(int x, int y, WidgetSprites sprites, OnPress onPress) {
        super(x, y, 18, 18, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
        this.sprites = sprites;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        boolean highlighted = this.active
                && mouseX >= this.getX()
                && mouseY >= this.getY()
                && mouseX < this.getX() + this.width
                && mouseY < this.getY() + this.height;
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                highlighted ? this.sprites.get(true, true) : this.sprites.get(this.toggled, false),
                this.getX(),
                this.getY(),
                this.width,
                this.height);
    }
}
