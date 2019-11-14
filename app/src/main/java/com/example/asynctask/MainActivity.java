package com.example.asynctask;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Task task = new Task(MainActivity.this);
                task.execute("https://api.myjson.com/bins/qfy5y");
            }
        });
    }


    public static class Task extends AsyncTask<String, Integer, String> {
        private final WeakReference<MainActivity> weakActivity;

        private Task(MainActivity activity) {
            weakActivity = new WeakReference<>(activity);
        }


        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(30000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.connect();
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == httpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = "";
                    StringBuilder stringBuffer = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuffer.append(line);
                    }
                    return stringBuffer.toString();
                }

            } catch (java.io.IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            MainActivity activity = weakActivity.get();

            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String value1 = jsonObject1.optString("name");
                    String value2 = jsonObject1.optString("number");
                    Log.d("json", value1 + ":" + value2);

                    activity.textView.setText(s);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
