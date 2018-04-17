package com.zemiak.ggz;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.*;
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

    private void process(String fileNameGiven, List<Node> gpx) {
        int i = 0;
        int count = 0;
        int suffix = -1;

        if (gpx.size() >= 512) {
            suffix = 0;
        }

        String fileNameWithExt = Paths.get(fileNameGiven).getFileName().toString();
        String fileName = fileNameWithExt.contains(".") ? fileNameWithExt.substring(0, fileNameWithExt.indexOf(".")) : fileNameWithExt;
        GpxFile gpxFile = new GpxFile(fileName + (suffix == -1 ? "" : "_" + suffix), zipfs);

        String gpxFileHeader = gpxFile.getHeader();
        int filePos = gpxFileHeader.length();
        LatLonBox box = new LatLonBox();


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
                    gpxFile.flushFile(gpxEntries, box);
                    suffix += 1;
                    box = new LatLonBox();

                } catch (IOException ex) {
                    throw new RuntimeException("Cannot add an index data/" + fileName + ".gpx into ZIP", ex);
                }

                gpxFile = new GpxFile(fileName + "_" + suffix, zipfs);
                gpxFileHeader = gpxFile.getHeader();
                filePos = gpxFileHeader.length();
            } else {
                filePos += gpxEntry.length();
            }
        }

        if (! indexEntries.isEmpty()) {
            try {
                gpxFile.flushFile(gpxEntries, box);
            } catch (IOException ex) {
                throw new RuntimeException("Cannot add an index data/" + fileName + ".gpx into ZIP", ex);
            }
        }
    }
}
