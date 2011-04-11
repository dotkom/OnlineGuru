package no.ntnu.online.onlineguru.plugin.plugins.spotify;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.utils.Wand;
import no.ntnu.online.onlineguru.utils.urlreader.URLReader;
import no.ntnu.online.onlineguru.utils.urlreader.URLReaderUser;

public class FetchURI implements URLReaderUser {

	private Wand wand;
	private Network network;
	private String target;
	private Types linkCase;
	
	Pattern searchPattern = Pattern.compile("" +
			"(<album[^>]+>[^<]+<name>([^>]+)</name>)" +
			"|" +
			"(<artist[^>]*>[^<]+<name>([^>]+)</name>)" +
			"|" +
			"(<track[^>]+>[^<]+<name>([^>]+)</name>)" 
	);
	
	public FetchURI(String url, Wand wand, Network network, String target, Types linkCase) {
		this.wand = wand;
		this.network = network;
		this.target = target;
		this.linkCase = linkCase;
		
		new URLReader(this, url);
	}
	
	public void urlReaderCallback(URLReader urlr) {
		String page = urlr.getInString();
		
		Matcher m = searchPattern.matcher(page);
		String album = "", artist = "", track = "";
		
		while (m.find()) {
			if (m.group(2) != null) {
				album += m.group(2);
			}
			else if (m.group(4) != null) {
				if (artist.isEmpty()) {
					artist += m.group(4);
				}
				else {
					artist += ", "+m.group(4);
				}
			}
			else if (m.group(6) != null) {
				track += m.group(6);
			}
 		}
		
		switch (linkCase) {
			case ALBUM:
				showAlbumInfo(album, artist);
				break;
			case ARTIST:
				showArtistInfo(artist);
				break;
			case TRACK:
				showTrackInfo(track, artist, album);
				break;
		}
	}
	
	private void showAlbumInfo(String album, String artist) {
		wand.sendMessageToTarget(network, target, "[spotify] Album: "+album+" - by "+artist);
	}
	
	private void showArtistInfo(String artist) {
		wand.sendMessageToTarget(network, target, "[spotify] Artist: "+artist);
	}

	private void showTrackInfo(String track, String artist, String album) {
		wand.sendMessageToTarget(network, target, "[spotify] "+artist+" - "+track+" - Album: "+album);
	}

	public void urlReaderCallback(URLReader urlReader,
			Object[] callbackParameters) {
		// TODO Auto-generated method stub
		
	}
	
}
