package com.zemiak.gpx;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Processor {
    public void process(Document dom) {
        Node gpx = dom.getChildNodes().item(0);
        if (null == gpx) {
            throw new IllegalStateException("The document does not have a main node");
        }

        if (!"gpx".equals(gpx.getNodeName())) {
            throw new IllegalStateException("Document is not a GPX file");
        }

        NodeFinder.findNodes(gpx.getChildNodes(), "wpt").forEach(wpt -> processWaypoint(wpt));

        print(dom);
    }

    private void processWaypoint(Node wpt) {
        Node cache = NodeFinder.findNode(wpt.getChildNodes(), "groundspeak:cache");
        boolean archived = "True".equalsIgnoreCase(cache.getAttributes().getNamedItem("archived").getNodeValue());
        boolean disabled = "False".equalsIgnoreCase(cache.getAttributes().getNamedItem("available").getNodeValue());

        updateNameIfNeeded(wpt, archived, disabled);

        String attrs = collectAttributes(cache);
        String hint = getHint(cache);

        Node attrLog = LogCreator.createLog(attrs);
        Node hintLog = LogCreator.createLog(hint);

        LogCreator.insertFirstLog(hintLog);
        LogCreator.insertFirstLog(attrLog);
    }

    private String getHint(Node cache) throws DOMException {
        Node hint = NodeFinder.findNode(cache.getChildNodes(), "groundspeak:encoded_hints");
        String hintText = (null == hint) ? "<none>" : hint.getNodeValue();
        return hintText;
    }

    private String collectAttributes(Node cache) throws DOMException {
        Node attributes = NodeFinder.findNode(cache.getChildNodes(), "groundspeak:attributes");
        List<String> attrs = new ArrayList<>();
        for (int i = 0; i < attributes.getChildNodes().getLength(); i++) {
            Node attr = attributes.getChildNodes().item(i);
            boolean attrIncluded = "1".equals(attr.getAttributes().getNamedItem("inc").getNodeValue());
            String attrText = attr.getTextContent();

            attrs.add((attrIncluded ? "Yes:" : "NO:") + attrText);
        }

        return attrs.stream().collect(Collectors.joining("\n"));
    }

    private void updateNameIfNeeded(Node wpt, boolean archived, boolean disabled) {
        Node name = NodeFinder.findNode(wpt.getChildNodes(), "urlname");
        String text = name.getNodeValue();
        boolean changed = false;

        if (archived) {
            text = "[A] " + text;
            changed = true;
        }

        if (disabled) {
            text = "[D] " + text;
            changed = true;
        }

        if (changed) {
            name.setNodeValue(text);
        }
    }

    private void print(Document doc) {
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
