package no.ntnu.online.onlineguru.plugin.plugins.manuallogin;

import no.ntnu.online.onlineguru.utils.SimpleIO;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Settings {

    private static final Path settingsPath = Paths.get("settings", "manuallogin.conf");
    private static final Logger logger = Logger.getLogger(Settings.class.getName());

    protected static  String registerKeyword      = null;
    protected static String loginKeyword          = null;
    protected static String reloadLoginKeyword    = null;
    protected static String defaultRootPassword   = null;
    protected static String defaultRootUser       = null;


    public static void LoadSettings() {

        try {
            Map<String, String> settings = SimpleIO.loadConfig(settingsPath.toString());

            registerKeyword     = settings.get("registerKeyword");
            loginKeyword        = settings.get("loginKeyword");
            reloadLoginKeyword  = settings.get("reloadLoginKeyword");
            defaultRootUser     = settings.get("defaultRootUser");
            defaultRootPassword = settings.get("defaultRootPassword");

            if(registerKeyword == null)
                settings.put("registerKeyword", "");
            if(loginKeyword == null)
                settings.put("loginKeyword", "");
            if(reloadLoginKeyword == null)
                settings.put("reloadLoginKeyword", "");
            if(defaultRootUser == null)
                settings.put("defaultRootUser", "");
            if(defaultRootPassword == null)
                settings.put("defaultRootPassword", "");

            SimpleIO.saveConfig(settingsPath.toString(), settings);

            if(registerKeyword == null || loginKeyword == null || reloadLoginKeyword == null ||
               defaultRootUser == null || defaultRootPassword == null) {

                logger.warn(String.format("Missing settings for ManualLogin-plugin. See %s", settingsPath.toString()));
            }
        }
        catch (IOException e) {
            logger.warn("Could not load settings file!");
        }
    }
}
