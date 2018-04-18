package com.zemiak.ggz;

import java.time.Instant;
import java.util.List;

public class GgzWriter {
    public static String getXml(List<Entry> indexEntries) {
        StringBuilder b = new StringBuilder(getHeader());
        indexEntries.forEach(e -> b.append(e.toString()));
        b.append(getFooter());

        return b.toString();
    }

    private static String getHeader() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
"<ggz xmlns=\"http://www.opencaching.com/xmlschemas/ggz/1/0\">\n" +
"  <time>{{TIME}}</time>".replace("{{TIME}}", Instant.now().toString());
    }

    private static String getFooter() {
        return "</ggz>";
    }
}
