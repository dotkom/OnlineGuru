package no.ntnu.online.onlineguru.utils.urlreader.model;

/**
 * @author Håvard Slettvold
 */

public interface URLReader {

    public void urlReaderCallback(Retriever retriever);
    public void urlReaderCallback(Retriever retriever, Object[] callbackParameters);

}