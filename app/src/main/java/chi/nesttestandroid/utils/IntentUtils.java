package chi.nesttestandroid.utils;

import android.content.Context;
import android.content.Intent;

import chi.nesttestandroid.ui.LoginActivity;
import chi.nesttestandroid.ui.ThermostatActivity;

/**
 * Created by macbook on 17.12.15.
 */
public class IntentUtils {

    /*
     *  Starts Login Activity
    */
    public static Intent getLoginActivityIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    /*
     *  Starts Thermostat Activity
    */
    public static Intent getThermostatActivityIntent(Context context) {
        Intent intent = new Intent(context, ThermostatActivity.class);
        return intent;
    }


}
