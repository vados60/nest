package chi.nesttestandroid.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.nestapi.lib.API.AccessToken;

/**
 * Created by macbook on 17.12.15.
 */
public class BaseActivity extends AppCompatActivity {
    protected static final int AUTH_TOKEN_REQUEST_CODE = 101;
    protected final static String ACCESS_TOKEN_KEY = "access_token_key";


    protected void finishWithResult(int result, AccessToken token) {
        final Intent intent = new Intent();
        intent.putExtra(ACCESS_TOKEN_KEY, token);
        setResult(result, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
