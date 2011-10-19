package no.ntnu.online.onlineguru;

import com.google.inject.Guice;
import com.google.inject.Injector;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.JoinEvent;
import no.fictive.irclib.model.listener.IRCEventListener;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.control.PluginManager;
import no.ntnu.online.onlineguru.utils.OnlineGuruDependencyModule;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;


public class OnlineGuru implements IRCEventListener {
    static Logger logger = Logger.getLogger(OnlineGuru.class);
    private ConcurrentHashMap<String, Network> networks = new ConcurrentHashMap<String, Network>();
    private Hashtable<Network, Vector<String>> channelsOnConnect = new Hashtable<Network, Vector<String>>();
    //	private ConnectionManager connectionManager;
    private EventDistributor eventDistributor;
    private Thread thread = null;
    public static Injector serviceLocator;

    public OnlineGuru() {
        final OnlineGuru me = this;
        try {
            Runtime.getRuntime().addShutdownHook(new ShutdownThread(me));
            logger.info("[Main thread] Shutdown hook added");
        } catch (Throwable t) {
            // we get here when the program is run with java
            // version 1.2.2 or older
            logger.warn("[Main thread] Could not add Shutdown hook");
        }

        configureServiceLocator();

        thread = new Thread("OnlineGuru") {
            public void run() {
                eventDistributor = new EventDistributor();
                initiate();
                new PluginManager(eventDistributor, me);

                while (true) {
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException ie) {
                        break;
                    }
                }
                logger.info("[OnlineGuru] Stopped");
            }
        };
        thread.start();
    }

    private void configureServiceLocator() {
        serviceLocator = Guice.createInjector(new OnlineGuruDependencyModule());
    }

    public void stopThread() {
        rudeDisconnect();
        thread.interrupt();
    }

    private void rudeDisconnect() {
        Enumeration<Network> networkEnumeration = getNetworks();
        while (networkEnumeration.hasMoreElements()) {
            Network network = networkEnumeration.nextElement();
            network.getProfile().setQuitMessage("Oops, someone killed my running process... ;-(");
            network.disconnect();
        }
    }

    private void initiate() {
        ArrayList<ConnectionInformation> information = VerifySettings.readSettings();

        for (ConnectionInformation c : information) {
            Network network = new Network(c.getHostname(), Integer.parseInt(c.getPort()), c.getBindAddress(), c.getServeralias(), c.getProfile());
            network.addListener(this);

            channelsOnConnect.put(network, c.getChannels());

            network.connect();
            networks.put(c.getServeralias(), network);
        }
    }

    private void sendWho(String channelname, Network network) {
        network.sendToServer("WHO " + channelname);
    }

    private void sendWhoX(String channelname, Network network) {
        network.sendToServer("WHOX " + channelname);
    }

    public void sendMessageToServer(Network network, String message) {
        network.sendToServer(message);
    }

    public void receiveEvent(Event event) {
        logger.info(String.format("%s: %s", event.getNetwork().getServerAlias(), event.getRawData()));
        switch (event.getEventType()) {
            case JOIN:
                handleJoin(event);
                break;
            case CONNECT:
                handleConnect(event);
                break;
        }
        eventDistributor.handleEvent(event);
    }

    private void handleJoin(Event event) {
        JoinEvent joinEvent = (JoinEvent) event;
        Network network = joinEvent.getNetwork();
        if (network.getProfile().equals(joinEvent.getNick())) {
            if (!network.getNetworkSettings().isNAMESX() && network.getNetworkSettings().isWHOX()) {
                sendWhoX(joinEvent.getChannel(), network);
            } else {
                sendWho(joinEvent.getChannel(), network);
            }
        }
    }

    private void handleConnect(Event event) {
        Network network = event.getNetwork();
        Vector<String> channels = channelsOnConnect.get(network);
        for (String channel : channels) {
            network.sendToServer("JOIN " + channel);
        }
    }

    public void receiveText(Network network, String text) {
//		System.out.println(network.getServerAlias() + ": " + text);
    }

	public Network getNetworkByAlias(String networkAlias) {
		return networks.get(networkAlias);
	}

    public Enumeration<Network> getNetworks() {
        return networks.elements();
    }

    // The ShutdownThread is the thread we pass to the
    // addShutdownHook method
    private static class ShutdownThread extends Thread {
        private OnlineGuru onlineGuru = null;

        public ShutdownThread(OnlineGuru onlineGuru) {
            super();
            this.onlineGuru = onlineGuru;
        }

        public void run() {
            logger.info("[Shutdown thread] Shutting down");
            onlineGuru.rudeDisconnect();
            try {
                logger.warn("[Shutdown thread] Giving Onlineguru 15 seconds to disconnect..");
                sleep(15000L);
            } catch (InterruptedException e) {
                // ignore
            }
            onlineGuru.stopThread();
            logger.info("[Shutdown thread] Shutdown complete");
        }
    }

    public static void main(String[] args) {
        new OnlineGuru();
    }

}
