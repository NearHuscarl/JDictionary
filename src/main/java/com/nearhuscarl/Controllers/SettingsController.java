package com.nearhuscarl.Controllers;

import com.nearhuscarl.App;
import com.nearhuscarl.Data.SettingsAccess;
import com.nearhuscarl.Models.Result;
import com.nearhuscarl.Models.ResultInfo;
import com.nearhuscarl.Models.Status;
import com.nearhuscarl.Models.HistoryOnStartup;
import com.nearhuscarl.Models.Settings;
import com.nearhuscarl.controls.FxDialogs;
import com.nearhuscarl.controls.NumericTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    public RadioButton newWordRadioButton;
    public RadioButton leftOffRadioButton;
    public RadioButton wordListRadioButton;
    public NumericTextField maxHistoryTextField;
    public Button cancelButton;
    public Button saveButton;
    public ToggleGroup historyStartup;

    private SettingsAccess settingsAccess = new SettingsAccess();

    private static Stage window;

    static void open() {
        try {
            window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Settings");
            window.setResizable(false);

            Parent root = FXMLLoader.load(App.class.getResource("Settings.fxml"));
            Scene scene = new Scene(root);
            window.setScene(scene);
            window.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Result<Settings> result = settingsAccess.loadSettings();
        if (result.getInfo().getStatus() != Status.Success) {
            ResultInfo info = result.getInfo();
            FxDialogs.showException("Error", info.getMessage(), info.getException());
        } else {
            Settings settings = result.getData();
            maxHistoryTextField.setText(Integer.toString(settings.getMaxHistory()));
            setHistoryOnStartupOption(settings.getHistoryOnStartup());

            cancelButton.setOnAction((e) -> window.close());
            saveButton.setOnAction(saveSettings);
        }
    }

    private void setHistoryOnStartupOption(HistoryOnStartup historyOnStartup) {
        switch (historyOnStartup) {
            case OPEN_NEW_WORD:
                newWordRadioButton.setSelected(true);
                break;
            case OPEN_WHERE_YOU_LEFT_OFF:
                leftOffRadioButton.setSelected(true);
                break;
            case OPEN_FROM_WORDLIST:
                wordListRadioButton.setSelected(true);
                break;
        }
    }

    private HistoryOnStartup getHistoryOnStartupOption() {
        RadioButton selectedRadioButton = (RadioButton)historyStartup.getSelectedToggle();
        String radioButtonId = selectedRadioButton.getId();

        switch (radioButtonId) {
            case "newWordRadioButton":
                return HistoryOnStartup.OPEN_NEW_WORD;
            case "leftOffRadioButton":
                return HistoryOnStartup.OPEN_WHERE_YOU_LEFT_OFF;
            case "wordListRadioButton":
                return HistoryOnStartup.OPEN_FROM_WORDLIST;
            default:
                return new Settings().getHistoryOnStartup(); // default
        }
    }

    private EventHandler<ActionEvent> saveSettings = (e) -> {
        Settings settings = new Settings();

        settings.setMaxHistory(Integer.parseInt(maxHistoryTextField.getText()));
        settings.setHistoryOnStartup(getHistoryOnStartupOption());
        settingsAccess.saveSettings(settings);

        window.close();
    };
}
