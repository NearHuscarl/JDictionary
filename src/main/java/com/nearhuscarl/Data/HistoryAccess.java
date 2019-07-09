package com.nearhuscarl.Data;

import com.nearhuscarl.Helpers.Result;
import com.nearhuscarl.Helpers.ResultInfo;
import com.nearhuscarl.Helpers.Status;
import com.nearhuscarl.Helpers.XmlUtil;
import com.nearhuscarl.Models.History;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

public class HistoryAccess<T> {

    public Result loadHistory() {
        History<String> history = new History<>();

        try {
            String savePath = Paths.get(System.getProperty("user.dir"), "history.xml").toString();
            File historyFile = new File(savePath);

            if (!historyFile.exists()) {
                return new Result<>(new ResultInfo(Status.Success), history);
            }

            Document doc = XmlUtil.createDocument(historyFile);

            Node currentIndex = XmlUtil.getElementByTagName(doc.getDocumentElement(), "currentIndex");
            history.setCurrentIndex(Integer.parseInt(XmlUtil.getText(currentIndex)));

            NodeList words = doc.getDocumentElement().getElementsByTagName("word");
            ArrayList<String> col = new ArrayList<>();
            for (int i = 0; i < words.getLength(); i++) {
                col.add(words.item(i).getTextContent());
            }
            history.setCollection(col);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new Result<>(new ResultInfo(Status.Failed, e.getMessage(), e), history);
        }
        return new Result<>(new ResultInfo(Status.Success), history);
    }


    public Result saveHistory(History<T> history) {
        try {
            Document doc = XmlUtil.createDocument();

            // root element
            Element rootElement = doc.createElement("history");
            doc.appendChild(rootElement);

            // child element of root
            Element currentIndex = doc.createElement("currentIndex");
            currentIndex.appendChild(doc.createTextNode(Integer.toString(history.getCurrentIndex())));
            rootElement.appendChild(currentIndex);

            Element collection = doc.createElement("collection");
            for (T item : history.getCollection()) {
                Element word = doc.createElement("word");
                word.appendChild(doc.createTextNode(item.toString()));
                collection.appendChild(word);
            }
            rootElement.appendChild(collection);

            // write the content into xml file
            String savePath = Paths.get(System.getProperty("user.dir"), "history.xml").toString();
            XmlUtil.transform(doc, savePath);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new Result<>(new ResultInfo(Status.Failed, e.getMessage(), e), null);
        }
        return new Result<>(new ResultInfo(Status.Success), null);
    }
}
