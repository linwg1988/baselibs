package stoneonline.framework.http;

import android.volley.toolbox.ResponseAndErrorListener;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import stoneonline.framework.utils.L;

/**
 * Created by wengui on 2016/4/7.
 */
public class ResponseListenerImpl implements ResponseAndErrorListener{

    private HttpListener listener;
    private HttpResult result;

    public ResponseListenerImpl(HttpListener listener,HttpRequest request) {
        this.listener = listener;
        this.result = new HttpResult();
        this.result.request = request;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        String message = VolleyErrorHelper.getMessage(error);
        this.result.result = false;
        this.result.errorMessage = message;
        int responseCode = VolleyErrorHelper.getResponseCode(error);
        if(responseCode == -1){
            this.result.resultCode = "NETWORK_ERROR";
        }else{
            this.result.resultCode = String.valueOf(responseCode);
        }
        L.e("Url:",result.request.requestUrl);
        L.e("Params:",result.request.params);
        L.e("Response:",error.getMessage());
        listener.onResponse(this.result);
    }

    @Override
    public void onResponse(String response) {
        L.e("Url:",result.request.requestUrl);
        L.e("Params:",result.request.params);
        L.e("Response:",response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean result = jsonObject.getBoolean("Result");
            String msgCode = jsonObject.getString("MSG_CODE");
            String key = jsonObject.getString("Key");
            this.result.result = result;
            this.result.resultCode = msgCode;
            this.result.key = key;
            if(result){
                String data = jsonObject.getString("Data");
                this.result.dataString = data;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            result.result = false;
            result.resultCode = "ANALYZE_ERROR";
            StackTraceElement element = e.getStackTrace()[0];
            String error = "Class:"+element.getClassName() + "  Line:"+element.getLineNumber() + "Method:" + element.getMethodName() + e.getMessage();
            result.errorMessage = "Data analyze exception:" + error;
        }
        listener.onResponse(this.result);
    }
}
