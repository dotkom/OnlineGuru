package no.ntnu.online.onlineguru.plugin.plugins.busbuddy;

/**
 * @author Håvard Slettvold
 */


import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

public class OracleRegexTest {
    private Pattern pattern1;
    private Matcher matcher;

    @Before
    public void setUp() {
        pattern1 = Pattern.compile("(?:Holdeplassen nærmest (?:\\w.+?) er|Buss? \\d+ (?:passerer|går fra|goes from)) (\\w.+?)(?:\\.| (?:kl|at))");
        //pattern2 = Pattern.compile("Buss? \\d+ (?:passerer|går fra|goes from) (\\w.+?) (?:kl|at)");
    }

    @Test
    public void testRegexMatchesToFindTravelDestinationFrom() {
        String oracleAnswer = "Holdeplassen nærmest Gløshaugen er Gløshaugen Syd. Buss 5 passerer  Dronningens gate D3 kl. 1617  og  kommer til Gløshaugen Syd, 8 minutter senere. Buss 52 passerer  Munkegata M3 kl. 1625  og  kommer til  Gløshaugen Syd,  7 minutter senere. Buss 52 passerer  Torget kl. 1626  og  kommer til  Gløshaugen Syd,  6 minutter senere.  Tidene angir tidligste passeringer av holdeplassene.";
        //regex på Holdeplassen nærmest (\w+) er (\w+)
        String oracleAnswer2 = "Buss 5 passerer Glxshaugen Nord kl. 0931 og kl. 1001 og kommer til Sentrumsterminalen, 5-8 minutter senere. Buss 52 passerer Gløshaugen Nord kl. 1010 og kommer til Munkegata M3, 6 minutter senere. Tidene angir tidligste passeringer av holdeplassene.";
        // Buss x passerer (\w+) kl .*
        String oracleAnswer3 = "Buss 5 går fra Ila kl. 1055 til Dronningens gate D3 kl. 1100 og buss 9 går fra Torget kl. 1117 til Heimdal sentrum kl. 1135. Tidene angir tidligste passeringer av holdeplassene.";

        String oracleAnswer4 = "17. Des. 2011 er en lørdag. For denne dato gjelder AtB Vinterruter. Buss 52 passerer Nardokrysset kl. 0722 og kl. 0752 og kommer til Munkegata M3, 9 minutter senere. Buss 8 passerer Nardokrysset kl. 0728 og kommer til Sentrumsterminalen, 10-13 minutter senere. Tidene angir tidligste passeringer av holdeplassene.";

        String oracleAnswer5 = "Bus 7 goes from Reppe at 3.43 pm to Strandveien at 4.07 pm and bus 4 goes from Strandveien at 4.25 pm to Lade allé 80 at 4.40 pm. The hours indicate the earliest passing times.";

        System.out.println("--- Test 1 ---");
        matcher = pattern1.matcher(oracleAnswer);
        assertEquals(true, matcher.find());
        assertEquals("Gløshaugen Syd", matcher.group(1));
        System.out.println("Holdeplass fra: "+ matcher.group(1));

        System.out.println("--- Test 2 ---");
        matcher = pattern1.matcher(oracleAnswer2);
        assertEquals(true, matcher.find());
        assertEquals("Glxshaugen Nord", matcher.group(1));
        System.out.println("Holdeplass fra: "+ matcher.group(1));

        System.out.println("--- Test 3 ---");
        matcher = pattern1.matcher(oracleAnswer3);
        assertEquals(true, matcher.find());
        assertEquals("Ila", matcher.group(1));
        System.out.println("Holdeplass fra: "+ matcher.group(1));

        System.out.println("--- Test 4 ---");
        matcher = pattern1.matcher(oracleAnswer4);
        assertEquals(true, matcher.find());
        assertEquals("Nardokrysset", matcher.group(1));
        System.out.println("Holdeplass fra: "+ matcher.group(1));

        System.out.println("--- Test 5 ---");
        matcher = pattern1.matcher(oracleAnswer5);
        assertEquals(true, matcher.find());
        assertEquals("Reppe", matcher.group(1));
        System.out.println("Holdeplass fra: "+ matcher.group(1));

    }
}