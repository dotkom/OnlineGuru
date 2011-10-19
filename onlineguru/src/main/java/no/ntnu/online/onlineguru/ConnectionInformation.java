package no.ntnu.online.onlineguru;

import no.fictive.irclib.model.user.Profile;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

public class ConnectionInformation {
	
	private Profile profile;
	private String hostname = "";
	private String port = "";
    private InetAddress bindAddress = null;
	private String serveralias = "";
	private Vector<String> channels = new Vector<String>();
    private Boolean isIpv6 = false; // defaults to false if not set in config.

    public boolean isValid() {
		if (!serveralias.isEmpty() && !hostname.isEmpty() && !port.isEmpty() && profile != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public Profile getProfile() {
		return profile;
	}
	
	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public String getPort() {
		return port;
	}
	
	public void setPort(String port) {
		this.port = port;
	}

    public boolean hasBindAddress() {
        return bindAddress != null;
    }

    public InetAddress getBindAddress() {
        return bindAddress;
    }

    public void setBindAddress(String bindAddress) throws UnknownHostException {
        if (isIpv6)
            this.bindAddress = Inet6Address.getByName(bindAddress);
        else
            this.bindAddress = Inet4Address.getByName(bindAddress);
    }


    public void setBindAddress(InetAddress bindAddress) throws UnknownHostException {
        this.bindAddress = bindAddress;
    }

    public boolean isIpv6() {
        return this.isIpv6;
    }

    public void setIpv6(boolean isIpv6) {
        this.isIpv6 = isIpv6;
    }
	
	public String getServeralias() {
		return serveralias;
	}
	
	public void setServeralias(String serveralias) {
		this.serveralias = serveralias;
	}
	
	public void addChannel(String channel) {
		channels.add(channel);
	}
	
	public Vector<String> getChannels() {
		return channels;
	}
}
