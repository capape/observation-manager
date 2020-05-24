package de.lehmannet.om.util.test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import de.lehmannet.om.util.DateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DateTester {

    private static Logger log = LoggerFactory.getLogger(DateTester.class);

    public static void main(String[] args) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 24);
        cal.set(Calendar.MONTH, 11);
        log.info("Date: {} \t(Timezone = {} )", cal, cal.getTimeZone());
        log.info("Gregorian date: {}", new Date(cal.getTimeInMillis()));
        double julian = DateConverter.toJulianDate(cal);
        log.info("Julian Date = {} ", julian);
        Calendar convertedJulianDate = DateConverter.toGregorianDate(julian, TimeZone.getDefault());
        log.info("Converted Julian Date: {} \t(Timezone = {})", new Date(convertedJulianDate.getTimeInMillis()),
                convertedJulianDate.getTimeZone().getDisplayName());
        String iso8601 = DateConverter.toISO8601(convertedJulianDate);
        log.info("ISO8601 Date = {} ", iso8601);
        Calendar converted8601Date = DateConverter.toDate(iso8601);
        log.info("Converted ISO8601 Date: {} \t(Timezone = {})", new Date(converted8601Date.getTimeInMillis()),
                converted8601Date.getTimeZone().getDisplayName());

    }
}
