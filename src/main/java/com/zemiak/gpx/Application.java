package com.zemiak.gpx;

import com.github.anorber.optget.Getopt;
import com.zemiak.ggz.GgzProducer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Application
{
    public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
        if (args.length == 0) {
            usage();
        }

        Getopt opts = new Getopt(args, "eg", "enrich", "ggz");
        boolean enrich = opts.hasOptions("-e") || opts.hasOptions("--enrich");
        boolean ggz = opts.hasOptions("-g") || opts.hasOptions("--ggz");
        String[] files = opts.getArgs();

        if ((files.length == 0) || (!ggz && files.length > 1) || (!enrich && !ggz)) {
            usage();
        }

        Map<String, String> gpx = new HashMap<>();
        for (String fileName: files) {
            if (enrich) {
                gpx.put(fileName, enrich(fileName));
            } else {
                gpx.put(fileName, catFile(fileName));
            }
        }

        if (ggz) {
            GgzProducer ggzProducer = new GgzProducer(gpx.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList()));
            ggzProducer.print();
        }
    }

    private static void usage() {
        System.out.println("Usage: gpx --enrich <gpx-file>");
        System.out.println("       gpx [--enrich] --ggz <gpx-file> [gpx-file 2] [gpx-file 3] ...");
        System.out.println("The output is written to stdout. Instead of long options, you can use \"g\" or \"e\"");
    }

    private static String enrich(String fileName) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document dom;

        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new IllegalStateException(ex);
        }

        try {
            dom = db.parse(fileName);
        } catch (SAXException | IOException ex) {
            throw new IllegalStateException(ex);
        }

        return new Processor(dom).process();
    }

    private static String catFile(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
