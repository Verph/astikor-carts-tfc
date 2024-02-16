package tfcastikorcarts.common.entities.carts;

import java.util.function.Supplier;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.config.AstikorCartsConfig;
import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;

import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.util.registry.RegistryWood;

import tfcastikorcarts.common.entities.AstikorEntities;
import tfcastikorcarts.config.TFCAstikorCartsConfig;

public class TFCAnimalCartEntity extends AbstractDrawnEntity implements ISlotCallback
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

    public final RegistryWood wood;
    public final Supplier<? extends Item> drop;
    public TFCPostilionEntity postilionEntity = null;

    public TFCAnimalCartEntity(final EntityType<? extends Entity> entityTypeIn, final Level worldIn, Supplier<? extends Item> drop, RegistryWood wood)
    {
        super(entityTypeIn, worldIn);
        this.drop = drop;
        this.wood = wood;
    }

    @Override
    public AstikorCartsConfig.CartConfig getConfig()
    {
        return AstikorCartsConfig.get().animalCart;
    }

    public TFCPostilionEntity getPostilionEntity(RegistryWood wood)
    {
        if (postilionEntity != null)
            return postilionEntity;

        if (wood != null)
            return AstikorEntities.POSTILION_TFC.get(wood).get().create(this.level());

        return null;
    }

    @Override
    public void tick()
    {
        super.tick();
        final Entity coachman = this.getControllingPassenger();
        final Entity pulling = this.getPulling();
        if (pulling != null && coachman != null && pulling.getControllingPassenger() == null)
        {
            final TFCPostilionEntity postilion = getPostilionEntity(wood);
            if (postilion != null)
            {
                postilion.moveTo(pulling.getX(), pulling.getY(), pulling.getZ(), coachman.getYRot(), coachman.getXRot());
                if (postilion.startRiding(pulling))
                {
                    this.level().addFreshEntity(postilion);
                }
                else
                {
                    postilion.discard();
                }
            }
        }
    }

    @Override
    public InteractionResult interact(final Player player, final InteractionHand hand)
    {
        if (player.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                for (final Entity entity : this.getPassengers()) {
                    if (!(entity instanceof Player)) {
                        entity.stopRiding();
                    }
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        final InteractionResult bannerResult = this.useBanner(player, hand);
        if (bannerResult.consumesAction()) {
            return bannerResult;
        }
        if (this.getPulling() != player) {
            if (!this.canAddPassenger(player)) {
                return InteractionResult.PASS;
            }
            if (!this.level().isClientSide) {
                return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void push(final Entity entityIn)
    {
        if (!entityIn.hasPassenger(this))
        {
            if (!this.level().isClientSide && this.getPulling() != entityIn && this.getControllingPassenger() == null && this.getPassengers().size() <= TFCAstikorCartsConfig.COMMON.maxAnimalSize.get() && !entityIn.isPassenger() && (entityIn.getBbWidth() < this.getBbWidth() || this.getPassengers().size() <= TFCAstikorCartsConfig.COMMON.maxAnimalSize.get()) && entityIn instanceof LivingEntity
                && canCarryWaterEntities(entityIn) && canPushIntoPlayers(entityIn))
            {
                entityIn.startRiding(this);
            }
            else
            {
                super.push(entityIn);
            }
        }
    }

    public static boolean canPushIntoPlayers(Entity entityIn)
    {
        if (entityIn instanceof Player)
            return TFCAstikorCartsConfig.COMMON.canPushIntoPlayers.get();
        else
            return true;
    }

    public static boolean canCarryWaterEntities(Entity entityIn)
    {
        if (entityIn instanceof WaterAnimal)
            return TFCAstikorCartsConfig.COMMON.canCarryWaterEntities.get();
        else
            return true;
    }

    @Override
    public boolean canAddPassenger(final Entity passenger)
    {
        return this.getPassengers().size() <= TFCAstikorCartsConfig.COMMON.maxAnimalSize.get();
    }

    @Override
    public double getPassengersRidingOffset() {
        return 11.0D / 16.0D;
    }


    @Override
    public void positionRider(Entity passenger, MoveFunction pCallback) {
        if (this.hasPassenger(passenger)) {
            double f = -0.1D;

            if (this.getPassengers().size() > 1) {
                f = this.getPassengers().indexOf(passenger) == 0 ? 0.2D : -0.6D;

                if (passenger instanceof Animal) {
                    f += 0.2D;
                }
            }

            final Vec3 forward = this.getLookAngle();
            final Vec3 origin = new Vec3(0.0D, this.getPassengersRidingOffset(), 1.0D / 16.0D);
            final Vec3 pos = origin.add(forward.scale(f + Mth.sin((float) Math.toRadians(this.getXRot())) * 0.7D));
            passenger.setPos(this.getX() + pos.x, this.getY() + pos.y + passenger.getMyRidingOffset(), this.getZ() + pos.z);
            passenger.setYBodyRot(this.getYRot());
            final float f2 = Mth.wrapDegrees(passenger.getYRot() - this.getYRot());
            final float f1 = Mth.clamp(f2, -105.0F, 105.0F);
            passenger.yRotO += f1 - f2;
            passenger.setYRot(passenger.getYRot() + (f1 - f2));
            passenger.setYHeadRot(passenger.getYRot());
            if (passenger instanceof Animal && this.getPassengers().size() > 1) {
                final int j = passenger.getId() % 2 == 0 ? 90 : 270;
                passenger.setYBodyRot(((Animal) passenger).yBodyRot + j);
                passenger.setYHeadRot(passenger.getYHeadRot() + j);
            }
        }
    }

    @Override
    public Item getCartItem() {
        return AstikorCarts.Items.ANIMAL_CART.get();
    }
}