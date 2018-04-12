package com.zemiak.xml;

import java.io.StringWriter;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Printer {
    public static String print(Document doc) {
        return Printer.print(doc, true);
    }

    public static String print(Element doc) {
        return Printer.print(doc, true);
    }

    public static String print(Document doc, Boolean xmlDeclaration) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
        } catch (TransformerConfigurationException ex) {
            throw new IllegalStateException("Cannot get transformer", ex);
        }

        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, xmlDeclaration ? "no" : "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);

        try {
            transformer.transform(new DOMSource(doc), result);
        } catch (TransformerException ex) {
            throw new IllegalStateException("Cannot process document", ex);
        }

        return result.toString();
    }

    public static String print(Element doc, Boolean xmlDeclaration) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
        } catch (TransformerConfigurationException ex) {
            throw new IllegalStateException("Cannot get transformer", ex);
        }

        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, xmlDeclaration ? "no" : "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);

        try {
            transformer.transform(new DOMSource(doc), result);
        } catch (TransformerException ex) {
            throw new IllegalStateException("Cannot process document", ex);
        }

        return result.toString();
    }
}
