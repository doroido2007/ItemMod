package com.boy.ItemMod.regi;

import com.boy.ItemMod.main.ItemMod;
import com.boy.ItemMod.item.ItemBoyIngot;
import com.boy.ItemMod.item.ItemEnderliteIngot;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ItemMod.MOD_ID);

    public static final RegistryObject<Item> BOY_INGOT = ITEMS.register("boy_ingot", () -> new ItemBoyIngot(new Item.Properties()));

    // 空中で右クリックすると落下速度ダウン
    public static final RegistryObject<Item> ENDERLITE_INGOT = ITEMS.register("enderlite_ingot", () -> new ItemEnderliteIngot(new Item.Properties()));
}