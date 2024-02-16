package tfcastikorcarts.util;

import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.common.capabilities.size.Weight;

import static tfcastikorcarts.TFCAstikorCarts.MOD_ID;

public class AstikorHelpers
{
    public static ResourceLocation identifier(String id)
    {
        return new ResourceLocation(MOD_ID, id);
    }

    public static float getWeightFactor(Weight weight, Size size)
    {
        return weight.ordinal() * size.ordinal();
    }
}
