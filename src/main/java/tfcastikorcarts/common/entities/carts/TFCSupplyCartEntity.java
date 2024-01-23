package tfcastikorcarts.common.entities.carts;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import de.mennomax.astikorcarts.config.AstikorCartsConfig;
import de.mennomax.astikorcarts.entity.AbstractDrawnInventoryEntity;
import de.mennomax.astikorcarts.inventory.container.SupplyCartContainer;
import de.mennomax.astikorcarts.util.CartItemStackHandler;
import net.dries007.tfc.common.TFCEffects;
import net.dries007.tfc.common.capabilities.size.IItemSize;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.util.Helpers;
import tfcastikorcarts.TFCAstikorCarts;
import tfcastikorcarts.config.TFCAstikorCartsConfig;
import tfcastikorcarts.util.AstikorHelpers;

public class TFCSupplyCartEntity extends AbstractDrawnInventoryEntity implements Container, ISlotCallback
{
    public static boolean isValid(ItemStack stack)
    {
        return ItemSizeManager.get(stack).getSize(stack).isEqualOrSmallerThan(TFCAstikorCartsConfig.COMMON.maxItemSize.get());
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return isValid(stack);
    }

    public final Supplier<? extends Item> drop;
    public static final ImmutableList<EntityDataAccessor<ItemStack>> CARGO = ImmutableList.of(
        SynchedEntityData.defineId(TFCSupplyCartEntity.class, EntityDataSerializers.ITEM_STACK),
        SynchedEntityData.defineId(TFCSupplyCartEntity.class, EntityDataSerializers.ITEM_STACK),
        SynchedEntityData.defineId(TFCSupplyCartEntity.class, EntityDataSerializers.ITEM_STACK),
        SynchedEntityData.defineId(TFCSupplyCartEntity.class, EntityDataSerializers.ITEM_STACK));

    public TFCSupplyCartEntity(final EntityType<? extends Entity> type, final Level world, Supplier<? extends Item> drop)
    {
        super(type, world);
        this.drop = drop;
    }

    @Override
    public AstikorCartsConfig.CartConfig getConfig()
    {
        return AstikorCartsConfig.get().supplyCart;
    }

    @Override
    public void pulledTick()
    {
        super.pulledTick();
        if (!this.level().isClientSide)
        {
            Player player = null;
            if (this.getPulling() instanceof Player pl)
            {
                player = pl;
            }
            else if (this.getPulling().getControllingPassenger() instanceof Player pl)
            {
                player = pl;
            }
            if (player != null)
            {
                float weightFactor = countOverburdened();
                if (weightFactor > TFCAstikorCartsConfig.COMMON.exhaustedThreshold.get())
                {
                    player.addEffect(Helpers.getExhausted(false));
                }
                if (weightFactor > TFCAstikorCartsConfig.COMMON.overburdenedThreshold.get())
                {
                    player.addEffect(Helpers.getOverburdened(false));
                }
                if (weightFactor > TFCAstikorCartsConfig.COMMON.pinnedThreshold.get())
                {
                    player.addEffect(new MobEffectInstance(TFCEffects.EXHAUSTED.get(), 25, 0, false, false));
                }
            }
        }
    }

    public float countOverburdened()
    {
        float count = 0;
        for (int i = 0; i < this.inventory.getSlots(); i++)
        {
            final ItemStack stack = this.inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                IItemSize size = ItemSizeManager.get(stack);
                float weightFactor = AstikorHelpers.getWeightFactor(size.getWeight(stack), size.getSize(stack));
                count += weightFactor;
            }
        }
        return count;
    }

    @Override
    public ItemStackHandler initInventory()
    {
        return new CartItemStackHandler<TFCSupplyCartEntity>(54, this)
        {
            @Override
            public void onLoad()
            {
                super.onLoad();
                this.onContentsChanged(0);
            }

            @Override
            public void onContentsChanged(final int slot)
            {
                final Object2IntMap<Item> totals = new Object2IntLinkedOpenHashMap<>();
                final Object2ObjectMap<Item, ItemStack> stacks = new Object2ObjectOpenHashMap<>();
                for (int i = 0; i < this.getSlots(); i++)
                {
                    final ItemStack stack = this.getStackInSlot(i);
                    if (!stack.isEmpty() && isValid(stack))
                    {
                        totals.mergeInt(stack.getItem(), 1, Integer::sum);
                        stacks.putIfAbsent(stack.getItem(), stack);
                    }
                }
                final Iterator<Object2IntMap.Entry<Item>> topTotals = totals.object2IntEntrySet().stream()
                    .sorted(Comparator.<Object2IntMap.Entry<Item>>comparingInt(e -> e.getKey() instanceof BlockItem ? 0 : 1)
                        .thenComparingInt(e -> -e.getIntValue()))
                    .limit(CARGO.size()).iterator();
                final ItemStack[] items = new ItemStack[CARGO.size()];
                Arrays.fill(items, ItemStack.EMPTY);
                final int forth = this.getSlots() / CARGO.size();
                for (int pos = 0; topTotals.hasNext() && pos < CARGO.size(); )
                {
                    final Object2IntMap.Entry<Item> entry = topTotals.next();
                    final int count = Math.max(1, (entry.getIntValue() + forth / 2) / forth);
                    for (int n = 1; n <= count && pos < CARGO.size(); n++)
                    {
                        final ItemStack stack = stacks.getOrDefault(entry.getKey(), ItemStack.EMPTY).copy();
                        if (isValid(stack))
                        {
                            stack.setCount(Math.min(stack.getMaxStackSize(), entry.getIntValue() / n));
                            items[pos++] = stack;
                        }
                    }
                }
                for (int i = 0; i < CARGO.size(); i++)
                {
                    this.cart.getEntityData().set(CARGO.get(i), items[i]);
                }
            }
        };
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand)
    {
        if (player.isSecondaryUseActive())
        {
            this.openContainer(player);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        final InteractionResult bannerResult = this.useBanner(player, hand);
        if (bannerResult.consumesAction())
        {
            return bannerResult;
        }
        final ItemStack held = player.getItemInHand(hand);
        if (this.hasJukebox())
        {
            if (this.level().isClientSide) return InteractionResult.SUCCESS;
            if (held.getItem() instanceof RecordItem && this.insertDisc(player, held) || this.ejectDisc(player))
            {
                return InteractionResult.CONSUME;
            }
            else
            {
                return InteractionResult.FAIL;
            }
        }
        if (this.isVehicle())
        {
            return InteractionResult.PASS;
        }
        if (!this.level().isClientSide)
        {
            return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
        }
        if (!isValid(held))
        {
            return InteractionResult.FAIL;
        }
        return InteractionResult.SUCCESS;
    }

    public boolean insertDisc(final Player player, final ItemStack held)
    {
        for (int i = 0; i < this.inventory.getSlots(); i++)
        {
            final ItemStack stack = this.inventory.getStackInSlot(i);
            if (DiscTag.insert(stack, held))
            {
                this.inventory.setStackInSlot(i, stack);
                this.getServer().overworld().getLevel().getChunkSource().broadcastAndSend(this, new ClientboundSetEntityDataPacket(this.getId(), this.entityData.packDirty()));
                this.level().broadcastEntityEvent(this, (byte) 5);
                if (!player.getAbilities().instabuild) held.shrink(1);
                    return true;
            }
        }
        return false;
    }

    public boolean ejectDisc(final Player player)
    {
        for (int i = 0; i < this.inventory.getSlots(); i++)
        {
            final ItemStack stack = this.inventory.getStackInSlot(i);
            final DiscTag record = DiscTag.get(stack);
            if (record.eject(player))
            {
                this.inventory.setStackInSlot(i, stack);
                return true;
            }
        }
        return false;
    }

    public boolean hasJukebox()
    {
        for (final EntityDataAccessor<ItemStack> slot : CARGO)
        {
            final ItemStack cargo = this.entityData.get(slot);
            if (cargo.getItem() == Items.JUKEBOX) return true;
        }
        return false;
    }

    public ItemStack getDisc()
    {
        for (final EntityDataAccessor<ItemStack> slot : CARGO) 
        {
            final ItemStack disc = DiscTag.get(this.entityData.get(slot)).disc;
            if (!disc.isEmpty()) return disc;
        }
        return ItemStack.EMPTY;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(final byte id)
    {
        if (id == 5)
        {
            /*for (final EntityDataAccessor<ItemStack> slot : CARGO) {
                final ItemStack disc = DiscTag.get(this.entityData.get(slot)).disc;
                if (!disc.isEmpty()) {
                    CartingJukeboxSound.play(this, disc);
                    break;
                }
            }*/
        }
        else
        {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public double getPassengersRidingOffset()
    {
        return 11.0D / 16.0D;
    }

    @Override
    public void positionRider(final Entity passenger, MoveFunction pCallback)
    {
        if (this.hasPassenger(passenger))
        {
            final Vec3 forward = this.getLookAngle();
            final Vec3 origin = new Vec3(0.0D, this.getPassengersRidingOffset(), 1.0D / 16.0D);
            final Vec3 pos = origin.add(forward.scale(-0.68D));
            passenger.setPos(this.getX() + pos.x, this.getY() + pos.y - 0.1D + passenger.getMyRidingOffset(), this.getZ() + pos.z);
            passenger.setYBodyRot(this.getYRot() + 180.0F);
            final float f2 = Mth.wrapDegrees(passenger.getYRot() - this.getYRot() + 180.0F);
            final float f1 = Mth.clamp(f2, -105.0F, 105.0F);
            passenger.yRotO += f1 - f2;
            passenger.setYRot(passenger.getYRot() + (f1 - f2));
            passenger.setYHeadRot(passenger.getYRot());
        }
    }

    public NonNullList<ItemStack> getCargo()
    {
        final NonNullList<ItemStack> cargo = NonNullList.withSize(CARGO.size(), ItemStack.EMPTY);
        for (int i = 0; i < CARGO.size(); i++)
        {
            cargo.set(i, this.entityData.get(CARGO.get(i)));
        }
        return cargo;
    }

    @Override
    public Item getCartItem()
    {
        return drop.get();
    }

    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        for (final EntityDataAccessor<ItemStack> parameter : CARGO)
        {
            this.entityData.define(parameter, ItemStack.EMPTY);
        }
    }

    public void openContainer(final Player player)
    {
        if (!this.level().isClientSide)
        {
            player.openMenu(new SimpleMenuProvider((id, inv, plyr) -> {
                return new SupplyCartContainer(id, inv, this);
            }, this.getDisplayName()));
        }
    }

    @Override
    public int getContainerSize()
    {
        return this.inventory.getSlots();
    }

    @Override
    public boolean isEmpty()
    {
        for(int i =0; i < this.inventory.getSlots(); i++)
        {
            ItemStack itemstack = this.inventory.getStackInSlot(i);
            if (!itemstack.isEmpty())
            {
                return false;
            }
        }
        return true;
    }
    @Override
    public @NotNull ItemStack getItem(int slot)
    {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount)
    {
        return inventory.extractItem(slot, amount, false);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot)
    {
        return inventory.extractItem(slot, 64, true);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack)
    {
        if (isValid(stack))
        {
            inventory.setStackInSlot(slot, stack);
        }
    }

    @Override
    public void setChanged()
    {
    }

    @Override
    public boolean stillValid(@NotNull Player player)
    {
        return true;
    }

    @Override
    public void clearContent()
    {
        for(int i =0; i < this.inventory.getSlots(); i++)
        {
            removeItemNoUpdate(i);
        }
    }

    static class DiscTag
    {
        static final DiscTag EMPTY = new DiscTag(ItemStack.EMPTY, new CompoundTag(), new CompoundTag(), ItemStack.EMPTY);

        final ItemStack stack;
        final CompoundTag nbt, tag;
        final ItemStack disc;

        DiscTag(final ItemStack stack, final CompoundTag nbt, final CompoundTag tag, final ItemStack disc)
        {
            this.stack = stack;
            this.nbt = nbt;
            this.tag = tag;
            this.disc = disc;
        }

        boolean isEmpty()
        {
            return this.stack.isEmpty();
        }

        boolean eject(final Player player)
        {
            if (this.isEmpty()) return false;
            this.tag.remove("RecordItem");
            if (this.tag.isEmpty()) this.nbt.remove("BlockEntityTag");
            if (this.nbt.contains("display", Tag.TAG_COMPOUND))
            {
                final CompoundTag display = this.nbt.getCompound("display");
                if (display.contains("Lore", Tag.TAG_LIST))
                {
                    final ListTag lore = display.getList("Lore", Tag.TAG_STRING);
                    final String descKey = this.disc.getItem().getDescriptionId() + ".desc";
                    for (int i = lore.size(); i --> 0; )
                    {
                        final String s = lore.getString(i);
                        final MutableComponent component;
                        try
                        {
                            component = Component.Serializer.fromJson(s);
                        }
                        catch (final JsonParseException ignored)
                        {
                            continue;
                        }
                        if (component.getContents() instanceof TranslatableContents translatable && descKey.equals(translatable.getKey()))
                        {
                            lore.remove(i);
                        }
                    }
                }
            }
            if (this.nbt.isEmpty()) this.stack.setTag(null);
            ItemHandlerHelper.giveItemToPlayer(player, this.disc, player.getInventory().selected);
            return true;
        }

        static DiscTag get(final ItemStack stack)
        {
            if (stack.getItem() != Items.JUKEBOX) return EMPTY;
            final CompoundTag nbt = stack.getTag();
            if (nbt == null || !nbt.contains("BlockEntityTag", Tag.TAG_COMPOUND)) return EMPTY;
            final CompoundTag tag = nbt.getCompound("BlockEntityTag");
            if (!tag.contains("RecordItem", Tag.TAG_COMPOUND)) return EMPTY;
            return new DiscTag(stack, nbt, tag, ItemStack.of(tag.getCompound("RecordItem")));
        }

        static boolean insert(final ItemStack stack, final ItemStack disc)
        {
            if (stack.getItem() != Items.JUKEBOX) return false;
            final CompoundTag tag = stack.getOrCreateTagElement("BlockEntityTag");
            if (tag.contains("RecordItem", Tag.TAG_COMPOUND)) return false;
            tag.put("RecordItem", disc.save(new CompoundTag()));
            final CompoundTag display = stack.getOrCreateTagElement("display");
            final ListTag lore = display.getList("Lore", Tag.TAG_STRING);
            lore.add(StringTag.valueOf(Component.Serializer.toJson(Component.translatable(disc.getDescriptionId() + ".desc"))));
            display.put("Lore", lore);
            return true;
        }
    }
}