package com.nearhuscarl.Controllers;

import com.nearhuscarl.Helpers.*;
import com.nearhuscarl.Models.History;
import com.nearhuscarl.controls.DefinitionTextArea;
import com.nearhuscarl.Data.DataAccess;
import com.nearhuscarl.Models.Word;
import com.nearhuscarl.controls.FxDialogs;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

public class Main implements Initializable {
    private DataAccess dataAccess;
    public DefinitionTextArea definitionTextArea;
    public ListView wordListView;
    public TextField inputTextField;
    public Button searchButton;
    private ArrayList<String> wordList;
    private History<String> history = new History<>();

    private String getSelectedWord() {
        return (String)wordListView.getSelectionModel().getSelectedItem();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataAccess = new DataAccess();
        Result<ArrayList<String>> result = dataAccess.selectNames();

        if (result.getInfo().getStatus() != Status.Success) {
            ResultInfo info = result.getInfo();
            FxDialogs.showException("Error", info.getMessage(), info.getException());
            return;
        }

        // We dont need ObservableList because word list will only changed (initialized)
        // once on startup
        wordList = result.getData();

        if (wordList.size() > 0) {
            searchWord(wordList.get(0));
        }

        wordListView.setItems(FXCollections.observableArrayList(wordList));
        wordListView.setOnMouseClicked(onMouseClickedWordList);

        searchButton.setOnAction(onSearch);
        inputTextField.setOnAction(onSearch);
        inputTextField.setOnKeyReleased(onKeyPressed);

        definitionTextArea.setOnMouseClickedArea(onDoubleClickedDefinitionArea);
    }

    private void searchWord(String query) {
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
        }
    }

    private EventHandler<MouseEvent> onMouseClickedWordList = new EventHandler<MouseEvent>() {

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

    public void onMouseClicked() {
        String text = definitionTextArea.getText();
    }

    private EventHandler<MouseEvent> onDoubleClickedDefinitionArea = event -> {
        if (event.getClickCount() == 2) {
            String query = WordUtil.normalize(definitionTextArea.getSelectedText());
            searchWord(query);
        }
    };
}
