package tfcastikorcarts.config;

import net.minecraftforge.common.ForgeConfigSpec;

import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.config.ConfigBuilder;

import tfcastikorcarts.common.container.ContainerList;

public class TFCACCommonConfig
{
    public final ForgeConfigSpec.DoubleValue maxAnimalSize;
    public final ForgeConfigSpec.IntValue maxPassengerCount;
    public final ForgeConfigSpec.EnumValue<Size> maxItemSize;
    public final ForgeConfigSpec.BooleanValue canPushIntoPlayers;
    public final ForgeConfigSpec.BooleanValue canCarryWaterEntities;
    public final ForgeConfigSpec.DoubleValue exhaustedThreshold;
    public final ForgeConfigSpec.DoubleValue overburdenedThreshold;
    public final ForgeConfigSpec.DoubleValue pinnedThreshold;
    public final ForgeConfigSpec.DoubleValue pullingDistanceModifier;
    public final ForgeConfigSpec.EnumValue<ContainerList> supplyCartInventorySize;
    public final ForgeConfigSpec.BooleanValue toggleFoodSpeed;

    TFCACCommonConfig(ConfigBuilder builder)
    {
        builder.push("general");

        maxAnimalSize = builder.comment("Max animal size that the animal cart can carry.").define("maxAnimalSize", 3D, 0D, Double.MAX_VALUE);
        maxPassengerCount = builder.comment("Amount of animals the animal cart can carry.").define("maxPassengerCount", 2, 0, Integer.MAX_VALUE);
        maxItemSize = builder.comment("The largest (inclusive) size of an item that is allowed in a supply cart.").define("maxItemSize", Size.VERY_LARGE);
        canPushIntoPlayers = builder.comment("Can the animal cart pick up players by pushing it into them?").define("canPushIntoPlayers", true);
        canCarryWaterEntities = builder.comment("Can the animal cart pick up water animals?").define("canCarryWaterEntities", true);
        pullingDistanceModifier = builder.comment("Pulling distance modifier for carts. Higher => farther/greater break tolerance.").define("pullingDistanceModifier", 2.0D, Double.MIN_VALUE, Double.MAX_VALUE);
        toggleFoodSpeed = builder.comment("Should the player's average nutrient level and thirst affect the pulling speed of carts?").define("toggleFoodSpeed", true);

        builder.comment("A very heavy huge item has a combined weight/size factor of 35, thus if a threshold is 140, then the player can carry 4 items.");
        exhaustedThreshold = builder.comment("Threshold at which the player carrying a cart will get exhausted. Higher => can carry more.").define("exhaustedThreshold", 140, 0, Double.MAX_VALUE);
        overburdenedThreshold = builder.comment("Threshold at which the player carrying a cart will get overburdened. Higher => can carry more.").define("overburdenedThreshold", 280, 0, Double.MAX_VALUE);
        pinnedThreshold = builder.comment("Threshold at which the player carrying a cart will get pinned. Higher => can carry more.").define("pinnedThreshold", 420, 0, Double.MAX_VALUE);
        supplyCartInventorySize = builder.comment("How big the supply cart inventory should be.").define("supplyCartInventorySize", ContainerList.SIZE_6);

        builder.pop();
    }
}
