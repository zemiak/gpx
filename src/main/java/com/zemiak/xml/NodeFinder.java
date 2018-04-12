package com.zemiak.xml;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class NodeFinder {
    public static Node findNode(NodeList nodes, String name) {
        List<Node> ret = findNodes(nodes, name);

        if (ret.size() != 1) {
            throw new IllegalStateException("Node " + name + " found " + ret.size() + " times in " + nodes.getLength() + " nodes");
        }

        return ret.get(0);
    }

    public static List<Node> findNodes(NodeList nodes, String name) {
        List<Node> ret = new ArrayList<>();

        for (int i = 0; i < nodes.getLength() ; i++) {
            Node n = nodes.item(i);

            if (name.equalsIgnoreCase(n.getNodeName())) {
                ret.add(n);
            }
        }

        return ret;
    }
}
