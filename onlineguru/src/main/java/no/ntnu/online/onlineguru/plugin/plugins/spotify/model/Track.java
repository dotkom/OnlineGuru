package no.ntnu.online.onlineguru.plugin.plugins.spotify.model;

/**
 * @author Håvard Slettvold
 */

import java.util.ArrayList;
import java.util.List;

public class Track {

    /*
        XML example for track
        
        <?xml version="1.0" encoding="iso-8859-1"?>
        <track xmlns="http://www.spotify.com/ns/music/1">
          <name>Titanium (feat. Sia)</name>
          <artist href="spotify:artist:1Cs0zKBU1kc0i8ypK3B9ai">
            <name>David Guetta</name>
          </artist>
          <artist href="spotify:artist:5WUlDfRSoLAfcVSX1WnrxN">
            <name>Sia</name>
          </artist>
          <album href="spotify:album:0qCuRUxCZIQuqlboSVQ1tB">
            <name>Nothing But The Beat</name>
            <availability>
              <territories>AT BE CA CH CY CZ DE DK EE ES FI FR GB GR HR HU IE IT LT LU NL NO PL PT RO SE SI SK</territories>
            </availability>
          </album>
          <available>true</available>
          <id type="isrc">GB28K1100036</id>
          <track-number>12</track-number>
          <length>245.040000</length>
          <popularity>0.97262</popularity>
        </track>
    
     */
    private String name;
    List<Artist> artists = new ArrayList<Artist>();
    private Album album;
    private boolean available;
    private double length;
    private double popularity;
    private String id;
    private int trackNumber;

    public Track() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    @Override
    public String toString() {
        String a = "";
        for (Artist artist : artists) {
            if (!a.isEmpty()) {
                a += ", ";
            }
            a += artist.getName();
        }
        return a+" - "+name+" - Album: "+album.getName();
    }
}
