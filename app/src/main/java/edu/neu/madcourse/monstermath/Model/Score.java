package edu.neu.madcourse.monstermath.Model;

public class Score {
    private int score;
    private String username;

    public Score(int score, String username) {
        this.score = score;
        this.username = username;
    }

    public Score() {

    }

    public int getScore() {
        return score;
    }

    public String getUsername() {
        return username;
    }
}
