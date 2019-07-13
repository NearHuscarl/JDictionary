package com.nearhuscarl.Controllers;

import com.nearhuscarl.App;
import com.nearhuscarl.Data.SettingsAccess;
import com.nearhuscarl.Models.*;
import com.nearhuscarl.controls.FxDialogs;
import com.nearhuscarl.controls.NumericTextField;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController extends ControllerBase implements Initializable {
    public RadioButton newWordRadioButton;
    public RadioButton leftOffRadioButton;
    public RadioButton wordListRadioButton;
    public NumericTextField maxHistoryTextField;
    public CheckBox learningEnabledCheckbox;
    public NumericTextField intervalMinTextField;
    public NumericTextField intervalSecTextField;
    public NumericTextField displaySecTextField;
    public RadioButton vaAllRadioButton;
    public RadioButton vaHistoryRadioButton;
    public RadioButton vaWordListRadioButton;
    public ListView<String> customWordList;
    public Button cancelButton;
    public Button saveButton;
    public ToggleGroup historyStartup;
    public ToggleGroup vocabularySource;

    private SettingsAccess settingsAccess = new SettingsAccess();

    public static void open() {
        try {
            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Settings");
            window.setResizable(false);

            FXMLLoader loader = new FXMLLoader(App.class.getResource("Settings.fxml"));
            Parent root = loader.load();
            ControllerBase controller = loader.getController();
            controller.setStage(window);

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
            learningEnabledCheckbox.setSelected(settings.isLearningEnabled());
            intervalMinTextField.setText(Integer.toString(settings.getMinInterval()));
            intervalSecTextField.setText(Integer.toString(settings.getSecInterval()));
            setVocabularySourceOption(settings.getVocabularySource());
            customWordList.setItems(FXCollections.observableArrayList(settings.getCustomWordList()));
            displaySecTextField.setText(Integer.toString(settings.getSecDisplay()));

            cancelButton.setOnAction((e) -> getStage().close());
            saveButton.setOnAction(saveSettings);

            // make ListView editable using TextField as input
            customWordList.setCellFactory(TextFieldListCell.forListView());
            customWordList.setOnEditCommit(t -> {
                // add new cell
                if (!t.getNewValue().equals("") && t.getIndex() == customWordList.getItems().size() - 1) {
                    customWordList.getItems().set(t.getIndex(), t.getNewValue());
                    customWordList.getItems().add("");
                }
                // modify existing cell
                if (!t.getNewValue().equals("") && t.getIndex() <= customWordList.getItems().size() - 2) {
                    customWordList.getItems().set(t.getIndex(), t.getNewValue());
                }
                // delete current cell
                if (t.getNewValue().equals("") && t.getIndex() <= customWordList.getItems().size() - 2) {
                    customWordList.getItems().remove(t.getIndex());
                }
            });
            // add empty item so it can be edited in TextField
            if (customWordList.getItems().size() == 0 ||
                    !customWordList.getItems().get(customWordList.getItems().size() - 1).equals("")) {
                customWordList.getItems().add("");
            }
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

    private void setVocabularySourceOption(VocabularySource vocabularySource) {
        switch (vocabularySource) {
            case ALL:
                vaAllRadioButton.setSelected(true);
                break;
            case HISTORY:
                vaHistoryRadioButton.setSelected(true);
                break;
            case CUSTOM_WORDLIST:
                vaWordListRadioButton.setSelected(true);
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

    private VocabularySource getVocabularySourceOption() {
        RadioButton selectedRadioButton = (RadioButton)vocabularySource.getSelectedToggle();
        String radioButtonId = selectedRadioButton.getId();

        switch (radioButtonId) {
            case "vaAllRadioButton":
                return VocabularySource.ALL;
            case "vaHistoryRadioButton":
                return VocabularySource.HISTORY;
            case "vaWordListRadioButton":
                return VocabularySource.CUSTOM_WORDLIST;
            default:
                return new Settings().getVocabularySource(); // default
        }
    }

    private EventHandler<ActionEvent> saveSettings = (e) -> {
        Settings settings = new Settings();

        settings.setMaxHistory(Integer.parseInt(maxHistoryTextField.getText()));
        settings.setHistoryOnStartup(getHistoryOnStartupOption());
        settings.setLearningEnabled(learningEnabledCheckbox.isSelected());
        settings.setMinInterval(Integer.parseInt(intervalMinTextField.getText()));
        settings.setSecInterval(Integer.parseInt(intervalSecTextField.getText()));
        settings.setVocabularySource(getVocabularySourceOption());
        settings.setCustomWordList((customWordList.getItems().filtered(v -> !v.equals(""))));
        settings.setSecDisplay(Integer.parseInt(displaySecTextField.getText()));

        settingsAccess.saveSettings(settings);

        getStage().close();
    };
}
