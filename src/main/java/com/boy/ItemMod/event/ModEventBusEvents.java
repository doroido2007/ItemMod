package com.boy.ItemMod.event;

import com.boy.ItemMod.entity.BoyBossEntity;
import com.boy.ItemMod.main.ItemMod;
import com.boy.ItemMod.regi.ItemModEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ItemMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    // モブのステータス(HPや攻撃力など)をゲームに登録する
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ItemModEntities.BOY_BOSS.get(), BoyBossEntity.createAttributes().build());
    }
}
