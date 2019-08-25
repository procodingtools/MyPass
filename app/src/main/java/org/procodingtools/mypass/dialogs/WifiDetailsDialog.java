package org.procodingtools.mypass.dialogs;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import org.procodingtools.mypass.R;
import org.procodingtools.mypass.activities.CurrentWifiActivity;
import org.procodingtools.mypass.adapters.recycler_views.WifiRecyclerAdapter;

public class WifiDetailsDialog {
    private Context context;
    private AlertDialog dialog;

    public WifiDetailsDialog(Context context) {
        this.context = context;
    }

    //pass null to adapter param to exit app (used only when user wants to get the current wifi password)
    public void show(String title, final String key, @Nullable final WifiRecyclerAdapter adapter) {
        String negativeText = "Copy & exit";
        String positiveText = "See all";
        if (adapter == null) {
            negativeText = "Copy";
            positiveText = "Ok";
        }

        dialog = new AlertDialog.Builder(context, R.style.myDialog).create();
        dialog.setTitle(title);
        dialog.setMessage("password: " + key);
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, negativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //copy password to clipboard
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("wifi", key);
                clipboard.setPrimaryClip(clip);

                //extit app or dismiss dialog
                if (adapter == null)
                    CurrentWifiActivity.ACTIVITY.finish();
                else
                    dialog.dismiss();
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public void dismiss() {
        dialog.dismiss();
    }
}
