package com.zemiak.gpx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Node;

public class GpxStore {
    private static final Map<String, List<Node>> GPX = new HashMap<>();

    public static void add(String fileName, Node wpt) {
        if (!GPX.containsKey(fileName)) {
            GPX.put(fileName, new ArrayList<>());
        }

        GPX.get(fileName).add(wpt);
    }

    public static Map<String, List<Node>> getAll() {
        return GPX;
    }

    public static boolean isEmpty() {
        return GPX.isEmpty();
    }
}
