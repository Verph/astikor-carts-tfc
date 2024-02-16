package tfcastikorcarts.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;

import tfcastikorcarts.common.entities.carts.TFCSupplyCartEntity;

public class TFCCartingJukeboxSound extends AbstractTickableSoundInstance
{
    private final TFCSupplyCartEntity cart;
    private final RecordItem disc;

    public TFCCartingJukeboxSound(final TFCSupplyCartEntity cart, final RecordItem disc)
    {
        super(disc.getSound(), SoundSource.RECORDS, RandomSource.create());
        this.cart = cart;
        this.disc = disc;
    }

    @Override
    public void tick()
    {
        if (this.cart.isAlive() && getDisc(this.cart.getDisc()) == this.disc)
        {
            this.x = (float) this.cart.getX();
            this.y = (float) this.cart.getY();
            this.z = (float) this.cart.getZ();
        } else {
            this.stop();
        }
    }

    public static void play(final TFCSupplyCartEntity entity, final ItemStack stack)
    {
        final RecordItem disc = getDisc(stack);
        final Minecraft mc = Minecraft.getInstance();
        final LocalPlayer player = mc.player;
        if (player == null) return;
        mc.getSoundManager().play(new TFCCartingJukeboxSound(entity, disc));
        if (entity.distanceToSqr(player) < 64.0D * 64.0D)
        {
            mc.gui.setNowPlaying(disc.getDisplayName());
        }
    }

    public static RecordItem getDisc(final ItemStack stack)
    {
        final Item item = stack.getItem();
        return item instanceof RecordItem recordItem ? recordItem : (RecordItem) Items.MUSIC_DISC_11;
    }
}