package com.kset.jobfair.ianic.jobfairpoll.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.kset.jobfair.ianic.jobfairpoll.R;

/**
 * A utility class which offers methods which help with user interaction; e.g. dialog boxes.
 *
 * @author Ivan Anić
 * @version 1.0
 */
public class MessagesUtil {

    /*
        Creates a dialog with a single confirm button and shows it to the user.
     */
    public static void createDialogExportFinished(Context context, int alert, int message) {
        new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(alert))
                .setMessage(context.getResources().getString(message))
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.ok), null)
                .create().show();
    }
}
