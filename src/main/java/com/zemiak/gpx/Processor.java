package com.zemiak.gpx;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Processor {
    Document dom;

    public Processor(Document dom) {
        this.dom = dom;
    }

    public void process() {
        Node gpx = dom.getChildNodes().item(0);
        if (null == gpx) {
            throw new IllegalStateException("The document does not have a main node");
        }

        if (!"gpx".equals(gpx.getNodeName())) {
            throw new IllegalStateException("Document is not a GPX file");
        }

        NodeFinder.findNodes(gpx.getChildNodes(), "wpt").forEach(wpt -> processWaypoint(wpt));

        DocumentPrinter.print(dom);
    }

    private void processWaypoint(Node wpt) {
        Node cache = NodeFinder.findNode(wpt.getChildNodes(), "groundspeak:cache");
        boolean archived = "True".equalsIgnoreCase(cache.getAttributes().getNamedItem("archived").getNodeValue());
        boolean disabled = "False".equalsIgnoreCase(cache.getAttributes().getNamedItem("available").getNodeValue());

        CacheModifier.updateNameIfNeeded(wpt, archived, disabled);

        String attrs = collectAttributes(cache);
        String hint = getHint(cache);

        CacheModifier.modifyCache(cache, attrs, hint);
    }

    private String getHint(Node cache) throws DOMException {
        Node hint = NodeFinder.findNode(cache.getChildNodes(), "groundspeak:encoded_hints");
        String hintText = (null == hint) ? "<none>" : hint.getFirstChild().getNodeValue();
        return hintText;
    }

    private String collectAttributes(Node cache) throws DOMException {
        Node attributes = NodeFinder.findNode(cache.getChildNodes(), "groundspeak:attributes");
        List<String> attrs = new ArrayList<>();
        for (int i = 0; i < attributes.getChildNodes().getLength(); i++) {
            Node attr = attributes.getChildNodes().item(i);
            if (null == attr) {
                throw new IllegalStateException("item " + i + " is null");
            }

            if (null == attr.getAttributes()) {
                continue;
            }

            if (null == attr.getAttributes().getNamedItem("inc")) {
                continue;
            }

            boolean attrIncluded = "1".equals(attr.getAttributes().getNamedItem("inc").getNodeValue());
            String attrText = attr.getTextContent();

            attrs.add((attrIncluded ? "Y:" : "N:") + attrText);
        }

        return attrs.stream().collect(Collectors.joining("\n"));
    }
}
