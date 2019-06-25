package com.nearhuscarl.Models;

import com.google.gson.annotations.SerializedName;
import com.nearhuscarl.Constants;
import com.nearhuscarl.Helpers.Helpers;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;

import java.text.MessageFormat;
import java.util.ArrayList;

public class Word {
    private String id;
    private String name;
    private String wordform;
    private ArrayList<Pronunciation> pronunciations = new ArrayList<>();
    private ArrayList<Reference> references = new ArrayList<>();
    @SerializedName("definitions")
    private ArrayList<DefinitionGroup> definitionGroups = new ArrayList<>();
    @SerializedName("extra_examples")
    private ArrayList<String> extraExamples = new ArrayList<>();
    private ArrayList<Idiom> idioms = new ArrayList<>();
    private ArrayList<String> similar = new ArrayList<>();

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

    public String getWordform() {
        if (wordform == null) {
            return "none";
        }
        return wordform;
    }

    public void setWordform(String wordform) {
        this.wordform = wordform;
    }

    public ArrayList<Pronunciation> getPronunciations() {
        return pronunciations;
    }

    public void setPronunciations(ArrayList<Pronunciation> pronunciations) {
        this.pronunciations = pronunciations;
    }

    public ArrayList<Reference> getReferences() {
        return references;
    }

    public void setReferences(ArrayList<Reference> references) {
        this.references = references;
    }

    public void setDefinitionGroups(ArrayList<DefinitionGroup> definitionGroups) {
        this.definitionGroups = definitionGroups;
    }

    public ArrayList<DefinitionGroup> getDefinitionGroups() {
        return definitionGroups;
    }

    public void setExtraExamples(ArrayList<String> extraExamples) {
        this.extraExamples = extraExamples;
    }

    public ArrayList<String> getExtraExamples() {
        return extraExamples;
    }

    public ArrayList<Idiom> getIdioms() {
        return idioms;
    }

    public void setIdioms(ArrayList<Idiom> idioms) {
        this.idioms = idioms;
    }

    public ArrayList<String> getSimilar() {
        return similar;
    }

    public void setSimilar(ArrayList<String> similar) {
        this.similar = similar;
    }

    private void appendLine(StringBuilder sb) {
        sb.append("\n");
    }
    private void appendLine(StringBuilder sb, String str) {
        sb.append(str + "\n");
    }

    private void appendHeadline(StringBuilder sb, String str) {
        sb.append(str + "\n\n");
    }

    // return name, wordform and pronunciation ipa
    private String prettySummary() {
        StringBuilder sb = new StringBuilder();

        appendLine(sb, name + " " + getWordform());

        for(Pronunciation pronunciation: pronunciations) {
            appendLine(sb, pronunciation.toString() + " " + FontAwesomeIcon.VOLUME_UP.unicode());
        }

        appendLine(sb);
        return sb.toString();
    }

    private String prettyDefinitionGroups() {
        int namespaceIndex = 0;
        StringBuilder sb = new StringBuilder();

        for (DefinitionGroup definitionGroup: definitionGroups) {
            if (definitionGroup.getNamespace() != null)
                appendHeadline(sb, ++namespaceIndex + ". " + definitionGroup.getNamespace());
            appendDefinitions(sb, definitionGroup.getDefinitions());
        }

        return sb.toString();
    }

    private StringBuilder appendDefinitions(StringBuilder sb, ArrayList<Definition> definitions) {
        for (Definition definition: definitions) {
            appendDefinition(sb, definition);
        }
        return sb;
    }

    private StringBuilder appendDefinition(StringBuilder sb, Definition definition) {
        sb.append("  ");

        String label = definition.getLabel();
        if (label != null)
        {
            if (label.charAt(0) != '(')
                sb.append("(" + label + ") ");
            else
                sb.append(label + " ");
        }

        String refer = definition.getRefer();
        if (refer != null)
        {
            sb.append(refer);
        }

        String property = definition.getProperty();
        if (property != null)
        {
            sb.append(property + " ");
        }

        String description = definition.getDescription();
        if (description != null)
            appendLine(sb, description);

        appendExamples(sb, definition.getExamples());
        appendReferences(sb, definition.getReferences());
        return sb;
    }

    private StringBuilder appendExamples(StringBuilder sb, ArrayList<String> examples) {
        if (examples.size() == 0)
            return sb;

        String bullet = "  " + Constants.U_Bullet + " ";

        for (String example: examples) {
            appendLine(sb, bullet + example);
        }

        appendLine(sb);
        return sb;
    }


    private StringBuilder appendReferences(StringBuilder sb, ArrayList<Reference> references) {
        if (references == null) {
            return sb;
        }

        for (Reference reference: references) {
            appendLine(sb, Constants.U_Right_Arrow + " " + reference.getName());
        }

        appendLine(sb);
        return sb;
    }


    private String prettyExtraExamples() {
        if (extraExamples.size() == 0)
            return "";

        StringBuilder sb = new StringBuilder();
        String bullet = " " + Constants.U_Bullet_ALT + " ";

        appendHeadline(sb, "Other Examples");


        for (String example: extraExamples) {
            appendLine(sb, bullet + example);
        }

        appendLine(sb);

        return sb.toString();
    }

    private String prettyIdioms() {
        if (idioms == null || idioms.size() == 0)
            return "";

        StringBuilder sb = new StringBuilder();
        appendHeadline(sb, "Idioms");

        for (Idiom idiom: idioms)
        {
            appendLine(sb, idiom.getName());

            for (Definition definition: idiom.getDefinitions()) {
                appendDefinition(sb, definition);
            }
        }

        return sb.toString();
    }

    public String toPrettyString() {
        var stringBuilder = new StringBuilder();

        stringBuilder.append(prettySummary());
        stringBuilder.append(appendReferences(new StringBuilder(), references));
        stringBuilder.append(prettyDefinitionGroups());
        stringBuilder.append(prettyExtraExamples());
        stringBuilder.append(prettyIdioms());

        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        var stringBuilder = new StringBuilder();

        stringBuilder.append(MessageFormat.format("ID: {0}\n", id));
        stringBuilder.append(MessageFormat.format("Name: {0}\n", name));
        stringBuilder.append(MessageFormat.format("Wordform: {0}\n", wordform));
        stringBuilder.append(MessageFormat.format("Pronunciation: {0}\n", Helpers.List2String(pronunciations)));
        stringBuilder.append(MessageFormat.format("References: {0}\n", Helpers.List2String(references)));
        stringBuilder.append(MessageFormat.format("DefinitionGroups: {0}\n", Helpers.List2String(definitionGroups)));
        stringBuilder.append(MessageFormat.format("ExtraExamples: {0}\n", Helpers.List2String(extraExamples)));
        stringBuilder.append(MessageFormat.format("Idioms: {0}\n", Helpers.List2String(idioms)));
        stringBuilder.append(MessageFormat.format("Similar: {0}\n", Helpers.List2String(similar)));

        return stringBuilder.toString();
    }
}
