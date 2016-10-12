package com.kset.jobfair.ianic.jobfairpoll.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kset.jobfair.ianic.jobfairpoll.R;
import com.kset.jobfair.ianic.jobfairpoll.objects.Question;
import com.kset.jobfair.ianic.jobfairpoll.util.ApplicationControlUtil;
import com.kset.jobfair.ianic.jobfairpoll.util.LocalMemoryUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

/**
 * An activity which processes the question and answer handling; retrieves the question data if
 * needed, displays the questions, and records the answers. Afterwards, the
 * {@linkplain ConfirmActivity} is launched.
 *
 * @author Ivan Anić
 * @version 1.0
 */
public class PollActivity extends AppCompatActivity {

    private static String QUESTIONS_FETCH_URL = "http://46.101.96.250/questions";

    private TextView textView;
    private String[] questions;
    private HashMap<Integer, Question> questionMap;
    private int[] answerIndexes;
    private int questionsAnswered;
    private HashMap<Integer, Integer> answer = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        //if questions aren't already retrieved from the remote database, retrieve them
        if (!LocalMemoryUtil.QUESTIONS_FETCHED)
            try {
                new HttpRequestDatabaseQuestion().execute().get();
                LocalMemoryUtil.QUESTIONS_FETCHED = true;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        else questionMap = LocalMemoryUtil.questionStorage;

        questions = toStringQuestionArray(new TreeMap<>(questionMap));

        textView = (TextView) findViewById(R.id.textView_question);

        setUpListeners();

        setNewQuestion();
    }

    /**
     * Transforms the question data from the {@linkplain Question} form, to a usable string array.
     *
     * @param questionHashMap - the question data
     * @return - the desired string array
     */
    private String[] toStringQuestionArray(TreeMap<Integer, Question> questionHashMap) {
        int size = questionHashMap.size();
        answerIndexes = new int[size];
        String[] ret = new String[size];

        int i = 0;
        for (Question q : questionHashMap.values()) {
            ret[i] = q.getText();
            answerIndexes[i] = q.getId();
            i++;
        }
        return ret;
    }

    /*
        Fetches the question data from the remote database.
     */
    public class HttpRequestDatabaseQuestion extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            JSONArray jsonArray = new JSONArray();
            try {
                URL url = new URL(QUESTIONS_FETCH_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                jsonArray = new JSONArray(in.readLine());
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            List<JSONObject> jsonQuestions = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject object = jsonArray.getJSONObject(i);
                    jsonQuestions.add(object);
                } catch (JSONException e) {
                    Toast.makeText(PollActivity.this, R.string.backend_error,
                            Toast.LENGTH_LONG);
                    return null;
                }
            }

            questionMap = new HashMap<>();
            for (JSONObject obj : jsonQuestions) {
                try {
                    questionMap.put(obj.getInt("id"),
                            new Question(obj.getInt("id"),
                                    obj.getString("text"),
                                    obj.getString("created_at"),
                                    obj.getString("updated_at")));
                } catch (JSONException e) {
                    Toast.makeText(PollActivity.this, R.string.backend_error,
                            Toast.LENGTH_LONG);
                    return null;
                }
            }

            LocalMemoryUtil.QUESTIONS_FETCHED = true;
            LocalMemoryUtil.questionStorage = questionMap;
            return null;
        }
    }

    private void setUpListeners() {
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        ImageButton imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
        ImageButton imageButton3 = (ImageButton) findViewById(R.id.imageButton3);
        ImageButton imageButton4 = (ImageButton) findViewById(R.id.imageButton4);
        ImageButton imageButton5 = (ImageButton) findViewById(R.id.imageButton5);

        imageButton.setOnClickListener(onClickListener);
        imageButton2.setOnClickListener(onClickListener);
        imageButton3.setOnClickListener(onClickListener);
        imageButton4.setOnClickListener(onClickListener);
        imageButton5.setOnClickListener(onClickListener);

    }

    private void launchConfirm() {
        LocalMemoryUtil.writeToMemory(answer, PollActivity.this);
        Intent toConfirm = new Intent(PollActivity.this, ConfirmActivity.class);
        startActivity(toConfirm);
        finish();
    }

    private void setNewQuestion() {
        textView.setText(questions[questionsAnswered++]);
    }


    /*
        A unique listener which has the same function, and depends solely on the pressed button id.
     */
    public View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int answerId = 0;
            switch (v.getId()) {
                case R.id.imageButton:
                    answerId = 0;
                    break;
                case R.id.imageButton2:
                    answerId = 1;
                    break;
                case R.id.imageButton3:
                    answerId = 2;
                    break;
                case R.id.imageButton4:
                    answerId = 3;
                    break;
                case R.id.imageButton5:
                    answerId = 4;
                    break;
            }

            answer.put(answerIndexes[questionsAnswered - 1], answerId);

            if (questionsAnswered < questions.length) {
                setNewQuestion();
            } else {
                launchConfirm();
            }
        }
    };


    @Override
    public void onBackPressed() {
        ApplicationControlUtil.exitApplication(this);
    }
}

