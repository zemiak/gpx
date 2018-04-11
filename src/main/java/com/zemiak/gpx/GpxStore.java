package com.zemiak.gpx;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Node;

public class GpxStore {
    private static final List<Node> GPX = new ArrayList<>();

    public static void add(Node wpt) {
        GPX.add(wpt);
    }

    public static List<Node> getAll() {
        return GPX;
    }
}
