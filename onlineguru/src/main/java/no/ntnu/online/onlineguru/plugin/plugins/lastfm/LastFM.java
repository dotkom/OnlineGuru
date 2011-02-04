package no.ntnu.online.onlineguru.plugin.plugins.lastfm;

import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;

import net.roarsoftware.lastfm.Caller;
import net.roarsoftware.lastfm.Track;
import net.roarsoftware.lastfm.User;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.help.Help;
import no.ntnu.online.onlineguru.utils.SimpleIO;
import no.ntnu.online.onlineguru.utils.WandRepository;
import org.apache.log4j.Logger;


public class LastFM implements PluginWithDependencies {
    static Logger logger = Logger.getLogger(LastFM.class);

	private String apikey = null;
	private final String settings_folder = "settings/";
	private final String settings_file = settings_folder + "lastfm.conf";
	private final String database_folder = "database/";
	private final String database_file = database_folder + "lastfm.db";
	
	private Hashtable<String, String> usernameMapping = new Hashtable<String, String>();
	private WandRepository wandRepository;
	private Help help;
	
	public LastFM() {
		initiate();
		Caller.getInstance().setUserAgent("onlineguru");
	}
	
	private void initiate() {
		try {
			SimpleIO.createFolder(database_folder);
			SimpleIO.createFile(database_file);
			SimpleIO.createFile(settings_file);
			usernameMapping = SimpleIO.loadConfig(database_file);
			apikey = SimpleIO.loadConfig(settings_file).get("apikey");
			
			if(apikey == null) {
				SimpleIO.writelineToFile(settings_file, "apikey=");
                logger.error("Lastfm.conf is not configured correctly");
			}
			else if(apikey.isEmpty()) {
                logger.error("Lastfm API key is empty");
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handleNowPlaying(Event e) {
		PrivMsgEvent pme = (PrivMsgEvent)e;
		
		String message = pme.getMessage();
		String target = pme.getTarget();
		String sender = pme.getSender();
		Network network = pme.getNetwork();
		
		String[] parameters = message.split("\\s+");
		
		if(parameters.length == 1) {
			if(usernameMapping.containsKey(sender)) {
				sendRecentTrack(network, target, usernameMapping.get(sender));
			} else {
			sendRecentTrack(network, target, sender);
			}
		}
		else if (parameters.length == 2) {
			String username = parameters[1];
			sendRecentTrack(network, target, username);
		}
	}
	
	private void sendRecentTrack(Network network, String target, String username) {
		Collection<Track> tracks = User.getRecentTracks(username, apikey);
		
		if(tracks.size() > 0) {
			for(Track track : tracks) {
				String artist = track.getArtist();
				String album = track.getAlbum();
				String song = track.getName();
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:kk:ss");
				Date date =  track.getPlayedWhen();
				
				String lastPlayedWhen = "";
				if(date != null) {
					lastPlayedWhen +=  " - Last played: "  + sdf.format(date);
				}
				if(album != null && !album.isEmpty()) {
					album = " - Album: " + album;
				}
				
				wandRepository.sendMessageToTarget(network, target, artist + " - " + song  + album + lastPlayedWhen);
				//We only want the last song
				break;
			}
		}
	}
	
	private void handleRegisterNickname(Event e) {
		PrivMsgEvent pme = (PrivMsgEvent)e;
		
		String message = pme.getMessage();
		String sender = pme.getSender();
		String[] parameters = message.split("\\s+");
		
		if(parameters.length == 3) {
			usernameMapping.put(sender, parameters[2]);
			try {
				SimpleIO.saveConfig(database_file, usernameMapping);
				wandRepository.sendMessageToTarget(e.getNetwork(), sender, "Your nickname was registered successfully.");
			} catch (IOException e1) {
				e1.printStackTrace();
				wandRepository.sendMessageToTarget(e.getNetwork(), sender, "Something went wrong with registering your !np nick");
			}
		}
	}
	
	private void handleUnregisterNickname(Event e) {
		PrivMsgEvent pme = (PrivMsgEvent)e;
		
		String message = pme.getMessage();
		String sender = pme.getSender();
		String[] parameters = message.split("\\s+");
		
		if(parameters.length == 2) {
			if(usernameMapping.containsKey(sender)) {
				usernameMapping.remove(sender);
				try {
					SimpleIO.saveConfig(database_file, usernameMapping);
					wandRepository.sendMessageToTarget(e.getNetwork(), sender, "Your nickname has been removed.");
				} catch (IOException e1) {
					e1.printStackTrace();
					wandRepository.sendMessageToTarget(e.getNetwork(), sender, "Something went wrong with unregistering your !np nick");
				}
			}
			else {
				wandRepository.sendMessageToTarget(e.getNetwork(), sender, "You have not yet registered your nickname with !np");
			}
		}
	}
	
	public void addEventDistributor(EventDistributor eventDistributor) {
		eventDistributor.addListener(this, EventType.PRIVMSG);
		
	}
	
	public void addWand(WandRepository wandRepository) {
		this.wandRepository = wandRepository;
	}
	
	public String getDescription() {
		return "Displays last.fm information.";
	}
	
	public void incomingEvent(Event e) {
		PrivMsgEvent pme = (PrivMsgEvent)e;
		String message = pme.getMessage();
		
		if(message.startsWith("!np unregister")) {
			handleUnregisterNickname(e);
		}
		else if(message.startsWith("!np register")) {
			handleRegisterNickname(e);
		}
		else if(message.startsWith("!np")) {
			handleNowPlaying(e);
		}
	}
	
	public String[] getDependencies() {
		return new String[] {"Help", };
	}

	public void loadDependency(Plugin plugin) {
		if (plugin instanceof Help) {
			this.help = (Help)plugin;
			help.addPublicTrigger("!np");
			help.addPublicHelp("!np", "!!np <Last.fm username> - Displays the last track played by the supplied Last.fm api.");
		}
	}
	
}
