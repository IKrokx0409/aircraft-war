package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.observer.BombObserver;
import edu.hitsz.prop.AbstractProp;

import java.util.List;

/**
 * 敌机抽象类
 *
 * @author hitsz
 */
public abstract class AbstractEnemyAircraft extends AbstractAircraft implements BombObserver{

    public AbstractEnemyAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public void forward() {
        super.forward();
        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

     @Override
     public abstract void Bombupdate();

    public abstract List<AbstractProp> dropProps();
}