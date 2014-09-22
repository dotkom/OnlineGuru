package no.ntnu.online.onlineguru.utils;

import java.util.ArrayList;
import java.util.List;

public class MessageChunker {

    public static List<String> chunkMessage(String message, int chunkSize) {

        List<String> chunkedMessages = new ArrayList<String>();

        double numberOfMessages = Math.ceil((double)message.length() / (double)chunkSize);

        for(int i = 0; i < numberOfMessages; i++) {

            int start = i * chunkSize;
            int end = chunkSize + (chunkSize * i);

            if(end > message.length())
                chunkedMessages.add(message.substring(start));
            else
                chunkedMessages.add(message.substring(start, end));
        }

        return chunkedMessages;
    }
}
