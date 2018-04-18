package com.zemiak.ggz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.w3c.dom.Node;

public class GgzProducer {
    FileOutputStream fos;
    ZipOutputStream zos;
    String zipFileName;
    List<Node> gpxEntries;
    List<Entry> indexEntries;

    public GgzProducer() throws IOException {
        this(File.createTempFile("ggz-file-name", ".zip").getAbsolutePath());
    }

    public GgzProducer(String fileName) throws IOException {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        fos = new FileOutputStream(fileName);
        zos = new ZipOutputStream(fos);

        this.zipFileName = fileName;

        indexEntries = new ArrayList<>();
        gpxEntries = new ArrayList<>();
    }

    public void process(Map<String, List<Node>> gpx) {
        gpx.keySet().forEach(gpxFile -> process(gpxFile, gpx.get(gpxFile)));

        ZipEntry ze = new ZipEntry("index/com/garmin/geocaches/v0/index.xml");


        try {
            zos.putNextEntry(ze);
            zos.write(GgzWriter.getXml(indexEntries).getBytes());
            zos.closeEntry();
        } catch (IOException ex) {
            throw new RuntimeException("Cannot add an index " + ze.getName() + " into ZIP", ex);
        }
    }

    public void close() throws IOException {
        zos.close();
        fos.close();
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
        GpxFile gpxFile = new GpxFile(fileName + (suffix == -1 ? "" : "_" + suffix), zos);

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

                gpxFile = new GpxFile(fileName + "_" + suffix, zos);
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

    public String getZipFileName() {
        return zipFileName;
    }
}
