package chi.nesttestandroid.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nestapi.lib.API.Listener;
import com.nestapi.lib.API.NestAPI;
import com.nestapi.lib.API.Thermostat;

import chi.nesttestandroid.R;
import chi.nesttestandroid.ui.widget.CustomSeekBar;

/**
 * Created by macbook on 18.12.15.
 */
public class ThermostatActivity extends BaseActivity implements Listener.ThermostatListener, SeekBar.OnSeekBarChangeListener, NestAPI.CompletionListener {

    private NestAPI mNestAPI;
    private Thermostat mThermostat;
    private Listener mListener;

    private CustomSeekBar mCurrentSeekBar, mTargetSeekBar;

    private TextView mThermostatCurrentTempTextView;
    private TextView mThermostatTargetTempTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thermostat_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentSeekBar = (CustomSeekBar) findViewById(R.id.thermostat_current_sb);
        mCurrentSeekBar.setMax(40);
        mCurrentSeekBar.setEnabled(false);

        mTargetSeekBar = (CustomSeekBar) findViewById(R.id.thermostat_target_sb);
        mTargetSeekBar.setOnSeekBarChangeListener(this);
        mTargetSeekBar.setMax(30);
        mTargetSeekBar.setEnabled(true);

        mThermostatCurrentTempTextView = (TextView) findViewById(R.id.current_temp);
        mThermostatTargetTempTextView = (TextView) findViewById(R.id.target_temp);

        mNestAPI = NestAPI.getInstance();

        mListener = new Listener.Builder()
                .setThermostatListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mNestAPI.addUpdateListener(mListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mNestAPI.removeUpdateListener(mListener);
    }


    @Override
    public void onThermostatUpdated(@NonNull Thermostat thermostat) {
        mThermostat = thermostat;
        updateThermostatViews();
    }

    private void updateThermostatViews() {
        mCurrentSeekBar.setProgress((int) mThermostat.getAmbientTemperatureC());
        mThermostatCurrentTempTextView.setText(String.valueOf(mThermostat.getAmbientTemperatureC()));

        mTargetSeekBar.setProgress((int) mThermostat.getTargetTemperatureC());
        mThermostatTargetTempTextView.setText(String.valueOf(mThermostat.getTargetTemperatureC()));

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mNestAPI.setTargetTemperatureC(mThermostat.getDeviceID(), (long) seekBar.getProgress(), this);
        mThermostatTargetTempTextView.setText(String.valueOf(seekBar.getProgress()));
    }

    @Override
    public void onComplete() {
//        Toast.makeText(this, "Done!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(int errorCode) {
//        Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
    }
}
