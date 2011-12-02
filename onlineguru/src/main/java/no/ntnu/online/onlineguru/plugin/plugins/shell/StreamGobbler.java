package no.ntnu.online.onlineguru.plugin.plugins.shell;

import no.fictive.irclib.model.network.Network;

import java.util.*;
import java.io.*;

/**
 * http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html?page=4
 */
public class StreamGobbler extends Thread {
    InputStream is;
    String type;
    private MessageObserver messageObserver;
    private String messageTarget;
    private Network network;

    StreamGobbler(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    public StreamGobbler(InputStream inputStream, String type, MessageObserver messageObserver, Network network, String target) {
        this.is = inputStream;
        this.type = type;
        this.messageObserver = messageObserver;
        this.network = network;
        this.messageTarget = target;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null)
                messageObserver.deliverMessage(network, messageTarget, type + ">" + line);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}