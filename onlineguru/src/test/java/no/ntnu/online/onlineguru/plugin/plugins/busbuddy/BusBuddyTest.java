package no.ntnu.online.onlineguru.plugin.plugins.busbuddy;


import no.norrs.busbuddy.pub.api.model.BusStop;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Add some proper tests here for later :p
 */
public class BusBuddyTest {

    @Test
    public void testSwappingDirection() {
        String locationId = "16010050";
        if ((int)locationId.charAt(4) != 1)
            locationId = new StringBuilder(locationId).replace(4,5, String.valueOf(1)).toString();
        assertEquals("16011050", locationId);
    }
    
    @Test 
    public void testRemovingDuplicates() {
        BusBuddyPlugin busBuddyPlugin = new BusBuddyPlugin("foo");
        
        List<BusStop> stops = new ArrayList<BusStop>(Arrays.asList(new BusStop[]{
                new BusStop(1234,"Foo", "fo", "atb", "1", 0,0),
                new BusStop(1234,"2", "fo", "atb", "1", 0,0),
                new BusStop(1234,"Bar", "fo", "atb", "1", 0,0),
                new BusStop(1234,"Bar", "fo", "atb", "0", 0,0),
                new BusStop(1234,"Foo", "fo", "atb", "0", 0,0),
                new BusStop(1234,"2", "fo", "atb", "0", 0,0),
        }));


        List<BusStop> fixedStops = busBuddyPlugin.removeDuplicateBusStops(stops);

        assertEquals(3, fixedStops.size());

        assertEquals("Foo", fixedStops.get(0).getName());
        assertEquals("2", fixedStops.get(1).getName());
        assertEquals("Bar", fixedStops.get(2).getName());
        for (BusStop stop : fixedStops)
            assertEquals("1", stop.getLocationId());
    }
}
