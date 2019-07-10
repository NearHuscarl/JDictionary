package com.nearhuscarl.Controllers;

import com.nearhuscarl.App;
import com.nearhuscarl.Constants;
import com.nearhuscarl.Data.DataAccess;
import com.nearhuscarl.Data.HistoryAccess;
import com.nearhuscarl.Data.SettingsAccess;
import com.nearhuscarl.Helpers.BinarySearch;
import com.nearhuscarl.Helpers.RandomUtil;
import com.nearhuscarl.Helpers.SpellCheck;
import com.nearhuscarl.Helpers.WordUtil;
import com.nearhuscarl.Models.*;
import com.nearhuscarl.controls.DefinitionTextArea;
import com.nearhuscarl.controls.FxDialogs;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController extends ControllerBase implements Initializable {

    public static void open() {
        try {
            Stage window = new Stage();
            FXMLLoader loader = new FXMLLoader(App.class.getResource("Main.fxml"));
            Parent root = loader.load();
            ControllerBase controller = loader.getController();
            controller.setStage(window);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(App.class.getResource("App.css").toExternalForm());

            window.getIcons().add(App.appIcon);
            window.setTitle("JDictionary");
            window.setScene(scene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);

        getStage().setOnCloseRequest(e -> {
            onClose();
            getStage().close();
            App.mainAppActive = false;
        });
    }

    private DataAccess dataAccess;
    private SettingsAccess settingsAccess;
    private HistoryAccess historyAccess;
    private SpellCheck spellCheck;
    public DefinitionTextArea definitionTextArea;
    public ListView<String> wordListView;
    public ListView<Word> otherWordListView;
    public TextField inputTextField;
    public Button searchButton;
    public Button prevButton;
    public Button nextButton;
    public Button addButton;
    public Button aboutButton;
    public Button settingButton;
    private ArrayList<String> wordList;
    private ObservableList<Word> otherWordList;
    private History<String> history;
    private Callback<ListView<Word>, ListCell<Word>> wordListCellCb = param -> new ListCell<Word>() {
        @Override
        protected void updateItem(Word item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
            } else {
                setText(item.getName() + " " + item.getWordform());
            }
        }
    };

    private String getSelectedWord() {
        return wordListView.getSelectionModel().getSelectedItem();
    }
    private Word getSelectedOtherWord() {
        return otherWordListView.getSelectionModel().getSelectedItem();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataAccess = new DataAccess();
        settingsAccess = new SettingsAccess();
        historyAccess = new HistoryAccess();
        otherWordListView.managedProperty().bind(otherWordListView.visibleProperty());
        otherWordList = FXCollections.observableArrayList();

        Result<ArrayList<String>> result = dataAccess.selectNames();
        if (result.getInfo().getStatus() != Status.Success) {
            ResultInfo info = result.getInfo();
            FxDialogs.showException("Error", info.getMessage(), info.getException());
            return;
        }

        Result<History> result2 = historyAccess.loadHistory();
        if (result2.getInfo().getStatus() != Status.Success) {
            ResultInfo info = result2.getInfo();
            FxDialogs.showException("Error", info.getMessage(), info.getException());
            return;
        }

        history = result2.getData();

        // We dont need ObservableList because word list will only changed (initialized)
        // once on startup
        wordList = result.getData();
        spellCheck = new SpellCheck(wordList);

        Result<Settings> result3 = settingsAccess.loadSettings();
        if (result3.getInfo().getStatus() != Status.Success) {
            ResultInfo info = result3.getInfo();
            FxDialogs.showException("Error", info.getMessage(), info.getException());
            return;
        }
        Settings settings = result3.getData();
        HistoryOnStartup historyOnStartup = settings.getHistoryOnStartup();

        if (historyOnStartup == HistoryOnStartup.OPEN_NEW_WORD) {
            searchWord(wordList.get(0));
        } else if (historyOnStartup == HistoryOnStartup.OPEN_WHERE_YOU_LEFT_OFF && history.count() > 0) {
            searchWord(history.current());
        } else if (historyOnStartup == HistoryOnStartup.OPEN_FROM_WORDLIST) {
            searchWord(RandomUtil.getRandomItem(wordList));
        }

        wordListView.setItems(FXCollections.observableArrayList(wordList));
        wordListView.setOnMouseClicked(onMouseClickedWordList);
        otherWordListView.setItems(otherWordList);
        otherWordListView.setOnMouseClicked(onMouseClickedOtherWordList);
        otherWordListView.setCellFactory(wordListCellCb);

        searchButton.setOnAction(onSearch);
        inputTextField.setOnAction(onSearch);
        inputTextField.setOnKeyReleased(onKeyPressed);
        prevButton.setOnAction(prevHistory);
        nextButton.setOnAction(nextHistory);
        addButton.setOnAction(addWord);
        settingButton.setOnAction(openSettings);
        aboutButton.setOnAction(openAbout);

        definitionTextArea.setOnMouseClickedArea(onDoubleClickedDefinitionArea);
        App.mainAppActive = true;
    }

    private void searchWord(String query) {
        searchWord(query, true);
    }
    private void searchWord(String query, Boolean updateOtherWordListView) {
        query = WordUtil.normalize(query);
        Result<ArrayList<Word>> result = dataAccess.selectDefinitionFrom(query);

        if (result.getInfo().getStatus() != Status.Success) {
            ResultInfo info = result.getInfo();
            FxDialogs.showException("Error", info.getMessage(), info.getException());
            return;
        }

        ArrayList<Word> data = result.getData();
        if (data.size() > 0) {
            Word word = data.get(0);
            definitionTextArea.setContent(word);
            updateHistory(word);
        }

        if (!updateOtherWordListView) return;

        otherWordList.clear();
        if (data.size() > 1) {
            for (int i = 1; i < data.size(); i++) {
                Word otherWord = data.get(i);
                otherWordList.add(otherWord);
            }
            otherWordListView.setVisible(true);
        }
        else {
            otherWordListView.setVisible(false);
        }

        if (data.size() == 0) { // word not found
            definitionTextArea.setContent(getSuggestions(query));
        }
    }

    private String getSuggestions(String wrongWord) {
        List<String> candidates = spellCheck.candidates(wrongWord);
        StringBuilder sb = new StringBuilder();

        sb.append("\n");
        sb.append("No match for \"" + wrongWord + "\" in the dictionary\n");
        sb.append("\n");

        if (candidates == null || (candidates.size() == 1 && candidates.get(0).equals(wrongWord))) {
            return sb.toString();
        }

        sb.append("Did you mean:\n");
        for (String candidate: candidates)
        {
            sb.append(" " + Constants.U_Bullet + " " + candidate + "\n");
        }

        return sb.toString();
    }

    private void updateHistory(Word word) {
        if (word == null) return;

        if (!word.getName().equals(history.current())) {
            history.add(word.getName());
        }
        onHistoryChanged();
    }

    private EventHandler<MouseEvent> onMouseClickedWordList = new EventHandler<>() {

        @Override
        public void handle(MouseEvent event) {
            if(event.getButton() == MouseButton.PRIMARY) {
                inputTextField.setText(getSelectedWord());

                if(event.getClickCount() == 2) {
                    String query = inputTextField.getText();
                    searchWord(query);
                }
            }
        }
    };
    private EventHandler<MouseEvent> onMouseClickedOtherWordList = new EventHandler<>() {

        @Override
        public void handle(MouseEvent event) {
            if(event.getButton() == MouseButton.PRIMARY) {
                Word otherWord = getSelectedOtherWord();
                inputTextField.setText(otherWord.getName());

                if(event.getClickCount() == 2) {
                    searchWord(otherWord.getId(), false);
                }
            }
        }
    };

    private EventHandler<KeyEvent> onKeyPressed = (e) -> {
        String query = inputTextField.getText();
        int selectedIndex = BinarySearch.prefix(query, wordList);

        wordListView.getSelectionModel().select(selectedIndex);
        wordListView.scrollTo(selectedIndex);
    };

    private EventHandler<ActionEvent> onSearch = (e) -> {
        String query = inputTextField.getText();
        searchWord(query);
    };

    private void onHistoryChanged() {
        boolean canGoToPreviousHistory = (!(history == null || history.count() == 0) && !history.isFirst());
        boolean canGoToNextHistory = (!(history == null || history.count() == 0) && !history.isLast());
        prevButton.setDisable(!canGoToPreviousHistory);
        nextButton.setDisable(!canGoToNextHistory);
    }

    private EventHandler<ActionEvent> prevHistory = (e) -> {
        searchWord(history.previous());
        onHistoryChanged();
    };

    private EventHandler<ActionEvent> nextHistory = (e) -> {
        searchWord(history.next());
        onHistoryChanged();
    };

    private EventHandler<MouseEvent> onDoubleClickedDefinitionArea = (event) -> {
        if (event.getClickCount() == 2) {
            searchWord(definitionTextArea.getSelectedText());
        }
    };

    private EventHandler<ActionEvent> addWord = (e) -> {
        Result<Settings> result = settingsAccess.loadSettings();
        if (result.getInfo().getStatus() == Status.Success) {
            Settings settings = result.getData();
            List<String> wordList = settings.getCustomWordList();
            String currentWord = history.current();

            if (!wordList.contains(currentWord)) {
                wordList.add(currentWord);
            }

            settingsAccess.saveSettings(settings);
        }
    };
    private EventHandler<ActionEvent> openSettings = (e) -> {
        SettingsController.open();
    };
    private EventHandler<ActionEvent> openAbout = (e) -> {
        AboutController.open();
    };

    private void onClose() {
        Result<Settings> result = settingsAccess.loadSettings();
        if (result.getInfo().getStatus() != Status.Success) {
            ResultInfo info = result.getInfo();
            FxDialogs.showException("Error", info.getMessage(), info.getException());
        }
        else {
            Settings settings = result.getData();
            history.trim(settings.getMaxHistory());
            historyAccess.saveHistory(history);
        }
    }
}
