package tfcastikorcarts.client;

import java.util.Locale;

import de.mennomax.astikorcarts.client.renderer.texture.AssembledTexture;
import de.mennomax.astikorcarts.client.renderer.texture.AssembledTextureFactory;
import de.mennomax.astikorcarts.client.renderer.texture.Material;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tfcastikorcarts.client.renderer.entity.*;
import tfcastikorcarts.client.renderer.entity.model.*;
import tfcastikorcarts.common.entities.AstikorEntities;

public final class ClientEventHandler
{
    public static void init()
    {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(ClientEventHandler::registerEntityRenderers);
        bus.addListener(ClientEventHandler::registerLayerDefinitions);
    }

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        // Entities
        for (Wood wood : Wood.VALUES)
        {
            event.registerEntityRenderer(AstikorEntities.SUPPLY_CART_TFC.get(wood).get(), ctx -> new TFCSupplyCartRenderer(ctx, wood.getSerializedName()));
            event.registerEntityRenderer(AstikorEntities.PLOW_TFC.get(wood).get(), ctx -> new TFCPlowRenderer(ctx, wood.getSerializedName()));
            event.registerEntityRenderer(AstikorEntities.ANIMAL_CART_TFC.get(wood).get(), ctx -> new TFCAnimalCartRenderer(ctx, wood.getSerializedName()));
            event.registerEntityRenderer(AstikorEntities.POSTILION_TFC.get(wood).get(), TFCPostilionRenderer::new);
        }
    }

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        for (Wood wood : Wood.VALUES)
        {
            event.registerLayerDefinition(TFCAnimalCartRenderer.entityName(wood.getSerializedName()), TFCAnimalCartModel::createLayer);
            event.registerLayerDefinition(TFCPlowRenderer.entityName(wood.getSerializedName()), TFCPlowModel::createLayer);
            event.registerLayerDefinition(TFCSupplyCartRenderer.entityName(wood.getSerializedName()), TFCSupplyCartModel::createLayer);
        }
    }
}
