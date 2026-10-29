package com.boy.ItemMod.regi;

import com.boy.ItemMod.main.ItemMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;

public class ItemModTiers {

    // ボーイ素材のツールランク(鉄と同じ採掘レベル、鉄より少し丈夫で速い。修理素材はボーイインゴット)
    public static final Tier BOY = TierSortingRegistry.registerTier(
            new ForgeTier(
                    2,                          // 採掘レベル(2 = 鉄と同じ)
                    500,                        // 耐久値(鉄は250)
                    6.5F,                       // 採掘速度(鉄は6.0)
                    2.5F,                       // 攻撃力ボーナス(鉄は2.0)
                    14,                         // エンチャントのしやすさ
                    BlockTags.NEEDS_IRON_TOOL,  // 掘れるブロックの条件
                    () -> Ingredient.of(ItemModItems.BOY_INGOT.get())),
            ResourceLocation.fromNamespaceAndPath(ItemMod.MOD_ID, "boy"),
            List.of(Tiers.IRON),                // 鉄より上
            List.of(Tiers.DIAMOND));            // ダイヤより下
}
