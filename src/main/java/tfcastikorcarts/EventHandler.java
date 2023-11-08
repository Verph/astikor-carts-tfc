package tfcastikorcarts;

import de.mennomax.astikorcarts.world.AstikorWorld;
import de.mennomax.astikorcarts.world.SimpleAstikorWorld;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
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
    }

    public static void onWorldTick(TickEvent.LevelTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            AstikorWorld.get(event.level).ifPresent(AstikorWorld::tick);
        }
    }
}
