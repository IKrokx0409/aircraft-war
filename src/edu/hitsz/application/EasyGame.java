package edu.hitsz.application;

public class EasyGame extends Game {

    public EasyGame(boolean soundEnabled) {
        super("EASY", soundEnabled);
    }

    @Override
    protected void loadBackgroundImage() {
        this.BackgroundImage = ImageManager.BACKGROUND_IMAGE_EASY;
    }

    @Override
    protected void setDifficultyParameters() {
        this.enemyMaxNumber = 5;
        this.eliteProb = 0.2;
        this.elitePlusProb = 0.0;
        this.cycleDuration = 700;
        this.heroShootCycleDuration = 300;
        this.difficultyIncreaseCycle = 10000;
        this.bossScoreThreshold = 999999;
        this.baseBossHp = 0;
        this.bossHpIncrease = false;
    }

    // @Override
    // protected boolean shouldIncreaseDifficultyOverTime() { return false; }
    // @Override
    // protected boolean shouldSpawnBoss() { return false; }
}