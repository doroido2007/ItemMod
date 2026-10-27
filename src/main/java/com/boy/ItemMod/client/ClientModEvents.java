package com.boy.ItemMod.client;

import com.boy.ItemMod.main.ItemMod;
import com.boy.ItemMod.regi.ItemModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// クライアント(画面を持つ側)だけで動く登録
@Mod.EventBusSubscriber(modid = ItemMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ItemModEntities.BOY_BOSS.get(), BoyBossRenderer::new);
    }
}
