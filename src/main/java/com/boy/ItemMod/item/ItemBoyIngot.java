package com.boy.ItemMod.item;

import com.boy.ItemMod.regi.ItemModTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ItemBoyIngot extends Item {
    public ItemBoyIngot(Properties p_41383_) {
        super(new Properties()
                .fireResistant()
                .stacksTo(64)
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(ItemModTags.Items.BOY_SERIES)) {
            player.addEffect(new MobEffectInstance((MobEffects.JUMP), 20*60*3, 6));
        } else {
            player.addEffect(new MobEffectInstance((MobEffects.DARKNESS), 20*60*3, 0));
        }
        return InteractionResultHolder.consume(stack);
    }
}
