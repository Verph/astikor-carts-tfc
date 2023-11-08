package tfcastikorcarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import de.mennomax.astikorcarts.client.renderer.entity.DrawnRenderer;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;

import tfcastikorcarts.client.AstikorRenderHelpers;
import tfcastikorcarts.client.renderer.entity.model.TFCAnimalCartModel;
import tfcastikorcarts.common.entities.carts.TFCAnimalCartEntity;
import tfcastikorcarts.util.AstikorHelpers;

import java.util.List;

public class TFCAnimalCartRenderer extends DrawnRenderer<TFCAnimalCartEntity, TFCAnimalCartModel>
{
    public final Pair<ResourceLocation, TFCAnimalCartModel> location;

    public static ModelLayerLocation entityName(String name)
    {
        return AstikorRenderHelpers.modelIdentifier("animal_cart/" + name);
    }

    public TFCAnimalCartRenderer(final EntityRendererProvider.Context renderManager, String name)
    {
        super(renderManager, new TFCAnimalCartModel(renderManager.bakeLayer(entityName(name))));
        this.shadowRadius = 1.0F;
        this.location = Pair.of(AstikorHelpers.identifier("textures/entity/animal_cart/" + name + ".png"), new TFCAnimalCartModel(renderManager.bakeLayer(entityName(name))));
    }

    @Override
    public void renderContents(final TFCAnimalCartEntity entity, final float delta, final PoseStack stack, final MultiBufferSource source, final int packedLight)
    {
        super.renderContents(entity, delta, stack, source, packedLight);
        final List<Pair<Holder<BannerPattern>, DyeColor>> list = entity.getBannerPattern();
        if (!list.isEmpty())
        {
            stack.pushPose();
            this.model.getBody().translateAndRotate(stack);
            stack.translate(0.0D, -0.6D, 1.56D);
            this.renderBanner(stack, source, packedLight, list);
            stack.popPose();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(final TFCAnimalCartEntity entity)
    {
        return getModelWithLocation(entity).getFirst();
    }

    public Pair<ResourceLocation, TFCAnimalCartModel> getModelWithLocation(TFCAnimalCartEntity entity)
    {
        return location;
    }
}