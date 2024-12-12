package main.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Map;

public class MenuState implements State {
    private final StateController stateController;
    private final JFrame frame;
    private JPanel panel;
    private OptionPanel[] optionPanels;
    private int currentOption;

    public MenuState(StateController stateController) {
        this.stateController = stateController;
        this.frame = stateController.getFrame();
    }

    @Override
    public void start() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setPreferredSize(Config.SCREEN_RESOLUTION);

        panel.add(Box.createVerticalGlue());
        panel.add(new TitlePanel("Pong"));
        createOptionPanels();
        panel.add(Box.createVerticalGlue());
        setKeyBindings();

        frame.add(panel);
        frame.validate();
        frame.pack();
    }

    @Override
    public void start(Map<String, Object> params) {
        System.out.println("Warning: MenuState start with params");
        start();
        int initOption = (int) params.get("initOption");
        if (initOption > 0 && initOption < optionPanels.length) {
            changeOption(initOption);
        }
    }

    @Override
    public void exit() {
        frame.remove(panel);
        frame.invalidate();
        frame.pack();
    }

    private static class TitlePanel extends JPanel {
        private final String title;
        private final Font font;
        private final FontMetrics fontMetrics;

        public TitlePanel(String title) {
            this.title = title;
            font = Resource.getFont().deriveFont(Font.BOLD, 52f);
            fontMetrics = getFontMetrics(font);

            int width = fontMetrics.stringWidth(title);
            int height = fontMetrics.getHeight();

            Dimension size = new Dimension(width, height);
            setMinimumSize(size);
            setMaximumSize(size);
            setPreferredSize(size);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D graphics2D = (Graphics2D) g;

            graphics2D.setColor(Color.BLACK);
            graphics2D.setFont(font);
            graphics2D.drawString(title, 0, fontMetrics.getAscent());
        }
    }

    private abstract static class OptionPanel extends JPanel {
        private final String title;
        private final Font font;
        private final FontMetrics fontMetrics;
        private boolean isActive;

        public OptionPanel(Dimension size, String title) {
            setMinimumSize(size);
            setMaximumSize(size);
            setPreferredSize(size);

            this.title = title;

            font = Resource.getFont().deriveFont(Font.PLAIN, 36f);
            fontMetrics = getFontMetrics(font);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D graphics2D = (Graphics2D) g;

            if (isActive) {
                final int STROKE_WIDTH = 4;
                final int ARC_DIAMETER = 8;

                int width = getWidth() - (ARC_DIAMETER * 2);
                int height = getHeight() - (ARC_DIAMETER * 2);

                Stroke oldStroke = graphics2D.getStroke();
                graphics2D.setStroke(new BasicStroke(STROKE_WIDTH));
                graphics2D.setColor(Color.BLUE);
                graphics2D.drawRoundRect(ARC_DIAMETER, ARC_DIAMETER, width, height, ARC_DIAMETER, ARC_DIAMETER);
                graphics2D.setStroke(oldStroke);
            }

            int x = (getWidth() - fontMetrics.stringWidth(title)) / 2;
            int y = ((getHeight() - fontMetrics.getHeight()) / 2) + fontMetrics.getAscent();

            graphics2D.setColor(Color.DARK_GRAY);
            graphics2D.setFont(font);
            graphics2D.drawString(title, x, y);
        }

        public void setActive(boolean isActive) {
            this.isActive = isActive;
        }

        public abstract void action();
    }

    private void createOptionPanels() {
        final Dimension OPTION_SIZE = new Dimension(400, 75);
        optionPanels = new OptionPanel[]{
                new OptionPanel(OPTION_SIZE, "Singleplayer") {
                    @Override
                    public void action() {
                        System.out.println("Let's play solo!");
                        stateController.changeState("game");
                    }
                },
                new OptionPanel(OPTION_SIZE, "Multiplayer") {
                    @Override
                    public void action() {
                        System.out.println("Let's play together!");
                        stateController.changeState("game", Collections.singletonMap("multiplayer", true));
                    }
                },
                new OptionPanel(OPTION_SIZE, "Exit") {
                    @Override
                    public void action() {
                        System.exit(0);
                    }
                }
        };
        optionPanels[0].setActive(true);
        currentOption = 0;

        for (OptionPanel optionPanel : optionPanels) {
            panel.add(optionPanel);
        }
    }

    private void changeOption(int newOptionIndex) {
        if (optionPanels == null) {
            createOptionPanels();
        }
        optionPanels[currentOption].setActive(false);
        optionPanels[currentOption].repaint();
        currentOption = newOptionIndex;
        optionPanels[currentOption].setActive(true);
        optionPanels[currentOption].repaint();
    }

    private void setKeyBindings() {
        ActionMap actionMap = panel.getActionMap();
        actionMap.put("selectPreviousOption", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentOption > 0) {
                    changeOption(currentOption - 1);
                }
            }
        });
        actionMap.put("selectNextOption", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentOption < optionPanels.length - 1) {
                    changeOption(currentOption + 1);
                }
            }
        });
        actionMap.put("runOptionAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionPanels[currentOption].action();
            }
        });

        InputMap inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke("UP"), "selectPreviousOption");
        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "selectNextOption");
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "runOptionAction");
    }
}
