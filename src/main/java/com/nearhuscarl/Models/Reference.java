package com.nearhuscarl.Models;

import java.text.MessageFormat;

public class Reference {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return MessageFormat.format("ID: {0}, Name: {1}", id, name);
    }
}
