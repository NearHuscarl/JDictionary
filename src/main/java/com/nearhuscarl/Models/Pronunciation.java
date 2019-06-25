package com.nearhuscarl.Models;

import com.google.gson.annotations.SerializedName;

public class Pronunciation {
    private String prefix;
    private String ipa = "";
    @SerializedName("filename")
    private String fileName;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getIpa() {
        return ipa;
    }

    public void setIpa(String ipa) {
        this.ipa = ipa;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getIpaText() {
        return ipa != null ? "/" + ipa + "/" : "";
    }

    @Override
    public String toString() {
        return getPrefix() + " " + getIpaText();
    }
}
