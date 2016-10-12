package com.kset.jobfair.ianic.jobfairpoll.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.Toast;

import com.kset.jobfair.ianic.jobfairpoll.R;
import com.kset.jobfair.ianic.jobfairpoll.util.ApplicationControlUtil;

/**
 * The starting activity of the application, offers the option to start a new poll or to sync the
 * locally stored data to the remote database.
 *
 * @author Ivan Anić
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton buttonStart = (ImageButton) findViewById(R.id.button_start);
        ImageButton button_export = (ImageButton) findViewById(R.id.button_exportHome);

        buttonStart.setOnClickListener((v) -> {
            Intent toPoll = new Intent(this, PollActivity.class);
            startActivity(toPoll);
            finish();
        });

        button_export.setOnClickListener((v) -> {
            Intent toPoll = new Intent(this, ConfirmActivity.class);
            startActivity(toPoll);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        ApplicationControlUtil.exitApplication(this);
    }
}
