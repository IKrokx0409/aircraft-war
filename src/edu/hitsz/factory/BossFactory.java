package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractEnemyAircraft;
import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.application.Main;

public class BossFactory extends EnemyFactory {
    @Override
    public AbstractEnemyAircraft createEnemy() {
        return new BossEnemy(
                Main.WINDOW_WIDTH / 2,
                100,
                5,
                0,
                500
        );
    }
}