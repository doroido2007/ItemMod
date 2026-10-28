package com.boy.ItemMod.entity.ai;

import com.boy.ItemMod.entity.BoyBossEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * ボス専用の攻撃AI。
 * 相手を追いかけて近づけば殴り、離れているあいだは矢を撃つ。
 * フェーズが進むほど、足が速くなり、矢の数と頻度が増える。
 */
public class BossAttackGoal extends Goal {

    // 矢を撃つ距離(2乗の値)。5ブロックより遠く、32ブロックより近いとき
    private static final double SHOOT_MIN_DIST_SQR = 5.0D * 5.0D;
    private static final double SHOOT_MAX_DIST_SQR = 32.0D * 32.0D;

    // フェーズごとの数字。添字がフェーズ番号(0番は使わない)
    private static final double[] SPEED          = {0.0D, 1.0D, 1.4D, 1.7D}; // 追いかける速さ
    private static final int[]    MELEE_COOLDOWN = {0, 20, 15, 10};          // 殴る間隔(tick)
    private static final int[]    SHOOT_COOLDOWN = {0, 60, 40, 30};          // 矢を撃つ間隔(tick)
    private static final int[]    ARROW_COUNT    = {0, 1, 3, 5};             // 一度に撃つ矢の数

    private final BoyBossEntity boss;
    private int shootCooldown;
    private int meleeCooldown;
    private int repathTimer;

    public BossAttackGoal(BoyBossEntity boss) {
        this.boss = boss;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.boss.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.boss.setAggressive(true);
        this.shootCooldown = 20;
        this.meleeCooldown = 0;
        this.repathTimer = 0;
    }

    @Override
    public void stop() {
        this.boss.setAggressive(false);
        this.boss.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.boss.getTarget();
        if (target == null) {
            return;
        }

        int phase = this.boss.getPhase();
        double distSqr = this.boss.distanceToSqr(target);
        boolean canSee = this.boss.getSensing().hasLineOfSight(target);

        this.boss.getLookControl().setLookAt(target, 30.0F, 30.0F);

        // 追いかける(フェーズが進むほど速い)
        this.repathTimer--;
        if (this.repathTimer <= 0) {
            this.repathTimer = 4 + this.boss.getRandom().nextInt(7);
            this.boss.getNavigation().moveTo(target, SPEED[phase]);
        }

        // 近接攻撃
        this.meleeCooldown = Math.max(this.meleeCooldown - 1, 0);
        double reachSqr = this.boss.getBbWidth() * 2.0F * this.boss.getBbWidth() * 2.0F + target.getBbWidth();
        if (distSqr <= reachSqr && canSee && this.meleeCooldown <= 0) {
            this.meleeCooldown = MELEE_COOLDOWN[phase];
            this.boss.swing(InteractionHand.MAIN_HAND);
            this.boss.doHurtTarget(target);
        }

        // 遠距離攻撃(フェーズが進むほど、矢の数が多く、間隔も短い)
        this.shootCooldown--;
        if (this.shootCooldown <= 0 && canSee
                && distSqr > SHOOT_MIN_DIST_SQR && distSqr < SHOOT_MAX_DIST_SQR) {
            this.boss.shootArrows(target, ARROW_COUNT[phase]);
            this.shootCooldown = SHOOT_COOLDOWN[phase];
        }
    }
}
