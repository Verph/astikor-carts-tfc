package tfcastikorcarts.common.container;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import tfcastikorcarts.common.container.SupplyCartContainer.RestrictedSlotItemHandler;
import tfcastikorcarts.common.entities.carts.TFCSupplyCartEntity;

public class CartContainer extends AbstractContainerMenu 
{
    public static CartContainer createSize0(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerList.SIZE_0, windowId, playerInv);
    }

    public static CartContainer createSize1(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerList.SIZE_1, windowId, playerInv);
    }

    public static CartContainer createSize2(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerList.SIZE_2, windowId, playerInv);
    }

    public static CartContainer createSize3(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerList.SIZE_3, windowId, playerInv);
    }

    public static CartContainer createSize4(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerList.SIZE_4, windowId, playerInv);
    }

    public static CartContainer createSize5(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerList.SIZE_5, windowId, playerInv);
    }

    public static CartContainer createSize6(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerList.SIZE_6, windowId, playerInv);
    }

    public static CartContainer createSize7(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerList.SIZE_7, windowId, playerInv);
    }

    public static CartContainer createSize8(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerList.SIZE_8, windowId, playerInv);
    }

    public static CartContainer createSize9(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerList.SIZE_9, windowId, playerInv);
    }

    public static CartContainer createSize10(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerList.SIZE_10, windowId, playerInv);
    }

    public static CartContainer createSize11(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerList.SIZE_11, windowId, playerInv);
    }

    public static CartContainer createSize12(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerList.SIZE_12, windowId, playerInv);
    }

    public static CartContainer createSize13(int windowId, Inventory playerInv, FriendlyByteBuf data)
    {
        return new CartContainer(ContainerList.SIZE_13, windowId, playerInv);
    }

    public final boolean allowAddSlot;
    public final ContainerList type;
    public final Container container;

    public CartContainer(ContainerList type, int id, Inventory inv)
    {
        this(type, id, inv, new SimpleContainer(type.getSize()));
    }

    public CartContainer(ContainerList type, int id, Inventory playerInv, Container container)
    {
        super(type.getMenuType(), id);
        checkContainerSize(container, type.getSize());
        container.startOpen(playerInv.player);
        this.type = type;
        this.container = container;
        this.allowAddSlot = true;
        final int columns = type.getColumns();
        final int rows = type.getRows();

        // Container
        if (type.getSize() <= 1)
        {
            this.addSlot(new RestrictedSlot(container, 0, 84, 44));
        }
        else
        {
            for (int chestRow = 0; chestRow < rows; chestRow++)
            {
                for (int chestCol = 0; chestCol < columns; chestCol++)
                {
                    this.addSlot(new RestrictedSlot(container, chestCol + chestRow * columns, 12 + chestCol * 18, 18 + chestRow * 18));
                }
            }
        }

        // Player Inventory + Hotbar
        int leftCol = (type.xSize - 162) / 2 + 1;

        for (int playerInvRow = 0; playerInvRow < 3; playerInvRow++)
        {
            for (int playerInvCol = 0; playerInvCol < 9; playerInvCol++)
            {
                this.addSlot(new Slot(playerInv, playerInvCol + playerInvRow * 9 + 9, leftCol + playerInvCol * 18, type.ySize - (4 - playerInvRow) * 18 - 10));
            }
        }

        for (int hotbarCol = 0; hotbarCol < 9; hotbarCol++)
        {
            this.addSlot(new Slot(playerInv, hotbarCol, leftCol + hotbarCol * 18, type.ySize - 24));
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

        if (slot != null && slot.hasItem())
        {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();

            if (index < this.type.getSize())
            {
                if (!this.moveItemStackTo(slotStack, this.type.getSize(), this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(slotStack, 0, this.type.getSize(), false))
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
        return stack;
    }

    @Override
    public Slot addSlot(Slot slot)
    {
        return allowAddSlot ? super.addSlot(slot) : slot;
    }

    @Override
    public boolean stillValid(final Player player)
    {
        return this.container.stillValid(player);
    }

    public ContainerList getContainerType()
    {
        return type;
    }

    public static class RestrictedSlot extends Slot
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
