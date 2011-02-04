package no.fictive.irclib.control;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import org.apache.log4j.Logger;

/**
 * 
 * @author Espen Jacobsson
 * A class making it easier to send messages to an IRC server.
 */
public class IRCBufferedWriter extends BufferedWriter {
    static Logger logger = Logger.getLogger(IRCBufferedWriter.class);

	/**
	 * Creates a new IRCBufferedWriter.
	 * @param writer A {@link Writer}
	 */
	public IRCBufferedWriter(Writer writer) {
		super(writer);
	}
	
	
	/**
	 * Writes a line to an IRC server.
	 * @param line Line to write.
	 */
	public void writeline(String line) {
		try {
			this.write(line + "\r\n");
			flush();
		} catch (IOException e) {
            logger.error("Failed writing to socket", e.getCause());
		}
	}
}
