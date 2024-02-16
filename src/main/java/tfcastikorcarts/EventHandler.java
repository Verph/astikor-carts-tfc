package tfcastikorcarts;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import de.mennomax.astikorcarts.world.AstikorWorld;
import de.mennomax.astikorcarts.world.SimpleAstikorWorld;

import tfcastikorcarts.common.entities.carts.TFCPostilionEntity;

import static tfcastikorcarts.TFCAstikorCarts.MOD_ID;

public class EventHandler
{
    public static void init()
    {
        final IEventBus bus = MinecraftForge.EVENT_BUS;

        bus.addGenericListener(Level.class, EventHandler::attachCapabilities);
        bus.addListener(EventHandler::onEntityInteract);
        bus.addListener(EventHandler::onWorldTick);
    }

    public static void attachCapabilities(AttachCapabilitiesEvent<Level> event)
    {
        event.addCapability(new ResourceLocation(MOD_ID, "astikor"), AstikorWorld.createProvider(SimpleAstikorWorld::new));
    }

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event)
    {
        final Entity rider = event.getTarget().getControllingPassenger();
        if (rider instanceof TFCPostilionEntity)
        {
            rider.stopRiding();
        }

        /*final Player player = event.getEntity();
        final Level level = player.level();

        if (player.isSpectator() || !player.isCrouching() || level.isClientSide())
        {
            event.setCancellationResult(InteractionResult.PASS);
        }
        ItemStack stack = player.getItemInHand(event.getHand());
        if (stack.getItem() instanceof LeadItem && event.getTarget() instanceof Mob mob)
        {
            Vec3 pos = mob.position();
            Vec3 min = pos.add(-7, -7, -7);
            Vec3 max = pos.add(7, 7, 7);
            List<Mob> entities = level.getEntitiesOfClass(Mob.class, new AABB(min, max));
            boolean handled = false;

            for (Mob selected : entities)
            {
                if (selected.getLeashHolder() != player)
                    continue;

                selected.setLeashedTo(mob, true);
                handled = true;
            }
            if (handled)
            {
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
        event.setCancellationResult(InteractionResult.PASS);*/
    }

    public static void onWorldTick(TickEvent.LevelTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            AstikorWorld.get(event.level).ifPresent(AstikorWorld::tick);
        }
    }
}
