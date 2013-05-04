package no.ntnu.online.onlineguru;

import org.apache.log4j.Logger;

public class ShutdownThread implements Runnable {

    private OnlineGuru onlineGuru = null;
    private static Logger logger;

    public ShutdownThread(OnlineGuru onlineGuru) {

        super();
        this.onlineGuru = onlineGuru;
        logger = Logger.getLogger(this.getClass());
    }

    public void run() {

        logger.info("[Shutdown thread] Shutting down");
        onlineGuru.rudeDisconnect();

        try {
            logger.warn("[Shutdown thread] Giving Onlineguru 15 seconds to disconnect..");
            Thread.sleep(15000L);
        }
        catch (InterruptedException e) {
            logger.warn(e);
        }

        onlineGuru.stopThread();
        logger.info("[Shutdown thread] Shutdown complete");
    }

}
