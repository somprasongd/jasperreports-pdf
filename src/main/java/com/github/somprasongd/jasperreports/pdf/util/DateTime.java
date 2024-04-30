package com.github.somprasongd.jasperreports.pdf.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTime {

    public static Date parseDate(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            // Handle parsing exception
            System.err.println("Error parsing date string: " + e.getMessage());
            return null;
        }
    }

    public static Date parseTime(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ssX", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            // Handle parsing exception
            System.err.println("Error parsing time string: " + e.getMessage());
            return null;
        }
    }

    public static Date parseRFC3339Date(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            // Handle parsing exception
            System.err.println("Error parsing RFC3339 string: " + e.getMessage());
            return null;
        }
    }
}
