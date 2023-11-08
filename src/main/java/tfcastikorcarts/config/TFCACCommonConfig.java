package tfcastikorcarts.config;

import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.config.ConfigBuilder;
import net.minecraftforge.common.ForgeConfigSpec;

public class TFCACCommonConfig
{
    public final ForgeConfigSpec.IntValue maxAnimalSize;
    public final ForgeConfigSpec.EnumValue<Size> maxItemSize;
    public final ForgeConfigSpec.BooleanValue canPushIntoPlayers;
    public final ForgeConfigSpec.BooleanValue canCarryWaterEntities;

    TFCACCommonConfig(ConfigBuilder builder)
    {
        builder.push("general");

        maxAnimalSize = builder.comment("Max animal size that the animal cart can carry.").define("maxAnimalSize", 3, 0, 69);
        maxItemSize = builder.comment("The largest (inclusive) size of an item that is allowed in a supply cart.").define("maxItemSize", Size.VERY_LARGE);
        canPushIntoPlayers = builder.comment("Can the animal cart pick up players by pushing it into them?").define("canPushIntoPlayers", true);
        canCarryWaterEntities = builder.comment("Can the animal cart pick up water animals?").define("canCarryWaterEntities", true);

        builder.pop();
    }
}
