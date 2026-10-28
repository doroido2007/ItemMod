package com.boy.ItemMod.client;

import com.boy.ItemMod.entity.BoyBossEntity;
import com.boy.ItemMod.main.ItemMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * ゾンビと同じ人型モデルを、独自テクスチャで表示する。
 * 通常はゾンビの1.5倍、復活後(第三形態)はさらにその1.5倍になり、テクスチャも変わる。
 */
public class BoyBossRenderer extends HumanoidMobRenderer<BoyBossEntity, HumanoidModel<BoyBossEntity>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ItemMod.MOD_ID, "textures/entity/boy_boss.png");
    private static final ResourceLocation TEXTURE_THIRD =
            ResourceLocation.fromNamespaceAndPath(ItemMod.MOD_ID, "textures/entity/boy_boss_third.png");

    private static final float SCALE = 1.5F;

    public BoyBossRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F * SCALE);
    }

    @Override
    protected void scale(BoyBossEntity entity, PoseStack poseStack, float partialTick) {
        float scale = entity.isRevived() ? SCALE * BoyBossEntity.THIRD_FORM_SCALE : SCALE;
        this.shadowRadius = 0.5F * scale; // 影の大きさも合わせる
        poseStack.scale(scale, scale, scale);
    }

    @Override
    public ResourceLocation getTextureLocation(BoyBossEntity entity) {
        return entity.isRevived() ? TEXTURE_THIRD : TEXTURE;
    }
}
