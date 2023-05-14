package de.lehmannet.om.util;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

import org.junit.Test;

public class DateManagerImplTest {

    /*
     *
     * Example 1 — Observation from Istanbul, Turkey (2 hrs east of Greenwich) at 1:15 am, January 10, 2010.
     *
     * Step 1: 01:15 Jan 10 Local Time Step 2: N/A Step 3: 01:15 - 2 hrs = 23:15 Jan 9 UT Step 4: 23:15 - 12 hrs = 11:15
     * Jan 9 GMAT Step 5: decimal = .4688 Step 6: JD for Jan 9, 2010 = 2455206 Final Result: 2455206.4688
     *
     *
     * Example 2 — Observation from Vancouver, BC Canada (8 hrs west of Greenwich) at 5:21 am, February 14, 2010.
     *
     * Step 1: 05:21 Feb 14 Local Time Step 2: N/A Step 3: 05:21 + 8 hrs = 13:21 Feb 14 UT Step 4: 13:21 - 12 hrs =
     * 01:21 Feb 14 GMAT Step 5: JD = 2,455,242 Step 6: decimal = .0563 Final Result: 2,455,242.0563
     *
     *
     */

    private final DateManagerImpl manager = new DateManagerImpl();

    @Test
    public void testFromJulianDate() {

        Calendar cal = DateConverter.toGregorianDate(2455206.4688);
        String dateConverterResult = manager.calendarToStringWithSeconds(cal);

        ZonedDateTime result = manager.fromAstronomicalJulianDate(2455206.4688, ZoneId.of("Turkey"));
        assertEquals(2010, result.getYear());
        assertEquals(1, result.getMonthValue());
        assertEquals(10, result.getDayOfMonth());
        assertEquals(1, result.getHour());
        assertEquals(4, result.getSecond());
        assertEquals("Turkey", result.getZone().getId());

    }

    @Test
    public void testToJulianDate() {

        ZonedDateTime time = ZonedDateTime.of(2010, 1, 10, 1, 15, 4, 0, ZoneId.of("Turkey"));
        double dateConverterResult = DateConverter.toJulianDate(time);
        double dateManagerResult = manager.toAstronomicalJulianDate(time);

        assertEquals(dateConverterResult, dateManagerResult, 0.0);
        assertEquals(2455206.4688, dateManagerResult, 0.001);

    }

    @Test
    public void conmutativeTest() throws ParseException {

        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

        double julianDate = manager.toAstronomicalJulianDate(now);
        ZonedDateTime result = manager.fromAstronomicalJulianDate(julianDate, ZoneId.systemDefault());

        assertEquals(now, result);

        String dateAsString = manager.formatAsAstronomicalJulianDate(now);
        double dateFromString = manager.parseAstronomicalJulianDate(dateAsString);
        assertEquals(julianDate, dateFromString, 0.00001);
    }
}
