package no.ntnu.online.onlineguru.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleIO {

    private static ArrayList<String> findFolders(String folder) {
        ArrayList<String> folders = new ArrayList<String>();

        String foldername = "";
        for (char c : folder.toCharArray()) {
            foldername += c;
            if (c == '\\' || c == '/') {
                folders.add(foldername);
            }
        }

        return folders;
    }

    private static void createFolders(String folder) {
        ArrayList<String> folders = findFolders(folder);

        for (String f : folders) {
            File file = new File(f);
            if (!file.exists()) {
                file.mkdir();
            }
        }
    }

    public static void createFolder(String foldername) {
        createFolders(foldername);
    }

    public static void createFile(String filename) throws IOException {
        createFolders(filename);
        File file = new File(filename);

        if (file.exists()) {
            return;
        } else {
            file.createNewFile();
        }
    }

    public static void writelineToFile(String filename, String line) throws FileNotFoundException, IOException {
        writeToFile(filename, line + "\r\n");
    }

    public static void writeToFile(String filename, String line) throws FileNotFoundException, IOException {
        File file = new File(filename);

        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        FileWriter writer = new FileWriter(file);
        writer.write(line);
        writer.close();
    }

    public static void appendLinesToFile(String filename, List<String> lines) throws FileNotFoundException, IOException {
        File file = new File(filename);

        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        FileWriter writer = new FileWriter(file, true);
        for (String line : lines)
            writer.write(line + "\r\n");
        writer.close();
    }

    public static void appendLineToFile(String filename, String line) throws FileNotFoundException, IOException {
        appendToFile(filename, line + "\r\n");
    }

    public static void appendToFile(String filename, String line) throws FileNotFoundException, IOException {
        File file = new File(filename);

        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        FileWriter writer = new FileWriter(file, true);
        writer.write(line);
        writer.close();
    }

    public static String readFileAsString(String filename) throws FileNotFoundException, IOException {
        File file = new File(filename);

        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuffer buffer = new StringBuffer();
        String line = null;

        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }

        return buffer.toString();
    }

    public static ArrayList<String> readFileAsList(String filename) throws FileNotFoundException, IOException {
        File file = new File(filename);

        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedReader reader = new BufferedReader(new FileReader(file));
        ArrayList<String> list = new ArrayList<String>();
        String line = null;

        while ((line = reader.readLine()) != null) {
            list.add(line);
        }

        return list;
    }

    public static Map<String, String> loadConfig(String filename) throws FileNotFoundException, IOException {
        ArrayList<String> file = readFileAsList(filename);
        Map<String, String> mappings = new HashMap<String, String>();

        for (String line : file) {
            if (line.contains("=")) {
                String key = line.substring(0, line.indexOf("="));
                String value = line.substring(line.indexOf("=") + 1);

                mappings.put(key, value);
            }
        }

        return mappings;
    }

    public static void saveConfig(String filename, Map<String, String> config) throws IOException {
        File file = new File(filename);
        file.delete();
        file.createNewFile();
        for (String key : config.keySet()) {
            String line = key + "=" + config.get(key);
            appendLineToFile(filename, line);
        }
    }

    public static boolean saveSerializedData(String filename, Object data) {
        FileOutputStream fosStream;
        ObjectOutputStream outStream;
        try {
            File file = new File(filename);
            file.delete();
            file.createNewFile();
            fosStream = new FileOutputStream(file);
            outStream = new ObjectOutputStream(fosStream);
            outStream.writeObject(data);
            outStream.flush();
            outStream.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(SimpleIO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            outStream = null;
            fosStream = null;

        }
        return false;
    }

    public static Object loadSerializedData(String filename) throws FileNotFoundException {
        FileInputStream fis;
        ObjectInputStream ois;
        Object data;
        try {
            File file = new File(filename);
            if (!file.exists()) {
                return null;
            }
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            data = (Object) ois.readObject();
            ois.close();
            fis.close();
            return data;
        } catch (IOException ex) {
            Logger.getLogger(SimpleIO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SimpleIO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ois = null;
            fis = null;

        }
        return null;
    }
}
