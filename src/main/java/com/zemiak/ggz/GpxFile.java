package com.zemiak.ggz;

import com.zemiak.xml.Printer;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.w3c.dom.Node;

public class GpxFile {
    private final String fileName;
    private final ZipOutputStream zos;
    private final String header;

    public GpxFile(String fileName, ZipOutputStream zos) {
        this.fileName = fileName;
        this.zos = zos;
        this.header = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
"<gpx xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"1.0\" creator=\"Groundspeak Pocket Query\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd http://www.groundspeak.com/cache/1/0/1 http://www.groundspeak.com/cache/1/0/1/cache.xsd\" xmlns=\"http://www.topografix.com/GPX/1/0\">\n" +
"  <name>{{NAME}}</name>\n" +
"  <desc>Geocache file generated by Groundspeak</desc>\n" +
"  <author>Groundspeak</author>\n" +
"  <email>contact@groundspeak.com</email>\n" +
"  <time>{{TIME}}</time>\n" +
"  <keywords>cache, geocache, groundspeak</keywords>\n" +
"  <bounds minlat={{MINLAT.}} minlon={{MINLON.}} maxlat={{MAXLAT.}} maxlon={{MAXLON.}} />\n"
        .replace("{{NAME}}", fileName)
        .replace("{{TIME}}", Instant.now().toString());
    }

    public int getHeaderLength() {
        return header.length();
    }

    public void flushFile(List<Node> gpxEntries,
            LatLonBox box) throws IOException {
        String gpxFileHeader = box.updateGpxHeader(header);
        StringBuilder text = new StringBuilder(gpxFileHeader);
        gpxEntries.forEach(e -> text.append(Printer.print(e, false)));
        text.append(endGpxFile());

        ZipEntry ze = new ZipEntry("data/" + fileName + ".gpx");
        zos.putNextEntry(ze);
        zos.write(text.toString().getBytes());
        zos.closeEntry();
    }

    private String endGpxFile() {
        return "</gpx>\n";
    }
}
