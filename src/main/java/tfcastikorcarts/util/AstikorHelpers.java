package tfcastikorcarts.util;

import static tfcastikorcarts.TFCAstikorCarts.MOD_ID;

import net.minecraft.resources.ResourceLocation;

public class AstikorHelpers
{
    public static ResourceLocation identifier(String id)
    {
        return new ResourceLocation(MOD_ID, id);
    }
}
