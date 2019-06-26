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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

public class HistoryAccess<T> {

        public Result loadHistory() {
        History<String> history = new History<>();

        try
        {
            String savePath = Paths.get(System.getProperty("user.dir"), "history.xml").toString();
            File historyFile = new File(savePath);

            if (!historyFile.exists()) {
                return new Result<>(new ResultInfo(Status.Success), history);
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(historyFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            Node maxHistory = XmlUtil.getElementByTagName(doc.getDocumentElement(), "maxHistory");
            history.setMaxHistory(Integer.parseInt(XmlUtil.getText(maxHistory)));

            Node currentIndex = XmlUtil.getElementByTagName(doc.getDocumentElement(), "currentIndex");
            history.setCurrentIndex(Integer.parseInt(XmlUtil.getText(currentIndex)));

            NodeList words = doc.getDocumentElement().getElementsByTagName("word");
            ArrayList<String> col = new ArrayList<>();
            for (int i = 0; i < words.getLength(); i++) {
                col.add(words.item(i).getTextContent());
            }
            history.setCollection(col);

        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
            return new Result<>(new ResultInfo(Status.Failed, e.getMessage(), e), history);
        }
        return new Result<>(new ResultInfo(Status.Success), history);
    }


    public Result saveHistory(History<T> history) {
        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // root element
            Element rootElement = doc.createElement("history");
            doc.appendChild(rootElement);

            // child element of root
            Element maxHistory = doc.createElement("maxHistory");
            maxHistory.appendChild(doc.createTextNode(Integer.toString(history.getMaxHistory())));
            rootElement.appendChild(maxHistory);

            Element currentIndex = doc.createElement("currentIndex");
            currentIndex.appendChild(doc.createTextNode(Integer.toString(history.getCurrentIndex())));
            rootElement.appendChild(currentIndex);

            Element collection = doc.createElement("collection");
            for (T item: history.getCollection()) {
                Element word = doc.createElement("word");
                word.appendChild(doc.createTextNode(item.toString()));
                collection.appendChild(word);
            }
            rootElement.appendChild(collection);

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            String savePath = Paths.get(System.getProperty("user.dir"), "history.xml").toString();
            StreamResult result = new StreamResult(new File(savePath));
            transformer.transform(source, result);
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
            return new Result<>(new ResultInfo(Status.Failed, e.getMessage(), e), null);
        }
        return new Result<>(new ResultInfo(Status.Success), null);
    }
}
