package edu.hitsz.prop;

import edu.hitsz.aircraft.*;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.Game;
import edu.hitsz.observer.BombObserver;
import java.util.List;

public class BombProp extends AbstractProp {

    public BombProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    // @Override
    public void activate(HeroAircraft hero, Game gameInstance) {
        // todo: BombProb实际功能
        System.out.println("BombSupply active!");
        notifyAllObservers(gameInstance);
        this.vanish();
    }

    private void notifyAllObservers(Game gameInstance) {
        int bombScore = 0;
        synchronized (gameInstance.getEnemyAircrafts()) {
            for (AbstractEnemyAircraft enemy : gameInstance.getEnemyAircrafts()) {
                if (!enemy.notValid()) {
                    int scoreToAdd = 0;
                    boolean destroyedByBomb = false;

                    if (enemy instanceof MobEnemy) {
                        scoreToAdd = 10;
                        enemy.Bombupdate();
                        destroyedByBomb = true;
                    } else if (enemy instanceof EliteEnemy) {
                        scoreToAdd = 20;
                        enemy.Bombupdate();
                        destroyedByBomb = true;
                    } else if (enemy instanceof ElitePlusEnemy) {
                        int hpBefore = enemy.getHp();
                        enemy.Bombupdate();
                        if (hpBefore > 0 && enemy.getHp() <= 0) {
                            scoreToAdd = 30;
                            destroyedByBomb = true;
                        }
                    }

                    if (destroyedByBomb) {
                        bombScore += scoreToAdd;
                    }
                }
            }
        }

        gameInstance.Bombupdate();
        if (bombScore > 0) {
            gameInstance.addScore(bombScore);
        }
    }
}