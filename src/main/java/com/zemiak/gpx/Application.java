package com.zemiak.gpx;

import com.github.anorber.optget.Getopt;
import com.zemiak.MessagePrinter;
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
        long start = System.currentTimeMillis();

        if (args.length == 0) {
            usage("No arguments provided");
        }

        Getopt opts = new Getopt(args, "co:");

        boolean enrich = !opts.hasOptions("-c");
        String[] files = opts.getArgs();

        String outputFileName = null;
        if (opts.hasOptions("-o")) {
            outputFileName = opts.getArgumentString("-o");
        }

        if (files.length == 0) {
            usage("You did not provide any GPX file(s)");
        }

        if (Objects.isNull(outputFileName)) {
            usage("You did not provide any output file");
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

        int count = 0;
        count = GpxStore.getAll().entrySet().stream().map((entry) -> entry.getValue().size()).reduce(count, Integer::sum);
        MessagePrinter.println("Found %d unique caches in %d files.", count, files.length);

        if (GpxStore.isEmpty()) {
            emptyResult();
        }

        GgzProducer ggzProducer = new GgzProducer(outputFileName);
        ggzProducer.process(GpxStore.getAll());
        ggzProducer.close();

        MessagePrinter.println("Finished in %d milliseconds, result is in %s.", System.currentTimeMillis() - start, ggzProducer.getZipFileName());
    }

    private static void usage(String reason) {
        System.out.println("Usage: gpx -o <ggz-file> [-c] <gpx-file> [gpx-file-2] ...\n");
        System.out.println("The output (GGZ) is written to stdout. The -o option (output) must be the first argument.");
        System.out.println("By default, the input GPX is enhanced with attributes in hint and description.");
        System.out.println("If you don't wish to enhance it, provide -c (copy) option.");
        System.out.println("reason: " + reason);
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
