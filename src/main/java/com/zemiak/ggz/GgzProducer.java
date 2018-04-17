package com.zemiak.ggz;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Node;

public class GgzProducer {
    FileSystem zipfs;
    String zipFileName;
    List<Node> gpxEntries;
    List<Entry> indexEntries;

    public GgzProducer() throws IOException {
        this(File.createTempFile("ggz-file-name", ".zip").getAbsolutePath());
    }

    public GgzProducer(String fileName) throws IOException {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        URI uri = URI.create("file:" + fileName);
        zipfs = FileSystems.newFileSystem(uri, env);
        this.zipFileName = fileName;

        indexEntries = new ArrayList<>();
        gpxEntries = new ArrayList<>();
    }

    public void process(Map<String, List<Node>> gpx) {
        gpx.keySet().forEach(gpxFile -> process(gpxFile, gpx.get(gpxFile)));

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

    private String endGpxFile() {
        return "</gpx>\n";
    }

    private void flushFile(String fileName, String gpxFileHeader, List<Node> gpxEntries,
            LatLonBox box) throws IOException {
        gpxFileHeader = box.updateGpxHeader(gpxFileHeader);
        StringBuilder text = new StringBuilder(gpxFileHeader);
        gpxEntries.forEach(e -> text.append(e.toString()));
        text.append(endGpxFile());

        Files.write(zipfs.getPath("data", fileName + ".gpx"), text.toString().getBytes());
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
"  <bounds minlat={{MINLAT.}} minlon={{MINLON.}} maxlat={{MAXLAT.}} maxlon={{MAXLON.}} />"
        .replace("{{NAME}}", fileName)
        .replace("{{TIME}}", Instant.now().toString())
                ;

    }

    private void process(String fileNameGiven, List<Node> gpx) {
        int i = 0;
        int count = 0;

        String fileNameWithExt = Paths.get(fileNameGiven).getFileName().toString();
        String fileName = fileNameWithExt.contains(".") ? fileNameWithExt.substring(0, fileNameWithExt.indexOf(".")) : fileNameWithExt;

        String gpxFileHeader = startGpxFile(fileName);
        int filePos = gpxFileHeader.length();
        LatLonBox box = new LatLonBox();
        int suffix = -1;

        while (i < gpx.size()) {
            Entry e = new Entry(gpx.get(i));
            e.setFileName(fileName);
            e.setFilePos(filePos);
            String gpxEntry = e.toString();
            e.setFileSize(gpxEntry.length());
            indexEntries.add(e);
            gpxEntries.add(gpx.get(i));
            box.update(e);

            count++;
            if (count >= 512) {
                suffix = 0;

                try {
                    flushFile(fileName + "_" + suffix, gpxFileHeader, gpxEntries, box);
                    suffix += 1;
                    box = new LatLonBox();
                } catch (IOException ex) {
                    throw new RuntimeException("Cannot add an index data/" + fileName + ".gpx into ZIP", ex);
                }

                fileName = String.valueOf(System.currentTimeMillis());
                gpxFileHeader = startGpxFile(fileName);
                filePos = gpxFileHeader.length();
            } else {
                filePos += gpxEntry.length();
            }
        }

        if (! indexEntries.isEmpty()) {
            try {
                if (suffix > 0) {
                    flushFile(fileName + "_" + suffix, gpxFileHeader, gpxEntries, box);
                } else {
                    flushFile(fileName, gpxFileHeader, gpxEntries, box);
                }

            } catch (IOException ex) {
                throw new RuntimeException("Cannot add an index data/" + fileName + ".gpx into ZIP", ex);
            }
        }
    }
}
