package no.ntnu.online.onlineguru.utils;


public class TextColor {
	
	public enum Color {
		WHITE(0),
		BLACK(1),
		DARKBLUE(2),
		DARKGREEN(3),
		RED(4),
		MAGNETA(5),
		PURPLE(6),
		ORANGE(7),
		YELLOW(8),
		LIGHTGREEN(9),
		TEAL(10),
		LIGHTBLUE(11),
		BLUE(12),
		PINK(13),
		DARKGRAY(14),
		GRAY(15),
		ENDCOLOR(16);
		
		private int color;
		
		Color(int color) {
			this.color = color;
		}
		
		public int getValue() {
			return color;
		}
	}

	private int foreground = -1;
	private int background = -1;
	
	public TextColor(TextColor.Color foreground) {
		this.foreground = foreground.getValue();
	}
	
	public TextColor(TextColor.Color foreground, TextColor.Color background) {
		this.foreground = foreground.getValue();
		this.background = background.getValue();
	}
	
	@Override
	public String toString() {
		String colors = (char)3 + "";
		
		if(foreground == 16 || background == 16) {
			return colors;
		}
		
		if(foreground != -1) {
			colors += String.valueOf(foreground);
		}
		
		if(background != -1) {
			colors += "," + String.valueOf(background);
		}
		
		return colors;
	}
}
