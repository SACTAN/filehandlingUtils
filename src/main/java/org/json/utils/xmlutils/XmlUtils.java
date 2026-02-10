package org.json.utils.xmlutils;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.locks.*;
import java.util.function.Predicate;

/**
 * Utility class for all XML file and data operations.
 * Thread-safe for file operations.
 */
public class XmlUtils {
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    // --- File Operations ---
    public static void createXmlFile(String path, String rootElementName) throws XmlFileException {
        lock.writeLock().lock();
        try {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath)) {
                throw new XmlFileException("File already exists: " + path);
            }
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            Element root = doc.createElement(rootElementName);
            doc.appendChild(root);
            writeXmlFile(path, doc);
        } catch (Exception e) {
            throw new XmlFileException("Failed to create XML file", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static Document readXmlFile(String path) throws XmlFileException {
        lock.readLock().lock();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            return dBuilder.parse(new File(path));
        } catch (Exception e) {
            throw new XmlFileException("Failed to read XML file", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void writeXmlFile(String path, Document doc) throws XmlFileException {
        lock.writeLock().lock();
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(path));
            transformer.transform(source, result);
        } catch (Exception e) {
            throw new XmlFileException("Failed to write XML file", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void deleteXmlFile(String path) throws XmlFileException {
        lock.writeLock().lock();
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            throw new XmlFileException("Failed to delete XML file", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void updateXmlFile(String path, java.util.function.Function<Document, Document> updater) throws XmlFileException {
        lock.writeLock().lock();
        try {
            Document doc = readXmlFile(path);
            Document updated = updater.apply(doc);
            writeXmlFile(path, updated);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // --- Query & Element Operations ---
    public static NodeList searchByTagName(String path, String tagName) throws XmlFileException {
        Document doc = readXmlFile(path);
        return doc.getElementsByTagName(tagName);
    }

    public static String getElementValue(String path, String tagName) throws XmlFileException {
        Document doc = readXmlFile(path);
        NodeList nodes = doc.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            return node.getTextContent();
        }
        return null;
    }

    public static void deleteElements(String path, String tagName, Predicate<Element> condition) throws XmlFileException {
        lock.writeLock().lock();
        try {
            Document doc = readXmlFile(path);
            NodeList nodes = doc.getElementsByTagName(tagName);
            for (int i = nodes.getLength() - 1; i >= 0; i--) {
                Node node = nodes.item(i);
                if (node instanceof Element && condition.test((Element) node)) {
                    node.getParentNode().removeChild(node);
                }
            }
            writeXmlFile(path, doc);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // --- Pretty Print ---
    public static String prettyPrint(String path) throws XmlFileException {
        Document doc = readXmlFile(path);
        return prettyPrint(doc);
    }
    public static String prettyPrint(Document doc) throws XmlFileException {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            throw new XmlFileException("Failed to pretty print XML file", e);
        }
    }

    // --- Validation ---
    public static boolean validateXml(String path) throws XmlFileException {
        try {
            Document doc = readXmlFile(path);
            return doc.getDocumentElement() != null;
        } catch (XmlFileException e) {
            return false;
        }
    }
    public static boolean hasNonEmptyTagName(Element element) {
        return element != null && element.getTagName() != null && !element.getTagName().trim().isEmpty();
    }

    // --- Exception as static inner class ---
    public static class XmlFileException extends Exception {
        public XmlFileException(String message) { super(message); }
        public XmlFileException(String message, Throwable cause) { super(message, cause); }
    }
}
