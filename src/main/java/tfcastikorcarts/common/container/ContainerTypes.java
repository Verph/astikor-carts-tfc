package tfcastikorcarts.common.container;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.util.registry.RegistrationHelpers;

import static tfcastikorcarts.TFCAstikorCarts.*;

public class ContainerTypes
{
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, MOD_ID);

    public static final RegistryObject<MenuType<CartContainer>> SUPPLY_CART_CONTAINER_SIZE_0 = ContainerTypes.<CartContainer>register("supply_cart_inventory_size_0", CartContainer::createSize0);
    public static final RegistryObject<MenuType<CartContainer>> SUPPLY_CART_CONTAINER_SIZE_1 = ContainerTypes.<CartContainer>register("supply_cart_inventory_size_1", CartContainer::createSize1);
    public static final RegistryObject<MenuType<CartContainer>> SUPPLY_CART_CONTAINER_SIZE_2 = ContainerTypes.<CartContainer>register("supply_cart_inventory_size_2", CartContainer::createSize2);
    public static final RegistryObject<MenuType<CartContainer>> SUPPLY_CART_CONTAINER_SIZE_3 = ContainerTypes.<CartContainer>register("supply_cart_inventory_size_3", CartContainer::createSize3);
    public static final RegistryObject<MenuType<CartContainer>> SUPPLY_CART_CONTAINER_SIZE_4 = ContainerTypes.<CartContainer>register("supply_cart_inventory_size_4", CartContainer::createSize4);
    public static final RegistryObject<MenuType<CartContainer>> SUPPLY_CART_CONTAINER_SIZE_5 = ContainerTypes.<CartContainer>register("supply_cart_inventory_size_5", CartContainer::createSize5);
    public static final RegistryObject<MenuType<CartContainer>> SUPPLY_CART_CONTAINER_SIZE_6 = ContainerTypes.<CartContainer>register("supply_cart_inventory_size_6", CartContainer::createSize6);
    public static final RegistryObject<MenuType<CartContainer>> SUPPLY_CART_CONTAINER_SIZE_7 = ContainerTypes.<CartContainer>register("supply_cart_inventory_size_7", CartContainer::createSize7);
    public static final RegistryObject<MenuType<CartContainer>> SUPPLY_CART_CONTAINER_SIZE_8 = ContainerTypes.<CartContainer>register("supply_cart_inventory_size_8", CartContainer::createSize8);
    public static final RegistryObject<MenuType<CartContainer>> SUPPLY_CART_CONTAINER_SIZE_9 = ContainerTypes.<CartContainer>register("supply_cart_inventory_size_9", CartContainer::createSize9);
    public static final RegistryObject<MenuType<CartContainer>> SUPPLY_CART_CONTAINER_SIZE_10 = ContainerTypes.<CartContainer>register("supply_cart_inventory_size_10", CartContainer::createSize10);
    public static final RegistryObject<MenuType<CartContainer>> SUPPLY_CART_CONTAINER_SIZE_11 = ContainerTypes.<CartContainer>register("supply_cart_inventory_size_11", CartContainer::createSize11);
    public static final RegistryObject<MenuType<CartContainer>> SUPPLY_CART_CONTAINER_SIZE_12 = ContainerTypes.<CartContainer>register("supply_cart_inventory_size_12", CartContainer::createSize12);
    public static final RegistryObject<MenuType<CartContainer>> SUPPLY_CART_CONTAINER_SIZE_13 = ContainerTypes.<CartContainer>register("supply_cart_inventory_size_13", CartContainer::createSize13);

    private static <C extends AbstractContainerMenu> RegistryObject<MenuType<C>> register(String name, IContainerFactory<C> factory)
    {
        return RegistrationHelpers.registerContainer(CONTAINERS, name, factory);
    }
}
