package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractEnemyAircraft;
import edu.hitsz.aircraft.ElitePlusEnemy;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

public class ElitePlusEnemyFactory extends EnemyFactory {
    @Override
    public AbstractEnemyAircraft createEnemy() {
        return new ElitePlusEnemy(
                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.ELITE_PLUS_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                8,
                7,
                90
        );
    }
}