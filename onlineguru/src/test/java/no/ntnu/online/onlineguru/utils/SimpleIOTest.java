package no.ntnu.online.onlineguru.utils;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Roy Sindre Norangshol
 */
public class SimpleIOTest {

    public SimpleIOTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of saveSerializedData method, of class SimpleIO.
     */
    @Test
    public void testSaveSerializedData() {
        System.out.println("saveSerializedData");
        String filename = "unitTestSerializedData";
        Hashtable<String, Integer> data = new Hashtable<String, Integer>();
        data.put("foo", 3);
        data.put("bar", 2);
        boolean result = SimpleIO.saveSerializedData(filename, data);
        assertTrue(result);
    }

    /**
     * Test of loadSerializedData method, of class SimpleIO.
     */
    @Test
    public void testLoadSerializedData() {
        try {
            System.out.println("loadSerializedData");
            String filename = "unitTestSerializedData";
            Hashtable<String, Integer> data = (Hashtable<String, Integer>) SimpleIO.loadSerializedData(filename);
            assertNotNull(data);
            System.out.println(data);
            assertEquals(2, data.size());
            assertEquals(3, data.get("foo").intValue());
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            fail("testSaveSerializedData() test failed, as this test cannot load it's testdata..");
        }
    }
}


