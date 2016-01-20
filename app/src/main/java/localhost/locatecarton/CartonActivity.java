package localhost.locatecarton;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;

public class CartonActivity extends AppCompatActivity {

    public static final String ZxingActivity = "com.google.zxing.client.android.SCAN";
    public static final String ApplicationPreferences = "ApplicationPreferences";
    public static final String EmptyString ="";

    private String TAG = CartonActivity.class.getName();
    final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carton);


        final EditText carton_entry = (EditText) findViewById(R.id.id_carton_entry);
        final Button submit_button = (Button) findViewById(R.id.id_submit_button);
        final ImageButton scan_button= (ImageButton) findViewById(R.id.id_scan_button);
        final TextView locationbrcd = (TextView) findViewById(R.id.id_location_barcode_label);

        locationbrcd.setText(Global.LocationBarcode);

        scan_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ZxingActivity);
                startActivityForResult(intent, 0);
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cartonBarcode = carton_entry.getText().toString();
                Global.CartonBarcode = cartonBarcode;
                if (cartonBarcode.isEmpty() || cartonBarcode.matches(EmptyString)) {
                    Global.ShowScreen(R.string.NullCartonMessage, context);
                    return;
                } else {
                    new AcceptCartonTask().execute(cartonBarcode);
                }
            }
        });
    }


    private class AcceptCartonTask extends AsyncTask<String, Void, ServerResponse> {
        @Override
        protected ServerResponse doInBackground(String... urls) {
            String requestURL = "http://"+ Global.wmServer  + "/api/"  + "Carton/" +Global.CartonBarcode;
            // params comes from the execute() call: params[0] is the url.

            ServerResponse sr = new ServerResponse();
            try {
                sr = AcceptCarton(requestURL);
            } catch (Exception e) {
                Log.e(TAG, "Unable to retrieve web page. URL may be invalid. URL Hit was: " + requestURL);
            }
            return sr;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ServerResponse ServerResponse) {
            if (ServerResponse.getError() == 0 ) {
                Global.ShowScreen(R.string.CartonAcceptSuccessMessage, context, getString(R.string.Success));
            } else {
                Log.e(TAG, "Error getting the Carton accepted. " + Global.CartonBarcode);
                Global.ShowScreen(R.string.CartonAcceptError, context);
            }
            return;
        }
    }

    public ServerResponse AcceptCarton(String requestURL) throws IOException {
        HttpRequestHelper requestHandler = new  HttpRequestHelper(requestURL);
        requestHandler.composeAcceptCartonRequest();
        return requestHandler.MakeRequest();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(CartonActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
