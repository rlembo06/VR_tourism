package rlembo.com.vr_tourism;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import be.appfoundry.nfclibrary.utilities.sync.NfcReadUtilityImpl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * TUTO :
 * https://www.journaldev.com/13629/okhttp-android-example-tutorial
 * http://sberfini.developpez.com/tutoriaux/android/nfc/
 * http://miageprojet2.unice.fr/User:EdouardAmosse/MBDS_-_CASABLANCA_2014-2015/Lecture_et_Ecriture_de_tags_NFC_avec_Android
 * https://code.tutsplus.com/tutorials/reading-nfc-tags-with-android--mobile-17278
 * https://android.jlelse.eu/create-a-nfc-reader-application-for-android-74cf24f38a6f
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public String url = "https://reqres.in/api/users/2";
    private PendingIntent pendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mTechLists;
    private NfcAdapter mNfcAdapter;

    Button syncGET, asyncGET;
    TextView txtString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mIntentFilters = new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)};
        mTechLists = new String[][]{ new String[]{Ndef.class.getName()}, new String[]{NdefFormatable.class.getName()} };

        syncGET = findViewById(R.id.syncGET);
        asyncGET = findViewById(R.id.asyncGET);

        syncGET.setOnClickListener(this);
        asyncGET.setOnClickListener(this);

        txtString = findViewById(R.id.txtString);
    }

    public void onResume() {
        super.onResume();

        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, mIntentFilters, mTechLists);
        }
    }

    public void onPause(){
        super.onPause();

        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        for (String message : new NfcReadUtilityImpl().readFromTagWithMap(intent).values()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            txtString.setText(message);
        }
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
