package com.zemiak.gpx;

import com.github.anorber.optget.Getopt;
import com.zemiak.ggz.GgzProducer;
import com.zemiak.xml.NodeFinder;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Application
{
    private static final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();

    public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
        if (args.length == 0) {
            usage();
        }

        Getopt opts = new Getopt(args, "e", "enrich");
        boolean enrich = opts.hasOptions("-e") || opts.hasOptions("--enrich");
        String[] files = opts.getArgs();

        if (files.length == 0) {
            usage();
        }

        Document gpx;
        for (String fileName: files) {
            if (enrich) {
                gpx = enrich(fileName);
            } else {
                gpx = parseFile(fileName);
            }

            NodeFinder.findNodes(gpx.getChildNodes(), "wpt").forEach(node -> GpxStore.add(fileName, node));
        }

        GgzProducer ggzProducer = new GgzProducer();
        ggzProducer.process(GpxStore.getAll());
        ggzProducer.close();

        System.out.println("Result: " + ggzProducer.getZipFileName());
    }

    private static void usage() {
        System.out.println("Usage: gpx [--enrich] <gpx-file> [gpx-file-2] ...");
        System.out.println("The output (GGZ) is written to stdout. Instead of long options, you can use \"-e\"");
        System.exit(1);
    }

    private static Document parseFile(String fileName) {
        DocumentBuilder db;
        try {
            db = DBF.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new IllegalStateException(ex);
        }

        Document dom;
        try {
            dom = db.parse(fileName);
        } catch (SAXException | IOException ex) {
            throw new IllegalStateException(ex);
        }

        return dom;
    }

    private static Document enrich(String fileName) {
        return new Processor(parseFile(fileName)).process();
    }
}
