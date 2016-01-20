package localhost.locatecarton;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SettingsActivity extends AppCompatActivity {
    public static final String ApplicationPreferences = "ApplicationPreferences";
    public static final String EmptyString = "";
    public static final String localHost = "localHost";
    private String TAG = SettingsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final EditText wm_server = (EditText) findViewById(R.id.input_wm_server_id);
        final Button save_button = (Button) findViewById(R.id.save_button);
        final Button check_status = (Button) findViewById(R.id.checkConnectionStatus);

        SharedPreferences settings = getSharedPreferences(ApplicationPreferences, 0);
        wm_server.setText(settings.getString("wm_server", EmptyString));

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistSettings();
                finish();
            }
        });

        check_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistSettings();
                setConnectionStatus();
            }
        });
    }

   void persistSettings()
    {
        final EditText wm_server = (EditText) findViewById(R.id.input_wm_server_id);

        SharedPreferences settings = getSharedPreferences(ApplicationPreferences, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("wm_server", wm_server.getText().toString());
        Global.wmServer = wm_server.getText().toString();
        editor.commit();
    }

    private void setConnectionStatus() {
        try {
            SharedPreferences settings = getSharedPreferences(ApplicationPreferences, 0);
            Global.wmServer = settings.getString("wm_server", "0");
            new checkSoapServerConnectionTask().execute(Global.wmServer);

        } catch (Exception e) {
            Log.i(TAG, "not able make Socket call properly");
            e.printStackTrace();
        }

    }

    private class checkSoapServerConnectionTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                String requestURL = "http://"+ Global.wmServer ;
                return getOnWMServer(requestURL);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            final CheckedTextView WMServerStatus = (CheckedTextView) findViewById(R.id.WMConnectionStatus);
            if (result.startsWith("200")) {
                WMServerStatus.setText(R.string.wm_server_reached);
                WMServerStatus.setChecked(true);
            } else {
                WMServerStatus.setText(R.string.wm_server_not_reached);
                WMServerStatus.setChecked(false);
            }
        }
    }


    private String getOnWMServer(String myurl) throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(TAG, "The response is: " + response);

            return String.valueOf(response);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }


}