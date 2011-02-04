package no.ntnu.online.onlineguru.plugin.plugins.middag;

public enum Dager {
	 
	SØNDAG ("søndag"), MANDAG ("mandag"), TIRSDAG ("tirsdag"), ONSDAG ("onsdag"), TORSDAG ("torsdag"), FREDAG ("fredag"), LØRDAG ("lørdag");

    private String name;
    
    private Dager(String name) {
    	this.name = name;
    }
    
    public String getName() {
    	return name;
    }
    
}