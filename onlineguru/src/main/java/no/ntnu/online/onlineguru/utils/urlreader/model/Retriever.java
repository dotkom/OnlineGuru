package no.ntnu.online.onlineguru.utils.urlreader.model;

import no.ntnu.online.onlineguru.exceptions.IncompliantCallerException;
import no.ntnu.online.onlineguru.utils.urlreader.connection.URLFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * @author Håvard Slettvold
 */


public class Retriever implements Runnable {

    static Logger logger = Logger.getLogger(Retriever.class);

    protected String urltext;
    protected URLReader caller;
    protected Object[] callbackParameters = null;

    protected URL url;
    protected HttpURLConnection conn;
    protected Charset charset;

    public Retriever(Object caller, String urltext) throws IncompliantCallerException {
		if (caller instanceof URLReader) {
			this.caller = (URLReader)caller;
			this.urltext = urltext;
		}
		else {
			throw new IncompliantCallerException("The calling object needs to implement the URLReader interface.");
		}
	}

	public Retriever(Object caller, String urltext, Object[] callbackParameters) throws IncompliantCallerException {
		if (caller instanceof URLReader) {
			this.caller = (URLReader)caller;
			this.urltext = urltext;
			this.callbackParameters = callbackParameters;
		}
		else {
			throw new IncompliantCallerException("The calling object needs to implement the URLReader interface.");
		}
	}

    public void  run() {
        URLFactory factory = new URLFactory(urltext);
        factory.start();

        url = factory.getURL();
        charset = factory.getCharset();
    }

     public Charset getCharset() {
        return charset;
    }

    public URL getURL() {
        return url;
    }

    /*
        Source return methods.
        Implemented only for the interface purpose of this class.

        If you wish to add other ways of returning sources from other Retrievers,
        you need to include them here as well.
    */

    /*
    public Document getDOMDocument() throws NotImplementedException {
        throw new NotImplementedException();
    }

    public String getInString() {
        throw new NotImplementedException();
    }
    */

}
