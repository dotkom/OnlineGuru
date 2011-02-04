package no.ntnu.online.onlineguru.plugin.plugins.middag;

import java.util.HashMap;

class Week {
	
    private HashMap<String, DaysMenu> weekMenu = new HashMap<String, DaysMenu>();
    
    public Week() {
    	for (Dager d : Dager.values()) {
            weekMenu.put(d.getName(), new DaysMenu());
        }
    }
    
    public void setMenu(String day, String kantine, String menu) {
    	DaysMenu d = weekMenu.get(day);
    	if (d != null) {
    		d.setMenu(kantine, menu);
    	}
    }
    
    public void setWeekMenu(String kantine, String menu) {
    	for (DaysMenu dm : weekMenu.values()) {
    		dm.setMenu(kantine, menu);
    	}
    }
    
    public String getMenu(String day, String kantine) {
    	DaysMenu d = weekMenu.get(day);
    	if (d != null) {
    		return d.getMenu(kantine);
    	}
    	else {
    		return "Kan ikke hente meny for '"+day+"'.";
    	}
    }
}// end class Week
