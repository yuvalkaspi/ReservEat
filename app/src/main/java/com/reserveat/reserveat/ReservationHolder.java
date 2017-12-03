package com.reserveat.reserveat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class ReservationHolder extends RecyclerView.ViewHolder {

    private final TextView restaurant;
    private final TextView branch;
    private final TextView date;
    private final TextView hour;
    private final TextView numOfPeople;

    ReservationHolder(View itemView) {
        super(itemView);
        restaurant = itemView.findViewById(R.id.restaurantHolder);
        branch = itemView.findViewById(R.id.branchHolder);
        date = itemView.findViewById(R.id.dateHolder);
        hour = itemView.findViewById(R.id.hourHolder);
        numOfPeople = itemView.findViewById(R.id.numOfPeopleHolder);
    }

    void setRestaurant(String r){
        restaurant.setText(r);
    }

    void setBranch(String b){
        branch.setText(b);
    }

    void setDate(String d){
        date.setText(d);
    }

    void setHour(String h){
        hour.setText(h);
    }

    void setNumOfPeople(int n){
        numOfPeople.setText(String.valueOf(n));
    }

}
