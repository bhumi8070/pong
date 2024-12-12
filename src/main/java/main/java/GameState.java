package main.java;

import main.java.game.GamePanel;

import javax.swing.*;
import java.util.Collections;
import java.util.Map;

public class GameState implements State {
    private final StateController stateController;
    private final JFrame frame;
    private GamePanel gamePanel;
    private boolean isRunning;

    public GameState(StateController stateController) {
        this.stateController = stateController;
        this.frame = stateController.getFrame();
    }

    @Override
    public void start() {
        start(Collections.singletonMap("multiplayer", false));
    }

    @Override
    public void start(Map<String, Object> params) {
        boolean isMultiplayer = (boolean) params.get("multiplayer");
        gamePanel = new GamePanel(isMultiplayer);

        frame.add(gamePanel);
        frame.validate();
        frame.pack();

        isRunning = true;
        runUpdateLoop();
    }

    @Override
    public void exit() {
        isRunning = false;
        frame.remove(gamePanel);
        frame.invalidate();
        frame.pack();
    }

    private void updateLoop() {
        final int TARGET_FPS = 60;
        final long TIME_BETWEEN_UPDATES = 1000_000000 / TARGET_FPS;

        long lastUpdateTime = System.nanoTime();
        long lastTime = System.currentTimeMillis();

        while (isRunning) {
            long now = System.nanoTime();
            long dt = System.currentTimeMillis() - lastTime;
            lastTime += dt;

            if (now - lastUpdateTime >= TIME_BETWEEN_UPDATES) {
                gamePanel.update(dt);
                lastUpdateTime = System.nanoTime();
            }

            gamePanel.repaint();
            if (gamePanel.isExitEarly()) {
                stateController.changeState("menu");
            }

            while (now - lastUpdateTime < TIME_BETWEEN_UPDATES) {
                Thread.yield();
                now = System.nanoTime();
            }
        }
    }

    private void runUpdateLoop() {
        Thread loop = new Thread(this::updateLoop);
        loop.start();
    }

}