package rlembo.com.vr_tourism;

import android.app.ProgressDialog;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;

/**
 * Created by EM612865 on 20/03/2018.
 */

public class VrRequestHttp {

    public void getVideo(int idTag, JsonHttpResponseHandler jsonHttpResponse) throws JSONException {
        RequestParams parametres = new RequestParams();
        parametres.put("idTag", idTag);

        // On Effectue une requete GET vers la page qui nous renvoi le lien de la vidéo.
        VrBackendRestClient.get("read.php", parametres, jsonHttpResponse);

        //VrBackendRestClient.get("v2/5ab9627b2c0000810f1864b5", parametres, jsonHttpResponse);
    }
}
