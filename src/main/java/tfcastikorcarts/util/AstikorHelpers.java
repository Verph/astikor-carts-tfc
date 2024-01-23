package tfcastikorcarts.util;

import static tfcastikorcarts.TFCAstikorCarts.MOD_ID;

import net.dries007.tfc.common.capabilities.size.IItemSize;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.common.capabilities.size.Weight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

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
