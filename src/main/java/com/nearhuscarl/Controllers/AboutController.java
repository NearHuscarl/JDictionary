package com.nearhuscarl.Controllers;

import com.nearhuscarl.App;
import com.nearhuscarl.Helpers.Link;
import com.sun.javafx.application.HostServicesDelegate;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class AboutController implements Initializable {

    public Hyperlink bugLink;
    public Hyperlink sourceLink;

    private static Stage window;

    static void open() {
        try {
            window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("About");
            window.setResizable(false);

            Parent root = FXMLLoader.load(App.class.getResource("About.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(App.class.getResource("About.css").toExternalForm());
            window.setScene(scene);
            window.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bugLink.setOnAction(e -> Link.OpenInBrowser("https://github.com/NearHuscarl/JDictionary/issues"));
        sourceLink.setOnAction(e -> Link.OpenInBrowser("https://github.com/NearHuscarl/JDictionary"));
    }
}