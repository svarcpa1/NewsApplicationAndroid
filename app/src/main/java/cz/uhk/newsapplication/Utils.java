package cz.uhk.newsapplication;

import java.util.Calendar;
import java.util.Locale;

public class Utils {

    public static String getCountry(){
        Locale locale = Locale.getDefault();
        String country = String.valueOf(locale.getCountry());
        return country.toLowerCase();
    }

    //news api allow max 1 month old search results
    public static String getDateBeforeMonth(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        return cal.toString();
    }
}
