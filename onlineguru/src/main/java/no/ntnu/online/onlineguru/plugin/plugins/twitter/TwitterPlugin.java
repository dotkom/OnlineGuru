package no.ntnu.online.onlineguru.plugin.plugins.twitter;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.SimpleIO;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.*;

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
    private final String TWEETTRIGGER = "!tweet";
    private final String TWITTERTRIGGER = "!twitter";
    private final String settings_folder = "settings/";
    private final String settings_file = settings_folder + "twitter.conf";
    private final String URLIDENT  = "twitter.com/#!/";
    private Pattern tweetIdentPattern;
    private Matcher matcher;
    private Wand wand;
    private Twitter twitter;
    private String token;
    private String secretToken;
    private String consumerKey;
    private String consumerSecret;
    private boolean credentialsOK = false;

    public TwitterPlugin() {
        initiate();

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(consumerKey);
        builder.setOAuthConsumerSecret(consumerSecret);
        builder.setOAuthAccessToken(token);
        builder.setOAuthAccessTokenSecret(secretToken);

        TwitterFactory factory = new TwitterFactory(builder.build());
        twitter = factory.getInstance();

        tweetIdentPattern.compile(URLIDENT);
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
            
            credentialsOK = token != null && secretToken != null && consumerKey != null && consumerSecret != null;

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
                String[] data = privMsgEvent.getMessage().split("\\s+");
                
                if(data.length < 2) return;
                String trigger = data[0];
                String message = privMsgEvent.getMessage().substring(trigger.length()).trim();
                
                if(trigger.equalsIgnoreCase(TWEETTRIGGER)) {
                	if(privMsgEvent.getTarget().equalsIgnoreCase("#online.dotkom")) {
                		if(!credentialsOK) {
                			wand.sendMessageToTarget(e.getNetwork(), "#online.dotkom", "Credentials are incomplete. Did not even try to tweet.");
                		}
                		if(messageHasCorrectTwitterLength(message)) {
                			if(tweet(message)) {
                				wand.sendMessageToTarget(e.getNetwork(), "#online.dotkom", "Twitteret: " + message);
                            } else {
                                wand.sendMessageToTarget(e.getNetwork(), "#online.dotkom", "Feilet ved sendelse av kvitter på det store internett!");
                            }
                		}
                	}
                }
                else if(trigger.equalsIgnoreCase(TWITTERTRIGGER)) {
                	if(data.length == 2) {
                		String screenName = data[1];

                        matcher = tweetIdentPattern.matcher(screenName);
                        boolean matchFound = matcher.find();

                        List<Status> statuses = getStatuses(screenName);

                        if(!matchFound){

                            if(statuses.size() > 0) {
                                Status latestStatus = statuses.get(0);
                                wand.sendMessageToTarget(e.getNetwork(), privMsgEvent.getTarget(), "Tweeted by " +
                                                                                                    latestStatus.getUser().getScreenName() + ", " +
                                                                                                    latestStatus.getCreatedAt().toGMTString() +
                                                                                                    ": " + latestStatus.getText());
                            }
                            else {
                                wand.sendMessageToTarget(e.getNetwork(), privMsgEvent.getTarget(), "Screen name not found, or user has not tweeted anything.");
                            }
                        }
                        else{
                            tweetIdentPattern.compile("\\d+");
                            matcher =  tweetIdentPattern.matcher(screenName);
                            String str="";
                            while(matcher.find()){
                                str += matcher.group();
                            }
                            int ident = Integer.parseInt(str);
                            Status linkedStatus = statuses.get(ident);
                            wand.sendMessageToTarget(e.getNetwork(), privMsgEvent.getTarget(), "Tweeted by " +
                                                                                                linkedStatus.getUser().getScreenName() + ", " +
                                                                                                linkedStatus.getCreatedAt().toGMTString() +
                                                                                                ": " + linkedStatus.getText());

                        }
                	}
                }
            }
        }
    }
    
    private boolean messageHasCorrectTwitterLength(String message) {
    	if(message.length() <= 140)
    		return true;
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
    
    private List<Status> getStatuses(String screenName) {
    	try {
			return twitter.getUserTimeline(screenName);
		} catch (TwitterException e1) {
			e1.printStackTrace();
		}
    	return Collections.emptyList();
    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    public void addWand(Wand wand) {
        this.wand = wand;
    }
}
