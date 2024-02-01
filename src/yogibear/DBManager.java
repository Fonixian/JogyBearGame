/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package yogibear;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author rde3cs
 */
public class DBManager {
    private final int MAX_ENTRY = 10;
    PreparedStatement insertStatement;
    PreparedStatement deleteStatement;
    Connection connection;

    public DBManager() throws SQLException {
        String dbURL = "jdbc:derby://localhost:1527/highscores;";
        connection = DriverManager.getConnection(dbURL);
        String insertQuery = "INSERT INTO YOGIHIGHSCORES (NAME, SCORE, TIME) VALUES (?, ?, ?)";
        insertStatement = connection.prepareStatement(insertQuery);
        String deleteQuery = "DELETE FROM YOGIHIGHSCORES WHERE SCORE=?";
        deleteStatement = connection.prepareStatement(deleteQuery);
    }
    
    public ArrayList<HighScore> getHighScores() throws SQLException {
        String query = "SELECT * FROM YOGIHIGHSCORES ORDER BY SCORE DESC";
        ArrayList<HighScore> highScores = new ArrayList<>();
        Statement stmt = connection.createStatement();
        ResultSet results = stmt.executeQuery(query);
        while (results.next()) {
            String name = results.getString("NAME");
            int score = results.getInt("SCORE");
            int time = results.getInt("TIME");
            highScores.add(new HighScore(name, score, time));
        }
        return highScores;
    }
    
    public void putHighScore(String name, int score, int time) throws SQLException {
        ArrayList<HighScore> highScores = getHighScores();
        if (highScores.size() <= MAX_ENTRY) {
            insertScore(name, score, time);
        } else {
            int leastScore = highScores.get(highScores.size() - 1).score;
            if (leastScore < score) {
                deleteScores(leastScore);
                insertScore(name, score, time);
            }
        }
    }
    
    private void insertScore(String name, int score, int time) throws SQLException {
        insertStatement.setString(1, name);
        insertStatement.setInt(2, score);
        insertStatement.setInt(3, time);
        insertStatement.executeUpdate();
    }

    /**
     * Deletes all the highscores with score.
     *
     * @param score
     */
    private void deleteScores(int score) throws SQLException {
        deleteStatement.setInt(1, score);
        deleteStatement.executeUpdate();
    }
    
}
