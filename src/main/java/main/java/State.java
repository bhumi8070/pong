package main.java;

import java.util.Map;

public interface State {
    void start();
    void start(Map<String, Object> params);
    void exit();
}
