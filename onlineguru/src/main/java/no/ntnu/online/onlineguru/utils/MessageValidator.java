package no.ntnu.online.onlineguru.utils;

public class MessageValidator {

    public static boolean isMessageValid(String message, String trigger) {
        // If the message length is lower than the trigger length, return false
        if(message.length() <= trigger.length())
            return false;

        //If the message does not start with the trigger, return false
        if(!message.substring(0, trigger.length()).equalsIgnoreCase(trigger))
            return false;

        String question = message.substring(trigger.length()).trim();

        //If the question for the bus oracle is empty, return false
        if(question.isEmpty())
            return false;

        //Everything is A OKAY! :)
        return true;
    }


    public static String getMessageWithoutTrigger(String message, String trigger) {
        try {
            return message.substring(trigger.length()).trim();
        } catch(StringIndexOutOfBoundsException e) {
            return message;
        }
    }
}
