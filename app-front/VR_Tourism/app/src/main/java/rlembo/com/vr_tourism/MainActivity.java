package rlembo.com.vr_tourism;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;

import be.appfoundry.nfclibrary.utilities.sync.NfcReadUtilityImpl;
import cz.msebera.android.httpclient.Header;

/**
 * TUTO :
 * https://www.journaldev.com/13629/okhttp-android-example-tutorial
 * http://sberfini.developpez.com/tutoriaux/android/nfc/
 * http://miageprojet2.unice.fr/User:EdouardAmosse/MBDS_-_CASABLANCA_2014-2015/Lecture_et_Ecriture_de_tags_NFC_avec_Android
 * https://code.tutsplus.com/tutorials/reading-nfc-tags-with-android--mobile-17278o
 * https://android.jlelse.eu/create-a-nfc-reader-application-for-android-74cf24f38a6f
 */

/**
 * @auteur : Lembo Romain & El Karmoudi Mohamed
 *
 * Classe principal de l'application.
 */
public class MainActivity extends AppCompatActivity {
    private PendingIntent pendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mTechLists;
    private NfcAdapter mNfcAdapter;
    private Gson gson = new Gson();

    //Button syncGET, asyncGET;
    TextView txtString;

    /**
     * @author Lembo Romain
     * Initialisation et démarrage de l'application.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(
                this,
                0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0
        );
        mIntentFilters = new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)};
        mTechLists = new String[][]{ new String[]{Ndef.class.getName()}, new String[]{NdefFormatable.class.getName()} };

        txtString = findViewById(R.id.txtString);
    }

    /**
     * @author Lembo Romain
     * Application relancé. Exécute le scan du Tag NFC
     */
    public void onResume() {
        super.onResume();

        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(
                    this,
                    pendingIntent,
                    mIntentFilters,
                    mTechLists
            );
        }
    }

    /**
     * @author Lembo Romain
     * Application en écoute, en attente de scan du tag NFC.
     */
    public void onPause(){
        super.onPause();

        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    /**
     * @author Lembo Romain
     * Lecture du Tag NFC.
     * Appelle de la méthode qui récupère la videos sur Youtube auprès du back-end.
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        for (String tagID : new NfcReadUtilityImpl().readFromTagWithMap(intent).values()) {
            Toast.makeText(this, tagID, Toast.LENGTH_SHORT).show();

            String[] parts = tagID.split("en");
            String id = parts[1];

            txtString.setText(id);
            try{
                this.chargerVideo(Integer.parseInt(id));
            }catch (NumberFormatException err){
                System.out.println("Impossible de convertir l'id");
            }
        }
    }

    /**
     * @author El karmourdi Mohamed
     * Méthode permettant de récupérer une vidéo Youtube depuis le back-end.
     * @param idVideo
     */
    public void chargerVideo(int idVideo){
        try
        {
            // Loader
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Chargement");
            progress.setMessage("Veuillez patienter on recherche la vidéo");
            progress.setCancelable(false);

            // On affiche le loader.
            progress.show();

            // On init une requete.
            VrRequestHttp request = new VrRequestHttp();

            // On lance la requete.
            request.getVideo(idVideo, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject object) {
                    try {
                        // On cache le loader
                        progress.dismiss();

                        // Le lien vidéo existe.
                        if(object.has("lien_video")){
                            // Affichage de la video
                            txtString.setText(object.getString("lien_video"));

                            // On ouvre le lien de la vidéo avec l'application youtube.
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(object.getString("lien_video")));
                            startActivity(intent);
                        }else if(object.has("message")){
                            // Aucune vidéo pour l'id X
                            txtString.setText(object.getString("message"));
                        }else{
                            txtString.setText("La requete ne renvoi pas le lien de la vidéo");
                        }
                    }catch(JSONException erreur){
                        txtString.setText("Erreur" + erreur);
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray object){}
                @Override
                public void onFailure(int statusCode, Header[] headers, String t, Throwable throwable) {
                    // On cache le loader
                    progress.dismiss();

                    txtString.setText("Erreur lancement de la requete :" + t);
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    // On cache le loader
                    progress.dismiss();

                    txtString.setText("Erreur lancement de la requete : " + errorResponse);
                }
            });
        }
        catch(JSONException erreur)
        {
            txtString.setText("Erreur lancement de la requete : " + erreur);
        }
    }
}
