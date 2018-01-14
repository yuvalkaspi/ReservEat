package com.reserveat.reserveat.common.utils;


import com.reserveat.reserveat.common.dbObjects.Reservation;
import com.reserveat.reserveat.common.dbObjects.ReservationHolder;

import java.text.ParseException;



public class ReservationUtils {

    public static void myPopulateViewHolder(ReservationHolder viewHolder, Reservation model) throws ParseException {
        String date = model.getDate();
        int indexOfSpace = date.indexOf(" ");
        String dateOldFormat = date.substring(0, indexOfSpace);
        String hour = date.substring(indexOfSpace + 1);
        String dateNewFormat = DateUtils.switchDateFormat(dateOldFormat, DateUtils.dateFormatDB, DateUtils.dateFormatUser);
        viewHolder.setRestaurant(model.getRestaurant());
        viewHolder.setBranch(model.getBranch());
        viewHolder.setDate(dateNewFormat);
        viewHolder.setHour(hour);
        viewHolder.setNumOfPeople(model.getNumOfPeople());
    }

}

