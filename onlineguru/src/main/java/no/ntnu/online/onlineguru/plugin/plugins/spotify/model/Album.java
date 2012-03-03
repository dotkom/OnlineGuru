package no.ntnu.online.onlineguru.plugin.plugins.spotify.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Håvard Slettvold
 */

public class Album {

    /*
        XML example for album
       
        <?xml version="1.0" encoding="iso-8859-1"?>
        <album xmlns="http://www.spotify.com/ns/music/1">
          <name>The Islander</name>
          <artist href="spotify:artist:2NPduAUeLVsfIauhRwuft1">
            <name>Nightwish</name>
          </artist>
          <released>2008</released>
          <id type="upc">0727361211967</id>
          <availability>
            <territories>AD AL AM AZ BA BE BG BY CY EE ES FR GB GE GF GP GR HR IE LI LT LU LV MC MD MK MQ MT NC NL PF PM PT RE RO SI SK SM TR UA VA YT</territories>
          </availability>
        </album>
    */

    private String name;
    private List<Artist> artists = new ArrayList<Artist>();
    private int released;
    private String id;
    private Availability availability;

    public Album() {

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
    
    public void add(Artist artist) {
        artists.add(artist);
    }

    public int getReleased() {
        return released;
    }

    public void setReleased(int released) {
        this.released = released;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
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
        return "Album: "+name+" - by "+a;
    }
}


