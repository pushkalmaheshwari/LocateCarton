package localhost.locatecarton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.IOException;

public class LocationActivity extends AppCompatActivity {

    public static final String ZxingActivity = "com.google.zxing.client.android.SCAN";
    public static final String ApplicationPreferences = "ApplicationPreferences";
    public static final String EmptyString ="";

    private String TAG = LocationActivity.class.getName();
    final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);


        final EditText location_entry = (EditText) findViewById(R.id.id_location_entry);
        final Button submit_button = (Button) findViewById(R.id.id_submit_button);
        final ImageButton scan_button= (ImageButton) findViewById(R.id.id_scan_button);

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
                String locationBarcode = location_entry.getText().toString();
                Global.LocationBarcode = locationBarcode;
                if (locationBarcode.isEmpty() || locationBarcode.matches(EmptyString)) {
                    Global.ShowScreen(R.string.NullLocationError, context);
                } else {
                    SharedPreferences settings = getSharedPreferences(ApplicationPreferences, 0);
                    Global.wmServer = settings.getString("wm_server", "0");
                    new ValidateLocationTask().execute(locationBarcode);
                }
            }
        });
    }


    private class ValidateLocationTask extends AsyncTask<String, Void, ServerResponse> {
        @Override
        protected ServerResponse doInBackground(String... urls) {
            String requestURL = "http://"+ Global.wmServer  + "/api" + "/Location/"+Global.LocationBarcode;
            // params comes from the execute() call: params[0] is the url.

            ServerResponse sr = new ServerResponse();
            try {
                sr = ValidateLocation(requestURL);
            } catch (Exception e) {
                Log.e(TAG, "Unable to retrieve web page. URL may be invalid. URL Hit was: " + requestURL);
            }
            return sr;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ServerResponse ServerResponse) {
            if (ServerResponse.getError() == 0 ) {
                startAcceptCartonActivity();
            } else {
                Global.ShowScreen(R.string.invalidLocation, context);
            }
        }
    }


    public void startAcceptCartonActivity()
    {
        Intent CartonIntent = new Intent( LocationActivity.this, CartonActivity.class);
        startActivity(CartonIntent);
    }

    public ServerResponse ValidateLocation(String requestURL) throws IOException {
             return new HttpRequestHelper(requestURL).MakeRequest();
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
            Intent intent = new Intent(LocationActivity.this, localhost.locatecarton.SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
