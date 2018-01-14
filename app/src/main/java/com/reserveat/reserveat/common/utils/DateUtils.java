package com.reserveat.reserveat.common.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import static com.reserveat.reserveat.common.utils.DateUtils.Day.FRIDAY;
import static com.reserveat.reserveat.common.utils.DateUtils.Day.MONDAY;
import static com.reserveat.reserveat.common.utils.DateUtils.Day.SATURDAY;
import static com.reserveat.reserveat.common.utils.DateUtils.Day.SUNDAY;
import static com.reserveat.reserveat.common.utils.DateUtils.Day.THURSDAY;
import static com.reserveat.reserveat.common.utils.DateUtils.Day.TUESDAY;
import static com.reserveat.reserveat.common.utils.DateUtils.Day.WEDNESDAY;
import static com.reserveat.reserveat.common.utils.DateUtils.TimeOfDay.AFTERNOON;
import static com.reserveat.reserveat.common.utils.DateUtils.TimeOfDay.EVENING;
import static com.reserveat.reserveat.common.utils.DateUtils.TimeOfDay.MORNING;
import static com.reserveat.reserveat.common.utils.DateUtils.TimeOfDay.NIGHT;
import static com.reserveat.reserveat.common.utils.DateUtils.TimeOfDay.NOON;

public class DateUtils {

    public static final String hourFormat = "HH:mm";
    public static final String dateFormatUser = "dd/MM/yyyy";
    public static final String dateFormatDB = "yyyy/MM/dd";
    public static final String fullDateFormatDB = dateFormatDB + " " + hourFormat;
    private static final String TAG = "DateUtils";


    /*
    * Returns true if the given date was passed and false otherwise
    */
    public static boolean isDatePassed(String dateInDBFormat){

        boolean isPassed = false;
        Date expiredDate = stringToDate(dateInDBFormat, fullDateFormatDB);
        if (expiredDate != null && (new Date()).after(expiredDate)) {
            isPassed = true;
        }

        return isPassed;
    }

    /*
     Receives a string represented a date and returns it's Date object
     */
    private static Date stringToDate(String aDate, String aFormat) {

        SimpleDateFormat format = new SimpleDateFormat(aFormat);
        Date date = null;
        try {
            date = format.parse(aDate);
        } catch (ParseException e) {
            return null;
        }
        return date;
    }


    /* Receives string represents date in format- oldFormat
      Returns  string represents the same date in format- newFormat */
    public static String switchDateFormat(String date, String oldFormat, String newFormat) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(oldFormat, Locale.getDefault());
        Date d = dateFormat.parse(date);
        dateFormat = new SimpleDateFormat(newFormat, Locale.getDefault());
        return dateFormat.format(d);
    }

    /* Receives a string contains date and hour
       Returns array which contains that date and the hour */
    public static List<String> getDate(String dateAndHour){

        int indexOfSpace = dateAndHour.indexOf(" ");
        String date = dateAndHour.substring(0, indexOfSpace);
        String hour = dateAndHour.substring(indexOfSpace + 1);

        List<String> dateAndHourList = new ArrayList<>();
        dateAndHourList.add(date);
        dateAndHourList.add(hour);

        return dateAndHourList;
    }

    public static Day getDaybyDate(Calendar calendar) {

        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                return SUNDAY;

            case Calendar.MONDAY:
                return MONDAY;

            case Calendar.TUESDAY:
                return TUESDAY;

            case Calendar.WEDNESDAY:
                return WEDNESDAY;

            case Calendar.THURSDAY:
                return THURSDAY;

            case Calendar.FRIDAY:
                return FRIDAY;
        }
        return SATURDAY;
    }

//    public static Day convertDayFormat(String dayStr){
//        switch (dayStr){
//            case "SUNDAY":
//                return SUNDAY;
//            case "MONDAY":
//                return MONDAY;
//            case "TUESDAY":
//                return TUESDAY;
//            case "WEDNESDAY":
//                return WEDNESDAY;
//            case "THURSDAY":
//                return THURSDAY;
//            case "FRIDAY":
//                return FRIDAY;
//        }
//        return SATURDAY;
//    }

    public static TimeOfDay getTimeOfDay(String time){

        int hour = Integer.valueOf(time.split(":")[0]);
        if(hour < 12){
            return MORNING;
        } else if(hour <= 15){
            return NOON;
        } else if(hour <= 18){
            return AFTERNOON;
        } else if(hour <= 21){
            return EVENING;
        }
        return NIGHT;
    }


    public enum Day {
        SUNDAY(1), MONDAY(2), TUESDAY(3), WEDNESDAY(4),
        THURSDAY(5), FRIDAY(6), SATURDAY(7);

        int index;
        Day(int i) {
            index = i;
        }
        public int getIndex() {
            return index;
        }
    }

    public enum TimeOfDay {
        MORNING(1), NOON(2), AFTERNOON(3), EVENING(4),
        NIGHT(5);

        int index;
        TimeOfDay(int i) {
            index = i;
        }
        public int getIndex() {
            return index;
        }
    }
}
