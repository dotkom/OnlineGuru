package no.ntnu.online.onlineguru.plugin.plugins.spotify;

import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.exceptions.IncompliantCallerException;
import no.ntnu.online.onlineguru.plugin.plugins.spotify.model.Album;
import no.ntnu.online.onlineguru.plugin.plugins.spotify.model.Artist;
import no.ntnu.online.onlineguru.plugin.plugins.spotify.model.Track;
import no.ntnu.online.onlineguru.plugin.plugins.spotify.xml.ModelFactory;
import no.ntnu.online.onlineguru.utils.Wand;
import no.ntnu.online.onlineguru.utils.urlreader.impl.XMLRetriever;
import no.ntnu.online.onlineguru.utils.urlreader.model.Retriever;
import no.ntnu.online.onlineguru.utils.urlreader.model.URLReader;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;

public class FetchURI implements URLReader {

	private Wand wand;
	private Network network;
	private String target;
	private Types linkCase;

    static Logger logger = Logger.getLogger(FetchURI.class);
	
	public FetchURI(String url, Wand wand, Network network, String target, Types linkCase) {
		this.wand = wand;
		this.network = network;
		this.target = target;
		this.linkCase = linkCase;

        try {
		    new XMLRetriever(this, url);
        } catch (IncompliantCallerException e) {

        }
	}
	
	public void urlReaderCallback(Retriever urlr) {

        XMLRetriever xr = (XMLRetriever)urlr;
        Document pageDocument = xr.getDOMDocument();
        
        Element root = pageDocument.getDocumentElement();
                
        try {
            switch (Types.valueOf(root.getNodeName().toUpperCase())) {
                case ALBUM:
                    Album album = ModelFactory.produceAlbum(xr.getURL().openStream());
                    showInfo(album);
                    break;
                case ARTIST:
                    Artist artist = ModelFactory.produceArtist(xr.getURL().openStream());
                    showInfo(artist);
                    break;
                case TRACK:
                    Track track = ModelFactory.produceTrack(xr.getURL().openStream());
                    showInfo(track);
                    break;
            }
        } catch (IOException ioe) {
            logger.error("Reading URL failed", ioe.getCause());
        }
    }

    private void showInfo(Object o) {
		wand.sendMessageToTarget(network, target, "[spotify] "+o.toString());
	}

    public void urlReaderCallback(Retriever HTMLRetriever, Object[] callbackParameters) {
        // Unused callback.
    }

}
