package com.nearhuscarl.Models;

import java.util.ArrayList;
import java.util.List;

public class Settings {
    private int maxHistory = 1000;
    private HistoryOnStartup historyOnStartup = HistoryOnStartup.OPEN_NEW_WORD;
    private boolean isLearningEnabled = true;
    private int minInterval = 20;
    private int secInterval = 0;
    private VocabularySource vocabularySource = VocabularySource.ALL;
    private List<String> customWordList = new ArrayList<>();
    private int secDisplay = 12;

    public int getMaxHistory() {
        return maxHistory;
    }

    public void setMaxHistory(int maxHistory) {
        this.maxHistory = maxHistory;
    }

    public HistoryOnStartup getHistoryOnStartup() {
        return historyOnStartup;
    }

    public void setHistoryOnStartup(HistoryOnStartup historyOnStartup) {
        this.historyOnStartup = historyOnStartup;
    }
    public boolean isLearningEnabled() {
        return isLearningEnabled;
    }

    public void setLearningEnabled(boolean learningEnabled) {
        isLearningEnabled = learningEnabled;
    }

    public int getMinInterval() {
        return minInterval;
    }

    public void setMinInterval(int minInterval) {
        this.minInterval = minInterval;
    }

    public int getSecInterval() {
        return secInterval;
    }

    public void setSecInterval(int secInterval) {
        this.secInterval = secInterval;
    }

    public VocabularySource getVocabularySource() {
        return vocabularySource;
    }

    public void setVocabularySource(VocabularySource vocabularySource) {
        this.vocabularySource = vocabularySource;
    }

    public List<String> getCustomWordList() {
        return customWordList;
    }

    public void setCustomWordList(List<String> customWordList) {
        this.customWordList = customWordList;
    }

    public int getSecDisplay() {
        return secDisplay;
    }

    public void setSecDisplay(int secDisplay) {
        this.secDisplay = secDisplay;
    }
}
