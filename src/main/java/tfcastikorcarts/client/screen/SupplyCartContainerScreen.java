package tfcastikorcarts.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import tfcastikorcarts.common.container.CartContainer;
import tfcastikorcarts.common.container.ContainerList;

public class SupplyCartContainerScreen extends AbstractContainerScreen<CartContainer> implements MenuAccess<CartContainer>
{
    public final ContainerList containerType;
    public final int textureXSize;
    public final int textureYSize;

    public SupplyCartContainerScreen(CartContainer container, Inventory playerInventory, Component title)
    {
        super(container, playerInventory, title);

        this.containerType = container.getContainerType();
        this.imageWidth = containerType.xSize;
        this.imageHeight = containerType.ySize;
        this.textureXSize = containerType.textureXSize;
        this.textureYSize = containerType.textureYSize;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY)
    {
        guiGraphics.drawString(this.font, this.title, 8, 6, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, (this.imageHeight - 96 + 2), 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.containerType.getResourceLocation());

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(this.containerType.getResourceLocation(), x, y, 0, 0, this.imageWidth, this.imageHeight, this.textureXSize, this.textureYSize);
    }
}
