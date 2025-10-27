package edu.hitsz.application;


public class HardGame extends Game {

    public HardGame(boolean soundEnabled) {
        super("HARD", soundEnabled);
    }

    @Override
    protected void loadBackgroundImage() {
        this.BackgroundImage = ImageManager.BACKGROUND_IMAGE_HARD;
    }

    @Override
    protected void setDifficultyParameters() {
        this.enemyMaxNumber = 7;
        this.eliteProb = 0.35;
        this.elitePlusProb = 0.15;
        this.cycleDuration = 500;
        this.heroShootCycleDuration = 300;
        this.difficultyIncreaseCycle = 8000;
        this.bossScoreThreshold = 300;
        this.baseBossHp = 800;
        this.bossHpIncrease = true;
    }
    @Override
    protected double getBossHpMultiplier() {
        return 1.1; // 每次 Boss 血量增加 10%
    }


    @Override
    protected boolean shouldIncreaseDifficultyOverTime() {
        return true;
    }

    @Override
    protected boolean shouldSpawnBoss() {
        return true;
    }

    @Override
    protected void increaseDifficulty() {
        // 困难模式提升幅度更大
        this.eliteProb = Math.min(0.6, this.eliteProb + 0.02);
        this.elitePlusProb = Math.min(0.4, this.elitePlusProb + 0.01);
        this.cycleDuration = Math.max(250, this.cycleDuration - 30); // 存在上限
        this.enemyHpMultiplier += 0.03;
        this.enemySpeedMultiplier += 0.015;
        System.out.println("难度提升! 精英概率:" + String.format("%.2f", eliteProb) +
                ", 超级精英概率:" + String.format("%.2f", elitePlusProb) +
                ", 敌机子弹射速:" + cycleDuration +
                ", 敌机血量倍率:" + String.format("%.2f", enemyHpMultiplier) +
                ", 敌机速度倍率:" + String.format("%.2f", enemySpeedMultiplier));
    }
}