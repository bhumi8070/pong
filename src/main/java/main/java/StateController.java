package main.java;

import javax.swing.*;
import java.util.Map;

public interface StateController {
    void changeState(String stateName);
    void changeState(String stateName, Map<String, Object> params);
    JFrame getFrame();
}
