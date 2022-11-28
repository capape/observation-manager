package de.lehmannet.om.util;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    public String offsetDateTimeToStringWithHour(OffsetDateTime date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        return date.format(dtf);
    }

    @Override
    public String offsetDateTimeToStringWithSeconds(OffsetDateTime date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        return date.format(dtfSeconds);
    }

    @Override
    public String offsetDateTimeToString(OffsetDateTime date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        return date.format(dtfDateOnly);
    }

}
