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
import no.ntnu.online.onlineguru.service.OnlineGuruServices;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class OnlineGuru implements IRCEventListener, Runnable {

    private static Logger logger = Logger.getLogger(OnlineGuru.class);
    public static Injector serviceLocator;

    private Queue<Event> eventQueue = new LinkedList<Event>();

    private ConcurrentHashMap<String, Network> networks = new ConcurrentHashMap<String, Network>();
    private Map<Network, ArrayList<String>> channelsOnConnect = new HashMap<Network, ArrayList<String>>();
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
                Thread.sleep(1);
                Event event = eventQueue.poll();

                if (event != null) {
                    processEvent(event);
                }
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
        serviceLocator = Guice.createInjector(new OnlineGuruServices());
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

    public void receiveEvent(Event event) {
        logger.info(String.format("<- %s: %s", event.getNetwork().getServerAlias(), event.getRawData()));
        eventQueue.offer(event);
    }

    private void processEvent(Event event) {
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
            }
            else {
                sendWho(joinEvent.getChannel(), network);
            }
        }
    }

    private void handleConnect(Event event) {
        Network network = event.getNetwork();
        ArrayList<String> channels = channelsOnConnect.get(network);
        for (String channel : channels) {
            sendToServer(network, "JOIN " + channel);
        }
    }

    private void sendWho(String channelname, Network network) {
        sendToServer(network, "WHO " + channelname);
    }

    private void sendWhoX(String channelname, Network network) {
        sendToServer(network, "WHOX " + channelname);
    }

    public void sendMessageToServer(Network network, String message) {
        sendToServer(network, message);
    }

    private void sendToServer(Network network, String text) {
        logger.info(String.format("-> %s: %s", network.getServerAlias(), text));
        network.sendToServer(text);
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
