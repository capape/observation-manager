package de.lehmannet.om.util;

import static org.junit.Assert.assertEquals;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateConverterTest {

    private static Logger log = LoggerFactory.getLogger(DateConverterTest.class);

    @Before
    public void setup() {}

    @Test
    public void testTest() {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 24);
        cal.set(Calendar.MONTH, 11);
        log.info("Date: {} \t(Timezone = {} )", cal, cal.getTimeZone());
        log.info("Gregorian date: {}", new Date(cal.getTimeInMillis()));
        double julian = DateConverter.toJulianDate(cal);

        log.info("Julian Date = {} ", julian);

        Calendar convertedJulianDate = DateConverter.toGregorianDate(julian, TimeZone.getDefault());
        log.info(
                "Converted Julian Date: {} \t(Timezone = {})",
                new Date(convertedJulianDate.getTimeInMillis()),
                convertedJulianDate.getTimeZone().getDisplayName());

        String iso8601 = DateConverter.toISO8601(convertedJulianDate);
        log.info("ISO8601 Date = {} ", iso8601);
        Calendar converted8601Date = DateConverter.toDate(iso8601);
        log.info(
                "Converted ISO8601 Date: {} \t(Timezone = {})",
                new Date(converted8601Date.getTimeInMillis()),
                converted8601Date.getTimeZone().getDisplayName());
    }

    @Test
    public void toJuliandDateTest() {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 24);
        cal.set(Calendar.MONTH, 11);
        log.info("Date: {} \t(Timezone = {} )", cal, cal.getTimeZone());
        log.info("Gregorian date: {}", new Date(cal.getTimeInMillis()));
        double julian = DateConverter.toJulianDate(cal);

        log.info("Julian Date = {} ", julian);

        Calendar convertedJulianDate = DateConverter.toGregorianDate(julian, TimeZone.getDefault());
        log.info(
                "Converted Julian Date: {} \t(Timezone = {})",
                new Date(convertedJulianDate.getTimeInMillis()),
                convertedJulianDate.getTimeZone().getDisplayName());
    }

    @Test
    public void toDateTest() {

        ZonedDateTime now = ZonedDateTime.of(2020, 6, 10, 10, 25, 0, 0, ZoneId.systemDefault());
        Date dateToConvert = Date.from(now.toInstant());
        ZonedDateTime.ofInstant(dateToConvert.toInstant(), ZoneId.systemDefault());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateToConvert);

        GregorianCalendar gc = GregorianCalendar.from(now);

        String expected = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String iso8601 = DateConverter.toISO8601(calendar);

        log.info("ISO8601 Date = {} ", iso8601);
        assertEquals("Calendar to string iso8601:", expected, iso8601);
    }
}
