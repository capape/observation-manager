package de.lehmannet.om.util;

import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;

public interface DateManager {

    /**
     * 
     * @param date
     * @return the date formatted or empty string
     */
    String dateToString(Date date);

    /**
     * 
     * @param date
     * @return the date formatted or empty string
     */
    String calendarToString(Calendar calendar);

    /**
     * 
     * @param date
     * @return the date formatted or empty string
     */
    String dateToStringWithSeconds(Date date);

    /**
     * 
     * @param date
     * @return the date formatted or empty string
     */
    String calendarToStringWithSeconds(Calendar calendar);

    /**
     * 
     * @param date
     * @return the date formatted or empty string
     */
    String offsetDateTimeToString(OffsetDateTime date);

    /**
     * 
     * @param date
     * @return the date formatted or empty string
     */
    String offsetDateTimeToStringWithSeconds(OffsetDateTime date);

}
