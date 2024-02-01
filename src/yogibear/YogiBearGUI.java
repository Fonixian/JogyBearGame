/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package yogibear;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.ArrayList;

/**
 *
 * @author rde3cs
 */
public class YogiBearGUI {
    private final int START_HEALTH = 3;
    private int health;
    private int basketCollected;
    
    private JFrame frame;
    private GameEngine gameEngine;
    
    private JPanel gui;
    
    private long startTime;
    
    private JLabel timeLabel;
    private JLabel bascetLabel;
    private JLabel hearthLabel;
    
    private Timer timer;
    
    private DBManager database;
    
    public YogiBearGUI(){
        try {
            database = new DBManager();

        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        basketCollected = 0;
        
        frame = new JFrame("Yogi Bear");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameEngine = new GameEngine(this.frame,START_HEALTH ,this);
        gameEngine.setPreferredSize(new Dimension(500, 500));
        frame.getContentPane().add(BorderLayout.CENTER, gameEngine);
        
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        JMenu gameMenu = new JMenu("Menu");
        
        menuBar.add(gameMenu);
        JMenuItem restartMenuItem = new JMenuItem("Restart");
        gameMenu.add(restartMenuItem);
        restartMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                basketCollected = 0;
                health = START_HEALTH;
                reset();
            }
        });
        
        JMenuItem scoresMenuItem = new JMenuItem("Scores");
        gameMenu.add(scoresMenuItem);
        scoresMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scoreDialoge();
            }
        });
            
        gui = new JPanel();
        gui.setLayout(new GridLayout(1, 3));
        
        hearthLabel = new JLabel("0");
        gui.add(hearthLabel);
        bascetLabel = new JLabel("0");
        gui.add(bascetLabel);
        timeLabel = new JLabel("0");
        gui.add(timeLabel);
        
        frame.getContentPane().add(BorderLayout.SOUTH, gui);
        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                health = gameEngine.getCurrHealth();
                timeLabel.setText(elapsedTime() + " ms");
                bascetLabel.setText("Baskets: " + basketCollected);
                hearthLabel.setText("HP: " + health + "/" + START_HEALTH);
                if(gameEngine.getCurrBasket() == 0){
                    reset();
                }
                if(gameEngine.getCurrHealth() == 0){
                    endDialoge();
                    reset();
                }
            }
        });
        startTime = System.currentTimeMillis();
        timer.start();
        
        frame.setSize(500, 500);
        frame.pack();
        frame.setVisible(true);
    }
    
    public long elapsedTime() {
        return System.currentTimeMillis() - startTime;
    }
    
    public void basketCollected(){
        basketCollected++;
    }
    
    public void reset(){
        timer.stop();
        gameEngine.close();
        frame.remove(gameEngine);
        gameEngine = new GameEngine(this.frame, health, this);
        gameEngine.setPreferredSize(new Dimension(500, 500));
        frame.add(gameEngine);
        
        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                health = gameEngine.getCurrHealth();
                timeLabel.setText(elapsedTime() + " ms");
                bascetLabel.setText("Bascets: " + basketCollected);
                hearthLabel.setText("HP: " + health + "/" + START_HEALTH);
                if(gameEngine.getCurrBasket() == 0){
                    reset();
                }
                if(gameEngine.getCurrHealth() == 0){
                    endDialoge();
                    reset();
                }
            }
        });
        frame.pack();
        frame.setVisible(true);
        timer.start();
    }
    
    private void endDialoge(){
        String name = (String) JOptionPane.showInputDialog(null, "Enter your name");
        try {
            database.putHighScore(name, basketCollected, (int)elapsedTime());

        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        health = START_HEALTH;
        basketCollected = 0;
        reset();
    }
    
    private void scoreDialoge(){
        ArrayList<HighScore> scores;
        try {
            scores = database.getHighScores();
            String msg = "";
            for(HighScore score : scores){
                msg += "name: " + score.name + " score: " + score.score + " time: " + score.time + " ms" + "\n";
            }
            JOptionPane.showMessageDialog(null, msg, "HighScores", JOptionPane.PLAIN_MESSAGE, null);

        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
