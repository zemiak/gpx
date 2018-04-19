package com.zemiak.ggz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
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
        int i = 0, count = 0, suffix = 0;

        String fileNameWithExt = Paths.get(fileNameGiven).getFileName().toString();
        String fileName = fileNameWithExt.contains(".") ? fileNameWithExt.substring(0, fileNameWithExt.indexOf(".")) : fileNameWithExt;
        GpxFile gpxFile = new GpxFile(fileName + "_" + suffix, zos);

        int filePos = gpxFile.getHeaderLength();
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

            i++;
            count++;
            if (count >= 512) {
                try {
                    gpxFile.flushFile(gpxEntries, box);
                } catch (IOException ex) {
                    throw new RuntimeException("Cannot add an index data/" + fileName + "_" + suffix + ".gpx into ZIP", ex);
                }

                suffix += 1;
                box = new LatLonBox();

                gpxFile = new GpxFile(fileName + "_" + suffix, zos);
                filePos = gpxFile.getHeaderLength();
                count = 0;
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
