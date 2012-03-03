package no.ntnu.online.onlineguru.plugin.plugins.spotify;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import no.ntnu.online.onlineguru.plugin.plugins.spotify.model.Album;
import no.ntnu.online.onlineguru.plugin.plugins.spotify.model.Artist;
import no.ntnu.online.onlineguru.plugin.plugins.spotify.model.Availability;
import no.ntnu.online.onlineguru.plugin.plugins.spotify.model.Track;
import no.ntnu.online.onlineguru.plugin.plugins.spotify.xml.ModelFactory;
import org.junit.Test;

import java.io.*;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * @author Håvard Slettvold
 */

public class FetchURITest {

    @Test
    public void testArtist() throws FileNotFoundException {
        String filename = "/spotify/SpotifyWebSourceArtist.xml";
        URL url = this.getClass().getResource(filename);
        File file = new File(url.getFile().replaceAll("%20", " "));
              
        Artist artist = ModelFactory.produceArtist(new FileInputStream(file));

        assertEquals("David Guetta", artist.getName());
        assertEquals("Artist: David Guetta", artist.toString());
    }

    @Test
    public void testAlbum() throws FileNotFoundException {
        String filename = "/spotify/SpotifyWebSourceAlbum.xml";
        URL url = this.getClass().getResource(filename);
        File file = new File(url.getFile().replaceAll("%20", " "));

        Album album = ModelFactory.produceAlbum(new FileInputStream(file));

        assertEquals("The Islander", album.getName());
        assertEquals(1, album.getArtists().size());
        assertEquals("Nightwish", album.getArtists().get(0).getName());
        assertEquals(2008, album.getReleased());
        assertEquals("0727361211967", album.getId());
        assertEquals("AD AL AM AZ BA BE BG BY CY EE ES FR GB GE GF GP GR HR IE LI LT LU " +
                "LV MC MD MK MQ MT NC NL PF PM PT RE RO SI SK SM TR UA VA YT", album.getAvailability().toString());
    }
    
    @Test
    public void testTrack() throws FileNotFoundException {
        String filename = "/spotify/SpotifyWebSourceTrack.xml";
        URL url = this.getClass().getResource(filename);
        File file = new File(url.getFile().replaceAll("%20", " "));

        Track track = ModelFactory.produceTrack(new FileInputStream(file));

        assertEquals("Titanium (feat. Sia)", track.getName());
        assertEquals(2, track.getArtists().size());
        assertEquals("David Guetta", track.getArtists().get(0).getName());
        assertEquals("Sia", track.getArtists().get(1).getName());
        assertEquals(true, track.isAvailable());
        assertEquals(245.040000, track.getLength(), '*');
        assertEquals(0.97262, track.getPopularity(), '*');
        assertEquals("Nothing But The Beat", track.getAlbum().getName());
        assertEquals(12, track.getTrackNumber());
    }
    
}
