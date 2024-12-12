package main.java.game;

import java.awt.*;

public class Player {
    private int score;
    private Paddle paddle;

    public Player() {
        score = 0;
    }

    public int getScore() {
        return score;
    }

    public void addScore() {
        score += 1;
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public void setPaddle(Paddle paddle) {
        this.paddle = paddle;
    }
}
