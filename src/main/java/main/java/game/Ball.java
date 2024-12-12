package main.java.game;

import java.awt.*;

public class Ball extends Rectangle {
    private final Dimension panelSize;
    private final double SPEED;
    private int borderOffset;
    private double dx;
    private double dy;
    private boolean isMoving;

    public Ball(Dimension panelSize) {
        this.panelSize = panelSize;
        height = width = (int) (panelSize.width * 0.02);

        SPEED = 0.4;
    }

    public void update(long dt) {
        if (!isMoving) {
            return;
        }

        x += (int) (dx * dt);
        y += (int) (dy * dt);

        if (x + width > panelSize.width) {
            dx *= -1;
            x = panelSize.width - width;
        } else if (x < 0) {
            dx *= -1;
            x = 0;
        }

        if (y + height + borderOffset > panelSize.height) {
            dy *= -1;
            y = panelSize.height - height - borderOffset;
        } else if (y < borderOffset) {
            dy *= -1;
            y = borderOffset;
        }
    }

    public void stop() {
        isMoving = false;
        dx = dy = 0;
    }

    public boolean isMoving() {
        return isMoving;
    }

    private double getRandomAngle() {
        final double MIN_ANGLE_1 = -Math.PI / 4;
        final double MAX_ANGLE_1 = Math.PI / 4;
        final double MIN_ANGLE_2 = 3 * Math.PI / 4;
        final double MAX_ANGLE_2 = 5 * Math.PI / 4;

        if (Math.random() < 0.5) {
            return MIN_ANGLE_1 + Math.random() * (MAX_ANGLE_1 - MIN_ANGLE_1);
        } else {
            return MIN_ANGLE_2 + Math.random() * (MAX_ANGLE_2 - MIN_ANGLE_2);
        }
    }

    public void serve() {
        isMoving = true;
        double angle = getRandomAngle();
        dx = Math.cos(angle) * SPEED;
        dy = Math.sin(angle) * SPEED;
    }

    public void bounceX() {
        double angle = getRandomAngle();
        int direction = -1;

        if (dx < 0) {
            direction = 1;
        }
        dx = Math.cos(angle) * SPEED * direction;
        dy = Math.sin(angle) * SPEED * -direction;
    }

    public void setBorderOffset(int borderSize) {
        borderOffset = borderSize;
    }

    public void paint(Graphics2D graphics2D) {
        graphics2D.setColor(Color.RED);
        graphics2D.fillOval(x, y, width, height);
    }
}
