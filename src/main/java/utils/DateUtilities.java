package utils;

import java.util.Calendar;


public class DateUtilities {

    public static Calendar date;
    public Calendar getDate(){return Calendar.getInstance();}

    public Calendar getSetDate(String unitType, Integer value){return setDate(getDate(), unitType, value);}

    /**
     * Create unique a calendar
     *
     * @param calendar calendar
     * @param unitType unit type
     * @param value date value
     * @return returns the specified calendar
     */
    Calendar setDate(Calendar calendar, String unitType, Integer value){
        switch (unitType.toLowerCase()){
            case "day":
                calendar.set(Calendar.DAY_OF_MONTH, value);
                break;

            case "hour":
                calendar.set(Calendar.HOUR_OF_DAY, value);
                break;

            case "minute":
                calendar.set(Calendar.MINUTE, value);
                break;

            case "month":
                calendar.set(Calendar.MONTH, value);
                break;

            default:
                calendar.set(Calendar.MONTH,4);
                calendar.set(Calendar.DAY_OF_MONTH,1);
                calendar.set(Calendar.HOUR_OF_DAY, 15);
                calendar.set(Calendar.MINUTE, 30);
                break;
        }
        return calendar;
    }
}
