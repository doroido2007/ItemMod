package com.boy.ItemMod.main;

import com.boy.ItemMod.regi.tab.ItemModTabs;
import com.boy.ItemMod.regi.ItemModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("itemmod")

public class ItemMod {

    public static final String MOD_ID = "itemmod";

    public ItemMod(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();
        ItemModItems.ITEMS.register(bus);
        ItemModTabs.MOD_TABS.register(bus);
    }
}
