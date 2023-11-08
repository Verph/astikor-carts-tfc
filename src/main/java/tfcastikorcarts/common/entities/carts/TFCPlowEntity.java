package tfcastikorcarts.common.entities.carts;

import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import de.mennomax.astikorcarts.config.AstikorCartsConfig;
import de.mennomax.astikorcarts.entity.AbstractDrawnInventoryEntity;
import de.mennomax.astikorcarts.inventory.container.PlowContainer;
import de.mennomax.astikorcarts.util.CartItemStackHandler;
import de.mennomax.astikorcarts.util.ProxyItemUseContext;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.devices.PlacedItemBlock;
import net.dries007.tfc.common.blocks.rock.LooseRockBlock;
import net.dries007.tfc.common.blocks.wood.ILeavesBlock;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import tfcastikorcarts.config.TFCAstikorCartsConfig;

public class TFCPlowEntity extends AbstractDrawnInventoryEntity implements ISlotCallback
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
    public static final int SLOT_COUNT = 3;
    public static final double BLADEOFFSET = 1.7D;
    public static final EntityDataAccessor<Boolean> PLOWING = SynchedEntityData.defineId(TFCPlowEntity.class, EntityDataSerializers.BOOLEAN);
    public static final ImmutableList<EntityDataAccessor<ItemStack>> TOOLS = ImmutableList.of(
        SynchedEntityData.defineId(TFCPlowEntity.class, EntityDataSerializers.ITEM_STACK),
        SynchedEntityData.defineId(TFCPlowEntity.class, EntityDataSerializers.ITEM_STACK),
        SynchedEntityData.defineId(TFCPlowEntity.class, EntityDataSerializers.ITEM_STACK));

    public TFCPlowEntity(final EntityType<? extends Entity> entityTypeIn, final Level worldIn, Supplier<? extends Item> drop) {
        super(entityTypeIn, worldIn);
        this.spacing = 1.3D;
        this.drop = drop;
    }

    @Override
    public AstikorCartsConfig.CartConfig getConfig() {
        return AstikorCartsConfig.get().plow;
    }

    @Override
    public ItemStackHandler initInventory() {
        return new CartItemStackHandler<TFCPlowEntity>(SLOT_COUNT, this) {
            @Override
            public void onLoad() {
                for (int i = 0; i < TOOLS.size(); i++) {
                    this.cart.getEntityData().set(TOOLS.get(i), this.getStackInSlot(i));
                }
            }

            @Override
            public void onContentsChanged(final int slot) {
                this.cart.updateSlot(slot);
            }
        };
    }

    public boolean getPlowing() {
        return this.entityData.get(PLOWING);
    }

    @Override
    public void pulledTick() {
        super.pulledTick();
        if (this.getPulling() == null) {
            return;
        }
        if (!this.level().isClientSide) {
            Player player = null;
            if (this.getPulling() instanceof Player pl) {
                player = pl;
            } else if (this.getPulling().getControllingPassenger() instanceof Player pl) {
                player = pl;
            }
            if (this.entityData.get(PLOWING) && player != null) {
                if (this.xo != this.getX() || this.zo != this.getZ()) {
                    this.plow(player);
                }
            }
        }
    }

    public void plow(final Player player)
    {
        for (int i = 0; i < SLOT_COUNT; i++)
        {
            final ItemStack stack = this.getStackInSlot(i);
            if (stack.getItem() instanceof TieredItem)
            {
                float offset = 38.0F - i * 38.0F;
                double blockPosX = this.getX() + Mth.sin((float) Math.toRadians(this.getYRot() - offset)) * BLADEOFFSET;
                double blockPosZ = this.getZ() - Mth.cos((float) Math.toRadians(this.getYRot() - offset)) * BLADEOFFSET;
                BlockPos blockPos = new BlockPos((int) blockPosX, (int) (this.getY() - 0.5D), (int) blockPosZ);

                if (Helpers.isItem(stack.getItem(), TagKey.create(Registries.ITEM, Helpers.identifier("scythes"))))
                {
                    final BlockPos upPos = blockPos.above();
                    final BlockPos upUpPos = upPos.above();
                    final BlockState blockStateAt = this.level().getBlockState(blockPos);
                    final BlockState blockStateAtUp = this.level().getBlockState(upPos);
                    final BlockState blockStateAtUpUp = this.level().getBlockState(upUpPos);
                    final Block blockAt = blockStateAt.getBlock();
                    final Block blockAtUp = blockStateAtUp.getBlock();
                    final Block blockAtUpUp = blockStateAtUpUp.getBlock();

                    if (blockAt instanceof BushBlock || blockAt instanceof GroundcoverBlock || blockAt instanceof LooseRockBlock || blockAt instanceof PlacedItemBlock || blockAt instanceof ILeavesBlock || Helpers.isBlock(blockAt, TFCTags.Blocks.MINEABLE_WITH_SCYTHE))
                    {
                        Block.dropResources(blockStateAt, level(), blockPos, blockStateAt.hasBlockEntity() ? level().getBlockEntity(blockPos) : null, player, player.getMainHandItem());
                        this.level().destroyBlock(blockPos, true);
                    }
                    if (blockAtUp instanceof BushBlock || blockAtUp instanceof GroundcoverBlock || blockAtUp instanceof LooseRockBlock || blockAtUp instanceof PlacedItemBlock || blockAtUp instanceof ILeavesBlock || Helpers.isBlock(blockAtUp, TFCTags.Blocks.MINEABLE_WITH_SCYTHE))
                    {
                        Block.dropResources(blockStateAtUp, level(), upPos, blockStateAtUp.hasBlockEntity() ? level().getBlockEntity(upPos) : null, player, player.getMainHandItem());
                        this.level().destroyBlock(upPos, true);
                    }
                    if (blockAtUpUp instanceof BushBlock || blockAtUpUp instanceof GroundcoverBlock || blockAtUpUp instanceof LooseRockBlock || blockAtUpUp instanceof PlacedItemBlock || blockAtUpUp instanceof ILeavesBlock || Helpers.isBlock(blockAtUpUp, TFCTags.Blocks.MINEABLE_WITH_SCYTHE))
                    {
                        Block.dropResources(blockStateAtUpUp, level(), upUpPos, blockStateAtUpUp.hasBlockEntity() ? level().getBlockEntity(upUpPos) : null, player, player.getMainHandItem());
                        this.level().destroyBlock(upUpPos, true);
                    }

                    final boolean damageable = stack.isDamageableItem();
                    final int count = stack.getCount();
                    stack.getItem().useOn(new ProxyItemUseContext(player, stack, new BlockHitResult(Vec3.ZERO, Direction.UP, blockPos, false)));
                    if (damageable && stack.getCount() < count)
                    {
                        this.playSound(SoundEvents.ITEM_BREAK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
                        this.updateSlot(i);
                    }
                }
                else if (Helpers.isItem(stack.getItem(), TFCTags.Items.KNIVES))
                {
                    final BlockPos upPos = blockPos.above();
                    final BlockPos upUpPos = upPos.above();
                    final Block blockAt = this.level().getBlockState(blockPos).getBlock();
                    final Block blockAtUp = this.level().getBlockState(upPos).getBlock();
                    final Block blockAtUpUp = this.level().getBlockState(upUpPos).getBlock();

                    if (blockAt instanceof BushBlock || blockAt instanceof GroundcoverBlock || blockAt instanceof LooseRockBlock || blockAt instanceof PlacedItemBlock || blockAt instanceof ILeavesBlock || Helpers.isBlock(blockAt, TFCTags.Blocks.MINEABLE_WITH_KNIFE))
                    {
                        this.level().destroyBlock(blockPos, true);
                    }
                    if (blockAtUp instanceof BushBlock || blockAtUp instanceof GroundcoverBlock || blockAtUp instanceof LooseRockBlock || blockAtUp instanceof PlacedItemBlock || blockAtUp instanceof ILeavesBlock || Helpers.isBlock(blockAtUp, TFCTags.Blocks.MINEABLE_WITH_KNIFE))
                    {
                        this.level().destroyBlock(upPos, true);
                    }
                    if (blockAtUpUp instanceof BushBlock || blockAtUpUp instanceof GroundcoverBlock || blockAtUpUp instanceof LooseRockBlock || blockAtUpUp instanceof PlacedItemBlock || blockAtUpUp instanceof ILeavesBlock || Helpers.isBlock(blockAtUpUp, TFCTags.Blocks.MINEABLE_WITH_KNIFE))
                    {
                        this.level().destroyBlock(upUpPos, true);
                    }

                    final boolean damageable = stack.isDamageableItem();
                    final int count = stack.getCount();
                    stack.getItem().useOn(new ProxyItemUseContext(player, stack, new BlockHitResult(Vec3.ZERO, Direction.UP, blockPos, false)));
                    if (damageable && stack.getCount() < count)
                    {
                        this.playSound(SoundEvents.ITEM_BREAK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
                        this.updateSlot(i);
                    }
                }
                else
                {
                    final BlockPos upPos = blockPos.above();
                    final BlockPos upUpPos = upPos.above();
                    final Block blockAt = this.level().getBlockState(blockPos).getBlock();
                    final Block blockAtUp = this.level().getBlockState(upPos).getBlock();
                    final Block blockAtUpUp = this.level().getBlockState(upUpPos).getBlock();

                    if (blockAt instanceof BushBlock || blockAt instanceof GroundcoverBlock || blockAt instanceof LooseRockBlock || blockAt instanceof PlacedItemBlock)
                    {
                        this.level().destroyBlock(blockPos, true);
                    }
                    if (blockAtUp instanceof BushBlock || blockAtUp instanceof GroundcoverBlock || blockAtUp instanceof LooseRockBlock || blockAtUp instanceof PlacedItemBlock)
                    {
                        this.level().destroyBlock(upPos, true);
                    }
                    if (blockAtUpUp instanceof BushBlock || blockAtUpUp instanceof GroundcoverBlock || blockAtUpUp instanceof LooseRockBlock || blockAtUpUp instanceof PlacedItemBlock)
                    {
                        this.level().destroyBlock(upUpPos, true);
                    }

                    final boolean damageable = stack.isDamageableItem();
                    final int count = stack.getCount();
                    stack.getItem().useOn(new ProxyItemUseContext(player, stack, new BlockHitResult(Vec3.ZERO, Direction.UP, blockPos, false)));
                    if (damageable && stack.getCount() < count)
                    {
                        this.playSound(SoundEvents.ITEM_BREAK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
                        this.updateSlot(i);
                    }
                }
            }
        }
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            this.openContainer(player);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        if (!this.level().isClientSide) {
            this.entityData.set(PLOWING, !this.entityData.get(PLOWING));
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    public void updateSlot(final int slot) {
        if (!this.level().isClientSide) {
            if (this.inventory.getStackInSlot(slot).isEmpty()) {
                this.entityData.set(TOOLS.get(slot), ItemStack.EMPTY);
            } else {
                this.entityData.set(TOOLS.get(slot), this.inventory.getStackInSlot(slot));
            }

        }
    }

    public ItemStack getStackInSlot(final int i) {
        return this.entityData.get(TOOLS.get(i));
    }

    @Override
    public Item getCartItem() {
        return drop.get();
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PLOWING, false);
        for (final EntityDataAccessor<ItemStack> param : TOOLS) {
            this.entityData.define(param, ItemStack.EMPTY);
        }
    }

    @Override
    public void readAdditionalSaveData(final CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(PLOWING, compound.getBoolean("Plowing"));
    }

    @Override
    public void addAdditionalSaveData(final CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Plowing", this.entityData.get(PLOWING));
    }

    public void openContainer(final Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer,
                new SimpleMenuProvider((windowId, playerInventory, p) -> new PlowContainer(windowId, playerInventory, this), this.getDisplayName()),
                buf -> buf.writeInt(this.getId())
            );
        }
    }
}