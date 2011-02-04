package no.fictive.irclib.model.network;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import no.fictive.irclib.model.channel.Channel;
import no.fictive.irclib.model.listener.IRCEventListener;
import no.fictive.irclib.model.networksettings.NetworkSettings;
import no.fictive.irclib.model.nick.Nick;
import no.fictive.irclib.model.nick.NickHandler;
import no.fictive.irclib.model.user.Profile;
import no.fictive.irclib.event.container.Event;

/**
 * 	
 * @author Espen Jacobsson
 * This class represents an IRC Network.
 * It holds all nicks visible, all channels,
 * all channelmodes with parameters and all usermodes.
 *
 */
public class Network {
	
	private Vector<IRCEventListener> eventListeners = new Vector<IRCEventListener>();
	
	private NickHandler nickHandler;
	private ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<String, Channel>();
	
	private long lastResponse = 0;
	private State state;
	
	private String hostname;
	private int port;
	private String serveralias;
	private Connection connection;
	private Profile profile;
	private NetworkSettings networkSettings;
	
	private Logger logger = Logger.getLogger(Network.class);
	

    public Network() {

    }

	/**
	 * Creates a new Network.
	 * @param hostname Hostname to connec to.
	 * @param port Port to connect on.
	 * @param serveralias An alias to identify the server with.
	 * @param profile A profile representing the user information for.
	 */
	public Network(String hostname, int port, String serveralias, Profile profile) {
		nickHandler = new NickHandler();
		networkSettings = new NetworkSettings();
		
		this.hostname = hostname;
		this.port = port;
		this.serveralias = serveralias;
		this.profile = profile;
		this.state = State.DISCONNECTED;
		
		connection = new Connection(hostname, port, profile, this, new NetworkEventHandler(this, nickHandler, profile));
	}
	
	
	/**
	 * Connects to the server.
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void connect() {
		try {
			connection.connect();
		} catch (UnknownHostException e) {
            logger.error("Unknown host exception", e.getCause());
		} catch (IOException e) {
            logger.error("I/O error", e.getCause());
		}
	}
	
	public void reconnect() {
		connection.stopConnectionValidation();
		disconnect();
		connection = new Connection(hostname, port, profile, this, new NetworkEventHandler(this, nickHandler, profile));
		connect();
	}
	
	
	/**
	 * Disconnects from the server.
	 */
	public void disconnect() {
		connection.disconnect();
	}
	
	
	/**
	 * Return the networksettings
	 * @return {@link NetworkSettings}
	 */
	public NetworkSettings getNetworkSettings() {
		return networkSettings;
	}
	
	
	/**
	 * Returns the {@link Profile} for this network.
	 * @return The {@link Profile} for this network.
	 */
	public Profile getProfile() {
		return profile;
	}
	
	
	/**
	 * Gets the hostname of the network.
	 * @return The hostname of the network.
	 */
	public String getHostname() {
		return hostname;
	}
	
	
	/**
	 * Sets the hostname of the network.
	 * @param hostname The hostname of the network.
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	
	/**
	 * Gets the port to connect on.
	 * @return The port to connect on.
	 */
	public int getPort() {
		return port;
	}
	
	
	/**
	 * Sets the port to connect on.
	 * @param port The port to connect on.
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	
	/**
	 * Gets the alias of the server.
	 * @return The alias of the server.
	 */
	public String getServerAlias() {
		return serveralias;
	}

    /**
     * Sets the serveralias for this network
     * @param serverAlias Network alias
     */
    public void setServerAlias(String serverAlias) {
        this.serveralias = serverAlias;
    }
	
	
	/**
	 * Sets the time for the last known response from the server.
	 */
	public void gotResponse() {
		lastResponse = System.currentTimeMillis();
		state = State.CONNECTED;
	}
	
	
	/**
	 * Gets the time for the last known response from the server.
	 * @return The time for the last known response from the server.
	 */
	public long getLastResponse() {
		return lastResponse;
	}
	

	/**
	 * Gets the state of the connection
	 * @return
	 */
	public State getState() {
		return state;
	}
	
	
	/**
	 * Sets the state of the connection
	 * @param state
	 */
	public void setState(State state) {
		this.state = state;
	}
	
	
	/**
	 * Removes a channel from this network.
	 * @param channelname Channel name of the channel to be removed.
	 */
	public void removeChannel(String channelname) {
		channels.remove(channelname);
	}
	
	
	/**
	 * Adds a channel with the name given to the network.
	 * @param channelname Name of the channel to be added.
	 */
	public void addChannel(String channelname) {
		channels.putIfAbsent(channelname, new Channel(channelname));
	}
	
	
	/**
	 * Gets a {@link Nick} from this network.
	 * @param nickname  Nickname of the user.
	 * @return
	 */
	public Nick getNick(String nickname) {
		return nickHandler.getNick(nickname);
	}
	
	
	/**
	 * Gets a {@link Channel} from this network.
	 * @param channelname Channel name.
	 * @return A {@link Channel}.
	 */
	public Channel getChannel(String channelname) {
		return channels.get(channelname);
	}
	
	
	/**
	 * Get all channels from this network.
	 * @return A <code>Vector</code> containing all {@link Channel}s.
	 */
	public Collection<Channel> getChannels() {
		return channels.values();
	}
	
	
	/**
	 * Sends an event to all listeners.
	 * @param event {@link Event} to send.
	 */
	public void fireEvent(Event event) {
		for(IRCEventListener listener : eventListeners) {
			listener.receiveEvent(event);
		}
	}
	
	
	/**
	 * Sends text to all listeners.
	 * @param text Text to send.
	 */
	public void fireText(String text) {
		for(IRCEventListener listener : eventListeners) {
			listener.receiveText(this, text);
		}
	}
	
	
	/**
	 * Adds a listener to this network.
	 * @param listener Listener to add.
	 */
	public void addListener(IRCEventListener listener) {
		eventListeners.add(listener);
	}
	
	
	/**
	 * Sends raw format to the server.
	 * @param text Raw format to send.
	 */
	public void sendToServer(String text) {
		connection.writeline(text);
	}
	
	public NickHandler getNickHandler() {
		return nickHandler;
	}
}
