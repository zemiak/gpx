package com.zemiak.ggz;

import com.zemiak.gpx.DocumentPrinter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;

public class GgzProducer {
    FileSystem zipfs;
    String fileName;
    final Index index = new Index();

    public GgzProducer() throws IOException {
        this(File.createTempFile("ggz-file-name", ".zip").getAbsolutePath());
    }

    public GgzProducer(String fileName) throws IOException {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        URI uri = URI.create("file:" + fileName);
        zipfs = FileSystems.newFileSystem(uri, env);
        this.fileName = fileName;
    }

    public void process(Map<String, Document> gpx) {
        gpx.entrySet().stream().forEach(e -> {
            Document doc = gpx.get(e);
            index.add(e.getKey(), doc);

            Path internalPath = zipfs.getPath("data", e.getKey());
            String xml = DocumentPrinter.print(doc);
            try {
                Files.write(internalPath, xml.getBytes(Charset.forName("UTF-8")));
            } catch (IOException ex) {
                throw new RuntimeException("Cannot add a file " + e.getKey() + " into ZIP", ex);
            }
        });

        Path indexPath = zipfs.getPath("index", "com", "garmin", "geocaches", "v0", "index.xml");
        try {
            Files.write(indexPath, index.toString().getBytes(Charset.forName("UTF-8")));
        } catch (IOException ex) {
            throw new RuntimeException("Cannot add an index " + index.toString() + " into ZIP", ex);
        }
    }

    public void close() throws IOException {
        zipfs.close();
    }

    public String getFile() {
        return fileName;
    }
}
