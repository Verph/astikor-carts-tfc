package tfcastikorcarts.common.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import de.mennomax.astikorcarts.entity.AbstractDrawnInventoryEntity;
import de.mennomax.astikorcarts.inventory.container.CartContainer;

import tfcastikorcarts.common.entities.carts.TFCSupplyCartEntity;

public class SupplyCartContainer extends CartContainer
{
    public final boolean allowAddSlot;
    public final ContainerList type;
    public final AbstractDrawnInventoryEntity cart;
    public final ItemStackHandler cartInv;

    public SupplyCartContainer(ContainerList type, int id, Inventory playerInv, AbstractDrawnInventoryEntity cart)
    {
        super(type.getMenuType(), id, cart);
        this.type = type;
        this.allowAddSlot = true;
        this.cart = cart;
        this.cartInv = cart.inventory;
        final int columns = type.getColumns();
        final int rows = type.getRows();

        // Container
        if (type.getSize() <= 1)
        {
            this.addSlot(new RestrictedSlotItemHandler(this.cartInv, 0, 84, 44));
        }
        else
        {
            for (int chestRow = 0; chestRow < rows; chestRow++)
            {
                for (int chestCol = 0; chestCol < columns; chestCol++)
                {
                    this.addSlot(new RestrictedSlotItemHandler(this.cartInv, chestCol + chestRow * columns, 12 + chestCol * 18, 18 + chestRow * 18));
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
    public boolean stillValid(final Player playerIn)
    {
        return this.cart.isAlive() && this.cart.distanceTo(playerIn) < 8.0F;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot instanceof RestrictedSlotItemHandler rest && slot.hasItem())
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

            if (index < this.cartInv.getSlots())
            {
                if (!this.moveItemStackTo(slotStack, this.cartInv.getSlots(), this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(slotStack, 0, this.cartInv.getSlots(), false))
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

    public static class RestrictedSlotItemHandler extends SlotItemHandler
    {
        public RestrictedSlotItemHandler(IItemHandler container, int slot, int x, int y)
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
