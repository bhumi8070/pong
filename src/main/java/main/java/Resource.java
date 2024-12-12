package main.java;

import java.awt.*;
import java.io.InputStream;

public class Resource {
    private Resource() {}

    public static Font getFont() {
        try {
            InputStream inputStream = Resource.class.getResourceAsStream("/font/Handjet-Medium.ttf");
            assert inputStream != null;
            return Font.createFont(Font.TRUETYPE_FONT, inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
