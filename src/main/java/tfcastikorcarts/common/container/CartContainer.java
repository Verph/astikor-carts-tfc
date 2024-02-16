package tfcastikorcarts.common.container;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import tfcastikorcarts.common.entities.carts.TFCSupplyCartEntity;

public class CartContainer extends ChestMenu
{
    public static CartContainer createSize0(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_0.get(), windowId, playerInv, ContainerList.SIZE_0);
    }

    public static CartContainer createSize1(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_1.get(), windowId, playerInv, ContainerList.SIZE_1);
    }

    public static CartContainer createSize2(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_2.get(), windowId, playerInv, ContainerList.SIZE_2);
    }

    public static CartContainer createSize3(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_3.get(), windowId, playerInv, ContainerList.SIZE_3);
    }

    public static CartContainer createSize4(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_4.get(), windowId, playerInv, ContainerList.SIZE_4);
    }

    public static CartContainer createSize5(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_5.get(), windowId, playerInv, ContainerList.SIZE_5);
    }

    public static CartContainer createSize6(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_6.get(), windowId, playerInv, ContainerList.SIZE_6);
    }

    public static CartContainer createSize7(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_7.get(), windowId, playerInv, ContainerList.SIZE_7);
    }

    public static CartContainer createSize8(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_8.get(), windowId, playerInv, ContainerList.SIZE_8);
    }

    public static CartContainer createSize9(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_9.get(), windowId, playerInv, ContainerList.SIZE_9);
    }

    public static CartContainer createSize10(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_10.get(), windowId, playerInv, ContainerList.SIZE_10);
    }

    public static CartContainer createSize11(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_11.get(), windowId, playerInv, ContainerList.SIZE_11);
    }

    public static CartContainer createSize12(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_12.get(), windowId, playerInv, ContainerList.SIZE_12);
    }

    public static CartContainer createSize13(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_13.get(), windowId, playerInv, ContainerList.SIZE_13);
    }

    // Default value of false when checked via super(), which calls addSlot()
    // Only set to true to allow this constructor to add slots.
    public final boolean allowAddSlot;
    public final ContainerList containerType;

    public CartContainer(MenuType<?> type, int id, Inventory inv, ContainerList containerType)
    {
        this(type, id, inv, new SimpleContainer(containerType.getSize()), containerType);
    }

    public CartContainer(MenuType<?> type, int id, Inventory playerInv, Container container, ContainerList containerType)
    {
        super(type, id, playerInv, container, containerType.getRows());
        checkContainerSize(container, containerType.getSize());
        container.startOpen(playerInv.player);
        this.containerType = containerType;

        allowAddSlot = true;

        /* 
         * TODO: Fix this shite!
         */
        // Container
        if (containerType.getSize() <= 1)
        {
            this.addSlot(new RestrictedSlot(playerInv, 0, 12 + 4 * 18, 8 + 2 * 18));
        }
        else
        {
            for (int chestRow = 0; chestRow < containerType.getRows(); chestRow++)
            {
                for (int chestCol = 0; chestCol < containerType.getColumns(); chestCol++)
                {
                    this.addSlot(new RestrictedSlot(playerInv, chestCol + chestRow * containerType.getColumns(), 12 + chestCol * 18, 18 + chestRow * 18));
                }
            }
        }

        // Player Inventory + Hotbar
        int leftCol = (containerType.xSize - 162) / 2 + 1;

        for (int playerInvRow = 0; playerInvRow < 3; playerInvRow++)
        {
            for (int playerInvCol = 0; playerInvCol < 9; playerInvCol++)
            {
                this.addSlot(new Slot(playerInv, playerInvCol + playerInvRow * 9 + 9, leftCol + playerInvCol * 18, containerType.ySize - (4 - playerInvRow) * 18 - 10));
            }
        }

        for (int hotbarCol = 0; hotbarCol < 9; hotbarCol++)
        {
            this.addSlot(new Slot(playerInv, hotbarCol, leftCol + hotbarCol * 18, containerType.ySize - 24));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot instanceof RestrictedSlot rest && slot.hasItem())
        {
            ItemStack item = slot.getItem();
            if (!rest.mayPlace(item))
            {
                return ItemStack.EMPTY;
            }
        }
        else
        {
            if (slot != null && slot.hasItem())
            {
                ItemStack slotStack = slot.getItem();
                stack = slotStack.copy();

                if (index < this.containerType.size)
                {
                    if (!this.moveItemStackTo(slotStack, this.containerType.size, this.slots.size(), true))
                    {
                    return ItemStack.EMPTY;
                    }
                }
                else if (!this.moveItemStackTo(slotStack, 0, this.containerType.size, false))
                {
                    return ItemStack.EMPTY;
                }

                if (slotStack.isEmpty())
                {
                    slot.set(ItemStack.EMPTY);
                }
                else
                {
                    slot.setChanged();
                }
            }
        }
        return stack;
    }

    @Override
    protected Slot addSlot(Slot slot)
    {
        return allowAddSlot ? super.addSlot(slot) : slot;
    }

    public ContainerList getContainerType()
    {
        return containerType;
    }

    private static class RestrictedSlot extends Slot
    {
        public RestrictedSlot(Container container, int slot, int x, int y)
        {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return super.mayPlace(stack) && TFCSupplyCartEntity.isValid(stack);
        }
    }
}
