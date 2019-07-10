package com.nearhuscarl.Controllers;

import javafx.stage.Stage;

public class ControllerBase {
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public Stage getStage() {
        return stage;
    }
}
