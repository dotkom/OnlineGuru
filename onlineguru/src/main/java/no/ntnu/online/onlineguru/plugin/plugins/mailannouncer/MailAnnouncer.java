package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer;


import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.control.ChanServ;
import no.ntnu.online.onlineguru.utils.SimpleIO;
import no.ntnu.online.onlineguru.utils.WandRepository;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MailAnnouncer is a {@link no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies} in {@link no.ntnu.online.onlineguru.OnlineGuru}
 *
 * @author Roy Sindre Norangshol <roy.sindre@norangshol.no>
 */
public class MailAnnouncer implements PluginWithDependencies {
    static Logger logger = Logger.getLogger(MailAnnouncer.class);
    private static final String DESCRIPTION = "MailAnnouncer announces incoming messages thru our XML RPC Server as Email messages to defined IRC channels";
    private static final String TRIGGER = "mail";
    private final String[] dependencies = new String[]{"ChanServ"};
    private final int XMLRPC_PORT = 9876;
    protected static final String DB_FOLDER = "database/";
    private final String DB_FILE_CLIENTS = DB_FOLDER + "mailannouncer-clients.db";
    private List<String> acceptedRpcClients;
    private EmailImpl emailReceiver;
    private WandRepository wandRepository;
    private ChanServ chanServ;
    private WebServer server;


    public MailAnnouncer() {
        super();
        try {
            acceptedRpcClients = (List<String>) SimpleIO.loadSerializedData(DB_FILE_CLIENTS);
            if (acceptedRpcClients == null) {
                acceptedRpcClients = new ArrayList<String>();
            }
        } catch (FileNotFoundException fileNotFoundException) {
            acceptedRpcClients = new ArrayList<String>();
            logger.error(String.format("Could not load %s", DB_FILE_CLIENTS));
        }
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public void incomingEvent(Event e) {
        switch (e.getEventType()) {
            case PRIVMSG: {
                handleMsg((PrivMsgEvent) e);
                break;
            }
        }
    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    public void addWand(WandRepository wandRepository) {
        this.wandRepository = wandRepository;
        try {
            startServer();
        } catch (XmlRpcException e) {
            logger.error("XML Rpc error", e.getCause());
        } catch (IOException e) {
            logger.error("I/O error", e.getCause());
        }

    }


    public void loadDependency(Plugin plugin) {
        if (plugin instanceof ChanServ) {
            chanServ = (ChanServ) plugin;
        }
    }

    public String[] getDependencies() {
        return dependencies;
    }

    private void handleMsg(PrivMsgEvent privMsgEvent) {
        if (privMsgEvent.isPrivateMessage()) {
            if (privMsgEvent.getMessage().toLowerCase().startsWith(TRIGGER) && chanServ.isNickLoggedIn(privMsgEvent.getSender())) {
                String[] message = privMsgEvent.getMessage().split("\\s+");
                if (message.length > 2) {
                    Boolean result = Boolean.FALSE;
                    if ("add".equalsIgnoreCase(message[1]) && message.length == 5) {
                        result = addAnnounce(message[2], message[3], message[4]);
                        if (result) {
                            wandRepository.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), String.format("Added mail announce for email %s to network %s on channel %s", message[2], message[3], message[4]));
                        } else {
                            wandRepository.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), String.format("Failed to add mail announce for email %s , does it already exist?", message[2]));
                        }
                    } else if ("addlistid".equalsIgnoreCase(message[1]) && message.length == 5) {
                        result = addListIdAnnounce(message[2], message[3], message[4]);
                        if (result) {
                            wandRepository.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), String.format("Added mail announce for list-id %s to network %s on channel %s", message[2], message[3], message[4]));
                        } else {
                            wandRepository.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), String.format("Failed to add mail announce for list id %s , does it already exist?", message[2]));
                        }
                    } else if ("tag".equalsIgnoreCase(message[1]) && message.length == 5) {
                        if ("list".equalsIgnoreCase(message[2])) {
                            result = emailReceiver.setAnnounceTag(LookupAnnounce.getLookup(null, message[3]), message[4]);
                        }   else if ("email".equalsIgnoreCase(message[2])) {
                            result = emailReceiver.setAnnounceTag(LookupAnnounce.getLookup(message[3], null), message[4]);
                        }

                        if (result) {
                            wandRepository.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), String.format("Modified entry %s to set announce tag %s" , message[3], message[4]));
                        } else {
                            wandRepository.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), String.format("Failed to modify entry %s to set announce tag %s" , message[3], message[4]));
                        }
                    }
                } else if ("list".equalsIgnoreCase(message[1].trim())) {
                    Iterator<Announce> announces = emailReceiver.getAnnounces();
                    if (announces.hasNext()) {
                        wandRepository.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), "Listing up mail announces:");
                        while (announces.hasNext()) {
                            Announce announce = announces.next();
                            wandRepository.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), announce.toString());
                        }
                    } else {
                        wandRepository.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getSender(), "No mail announces added yet...");
                    }
                }

            }
        }


    }

    private Boolean addAnnounce(String toEmail, String network, String channel) {
        return addAnnounce(toEmail, network, channel, null);
    }

    private Boolean addAnnounce(String toEmail, String network, String channel, String tag) {
        ConcurrentHashMap<String, List<String>> channelsToAnnounce = new ConcurrentHashMap<String, List<String>>();
        ArrayList<String> channels = new ArrayList<String>();
        channels.add(channel);
        channelsToAnnounce.put(network, channels);

        return emailReceiver.addAnnounce(new Announce(tag, toEmail, null, channelsToAnnounce));
    }

    private Boolean addListIdAnnounce(String listId, String network, String channel) {
        ConcurrentHashMap<String, List<String>> channelsToAnnounce = new ConcurrentHashMap<String, List<String>>();
        ArrayList<String> channels = new ArrayList<String>();
        channels.add(channel);
        channelsToAnnounce.put(network, channels);

        return emailReceiver.addAnnounce(new Announce(null, null, null, channelsToAnnounce, listId));
    }


    /**
     * Helper method for starting the XML RPC Server to accept XML-RPC calls from clients defined in acceptedRpcClients
     *
     * @throws XmlRpcException
     * @throws IOException
     */
    private void startServer() throws XmlRpcException, IOException {
        server = new WebServer(XMLRPC_PORT);
        server.setParanoid(true);
        for (String acceptedHost : acceptedRpcClients) {
            server.acceptClient(acceptedHost);
        }
        server.acceptClient("129.241.105.207"); // quick fix for dworek for now..
        XmlRpcServer xmlRpcServer = server.getXmlRpcServer();
        this.emailReceiver = new EmailImpl(wandRepository);

        PropertyHandlerMapping propertyHandlerMapping = new PropertyHandlerMapping();
        propertyHandlerMapping.setRequestProcessorFactoryFactory(new EmailRequestProcessorFactoryFactory(emailReceiver));
        propertyHandlerMapping.addHandler(Email.class.getName(), Email.class);
        xmlRpcServer.setHandlerMapping(propertyHandlerMapping);

        XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
        serverConfig.setEnabledForExtensions(true);
        serverConfig.setContentLengthOptional(false);
        server.start();
        logger.info(String.format("Started XMLRPC, allowing these hosts for email reporting: %s", Arrays.deepToString((acceptedRpcClients.toArray()))));
    }

    public static void main(String[] args) throws Exception {
        /*
      MailAnnouncer ma = new MailAnnouncer();
      ma.startServer();

      XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
      config.setServerURL(new URL("http://127.0.0.1:8080/xmlrpc"));
      config.setEnabledForExtensions(true);
      config.setConnectionTimeout(60 * 1000);
      config.setReplyTimeout(60 * 1000);

      XmlRpcClient client = new XmlRpcClient();

      // use Commons HttpClient as transport
      client.setTransportFactory(
          new XmlRpcCommonsTransportFactory(client));
      // set configuration
      client.setConfig(config);

      // make the a regular call
      Object[] params = new Object[]
          { new String("Yooo") };
      Boolean result = (Boolean) client.execute("Email.announceEmail", params);

      System.out.println(String.format("Resultat: %s", result));*/
        // make a call using dynamic proxy
        /*ClientFactory factory = new ClientFactory(client);
     Email email = (Email) factory.newInstance(Email.class);
     boolean result = email.announceEmail("Yoo");*/
    }

}
