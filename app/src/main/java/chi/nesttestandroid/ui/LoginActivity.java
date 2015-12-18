package chi.nesttestandroid.ui;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.nestapi.lib.API.AccessToken;
import com.nestapi.lib.APIUrls;
import com.nestapi.lib.ClientMetadata;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import chi.nesttestandroid.R;
import chi.nesttestandroid.utils.Constants;

/**
 * Created by macbook on 17.12.15.
 */
public class LoginActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<AccessToken> {

    private WebView mWebView;
    private String mCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.clearCache(true);
        mWebView.setWebViewClient(new Client());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(String.format(Constants.CLIENT_CODE_URL, Constants.CLIENT_ID, Constants.CLIENT_SECRET));
    }

    private String parseCodeQuery(String urlStr) {
        try {
            List<NameValuePair> params = URLEncodedUtils.parse(new URI(urlStr), "UTF-8");
            for (NameValuePair pair : params) {
                if (pair.getName().equals("code")) {
                    return pair.getValue();
                }
            }
            return null;
        } catch (URISyntaxException excep) {
            return null;
        }
    }

    @Override
    public Loader<AccessToken> onCreateLoader(int i, Bundle bundle) {
        return new ObtainAccessTokenTask(this, mCode, Constants.CLIENT_ID, Constants.CLIENT_SECRET);
    }

    @Override
    public void onLoadFinished(Loader<AccessToken> loader, AccessToken accessToken) {
        if(accessToken != null) {
            finishWithResult(RESULT_OK, accessToken);
        } else {
            finishWithResult(RESULT_CANCELED, null);
        }
    }

    @Override
    public void onLoaderReset(Loader<AccessToken> loader) {

    }

    private class Client extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith(Constants.REDIRECT_URL)) {
                mCode = parseCodeQuery(url);
                getLoaderManager().restartLoader(0, null, LoginActivity.this);
            }

            return true;
        }
    }

    private static class ObtainAccessTokenTask extends AsyncTaskLoader<AccessToken> {

        private static final int BUFFER_SIZE = 4096;

        private String mCode, mClientId, mSecret;

        public ObtainAccessTokenTask(Context context, String code, String clientId, String secret) {
            super(context);
            mCode = code;
            mClientId = clientId;
            mSecret = secret;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public AccessToken loadInBackground() {
            try {
                String formattedUrl = String.format(APIUrls.ACCESS_URL, mCode, mClientId, mSecret);
                URL url = new URL(formattedUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                InputStream in = new BufferedInputStream(conn.getInputStream());
                String result = readStream(in);
                JSONObject object = new JSONObject(result);

                return AccessToken.fromJSON(object);
            } catch (JSONException | IOException excep) {
                return null;
            }
        }

        private static String readStream(InputStream stream) throws IOException {
            final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            final byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = stream.read(buffer, 0, buffer.length)) != -1) {
                outStream.write(buffer, 0, read);
            }
            final byte[] data = outStream.toByteArray();
            return new String(data);
        }
    }

}
