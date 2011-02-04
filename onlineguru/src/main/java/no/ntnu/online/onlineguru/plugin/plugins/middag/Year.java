package no.ntnu.online.onlineguru.plugin.plugins.middag;

import java.util.HashMap;

class Year {
	
	private HashMap<Integer, Week> yearMenu = new HashMap<Integer, Week>();
	
	public void setMenu(int week, String day, String kantine, String menu) {
		day = day.toLowerCase();
		Week w = yearMenu.get(week);
		if (w != null) {
			w.setMenu(day, kantine, menu);
		}
		else {
			Week newWeek = new Week();
			newWeek.setMenu(day, kantine, menu);
			yearMenu.put(week, newWeek);
		}
	}
	
	public void setWeekMenu(int week, String kantine, String menu) {
		Week w = yearMenu.get(week);
		if (w == null) {
			w = new Week();
			yearMenu.put(week, w);
		}
		w.setWeekMenu(kantine, menu);
	}
	
	public String getMenu(int week, String day, String kantine) {
		day = day.toLowerCase();
		Week w = yearMenu.get(week);
		if (w != null) {
			return w.getMenu(day, kantine);
		}
		else {
			return "Kan ikke hente meny for uke '"+week+"'.";
		}
	}
	
	public boolean hasWeek(int week) {
		Week w = yearMenu.get(week);
		if (w != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void makeWeek(int week) {
		Week w = yearMenu.get(week);
		if (w == null) {
			w = new Week();
			yearMenu.put(week, w);
		}
	}
}