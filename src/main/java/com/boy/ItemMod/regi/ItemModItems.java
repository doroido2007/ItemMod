package com.boy.ItemMod.regi;

import com.boy.ItemMod.main.ItemMod;
import com.boy.ItemMod.item.ItemBoyIngot;
import com.boy.ItemMod.item.ItemEnderliteIngot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ItemMod.MOD_ID);

    public static final RegistryObject<Item> BOY_INGOT = ITEMS.register("boy_ingot", () -> new ItemBoyIngot(new Item.Properties()));

    // 空中で右クリックすると落下速度ダウン
    public static final RegistryObject<Item> ENDERLITE_INGOT = ITEMS.register("enderlite_ingot", () -> new ItemEnderliteIngot(new Item.Properties()));

    // ボーイボスのスポーンエッグ(卵の色は 紫 と 赤)
    public static final RegistryObject<Item> BOY_BOSS_SPAWN_EGG = ITEMS.register("boy_boss_spawn_egg",
            () -> new ForgeSpawnEggItem(ItemModEntities.BOY_BOSS, 0x4B1E6E, 0xFF2828, new Item.Properties()));

    // ボーイソード(攻撃力は 12 + Tier ボーナス 2.5 + 素手 1、攻撃速度は標準の剣と同じ)
    public static final RegistryObject<Item> BOY_SWORD = ITEMS.register("boy_sword",
            () -> new SwordItem(ItemModTiers.BOY, 12, -2.4F, new Item.Properties()));

    // ボーイピッケル(攻撃速度は標準のピッケルと同じ)
    public static final RegistryObject<Item> BOY_PICKAXE = ITEMS.register("boy_pickaxe",
            () -> new PickaxeItem(ItemModTiers.BOY, 1, -2.8F, new Item.Properties()));

    // ボーイブロックを手に持てるようにするための BlockItem
    public static final RegistryObject<Item> BOY_BLOCK = ITEMS.register("boy_block",
            () -> new BlockItem(ItemModBlocks.BOY_BLOCK.get(), new Item.Properties()));
}