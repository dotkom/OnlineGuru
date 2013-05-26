package no.ntnu.online.onlineguru.plugin.plugins.git.storage;

import no.ntnu.online.onlineguru.plugin.plugins.git.GitAnnouncementRepository;
import no.ntnu.online.onlineguru.plugin.plugins.git.IRCAnnounce;
import no.ntnu.online.onlineguru.utils.SimpleIO;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.util.HashMap;


/**
 * @author Roy Sindre Norangshol <roy.sindre at norangshol dot no>
 *         Date: 11.04.11
 *         Time: 16:47
 */
public class SqliteGitAnnouncementPersister implements GitAnnouncementRepository {


    static Logger logger = Logger.getLogger(SqliteGitAnnouncementPersister.class);
    private String dbFile;

    public SqliteGitAnnouncementPersister(String dbFileForAnnouncements) {
        dbFile = dbFileForAnnouncements;
    }

    public void save(HashMap<String, IRCAnnounce> hashMap) {
        SimpleIO.saveSerializedData(dbFile, hashMap);
    }

    public HashMap<String, IRCAnnounce> load() {
        HashMap<String, IRCAnnounce> announceHashMap;
        try {
            announceHashMap = (HashMap<String, IRCAnnounce>) SimpleIO.loadSerializedData(dbFile);
            if (announceHashMap == null) {
                return new HashMap<String, IRCAnnounce>();
            }
        } catch (FileNotFoundException fileNotFoundException) {
            logger.error(String.format("Could not load %s", dbFile));
            return new HashMap<String, IRCAnnounce>();
        }
        return announceHashMap;
    }
}


