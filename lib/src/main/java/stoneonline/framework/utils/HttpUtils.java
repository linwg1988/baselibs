package stoneonline.framework.utils;

import java.util.HashMap;
import java.util.Map;

import android.volley.toolbox.UTFStringRequest;
import android.volley.toolbox.VolleyCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;

import android.volley.toolbox.ResponseAndErrorListener;

/**
 * Author wengui
 */
public class HttpUtils {

    private static HttpUtils util;

	private Object tag = HttpUtils.class.getSimpleName();

    private HttpUtils() {
    }

   public static HttpUtils getUtil() {
        if (util == null) {
            synchronized (HttpUtils.class) {
                if (util == null) {
                    util = new HttpUtils();
                }
            }
        }
        return util;
    }

    public void doGet(String url, ResponseAndErrorListener listener) {
        RequestQueue requestQueue = VolleyCompat.getRequestQueue();
        StringRequest myRequest = new UTFStringRequest(Request.Method.GET, url,
                listener, listener);
        myRequest.setTag(tag);
        requestQueue.add(myRequest);
    }

    public static Map<String,String> createParams(String data){
        final HashMap<String, String> map = new HashMap<>();
        map.put("VerifyCode","12345689712");
        map.put("VerifyKey","123489794651");
        map.put("Data",data);
        map.put("Key","148465313");
        return map;
    }

    public void doPost(String url, final Map<String, String> map,
                       ResponseAndErrorListener rr) {
        doPost(url,map,rr,new DefaultRetryPolicy(20 * 1000, 1, 1.0f),tag);
    }

    public void doPost(String url, final Map<String, String> map,
                       ResponseAndErrorListener rr,RetryPolicy retryPolicy) {
        doPost(url,map,rr,new DefaultRetryPolicy(20 * 1000, 1, 1.0f),tag);
    }

    public void doPost(String url, final Map<String, String> map,
                       ResponseAndErrorListener rr, RetryPolicy retryPolicy,Object tag) {
        RequestQueue rq = VolleyCompat.getRequestQueue();
        UTFStringRequest mr = new UTFStringRequest(Request.Method.POST, url, rr, rr) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return map;
            }
        };
        mr.setTag(tag);
        rq.add(mr);
        mr.setRetryPolicy(retryPolicy);
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public void cancelRequest(final String tag) {
        RequestQueue rq = VolleyCompat.getRequestQueue();
        rq.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return request.getTag().equals(tag);
            }
        });
    }

}
