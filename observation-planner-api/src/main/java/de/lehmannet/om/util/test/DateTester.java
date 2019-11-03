package de.lehmannet.om.util.test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import de.lehmannet.om.util.DateConverter;

public class DateTester {

	public static void main(String[] args) {
        
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 24);
        cal.set(Calendar.MONTH, 11);        
        System.out.println("Date: " + cal + "\t(Timezone = " + cal.getTimeZone() + ")");
        System.out.println("Gregorian date: " + new Date(cal.getTimeInMillis()));
        double julian = DateConverter.toJulianDate(cal);
        System.out.println("Julian Date = " + julian);
        Calendar convertedJulianDate = DateConverter.toGregorianDate(julian, TimeZone.getDefault());
        System.out.println("Converted Julian Date: " + new Date(convertedJulianDate.getTimeInMillis()) + "\t(Timezone = " + convertedJulianDate.getTimeZone().getDisplayName() + ")");        
        String iso8601 = DateConverter.toISO8601(convertedJulianDate);
        System.out.println("ISO8601 Date = " + iso8601);
        Calendar converted8601Date = DateConverter.toDate(iso8601);
        System.out.println("Converted ISO8601 Date: " +  new Date(converted8601Date.getTimeInMillis()) + "\t(Timezone = " + converted8601Date.getTimeZone().getDisplayName() + ")");
        
	}
}
