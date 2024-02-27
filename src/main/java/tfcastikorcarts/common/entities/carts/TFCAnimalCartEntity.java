package tfcastikorcarts.common.entities.carts;

import java.util.function.Supplier;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
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

import de.mennomax.astikorcarts.config.AstikorCartsConfig;
import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;

import net.dries007.tfc.common.TFCEffects;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.util.Helpers;
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

    @Override
    public void pulledTick()
    {
        super.pulledTick();

        if (this.getPulling() == null)
        {
            return;
        }

        Player player = null;

        if (this.getPulling() instanceof Player pl)
        {
            player = pl;
        }
        else if (this.getPulling().getControllingPassenger() instanceof Player pl)
        {
            player = pl;
        }
        if (player != null && player.getFoodData() instanceof TFCFoodData foodData)
        {
            final double animalWeight = countOverburdened();
            final double healthFactor = TFCAstikorCartsConfig.COMMON.toggleFoodSpeed.get() ? Mth.map(foodData.getNutrition().getAverageNutrition(), 0D, 1.0D, 0.75D, 1.0D) * Mth.map(foodData.getThirst(), 0D, 100D, 0.5D, 1.0D) : 1.0D;
            final double speedFactor = Mth.clamp(healthFactor / animalWeight, 0D, 1.0D);
            player.setDeltaMovement(player.getDeltaMovement().multiply(speedFactor, 1.0D, speedFactor));

            if (animalWeight > 30D)
            {
                player.addEffect(new MobEffectInstance(TFCEffects.PINNED.get(), 25, 0, false, false));
            }
            else if (animalWeight > 20D)
            {
                player.addEffect(Helpers.getOverburdened(false));
            }
            else if (animalWeight > 10D)
            {
                player.addEffect(Helpers.getExhausted(false));
            }
        }
    }

    public float countOverburdened()
    {
        float count = 1f;
        if (this.getPassengers().size() > 0)
        {
            for (int i = 0; i < this.getPassengers().size(); i++)
            {
                count += this.getPassengers().get(i).getBoundingBox().getSize();
            }
        }
        return count;
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
        if (player.isSecondaryUseActive())
        {
            if (!this.level().isClientSide)
            {
                for (final Entity entity : this.getPassengers())
                {
                    if (!(entity instanceof Player))
                    {
                        entity.stopRiding();
                    }
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        final InteractionResult bannerResult = this.useBanner(player, hand);
        if (bannerResult.consumesAction())
        {
            return bannerResult;
        }
        if (this.getPulling() != player)
        {
            if (!this.canAddPassenger(player))
            {
                return InteractionResult.PASS;
            }
            if (!this.level().isClientSide)
            {
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
            final double animalSize = entityIn.getBoundingBox().getSize();
            final double maxAnimalSize = TFCAstikorCartsConfig.COMMON.maxAnimalSize.get();
            if (!this.level().isClientSide && this.getPulling() != entityIn && this.getControllingPassenger() == null && animalSize <= maxAnimalSize && !entityIn.isPassenger() && (entityIn.getBbWidth() < this.getBbWidth() || animalSize <= maxAnimalSize) && entityIn instanceof LivingEntity && canCarryWaterEntities(entityIn) && canPushIntoPlayers(entityIn))
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
        final double animalSize = passenger.getBoundingBox().getSize();
        final double maxAnimalSize = TFCAstikorCartsConfig.COMMON.maxAnimalSize.get();
        return animalSize <= maxAnimalSize && this.getPassengers().size() <= TFCAstikorCartsConfig.COMMON.maxPassengerCount.get();
    }

    @Override
    public double getPassengersRidingOffset()
    {
        return 11.0D / 16.0D;
    }


    @Override
    public void positionRider(Entity passenger, MoveFunction pCallback)
    {
        if (this.hasPassenger(passenger))
        {
            double f = -0.1D;

            if (this.getPassengers().size() > 1)
            {
                f = this.getPassengers().indexOf(passenger) == 0 ? 0.2D : -0.6D;

                if (passenger instanceof Animal)
                {
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
            if (passenger instanceof Animal && this.getPassengers().size() > 1)
            {
                final int j = passenger.getId() % 2 == 0 ? 90 : 270;
                passenger.setYBodyRot(((Animal) passenger).yBodyRot + j);
                passenger.setYHeadRot(passenger.getYHeadRot() + j);
            }
        }
    }

    @Override
    public Item getCartItem()
    {
        return drop.get();
    }
}