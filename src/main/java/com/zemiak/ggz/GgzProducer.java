package com.zemiak.ggz;

import com.zemiak.xml.Printer;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Node;

public class GgzProducer {
    FileSystem zipfs;
    String zipFileName;

    public GgzProducer() throws IOException {
        this(File.createTempFile("ggz-file-name", ".zip").getAbsolutePath());
    }

    public GgzProducer(String fileName) throws IOException {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        URI uri = URI.create("file:" + fileName);
        zipfs = FileSystems.newFileSystem(uri, env);
        this.zipFileName = fileName;
    }

    public void process(List<Node> gpx) {
        int i = 0;

        int count = 0;
        List<Entry> indexEntries = new ArrayList<>();
        List<Node> entries = new ArrayList<>();

        String fileName = String.valueOf(System.currentTimeMillis());
        String gpxFileHeader = startGpxFile(fileName);
        int filePos = gpxFileHeader.length();

        while (i < gpx.size()) {
            Entry e = new Entry(gpx.get(i));
            e.setFileName(fileName);
            e.setFilePos(filePos);
            String gpxEntry = e.toString();
            e.setFileSize(gpxEntry.length());
            indexEntries.add(e);
            entries.add(gpx.get(i));

            count++;
            if (count >= 512) {
                flushFile(fileName, gpxFileHeader, entries);

                fileName = String.valueOf(System.currentTimeMillis());
                gpxFileHeader = startGpxFile(fileName);
                filePos = gpxFileHeader.length();
            } else {
                filePos += gpxEntry.length();
            }
        }

        if (! indexEntries.isEmpty()) {
            flushFile(fileName, gpxFileHeader, entries);
        }

        Path indexPath = zipfs.getPath("index", "com", "garmin", "geocaches", "v0", "index.xml");
        try {
            Files.write(indexPath, indexEntries.toString().getBytes(Charset.forName("UTF-8")));
        } catch (IOException ex) {
            throw new RuntimeException("Cannot add an index " + indexPath.toString() + " into ZIP", ex);
        }
    }

    public void close() throws IOException {
        zipfs.close();
    }

    public String getFile() {
        return zipFileName;
    }

    private String startGpxFile(String fileName) {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
"<gpx xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"1.0\" creator=\"Groundspeak Pocket Query\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd http://www.groundspeak.com/cache/1/0/1 http://www.groundspeak.com/cache/1/0/1/cache.xsd\" xmlns=\"http://www.topografix.com/GPX/1/0\">\n" +
"  <name>{{NAME}}</name>\n" +
"  <desc>Geocache file generated by Groundspeak</desc>\n" +
"  <author>Groundspeak</author>\n" +
"  <email>contact@groundspeak.com</email>\n" +
"  <time>{{TIME}}</time>\n" +
"  <keywords>cache, geocache, groundspeak</keywords>\n" +
"  <bounds minlat=\"{{MINLAT}}\" minlon=\"{{MINLON}}\" maxlat=\"{{MAXLAT}}\" maxlon=\"{{MAXLON}}\" />";

        {{NAME}} take from original gpx file
        {{TIME}} 2018-03-13T09:55:06.8568462Z - take from Instant.now().toString()
        {{MINLAT}} take from original gpx file
        {{MINLON}} take from original gpx file
        {{MAXLAT}} take from original gpx file
        {{MAXLON}} take from original gpx file
    }

    private String endGpxFile() {
        return "</gpx>\n";
    }

    private void flushFile(String fileName, String gpxFileHeader, List<Node> entries) {
        2/
    }
}
