package edu.hitsz.application;


public class NormalGame extends Game {

    public NormalGame(boolean soundEnabled) {
        super("NORMAL", soundEnabled);
    }

    @Override
    protected void loadBackgroundImage() {
        this.BackgroundImage = ImageManager.BACKGROUND_IMAGE_NORMAL;
    }

    @Override
    protected void setDifficultyParameters() {
        this.enemyMaxNumber = 6;
        this.eliteProb = 0.3;
        this.elitePlusProb = 0.1;
        this.cycleDuration = 600;
        this.heroShootCycleDuration = 300;
        this.difficultyIncreaseCycle = 10000;
        this.bossScoreThreshold = 500;
        this.baseBossHp = 600;
        this.bossHpIncrease = false; // 普通模式 Boss 血量不增加
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
        this.eliteProb = Math.min(0.5, this.eliteProb + 0.01);
        this.elitePlusProb = Math.min(0.3, this.elitePlusProb + 0.005);
        this.cycleDuration = Math.max(300, this.cycleDuration - 20);
        this.enemyHpMultiplier += 0.02;
        this.enemySpeedMultiplier += 0.01;
        System.out.println("难度提升! 精英概率:" + String.format("%.2f", eliteProb) +
                ", 超级精英概率:" + String.format("%.2f", elitePlusProb) +
                ", 产生周期:" + cycleDuration +
                ", 敌机血量倍率:" + String.format("%.2f", enemyHpMultiplier) +
                ", 敌机速度倍率:" + String.format("%.2f", enemySpeedMultiplier));
    }
}