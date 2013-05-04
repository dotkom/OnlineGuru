package no.ntnu.online.onlineguru;

import com.google.inject.Guice;
import com.google.inject.Injector;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.JoinEvent;
import no.fictive.irclib.model.listener.IRCEventListener;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.exceptions.MissingSettingsException;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.control.PluginManager;
import no.ntnu.online.onlineguru.utils.OnlineGuruDependencyModule;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;


public class OnlineGuru implements IRCEventListener, Runnable {

    private static Logger logger = Logger.getLogger(OnlineGuru.class);
    public static Injector serviceLocator;


    private ConcurrentHashMap<String, Network> networks = new ConcurrentHashMap<String, Network>();
    private Hashtable<Network, Vector<String>> channelsOnConnect = new Hashtable<Network, Vector<String>>();
    private EventDistributor eventDistributor;
    private Thread thread = null;

    ArrayList<ConnectionInformation> information;

    public OnlineGuru() {
        verifySettings();
        addShutdownHook();
        configureServiceLocator();

        eventDistributor = new EventDistributor();
        initiate();
        new PluginManager(eventDistributor, this);

        thread = new Thread(this);
        thread.setName("OnlineGuru");
        thread.start();
    }

    public void run() {

        //Keep the program rolling.
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                break;
            }
        }

        logger.info("[OnlineGuru] Stopped");
    }

    /**
     * Java version 1.2.3 or newer is required for this.
     */
    private void addShutdownHook() {
        try {
            Runtime.getRuntime().addShutdownHook(new ShutdownThread(this));
            logger.info("[Main thread] Shutdown hook added");
        } catch (Throwable t) {
            logger.warn("[Main thread] Could not add Shutdown hook. Update your Java version.");
        }
    }

    private void verifySettings() {

        try {
            information = VerifySettings.readSettings();
        } catch (MissingSettingsException mse) {
            logger.error(mse.getError(), mse.getCause());
            System.exit(1);
        }
    }

    private void configureServiceLocator() {
        serviceLocator = Guice.createInjector(new OnlineGuruDependencyModule());
    }

    protected void stopThread() {
        rudeDisconnect();
        thread.interrupt();
    }

    protected void rudeDisconnect() {
        Enumeration<Network> networkEnumeration = getNetworks();
        while (networkEnumeration.hasMoreElements()) {
            Network network = networkEnumeration.nextElement();
            network.getProfile().setQuitMessage("Oops, someone killed my running process... ;-(");
            network.disconnect();
        }
    }

    private void initiate() {
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

    public static void main(String[] args) {
        new OnlineGuru();
    }

}
