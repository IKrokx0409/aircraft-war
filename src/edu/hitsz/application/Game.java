package edu.hitsz.application;

import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.BombProp;
import edu.hitsz.ui.RankingPanel;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import edu.hitsz.factory.EliteEnemyFactory;
import edu.hitsz.factory.EnemyFactory;
import edu.hitsz.factory.MobEnemyFactory;
import edu.hitsz.factory.ElitePlusEnemyFactory;
import edu.hitsz.factory.BossFactory;

import edu.hitsz.dao.GameRecord;
import edu.hitsz.dao.GameRecordDao;
import edu.hitsz.dao.GameRecordDaoImpl;

import edu.hitsz.audio.MusicThread;

import edu.hitsz.observer.BombObserver;

import java.util.Date;
import java.util.Comparator;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;



/**
 * 游戏主面板，游戏启动
 *
 * @author hitsz
 */
public abstract class Game extends JPanel implements BombObserver{

    private int backGroundTop = 0;
    // private int scoreBeforeBomb = 0;
    /**
     * Scheduled 线程池，用于任务调度
     */
    private final ScheduledExecutorService executorService;

    private final EnemyFactory mobEnemyFactory;
    private final EnemyFactory eliteEnemyFactory;
    private final EnemyFactory elitePlusEnemyFactory;
    private final EnemyFactory bossFactory;

    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private int timeInterval = 40;

    private final HeroAircraft heroAircraft;
    private final List<AbstractEnemyAircraft> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<AbstractProp> props;
    /**
     * 屏幕中出现的敌机最大数量
     */
    protected int enemyMaxNumber = 5;
    protected double eliteProb = 0.3;
    protected double elitePlusProb = 0.15;

    // Boss机相关状态
    protected int bossScoreThreshold = 500;
    private int bossSpawnCount = 0;
    private boolean bossIsActive = false;
    /**
     * 当前得分
     */
    private int score = 0;
    /**
     * 当前时刻
     */
    private int time = 0;

    /**
     * 周期（ms)
     * 指示子弹的发射、敌机的产生频率
     */
    protected int cycleDuration = 600;
    protected int heroShootCycleDuration = 400;
    private int cycleTime = 0;
    private int heroShootCycleTime = 0;

    private boolean soundEnabled;
    private MusicThread bgmThread;
    private MusicThread bossBgmThread;

    protected BufferedImage BackgroundImage;
    private String difficulty;

    /**
     * 游戏结束标志
     */
    private boolean gameOverFlag = false;
    protected int difficultyIncreaseCycle;
    protected double enemyHpMultiplier = 1.0;
    protected double enemySpeedMultiplier = 1.0;
    protected int difficultyIncreaseTime = 0;
    protected int baseBossHp;
    protected boolean bossHpIncrease;

    protected abstract void loadBackgroundImage();

    protected abstract void setDifficultyParameters();

    protected boolean shouldIncreaseDifficultyOverTime() {
        return false;
    }

    protected boolean shouldSpawnBoss() {
        return false;
    }

    protected double getBossHpMultiplier() {
        return 1.0;
    }

    protected void increaseDifficulty() {
        System.out.println("Difficulty increase triggered (default implementation).");
    }

    @Override
    public void Bombupdate() {
        synchronized (enemyBullets) {
            enemyBullets.clear();
        }
        if (soundEnabled) {
            new MusicThread("src/videos/bomb_explosion.wav").start();
        }
    }

    public Game(String difficulty, boolean soundEnabled) {
        heroAircraft = HeroAircraft.getInstance();
        this.mobEnemyFactory = new MobEnemyFactory();
        this.eliteEnemyFactory = new EliteEnemyFactory();
        this.elitePlusEnemyFactory = new ElitePlusEnemyFactory();
        this.bossFactory = new BossFactory();
        this.difficulty = difficulty;

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();
        /**
         * Scheduled 线程池，用于定时任务调度
         * 关于alibaba code guide：可命名的 ThreadFactory 一般需要第三方包
         * apache 第三方库： org.apache.commons.lang3.concurrent.BasicThreadFactory
         */
        this.executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("game-action-%d").daemon(true).build());

        this.soundEnabled = soundEnabled;
        if (this.soundEnabled) {
            bgmThread = new MusicThread("src/videos/bgm.wav", true);
            bgmThread.start();
        }

        setDifficultyParameters();
        loadBackgroundImage();
        //启动英雄机鼠标监听
        new HeroController(this, heroAircraft);
    }

    public List<AbstractEnemyAircraft> getEnemyAircrafts() {
        return enemyAircrafts;
    }

    public synchronized void addScore(int increment) {
        this.score += increment;
    }
    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {
        Runnable task = () -> {
            time += timeInterval;
            if (timeCountAndNewCycleJudge()) {
                System.out.println(time);
                spawnNewEnemies();


                enemyShootAction();
            }
            heroShootAction();
            if (shouldIncreaseDifficultyOverTime()) {
                difficultyIncreaseTime += timeInterval;
                if (difficultyIncreaseTime >= difficultyIncreaseCycle) {
                    difficultyIncreaseTime %= difficultyIncreaseCycle;
                    increaseDifficulty();
                }
            }

            // 子弹移动
            bulletsMoveAction();

            // 飞机移动
            aircraftsMoveAction();

            // 撞击检测
            crashCheckAction();

            // 道具移动
            propsMoveAction();

            // 后处理
            postProcessAction();

            //每个时刻重绘界面
            repaint();

            // 游戏结束检查英雄机是否存活
            if (heroAircraft.getHp() <= 0) {
                // 游戏结束
                executorService.shutdown();
                gameOverFlag = true;

                if (soundEnabled) {
                    if (bgmThread != null) {
                        bgmThread.stopMusic();
                    }
                    if (bossBgmThread != null) {
                        bossBgmThread.stopMusic();
                    }
                    new MusicThread("src/videos/game_over.wav").start();
                }

                System.out.println("Game Over!");

                // lab4.2 评分和排行榜
                String playerName = JOptionPane.showInputDialog(
                        this,
                        "游戏结束, 你的得分为 " + this.score + "。\n请输入你的名字:",
                        "记录得分",
                        JOptionPane.PLAIN_MESSAGE
                );

                if (playerName != null && !playerName.trim().isEmpty()) {
                    GameRecordDao dao = new GameRecordDaoImpl();
                    GameRecord newRecord = new GameRecord(playerName.trim(), this.score, new Date());
                    dao.addRecord(newRecord, this.difficulty);
                    System.out.println("得分已记录。");
                } else {
                    System.out.println("得分未记录。");
                }

                RankingPanel rankingPanel = new RankingPanel(this.difficulty);

                Main.cardPanel.remove(rankingPanel);
                Main.cardPanel.add(rankingPanel, "RANKING_" + this.difficulty);
                Main.cardLayout.show(Main.cardPanel, "RANKING_" +  this.difficulty);

//                GameRecordDao dao = new GameRecordDaoImpl();
//
//                String playerName = "Player";
//                GameRecord newRecord = new GameRecord(playerName, this.score, new Date());
//                dao.addRecord(newRecord);

//                List<GameRecord> records = dao.getAllRecords();
//                records.sort(Comparator.comparingInt(GameRecord::getScore).reversed());
//
//                System.out.println("*************************************");
//                System.out.println("          ---RANKING LIST---         ");
//                System.out.println("*************************************");
//                int rank = 1;
//                for (GameRecord record : records) {
//                    System.out.printf("rank%2d: %s, %6d, %s\n",
//                            rank++,
//                            record.getPlayerName(),
//                            record.getScore(),
//                            record.getFormattedTimestamp());
//                }
            }

        };

        /**
         * 以固定延迟时间进行执行
         * 本次任务执行完成后，需要延迟设定的延迟时间，才会执行新的任务
         */
        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);

    }

    //***********************
    //      Action 各部分
    //***********************

    protected void spawnNewEnemies() {
        if (enemyAircrafts.size() < enemyMaxNumber) {
            double r = Math.random();
            AbstractEnemyAircraft newEnemy;
            if (r < elitePlusProb) {
                newEnemy = elitePlusEnemyFactory.createEnemy(enemyHpMultiplier, enemySpeedMultiplier);
            } else if (r < elitePlusProb + eliteProb) {
                newEnemy = eliteEnemyFactory.createEnemy(enemyHpMultiplier, enemySpeedMultiplier);
            } else {
                newEnemy = mobEnemyFactory.createEnemy(enemyHpMultiplier, enemySpeedMultiplier);
            }
            enemyAircrafts.add(newEnemy);
        }
    }

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private void enemyShootAction() {
        // TODO 敌机射击
        for (AbstractAircraft enemy : enemyAircrafts) {
            enemyBullets.addAll(enemy.shoot());
        }
    }

    private void heroShootAction() {
        heroShootCycleTime += timeInterval;
        if (heroShootCycleTime >= heroShootCycleDuration) {
            heroShootCycleTime %= heroShootCycleDuration;
            if(!gameOverFlag){
                heroBullets.addAll(heroAircraft.shoot());
            }
        }
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    private void propsMoveAction() {
        for (AbstractProp prop : props) {
            prop.forward();
        }
    }

    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() {
        // TODO 敌机子弹攻击英雄
        List<AbstractEnemyAircraft> newEnemies = new LinkedList<>();

        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if (heroAircraft.crash(bullet)) {
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractEnemyAircraft  enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {

                    if (soundEnabled) {
                        new MusicThread("src/videos/bullet_hit.wav").start();
                    }

                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    if (enemyAircraft.notValid()) {
                        if (enemyAircraft instanceof ElitePlusEnemy) {
                            score += 30;
                            props.addAll(enemyAircraft.dropProps());
                        } else if (enemyAircraft instanceof EliteEnemy) {
                            score += 20;
                            props.addAll(enemyAircraft.dropProps());
                        } else if (enemyAircraft instanceof BossEnemy) {
                            score += 100;
                            props.addAll(enemyAircraft.dropProps());
                            bossIsActive = false;

                            if (soundEnabled) {
                                if (bossBgmThread != null) {
                                    bossBgmThread.stopMusic();
                                }
                                // 启动普通BGM
                                if (bgmThread != null) bgmThread.stopMusic(); // 停止旧BGM
                                bgmThread = new MusicThread("src/videos/bgm.wav", true);
                                bgmThread.start();
                            }

                        } else {
                            score += 10;
                        }

                        if (!bossIsActive && score / bossScoreThreshold > bossSpawnCount) {
                            System.out.println("Boss is coming!");
//                            newEnemies.add(bossFactory.createEnemy());
                            int bossHp = (int) (baseBossHp * Math.pow(getBossHpMultiplier(), bossSpawnCount));
                            AbstractEnemyAircraft boss = ((BossFactory)bossFactory).createEnemy(bossHp);
                            newEnemies.add(boss);
                            bossIsActive = true;
                            bossSpawnCount++;

                            if (soundEnabled) {
                                if (bgmThread != null) {
                                    bgmThread.stopMusic();
                                }
                                // 启动Boss BGM
                                if (bossBgmThread != null) bossBgmThread.stopMusic(); // 停止Boss BGM
                                bossBgmThread = new MusicThread("src/videos/bgm_boss.wav", true);
                                bossBgmThread.start();
                            }

                        }
                    }
                }
                // 英雄机 与 敌机 相撞
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // 我方获得道具
        for (AbstractProp prop : props) {
            if (prop.notValid()) {
                continue;
            }
            if (heroAircraft.crash(prop)) {
                if (soundEnabled) {
                    new MusicThread("src/videos/get_supply.wav").start();
                }

                if (prop instanceof BombProp) {
                    ((BombProp) prop).activate(heroAircraft, this);
                } else {
                    prop.activate(heroAircraft);
                }
            }
        }

        enemyAircrafts.addAll(newEnemies);
    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }


    //***********************
    //      Paint 各部分
    //***********************

    /**
     * 重写paint方法
     * 通过重复调用paint方法，实现游戏动画
     *
     * @param  g
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // 绘制背景,图片滚动
        g.drawImage(BackgroundImage, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(BackgroundImage, 0, this.backGroundTop, null);
//        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
//        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop, null);
        this.backGroundTop += 1;
        if (this.backGroundTop == Main.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        // 先绘制子弹，后绘制飞机
        // 这样子弹显示在飞机的下层
        paintImageWithPositionRevised(g, enemyBullets);
        paintImageWithPositionRevised(g, heroBullets);
        paintImageWithPositionRevised(g, props);
        paintImageWithPositionRevised(g, enemyAircrafts);

        g.drawImage(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

        //绘制得分和生命值
        paintScoreAndLife(g);

    }

    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
        }
    }

    private void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(new Color(16711680));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE:" + this.score, x, y);
        y = y + 20;
        g.drawString("LIFE:" + this.heroAircraft.getHp(), x, y);
    }


}
