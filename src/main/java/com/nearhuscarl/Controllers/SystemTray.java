package com.nearhuscarl.Controllers;

import com.nearhuscarl.App;
import com.nearhuscarl.Data.DataAccess;
import com.nearhuscarl.Data.HistoryAccess;
import com.nearhuscarl.Data.SettingsAccess;
import com.nearhuscarl.Helpers.ListUtil;
import com.nearhuscarl.Helpers.RandomUtil;
import com.nearhuscarl.Models.*;
import com.nearhuscarl.controls.DefinitionTextArea;
import com.nearhuscarl.controls.FxDialogs;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SystemTray {

    private DataAccess dataAccess = new DataAccess();
    private HistoryAccess historyAccess = new HistoryAccess();
    private SettingsAccess settingsAccess = new SettingsAccess();

    private java.awt.MenuItem statusItem;
    private DefinitionTextArea textArea;

    // application stage is stored so that it can be shown and hidden based on system tray icon operations.
    private Stage stage;
    private Stage parentStage;

    // a timer allowing the tray icon to provide a periodic notification event.
    private Timer showPopupTimer;
    private Timer hidePopupTimer;

    private LearningStatus currentStatus;
    private int minInterval;
    private int secInterval;
    private int secDisplay;
    private List<String> wordList;

    private void setCurrentStatus(LearningStatus status) {
        setCurrentStatus(status, false);
    }
    private void setCurrentStatus(LearningStatus status, boolean reset) {
        switch (status) {
            case RUN:
                if (reset) stopShowTimer();
                startShowTimer();
                Platform.runLater(() -> {
                    if (statusItem != null) statusItem.setLabel("Stop");
                });
                break;
            case STOP:
                stopShowTimer();
                Platform.runLater(() -> {
                    if (statusItem != null) statusItem.setLabel("Resume");
                });
                break;
        }
        currentStatus = status;
    }

    private static SystemTray Systemtray;
    public static void open() {
        if (Systemtray == null) {
            Systemtray = new SystemTray();
        }
    }

    private SystemTray() {
        // stores a reference to the stage.
        stage = new Stage();

        // instructs the javafx system not to exit implicitly when the last application window is shut.
        Platform.setImplicitExit(false);

        // sets up the tray icon (using awt code run on the swing thread).
        javax.swing.SwingUtilities.invokeLater(this::addAppToTray);

        // out stage will be translucent, so give it a transparent style.
        stage.initStyle(StageStyle.TRANSPARENT);

        int balloonWidth = 330;
        int balloonHeight = 220;
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setX(bounds.getMaxX() - balloonWidth - 10);
        stage.setY(bounds.getMaxY() - balloonHeight - 10);
        stage.setWidth(balloonWidth);
        stage.setHeight(balloonHeight);
        stage.getIcons().add(App.appIcon);
        stage.setAlwaysOnTop(true); // popup always on top of other program
        stage.initStyle(StageStyle.UNDECORATED);

        // need 2 stages since we cannot init multiple styles in 1 stage
        // StageStyle.UTILITY is to remove icon on the taskbar because it's only a notification
        // StageStyle.UNDECORATED is to remove 3 buttons (max/min and close)
        // https://stackoverflow.com/questions/46712293/how-can-i-remove-my-javafx-program-from-the-taskbar
        parentStage = new Stage();
        parentStage.initStyle(StageStyle.UTILITY);
        parentStage.setOpacity(0);
        parentStage.setHeight(0);
        parentStage.setWidth(0);
        stage.initOwner(parentStage);

        Scene scene = new Scene(createContent());
        scene.getStylesheets().add(App.class.getResource("App.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);

        stage.setScene(scene);
    }

    /**
     * @return the the layout for the learning balloon
     */
    private Parent createContent() {
        textArea = new DefinitionTextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        // make text area size expand to parent size
        textArea.setPrefSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        VBox layout = new VBox(textArea);
        layout.setPadding(new Insets(10, 10, 10, 10));

        layout.setOnMouseClicked(e -> {
            // hide balloon on right mouse click event
            if (e.getButton() == MouseButton.SECONDARY) {
                stage.hide();
            }
        });

        layout.setOnMouseEntered(e -> stopHideTimer());
        layout.setOnMouseExited(e -> startHideTimer());

        return layout;
    }

    private java.awt.SystemTray tray;
    private java.awt.TrayIcon trayIcon;

    /**
     * Sets up a system tray icon for the application.
     */
    private void addAppToTray() {
        try {
            // ensure awt toolkit is initialized.
            java.awt.Toolkit.getDefaultToolkit();

            // app requires system tray support, just exit if there is no support.
            if (!java.awt.SystemTray.isSupported()) {
                System.out.println("No system tray support, application exiting.");
                Platform.exit();
            }

            // set up a system tray icon.
            tray = java.awt.SystemTray.getSystemTray();
            URL imageLoc = App.class.getResource("icon-small.png");
            java.awt.Image image = ImageIO.read(imageLoc);
            trayIcon = new java.awt.TrayIcon(image);

            // if the user double-clicks on the tray icon, show the main app stage.
            trayIcon.addActionListener(e -> showStage());

            // if the user selects the default menu item
            // show the main app stage.
            java.awt.MenuItem openItem = new java.awt.MenuItem("JDictionary");
            openItem.addActionListener(openMainApp);

            // Status Stop/Resume showing word in learning word list after a certain amount of time
            statusItem = new java.awt.MenuItem("Stop");
            statusItem.addActionListener(toggleStatus);

            // Open settings/about window
            java.awt.MenuItem settingsItem = new java.awt.MenuItem("Settings");
            settingsItem.addActionListener(openSettings);
            java.awt.MenuItem aboutItem = new java.awt.MenuItem("About");
            aboutItem.addActionListener(openAbout);

            // to really exit the application, the user must go to the system tray icon
            // and select the exit option, this will shutdown JavaFX and remove the
            // tray icon (removing the tray icon will also shut down AWT).
            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
            exitItem.addActionListener(e -> exitSystemTray());

            // setup the popup menu for the application.
            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            popup.add(openItem);
            popup.addSeparator();
            popup.add(statusItem);
            popup.add(settingsItem);
            popup.add(aboutItem);
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            // add the application tray icon to the system tray.
            tray.add(trayIcon);
            loadSettings();
        } catch (java.awt.AWTException | IOException e) {
            System.out.println("Unable to init system tray");
            e.printStackTrace();
        }
    }

    private void stopShowTimer() {
        System.out.println("stopShowTimer");
        if (showPopupTimer != null) {
            showPopupTimer.cancel();
        }
    }
    private void startShowTimer() {
        System.out.println("startShowTimer");
        showPopupTimer = new Timer();
        showPopupTimer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        showStage();
                        startHideTimer();
                    }
                },
                minInterval * 60 * 1000 + secInterval * 1000
        );
    }
    private void stopHideTimer() {
        System.out.println("stopHideTimer");
        if (hidePopupTimer != null) {
            hidePopupTimer.cancel();
        }
    }
    private void startHideTimer() {
        System.out.println("startHideTimer");
        // hide the stage automatically after an amount of time
        hidePopupTimer = new Timer();
        hidePopupTimer.schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        hideStage();
                        startShowTimer();
                    }
                },
                secDisplay * 1000
        );
    }

    private void loadSettings() {
        Result<Settings> result = settingsAccess.loadSettings();

        if (result.getInfo().getStatus() == Status.Success) {
            Settings settings = result.getData();

            setCurrentStatus(settings.isLearningEnabled() ? LearningStatus.RUN : LearningStatus.STOP, true);
            minInterval = settings.getMinInterval();
            secInterval = settings.getSecInterval();
            secDisplay = settings.getSecDisplay();

            switch (settings.getVocabularySource()) {
                case ALL:
                    Result<ArrayList<String>> dataResult = dataAccess.selectNames();
                    if (dataResult.getInfo().getStatus() == Status.Success) {
                        wordList = dataResult.getData();
                    } else {
                        ResultInfo info = dataResult.getInfo();
                        FxDialogs.showException("Error", info.getMessage(), info.getException());
                    }
                    break;
                case HISTORY:
                    Result<History> historyResult = historyAccess.loadHistory();
                    if (historyResult.getInfo().getStatus() == Status.Success) {
                        History<String> history = historyResult.getData();
                        wordList = ListUtil.distinct(history.getCollection());
                    } else {
                        ResultInfo info = historyResult.getInfo();
                        FxDialogs.showException("Error", info.getMessage(), info.getException());
                    }
                    break;
                case CUSTOM_WORDLIST:
                    wordList = settings.getCustomWordList();
                    break;
            }
        } else {
            ResultInfo info = result.getInfo();
            FxDialogs.showException("Error", info.getMessage(), info.getException());
            Platform.exit();
        }
    }

    private ActionListener openMainApp = e -> Platform.runLater(() -> {
        if (!App.mainAppActive) {
            MainController.open();
        }
    });
    private ActionListener openSettings = e -> Platform.runLater(() -> {
        SettingsController.open();
        loadSettings();
    });
    private ActionListener openAbout = e -> Platform.runLater(AboutController::open);

    private ActionListener toggleStatus = e -> {
        switch (currentStatus) {
            case RUN:
                setCurrentStatus(LearningStatus.STOP);
                break;
            case STOP:
                setCurrentStatus(LearningStatus.RUN);
                break;
        }
    };

    private void exitSystemTray() {
        showPopupTimer.cancel();
        Platform.exit();
        tray.remove(trayIcon);
    }

    private boolean setRandomWord() {
        if (wordList == null) return false;

        String randomItem = RandomUtil.getRandomItem(wordList);
        Result<ArrayList<Word>> result = dataAccess.selectDefinitionFrom(randomItem);

        if (result.getInfo().getStatus() == Status.Success) {
            ArrayList<Word> data = result.getData();
            if (data.size() > 0) {
                Word word = data.get(0);
                textArea.setContent(word);
                return true;
            }
            return false;
        } else {
            ResultInfo info = result.getInfo();
            FxDialogs.showException("Error", info.getMessage(), info.getException());
            return false;
        }
    }

    /**
     * Shows the application stage and ensures that it is brought ot the front of all stages.
     */
    private void showStage() {
        Platform.runLater(() -> {
            if (stage != null && setRandomWord()) {
                parentStage.show();
                stage.show();
            }
        });
    }

    private void hideStage() {
        Platform.runLater(() -> {
            if (stage != null) {
                parentStage.hide();
                stage.hide();
            }
        });
    }
}
