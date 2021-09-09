package edu.neu.madcourse.monstermath.Model;

public class Player {
    private String username;
    private int score;
    private boolean gameOver;

    public Player(String username, int score) {
        this.username = username;
        this.score = score;
        this.gameOver = false;
    }

    public Player() {}

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
