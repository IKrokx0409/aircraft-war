//package edu.hitsz.application;
//
//import javax.swing.*;
//import java.awt.*;
//
///**
// * 程序入口
// * @author hitsz
// */
//public class Main {
//
//    public static final int WINDOW_WIDTH = 512;
//    public static final int WINDOW_HEIGHT = 768;
//
//    public static void main(String[] args) {
//
//        System.out.println("Hello Aircraft War");
//
//        // 获得屏幕的分辨率，初始化 Frame
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        JFrame frame = new JFrame("Aircraft War");
//        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
//        frame.setResizable(false);
//        //设置窗口的大小和位置,居中放置
//        frame.setBounds(((int) screenSize.getWidth() - WINDOW_WIDTH) / 2, 0,
//                WINDOW_WIDTH, WINDOW_HEIGHT);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        Game game = new Game(difficulty, soundEnabled);
//        frame.add(game);
//        frame.setVisible(true);
//        game.action();
//    }
//}

package edu.hitsz.application;

import edu.hitsz.ui.StartPanel;

import javax.swing.*;
import java.awt.*;

/**
 * 程序入口
 * @author hitsz
 */
public class Main {

    public static final CardLayout cardLayout = new CardLayout(0, 0);
    public static final JPanel cardPanel = new JPanel(cardLayout);
    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;

    public static void main(String[] args) {

        System.out.println("Hello Aircraft War");

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Aircraft War");
            frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            frame.setResizable(false);
            frame.setBounds(((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - WINDOW_WIDTH) / 2, 0,
                    WINDOW_WIDTH, WINDOW_HEIGHT);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.add(cardPanel);
            StartPanel startPanel = new StartPanel();
            cardPanel.add(startPanel.getMainPanel(), "START_PANEL");

            cardLayout.show(cardPanel, "START_PANEL");

            frame.setVisible(true);
        });
    }
}