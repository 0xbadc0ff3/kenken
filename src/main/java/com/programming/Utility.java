package com.programming;

import javax.swing.*;
import java.awt.*;

public final class Utility {
    public static int MAX_BLOCK_SIZE = 4;
    public static int MAX_BOARD_SIZE = 6;
    public static int BLOCK_BORDER_SIZE = 3;
    public static int DEFAULT_BORDER_SIZE = 1;
    public static Image APP_LOGO = new ImageIcon("src/main/java/com/programming/logo.png").getImage();
    public static String APP_VERSION = "1.0.0";
    public static Font FONT = new Font( "SansSerif", Font.BOLD, 16 );
    public static Color DEFAULT_COLOR = Color.BLACK;
    public static Color WARNING_COLOR = Color.RED;
    public static Color VALID_COLOR = new Color(50,150,50);
    public static int WIDTH = 800;
    public static int HEIGHT = 800;
    private Utility(){}
}
