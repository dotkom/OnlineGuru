package no.ntnu.online.onlineguru.plugin.plugins.chanserv.model;

import java.util.HashSet;
import java.util.Set;

public class Flags {
	
	private Set<Character> flags = new HashSet<Character>();
	
	public Flags(String flags) {
		updateFlags(flags);
	}
	
	
	/**
	 * O = op
	 * o = deop
	 * V = voice
	 * v = devoice
	 * K = kick
	 * B = ban
	 * b = unban
	 * L = list users
	 * m = mute/unmute
	 * T = topic
	 * @param flags
	 */
	public void updateFlags(String flags) {
		
		String allFlags = "OoVvKBbLmT";
		String removed = "";
		
		for(char c : allFlags.toCharArray()) {
			if(!flags.contains(String.valueOf(c))) {
				removed += c;
			}
		}
				
		boolean added = false;
		for(char c : allFlags.toCharArray()) {
			if(!removed.contains(String.valueOf(c))) { added = true; }
			
			switch(c) {
				case 'O':
					if(added) this.flags.add(c);
					else this.flags.remove(c);
					break;
				case 'o':
					if(added) this.flags.add(c);
					else this.flags.remove(c);
					break;
				case 'V':
					if(added) this.flags.add(c);
					else this.flags.remove(c);
					break;
				case 'v':
					if(added) this.flags.add(c);
					else this.flags.remove(c);
					break;
				case 'K':
					if(added) this.flags.add(c);
					else this.flags.remove(c);
					break;
				case 'B':
					if(added) this.flags.add(c);
					else this.flags.remove(c);
					break;
				case 'b':
					if(added) this.flags.add(c);
					else this.flags.remove(c);
					break;
				case 'L':
					if(added) this.flags.add(c);
					else this.flags.remove(c);
					break;
				case 'm':
					if(added) this.flags.add(c);
					else this.flags.remove(c);
					break;
				case 'T':
					if(added) this.flags.add(c);
					else this.flags.remove(c);
					break;
			}
			added = false;
		}
	}
	
	public boolean containsFlag(char flag) {
		return flags.contains(flag);
	}
}