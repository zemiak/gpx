package com.zemiak.gpx;

import com.zemiak.xml.NodeFinder;
import java.util.*;
import org.w3c.dom.Node;

public class GpxStore {
    private static final Map<String, List<Node>> GPX = new HashMap<>();
    private static final Set<String> CODES = new HashSet<>();

    public static void add(String fileName, Node wpt) {
        String code = NodeFinder.findNode(wpt.getChildNodes(), "name").getFirstChild().getNodeValue();
        if (!CODES.contains(code)) {
            CODES.add(code);

            if (!GPX.containsKey(fileName)) {
                GPX.put(fileName, new ArrayList<>());
            }

            GPX.get(fileName).add(wpt);
        }
    }

    public static Map<String, List<Node>> getAll() {
        return GPX;
    }

    public static boolean isEmpty() {
        return GPX.isEmpty();
    }
}
