package com.github.anorber.optget;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class GetoptTest {
    @Test
    public void enrichGgzMultipleFiles() {
        Getopt opts = new Getopt(new String[]{"--enrich", "-g", "1.gpx", "2.gpx", "3.gpz"}, "eg", "enrich", "ggz");
        assertTrue(opts.hasOptions("-g"));
        assertFalse(opts.hasOptions("--ggz"));
        assertTrue(opts.getArgs().length == 3);
    }

    @Test
    public void enrichNoGgzMultipleFiles() {
        Getopt opts = new Getopt(new String[]{"--enrich", "1.gpx", "2.gpx", "3.gpz"}, "eg", "enrich", "ggz");
        assertFalse(opts.hasOptions("-g"));
        assertFalse(opts.hasOptions("--ggz"));
        assertTrue(opts.getArgs().length == 3);
    }
}