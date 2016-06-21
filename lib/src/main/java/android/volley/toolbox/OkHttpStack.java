package android.volley.toolbox;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.android.volley.toolbox.HurlStack;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

/**
 * Created by wengui on 2016/2/19.
 */
public class OkHttpStack extends HurlStack {
    private OkHttpClient mOkHttpClient;

    public OkHttpStack(){
        this(new OkHttpClient());
    }

    public OkHttpStack(OkHttpClient okHttpClient) {
        this.mOkHttpClient = okHttpClient;
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        final OkUrlFactory okUrlFactory = new OkUrlFactory(mOkHttpClient);
        return okUrlFactory.open(url);
    }
}
