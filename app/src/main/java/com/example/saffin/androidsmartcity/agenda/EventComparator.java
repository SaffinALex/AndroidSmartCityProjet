package com.example.saffin.androidsmartcity.agenda;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Th0ma on 28/05/2020
 */
public class EventComparator implements Comparator<Event> {

    private DateFormatManipulator mFormatManipulator = new DateFormatManipulator();

    @Override
    public int compare(Event event, Event t1) {
        if(event.equals(t1)){
            return 0;
        }

        ArrayList<Integer> event_Day = mFormatManipulator.stringDateToIntDate(event.getJour());
        ArrayList<Integer> t1_Day = mFormatManipulator.stringDateToIntDate(t1.getJour());

        int number_of_day_in_event_day = event_Day.get(0) + 31 * event_Day.get(1) + 365 * event_Day.get(2);
        int number_of_day_in_t1_day = t1_Day.get(0) + 31 * t1_Day.get(1) + 365 * t1_Day.get(2);

        if(number_of_day_in_event_day < number_of_day_in_t1_day){
            return -1;
        }
        else if(number_of_day_in_event_day > number_of_day_in_t1_day){
            return 1;
        }

        int number_of_minutes_in_event = mFormatManipulator.stringHourToIntTotalMinutes(event.getHeures());
        int number_of_minutes_in_t1 = mFormatManipulator.stringHourToIntTotalMinutes(t1.getHeures());

        if(number_of_minutes_in_event < number_of_minutes_in_t1){
            return -1;
        }
        else if(number_of_minutes_in_event > number_of_minutes_in_t1){
            return 1;
        }

        return 0;
    }
}
