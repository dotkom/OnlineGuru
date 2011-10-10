package no.fictive.irclib.model.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import org.apache.log4j.Logger;

import no.fictive.irclib.control.IRCBufferedWriter;
import no.fictive.irclib.control.IRCEventHandler;
import no.fictive.irclib.control.MessageQueue;
import no.fictive.irclib.model.user.Profile;

/**
 * @author Espen Jacobsson
 *         This class keeps a connection to an IRC server.
 */
public class Connection implements Runnable {

    private IRCEventHandler eventHandler;
    private Network network;
    private ConnectionValidator connectionValidator;

    private Socket socket;
    private BufferedReader reader;
    private MessageQueue messageQueue;
    private Logger logger = Logger.getLogger(Connection.class);

    private boolean running = false;

    private String hostname;
    private int port;
    private Profile profile;


    /**
     * Creates a new connection.
     *
     * @param hostname            Hostname to connect to.
     * @param port                Port to connect on.
     * @param profile             {@link Profile} to associate with this connection.
     * @param network             {@link Network} to associate with this connection.
     * @param networkEventHandler {@link NetworkEventHandler} to create a link with an {@link IRCEventHandler}.
     */
    public Connection(String hostname, int port, Profile profile, Network network, NetworkEventHandler networkEventHandler) {
        this.hostname = hostname;
        this.port = port;
        this.profile = profile;
        this.network = network;
        this.eventHandler = new IRCEventHandler(network, profile, networkEventHandler);
    }

    /**
     * Initiates a connection to the network.
     *
     * @throws UnknownHostException
     * @throws IOException
     */
    public void connect() throws UnknownHostException, IOException {
        network.setState(State.CONNECTING);
        socket = new Socket(hostname, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        messageQueue = new MessageQueue(new IRCBufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
        running = true;
        Random random = new Random();
        Thread thread = new Thread(this);
        thread.setName(network.getServerAlias() + random.nextInt());
        thread.start();
        connectionValidator = new ConnectionValidator(network);
        sendUserInfo();
    }


    /**
     * Sends the initial information to the server based on the information in the {@link Profile}.
     */
    private void sendUserInfo() {
        messageQueue.writeline("CAP LS");
        messageQueue.writeline("NICK " + profile.getNickname());
        messageQueue.writeline("USER " + profile.getNickname() + " 0 * :" + profile.getRealname());
    }


    /**
     * Disconnects from the network the good way, sending a QUIT-message.
     */
    public void disconnect() {
        if (profile.getQuitMessage() == null)
            messageQueue.writeline("QUIT");
        else
            messageQueue.writeline("QUIT :" + profile.getQuitMessage());
        stop();
    }

    /**
     * @see Runnable#run()
     */
    public void run() {
        while (running) {
            try {
                String line = new String(reader.readLine().getBytes(), "UTF-8");
                network.gotResponse();
                eventHandler.handleEvent(line);
            } catch (IOException e) {
                logger.error("Connection broken.");
                close();
            } catch (NullPointerException e) {
                logger.error("Pipe empty.");
                close();
            }
        }
        close();
    }

    private void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            logger.error("I/O error", e.getCause());
        }
        running = false;
    }


    /**
     * Stops this thread.
     */
    protected void stop() {
        close();
    }


    /**
     * Stops the connectionvalidator from running in the background.
     */
    protected void stopConnectionValidation() {
        connectionValidator.stop();
    }


    /**
     * Writes a line to the server.
     *
     * @param line The line to write.
     */
    public void writeline(String line) {
        messageQueue.writeline(line);
    }
}
