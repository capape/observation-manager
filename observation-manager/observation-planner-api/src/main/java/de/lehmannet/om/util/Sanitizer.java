package de.lehmannet.om.util;

public class Sanitizer {
    public static String toLogMessage(String messageToLog) {

        if (messageToLog == null) {
            return "";
        }

        return messageToLog.replaceAll("[\r\n]", "");

    }

    public static String toLogMessage(Object[] data) {

        if (data == null) {
            return "";
        }

        StringBuffer result = new StringBuffer();
        result.append("[");
        for (Object item : data) {
            result.append(Sanitizer.toLogMessage(item.toString()));
            result.append(", ");
        }
        result.append("]");

        return result.toString().replaceAll("[\r\n]", "");

    }

}
