package com.nearhuscarl;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    private static EventHandler<WindowEvent> onClose;
    public static void setOnClose(EventHandler<WindowEvent> onClose) {
        App.onClose = onClose;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("App.css").toExternalForm());
        Font.loadFont(App.class.getResource("OpenSansEmoji.ttf").toExternalForm(), 10);
        Font.loadFont(App.class.getResource("fontawesome-webfont.ttf").toExternalForm(), 10);

        primaryStage.setTitle("JDictionary");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            onClose.handle(e);
            primaryStage.close();
        });
    }
}
