package de.lehmannet.om.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.JulianFields;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public class DateManagerImpl implements DateManager {

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    private final SimpleDateFormat sdfDateOnly = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private final SimpleDateFormat sdfSeconds = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            .withZone(ZoneId.systemDefault());
    private final DateTimeFormatter dtfDateOnly = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            .withZone(ZoneId.systemDefault());
    private final DateTimeFormatter dtfSeconds = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    @Override
    public String dateToString(Date date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }

        return this.sdfDateOnly.format(date);
    }

    @Override
    public String dateToStringWithHour(Date date) {

        if (date == null) {
            return StringUtils.EMPTY;
        }

        return this.sdf.format(date);
    }

    @Override
    public String calendarToStringWithHour(Calendar calendar) {

        if (calendar == null) {
            return StringUtils.EMPTY;
        }
        return this.dateToStringWithHour(calendar.getTime());
    }

    @Override
    public String dateToStringWithSeconds(Date date) {

        if (date == null) {
            return StringUtils.EMPTY;
        }

        return this.sdfSeconds.format(date);
    }

    @Override
    public String calendarToStringWithSeconds(Calendar calendar) {

        if (calendar == null) {
            return StringUtils.EMPTY;
        }
        return this.dateToStringWithSeconds(calendar.getTime());
    }

    @Override
    public String calendarToString(Calendar calendar) {
        if (calendar == null) {
            return StringUtils.EMPTY;
        }
        return this.dateToString(calendar.getTime());
    }

    @Override
    public String zonedDateTimeToStringWithHour(ZonedDateTime date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        return date.format(dtf);
    }

    @Override
    public String zonedDateTimeToStringWithSeconds(ZonedDateTime date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        return date.format(dtfSeconds);
    }

    @Override
    public String zonedDateTimeToString(ZonedDateTime date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        return date.format(dtfDateOnly);
    }

    @Override
    public String offsetDateTimeToString(OffsetDateTime date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }

        return zonedDateTimeToString(date.toZonedDateTime());
    }

    @Override
    public String offsetDateTimeToStringWithHour(OffsetDateTime date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }

        return zonedDateTimeToStringWithHour(date.toZonedDateTime());
    }

    @Override
    public String offsetDateTimeToStringWithSeconds(OffsetDateTime date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }

        return zonedDateTimeToStringWithSeconds(date.toZonedDateTime());
    }

    @Override
    public double toAstronomicalJulianDate(ZonedDateTime date) {

        ZonedDateTime datePickerDateInGMAT = date.withZoneSameInstant(ZoneId.of("UTC")).minusHours(12l).withNano(0);
        long dayOfJulianDate = JulianFields.JULIAN_DAY.getFrom(datePickerDateInGMAT);

        double hourPart = (double) datePickerDateInGMAT.getHour();
        double minutePartInHour = (double) datePickerDateInGMAT.getMinute() / 60.0d;
        double secondsPartInHour = (double) datePickerDateInGMAT.getSecond() / 3600.0d;
        double decimalPartOfDay = (minutePartInHour + hourPart + secondsPartInHour) / 24.0d;

        double julianDate = dayOfJulianDate + decimalPartOfDay;
        return julianDate;
    }

    @Override
    public double getAstronomicalJulianDateDay(ZonedDateTime date) {

        ZonedDateTime datePickerDateInGMAT = date.withZoneSameInstant(ZoneId.of("UTC")).minusHours(12l);
        return (double) JulianFields.JULIAN_DAY.getFrom(datePickerDateInGMAT);
    }

    @Override
    public ZonedDateTime fromAstronomicalJulianDate(double date, ZoneId zone) {

        int days = (int) date;
        double decimalPartOfDay = date - days;

        double hoursDecimal = 24 * decimalPartOfDay;
        int hours = (int) hoursDecimal;

        double decimalPartOfHour = hoursDecimal - hours;
        double minutesDecimal = 60 * decimalPartOfHour;
        int minutes = (int) minutesDecimal;

        double decimalPartOfMinutes = minutesDecimal - minutes;
        double secondsDecimal = 60 * decimalPartOfMinutes;
        int seconds = Math.round((float) secondsDecimal);
        int nanoSeconds = 0;

        ZonedDateTime zeroTime = Instant.ofEpochMilli(0l).atZone(ZoneId.of("UTC"));
        long diffDays = days - JulianFields.JULIAN_DAY.getFrom(zeroTime);

        ZonedDateTime julianDate = zeroTime.withHour(hours).withMinute(minutes).withSecond(seconds)
                .withNano(nanoSeconds).plusDays(diffDays).plusHours(12l);

        return julianDate.withZoneSameInstant(zone);

    }

    @Override
    public double parseAstronomicalJulianDate(String jdString) throws ParseException {
        NumberFormat instance = NumberFormat.getInstance();
        instance.setMinimumFractionDigits(6);
        instance.setGroupingUsed(false);
        Number number = instance.parse(jdString);
        double jd = number.doubleValue();
        return jd;
    }

    @Override
    public String formatAsAstronomicalJulianDate(ZonedDateTime date) {
        NumberFormat instance = NumberFormat.getInstance();
        instance.setMinimumFractionDigits(6);
        instance.setGroupingUsed(false);
        return instance.format(toAstronomicalJulianDate(date));
    }

}
