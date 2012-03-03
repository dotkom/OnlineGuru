package no.ntnu.online.onlineguru.utils.urlreader;

import no.ntnu.online.onlineguru.utils.urlreader.connection.URLFactory;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Håvard Slettvold
 */
public class URLFactoryTest {
    
    Pattern pattern;
    Matcher matcher;      
    URLFactory urlfactory = new URLFactory("test");
    
    @Test
    public void xmlRegexTest() {
        pattern = Pattern.compile(urlfactory.xmlRegex(), Pattern.CASE_INSENSITIVE);
        
        String xml = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>";
        matcher = pattern.matcher(xml);
        assertTrue(matcher.find());
        assertEquals("iso-8859-1", matcher.group(1));
        
        
    }
    
    @Test
    public void htmlRegexTest() {
        pattern = Pattern.compile(urlfactory.htmlRegex(), Pattern.CASE_INSENSITIVE);

        String html, xhtml;

        html = "<meta http-equiv=\"content-type\" content=\"text/html; charset=iso-8859-1\">";
        matcher = pattern.matcher(html);
        assertTrue(matcher.find());
        assertEquals("iso-8859-1", matcher.group(1));

        xhtml = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />";
        matcher = pattern.matcher(xhtml);
        assertTrue(matcher.find());
        assertEquals("utf-8", matcher.group(1));

        html = "<meta charset='utf-8'>";
        matcher = pattern.matcher(html);
        assertTrue(matcher.find());
        assertEquals("utf-8", matcher.group(1));
        
        xhtml = "<meta charset=\"utf-8\" />";
        matcher = pattern.matcher(xhtml);
        assertTrue(matcher.find());
        assertEquals("utf-8", matcher.group(1));
    }
    
}
