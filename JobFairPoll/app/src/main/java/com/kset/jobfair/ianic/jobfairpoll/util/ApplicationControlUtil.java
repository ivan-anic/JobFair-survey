package com.kset.jobfair.ianic.jobfairpoll.util;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

import com.kset.jobfair.ianic.jobfairpoll.R;

/**
 * A utility class which offers methods which control the application and activity flow.
 *
 * @author Ivan Anić
 * @version 1.0
 */
public class ApplicationControlUtil {
    private static Boolean exit = false;

    /*
        Forces the activity to end and the application to close if the back button is pressed
        two times.
     */
    public static void exitApplication(Activity activity) {
        if (exit) {
            activity.finish();
            System.exit(0);
        } else {
            Toast.makeText(activity, R.string.exit_check,
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(() -> exit = false, 3 * 1000);
        }
    }
}
