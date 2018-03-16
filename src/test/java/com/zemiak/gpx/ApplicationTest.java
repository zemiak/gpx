package com.zemiak.gpx;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ApplicationTest {
    Document dom;

    @Before
    public void init() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse("./src/test/resources/test.gpx");
        } catch (ParserConfigurationException ex) {
            System.err.println("ParserConfigurationException");
        } catch (SAXException ex) {
            System.err.println("SAXException");
        } catch (IOException ex) {
            System.err.println("IOException");
        }

        assert null != dom;
    }


    @Test
    public void hasChildNodes()
    {
        assertThat("Must have child nodes", dom.hasChildNodes());
    }
}
