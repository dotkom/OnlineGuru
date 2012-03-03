package no.ntnu.online.onlineguru.plugin.plugins.spotify.model;

/**
 * @author Håvard Slettvold
 */

public class Artist {

    /*
        XML example for artist

        <?xml version="1.0" encoding="iso-8859-1"?>
        <artist xmlns="http://www.spotify.com/ns/music/1">
          <name>David Guetta</name>
        </artist>
    */

    private String name;

    public Artist() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "Artist: "+name;
    }
}
