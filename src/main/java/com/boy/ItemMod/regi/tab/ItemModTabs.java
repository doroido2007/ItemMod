package com.boy.ItemMod.regi.tab;

import com.boy.ItemMod.main.ItemMod;
import com.boy.ItemMod.regi.ItemModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ItemModTabs {

    public static final DeferredRegister<CreativeModeTab> MOD_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ItemMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ITEM_MAIN = MOD_TABS.register("item_main",
            () -> {return CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ItemModItems.BOY_INGOT.get()))
                    .title(Component.translatable("itemGroup.item_main"))
                    .displayItems((param, output) -> {
                        for(Item item: ItemMain.items){
                            output.accept(item);
                        }
                    })
                    .build();
    });
}
