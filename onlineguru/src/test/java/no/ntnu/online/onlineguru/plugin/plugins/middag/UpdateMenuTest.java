package no.ntnu.online.onlineguru.plugin.plugins.middag;

import static org.junit.Assert.*;

import org.junit.ComparisonFailure;
import org.junit.Test;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import javax.xml.xpath.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * @author Håvard Slettvold
 */


public class UpdateMenuTest {

    String[] wantedContent = {
            "Juletallerken",
            "65",
            "Pastasnadder med skinke",
            "34",
            "Kikerter og tortillalefse (L,V)",
            "55",
            "Spinatsuppe",
            "15",
        };

    public Document makeDocument(String filename) {
        URL url = this.getClass().getResource(filename);
        File file = new File(url.getFile().replaceAll("%20", " "));

        assertTrue(file.exists());

        Tidy tidy = new Tidy();
        tidy.setInputEncoding(Charset.forName("UTF-8").toString());
        tidy.setQuiet(true);
        tidy.setShowWarnings(false);
        tidy.setShowErrors(0);

        try {
            return tidy.parseDOM(new FileInputStream(file), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Test
    public void testParseDomDocument() {
        String filename = "/middag/UpdateMenuPDD.html";
        Document doc = makeDocument(filename);

        try{

            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile("//table[@id='menytable']/tbody/tr[td='Tirsdag']/td/table/tbody/tr/td/text()");

            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nl = null;

            if (result instanceof NodeList) {
                nl = (NodeList)result;

                for (int i=0; i<nl.getLength();i++) {
                    assertEquals(nl.item(i).getNodeValue(), wantedContent[i]);
                }
            }
        } catch (XPathExpressionException xpee) {
            System.err.println("Something went wrong.");
            xpee.printStackTrace();
        }
    }

}
