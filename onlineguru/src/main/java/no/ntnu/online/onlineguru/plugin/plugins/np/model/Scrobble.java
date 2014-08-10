package no.ntnu.online.onlineguru.plugin.plugins.np.model;

/**
 * @author Håvard Slettvold
 */
public class Scrobble {

    private String albumd;
    private String artist;
    private String track;
    private String uri;

    public String getAlbumd() {
        return albumd;
    }

    public void setAlbumd(String albumd) {
        this.albumd = albumd;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
