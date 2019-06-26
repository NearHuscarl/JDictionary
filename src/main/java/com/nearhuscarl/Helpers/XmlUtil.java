package com.nearhuscarl.Helpers;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtil {
    public static Node getElementByTagName(Element element, String name) {
        NodeList nodeList = element.getElementsByTagName(name);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0);
        }
        return null;
    }

    public static String getText(Node node) {
        if (node == null) {
            return "";
        }
        return node.getTextContent();
    }
}
