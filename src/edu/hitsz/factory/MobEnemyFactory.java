package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractEnemyAircraft;
import edu.hitsz.aircraft.MobEnemy;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

public class MobEnemyFactory extends EnemyFactory {
//    @Override
//    public AbstractEnemyAircraft createEnemy() {
//        return new MobEnemy(
//                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
//                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
//                0, 10, 30);
//    }

    @Override
    public AbstractEnemyAircraft createEnemy(double hpMultiplier, double speedMultiplier) {
        int baseHp = 30;
        int baseSpeedY = 10;
        int hp = (int) (baseHp * hpMultiplier);
        int speedY = (int) (baseSpeedY * speedMultiplier);

        return new MobEnemy(
                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                0, speedY, hp);
    }

}