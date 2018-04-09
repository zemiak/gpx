package com.zemiak.ggz;

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

public class GgzProducer {
    FileSystem zipfs;
    String fileName;

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

    public void process(Map<String, String> gpx) {
        gpx.entrySet().stream().forEach(e -> {
            Path internalPath = zipfs.getPath(e.getKey());
            try {
                Files.write(internalPath, e.getValue().getBytes(Charset.forName("UTF-8")));
            } catch (IOException ex) {
                throw new RuntimeException("Cannot add a file " + e.getKey() + " into ZIP", ex);
            }
        });
    }

    public void close() throws IOException {
        zipfs.close();
    }

    public String getFile() {
        return fileName;
    }
}
