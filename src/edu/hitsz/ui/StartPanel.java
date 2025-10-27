package edu.hitsz.ui;

import edu.hitsz.application.*;

import javax.swing.*;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartPanel {
    private JPanel mainPanel;
    private JButton easyButton;
    private JButton normalButton;
    private JButton hardButton;
    private JCheckBox soundCheckBox;

    public StartPanel() {
        easyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame("EASY");
            }
        });

        normalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame("NORMAL");
            }
        });

        hardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame("HARD");
            }
        });

    }

    private void startGame(String difficulty) {
        boolean soundEnabled = soundCheckBox.isSelected();

        // [MODIFY] 根据难度创建不同的 Game 子类实例
        Game gamePanel;
        switch (difficulty) {
            case "EASY":
                gamePanel = new EasyGame(soundEnabled);
                break;
            case "NORMAL":
                gamePanel = new NormalGame(soundEnabled);
                break;
            case "HARD":
                gamePanel = new HardGame(soundEnabled);
                break;
            default:
                gamePanel = new EasyGame(soundEnabled);
                break;
        }

        Main.cardPanel.add(gamePanel, "GAME_" + difficulty);
        Main.cardLayout.show(Main.cardPanel, "GAME_" + difficulty);
        gamePanel.action(); // 启动游戏
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}