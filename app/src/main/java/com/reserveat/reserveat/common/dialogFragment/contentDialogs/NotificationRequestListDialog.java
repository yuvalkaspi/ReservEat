package com.reserveat.reserveat.common.dialogFragment.contentDialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.reserveat.reserveat.R;
import com.reserveat.reserveat.common.utils.DBUtils;
import com.reserveat.reserveat.common.utils.DialogUtils;

import java.util.HashMap;
import java.util.Map;

public class NotificationRequestListDialog extends NotificationContentDialog {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View root = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.actions_dialog, null);
        Button detailsButton = root.findViewById(R.id.details_button);
        Button removeButton = root.findViewById(R.id.remove_button);

        Button reviewButton = root.findViewById(R.id.review_button);
        Button spamButton = root.findViewById(R.id.spam_button);
        ImageButton closeButton = root.findViewById(R.id.close_button);
        TextView title = root.findViewById(R.id.actions_title);

        reviewButton.setVisibility(View.GONE);
        spamButton.setVisibility(View.GONE);
        title.setText(R.string.notification_request);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationContentDialog newFragment = new NotificationRequestDetailsDialog();
                NotificationContentDialog.initInstance(newFragment, key, notificationRequest);
                newFragment.show(getFragmentManager(), "NotificationRequestDetailsDialog");
                dialog.dismiss();
            }
        });
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.removeClick(dialog, "notificationRequests", key, getActivity());
            }
        });


        builder.setView(root);

        dialog = builder.create();
        return dialog;
    }

}
