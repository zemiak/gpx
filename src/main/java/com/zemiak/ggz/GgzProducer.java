package com.zemiak.ggz;

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

        int filePos = 0;
        int count = 0;
        List<Entry> indexEntries = new ArrayList<>();
        List<Node> entries = new ArrayList<>();

        String fileName = String.valueOf(System.currentTimeMillis());
        String gpxFileHeader = startGpxFile(fileName);

        while (i < gpx.size()) {
            Entry e = new Entry(gpx.get(i));
            e.setFileName(fileName);
            e.setFilePos(gpxFileHeader.length() + filePos);
            String gpxEntry = getGpxEntryAsString(e);
            e.setFileSize(gpxEntry.length());
            indexEntries.add(e);
            entries.add(gpx.get(i));

            count++;
            if (count >= 512) {
                flushFile(fileName, gpxFileHeader, entries);
            }
        }

        if (! indexEntries.isEmpty()) {
            flushFile(fileName, gpxFileHeader, entries);
        }

        Path indexPath = zipfs.getPath("index", "com", "garmin", "geocaches", "v0", "index.xml");
        try {
            Files.write(indexPath, indexEntries.toString().getBytes(Charset.forName("UTF-8")));
        } catch (IOException ex) {
            throw new RuntimeException("Cannot add an index " + index.toString() + " into ZIP", ex);
        }
    }

    public void close() throws IOException {
        zipfs.close();
    }

    public String getFile() {
        return zipFileName;
    }

    private String getGpxEntryAsString(Entry e) {
        1/
    }

    private String startGpxFile(String fileName) {
        2/
    }

    private void flushFile(String fileName, String gpxFileHeader, List<Node> entries) {
        3/
    }
}
