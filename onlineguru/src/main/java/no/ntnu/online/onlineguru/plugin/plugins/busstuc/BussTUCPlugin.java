package no.ntnu.online.onlineguru.plugin.plugins.busstuc;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.MessageChunker;
import no.ntnu.online.onlineguru.utils.MessageValidator;
import no.ntnu.online.onlineguru.utils.UrlUtil;
import no.ntnu.online.onlineguru.utils.Wand;
import no.ntnu.online.onlineguru.utils.websiteretriever.WebSiteRetrieverFactory;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.nio.charset.Charset;
import java.util.List;

public class BussTUCPlugin implements Plugin {

    private static final String busTucUrl = "http://busstjener.idi.ntnu.no/busstuc/oracle?q=";
    private static final String trigger = "!buss";

    private Logger logger = Logger.getLogger(BussTUCPlugin.class);
    private Wand wand;


    public String getDescription() {
		return "Ask BusTUC questions about buses with '!buss <question>'";
    }

    public void incomingEvent(Event e) {

        PrivMsgEvent event = (PrivMsgEvent) e;
        String message = event.getMessage();

        if(!MessageValidator.isMessageValid(message, trigger))
            return;

        String question = MessageValidator.getMessageWithoutTrigger(message, trigger);
        question = UrlUtil.encodeUrl(question, Charset.forName("utf-8").toString());

        String url = busTucUrl + question;
        String target = wand.getTarget(event);

        //Retrieve the result from BusTUC asynchronously
        WebSiteRetrieverFactory
                .start()
                .setUrl(url)
                .setCaller(this)
                .setMethodName("queryCallback")
                .setParameterTypes(Document.class, Network.class, String.class)
                .setReturnObjects(event.getNetwork(), target)
                .fetch();
    }



    public void queryCallback(Document document, Network network, String target) {

        String answer = document.text();
        List<String> chunkedAnswer = MessageChunker.chunkMessage(answer, 450);

        for(String chunk : chunkedAnswer) {
            wand.sendMessageToTarget(network, target, chunk);
        }
    }


	public void addEventDistributor(EventDistributor eventDistributor) {
		eventDistributor.addListener(this, EventType.PRIVMSG);
	}

	public void addWand(Wand wand) {
		this.wand = wand;
	}
}
