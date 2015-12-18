package chi.nesttestandroid.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nestapi.lib.API.AccessToken;
import com.nestapi.lib.API.Listener;
import com.nestapi.lib.API.NestAPI;
import com.nestapi.lib.API.Thermostat;

import chi.nesttestandroid.R;
import chi.nesttestandroid.utils.IntentUtils;
import chi.nesttestandroid.utils.Settings;

public class MainActivity extends BaseActivity implements NestAPI.AuthenticationListener, Listener.ThermostatListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String THERMOSTAT_KEY = "thermostat_key";
    private static final String STRUCTURE_KEY = "structure_key";

    private AccessToken mToken;
    private Listener mUpdateListener;
    private NestAPI mNestAPI;
    private ProgressDialog mProgressDialog;
    private TextView mThermostatTextView, mThermostatDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mThermostatTextView = (TextView) findViewById(R.id.thermostat);
        mThermostatDetails = (TextView) findViewById(R.id.thermostat_details);
        mThermostatTextView.setOnClickListener(this);
        mThermostatDetails.setOnClickListener(this);

        mProgressDialog = new ProgressDialog(this);

        mToken = Settings.loadAuthToken(this);
        mNestAPI = NestAPI.getInstance();
        if (mToken != null) {
            authenticate(mToken);
        } else {
            obtainAccessToken();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AUTH_TOKEN_REQUEST_CODE:
                AccessToken accessToken = (AccessToken) data.getParcelableExtra(ACCESS_TOKEN_KEY);
                Settings.saveAuthToken(this, accessToken);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Settings.logout(this);
            obtainAccessToken();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void authenticate(AccessToken token) {
        Log.v(TAG, "Authenticating...");
        mProgressDialog.setTitle("Authenticating...");
        mProgressDialog.show();
        NestAPI.getInstance().authenticate(token, this);
    }

    private void fetchData() {
        Log.v(TAG, "Fetching data...");

        mUpdateListener = new Listener.Builder()
                .setThermostatListener(this)
                .build();

        mNestAPI.addUpdateListener(mUpdateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mNestAPI.removeUpdateListener(mUpdateListener);
    }

    @Override
    public void onAuthenticationSuccess() {
        Log.v(TAG, "Authentication succeeded.");
        mProgressDialog.setTitle("Authentication succeeded");
        mProgressDialog.dismiss();
        fetchData();
    }

    @Override
    public void onAuthenticationFailure(int errorCode) {
        Log.v(TAG, "Authentication failed with error: " + errorCode);
    }

    private void obtainAccessToken() {
        startActivityForResult(IntentUtils.getLoginActivityIntent(this), AUTH_TOKEN_REQUEST_CODE);
    }

    @Override
    public void onThermostatUpdated(@NonNull Thermostat thermostat) {
        mThermostatDetails.setText("Current temperature : " + thermostat.getAmbientTemperatureC() + ",\n"
                + " Target temperature : " + thermostat.getTargetTemperatureC());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.thermostat:
            case R.id.thermostat_details:
                startActivity(IntentUtils.getThermostatActivityIntent(this));
                break;
        }
    }
}
