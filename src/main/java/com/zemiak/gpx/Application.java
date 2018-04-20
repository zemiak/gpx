package com.zemiak.gpx;

import com.github.anorber.optget.Getopt;
import com.zemiak.ggz.GgzProducer;
import com.zemiak.xml.NodeFinder;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Application
{
    private static final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();

    public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
        if (args.length == 0) {
            usage();
        }

        Getopt opts = new Getopt(args, "co:", "copy", "output=");
        boolean enrich = !opts.hasOptions("c") && !opts.hasOptions("copy");
        String[] files = opts.getArgs();

        String outputFileName = null;
        if (opts.hasOptions("o")) {
            outputFileName = opts.getArgumentString("o");
        } else if (opts.hasOptions("output")) {
            outputFileName = opts.getArgumentString("output");
        }

        if (files.length == 0 || Objects.isNull(outputFileName)) {
            usage();
        }

        Document gpx;
        for (String fileName: files) {
            if (enrich) {
                gpx = enrich(fileName);
            } else {
                gpx = parseFile(fileName);
            }

            List<Node> gpxNodes = NodeFinder.findNodes(gpx.getChildNodes(), "gpx");
            if (gpxNodes.size() != 1) {
                missingGpxNode(fileName);
            }

            NodeList childNodes = gpxNodes.get(0).getChildNodes();
            List<Node> wptNodes = NodeFinder.findNodes(childNodes, "wpt");
            wptNodes.forEach(node -> GpxStore.add(fileName, node));
        }

        if (GpxStore.isEmpty()) {
            emptyResult();
        }

        GgzProducer ggzProducer = new GgzProducer(outputFileName);
        ggzProducer.process(GpxStore.getAll());
        ggzProducer.close();

        System.out.println("Result: " + ggzProducer.getZipFileName());
    }

    private static void usage() {
        System.out.println("Usage: gpx <--output|-o> <ggz-file> [--copy|-c] <gpx-file> [gpx-file-2] ...\n");
        System.out.println("The output (GGZ) is written to stdout.");
        System.out.println("By default, the input GPX is enhanced with attributes in hint and description.");
        System.out.println("If you don't wish to enhance it, provide --copy option.");
        System.exit(1);
    }

    private static void emptyResult() {
        System.out.println("gpx error: could not read any cache (wpt) from provided file(s)");
        System.exit(2);
    }

    private static void missingGpxNode(String fileName) {
        System.out.println("gpx error: could not read main gpx node from provided file " + fileName);
        System.exit(2);
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
