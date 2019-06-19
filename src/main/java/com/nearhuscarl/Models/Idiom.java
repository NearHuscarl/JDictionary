package com.nearhuscarl.Models;

import com.nearhuscarl.Helpers.Helpers;

import java.text.MessageFormat;
import java.util.ArrayList;

public class Idiom {
    private String name;
    private ArrayList<Definition> definitions; // Idiom Definitions dont have Property

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Definition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(ArrayList<Definition> definitions) {
        this.definitions = definitions;
    }

    @Override
    public String toString() {
        return MessageFormat.format("Name: {0}\n", name)
                + MessageFormat.format("Definitions: {0}\n", Helpers.List2String(definitions));
    }
}
