package tfcastikorcarts.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import tfcastikorcarts.util.AstikorHelpers;

public class AstikorTags
{
    public static class Items
    {
        public static final TagKey<Item> CART_WHEEL = create("cart_wheel");

        private static TagKey<Item> create(String id)
        {
            return TagKey.create(Registries.ITEM, AstikorHelpers.identifier(id));
        }
    }
}
