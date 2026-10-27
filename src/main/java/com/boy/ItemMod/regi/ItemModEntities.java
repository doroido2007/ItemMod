package com.boy.ItemMod.regi;

import com.boy.ItemMod.entity.BoyBossEntity;
import com.boy.ItemMod.main.ItemMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ItemMod.MOD_ID);

    // 当たり判定はゾンビの1.5倍(見た目の大きさはレンダラー側で1.5倍にしている)
    public static final RegistryObject<EntityType<BoyBossEntity>> BOY_BOSS = ENTITIES.register("boy_boss",
            () -> EntityType.Builder.of(BoyBossEntity::new, MobCategory.MONSTER)
                    .sized(0.9F, 2.925F)
                    .clientTrackingRange(10)
                    .build("boy_boss"));
}
