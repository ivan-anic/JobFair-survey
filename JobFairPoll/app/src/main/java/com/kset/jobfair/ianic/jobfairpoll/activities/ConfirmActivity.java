package com.kset.jobfair.ianic.jobfairpoll.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import com.kset.jobfair.ianic.jobfairpoll.R;
import com.kset.jobfair.ianic.jobfairpoll.util.ApplicationControlUtil;
import com.kset.jobfair.ianic.jobfairpoll.util.LocalMemoryUtil;
import com.kset.jobfair.ianic.jobfairpoll.util.MessagesUtil;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import static com.kset.jobfair.ianic.jobfairpoll.util.LocalMemoryUtil.getPostDataString;
import static com.kset.jobfair.ianic.jobfairpoll.util.LocalMemoryUtil.readFromMemory;

/**
 * An activity which processes the actions which can occur after the poll is completed; it can
 * either start a new poll or sync the locally stored data with the remote database.
 *
 * @author Ivan Anić
 * @version 1.0
 */
public class ConfirmActivity extends AppCompatActivity {
    public final static String POLL_CREATE_URL = "http://46.101.96.250/polls";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_activity);

        ImageButton button_restart = (ImageButton) findViewById(R.id.button_restart);
        ImageButton button_export = (ImageButton) findViewById(R.id.button_export);

        HashMap map = (HashMap) readFromMemory(this);

        Toast.makeText(this, map.toString(), Toast.LENGTH_LONG);
        Log.d("CONFIRM", map.toString());
        button_restart.setOnClickListener((v) -> {
            Intent toPoll = new Intent(this, PollActivity.class);
            startActivity(toPoll);
            finish();
        });

        button_export.setOnClickListener((v) -> {
            SyncDataWithServer upload = new SyncDataWithServer(this);
            upload.execute(this);
            Log.d("test", "test");
            LocalMemoryUtil.flushFileBuffer(this);
        });
    }

    /*
            Stores the answer data to the remote database.
    */
    public class SyncDataWithServer extends AsyncTask<Context, Void, Boolean> {
        Context context;
        private ProgressDialog dialog;
        int responseCode;

        public SyncDataWithServer(Context context) {
            this.context = context;
        }

        @Override
        public Boolean doInBackground(Context... params) {
            HashMap<Integer, HashMap> tempMap = (HashMap<Integer, HashMap>) readFromMemory(context);

            URL url;
            try {
                url = new URL(POLL_CREATE_URL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                for (HashMap<Integer, Integer> answers : tempMap.values()) {
                    writer.write(getPostDataString(answers));
                }
                writer.flush();
                writer.close();
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage(getResources().getString(R.string.wait));
            dialog.setIndeterminate(true);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(bool);

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                MessagesUtil.createDialogExportFinished(
                        context, R.string.export_alert, R.string.export);
            } else {
                MessagesUtil.createDialogExportFinished(
                        context, R.string.file_error_alert, R.string.file_error);
            }
        }
    }

    @Override
    public void onBackPressed() {
        ApplicationControlUtil.exitApplication(this);
    }
}