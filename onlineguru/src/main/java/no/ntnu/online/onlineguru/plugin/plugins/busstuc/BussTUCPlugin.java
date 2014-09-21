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
import no.ntnu.online.onlineguru.utils.websiteretriever.model.WebSiteRetrieverResult;
import org.apache.log4j.Logger;

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
        String target = event.getTarget();

        //Retrieve the result from BusTUC asynchronously

        WebSiteRetrieverFactory
                .start()
                .setUrl(url)
                .setCaller(this)
                .setMethodName("queryCallback")
                .setReturnObjects(event.getNetwork(), target)
                .fetch();
    }



    public void queryCallback(WebSiteRetrieverResult result) throws ClassCastException {

        String answer = result.getDocument().text();

        Network network = (Network) result.getPassedObjects()[0];
        String target = (String) result.getPassedObjects()[1];

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
