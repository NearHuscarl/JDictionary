package com.nearhuscarl.Models;

public class Settings {
    private int maxHistory = 1000;
    private HistoryOnStartup historyOnStartup = HistoryOnStartup.OPEN_NEW_WORD;

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
}
