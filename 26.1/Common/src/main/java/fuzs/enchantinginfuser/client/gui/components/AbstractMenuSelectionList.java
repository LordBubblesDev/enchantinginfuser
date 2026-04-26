package fuzs.enchantinginfuser.client.gui.components;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

/**
 * A selection list implementation that can be used as part of a screen anywhere, without having to cover the whole
 * screen width.
 * <p>
 * Also, the scroll bar is mostly handled separately and is placed outside the bounds of the actual list.
 */
public abstract class AbstractMenuSelectionList<E extends AbstractMenuSelectionList.Entry<E>> extends ContainerObjectSelectionList<E> {
    public static final WidgetSprites SCROLLER_SPRITES = new WidgetSprites(
            Identifier.withDefaultNamespace("container/creative_inventory/scroller"),
            Identifier.withDefaultNamespace("container/creative_inventory/scroller_disabled"),
            Identifier.withDefaultNamespace("container/creative_inventory/scroller")
    );
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;

    private final int scrollbarOffset;
    private boolean customScrolling;

    public AbstractMenuSelectionList(Minecraft minecraft, int x, int y, int width, int height, int itemHeight, int scrollbarOffset) {
        super(minecraft, width, height, y, itemHeight);
        this.scrollbarOffset = scrollbarOffset;
        this.setX(x);
    }

    @Override
    public int addEntry(E entry) {
        return super.addEntry(entry);
    }

    @Override
    public void clearEntries() {
        super.clearEntries();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean doubleClick) {
        if (this.updateScrolling(mouseButtonEvent)) {
            this.customScrolling = true;
            this.setMouseButtonScrollAmount(mouseButtonEvent);
            return true;
        }
        return super.mouseClicked(mouseButtonEvent, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double dragX, double dragY) {
        if (this.customScrolling) {
            this.setMouseButtonScrollAmount(mouseButtonEvent);
            return true;
        }
        return super.mouseDragged(mouseButtonEvent, dragX, dragY);
    }

    @Override
    public void onRelease(MouseButtonEvent mouseButtonEvent) {
        this.customScrolling = false;
        super.onRelease(mouseButtonEvent);
    }

    private void setMouseButtonScrollAmount(MouseButtonEvent mouseButtonEvent) {
        double scrollOffset = (mouseButtonEvent.y() - this.getY() - this.scrollerHeight() / 2.0) / (this.getHeight()
                - this.scrollerHeight());
        this.setScrollAmount(Mth.clamp(scrollOffset, 0.0, 1.0) * this.maxScrollAmount());
    }

    @Override
    public int getRowWidth() {
        return this.getWidth();
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractWidgetRenderState(guiGraphics, mouseX, mouseY, partialTick);
        boolean scrollbarUsable = this.maxScrollAmount() > 0;
        int posY = this.scrollBarY();
        Identifier sprite = SCROLLER_SPRITES.get(scrollbarUsable, false);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, this.customScrollBarX(), posY, SCROLLER_WIDTH, SCROLLER_HEIGHT);
        if (this.isOverScrollbar(mouseX, mouseY)) {
            guiGraphics.requestCursor(CursorTypes.POINTING_HAND);
        }
    }

    @Override
    protected void extractListSeparators(GuiGraphicsExtractor guiGraphics) {
        // NO-OP
    }

    @Override
    protected void extractListBackground(GuiGraphicsExtractor guiGraphics) {
        // NO-OP
    }

    @Override
    protected int scrollBarX() {
        // Hide vanilla scrollbar rendering and draw our own themed sprite in extractWidgetRenderState.
        return this.getRowRight() + 10_000;
    }

    protected int customScrollBarX() {
        return this.getRowRight() + this.scrollbarOffset;
    }

    @Override
    protected int scrollerHeight() {
        return SCROLLER_HEIGHT;
    }

    @Override
    protected int contentHeight() {
        return super.contentHeight() - 4;
    }

    @Override
    protected boolean isOverScrollbar(double mouseX, double mouseY) {
        return mouseX >= this.customScrollBarX()
                && mouseX < this.customScrollBarX() + SCROLLER_WIDTH
                && mouseY >= this.getY()
                && mouseY < this.getY() + this.getHeight();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || this.isOverScrollbar(mouseX, mouseY);
    }

    @Override
    protected void extractSelection(GuiGraphicsExtractor guiGraphics, E entry, int innerColor) {
        // NO-OP
    }

    @Override
    public int getRowLeft() {
        return this.getX();
    }

    @Override
    public int getRowTop(int index) {
        return super.getRowTop(index) - 4;
    }

    protected abstract static class Entry<E extends Entry<E>> extends ContainerObjectSelectionList.Entry<E> {
        private final List<AbstractWidget> children = new ArrayList<>();

        @Override
        public int getContentX() {
            return this.getX();
        }

        @Override
        public int getContentY() {
            return this.getY();
        }

        @Override
        public int getContentHeight() {
            return this.getHeight();
        }

        @Override
        public int getContentWidth() {
            return this.getWidth();
        }

        protected <T extends AbstractWidget> T addRenderableWidget(T widget) {
            this.children.add(widget);
            return widget;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            for (AbstractWidget abstractWidget : this.children) {
                abstractWidget.setY(this.getContentY());
                abstractWidget.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.children;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.children;
        }
    }
}
