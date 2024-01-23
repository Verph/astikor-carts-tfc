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
    public final ForgeConfigSpec.DoubleValue exhaustedThreshold;
    public final ForgeConfigSpec.DoubleValue overburdenedThreshold;
    public final ForgeConfigSpec.DoubleValue pinnedThreshold;

    TFCACCommonConfig(ConfigBuilder builder)
    {
        builder.push("general");

        maxAnimalSize = builder.comment("Max animal size that the animal cart can carry.").define("maxAnimalSize", 3, 0, 69);
        maxItemSize = builder.comment("The largest (inclusive) size of an item that is allowed in a supply cart.").define("maxItemSize", Size.VERY_LARGE);
        canPushIntoPlayers = builder.comment("Can the animal cart pick up players by pushing it into them?").define("canPushIntoPlayers", true);
        canCarryWaterEntities = builder.comment("Can the animal cart pick up water animals?").define("canCarryWaterEntities", true);

        builder.comment("A very heavy huge item has a combined weight/size factor of 35, thus if a threshold is 140, then the player can carry 4 items.");
        exhaustedThreshold = builder.comment("Threshold at which the player carrying a cart will get exhausted. Higher => can carry more.").define("exhaustedThreshold", 140, 0, Double.MAX_VALUE);
        overburdenedThreshold = builder.comment("Threshold at which the player carrying a cart will get overburdened. Higher => can carry more.").define("overburdenedThreshold", 280, 0, Double.MAX_VALUE);
        pinnedThreshold = builder.comment("Threshold at which the player carrying a cart will get pinned. Higher => can carry more.").define("pinnedThreshold", 420, 0, Double.MAX_VALUE);

        builder.pop();
    }
}
