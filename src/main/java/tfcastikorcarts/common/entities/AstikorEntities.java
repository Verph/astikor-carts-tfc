package tfcastikorcarts.common.entities;

import static tfcastikorcarts.TFCAstikorCarts.MOD_ID;

import java.util.Locale;
import java.util.Map;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.world.AstikorWorld;
import de.mennomax.astikorcarts.world.SimpleAstikorWorld;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.Helpers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tfcastikorcarts.common.items.AstikorItems;
import tfcastikorcarts.common.entities.carts.*;

@SuppressWarnings("unused")
public class AstikorEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);

    public static final Map<Wood, RegistryObject<EntityType<TFCSupplyCartEntity>>> SUPPLY_CART_TFC = Helpers.mapOfKeys(Wood.class, wood ->
        register("supply_cart/" + wood.name(), EntityType.Builder.<TFCSupplyCartEntity>of((type, level) -> new TFCSupplyCartEntity(type, level, AstikorItems.SUPPLY_CART_TFC.get(wood)), MobCategory.MISC).sized(1.5F, 1.4F).clientTrackingRange(10))
    );
    public static final Map<Wood, RegistryObject<EntityType<TFCPlowEntity>>> PLOW_TFC = Helpers.mapOfKeys(Wood.class, wood ->
        register("plow/" + wood.name(), EntityType.Builder.<TFCPlowEntity>of((type, level) -> new TFCPlowEntity(type, level, AstikorItems.PLOW_TFC.get(wood)), MobCategory.MISC).sized(1.3F, 1.4F).clientTrackingRange(10))
    );
    public static final Map<Wood, RegistryObject<EntityType<TFCAnimalCartEntity>>> ANIMAL_CART_TFC = Helpers.mapOfKeys(Wood.class, wood ->
        register("animal_cart/" + wood.name(), EntityType.Builder.<TFCAnimalCartEntity>of((type, level) -> new TFCAnimalCartEntity(type, level, AstikorItems.ANIMAL_CART_TFC.get(wood), wood), MobCategory.MISC).sized(1.3F, 1.4F).clientTrackingRange(10))
    );
    public static final Map<Wood, RegistryObject<EntityType<TFCPostilionEntity>>> POSTILION_TFC = Helpers.mapOfKeys(Wood.class, wood ->
        register("postilion/" + wood.name(), EntityType.Builder.<TFCPostilionEntity>of((type, level) -> new TFCPostilionEntity(type, level, AstikorItems.POSTILION_TFC.get(wood)), MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(10))
    );

    public static <E extends Entity> RegistryObject<EntityType<E>> register(String name, EntityType.Builder<E> builder)
    {
        return register(name, builder, true);
    }

    public static <E extends Entity> RegistryObject<EntityType<E>> register(String name, EntityType.Builder<E> builder, boolean serialize)
    {
        final String id = name.toLowerCase(Locale.ROOT);
        return ENTITIES.register(id, () -> {
            if (!serialize) builder.noSave();
            return builder.build(MOD_ID + ":" + id);
        });
    }

    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event)
    {
        for (Wood wood : Wood.VALUES)
        {
            event.put(POSTILION_TFC.get(wood).get(), LivingEntity.createLivingAttributes().build());
        }
    }
}
