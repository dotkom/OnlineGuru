package no.ntnu.online.onlineguru.plugin.plugins.middag;

import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.ntnu.online.onlineguru.exceptions.IncompliantCallerException;
import no.ntnu.online.onlineguru.utils.urlreader.impl.HTMLRetriever;

import no.ntnu.online.onlineguru.utils.urlreader.model.Retriever;
import no.ntnu.online.onlineguru.utils.urlreader.model.URLReader;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.xml.xpath.*;

public class UpdateMenu implements URLReader {

    static Logger logger = Logger.getLogger(UpdateMenu.class);
    private Middag middag;
    private int week = 0;
    private int year = 0;
    private String kantine;
    private String day;
    private PrivMsgEvent event;
    private Document pageDocument;

    public UpdateMenu(Middag middag, String url, String day, int week, int year, String kantine) {
        this.middag = middag;
        this.day = day;
        this.week = week;
        this.year = year;
        this.kantine = kantine;

        try {
            new HTMLRetriever(this, url);
        } catch (IncompliantCallerException e) {
            logger.error(e.getMessage(), e.getCause());
        }
    }

    public void setEvent(PrivMsgEvent e) {
        this.event = e;
    }

    public void urlReaderCallback(Retriever urlr) {
        HTMLRetriever hr = (HTMLRetriever)urlr;
        pageDocument = hr.getDOMDocument();

        parseDomDocument();
    }

    public void parseDomDocument() {

        try {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile("//table[@id='menytable']/tbody/tr[td='"+WordUtils.capitalize(day.toLowerCase())+"']/td/table/tr/td/text()");

            Object result = expr.evaluate(pageDocument, XPathConstants.NODESET);
            NodeList nl = (NodeList)result;

            if (nl.getLength() > 0) {
                String menu = "";
                String nodeContent;

                for (int i=0; i<nl.getLength();i++) {
                    nodeContent = nl.item(i).getNodeValue();

                    // if this is dividible by 2, it's a food
                    if (i%2 == 0) {
                        // append separator if there's already content in menu
                        if (!menu.isEmpty()) {
                            menu += ", ";
                        }
                        menu += nodeContent;
                    }
                    // else it's a price
                    else {
                        menu += " - " + nodeContent.replaceAll(",-", "") + " kr";
                    }

                }

                setMenu(kantine, menu);
            }
            else {
                setMenu(kantine, "No menu set.");
            }

            // If event is not null, the update was asked for by a user, and we need to
            // relaunch the event to Middag can process it, now that it is updated.
            if (event != null) {
                middag.incomingEvent(event);
            }

        } catch (XPathExpressionException xpee) {
            logger.error(xpee.getMessage(), xpee.getCause());
        }

    }

    public void setMenu(String kantine, String menu) {
        switch (Kantiner.valueOf(kantine)) {
            case HANGAREN: {
                middag.setHangaren(menu);
                break;
            }
            case REALFAG: {
                middag.setRealfag(menu);
                break;
            }
        }
    }

    public void urlReaderCallback(Retriever Retriever,
                                  Object[] callbackParameters) {
        throw new NotImplementedException();
    }


}