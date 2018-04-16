package com.zemiak.ggz;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class LatLonBox {
    private static final String FORMAT = "#00.000000";
    private final NumberFormat formatter = new DecimalFormat(FORMAT);

    private double minlat = 1000.0;
    private double minlon = 1000.0;
    private double maxlat = 0.0;
    private double maxlon = 0.0;

    public double getMinlat() {
        return minlat;
    }

    public double getMinlon() {
        return minlon;
    }

    public double getMaxlat() {
        return maxlat;
    }

    public double getMaxlon() {
        return maxlon;
    }

    public void update(Entry e) {
        double elat = Double.valueOf(e.getLat());
        double elon = Double.valueOf(e.getLon());

        if (elat < minlat) {
            minlat = elat;
        }

        if (elat > maxlat) {
            maxlat = elat;
        }

        if (elon < minlon) {
            minlon = elon;
        }

        if (elon > maxlon) {
            maxlon = elon;
        }
    }

    public String updateGpxHeader(String gpxFileHeader) {
        gpxFileHeader = updatePlaceholder(gpxFileHeader, "{{MINLAT.}}", minlat);
        gpxFileHeader = updatePlaceholder(gpxFileHeader, "{{MINLON.}}", minlon);
        gpxFileHeader = updatePlaceholder(gpxFileHeader, "{{MAXLAT.}}", maxlat);
        gpxFileHeader = updatePlaceholder(gpxFileHeader, "{{MAXLON.}}", maxlon);

        return gpxFileHeader;
    }

    private String updatePlaceholder(String header, String placeholder, double val) {
        return header.replace(placeholder, "\"" + formatter.format(val) + "\"");
    }
}
