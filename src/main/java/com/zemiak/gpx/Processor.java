package com.zemiak.gpx;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Processor {
    public void process(Document dom) {
        Node gpx = dom.getChildNodes().item(0);
        if (null == gpx) {
            throw new IllegalStateException("The document does not have a main node");
        }

        if (!"gpx".equals(gpx.getNodeName())) {
            throw new IllegalStateException("Document is not a GPX file");
        }

        NodeList waypoints = gpx.getChildNodes();
        for (int i = 0; i < waypoints.getLength() ; i++) {
            Node wpt = waypoints.item(i);

            if ("wpt".equalsIgnoreCase(wpt.getNodeName())) {
                processWaypoint(wpt);
            }
        }

        print(dom);
    }

    private void processWaypoint(Node wpt) {
        Node cache = findNode(wpt.getChildNodes(), "groundspeak:cache");
        boolean archived = "True".equalsIgnoreCase(cache.getAttributes().getNamedItem("archived").getNodeValue());
        boolean disabled = "False".equalsIgnoreCase(cache.getAttributes().getNamedItem("available").getNodeValue());

        Node attributes = findNode(cache.getChildNodes(), "groundspeak:attributes");
        List<String> attrs = new ArrayList<>();
        for (int i = 0; i < attributes.getChildNodes().getLength(); i++) {
            Node attr = attributes.getChildNodes().item(i);
            boolean attrIncluded = "1".equals(attr.getAttributes().getNamedItem("inc").getNodeValue());
            String attrText = attr.getTextContent();

            attrs.add((attrIncluded ? "Yes:" : "NO:") + attrText);
        }
    }

    private void print(Document dom) {

    }

    private Node findNode(NodeList nodes, String name) {
        for (int i = 0; i < nodes.getLength() ; i++) {
            Node n = nodes.item(i);

            if (name.equalsIgnoreCase(n.getNodeName())) {
                return n;
            }
        }

        throw new IllegalStateException("Node " + name + " not found in " + nodes.getLength() + " nodes");
    }
}
