package de.lehmannet.om.util;

import java.text.ParseException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

public interface DateManager {

    /**
     * @param date
     *
     * @return the date formatted or empty string
     */
    String dateToString(Date date);

    /**
     * @param date
     *
     * @return the date formatted or empty string
     */
    String calendarToString(Calendar calendar);

    /**
     * @param date
     *
     * @return the date formatted or empty string
     */
    String dateToStringWithHour(Date date);

    /**
     * @param date
     *
     * @return the date formatted or empty string
     */
    String calendarToStringWithHour(Calendar calendar);

    /**
     * @param date
     *
     * @return the date formatted or empty string
     */
    String dateToStringWithSeconds(Date date);

    /**
     * @param date
     *
     * @return the date formatted or empty string
     */
    String calendarToStringWithSeconds(Calendar calendar);

    /**
     * @param date
     *
     * @return the date formatted or empty string in current zone
     */
    String offsetDateTimeToString(OffsetDateTime date);

    /**
     * @param date
     *
     * @return the date formatted or empty string in current zone
     */
    String offsetDateTimeToStringWithHour(OffsetDateTime date);

    /**
     * @param date
     *
     * @return the date formatted or empty string in current zone
     */
    String offsetDateTimeToStringWithSeconds(OffsetDateTime date);

    /**
     * @param date
     *
     * @return the date formatted or empty string
     */
    String zonedDateTimeToString(ZonedDateTime date);

    /**
     * @param date
     *
     * @return the date formatted or empty string
     */
    String zonedDateTimeToStringWithHour(ZonedDateTime date);

    /**
     * @param date
     *
     * @return the date formatted or empty string
     */
    String zonedDateTimeToStringWithSeconds(ZonedDateTime date);

    ZonedDateTime fromAstronomicalJulianDate(double date, ZoneId zone);

    double toAstronomicalJulianDate(ZonedDateTime date);

    double getAstronomicalJulianDateDay(ZonedDateTime date);

    double parseAstronomicalJulianDate(String jdString) throws ParseException;
    ZonedDateTime parseJulianDateToZonedDateTime(String jdString);

    String formatAsAstronomicalJulianDate(ZonedDateTime date);

}
