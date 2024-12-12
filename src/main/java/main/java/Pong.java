package main.java;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Pong {
    public Pong() {
        JFrame frame = new JFrame();
        frame.setTitle("Pong");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);

        new GameController(frame);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Pong::new);
    }

    private static class GameController implements StateController {
        private final JFrame frame;
        private final Map<String, State> stateMap;
        private State currentState;

        private GameController(JFrame frame) {
            this.frame = frame;

            stateMap = new HashMap<>();
            stateMap.put("menu", new MenuState(this));
            stateMap.put("game", new GameState(this));

            changeState("menu");
        }

        @Override
        public void changeState(String stateName) {
            if (currentState != null) {
                currentState.exit();
            }
            currentState = stateMap.get(stateName);
            currentState.start();
        }

        @Override
        public void changeState(String stateName, Map<String, Object> params) {
            if (currentState != null) {
                currentState.exit();
            }
            currentState = stateMap.get(stateName);
            currentState.start(params);
        }

        @Override
        public JFrame getFrame() {
            return frame;
        }
    }
}
