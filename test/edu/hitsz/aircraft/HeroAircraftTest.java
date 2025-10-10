package edu.hitsz.aircraft;

import static org.junit.jupiter.api.Assertions.*;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class HeroAircraftTest {
    private HeroAircraft heroAircraft;
    @BeforeEach
    void setUp() {
        heroAircraft = HeroAircraft.getInstance();
        heroAircraft.increaseHp(1000);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Test decreaseHp method")
    void decreaseHp() {
        System.out.println("**--- Test decreaseHp method executed ---**");

        heroAircraft.decreaseHp(200);
        assertEquals(800, heroAircraft.getHp());

        heroAircraft.decreaseHp(800);
        assertEquals(0, heroAircraft.getHp());
        assertTrue(heroAircraft.notValid());

        // 扣血至小于零为0
        heroAircraft.decreaseHp(50);
        assertEquals(0, heroAircraft.getHp());
        assertTrue(heroAircraft.notValid());
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Test increaseHp method")
    void shoot() {
        System.out.println("**--- Test increaseHp method executed ---**");
        heroAircraft.decreaseHp(500);
        assertEquals(500, heroAircraft.getHp());

        heroAircraft.increaseHp(200);
        assertEquals(700, heroAircraft.getHp());

        // 回血超过上限
        heroAircraft.increaseHp(500);
        assertEquals(1000, heroAircraft.getHp());
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Test shoot method")
    void increaseHp() {
        System.out.println("**--- Test shoot method executed ---**");
        List<BaseBullet> bullets = heroAircraft.shoot();
        assertNotNull(bullets);

        // 子弹数量应为1
        assertEquals(1, bullets.size());

        // 子弹的初始位置和速度
        BaseBullet bullet = bullets.get(0);
        assertEquals(heroAircraft.getLocationX(), bullet.getLocationX()); // x坐标应一致
        assertTrue(bullet.getSpeedY() < 0); // 子弹向上飞，速度应为负
    }
}