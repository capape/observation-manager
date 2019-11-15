/* ====================================================================
 * /util/DateConverter.java
 *
 * (c) by Dirk Lehmann
 * ====================================================================
 */
package de.lehmannet.om.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;
import java.util.TimeZone;

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
    // ---------
    // Constants ---------------------------------------------------------
    // ---------
    /* Delimiter for ISO8601 date entries. Example: year-month-day */
    private static final String DATE_DELIMITER = "-";
    /* Delimiter for ISO8601 time entries. Example: hour-minute-second */
    private static final String TIME_DELIMITER = ":";
    /* Delimiter for ISO8601 date and time section. Example: dateTtime */
    private static final String DATETIME_DELIMITER = "T";
    /* Timezone symbol for UTC (zulu) time. Used when time is set in UTC. */
    private static final String UTC_TIMEZONE_OFFSET = "Z";

    // --------------
    // Public methods ----------------------------------------------------
    // --------------
    // -------------------------------------------------------------------
    /**
     *
     * Converts a gregorian date into a julian date.
     *
     *
     *
     * @param gregorianDate The gregorianDate date
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
        gregStartDate.set(1583, 9, 16); // 16.10.1583 day of introduction of greg. Calendar. Before that there're no
                                        // leap
                                        // years
        if (gregorianCalendar.after(gregStartDate)) {
            leapYear = (double) (2 - (year / 100) + (year / 400));
        }
        Calendar gregBeginDate = Calendar.getInstance();
        gregBeginDate.set(1583, 9, 4); // 04.10.1583 last day before gregorian calendar reformation
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

        double julianDate = day + c + d + fracSecs + leapYear - 1524.5;

        /*
         * double julianDate = c + d + hourAndMinutes + leapYear - 1524.5;
         *
         * if( (month == 5) || (month == 7) || (month == 10) || (month == 12) ) { //
         * Those month have 31 days julianDate = julianDate-1; }
         */

        // System.out.println("@@ day= " + Double.toString(day) + " month= " +
        // Long.toString(month) + " year= " + Long.toString(year) + " hour= " +
        // Long.toString(hour) + " mimute= " + Long.toString(minute) + " seconds= " +
        // Long.toString(seconds) + " tzOffset= " + Long.toString(tzOffset));
        // System.out.println("@@ JULIAN DATE: " + Double.toString(julianDate) + " ==>
        // c=" + Long.toString(c) + " d= " + Long.toString(d) + " fracSecs= " +
        // Double.toString(fracSecs) + " leapYear= " + Double.toString(leapYear));

        // GMB APR12,2012 - end fix #3514617
        // -------------------------------------------------------------------------------

        return julianDate;
    }

    // -------------------------------------------------------------------
    /**
     *
     * Converts a julian date into a gregorian date.<br>
     *
     *
     *
     * @param julianDate The julian date
     *
     * @return A gregorian date with seconds accuracy (Timezone = GMT)
     */
    public static Calendar toGregorianDate(double julianDate) {
        return DateConverter.toGregorianDate(julianDate, TimeZone.getTimeZone("GMT"));
    }

    // -------------------------------------------------------------------
    /**
     *
     * Converts a julian date into a gregorian date.<br>
     *
     *
     *
     * @param julianDate The julian date
     *
     * @param zone       The timzone for the returned gregorian date (if
     *                   <code>NULL</code> is
     *
     *                   passed GMT will be taken)
     *
     * @return A gregorian date
     */
    public static Calendar toGregorianDate(double julianDate, TimeZone zone) {
        final Double doubleJulianDate = Double.valueOf(julianDate);
        if (doubleJulianDate.isNaN() || doubleJulianDate.isInfinite()) {
            throw new IllegalArgumentException("Julian Date has illegal value. (Value=" + julianDate + ")");
        }
        if (zone == null) {
            zone = TimeZone.getTimeZone("GMT");
        }
        julianDate = julianDate + 0.5;
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

    // -------------------------------------------------------------------
    /**
     *
     * Converts a Date object into a String object that represents a
     *
     * ISO8601 conform string.
     *
     *
     *
     * @param calendar A java.util.Date object that has to be converted
     *
     * @return A ISO8601 conform String, or <code>null</code> if the
     *
     *         given date was <code>null</code>
     */
    public static String toISO8601(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        StringBuffer iso8601 = new StringBuffer();
        iso8601.append(calendar.get(Calendar.YEAR));
        iso8601.append(DATE_DELIMITER);
        iso8601.append(setLeadingZero(calendar.get(Calendar.MONTH) + 1));
        iso8601.append(DATE_DELIMITER);
        iso8601.append(setLeadingZero(calendar.get(Calendar.DAY_OF_MONTH)));
        iso8601.append(DATETIME_DELIMITER);
        iso8601.append(setLeadingZero(calendar.get(Calendar.HOUR_OF_DAY)));
        iso8601.append(TIME_DELIMITER);
        iso8601.append(setLeadingZero(calendar.get(Calendar.MINUTE)));
        iso8601.append(TIME_DELIMITER);
        iso8601.append(setLeadingZero(calendar.get(Calendar.SECOND)));
        // Get Offset in minutes
        int offset = ((calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)) / 1000) / 60;
        iso8601.append(formatTimezone(offset));
        return iso8601.toString();
    }

    // -------------------------------------------------------------------
    /**
     *
     * Converts a String object that contains a ISO8601 conform value to an
     *
     * java.util.Calendar object.
     *
     *
     *
     * @param iso8601 A String with a ISO8601 conform value
     *
     * @return The parameters date as java.util.Calendar, or <code>null</code>
     *
     *         if the given string was <code>null</code> or empty.
     *
     * @throws NumberFormatException if given ISO8601 is malformed.
     */
    public static Calendar toDate(String iso8601) throws NumberFormatException {
        if (iso8601 == null || "".equals(iso8601)) {
            return null;
        }
        StringTokenizer tokenizer = new StringTokenizer(iso8601);
        String year = tokenizer.nextToken(DATE_DELIMITER);
        String month = tokenizer.nextToken(DATE_DELIMITER);
        String day = tokenizer.nextToken(DATETIME_DELIMITER);
        day = day.substring(1, day.length()); // cutoff '-'
        String hour = tokenizer.nextToken(TIME_DELIMITER);
        hour = hour.substring(1, hour.length()); // cutoff 'T'
        hour = cutLeadingZeroAndPlus(hour);
        String minute = tokenizer.nextToken(TIME_DELIMITER);
        minute = cutLeadingZeroAndPlus(minute);
        String secAndTZ = iso8601.substring(iso8601.indexOf(TIME_DELIMITER) + 4, iso8601.length());
        String second = secAndTZ.substring(0, 2);
        second = cutLeadingZeroAndPlus(second);
        String timeZone = secAndTZ.substring(2, secAndTZ.length());
        int i_year = 0;
        int i_month = 0;
        int i_day = 0;
        int i_hour = 0;
        int i_minute = 0;
        int i_second = 0;
        String step = "";
        try {
            step = "year";
            i_year = Integer.parseInt(year);
            step = "month";
            i_month = Integer.parseInt(month) - 1;
            step = "day";
            i_day = Integer.parseInt(day);
            step = "hour";
            i_hour = Integer.parseInt(hour);
            step = "minute";
            i_minute = Integer.parseInt(minute);
            step = "second";
            i_second = Integer.parseInt(second);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("Cannot generate ISO8601 date because " + step + " is malformed. ");
        }
        Calendar calendar = null;
        if ((UTC_TIMEZONE_OFFSET.equals(timeZone)) || ("".equals(timeZone.trim()))) {
            calendar = new GregorianCalendar(new SimpleTimeZone(0, "GMT"));
        } else {
            calendar = new GregorianCalendar(createTimezone(timeZone));
        }
        calendar.set(Calendar.YEAR, i_year);
        calendar.set(Calendar.MONTH, i_month);
        calendar.set(Calendar.DAY_OF_MONTH, i_day);
        calendar.set(Calendar.HOUR_OF_DAY, i_hour);
        calendar.set(Calendar.MINUTE, i_minute);
        calendar.set(Calendar.SECOND, i_second);
        calendar.set(Calendar.MILLISECOND, 0); // make sure they have no meanings if we compare dates
        return calendar;
    }

    // ---------------
    // Private methods ---------------------------------------------------
    // ---------------
    // -------------------------------------------------------------------
    /*
     *
     * Creates a TimeZone object from the last part of a ISO8601 date
     *
     * representing String. (e.g. +1:00)
     */
    private static TimeZone createTimezone(String timeZone) throws NumberFormatException {
        String h = timeZone.substring(0, timeZone.indexOf(TIME_DELIMITER));
        if (h.startsWith("+")) {
            h = cutLeadingZeroAndPlus(h);
        }
        String m = timeZone.substring(timeZone.indexOf(TIME_DELIMITER) + 1, timeZone.length());
        int hour = 0;
        int minute = 0;
        try {
            hour = Integer.parseInt(h);
            minute = Integer.parseInt(m);
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException(
                    "Cannot generate ISO8601 date because timezones hour or minute value is malformed. ");
        }
        // Calculate Timezone Offset:
        // Hour: hour * secOfHour(3600) * mSecOfSecond(1000)
        // Minute: minute * mSecOfSecond(1000) * secOfMinute(60)
        // Depending on negative or positiv offset the minutes have to be added or
        // subtracted from hour offset.
        // Therefore add always and set minutes positiv or negativ (depending on hour
        // value (if it is set)).
        SimpleTimeZone tz = null;
        int offset = 0;
        if (hour == 0) {
            offset = (hour * 3600 * 1000) + ((minute * 1000 * 60));
            tz = new SimpleTimeZone(offset, "");
        } else {
            offset = (hour * 3600 * 1000) + ((minute * 1000 * 60) * (hour / Math.abs(hour)));
            tz = new SimpleTimeZone(offset, "");
        }
        tz.setDSTSavings(1); // We request user to not enter DST
        return tz;
    }

    // -------------------------------------------------------------------
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

    // -------------------------------------------------------------------
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

    // -------------------------------------------------------------------
    /*
     *
     * Cuts off leadings zeros (and the + sign, if given) from a string
     */
    private static String cutLeadingZeroAndPlus(String value) {
        if (value.startsWith("+0")) {
            return value.substring(2, value.length());
        } else if (value.startsWith("-0")) {
            return "-" + value.substring(2, value.length());
        }
        if (value.startsWith("+")) {
            value = value.substring(1, value.length());
        }
        return value;
    }

    // -------------------------------------------------------------------
    /*
     *
     * Takes a value in minutes and formats it an ISO8601 timezone String.
     *
     * If the timezone is UTC, then the returned String will only contain
     *
     * a 'Z' (not 0:00).
     */
    private static String formatTimezone(int min) {
        // Get complete hours
        int hour = min / 60;
        // Get minutes from not complete hour
        int minutes = (hour * 60) - min;
        // If hour and minutes equal 0 (UTC) the offset is given as Z
        if ((hour == 0) && (minutes == 0)) {
            return UTC_TIMEZONE_OFFSET;
        }
        // Calculate the hour offset
        String hourOffset = setLeadingZero(hour);
        if (hour > -1) {
            hourOffset = "+" + hourOffset;
        }
        // Calculate the minute offset
        String minOffset = setLeadingZero(Math.abs(minutes));
        return hourOffset + TIME_DELIMITER + minOffset;
    }
}
