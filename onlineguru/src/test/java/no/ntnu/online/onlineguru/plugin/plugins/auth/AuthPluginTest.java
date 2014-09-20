package no.ntnu.online.onlineguru.plugin.plugins.auth;

import no.fictive.irclib.model.network.Network;
import no.fictive.irclib.model.user.Profile;
import no.ntnu.online.onlineguru.utils.Wand;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Håvard Slettvold
 */
public class AuthPluginTest {

    String myNick = "OnlineGuru";
    String myAltNick = "OnlineAlt";

    AuthPlugin auth;

    Network mockNetwork;
    Profile profile;
    Wand mockWand;
    Set mockSet;

    @Before
    public void setup() {
        mockNetwork = mock(Network.class);
        mockWand = mock(Wand.class);
        mockSet = mock(Set.class);

        profile = new Profile(myNick, myAltNick, "", "", "");
        profile.setCurrentNickname(myNick);

        // Make NickServPlugin and a Network instance
        auth = new AuthPlugin();
        auth.addWand(mockWand);

        // Mock the getting of your nickname
        when(mockNetwork.getProfile()).thenReturn(profile);
        when(mockSet.size()).thenReturn(1);
        when(mockNetwork.commonChannels(myNick)).thenReturn(mockSet);
    }

    @Test
    public void checkNickTest() {
        // Check that currentNick and myNick are equal to start off
        assertEquals(myNick, profile.getCurrentNickname());

        // When running checkNick it should return null since nothing needs to be changed
        String newNick = auth.checkNick(mockNetwork);
        assertNull(newNick);

        // Setting current nick to myAltNick manually, also making sure set returns 0 so we share no channels
        profile.setCurrentNickname(myAltNick);
        when(mockSet.size()).thenReturn(0);

        // When running this now, we should be informed that we should change nick
        newNick = auth.checkNick(mockNetwork);
        // The returned nick should be the primary nick in the profile
        assertNotNull(newNick);
        assertEquals(myNick, newNick);
    }

}
