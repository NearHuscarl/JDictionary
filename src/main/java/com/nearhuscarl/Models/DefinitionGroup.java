package com.nearhuscarl.Models;

import com.nearhuscarl.Helpers.Helpers;

import java.text.MessageFormat;
import java.util.ArrayList;

public class DefinitionGroup {
    private String namespace;
    private ArrayList<Definition> definitions = new ArrayList<>();

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public ArrayList<Definition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(ArrayList<Definition> definitions) {
        this.definitions = definitions;
    }

    @Override
    public String toString() {
        return MessageFormat.format("Namespace: {0}\n", namespace)
                + MessageFormat.format("Definitions: {0}\n", Helpers.List2String(definitions));
    }
}
