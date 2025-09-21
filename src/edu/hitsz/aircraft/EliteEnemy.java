// 文件位置: src/edu/hitsz/aircraft/EliteEnemy.java
package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.BombProp;
import edu.hitsz.prop.FireProp;
import edu.hitsz.prop.HpProp;

import java.util.LinkedList;
import java.util.List;

/**
 * 精英敌机
 * 可以射击，坠毁后可能掉落道具
 *
 * @author hitsz
 */
public class EliteEnemy extends AbstractEnemyAircraft {

    /** 攻击方式 */
    private final int shootNum = 1;
    private final int power = 20;
    private final int direction = 1;

    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public List<BaseBullet> shoot() {
        List<BaseBullet> res = new LinkedList<>();
        int x = this.getLocationX();
        int y = this.getLocationY() + direction * 2;
        int speedY = this.getSpeedY() + direction * 4;
        BaseBullet bullet = new EnemyBullet(x, y, 0, speedY, power);
        res.add(bullet);
        return res;
    }

    @Override
    public List<AbstractProp> dropProps() {
        List<AbstractProp> props = new LinkedList<>();
        // 30%
        double r = Math.random();
        if (r < 0.1) {
            props.add(new HpProp(locationX, locationY, 0, 5));
        } else if (r < 0.2) {
            props.add(new FireProp(locationX, locationY, 0, 5));
        } else if (r < 0.3) {
            props.add(new BombProp(locationX, locationY, 0, 5));
        }

        return props;
    }
}