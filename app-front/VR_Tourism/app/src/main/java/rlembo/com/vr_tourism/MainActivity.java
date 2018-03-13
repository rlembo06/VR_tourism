package rlembo.com.vr_tourism;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * TUTO :
 * https://www.journaldev.com/13629/okhttp-android-example-tutorial
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public String url = "https://reqres.in/api/users/2";

    Button syncGET, asyncGET;
    TextView txtString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        syncGET = findViewById(R.id.syncGET);
        asyncGET = findViewById(R.id.asyncGET);

        syncGET.setOnClickListener(this);
        asyncGET.setOnClickListener(this);

        txtString = findViewById(R.id.txtString);
    }

    public void run(String url) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject json = new JSONObject(myResponse);
                            txtString.setText(
                                    "First Name: "+json.getJSONObject("data").getString("first_name") + "\n" +
                                    "Last Name: " + json.getJSONObject("data").getString("last_name")
                            );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.syncGET:
                OkHttpHandler okHttpHandler = new OkHttpHandler();
                okHttpHandler.execute(url);

            case R.id.asyncGET:
                try {
                    run(url);
                }
                catch (IOException e) { e.printStackTrace(); }

            break;
        }
    }

    public class OkHttpHandler extends AsyncTask<String, Void, String> {

        OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String... params) {

            Request.Builder builder = new Request.Builder();
            builder.url(params[0]);
            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            }
            catch (Exception e) { e.printStackTrace(); }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            txtString.setText(s);
        }
    }

}
