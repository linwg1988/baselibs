package android.volley.toolbox;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by wengui on 2016/2/19.
 */
public class UTFStringRequest extends StringRequest {
    public UTFStringRequest(String url, Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    public UTFStringRequest(int method, String url, Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, "utf-8");
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed,
                HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Charset", "UTF-8");
//		headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Connection", "Close");
        return headers;
    }

    @Override
    public RetryPolicy getRetryPolicy() {
        RetryPolicy retryPolicy = new DefaultRetryPolicy(30 * 1000, 3, 1.0f);
        return retryPolicy;
    }
}
