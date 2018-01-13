package com.reserveat.reserveat.common.dbObjects;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.reserveat.reserveat.R;
import com.reserveat.reserveat.common.utils.DBUtils;

public class NotificationRequestHolder extends RecyclerView.ViewHolder {

    private TextView description;
    private SwitchCompat isActiveSwitch;

    NotificationRequestHolder(View itemView) {
        super(itemView);
        description = itemView.findViewById(R.id.notificationDescription);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view ,getAdapterPosition());
            }
        });
        isActiveSwitch = itemView.findViewById(R.id.isActive);
        isActiveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mClickListener.onSwitchClick(isChecked ,getAdapterPosition());
            }
        });
    }

    public void setDescription(String r){
        description.setText(r);
    }
    public void setIsActive(boolean b){
        isActiveSwitch.setChecked(b);
    }

    private ClickListener mClickListener = new ClickListener() {
        @Override
        public void onItemClick(View view, int position) {

        }

        @Override
        public void onSwitchClick(boolean isChecked, int position) {

        }
    };

    //Interface to send callbacks...
    public interface ClickListener{
        public void onItemClick(View view, int position);
        public void onSwitchClick(boolean isChecked, int position);
    }

    public void setOnClickListener(ClickListener clickListener){
        mClickListener = clickListener;
    }

}
