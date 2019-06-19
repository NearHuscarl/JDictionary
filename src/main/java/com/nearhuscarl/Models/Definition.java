package com.nearhuscarl.Models;

import com.nearhuscarl.Helpers.Helpers;

import java.text.MessageFormat;
import java.util.ArrayList;

public class Definition {
    private String property;
    private String label;
    private String refer;
    private ArrayList<Reference> references;
    private String description;
    private ArrayList<String> examples;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getRefer() {
        return refer;
    }

    public void setRefer(String refer) {
        this.refer = refer;
    }

    public ArrayList<Reference> getReferences() {
        return references;
    }

    public void setReferences(ArrayList<Reference> references) {
        this.references = references;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getExamples() {
        return examples;
    }

    public void setExamples(ArrayList<String> examples) {
        this.examples = examples;
    }

    @Override
    public String toString() {
        var stringBuilder = new StringBuilder();

        if (property != null) {
            stringBuilder.append(MessageFormat.format("Property: {0}\n", property));
        }
        if (label != null) {
            stringBuilder.append(MessageFormat.format("Label: {0}\n", label));
        }
        if (refer != null) {
            stringBuilder.append(MessageFormat.format("Refer: {0}\n", refer));
        }
        if (references != null) {
            stringBuilder.append(MessageFormat.format("References: {0}\n", Helpers.List2String(references)));
        }
        if (description != null) {
            stringBuilder.append(MessageFormat.format("Description: {0}\n", description));
        }
        if (examples != null) {
            stringBuilder.append(MessageFormat.format("Examples: {0}\n", Helpers.List2String(examples)));
        }

        return stringBuilder.toString();
    }
}
