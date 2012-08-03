package no.ntnu.online.onlineguru.utils.urlreader.impl;

import java.io.*;
import java.net.URLConnection;

import no.ntnu.online.onlineguru.exceptions.IncompliantCallerException;
import no.ntnu.online.onlineguru.utils.urlreader.connection.URLFactory;
import no.ntnu.online.onlineguru.utils.urlreader.model.Retriever;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class HTMLRetriever extends Retriever {

    static Logger logger = Logger.getLogger(HTMLRetriever.class);
    private Tidy tidy = new Tidy();

    URLConnection urlc;
    Document pageDocument;

    public HTMLRetriever(Object caller, String urltext) throws IncompliantCallerException {
        super(caller, urltext);

        new Thread(this).start();
    }

    public HTMLRetriever(Object caller, String urltext, Object[] callbackParameters) throws IncompliantCallerException {
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
            try {
                urlc = (URLConnection) url.openConnection();
                urlc.setRequestProperty("User-Agent", "OnlineGuru");
            } catch (IOException e) {
                logger.error("Failed to open connection.", e.getCause());
            }

            getPage();
            callback();
        }
    }

    private void getPage() {
        try  {
            tidy.setInputEncoding(charset.toString());
            tidy.setQuiet(true);
            tidy.setShowWarnings(false);
            tidy.setShowErrors(0);

            pageDocument = tidy.parseDOM(urlc.getInputStream(), null);
        } catch (IOException e) {
            logger.error("HTMLRetriever.getPage() ", e.getCause());
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

    public String getInString() {
        String pageString = null;

        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Source source = new DOMSource(pageDocument);
            Result output = new StreamResult(baos);
            transformer.transform(source, output);

            pageString = baos.toString().replaceAll("\\s+", " ");


        } catch (TransformerConfigurationException e) {
            logger.error(e.getMessage(), e.getCause());
            e.printStackTrace();
        } catch (TransformerException e) {
            logger.error(e.getMessage(), e.getCause());
            e.printStackTrace();
        }

        return pageString;
    }

}