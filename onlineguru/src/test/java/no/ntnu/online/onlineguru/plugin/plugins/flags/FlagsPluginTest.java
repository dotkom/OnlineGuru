package no.ntnu.online.onlineguru.plugin.plugins.flags;

import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Håvard Slettvold
 */
public class FlagsPluginTest {

    FlagsPlugin fp = new FlagsPlugin();

    @Test
    public void testSerializeFlags() {
        Set<Flag> flags = new HashSet<Flag>();
        flags.add(Flag.O);
        flags.add(Flag.K);
        flags.add(Flag.o);
        flags.add(Flag.L);
        flags.add(Flag.V);
        // This flag should be ignored by serializing
        flags.add(Flag.ANYONE);

        // Serialize, sort, assert.
        String serialized = fp.serializeFlags(flags);
        char[] fl = serialized.toCharArray();
        Arrays.sort(fl);
        serialized = String.valueOf(fl);

        assertEquals("KLOVo", serialized);
    }

    @Test
    public void testDeserializeFlags() {
        // Q is not a valid flag, and should be logged and ignored.
        String flags = "KLOVoQ";
        Set<Flag> deserialized;

        deserialized = fp.deserializeFlags(flags);

        assertEquals(deserialized.size(), 5);
    }

    @Test
    public void testUpdateFlags() {
        Set<Flag> flags = new HashSet<Flag>();
        flags.add(Flag.O);
        flags.add(Flag.K);
        flags.add(Flag.o);
        flags.add(Flag.L);
        flags.add(Flag.V);

        flags = fp.updateFlags(flags, "+XZbt-fYV+L");

        assertTrue(flags.contains(Flag.b));
        assertTrue(flags.contains(Flag.t));
        assertFalse(flags.contains(Flag.V));
        assertTrue(flags.contains(Flag.L));
    }

}
