package com.boy.ItemMod.regi;

import com.boy.ItemMod.main.ItemMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ItemMod.MOD_ID);

    // ボーイブロック(ピッケルで掘らないとアイテムが落ちない。鉄のツール相当が必要)
    public static final RegistryObject<Block> BOY_BLOCK = BLOCKS.register("boy_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()));
}
