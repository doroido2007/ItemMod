package com.boy.ItemMod.entity;

import com.boy.ItemMod.entity.ai.BossAttackGoal;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
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

    /** 復活の演出にかける時間(tick)。20tick = 1秒 */
    private static final int REVIVE_DURATION = 20 * 3;

    /** 第三形態の大きさ(通常のボスに対する倍率) */
    public static final float THIRD_FORM_SCALE = 1.5F;

    // 復活したか(第三形態か)。見た目やサイズを画面側でも知る必要があるので、同期するデータにしている
    private static final EntityDataAccessor<Boolean> DATA_REVIVED =
            SynchedEntityData.defineId(BoyBossEntity.class, EntityDataSerializers.BOOLEAN);

    // 復活演出の残り時間(tick)。0より大きいあいだは「復活中」
    private int reviveTimer = 0;

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
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_REVIVED, false);
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

    // ---- フェーズ ----

    /** 復活後(第三形態)か */
    public boolean isRevived() {
        return this.entityData.get(DATA_REVIVED);
    }

    /** 復活の演出中か(サーバー側だけで正しく判定できる) */
    public boolean isReviving() {
        return this.reviveTimer > 0;
    }

    /** 今のフェーズ。復活後は3。それ以外はHPが半分より多ければ1、半分以下なら2 */
    public int getPhase() {
        if (this.isRevived()) {
            return 3;
        }
        return this.getHealth() <= this.getMaxHealth() / 2.0F ? 2 : 1;
    }

    // ---- 復活(第三形態) ----

    /** 1回目に倒されたときは死なずに、復活の演出に入る */
    @Override
    public void die(DamageSource source) {
        // /kill や奈落などの「無敵を貫通する死」では復活させず、そのまま倒す
        if (!this.level().isClientSide
                && !this.isRevived()
                && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.startRevive();
            return; // super.die() を呼ばない = 死なない
        }
        super.die(source);
    }

    private void startRevive() {
        this.entityData.set(DATA_REVIVED, true); // 第三形態に(サイズ変更もここで反映される)
        this.reviveTimer = REVIVE_DURATION;
        this.setHealth(this.getMaxHealth());     // HPを全回復
        this.removeAllEffects();
        this.getNavigation().stop();
        this.setInvisible(true);                 // 姿が消える

        // 煙の爆発(見た目と音だけで、ブロックやプレイヤーへの被害はない)
        if (this.level() instanceof ServerLevel serverLevel) {
            double y = this.getY() + this.getBbHeight() / 2.0D;
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), y, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), y, this.getZ(), 80, 1.0D, 1.2D, 1.0D, 0.1D);
        }
        this.playSound(SoundEvents.GENERIC_EXPLODE, 4.0F, 0.8F);
    }

    private void finishRevive() {
        this.setInvisible(false); // 大きくなった姿で現れる
        if (this.level() instanceof ServerLevel serverLevel) {
            double y = this.getY() + this.getBbHeight() / 2.0D;
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), y, this.getZ(), 60, 1.2D, 1.5D, 1.2D, 0.08D);
        }
        this.playSound(SoundEvents.ENDER_DRAGON_GROWL, 3.0F, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }

        if (this.reviveTimer > 0) {
            this.reviveTimer--;
            // 復活中は、姿がないまま煙だけが立ちこめる
            if (this.level() instanceof ServerLevel serverLevel) {
                double y = this.getY() + this.getBbHeight() / 2.0D;
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), y, this.getZ(), 6, 0.6D, 0.8D, 0.6D, 0.02D);
            }
            if (this.reviveTimer == 0) {
                this.finishRevive();
            }
        }

        this.updateBossBar();
    }

    /** 復活中は動かない(AIも止まる) */
    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.isReviving();
    }

    /** 復活中は無敵(/kill などは通る) */
    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (this.isReviving() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    /** 第三形態になったら当たり判定を大きくする */
    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions dimensions = super.getDimensions(pose);
        return this.isRevived() ? dimensions.scale(THIRD_FORM_SCALE) : dimensions;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (DATA_REVIVED.equals(key)) {
            this.refreshDimensions(); // 当たり判定を作り直す
        }
        super.onSyncedDataUpdated(key);
    }

    // ---- セーブとロード ----

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Revived", this.isRevived());
        tag.putInt("ReviveTimer", this.reviveTimer);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(DATA_REVIVED, tag.getBoolean("Revived"));
        this.reviveTimer = tag.getInt("ReviveTimer");
    }

    // ---- 攻撃 ----

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

    /** HPバーの長さと色を更新する(フェーズ1は紫、2は黄、3は赤) */
    private void updateBossBar() {
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        this.bossEvent.setColor(switch (this.getPhase()) {
            case 3 -> BossEvent.BossBarColor.RED;
            case 2 -> BossEvent.BossBarColor.YELLOW;
            default -> BossEvent.BossBarColor.PURPLE;
        });
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
