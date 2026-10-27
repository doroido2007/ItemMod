package com.boy.ItemMod.client;

import com.boy.ItemMod.entity.BoyBossEntity;
import com.boy.ItemMod.main.ItemMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/** ゾンビと同じ人型モデルを1.5倍にして、独自テクスチャで表示する */
public class BoyBossRenderer extends HumanoidMobRenderer<BoyBossEntity, HumanoidModel<BoyBossEntity>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ItemMod.MOD_ID, "textures/entity/boy_boss.png");

    private static final float SCALE = 1.5F;

    public BoyBossRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F * SCALE);
    }

    @Override
    protected void scale(BoyBossEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(SCALE, SCALE, SCALE);
    }

    @Override
    public ResourceLocation getTextureLocation(BoyBossEntity entity) {
        return TEXTURE;
    }
}
