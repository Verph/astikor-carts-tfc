package tfcastikorcarts.common;

import static tfcastikorcarts.TFCAstikorCarts.MOD_ID;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.dries007.tfc.common.blocks.DecorationBlockRegistryObject;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.util.SelfTests;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import tfcastikorcarts.common.items.AstikorItems;

public class AstikorItemGroup
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final CreativeTabHolder CARTS = register("carts", () -> new ItemStack(AstikorItems.WHEEL_TFC.get(Wood.OAK).get()), AstikorItemGroup::fillCartsTab);

    public static Stream<CreativeModeTab.DisplayItemsGenerator> generators()
    {
        return Stream.of(CARTS).map(holder -> holder.generator);
    }

    public static void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event)
    {
        final CreativeModeTab tab = event.getTab();

        FoodCapability.setStackNonDecaying(tab.getIconItem());
        for (ItemStack item : tab.getDisplayItems())
        {
            FoodCapability.setStackNonDecaying(item);
        }
    }

    public static void fillCartsTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        for (Wood wood : Wood.values())
        {
            accept(out, AstikorItems.WHEEL_TFC.get(wood));
            accept(out, AstikorItems.SUPPLY_CART_TFC.get(wood));
            accept(out, AstikorItems.PLOW_TFC.get(wood));
            accept(out, AstikorItems.ANIMAL_CART_TFC.get(wood));
            accept(out, AstikorItems.POSTILION_TFC.get(wood));
        }
    }

    public static CreativeTabHolder register(String name, Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator displayItems)
    {
        final RegistryObject<CreativeModeTab> reg = CREATIVE_TABS.register(name, () -> CreativeModeTab.builder()
            .icon(icon)
            .title(Component.translatable("tfcastikorcarts.creative_tab." + name))
            .displayItems(displayItems)
            .build());
        return new CreativeTabHolder(reg, displayItems);
    }

    public static <T extends ItemLike, R extends Supplier<T>, K1, K2> void accept(CreativeModeTab.Output out, Map<K1, Map<K2, R>> map, K1 key1, K2 key2)
    {
        if (map.containsKey(key1) && map.get(key1).containsKey(key2))
        {
            out.accept(map.get(key1).get(key2).get());
        }
    }

    public static <T extends ItemLike, R extends Supplier<T>, K> void accept(CreativeModeTab.Output out, Map<K, R> map, K key)
    {
        if (map.containsKey(key))
        {
            out.accept(map.get(key).get());
        }
    }

    public static <T extends ItemLike, R extends Supplier<T>> void accept(CreativeModeTab.Output out, R reg)
    {
        if (reg.get().asItem() == Items.AIR)
        {
            SelfTests.reportExternalError();
            return;
        }
        out.accept(reg.get());
    }

    public static void accept(CreativeModeTab.Output out, DecorationBlockRegistryObject decoration)
    {
        out.accept(decoration.stair().get());
        out.accept(decoration.slab().get());
        out.accept(decoration.wall().get());
    }

    public static <T> void consumeOurs(IForgeRegistry<T> registry, Consumer<T> consumer)
    {
        for (T value : registry)
        {
            if (Objects.requireNonNull(registry.getKey(value)).getNamespace().equals(MOD_ID))
            {
                consumer.accept(value);
            }
        }
    }

    public record CreativeTabHolder(RegistryObject<CreativeModeTab> tab, CreativeModeTab.DisplayItemsGenerator generator) {}
}