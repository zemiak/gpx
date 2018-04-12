package com.zemiak.gpx;

import com.zemiak.xml.NodeFinder;
import org.w3c.dom.Node;

public class CacheModifier {
    public static void modifyCache(Node cache, String attrs, String hint) {
        modifyDescription(cache, attrs, hint);
        modifyHint(cache, attrs, hint);
    }

    private static void modifyDescription(Node cache, String attrs, String hint) {
        Node desc = NodeFinder.findNode(cache.getChildNodes(), "groundspeak:long_description");
        desc.getFirstChild().setNodeValue(attrs.replace("\n", "<p>&nbsp;.</p>")
                + "<p>&nbsp;.</p><p>&nbsp;.</p>"
                + hint
                + "<p>&nbsp;.</p><p>&nbsp;.</p>"
                + desc.getFirstChild().getNodeValue());
    }

    private static void modifyHint(Node cache, String attrs, String hint) {
        Node hintNode = NodeFinder.findNode(cache.getChildNodes(), "groundspeak:encoded_hints");
        hintNode.getFirstChild().setNodeValue(attrs.replace("\n", "<p>&nbsp;.</p>")
                + "<p>&nbsp;.</p><p>&nbsp;.</p>"
                + hint);
    }

    public static void updateNameIfNeeded(Node wpt, boolean archived, boolean disabled) {
        Node name = NodeFinder.findNode(wpt.getChildNodes(), "urlname");
        String text = name.getFirstChild().getNodeValue();
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
            name.getFirstChild().setNodeValue(text);
        }
    }
}
