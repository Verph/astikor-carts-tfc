package tfcastikorcarts.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import de.mennomax.astikorcarts.util.CartWheel;

import tfcastikorcarts.config.TFCAstikorCartsConfig;

@Mixin(AbstractDrawnEntity.class)
public abstract class AbstractDrawnEntityMixin extends Entity
{    
    @Shadow protected List<CartWheel> wheels;
    @Shadow protected double spacing = 1.7D;
    @Shadow public Entity pulling;
    @Shadow protected AbstractDrawnEntity drawn;

    public AbstractDrawnEntityMixin(EntityType<?> entityType, Level level)
    {
        super(entityType, level);
    }

    @Overwrite(remap = false)
    public void pulledTick()
    {
        if (this.pulling == null)
        {
            return;
        }
        Vec3 targetVec = this.getRelativeTargetVec(1.0F);
        this.handleRotation(targetVec);
        while (this.getYRot() - this.yRotO < -180.0F)
        {
            this.yRotO -= 360.0F;
        }
        while (this.getYRot() - this.yRotO >= 180.0F)
        {
            this.yRotO += 360.0F;
        }
        if (this.pulling.onGround())
        {
            targetVec = new Vec3(targetVec.x, 0.0D, targetVec.z);
        }
        final double targetVecLength = targetVec.length();
        final double r = 0.2D;
        final double relativeSpacing = Math.max(this.spacing + 0.5D * this.pulling.getBbWidth(), 1.0D);
        final double diff = targetVecLength - relativeSpacing;
        final Vec3 move;
        if (Math.abs(diff) < r)
        {
            move = this.getDeltaMovement();
        }
        else
        {
            move = this.getDeltaMovement().add(targetVec.subtract(targetVec.normalize().scale(relativeSpacing + r * Math.signum(diff))));
        }
        this.onGround();
        final double startX = this.getX();
        final double startY = this.getY();
        final double startZ = this.getZ();
        this.move(MoverType.SELF, move);
        if (!this.isAlive())
        {
            return;
        }
        this.addStats(this.getX() - startX, this.getY() - startY, this.getZ() - startZ);
        if (this.level().isClientSide)
        {
            for (final CartWheel wheel : this.wheels)
            {
                wheel.tick();
            }
        }
        else
        {
            targetVec = this.getRelativeTargetVec(1.0F);
            if (targetVec.length() > relativeSpacing + 1.0D + TFCAstikorCartsConfig.COMMON.pullingDistanceModifier.get())
            {
                this.setPulling(null);
            }
        }
        this.updatePassengers();
        if (this.drawn != null)
        {
            this.drawn.pulledTick();
        }
    }

    @Shadow
    public Vec3 getRelativeTargetVec(final float delta)
    {
        return new Vec3(0, 0, 0);
    }

    @Shadow
    public void handleRotation(final Vec3 target) {}

    @Shadow
    private void addStats(final double x, final double y, final double z) {}

    @Shadow
    public void setPulling(final Entity entityIn) {}

    @Shadow
    public void updatePassengers() {}
}
