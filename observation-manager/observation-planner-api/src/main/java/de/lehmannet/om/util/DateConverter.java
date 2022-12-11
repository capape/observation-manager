/* ====================================================================
 * /util/DateConverter.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.util;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;

/**
 *
 * The DateConverter is a helper class that provides methods for
 *
 * handling the all kind of date formats.<br>
 *
 * E.g. the ISO8601 date format (A short summary about the ISO8601 date
 *
 * format can be accessed at the
 *
 * <a href="http://www.w3.org/TR/NOTE-datetime">W3C</a>.), or the Julian
 *
 * date.<br>
 *
 *
 *
 * @author doergn@users.sourceforge.net
 *
 * @since 1.0
 */
public class DateConverter {

    // --------------
    // Public methods ----------------------------------------------------
    // --------------
    /**
     *
     * Converts a gregorian date into a julian date.
     *
     *
     *
     * @return A julian date with seconds accuracy
     */
    public static double toJulianDate(Calendar gregorianCalendar) {
        if (gregorianCalendar == null) {
            throw new IllegalArgumentException("Gregorian date has illegal value. (NULL)");
        }
        int month = gregorianCalendar.get(Calendar.MONTH) + 1; // Java Month starts with 0!
        int year = gregorianCalendar.get(Calendar.YEAR);
        int day = gregorianCalendar.get(Calendar.DAY_OF_MONTH);
        int hour = gregorianCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = gregorianCalendar.get(Calendar.MINUTE);
        int seconds = gregorianCalendar.get(Calendar.SECOND);
        // Timezone offset (including Daylight Saving Time) in hours
        int tzOffset = (gregorianCalendar.get(Calendar.DST_OFFSET) + gregorianCalendar.get(Calendar.ZONE_OFFSET))
                / (60 * 60 * 1000);
        if (month < 3) {
            year--;
            month = month + 12;
        }

        // Calculation of leap year
        double leapYear = Double.NaN;
        Calendar gregStartDate = Calendar.getInstance();
        gregStartDate.set(1583, Calendar.OCTOBER, 16); // 16.10.1583 day of introduction of greg. Calendar. Before that
                                                       // there're no
        // leap
        // years
        if (gregorianCalendar.after(gregStartDate)) {
            leapYear = 2 - (year / 100) + ((year / 100) / 4);
        }
        Calendar gregBeginDate = Calendar.getInstance();
        gregBeginDate.set(1583, Calendar.OCTOBER, 4); // 04.10.1583 last day before gregorian calendar reformation
        if ((gregorianCalendar.before(gregBeginDate)) || (gregorianCalendar.equals(gregBeginDate))) {
            leapYear = 0;
        }
        if (Double.isNaN(leapYear)) {
            throw new IllegalArgumentException(
                    "Date is not valid. Due to gregorian calendar reformation after the 04.10.1583 follows the 16.10.1583.");
        }

        // GMB APR12,2012 - fixing bug #3514617
        // ----------------------------------------------------------------------------
        double fracSecs = (double) (((hour - tzOffset) * 3600) + (minute * 60) + seconds) / 86400;

        long c = (long) (365.25 * (year + 4716));
        long d = (long) (30.6001 * (month + 1));

        return day + c + d + fracSecs + leapYear - 1524.5;
    }

    // --------------
    // Public methods ----------------------------------------------------
    // --------------
    /**
     *
     * Converts a gregorian date into a julian date.
     *
     *
     *
     * @return A julian date with seconds accuracy
     */
    public static double toJulianDate(ZonedDateTime datetime) {
        if (datetime == null) {
            throw new IllegalArgumentException("Gregorian date has illegal value. (NULL)");
        }

        final Date date = Date.from(datetime.toInstant());
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return toJulianDate(calendar);
    }

    /**
     *
     * Converts a julian date into a gregorian date.<br>
     *
     *
     *
     * @param julianDate
     *            The julian date
     *
     * @return A gregorian date with seconds accuracy (Timezone = GMT)
     */
    public static Calendar toGregorianDate(double julianDate) {
        return DateConverter.toGregorianDate(julianDate, TimeZone.getTimeZone("GMT"));
    }

    /**
     *
     * Converts a julian date into a gregorian date.<br>
     *
     *
     *
     * @param pjulianDate
     *            The julian date
     *
     * @param pzone
     *            The timzone for the returned gregorian date (if <code>NULL</code> is
     *
     *            passed GMT will be taken)
     *
     * @return A gregorian date
     */
    public static Calendar toGregorianDate(double pjulianDate, TimeZone pzone) {

        if (Double.isNaN(pjulianDate) || Double.valueOf(pjulianDate).isInfinite()) {
            throw new IllegalArgumentException("Julian Date has illegal value. (Value=" + pjulianDate + ")");
        }
        TimeZone zone;
        if (pzone == null) {
            zone = TimeZone.getTimeZone("GMT");
        } else {
            zone = pzone;
        }
        double julianDate = pjulianDate + 0.5;
        int onlyDays = (int) Math.round(julianDate);
        double onlyMinutes = julianDate - onlyDays;
        double hours = 24 * onlyMinutes;
        int hour = (int) (Math.round(hours));
        int minute = (int) ((hours - hour) * 60);
        // int sec = (int)Math.round((hours * 3600) - ((minute * 60) + (hour * 3600)));
        int sec = (int) ((((hours - hour) * 60) - minute) * 60);
        double leapYear100 = (int) ((onlyDays - 1867216.25) / 36524.25);
        double daysLeapYear = onlyDays + 1 + leapYear100 - (int) (leapYear100 / 4);
        if (onlyDays < 2299161) {
            daysLeapYear = onlyDays;
        }
        double completeLeapDays = daysLeapYear + 1524;
        double completeYear = (int) ((completeLeapDays - 122.1) / 365.25);
        double completeDays = (int) (365.25 * completeYear);
        double completeMonths = (int) ((completeLeapDays - completeDays) / 30.6001);
        int day = (int) (completeLeapDays - completeDays - (int) (30.6001 * completeMonths) + onlyMinutes);
        int month = 0;
        if (completeMonths < 14) {
            month = (int) completeMonths - 1;
        } else {
            month = (int) completeMonths - 13;
        }
        int year = 0;
        if (month > 2) {
            year = (int) completeYear - 4716; // only AD years
        } else {
            year = (int) completeYear - 4715; // only AD years
        }
        Calendar gregorianDate = Calendar.getInstance(zone);
        gregorianDate.set(year, month - 1, day + 1);
        // DST offset and timezone offset calculation
        int offset = (gregorianDate.get(Calendar.ZONE_OFFSET) + gregorianDate.get(Calendar.DST_OFFSET)) / (3600 * 1000);
        // Month-1 as January is 0 in JAVA dates/calendars
        gregorianDate.set(year, month - 1, day + 1, hour + offset, minute, sec);
        return gregorianDate;
    }

    /**
     *
     * Converts a Date object into a String object that represents a
     *
     * ISO8601 conform string.
     *
     *
     *
     * @param calendar
     *            A java.util.Date object that has to be converted
     *
     * @return A ISO8601 conform String, or <code>null</code> if the
     *
     *         given date was <code>null</code>
     */
    public static String toISO8601(Calendar calendar) {

        if (calendar == null) {
            return null;
        }

        return DatatypeConverter.printDateTime(calendar);
        /*
         * StringBuilder iso8601 = new StringBuilder(); iso8601.append(calendar.get(Calendar.YEAR));
         * iso8601.append(DATE_DELIMITER); iso8601.append(setLeadingZero(calendar.get(Calendar.MONTH) + 1));
         * iso8601.append(DATE_DELIMITER); iso8601.append(setLeadingZero(calendar.get(Calendar.DAY_OF_MONTH)));
         * iso8601.append(DATETIME_DELIMITER); iso8601.append(setLeadingZero(calendar.get(Calendar.HOUR_OF_DAY)));
         * iso8601.append(TIME_DELIMITER); iso8601.append(setLeadingZero(calendar.get(Calendar.MINUTE)));
         * iso8601.append(TIME_DELIMITER); iso8601.append(setLeadingZero(calendar.get(Calendar.SECOND))); // Get Offset
         * in minutes int offset = ((calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)) / 1000) /
         * 60; iso8601.append(formatTimezone(offset)); return iso8601.toString();
         */
    }

    /**
     *
     * Converts a String object that contains a ISO8601 conform value to an
     *
     * java.util.Calendar object.
     *
     *
     *
     * @param iso8601
     *            A String with a ISO8601 conform value
     *
     * @return The parameters date as java.util.Calendar, or <code>null</code>
     *
     *         if the given string was <code>null</code> or empty.
     *
     * @throws NumberFormatException
     *             if given ISO8601 is malformed.
     */
    public static Calendar toDate(String iso8601) throws NumberFormatException {
        if (iso8601 == null || "".equals(iso8601)) {
            return null;
        }

        return DatatypeConverter.parseDateTime(iso8601);

    }

    /*
     *
     * Sets a leading 0 to a given value, if the value has only one digit.
     */
    public static String setLeadingZero(int value) {
        if ((value <= 9) && (value >= -9)) {
            if (value < 0) {
                return "-0" + Math.abs(value);
            }
            return "0" + Math.abs(value);
        }
        return "" + value;
    }

    /*
     *
     * Sets a leading 0 to a given value, if the value has only one digit.
     */
    public static String setLeadingZero(double value) {
        if ((value <= 9) && (value >= -9)) {
            if (value < 0) {
                return "-0" + Math.abs(value);
            }
            return "0" + Math.abs(value);
        }
        return "" + value;
    }

}
