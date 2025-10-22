package edu.hitsz.ui;

import edu.hitsz.application.Game;
import edu.hitsz.application.Main;

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

        Game gamePanel = new Game(difficulty, soundEnabled);

        Main.cardPanel.add(gamePanel, "GAME");
        Main.cardLayout.show(Main.cardPanel, "GAME");
        gamePanel.action();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}