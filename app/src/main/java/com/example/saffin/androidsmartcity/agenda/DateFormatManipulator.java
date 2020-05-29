package com.example.saffin.androidsmartcity.agenda;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Th0ma on 28/05/2020
 */
public class DateFormatManipulator {

    public Date integerToDate(int dayOfMonth, int month, int year, int hour, int minute){

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy/HH/mm");

        if(dayOfMonth < 1 || dayOfMonth > 31){
            dayOfMonth = 1;
        }
        if(month < 1 || month > 12){
            month = 1;
        }
        if(hour < 0 || hour > 23){
            hour = 0;
        }
        if(minute < 0 || minute > 59){
            minute = 0;
        }

        String month_str;
        String day_str;
        String hour_str;
        String minute_str;

        if(month < 10){
            month_str = "0" + Integer.toString(month);
        }else{
            month_str = Integer.toString(month);
        }

        if(dayOfMonth < 10){
            day_str = "0" + Integer.toString(dayOfMonth);
        }else{
            day_str = Integer.toString(dayOfMonth);
        }

        if(hour < 10){
            hour_str = "0" + Integer.toString(hour);
        }else{
            hour_str = Integer.toString(hour);
        }

        if(minute < 10){
            minute_str = "0" + Integer.toString(minute);
        }else{
            minute_str = Integer.toString(minute);
        }

        String parcelable = month_str + "/" + day_str + "/" + Integer.toString(year) + "/" + hour_str + "/" + minute_str;
        Date dae = new Date();
        try {
            dae = formatter.parse(parcelable);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return dae;
    }

    public ArrayList<Integer> stringDateToIntDate(String date_str){
        ArrayList<Integer> results = new ArrayList<>();

        System.out.println(date_str);

        String day_str = date_str.substring(0,date_str.indexOf('/'));

        String month_str = date_str.substring(date_str.indexOf('/')+1,date_str.lastIndexOf('/'));

        String year_str = date_str.substring(date_str.lastIndexOf('/')+1);

        int day = Integer.parseInt(day_str);
        int month = Integer.parseInt(month_str);
        int year = Integer.parseInt(year_str);

        results.add(day);
        results.add(month);
        results.add(year);

        return results;
    }

    public String stringHoursFromDate(Date d){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        return Integer.toString(hours) + ":" + Integer.toString(minutes);
    }

    public int stringHourToIntTotalMinutes(String hour_str){
        int hour = Integer.parseInt(hour_str.substring(0,hour_str.indexOf(':')));
        return Integer.parseInt(hour_str.substring(hour_str.indexOf(':')+1)) + 60 * hour;
    }

    public String dateToStringDay(Date d){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        month++;

        return Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(year);
    }
}
