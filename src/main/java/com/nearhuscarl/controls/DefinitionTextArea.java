package com.nearhuscarl.controls;
import com.nearhuscarl.Constants;
import javafx.beans.NamedArg;
import javafx.event.EventType;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.event.MouseOverTextEvent;
import org.fxmisc.richtext.model.*;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefinitionTextArea extends StyleClassedTextArea {

    private static final String[] LABELS_WITH_BRACKET = new String[] {
            "countable", "uncountable", "singular", "plural", "transitive", "intransitive",
            "no passive", "only before",
    };

    private static final String[] LABELS_WITH_PARENTHESES = new String[] {
            "of soldiers",
            "((especially|both) )?(British English|North American English)",
            "formal", "informal", "saying", "old-fashioned", "humorous", "literary",
    };

    // \n.*?(?:\n\s)
    private static final String KEYWORD_PATTERN = "(?i)^(\\w+) (\\w+)"; // match the first word at the first line
    private static final String BRE_PATTERN = "BrE";
    private static final String NAME_PATTERN = "NAmE";
    private static final String LABELS_WITH_BRACKET_PATTERN =
            "\\[(" + String.join("|", LABELS_WITH_BRACKET) + ")(,[ a-z,]+)?\\]";
    private static final String LABELS_WITH_PARENTHESES_PATTERN =
            "\\((" + String.join("|", LABELS_WITH_PARENTHESES) + ")(,[ a-z,]+)?\\)";
    private static final String EXAMPLE_PATTERN = Constants.U_Bullet + ".*\\n";
    private static final String HEADER_PATTERN = "(?m)^([0-9]+\\..*|Idioms|Other Examples)$";
    private static final String ICON_PATTERN = "[\\uf000-\\uffff]";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PBRE>" + BRE_PATTERN + ")"
                    + "|(?<PNAME>" + NAME_PATTERN + ")"
                    + "|(?<ICON>" + ICON_PATTERN + ")"
                    + "|(?<LABEL1>" + LABELS_WITH_BRACKET_PATTERN + ")"
                    + "|(?<LABEL2>" + LABELS_WITH_PARENTHESES_PATTERN + ")"
                    + "|(?<EXAMPLE>" + EXAMPLE_PATTERN + ")"
                    + "|(?<HEADER>" + HEADER_PATTERN + ")"
    );

    public DefinitionTextArea(@NamedArg("document") EditableStyledDocument<Collection<String>, String, Collection<String>> document,
                                @NamedArg("preserveStyle") boolean preserveStyle) {
        super(document, preserveStyle);
        setupSyntaxHighlighting();
    }

    public DefinitionTextArea(@NamedArg("preserveStyle") boolean preserveStyle) {
        super(preserveStyle);
        setupSyntaxHighlighting();
    }

    /**
     * Creates a text area with empty text content.
     */
    public DefinitionTextArea()  {
        super();
        setupSyntaxHighlighting();
    }

    private void setupSyntaxHighlighting() {
        // recompute the syntax highlighting 500 ms after user stops editing area
        Subscription cleanupWhenNoLongerNeedIt = this

                // plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
                // multi plain changes = save computation by not rerunning the code multiple times
                //   when making multiple changes (e.g. renaming a method at multiple parts in file)
                .multiPlainChanges()

                // do not emit an event until 500 ms have passed since the last emission of previous stream
                .successionEnds(Duration.ofMillis(500))

                // run the following code block when previous stream emits an event
                .subscribe(ignore -> this.setStyleSpans(0, computeHighlighting(this.getText())));

        // when no longer need syntax highlighting and wish to clean up memory leaks
        // run: `cleanupWhenNoLongerNeedIt.unsubscribe();`
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        String lastIpa = "";
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        while(matcher.find()) {
            String styleClass = "definition-text ";

            if (matcher.group("KEYWORD") != null) {
                styleClass = "keyword";
            }
            else if (matcher.group("PBRE") != null) {
                styleClass = "blue";
                lastIpa = "bre";
            }
            else if (matcher.group("PNAME") != null) {
                styleClass = "red";
                lastIpa = "name";
            }
            else if (matcher.group("ICON") != null) {
                styleClass = "icon button";
                if (lastIpa.equals("bre")) styleClass += " icon-blue";
                if (lastIpa.equals("name")) styleClass += " icon-red";
            }
            else if (matcher.group("LABEL1") != null) {
                styleClass += "label";
            }
            else if (matcher.group("LABEL2") != null) {
                styleClass += "label";
            }
            else if (matcher.group("EXAMPLE") != null) {
                styleClass += "example";
            }
            else if (matcher.group("HEADER") != null) {
                styleClass += "header";
            }
            /* never happens */ assert styleClass != null;

            if (styleClass.equals("keyword")) {
                String wordAndWordform = matcher.group(1);
                String word = matcher.group(2);
                // String wordform = matcher.group(3);

                spansBuilder.add(Collections.singleton("keyword"), word.length() - lastKwEnd);
                spansBuilder.add(Collections.singleton("wordform"), wordAndWordform.length() - word.length());
            }
            else {
                spansBuilder.add(Collections.singleton("definition-text"), matcher.start() - lastKwEnd);
                spansBuilder.add(Arrays.asList(styleClass.split(" ")), matcher.end() - matcher.start());
            }
            lastKwEnd = matcher.end();
        }

        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
