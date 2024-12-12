package main.java.game;

import main.java.Config;
import main.java.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel {
    private final int borderStrokeSize;
    private final Player player1;
    private final Player player2;
    private final Ball ball;
    private final Menu menu;
    private boolean isPlaying;
    private boolean isExitEarly;
    private boolean activePlayer2Bot;

    public GamePanel() {
        this(true);
    }

    public GamePanel(boolean isMultiplayer) {
        setSize(Config.SCREEN_RESOLUTION);
        setPreferredSize(Config.SCREEN_RESOLUTION);
        setMinimumSize(Config.SCREEN_RESOLUTION);
        setMaximumSize(Config.SCREEN_RESOLUTION);

        Dimension gamePanelSize = getSize();
        Paddle paddle1 = new Paddle(gamePanelSize);
        Paddle paddle2 = new Paddle(gamePanelSize);
        ball = new Ball(gamePanelSize);

        int paddle1X = (int) (gamePanelSize.width * 0.04);
        int paddle2X = (int) ((gamePanelSize.width * 0.96) - paddle2.width);
        int paddleYCenter = (gamePanelSize.height - paddle1.height) / 2;
        paddle1.setLocation(paddle1X, paddleYCenter);
        paddle2.setLocation(paddle2X, paddleYCenter);
        resetBallLocation();

        borderStrokeSize = (int) (gamePanelSize.width * 0.01);
        paddle1.setBorderOffset(borderStrokeSize);
        paddle2.setBorderOffset(borderStrokeSize);
        ball.setBorderOffset(borderStrokeSize);

        player1 = new Player();
        player1.setPaddle(paddle1);

        player2 = new Player();
        player2.setPaddle(paddle2);
        if (!isMultiplayer) {
            activePlayer2Bot = true;
            paddle2.setSpeedSlow();
        }

        setUpKeyBindings();
        isPlaying = true;
        menu = new Menu(this);
    }

    public void update(long dt) {
        if (isPlaying) {
            Paddle paddle1 = player1.getPaddle();
            Paddle paddle2 = player2.getPaddle();

            paddle1.update(dt);
            if (activePlayer2Bot) {
                if (ball.x > getWidth() / 2) {
                    double yCenterBall = ball.y + (double) ball.height / 2;
                    if (yCenterBall < paddle2.y) {
                        paddle2.moveUp();
                    } else if (yCenterBall > paddle2.y + paddle2.height) {
                        paddle2.moveDown();
                    }
                } else {
                    paddle2.idle();
                }
            }
            paddle2.update(dt);
            ball.update(dt);

            if (ball.intersects(paddle1)) {
                ball.x = paddle1.x + paddle1.width + 1;
                ball.bounceX();
            } else if (ball.intersects(paddle2)) {
                ball.x = paddle2.x - ball.width - 1;
                ball.bounceX();
            }

            if (ball.x < borderStrokeSize) {
                ball.stop();
                resetBallLocation();
                player2.addScore();
            } else if (ball.x + ball.width > getWidth() - borderStrokeSize) {
                ball.stop();
                resetBallLocation();
                player1.addScore();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        paintBorder(graphics2D);

        player1.getPaddle().paint(graphics2D);
        player2.getPaddle().paint(graphics2D);

        ball.paint(graphics2D);

        paintScore(graphics2D);

        if (!isPlaying) {
            menu.paint(graphics2D);
        }
    }

    private void paintBorder(Graphics2D graphics2D) {
        graphics2D.setColor(Color.LIGHT_GRAY);
        graphics2D.setStroke(new BasicStroke(borderStrokeSize));
        graphics2D.drawRect(
                borderStrokeSize / 2, borderStrokeSize / 2,
                getWidth() - borderStrokeSize, getHeight() - borderStrokeSize);
        float[] dash = {borderStrokeSize};
        graphics2D.setStroke(new BasicStroke(borderStrokeSize, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, borderStrokeSize, dash, 0));
        graphics2D.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
    }

    private void paintScore(Graphics2D graphics2D) {
        Font font = Resource.getFont().deriveFont(48f);
        FontMetrics fontMetrics = graphics2D.getFontMetrics(font);

        String scorePlayer1 = String.valueOf(player1.getScore());
        String scorePlayer2 = String.valueOf(player2.getScore());

        int borderMargin = (int) (getWidth() * 0.02);
        int xCenter = getWidth() / 2;

        int xPlayer1Score = xCenter - (borderStrokeSize / 2) - borderMargin - fontMetrics.stringWidth(scorePlayer1);
        int xPlayer2Score = xCenter + (borderStrokeSize / 2) + borderMargin;
        int yPlayerScore = borderStrokeSize + borderMargin + fontMetrics.getAscent();

        graphics2D.setColor(Color.BLACK);
        graphics2D.setFont(font);
        graphics2D.drawString(scorePlayer1, xPlayer1Score, yPlayerScore);
        graphics2D.drawString(scorePlayer2, xPlayer2Score, yPlayerScore);
    }

    private void resetBallLocation() {
        int xBall = (getWidth() - ball.width) / 2;
        int yBall = (getHeight() - ball.height) / 2;
        ball.setLocation(xBall, yBall);
    }

    private void setUpKeyBindings() {
        setUpActionMap();
        setUpInputMap();
    }

    private void setUpInputMap() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Ball
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "SPACE");

        // Player 1
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "W");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "S");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "RELEASED_1");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "RELEASED_1");

        // Menu
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESCAPE");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ENTER");

        // Menu and Player 2 (Overlapping)
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "UP");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "DOWN");

        // Player 2 in multiplayer
        if (!activePlayer2Bot) {
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "RELEASED_2");
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "RELEASED_2");
        }
    }

    private void setUpActionMap() {
        ActionMap actionMap = getActionMap();

        // Ball
        actionMap.put("SPACE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ball.isMoving()) {
                    ball.serve();
                }
            }
        });

        // Player 1
        actionMap.put("W", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPlaying) {
                    player1.getPaddle().moveUp();
                }
            }
        });
        actionMap.put("S", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPlaying) {
                    player1.getPaddle().moveDown();
                }
            }
        });
        actionMap.put("RELEASED_1", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPlaying) {
                    player1.getPaddle().idle();
                }
            }
        });

        // Menu
        actionMap.put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isPlaying = !isPlaying;
            }
        });
        actionMap.put("ENTER", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPlaying) {
                    menu.selectOption();
                }
            }
        });
        actionMap.put("UP", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPlaying && !activePlayer2Bot) {
                    player2.getPaddle().moveUp();
                } else {
                    menu.moveUp();
                }
            }
        });
        actionMap.put("DOWN", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPlaying && !activePlayer2Bot) {
                    player2.getPaddle().moveDown();
                } else {
                    menu.moveDown();
                }
            }
        });

        // Multiplayer
        if (!activePlayer2Bot) {
            actionMap.put("RELEASED_2", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isPlaying && !activePlayer2Bot) {
                        player2.getPaddle().idle();
                    }
                }
            });
        }

    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public int getBorderStrokeSize() {
        return borderStrokeSize;
    }

    public void exitEarly() {
        this.isExitEarly = true;
    }

    public boolean isExitEarly() {
        return isExitEarly;
    }
}
