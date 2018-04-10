package com.zemiak.ggz;

import com.zemiak.gpx.DocumentPrinter;
import com.zemiak.gpx.NodeFinder;
import java.io.IOException;
import java.time.Instant;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Index {
    Document doc;
    Element rootElement;

    public Index() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new IllegalStateException(ex);
        }

        initIndexDocument(db);
    }

    private void initIndexDocument(DocumentBuilder db) {
        doc = db.newDocument();

        doc.setXmlStandalone(true);
        rootElement = doc.createElement("ggz");
        rootElement.setAttribute("xmlns", "http://www.opencaching.com/xmlschemas/ggz/1/0");

        addElement(rootElement, "time", Instant.now().toString());
    }

    @Override
    public String toString() {
        return DocumentPrinter.print(doc);
    }

    public void add(String fileName, Document dom) {
        Node gpx = dom.getChildNodes().item(0);
        if (null == gpx) {
            throw new IllegalStateException("The document does not have a main node");
        }

        if (!"gpx".equals(gpx.getNodeName())) {
            throw new IllegalStateException("Document is not a GPX file");
        }

        Element file = doc.createElement("file");
        addElement(file, "name", fileName);

        try {
            addElement(file, "crc", Crc.compute(fileName));
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot compute CRC32 from " + fileName, ex);
        }

        addElement(file, "time", Instant.now().toString());

        NodeFinder.findNodes(gpx.getChildNodes(), "wpt").forEach(wpt -> Entry.processWaypoint(file, wpt));

        rootElement.appendChild(file);
    }

    private void addElement(Element el, String name, String data) {
        Element child = doc.createElement("name");
        child.appendChild(doc.createTextNode(data));
        el.appendChild(child);
    }
}
