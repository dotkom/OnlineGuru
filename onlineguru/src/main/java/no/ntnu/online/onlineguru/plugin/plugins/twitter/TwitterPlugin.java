package no.ntnu.online.onlineguru.plugin.plugins.twitter;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.SimpleIO;
import no.ntnu.online.onlineguru.utils.Wand;
import no.ntnu.online.onlineguru.utils.WandRepository;
import org.apache.log4j.Logger;
import sun.java2d.pipe.SpanShapeRenderer;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.http.AccessToken;
import twitter4j.http.Authorization;
import twitter4j.internal.http.HttpRequest;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: rockj
 * Date: 2/9/11
 * Time: 10:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class TwitterPlugin implements Plugin {
    static Logger logger = Logger.getLogger(TwitterPlugin.class);
    private final String DESCRIPTION = "Twitter plugin";
    private final String TRIGGER = "!tweet";
    private final String settings_folder = "settings/";
    private final String settings_file = settings_folder + "twitter.conf";
    private WandRepository wand;
    private Twitter twitter;
    private String token;
    private String secretToken;
    private String consumerKey;
    private String consumerSecret;

    public TwitterPlugin() {
        initiate();

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(consumerKey);
        builder.setOAuthConsumerSecret(consumerSecret);
        builder.setOAuthAccessToken(token);
        builder.setOAuthAccessTokenSecret(secretToken);

        TwitterFactory factory = new TwitterFactory(builder.build());
        twitter = factory.getInstance();
    }

    private void initiate() {
        try {
            SimpleIO.createFile(settings_file);

            token = SimpleIO.loadConfig(settings_file).get("token");
            secretToken = SimpleIO.loadConfig(settings_file).get("secretToken");
            consumerKey = SimpleIO.loadConfig(settings_file).get("consumerKey");
            consumerSecret = SimpleIO.loadConfig(settings_file).get("consumerSecret");

            if (token == null) {
                SimpleIO.writelineToFile(settings_file, "token=");
                logger.error("Twitter.conf is not configured correctly");
            } else if (token.isEmpty()) {
                logger.error("Twitter token key is empty");
            }
            if (secretToken == null) {
                SimpleIO.writelineToFile(settings_file, "secretToken=");
                logger.error("Twitter.conf is not configured correctly");
            } else if (secretToken.isEmpty()) {
                logger.error("Twitter secretToken key is empty");
            }

            if (consumerKey == null) {
                SimpleIO.writelineToFile(settings_file, "consumerKey=");
                logger.error("Twitter.conf is not configured correctly");
            } else if (consumerKey.isEmpty()) {
                logger.error("Twitter token key is empty");
            }
            if (consumerSecret == null) {
                SimpleIO.writelineToFile(settings_file, "consumerSecret=");
                logger.error("Twitter.conf is not configured correctly");
            } else if (consumerSecret.isEmpty()) {
                logger.error("Twitter secretToken key is empty");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public void incomingEvent(Event e) {
        switch (e.getEventType()) {
            case PRIVMSG: {
                PrivMsgEvent privMsgEvent = (PrivMsgEvent) e;
                if (privMsgEvent.isChannelMessage() && privMsgEvent.getTarget().trim().equalsIgnoreCase("#online.dotkom")) {
                    if (messageContainsValidTwitterAndCorrectTwitterLength(privMsgEvent.getMessage())) {
                        String[] message = privMsgEvent.getMessage().split("\\s+");
                        if (message.length > 1 && message[0].trim().equalsIgnoreCase(TRIGGER)) {
                            String messageToTweet = privMsgEvent.getMessage().substring(TRIGGER.length()).trim();
                            if (tweet(messageToTweet)) {
                                wand.sendMessageToTarget(e.getNetwork(), "#online.dotkom", "Twitteret: " + messageToTweet);
                            } else {
                                wand.sendMessageToTarget(e.getNetwork(), "#online.dotkom", "Feilet ved sendelse av kvitter på det store internett!");
                            }
                        }
                    }
                }
            }
        }

    }

    private boolean messageContainsValidTwitterAndCorrectTwitterLength(String message) {
        if (
                (message.length() > (TRIGGER.length() + 1)) &&
                        ((message.length() - TRIGGER.length()) < 140)
                ) {
            return true;
        }
        return false;
    }

    private boolean tweet(String tweetMessage) {
        try {
            twitter.updateStatus(tweetMessage);
            return true;
        } catch (TwitterException e) {
            logger.error("Twitter exception", e.getCause());
        }
        return false;
    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    public void addWand(WandRepository wandRepository) {
        this.wand = wandRepository;
    }

}
