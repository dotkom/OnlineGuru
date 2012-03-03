package no.ntnu.online.onlineguru.utils.urlreader.impl;

import no.ntnu.online.onlineguru.exceptions.IncompliantCallerException;
import no.ntnu.online.onlineguru.utils.urlreader.connection.URLFactory;
import no.ntnu.online.onlineguru.utils.urlreader.model.Retriever;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * @author Håvard Slettvold
 */


public class XMLRetriever extends Retriever {

    private Document pageDocument;


    public XMLRetriever(Object caller, String urltext) throws IncompliantCallerException {
        super(caller, urltext);

        new Thread(this).start();
    }

    public XMLRetriever(Object caller, String urltext, Object[] callbackParameters) throws IncompliantCallerException {
        super(caller, urltext, callbackParameters);

        new Thread(this).start();
    }

    @Override
    public void run() {
        URLFactory factory = new URLFactory(urltext);
        factory.start();

        url = factory.getURL();
        charset = factory.getCharset();

        if (url != null && charset != null) {
            getPage();
            callback();
        }
    }

    public void getPage() {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            pageDocument = docBuilder.parse(url.openStream());

            // normalize text representation
            pageDocument.getDocumentElement().normalize();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private void callback() {
        if (callbackParameters != null) {
            caller.urlReaderCallback(this, callbackParameters);
        }
        else {
            caller.urlReaderCallback(this);
        }
    }

    /*
        Various methods for getting the html
    */

    public Document getDOMDocument() {
        return pageDocument;
    }

}
