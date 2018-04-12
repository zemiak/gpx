package com.zemiak.ggz;

import com.zemiak.xml.NodeFinder;
import com.zemiak.xml.Printer;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Entry {
    private static final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();

    private final String code;
    private final String name;
    private final String type;
    private final String lat;
    private final String lon;
    private Integer filePos;
    private Integer fileSize;
    private final String awesomeness = "3.0";
    private final String difficulty;
    private final String size;
    private final String terrain;
    private final Boolean found;
    private String fileName;

    public static final Map<String, String> SIZES = new HashMap<>();
    static {
        SIZES.put("micro", "2.0");
        SIZES.put("small", "3.0");
        SIZES.put("regular", "4.0");
        SIZES.put("large", "5.0");
        SIZES.put("other", "-1.0");
        SIZES.put("no chosen", "-2.0");
        SIZES.put("virtual", "0.0");
    }

    public Entry(Node wpt) {
        Node cache = NodeFinder.findNode(wpt.getChildNodes(), "groundspeak:cache");
        code = NodeFinder.findNode(wpt.getChildNodes(), "name").getFirstChild().getNodeValue();
        name = NodeFinder.findNode(wpt.getChildNodes(), "urlname").getFirstChild().getNodeValue();
        type = NodeFinder.findNode(cache.getChildNodes(), "groundspeak:type").getFirstChild().getNodeValue();
        lat = wpt.getAttributes().getNamedItem("lat").getNodeValue();
        lon = wpt.getAttributes().getNamedItem("lon").getNodeValue();
        difficulty = NodeFinder.findNode(cache.getChildNodes(), "groundspeak:difficulty").getFirstChild().getNodeValue();
        size = SIZES.get(NodeFinder.findNode(cache.getChildNodes(), "groundspeak:container").getFirstChild().getNodeValue().toLowerCase());
        terrain = NodeFinder.findNode(cache.getChildNodes(), "groundspeak:terrain").getFirstChild().getNodeValue();
        found = false;
    }

    public void setFilePos(Integer filePos) {
        this.filePos = filePos;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        DocumentBuilder db;
        try {
            db = DBF.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new IllegalStateException(ex);
        }

        Document doc = db.newDocument();

        return Printer.print(getGch(doc), false);
    }

    private static void addElement(Document doc, Element el, String name, String data) {
        Element child = doc.createElement(name);
        child.appendChild(doc.createTextNode(data));
        el.appendChild(child);
    }

    private Element getGch(Document doc) {
        Element gch = doc.createElement("gch");
        addElement(doc, gch, "code", code);
        addElement(doc, gch, "name", name);
        addElement(doc, gch, "type", type);
        addElement(doc, gch, "lat", lat);
        addElement(doc, gch, "lon", lon);
        addElement(doc, gch, "file_pos", String.valueOf(filePos));
        addElement(doc, gch, "file_size", String.valueOf(fileSize));

        Element ratings = doc.createElement("ratings");
        addElement(doc, ratings, "awesomeness", awesomeness);
        addElement(doc, ratings, "difficulty", difficulty);
        addElement(doc, ratings, "size", size);
        addElement(doc, ratings, "terrain", terrain);
        gch.appendChild(ratings);

        addElement(doc, gch, "found", found ? "true" : "false");

        return gch;
    }
}
