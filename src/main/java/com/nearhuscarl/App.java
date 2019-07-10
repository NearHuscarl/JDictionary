package com.nearhuscarl;

import com.nearhuscarl.Controllers.MainController;
import com.nearhuscarl.Controllers.SystemTray;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public static Image appIcon = new Image(App.class.getResourceAsStream("icon.png"));
    public static boolean mainAppActive = false;

    @Override
    public void start(Stage primaryStage) {
        Font.loadFont(App.class.getResource("OpenSansEmoji.ttf").toExternalForm(), 10);
        Font.loadFont(App.class.getResource("fontawesome-webfont.ttf").toExternalForm(), 10);

        MainController.open();
        SystemTray.open();
    }
}
