package no.ntnu.online.onlineguru.plugin.plugins.spotify;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Håvard Slettvold
 */
public class SpotifyPluginTest{

    SpotifyPlugin spotifyPluginPlugin = new SpotifyPlugin();
    Pattern linkPAttern = spotifyPluginPlugin.getLinkPattern();
    Matcher matcher;

    @Test
    public void linkMatcherTest() {
        String validOpen1 = "test http://open.spotify.com/track/56fwHfJaBpaauvFJrnwk2L";
        String validOpen2 = "http://open.spotify.com/artist/1Cs0zKBU1kc0i8ypK3B9ai test";
        String validOpen3 = "http://open.spotify.com/album/4a2MHqvAGcm2oEoPWhlB7f";
        String validSpotifyURI1 = "spotify:track:56fwHfJaBpaauvFJrnwk2L";
        String validSpotifyURI2 = "spotify:artist:1Cs0zKBU1kc0i8ypK3B9ai";
        String validSpotifyURI3 = "spotify:album:4a2MHqvAGcm2oEoPWhlB7f";

        String invalidOpen1 = "http://open.spotify.com/user/madsop/playlist/6xJXJremRYEFybljM1YJ1z";

        matcher = linkPAttern.matcher(validOpen1);
        assertTrue(matcher.find());
        matcher = linkPAttern.matcher(validOpen2);
        assertTrue(matcher.find());
        matcher = linkPAttern.matcher(validOpen3);
        assertTrue(matcher.find());

        matcher = linkPAttern.matcher(validSpotifyURI1);
        assertTrue(matcher.find());
        matcher = linkPAttern.matcher(validSpotifyURI2);
        assertTrue(matcher.find());
        matcher = linkPAttern.matcher(validSpotifyURI3);
        assertTrue(matcher.find());

        matcher = linkPAttern.matcher(invalidOpen1);
        assertFalse(matcher.find());



    }

}
