package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractEnemyAircraft;

public abstract class EnemyFactory{
    // public abstract AbstractEnemyAircraft createEnemy();
    public abstract AbstractEnemyAircraft createEnemy(double hpMultiplier, double speedMultiplier);
    public AbstractEnemyAircraft createEnemy() {
        return createEnemy(1.0, 1.0);
    }
}