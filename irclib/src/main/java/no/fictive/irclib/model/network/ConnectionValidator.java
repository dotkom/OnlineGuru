package no.fictive.irclib.model.network;

/**
 * Makes sure the connection is alive.
 * @author Espen Jacobsson
 */

public class ConnectionValidator implements Runnable {
	
	private boolean running = false;
	
	/**
	 * Max time to receive data from the server.
	 */
	private final int maxResponseTimeout = 300;
	
	/**
	 * Max time to receive response to ping from server.
	 */
	private final int maxPingResponseTimeout = 330;
	
	/**
	 * Max time to try connecting to a server.
	 */
	private final int maxConnectingTimeout = 30;
	
	
	private Network network;
	
	public ConnectionValidator(Network network) {
		this.network = network;
		running = true;
		new Thread(this).start();
	}
	
	public void stop() {
		running = false;
	}
	
	public void run() {
		
		while(running) {
			long lastResponse = network.getLastResponse();
			long now = System.currentTimeMillis();
			State state = network.getState();
			
			switch(state) {
				case CONNECTED:
					if((now - lastResponse) / 1000 >= maxResponseTimeout) {
						network.setState(State.NEEDS_PING);
					}
					break;
				case CONNECTING:
					if((now - lastResponse) / 1000 >= maxConnectingTimeout) {
						network.fireText("Connecting took more than 30 seconds. Trying again...");
					}
					break;
				case DISCONNECTED:
					network.reconnect();
					network.fireText("Disconnected from server, reconnecting.");
					break;
				case NEEDS_PING:
					network.sendToServer("PING " + network.getHostname());
					network.setState(State.PING_SENT);
					break;
				case PING_SENT:
					if((now - lastResponse) / 1000 >= maxPingResponseTimeout) {
						network.setState(State.DISCONNECTED);
					}
					break;
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
