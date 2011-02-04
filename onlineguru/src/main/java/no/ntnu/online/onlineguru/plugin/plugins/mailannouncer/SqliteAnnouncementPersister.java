package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer;

import no.ntnu.online.onlineguru.utils.SimpleIO;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * @author Dag Olav Prestegarden <dagolav@prestegarden.com>
 * @author Roy Sindre Norangshol <roy.sindre@norangshol.no>
 */
public class SqliteAnnouncementPersister implements AnnouncementRepository {
    static Logger logger = Logger.getLogger(SqliteAnnouncementPersister.class);
    private String dbFile;

    public SqliteAnnouncementPersister(String dbFileForAnnouncements) {
        dbFile = dbFileForAnnouncements;
    }

    public void save(HashMap<String, Announce> hashMap) {
        SimpleIO.saveSerializedData(dbFile, hashMap);
    }

    public HashMap<String, Announce> load() {
        HashMap<String, Announce> announceHashMap;
        try {
            announceHashMap = (HashMap<String, Announce>) SimpleIO.loadSerializedData(dbFile);
            if (announceHashMap == null) {
                return new HashMap<String, Announce>();
            }
        } catch (FileNotFoundException fileNotFoundException) {
            logger.error(String.format("Could not load %s", dbFile));
            return new HashMap<String, Announce>();
        }
        return announceHashMap;
    }
}
