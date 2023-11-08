package tfcastikorcarts;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import de.mennomax.astikorcarts.Initializer;
import de.mennomax.astikorcarts.server.ServerInitializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import tfcastikorcarts.client.ClientEventHandler;
import tfcastikorcarts.client.ClientTextureHandler;
import tfcastikorcarts.common.AstikorItemGroup;
import tfcastikorcarts.common.entities.AstikorEntities;
import tfcastikorcarts.common.items.AstikorItems;
import tfcastikorcarts.config.TFCAstikorCartsConfig;

@Mod(TFCAstikorCarts.MOD_ID)
public class TFCAstikorCarts
{
    public static final String MOD_ID = "tfcastikorcarts";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TFCAstikorCarts()
    {
        final Initializer.Context ctx = new InitContext();
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(AstikorEntities::onEntityAttributeCreation);

        AstikorItems.ITEMS.register(bus);
        AstikorEntities.ENTITIES.register(bus);
        TFCAstikorCartsConfig.init();
        AstikorItemGroup.CREATIVE_TABS.register(bus);
        EventHandler.init();

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            ClientEventHandler.init();
        }
        DistExecutor.runForDist(() -> ClientTextureHandler::new, () -> ServerInitializer::new).init(ctx);
    }

    private static class InitContext implements Initializer.Context
    {
        @Override
        public ModLoadingContext context()
        {
            return ModLoadingContext.get();
        }

        @Override
        public IEventBus bus()
        {
            return MinecraftForge.EVENT_BUS;
        }

        @Override
        public IEventBus modBus()
        {
            return FMLJavaModLoadingContext.get().getModEventBus();
        }
    }
}