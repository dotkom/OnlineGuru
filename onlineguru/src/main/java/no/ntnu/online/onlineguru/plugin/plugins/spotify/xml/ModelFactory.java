package no.ntnu.online.onlineguru.plugin.plugins.spotify.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import no.ntnu.online.onlineguru.plugin.plugins.spotify.model.Album;
import no.ntnu.online.onlineguru.plugin.plugins.spotify.model.Artist;
import no.ntnu.online.onlineguru.plugin.plugins.spotify.model.Availability;
import no.ntnu.online.onlineguru.plugin.plugins.spotify.model.Track;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author Håvard Slettvold
 */
public class ModelFactory {

    public static Artist produceArtist(InputStream is) {
        XStream xstream = new XStream(new StaxDriver());
        xstream.alias("artist", Artist.class);

        return (Artist)xstream.fromXML(is);
    }
    
    public static Album produceAlbum(InputStream is) {
        XStream xstream = new XStream(new StaxDriver());
        xstream.alias("album", Album.class);
        xstream.alias("artist", Artist.class);
        xstream.alias("availability", Availability.class);
        xstream.addImplicitCollection(Album.class, "artists");

        return (Album)xstream.fromXML(is);
    }
    
    public static Track produceTrack(InputStream is) {
        XStream xstream = new XStream(new StaxDriver());
        xstream.alias("track", Track.class);
        xstream.alias("album", Album.class);
        xstream.alias("artist", Artist.class);
        xstream.alias("availability", Availability.class);
        xstream.addImplicitCollection(Track.class, "artists");
        xstream.aliasField("track-number", Track.class, "trackNumber");

        return (Track)xstream.fromXML(is);
    }
}
