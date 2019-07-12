package com.nearhuscarl.Data;

import com.nearhuscarl.Models.*;
import com.nearhuscarl.Helpers.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SettingsAccess {
    public Result loadSettings() {
        Settings settings = new Settings();

        try {
            String savePath = Paths.get(System.getProperty("user.dir"), "settings.xml").toString();
            File settingsFile = new File(savePath);

            if (!settingsFile.exists()) {
                return new Result<>(new ResultInfo(Status.Success), settings);
            }

            Document doc = XmlUtil.createDocument(settingsFile);

            Node maxHistory = XmlUtil.getElementByTagName(doc.getDocumentElement(), "maxHistory");
            settings.setMaxHistory(Integer.parseInt(XmlUtil.getText(maxHistory)));

            Node historyOnStartup = XmlUtil.getElementByTagName(doc.getDocumentElement(), "historyOnStartup");
            settings.setHistoryOnStartup(HistoryOnStartup.valueOf(XmlUtil.getText(historyOnStartup)));

            Node learningEnabled = XmlUtil.getElementByTagName(doc.getDocumentElement(), "learningEnabled");
            settings.setLearningEnabled(Boolean.parseBoolean(XmlUtil.getText(learningEnabled)));

            Node minInterval = XmlUtil.getElementByTagName(doc.getDocumentElement(), "minInterval");
            settings.setMinInterval(Integer.parseInt(XmlUtil.getText(minInterval)));

            Node secInterval = XmlUtil.getElementByTagName(doc.getDocumentElement(), "secInterval");
            settings.setSecInterval(Integer.parseInt(XmlUtil.getText(secInterval)));

            Node vocabularySource = XmlUtil.getElementByTagName(doc.getDocumentElement(), "vocabularySource");
            settings.setVocabularySource(VocabularySource.valueOf(XmlUtil.getText(vocabularySource)));

            NodeList words = doc.getDocumentElement().getElementsByTagName("word");
            ArrayList<String> customWordList = new ArrayList<>();
            for (int i = 0; i < words.getLength(); i++) {
                customWordList.add(words.item(i).getTextContent());
            }
            settings.setCustomWordList(customWordList);

            Node secDisplay = XmlUtil.getElementByTagName(doc.getDocumentElement(), "secDisplay");
            settings.setSecDisplay(Integer.parseInt(XmlUtil.getText(secDisplay)));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new Result<>(new ResultInfo(Status.Failed, e.getMessage(), e), settings);
        }
        return new Result<>(new ResultInfo(Status.Success), settings);
    }


    public Result saveSettings(Settings settings) {
        try {
            Document doc = XmlUtil.createDocument();

            // root element
            Element rootElement = doc.createElement("settings");
            doc.appendChild(rootElement);

            // child element of root
            Element maxHistory = doc.createElement("maxHistory");
            maxHistory.appendChild(doc.createTextNode(Integer.toString(settings.getMaxHistory())));
            rootElement.appendChild(maxHistory);

            Element historyOnStartup = doc.createElement("historyOnStartup");
            historyOnStartup.appendChild(doc.createTextNode(settings.getHistoryOnStartup().name()));
            rootElement.appendChild(historyOnStartup);

            Element isLearningEnabled = doc.createElement("learningEnabled");
            isLearningEnabled.appendChild(doc.createTextNode(Boolean.toString(settings.isLearningEnabled())));
            rootElement.appendChild(isLearningEnabled);

            Element minInterval = doc.createElement("minInterval");
            minInterval.appendChild(doc.createTextNode(Integer.toString(settings.getMinInterval())));
            rootElement.appendChild(minInterval);

            Element secInterval = doc.createElement("secInterval");
            secInterval.appendChild(doc.createTextNode(Integer.toString(settings.getSecInterval())));
            rootElement.appendChild(secInterval);

            Element vocabularySource = doc.createElement("vocabularySource");
            vocabularySource.appendChild(doc.createTextNode(settings.getVocabularySource().name()));
            rootElement.appendChild(vocabularySource);

            Element customWordList = doc.createElement("customWordList");
            for (String item : settings.getCustomWordList()) {
                Element word = doc.createElement("word");
                word.appendChild(doc.createTextNode(item));
                customWordList.appendChild(word);
            }
            rootElement.appendChild(customWordList);

            Element secDisplay = doc.createElement("secDisplay");
            secDisplay.appendChild(doc.createTextNode(Integer.toString(settings.getSecDisplay())));
            rootElement.appendChild(secDisplay);

            // write the content into xml file
            String savePath = Paths.get(System.getProperty("user.dir"), "settings.xml").toString();
            XmlUtil.transform(doc, savePath);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new Result<>(new ResultInfo(Status.Failed, e.getMessage(), e), null);
        }
        return new Result<>(new ResultInfo(Status.Success), null);
    }
}
