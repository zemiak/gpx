package com.zemiak.gpx;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import static java.lang.System.out;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

public class DocumentPrinter {
    public static void print(Document doc) {
        // print to stdout
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
        } catch (TransformerConfigurationException ex) {
            throw new IllegalStateException("Cannot get transformer", ex);
        }

        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        try {
            transformer.transform(new DOMSource(doc),
                    new StreamResult(new OutputStreamWriter(out, "UTF-8")));
        } catch (TransformerException ex) {
            throw new IllegalStateException("Cannot process document", ex);
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("Unsupported encoding for document", ex);
        }
    }
}
