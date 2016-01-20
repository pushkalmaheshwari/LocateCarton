package localhost.locatecarton;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by maheshwarip on 10-01-2016.
 */
public class HttpRequestHelper {
    String requestURL;
    private String TAG = HttpRequestHelper.class.getName();

    public HttpRequestHelper(String requestURL) {
       this.requestURL = requestURL;
    }


    public ServerResponse MakeRequest() throws IOException {
        InputStream is = null;
        ServerResponse repsonseClass = new ServerResponse();
        try {
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(TAG, "The response is: " + response);
            repsonseClass  = ParseHttpServerRepsonse(conn.getResponseMessage());

            return repsonseClass;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return repsonseClass;
    }


    public String composeAcceptCartonRequest()
    {
        String retval = null ;
        try {
            final JSONObject payload = new JSONObject();

            payload.put("LocationBarcode", Global.LocationBarcode);
            payload.put("CartonBarcode", Global.CartonBarcode);

            retval = payload.toString();

        } catch (JSONException e) {

            Log.e(TAG , "Error in building composeAcceptCartonRequest payload.");
        }
        return retval;

    }



    public ServerResponse ParseHttpServerRepsonse(String responseString) throws JSONException {
        ServerResponse response = new ServerResponse();
        try {

            JSONObject responseJson = new JSONObject(responseString);
            response.setError(responseJson.getInt("Error"));
            response.setMessage(responseJson.getString("Message"));


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "ParseHttpServerRepsonse: "+responseString);
        }
        return response;
    }

}
