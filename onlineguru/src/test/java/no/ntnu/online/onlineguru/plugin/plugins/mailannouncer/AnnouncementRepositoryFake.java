package no.ntnu.online.onlineguru.plugin.plugins.mailannouncer;

import java.util.HashMap;

public class AnnouncementRepositoryFake implements AnnouncementRepository {
    private HashMap<String, Announce> announcements;
    private int numberOfTimesSaved = 0;

    public AnnouncementRepositoryFake() {
        announcements = new HashMap<String, Announce>();
    }

    public void save(HashMap<String, Announce> hashMap) {
        announcements = hashMap;
        numberOfTimesSaved++;
    }

    public HashMap<String, Announce> load() {
        return announcements;
    }

    public int getNumberOfTimesSaveHasBeenCalled() {
        return numberOfTimesSaved;
    }
}
