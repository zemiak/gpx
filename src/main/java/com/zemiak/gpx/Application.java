package com.zemiak.gpx;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Application
{
    public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
        if (1 != args.length) {
            usage();
        }

        String fileName = args[0];

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document dom;

        DocumentBuilder db = dbf.newDocumentBuilder();
        dom = db.parse(fileName);

        new Processor(dom).process();
    }

    private static void usage() {
        System.out.println("Usage: gpx <gpx-file>");
    }
}
