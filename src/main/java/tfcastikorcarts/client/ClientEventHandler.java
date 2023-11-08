package tfcastikorcarts.client;

import java.util.Locale;

import de.mennomax.astikorcarts.client.renderer.texture.AssembledTexture;
import de.mennomax.astikorcarts.client.renderer.texture.AssembledTextureFactory;
import de.mennomax.astikorcarts.client.renderer.texture.Material;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tfcastikorcarts.TFCAstikorCarts;
import tfcastikorcarts.client.renderer.entity.*;
import tfcastikorcarts.client.renderer.entity.model.*;
import tfcastikorcarts.common.entities.AstikorEntities;
import tfcastikorcarts.util.AstikorHelpers;

public final class ClientEventHandler
{
    public static void init()
    {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(ClientEventHandler::registerEntityRenderers);
        bus.addListener(ClientEventHandler::registerLayerDefinitions);
        registerAssembledTextures(bus);
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

    public static void registerAssembledTextures(final IEventBus event)
    {
        for (Wood wood : Wood.VALUES)
        {
            new AssembledTextureFactory()
                .add(AstikorHelpers.identifier("textures/entity/animal_cart/" + wood.name().toLowerCase(Locale.ROOT) + ".png"), new AssembledTexture(64, 64)
                    .add(new Material(new ResourceLocation(TerraFirmaCraft.MOD_ID, "block/" + TFCBlocks.WOODS.get(wood).get(Wood.BlockType.PLANKS).getId().getPath()), 16)
                        .fill(0, 0, 60, 38, Material.R0, 0, 2)
                        .fill(0, 28, 20, 33, Material.R90, 4, -2)
                        .fill(12, 30, 8, 31, Material.R270, 0, 4)
                    )
                    .add(new Material(new ResourceLocation(TerraFirmaCraft.MOD_ID, "block/" + TFCBlocks.WOODS.get(wood).get(Wood.BlockType.STRIPPED_LOG).getId().getPath()), 16)
                        .fill(54, 54, 10, 10, Material.R0, 0, 2)
                    )
                    .add(new Material(new ResourceLocation(TerraFirmaCraft.MOD_ID, "block/" + TFCBlocks.WOODS.get(wood).get(Wood.BlockType.LOG).getId().getPath()), 16)
                        .fill(0, 21, 60, 4, Material.R90)
                        .fill(46, 60, 8, 4, Material.R90)
                    )
                    .add(new Material(new ResourceLocation(TerraFirmaCraft.MOD_ID, "block/" + TFCBlocks.ROCK_BLOCKS.get(Rock.DACITE).get(Rock.BlockType.RAW).getId().getPath()), 16)
                        .fill(62, 55, 2, 9)
                    )
                )
                .add(AstikorHelpers.identifier("textures/entity/plow/" + wood.name().toLowerCase(Locale.ROOT) + ".png"), new AssembledTexture(64, 64)
                    .add(new Material(new ResourceLocation(TerraFirmaCraft.MOD_ID, "block/" + TFCBlocks.WOODS.get(wood).get(Wood.BlockType.PLANKS).getId().getPath()), 16)
                        .fill(0, 0, 64, 32, Material.R90)
                        .fill(0, 8, 42, 3, Material.R0, 0, 1)
                        .fill(0, 27, 34, 3, Material.R0, 0, 2)
                    )
                    .add(new Material(new ResourceLocation(TerraFirmaCraft.MOD_ID, "block/" + TFCBlocks.WOODS.get(wood).get(Wood.BlockType.STRIPPED_LOG).getId().getPath()), 16)
                        .fill(54, 54, 10, 10, Material.R0, 2, 0)
                    )
                    .add(new Material(new ResourceLocation(TerraFirmaCraft.MOD_ID, "block/" + TFCBlocks.WOODS.get(wood).get(Wood.BlockType.LOG).getId().getPath()), 16)
                        .fill(0, 0, 54, 4, Material.R90)
                        .fill(46, 60, 8, 4, Material.R90)
                    )
                    .add(new Material(new ResourceLocation(TerraFirmaCraft.MOD_ID, "block/" + TFCBlocks.ROCK_BLOCKS.get(Rock.DACITE).get(Rock.BlockType.RAW).getId().getPath()), 16)
                        .fill(62, 55, 2, 9)
                    )
                )
                .add(AstikorHelpers.identifier("textures/entity/supply_cart/" + wood.name().toLowerCase(Locale.ROOT) + ".png"), new AssembledTexture(64, 64)
                    .add(new Material(new ResourceLocation(TerraFirmaCraft.MOD_ID, "block/" + TFCBlocks.WOODS.get(wood).get(Wood.BlockType.PLANKS).getId().getPath()), 16)
                        .fill(0, 0, 60, 45, Material.R0, 0, 2)
                        .fill(0, 27, 60, 8, Material.R0, 0, 1)
                    )
                    .add(new Material(new ResourceLocation(TerraFirmaCraft.MOD_ID, "block/" + TFCBlocks.WOODS.get(wood).get(Wood.BlockType.STRIPPED_LOG).getId().getPath()), 16)
                        .fill(54, 54, 10, 10, Material.R0, 0, 2)
                    )
                    .add(new Material(new ResourceLocation(TerraFirmaCraft.MOD_ID, "block/" + TFCBlocks.WOODS.get(wood).get(Wood.BlockType.LOG).getId().getPath()), 16)
                        .fill(0, 23, 54, 4, Material.R90)
                        .fill(46, 60, 8, 4, Material.R90)
                    )
                    .add(new Material(new ResourceLocation(TerraFirmaCraft.MOD_ID, "block/" + TFCBlocks.ROCK_BLOCKS.get(Rock.DACITE).get(Rock.BlockType.RAW).getId().getPath()), 16)
                        .fill(62, 55, 2, 9)
                    )
                    .add(new Material(new ResourceLocation("block/composter_side"), 16)
                        .fill(16, 47, 44, 5, Material.R0, -2, 1)
                        .fill(16, 54, 38, 5, Material.R0, -2, -6)
                    )
                    .add(new Material(new ResourceLocation("block/composter_top"), 16)
                        .fill(18, 45, 10, 2, Material.R0, -2, 3)
                        .fill(28, 45, 10, 2, Material.R0, 10, 3)
                        .fill(18, 52, 8, 2, Material.R0, 0, -4)
                        .fill(26, 52, 9, 2, Material.R0, 11, -4)
                    )
                    .add(new Material(new ResourceLocation(TerraFirmaCraft.MOD_ID, "block/" + TFCBlocks.SOIL.get(SoilBlockType.DIRT).get(SoilBlockType.Variant.LOAM).getId().getPath()), 16)
                        .fill(0, 45, 16, 17)
                    )
                )
                .register(event);
        }
    }
}
