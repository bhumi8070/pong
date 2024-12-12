package main.java.game;

import java.awt.*;

public class Paddle extends Rectangle {
    private final Dimension panelSize;
    private int borderOffset;
    private int velocity;
    private double speed;

    public Paddle(Dimension panelSize) {
        this.panelSize = panelSize;
        setSizeNormal();
        setSpeedNormal();
    }

    public void update(long dt) {
        y += (int) (velocity * speed * dt);

        if (y + height + borderOffset > panelSize.height) {
            y = panelSize.height - height - borderOffset;
        }
        else if (y < borderOffset) {
            y = borderOffset;
        }
    }

    public void moveUp() {
        velocity = -1;
    }

    public void moveDown() {
        velocity = 1;
    }

    public void idle() {
        velocity = 0;
    }

    public void setSizeNormal() {
        int width = (int) (panelSize.width * 0.02);
        int height = (int) (panelSize.height * 0.15);

        setSize(width, height);
    }

    public void setSpeedNormal() {
        speed = 0.4;
    }

    public void setSpeedSlow() {
        speed = 0.25;
    }

    public void setBorderOffset(int borderSize) {
        borderOffset = borderSize;
    }

    public void paint(Graphics2D graphics2D) {
        final int ARC = width;

        graphics2D.setColor(Color.DARK_GRAY);
        graphics2D.fillRoundRect(x, y, width, height, ARC, ARC);
    }
}
