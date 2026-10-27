package com.boy.ItemMod.regi;

import com.boy.ItemMod.main.ItemMod;
import com.boy.ItemMod.item.ItemBoyIngot;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ItemMod.MOD_ID);

    public static final RegistryObject<Item> BOY_INGOT = ITEMS.register("boy_ingot", () -> new ItemBoyIngot(new Item.Properties()));

    // 効果なし・ただ存在するだけのアイテム
    public static final RegistryObject<Item> ENDERLITE_INGOT = ITEMS.register("enderlite_ingot", () -> new Item(new Item.Properties()));
}