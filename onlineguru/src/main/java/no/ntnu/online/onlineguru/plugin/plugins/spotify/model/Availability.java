package no.ntnu.online.onlineguru.plugin.plugins.spotify.model;

/**
 * @author Håvard Slettvold
 */
public class Availability {
    
    private String territories;
    
    public Availability() {
        
    }

    public String[] getTerritories() {
        return territories.split(" ");
    }
    
    @Override
    public String toString() {
        return territories;
    }
    
}
