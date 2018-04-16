package com.zemiak.ggz;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class LatLonBoxTest {
    private static final String FORMAT = "#00.000000";

    @Test
    public void testFloat() {
        NumberFormat formatter = new DecimalFormat(FORMAT);
        assertEquals("07.120000", formatter.format(7.12f));
    }

}