package no.ntnu.online.onlineguru.utils.urlreader;

public interface URLReaderUser {
	public void urlReaderCallback(URLReader urlReader);
	public void urlReaderCallback(URLReader urlReader, Object[] callbackParameters);
}
