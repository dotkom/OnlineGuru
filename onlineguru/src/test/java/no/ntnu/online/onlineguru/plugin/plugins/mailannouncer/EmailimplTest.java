package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer;

import no.fictive.irclib.model.channel.Channel;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.utils.WandRepository;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Dag Olav Prestegarden <dagolav@prestegarden.com>
 * @author Roy Sindre Norangshol <roy.sindre@norangshol.no>
 */
public class EmailimplTest {
    private EmailImpl testSubject;
    private AnnouncementRepositoryFake repo;
    private static ConcurrentHashMap<String, List<String>> channelsToSendTo = new ConcurrentHashMap<String, List<String>>();

    @Before
    public void setUp() {
        repo = new AnnouncementRepositoryFake();

        Network network = new Network();
        network.setServerAlias("freenode");
        Channel channel = new Channel("#lol");

        ConcurrentHashMap<String, Network> networks = new ConcurrentHashMap<String, Network>();
        ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<String, Channel>();
        networks.put("freenode", network);
        channels.put("#lol", channel);

        WandRepository fakeWand = new FakeWand(networks, channels);
        testSubject = new EmailImpl(fakeWand, repo);
        // Some stupid test data stuff.... you can probably do this better than me :)
        ArrayList<String> channelstoSendTo = new ArrayList<String>();
        channelstoSendTo.add("#lol");
        channelsToSendTo.put("freenode", channelstoSendTo);

    }

    @Test
    public void simpleTestToCheckIfSaveHasBeenCalled() {
        // We add an announcement
        testSubject.addAnnounce(new Announce("toEmail", "fromEmail", channelsToSendTo));

        // We make sure EmailImpl has asked the repository to save
        // the announcement list once - and only once.
        assertEquals(1, repo.getNumberOfTimesSaveHasBeenCalled());
    }

    @Test
    public void testAgainstDoubleKeySave() {

        testSubject.addAnnounce(new Announce(null, null, null, channelsToSendTo, "<funky.id>"));
        assertEquals(1, repo.getNumberOfTimesSaveHasBeenCalled());

        testSubject.addAnnounce(new Announce(null, null, null, channelsToSendTo, "<funky.id>"));

        assertEquals(1, repo.load().size());
        assertEquals(1, repo.getNumberOfTimesSaveHasBeenCalled());

    }

    @Test
    public void simpleTestToVerifyLoad() {
        // We add an announcement
        testSubject.addAnnounce(new Announce("toEmail", "fromEmail", channelsToSendTo));
        // We add an announcement
        testSubject.addAnnounce(new Announce("toEmail2", "fromEmail2", channelsToSendTo));

        assertEquals(2, repo.load().size());
    }

    @Test
    public void simpleTestToVerifyDeletionOfAnAnnounceByEmail() {
        // We add an announcement
        testSubject.addAnnounce(new Announce("toEmail", "fromEmail", channelsToSendTo));
        // We add an announcement
        testSubject.addAnnounce(new Announce("toEmail2", "fromEmail2", channelsToSendTo));
        // These should have been saved

        testSubject.removeAnnounce(LookupAnnounce.getLookup("toEmail", null));

        assertEquals(3, repo.getNumberOfTimesSaveHasBeenCalled());

        HashMap<String, Announce> announces = repo.load();

        assertEquals(1, announces.size());

        assertNotNull(announces.get("toEmail2"));
        assertEquals("toEmail2", announces.get("toEmail2").getToEmail());
    }

    @Test
    public void testEmailAnnounceByListId() {
        //public Boolean announceEmail(String toEmail, String fromEmail, String subject, String listId) {
        String theListId = "<a.funky.list.id>";
        testSubject.addAnnounce(new Announce(null, null, null, channelsToSendTo, theListId));

        assertEquals(1, repo.getNumberOfTimesSaveHasBeenCalled());


        assertEquals(true, testSubject.announceEmail("toEmail", "fromEmail", "someSubject", theListId).booleanValue());
        assertEquals(false, testSubject.announceEmail("toEmail", "fromEmail", "someSubject", "notAdded").booleanValue());
    }


}

