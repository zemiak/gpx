package com.zemiak.ggz;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Entry {
    public static void processWaypoint(Document doc, Element file, Node wpt, int filePos, int entrySizeInBytes) {
        Element gch = doc.createElement("gch");
        addElement(doc, gch, "code", getCacheCode(wpt));
        addElement(doc, gch, "name", getCacheName(wpt));
        addElement(doc, gch, "type", getCacheType(wpt));
        addElement(doc, gch, "lat", getCacheLat(wpt));
        addElement(doc, gch, "lon", getCacheLon(wpt));
        addElement(doc, gch, "file_pos", String.valueOf(filePos));
        addElement(doc, gch, "file_size", String.valueOf(entrySizeInBytes));

        Element ratings = doc.createElement("ratings");
        addElement(doc, ratings, "awesomeness", getCacheAwesomeness(wpt));
        addElement(doc, ratings, "difficulty", getCacheDifficulty(wpt));
        addElement(doc, ratings, "size", getCacheSize(wpt));
        addElement(doc, ratings, "terrain", getCacheTerrain(wpt));
        gch.appendChild(ratings);

        addElement(doc, gch, "found", getCacheFound(wpt) ? "true" : "false");

        file.appendChild(gch);
    }

    private static void addElement(Document doc, Element el, String name, String data) {
        Element child = doc.createElement(name);
        child.appendChild(doc.createTextNode(data));
        el.appendChild(child);
    }

    private static String getCacheCode(Node wpt) {
        1
    }

    private static String getCacheName(Node wpt) {
        2
    }

    private static String getCacheType(Node wpt) {
        3
    }

    private static String getCacheLat(Node wpt) {
        4
    }

    private static String getCacheLon(Node wpt) {
        5
    }

    private static String getCacheAwesomeness(Node wpt) {
        6
    }

    private static String getCacheDifficulty(Node wpt) {
        7
    }

    private static String getCacheSize(Node wpt) {
        8
    }

    private static String getCacheTerrain(Node wpt) {
        9
    }

    private static boolean getCacheFound(Node wpt) {
        10
    }
}
