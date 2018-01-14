package com.reserveat.reserveat.common.dialogFragment.contentDialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.reserveat.reserveat.R;
import com.reserveat.reserveat.common.utils.DBUtils;
import com.reserveat.reserveat.common.utils.DialogUtils;

import java.util.HashMap;
import java.util.Map;

public class NotificationRequestListDialog extends ContentBaseDialog {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View root = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(contentId, null);
        final Button detailsButton = root.findViewById(R.id.details_Button);
        final Button removeButton = root.findViewById(R.id.remove_Button);

        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationRequestDetailsDialog newFragment = new NotificationRequestDetailsDialog();
                DialogUtils.initContentDialog(newFragment, R.string.details, R.layout.notification_request_details_dialog,key);
                newFragment.show(getFragmentManager(), "NotificationRequestDetailsDialog");
                dialog.dismiss();
            }
        });
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/users/" + DBUtils.getCurrentUser().getUid() + "/notificationRequests/" + key, null);
                childUpdates.put("/notificationRequests/" + key, null);
                removeClick(childUpdates);
            }
        });


        builder.setTitle(titleStringId)
                .setView(root);

        dialog = builder.create();
        return dialog;
    }

}
