package tfcastikorcarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import de.mennomax.astikorcarts.client.renderer.entity.DrawnRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import tfcastikorcarts.client.AstikorRenderHelpers;
import tfcastikorcarts.client.renderer.entity.model.TFCPlowModel;
import tfcastikorcarts.common.entities.carts.TFCPlowEntity;
import tfcastikorcarts.util.AstikorHelpers;

public class TFCPlowRenderer extends DrawnRenderer<TFCPlowEntity, TFCPlowModel>
{
    public final Pair<ResourceLocation, TFCPlowModel> location;

    public static ModelLayerLocation entityName(String name)
    {
        return AstikorRenderHelpers.modelIdentifier("plow/" + name);
    }

    public TFCPlowRenderer(final EntityRendererProvider.Context renderManager, String name)
    {
        super(renderManager, new TFCPlowModel(renderManager.bakeLayer(entityName(name))));
        this.shadowRadius = 1.0F;
        this.location = Pair.of(AstikorHelpers.identifier("textures/entity/plow/" + name + ".png"), new TFCPlowModel(renderManager.bakeLayer(entityName(name))));
    }

    @Override
    public ResourceLocation getTextureLocation(final TFCPlowEntity entity)
    {
        return getModelWithLocation(entity).getFirst();
    }

    public Pair<ResourceLocation, TFCPlowModel> getModelWithLocation(TFCPlowEntity entity)
    {
        return location;
    }

    @Override
    protected void renderContents(final TFCPlowEntity entity, final float delta, final PoseStack stack, final MultiBufferSource source, final int packedLight)
    {
        super.renderContents(entity, delta, stack, source, packedLight);
        for (int i = 0; i < entity.inventory.getSlots(); i++) {
            final ItemStack itemStack = entity.getStackInSlot(i);
            if (itemStack.isEmpty()) {
                continue;
            }
            this.attach(this.model.getBody(), this.model.getShaft(i), s -> {
                s.mulPose(Axis.XP.rotationDegrees(-90.0F));
                s.mulPose(Axis.YP.rotationDegrees(90.0F));
                s.translate(-4.0D / 16.0D, 1.0D / 16.0D, 0.0D);
                if (itemStack.getItem() instanceof BlockItem) {
                    s.translate(0.0D, -0.1D, 0.0D);
                    s.mulPose(Axis.ZP.rotationDegrees(180.0F));
                }
                Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, s, source, entity.level(), 0);
            }, stack);
        }
    }
}
