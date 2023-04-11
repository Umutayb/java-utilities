package utils;

import java.util.Calendar;


public class DateUtilities {

    public static Calendar date;
    /**
     * Returns the current date and time as a Calendar object.
     *
     * @return The current date and time as a Calendar object.
     */
    public Calendar getDate(){return Calendar.getInstance();}

    /**
     * Returns a new Calendar object with the specified unit of time set to the specified value.
     * The unit of time can be one of "year", "month", "day", "hour", "minute", or "second".
     *
     * @param unitType The type of unit to set (year, month, day, hour, minute, or second).
     * @param value    The value to set the unit of time to.
     * @return A new Calendar object with the specified unit of time set to the specified value.
     */
    public Calendar getSetDate(String unitType, Integer value){return setDate(getDate(), unitType, value);}

    /**
     * Sets the specified unit of time in the provided Calendar object to the specified value.
     * The unit of time can be one of "year", "month", "day", "hour", "minute", or "second".
     *
     * @param calendar  The Calendar object to set the date on.
     * @param unitType  The type of unit to set (year, month, day, hour, minute, or second).
     * @param value     The value to set the unit of time to.
     * @return The modified Calendar object with the specified unit of time set to the specified value.
     */
    Calendar setDate(Calendar calendar, String unitType, Integer value){
        switch (unitType.toLowerCase()){
            case "year":
                calendar.set(Calendar.YEAR, value);
                break;

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

            case "second":
                calendar.set(Calendar.SECOND, value);

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
