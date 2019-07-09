package com.nearhuscarl.Data;

import com.nearhuscarl.Models.Result;
import com.nearhuscarl.Models.ResultInfo;
import com.nearhuscarl.Models.Status;
import com.nearhuscarl.Helpers.XmlUtil;
import com.nearhuscarl.Models.HistoryOnStartup;
import com.nearhuscarl.Models.Settings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Paths;

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
            Element rootElement = doc.createElement("history");
            doc.appendChild(rootElement);

            // child element of root
            Element maxHistory = doc.createElement("maxHistory");
            maxHistory.appendChild(doc.createTextNode(Integer.toString(settings.getMaxHistory())));
            rootElement.appendChild(maxHistory);

            Element historyOnStartup = doc.createElement("historyOnStartup");
            historyOnStartup.appendChild(doc.createTextNode(settings.getHistoryOnStartup().name()));
            rootElement.appendChild(historyOnStartup);

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
