package com.audtream.desktop.util;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class IconTool {

    public static void setIcon(Button button, FontAwesomeSolid solid) {
        FontIcon icon = new FontIcon(solid);
        icon.setIconSize(16);
        icon.setIconColor(Color.rgb(237, 223, 239));
        button.setGraphic(icon);
        button.setContentDisplay(ContentDisplay.LEFT);
        button.setGraphicTextGap(10);
    }

    public static void setIcon(Button button, FontAwesomeSolid solid, int size) {
        FontIcon icon = new FontIcon(solid);
        icon.setIconSize(size);
        icon.setIconColor(Color.rgb(237, 223, 239));
        button.setGraphic(icon);
        button.setContentDisplay(ContentDisplay.LEFT);
        button.setGraphicTextGap(10);
    }
}
