package com.boy.ItemMod.entity.ai;

import com.boy.ItemMod.entity.BoyBossEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * ボス専用の攻撃AI。
 * 相手を追いかけて近づけば殴り、離れているあいだは矢を撃つ。
 * HPが半分以下(フェーズ2)になると、足が速くなり、矢の数と頻度が増える。
 */
public class BossAttackGoal extends Goal {

    // 矢を撃つ距離(2乗の値)。5ブロックより遠く、32ブロックより近いとき
    private static final double SHOOT_MIN_DIST_SQR = 5.0D * 5.0D;
    private static final double SHOOT_MAX_DIST_SQR = 32.0D * 32.0D;

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

        boolean phase2 = this.boss.getPhase() == 2;
        double distSqr = this.boss.distanceToSqr(target);
        boolean canSee = this.boss.getSensing().hasLineOfSight(target);

        this.boss.getLookControl().setLookAt(target, 30.0F, 30.0F);

        // 追いかける(フェーズ2は速い)
        this.repathTimer--;
        if (this.repathTimer <= 0) {
            this.repathTimer = 4 + this.boss.getRandom().nextInt(7);
            this.boss.getNavigation().moveTo(target, phase2 ? 1.4D : 1.0D);
        }

        // 近接攻撃
        this.meleeCooldown = Math.max(this.meleeCooldown - 1, 0);
        double reachSqr = this.boss.getBbWidth() * 2.0F * this.boss.getBbWidth() * 2.0F + target.getBbWidth();
        if (distSqr <= reachSqr && canSee && this.meleeCooldown <= 0) {
            this.meleeCooldown = phase2 ? 15 : 20;
            this.boss.swing(InteractionHand.MAIN_HAND);
            this.boss.doHurtTarget(target);
        }

        // 遠距離攻撃(フェーズ2は3発ずつ、間隔も短い)
        this.shootCooldown--;
        if (this.shootCooldown <= 0 && canSee
                && distSqr > SHOOT_MIN_DIST_SQR && distSqr < SHOOT_MAX_DIST_SQR) {
            this.boss.shootArrows(target, phase2 ? 3 : 1);
            this.shootCooldown = phase2 ? 40 : 60;
        }
    }
}
