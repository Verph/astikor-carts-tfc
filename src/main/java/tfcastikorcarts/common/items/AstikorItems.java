package tfcastikorcarts.common.items;

import static tfcastikorcarts.TFCAstikorCarts.MOD_ID;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import de.mennomax.astikorcarts.item.CartItem;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.Helpers;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class AstikorItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final Map<Wood, RegistryObject<Item>> WHEEL_TFC = Helpers.mapOfKeys(Wood.class, wood -> register("wheel/" + wood.name()));
    public static final Map<Wood, RegistryObject<Item>> SUPPLY_CART_TFC = Helpers.mapOfKeys(Wood.class, wood -> register("supply_cart/" + wood.name(), () -> new CartItem(new Item.Properties().stacksTo(1))));
    public static final Map<Wood, RegistryObject<Item>> PLOW_TFC = Helpers.mapOfKeys(Wood.class, wood -> register("plow/" + wood.name(), () -> new CartItem(new Item.Properties().stacksTo(1))));
    public static final Map<Wood, RegistryObject<Item>> ANIMAL_CART_TFC = Helpers.mapOfKeys(Wood.class, wood -> register("animal_cart/" + wood.name(), () -> new CartItem(new Item.Properties().stacksTo(1))));
    public static final Map<Wood, RegistryObject<Item>> POSTILION_TFC = Helpers.mapOfKeys(Wood.class, wood -> register("postilion/" + wood.name(), () -> new CartItem(new Item.Properties().stacksTo(1))));

    private static RegistryObject<Item> register(String name)
    {
        return register(name, () -> new Item(new Item.Properties()));
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item)
    {
        return ITEMS.register(name.toLowerCase(Locale.ROOT), item);
    }
}
