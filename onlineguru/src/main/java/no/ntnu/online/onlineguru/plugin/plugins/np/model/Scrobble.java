package no.ntnu.online.onlineguru.plugin.plugins.np.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Håvard Slettvold
 */
public class Scrobble {

    @SerializedName("Album")
    private String album;
    @SerializedName("Artist")
    private String artist;
    @SerializedName("Auth")
    private String auth;
    @SerializedName("Track")
    private String track;
    @SerializedName("Uri")
    private String uri;

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getAuth() {
        return auth;
    }

    public String getTrack() {
        return track;
    }

    public String getUri() {
        return uri;
    }
}
