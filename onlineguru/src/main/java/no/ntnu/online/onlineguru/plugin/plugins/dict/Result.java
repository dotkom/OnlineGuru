package no.ntnu.online.onlineguru.plugin.plugins.dict;

import java.util.HashMap;

public class Result {

    // word, translation
    private HashMap<String, String> result;

    public Result() {
        result = new HashMap<String, String>();
    }

    public void addTranslationToWord(String word, String translation) {
        result.put(word, translation);
    }

    public String getTranslation(String word) {
        if (result.containsKey(word)) {
            return result.get(word);
        } else {
            return "Kunne ikke finne ordet '" + word + "' i ordboken.";
        }
    }

    @Override
    public String toString() {
        return "Result { #-enteries: " + result.size() + " }";
    }
}
