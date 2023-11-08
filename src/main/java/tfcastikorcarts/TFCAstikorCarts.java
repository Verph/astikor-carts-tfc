package tfcastikorcarts;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import tfcastikorcarts.client.ClientEventHandler;
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
    }
}