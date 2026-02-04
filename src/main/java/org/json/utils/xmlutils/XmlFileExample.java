package org.json.utils.xmlutils;
import com.example.xmlutils.XmlFileUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlFileExample {
    public static void main(String[] args) throws XmlFileException {
        String path = "test.xml";
        String root = "users";

        // Create
        XmlFileUtils.createXmlFile(path, root);

        // Read
        Document doc = XmlFileUtils.readXmlFile(path);
        System.out.println("Root element: " + doc.getDocumentElement().getTagName());

        // Add a child element
        doc = XmlFileUtils.readXmlFile(path);
        Element user = doc.createElement("user");
        user.setAttribute("id", "1");
        user.setTextContent("Alice");
        doc.getDocumentElement().appendChild(user);
        XmlFileUtils.writeXmlFile(path, doc);

        // Get element value
        String value = XmlFileUtils.getElementValue(path, "user");
        System.out.println("User value: " + value);

        // Pretty print
        System.out.println(XmlFileUtils.prettyPrint(path));

        // Validate
        System.out.println("Valid: " + XmlFileUtils.validateXml(path));

        // Delete user element
        XmlFileUtils.deleteElements(path, "user", e -> "1".equals(e.getAttribute("id")));

        // Delete file
        XmlFileUtils.deleteXmlFile(path);
    }
}
