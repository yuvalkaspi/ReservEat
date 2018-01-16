package com.reserveat.reserveat.common.dbObjects;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.reserveat.reserveat.R;

public class ReservationHolder extends RecyclerView.ViewHolder {

    private final TextView restaurant;
    private final TextView branch;
    private final TextView date;
    private final TextView hour;
    private final TextView numOfPeople;
    private final ImageView boilHotness;
    private final ImageView hotHotness;
    private final ImageView warmHotness;

    ReservationHolder(View itemView) {
        super(itemView);
        restaurant = itemView.findViewById(R.id.restaurantHolder);
        branch = itemView.findViewById(R.id.branchHolder);
        date = itemView.findViewById(R.id.dateHolder);
        hour = itemView.findViewById(R.id.hourHolder);
        numOfPeople = itemView.findViewById(R.id.numOfPeopleHolder);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view ,getAdapterPosition());
            }
        });
        boilHotness = itemView.findViewById(R.id.boil);
        hotHotness = itemView.findViewById(R.id.hot);
        warmHotness = itemView.findViewById(R.id.warm);

    }

    public void setRestaurant(String r){
        restaurant.setText(r);
    }
    //String getRestaurant(){ return restaurant.getText().toString(); }

    public void setBranch(String b){
        branch.setText(b);
    }

    public void setDate(String d){
        date.setText(d);
    }

    public void setHour(String h){
        hour.setText(h);
    }

    public void setNumOfPeople(int n){
        numOfPeople.setText(String.valueOf(n));
    }

    public void setHotness(int hotness){
        if(hotness >= 9){
            boilHotness.setVisibility(View.VISIBLE);
            hotHotness.setVisibility(View.GONE);
            warmHotness.setVisibility(View.GONE);
        } else if( hotness == 8) {
            hotHotness.setVisibility(View.VISIBLE);
            boilHotness.setVisibility(View.GONE);
            warmHotness.setVisibility(View.GONE);
        } else if( hotness == 7){
            warmHotness.setVisibility(View.VISIBLE);
            hotHotness.setVisibility(View.GONE);
            boilHotness.setVisibility(View.GONE);
        } else{
            warmHotness.setVisibility(View.GONE);
            hotHotness.setVisibility(View.GONE);
            boilHotness.setVisibility(View.GONE);
        }

    }

    private ClickListener mClickListener = new ClickListener() {
        @Override
        public void onItemClick(View view, int position) {

        }
    };

    //Interface to send callbacks...
    public interface ClickListener{
        public void onItemClick(View view, int position);
    }

    public void setOnClickListener(ClickListener clickListener){
        mClickListener = clickListener;
    }

}
