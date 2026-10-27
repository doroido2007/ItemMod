package com.boy.ItemMod.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemEnderliteIngot extends Item {

    // 落下速度ダウンの持続時間(tick)。20tick = 1秒
    private static final int SLOW_FALLING_DURATION = 20 * 10;

    public ItemEnderliteIngot(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // 地面に立っているときは何も起こさない
        if (player.onGround()) {
            return InteractionResultHolder.pass(stack);
        }

        // 効果の付与はサーバー側だけで行う
        if (!world.isClientSide) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, SLOW_FALLING_DURATION, 0));
        }
        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide);
    }
}
