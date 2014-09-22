package no.ntnu.online.onlineguru.plugin.plugins.flags.storage;

import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.exceptions.MalformedSettingsException;
import no.ntnu.online.onlineguru.exceptions.MissingSettingsException;
import no.ntnu.online.onlineguru.utils.SimpleIO;
import no.ntnu.online.onlineguru.utils.settingsreader.Settings;
import no.ntnu.online.onlineguru.utils.settingsreader.SettingsReader;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author Håvard Slettvold
 */
public class NetworkFlags {
    static Logger logger = Logger.getLogger(NetworkFlags.class);

    private Set<String> superUsers;
    private Map<String, ChannelFlags> channels;
    private final String networkFlagsFile;

    public NetworkFlags(Network network, String networkFlagsFile, String superuser) {
        this.superUsers = new HashSet<String>();
        this.channels = new HashMap<String, ChannelFlags>();
        this.networkFlagsFile = networkFlagsFile;

        initiate(network, networkFlagsFile);
        superUsers.add(superuser);
    }

    private void initiate(Network network, String networkFlagsFile) {
        try {
            List<Settings> settingsList = SettingsReader.readSettings(networkFlagsFile);

            for (Settings settings : settingsList) {
                if (settings.getBlockName().equals("superusers")) {
                    superUsers.addAll(settings.getSettings().keySet());
                }
                else {
                    channels.put(settings.getBlockName(), new ChannelFlags(settings.getSettings()));
                }
            }
        } catch (MalformedSettingsException mse) {
            logger.error("Failed to read settings for "+ network.getServerAlias(), mse.getCause());
        } catch (MissingSettingsException mse) {
            logger.warn("Missing database file for network " + network.getServerAlias() + ". Creating..", mse.getCause());
            try {
                SimpleIO.createFile(networkFlagsFile);
            } catch (IOException ioe) {
                logger.error("Failed to create database file for network "+ network.getServerAlias(), ioe.getCause());
            }
        }
    }

    public void addChannel(String channel) {
        ChannelFlags cf = channels.get(channel);
        if (cf == null) {
            channels.put(channel, new ChannelFlags());
        }
    }

    public boolean saveFlags(String channel, String username, String flags) {
        ChannelFlags cf = channels.get(channel);
        if (cf != null) {
            boolean success = cf.saveFlags(username, flags);
            serializeToFile();
            return success;
        }
        return false;
    }

    public String getFlags(String channel, String username) {
        ChannelFlags cf = channels.get(channel);
        if (cf != null) {
            return cf.getFlags(username);
        }
        return "";
    }

    public boolean isSuperuser(String username) {
        return superUsers.contains(username);
    }

    public boolean addSuperuser(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        if (superUsers.contains(username)) {
            return false;
        }
        else {
            superUsers.add(username);
            serializeToFile();
            return true;
        }
    }

    public boolean removeSuperuser(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        if (!superUsers.contains(username)) {
            return false;
        }
        else {
            superUsers.remove(username);
            serializeToFile();
            return true;
        }
    }

    private void serializeToFile() {
        File db = new File(networkFlagsFile);

        if (!db.exists()) {
            try {
                SimpleIO.createFile(networkFlagsFile);
            } catch (IOException e) {
                logger.error("Failed to create db file: " + networkFlagsFile);
            }
        }

        BufferedWriter writer;

        try {
            writer = new BufferedWriter(new FileWriter(db.getAbsoluteFile()));

            writer.write("[superusers]");
            writer.newLine();
            for (String s : superUsers) {
                writer.write(s+"=1");
                writer.newLine();
            }
            for (String channel : channels.keySet()) {
                if (channels.get(channel).hasFlags()) {
                    writer.write(String.format("[%s]", channel));
                    writer.newLine();
                    channels.get(channel).serializeToFile(writer);
                }
            }

            writer.close();
        } catch (IOException e) {
            logger.error("Failed to serialize flags to file: "+networkFlagsFile);
        }
    }

}
