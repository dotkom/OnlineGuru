package no.fictive.irclib.control;

import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 
 * @author Espen Jacobsson
 * Flood control, queues up messages.
 * Maximum 4 messages per 2 seconds to avoid excess flood.
 */
public class MessageQueue extends Thread {
    static Logger logger = Logger.getLogger(MessageQueue.class);
	private Queue<String> messageQueue = new LinkedList<String>();
	private IRCBufferedWriter writer;
	private boolean hasMessage = false;

	
	/**
	 * Create a new message queue.
	 * @param writer An {@link IRCBufferedWriter}.
	 */
	public MessageQueue(IRCBufferedWriter writer) {
		this.writer = writer;
		start();
	}

	
	/**
	 * @see Runnable#run()
	 */
	public void run() {
		while (true) {
			try {
				int counter = 0;
				long lastFourMessagesTime = 0;
				while(hasMessage && counter < 4) {
					
					lastFourMessagesTime += System.currentTimeMillis();
					String message = messageQueue.poll();
					
					if(message == null) {
						hasMessage = false;
					} else {
						writer.writeline(message);
						Thread.sleep(600);
						counter++;
					}
				}
				
				if(System.currentTimeMillis() - (lastFourMessagesTime/4) < 2000)
					Thread.sleep(2000);
				else 
					Thread.sleep(200);
				
			} catch (InterruptedException e) {
				logger.error(e.getCause());
			}
		}
	}

	
	/**
	 * Writes a line to the IRC server.
	 * @param message Line to write.
	 */
	public synchronized void writeline(String message) {
		if(message.contains("\r\n") || message.contains("\n") || message.contains("\r")) {
            logger.warn("MESSAGE CONTAINS ILLEGAL NEW-LINE CHARACTERS. NOT PRINTING.");
			return;
		}
		messageQueue.offer(message);
		hasMessage = true;
	}
}
