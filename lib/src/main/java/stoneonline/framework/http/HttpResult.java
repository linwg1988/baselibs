package stoneonline.framework.http;

/**
 * Created by wengui on 2016/4/7.
 */
public class HttpResult {
    public HttpRequest request;

    public boolean result;

    public String key;

    public String resultCode;

    public String errorMessage;

    public String dataString;

    @Override
    public String toString() {
        return "HttpResult{" +
                "request=" + request +
                ", result=" + result +
                ", key='" + key + '\'' +
                ", resultCode='" + resultCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", dataString='" + dataString + '\'' +
                '}';
    }
}
