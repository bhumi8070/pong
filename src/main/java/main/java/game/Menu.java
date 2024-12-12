package main.java.game;

import main.java.Resource;

import java.awt.*;

public class Menu {
    private final Dimension gamePanelSize;
    private final Option[] options;
    private int currentOption;

    public Menu(GamePanel gamePanel) {
        gamePanelSize = gamePanel.getSize();
        options = new Option[]{
                new Option("Resume") {
                    @Override
                    void runAction() {
                        gamePanel.setPlaying(true);
                    }
                },
                new Option("Exit to menu") {
                    @Override
                    void runAction() {
                        gamePanel.exitEarly();
                    }
                }
        };
        currentOption = 0;
    }

    public void moveUp() {
        if (currentOption > 0) {
            currentOption -= 1;
        }
    }

    public void moveDown() {
        if (currentOption < options.length - 1) {
            currentOption += 1;
        }
    }

    public void selectOption() {
        options[currentOption].runAction();
    }

    public void paint(Graphics2D graphics2D) {
        Font font = Resource.getFont().deriveFont((float) (gamePanelSize.width * 0.045));
        FontMetrics fontMetrics = graphics2D.getFontMetrics(font);
        graphics2D.setFont(font);

        double optionPadding = fontMetrics.getHeight() * 0.25;
        double optionHeight = fontMetrics.getHeight() + optionPadding;
        int menuWidth = (int) (gamePanelSize.height * 0.32);
        int menuHeight = (int) (optionHeight * options.length);
        int arcDiameter = (int) (gamePanelSize.width * 0.01);

        int xMenu = (gamePanelSize.width - menuWidth) / 2;
        int yMenu = (gamePanelSize.height - menuHeight) / 2;
        graphics2D.setColor(Color.LIGHT_GRAY);
        graphics2D.fillRoundRect(xMenu, yMenu, menuWidth, menuHeight, arcDiameter, arcDiameter);

        for (int i = 0; i < options.length; ++i) {
            if (i == currentOption) {
                int yCursor = (int) (yMenu + (optionHeight * i));
                float stroke = (float) (gamePanelSize.width * 0.005);

                graphics2D.setColor(Color.BLUE);
                graphics2D.setStroke(new BasicStroke(stroke));
                graphics2D.drawRoundRect(xMenu, yCursor, menuWidth, (int) optionHeight, arcDiameter, arcDiameter);
            }

            String title = options[i].title;
            int xTitleText = xMenu + ((menuWidth - fontMetrics.stringWidth(title)) / 2);
            int yTitleText = yMenu + (int) (optionHeight * i) + ((fontMetrics.getHeight() + fontMetrics.getAscent()) / 2);
            graphics2D.setColor(Color.BLACK);
            graphics2D.drawString(title, xTitleText, yTitleText);
        }
    }

    private abstract static class Option {
        String title;

        Option(String title) {
            this.title = title;
        }

        abstract void runAction();
    }
}
