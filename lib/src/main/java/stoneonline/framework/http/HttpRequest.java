package stoneonline.framework.http;

/**
 * Created by wengui on 2016/4/7.
 */
public class HttpRequest {
    public String requestUrl;

    public String params;

    public String tag;

    public Class<?> clazz;

    public HttpRequest(String requestUrl) {
        this.requestUrl = requestUrl;
        this.params = "";
    }

    public HttpRequest(String requestUrl, String params) {
        this.requestUrl = requestUrl;
        this.params = params;
    }

    public HttpRequest(String requestUrl, String params, String tag) {
        this.requestUrl = requestUrl;
        this.params = params;
        this.tag = tag;
    }

    public HttpRequest(String requestUrl, String params, Class<?> clazz) {
        this.requestUrl = requestUrl;
        this.params = params;
        this.clazz = clazz;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestUrl='" + requestUrl + '\'' +
                ", params='" + params + '\'' +
                ", tag='" + tag + '\'' +
                ", clazz=" + clazz +
                '}';
    }
}
