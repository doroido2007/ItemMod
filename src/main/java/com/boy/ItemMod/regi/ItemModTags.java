package com.boy.ItemMod.regi;

import com.boy.ItemMod.main.ItemMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ItemModTags {

    public static class Blocks {
        private static TagKey<Block> tag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(ItemMod.MOD_ID, name));
        }
    }

    public static class Items {

        public static final TagKey<Item> BOY_SERIES = tag("boy_series");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(ItemMod.MOD_ID, name));
        }
    }
}