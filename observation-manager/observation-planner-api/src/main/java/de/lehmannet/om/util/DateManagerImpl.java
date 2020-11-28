package de.lehmannet.om.util;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public class DateManagerImpl implements DateManager {

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
    private final SimpleDateFormat sdfSeconds = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.getDefault());

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
    private final DateTimeFormatter dtfSeconds = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss");

    @Override
    public String dateToString(Date date) {

        if (date == null) {
            return StringUtils.EMPTY;
        }

        return this.sdf.format(date);
    }

    @Override
    public String calendarToString(Calendar calendar) {

        if (calendar == null) {
            return StringUtils.EMPTY;
        }
        return this.dateToString(calendar.getTime());
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
    public String offsetDateTimeToString(OffsetDateTime date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        return date.toLocalDateTime().format(dtf);
    }

    @Override
    public String offsetDateTimeToStringWithSeconds(OffsetDateTime date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        return date.toLocalDateTime().format(dtfSeconds);
    }

}
