package no.ntnu.online.onlineguru.plugin.plugins.middag;

import org.junit.Test;

import java.io.*;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * @author Håvard Slettvold
 */
public class UpdateMenuTest {

    public String readResourceFile(String filename) {
        URL url = this.getClass().getResource(filename);
        File file = new File(url.getFile());

        StringBuffer buffer = new StringBuffer();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer.toString();
    }

    @Test
    public void testFindMenuItemsNormalMenu() {
        // Test an actual menu from Hangaren
        String filename = "/middag/normalMenuHangaren.html";
        String html = readResourceFile(filename);

        assertEquals(
                "Champignonsuppe (V,G,L) 20,- Røkt torsk med gulrotstuin... 63,- Svinesteik med surkål, gul... 68,- Vegetarlasagne (V) 62,-",
                UpdateMenu.findMenuItems(html)
        );

        // Test an actual menu from Realfag
        filename = "/middag/normalMenuRealfag.html";
        html = readResourceFile(filename);

        assertEquals(
                "Fiskeburger med stekte pote... 62,- Karbonade i pitabrød med s... 48,- Falaffel med couscouspytt (... 62,- Bønnesuppe med kjøtt (G,L) 20,-",
                UpdateMenu.findMenuItems(html)
        );
    }

    @Test
    public void testFindMenuEmptyMenu() {
        String filename = "/middag/emptyMenu.html";
        String html = readResourceFile(filename);

        assertEquals(
                "Ingen meny tilgjengelig",
                UpdateMenu.findMenuItems(html)
        );
    }

}
