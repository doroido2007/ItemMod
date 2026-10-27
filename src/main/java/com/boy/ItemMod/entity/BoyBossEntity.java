package com.boy.ItemMod.entity;

import com.boy.ItemMod.entity.ai.BossAttackGoal;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class BoyBossEntity extends Monster {

    // 画面上部に出るHPバー
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);

    public BoyBossEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 50;
    }

    /** ボスの基本ステータス */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 200.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
    }

    @Override
    protected void registerGoals() {
        // 数字が小さいほど優先される
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BossAttackGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        // 攻撃する相手の選び方
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    /** 今のフェーズ。HPが半分より多ければ1、半分以下なら2 */
    public int getPhase() {
        return this.getHealth() <= this.getMaxHealth() / 2.0F ? 2 : 1;
    }

    /** 矢を撃つ。複数発のときは少しばらける */
    public void shootArrows(LivingEntity target, int count) {
        for (int i = 0; i < count; i++) {
            Arrow arrow = new Arrow(this.level(), this);
            double dx = target.getX() - this.getX();
            double dy = target.getY(0.3333333333333333D) - arrow.getY();
            double dz = target.getZ() - this.getZ();
            double horizontal = Math.sqrt(dx * dx + dz * dz);
            float inaccuracy = count > 1 ? 8.0F : 2.0F;

            arrow.shoot(dx, dy + horizontal * 0.2D, dz, 1.6F, inaccuracy);
            arrow.setBaseDamage(3.0D);
            arrow.pickup = AbstractArrow.Pickup.DISALLOWED; // 拾えない矢にする
            this.level().addFreshEntity(arrow);
        }
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    // ---- HPバーの処理 ----

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        // フェーズ2に入ったらバーの色を赤にする
        this.bossEvent.setColor(this.getPhase() == 2 ? BossEvent.BossBarColor.RED : BossEvent.BossBarColor.PURPLE);
    }

    // ---- その他 ----

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false; // ボスは離れても消えない
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ZOMBIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_DEATH;
    }
}
