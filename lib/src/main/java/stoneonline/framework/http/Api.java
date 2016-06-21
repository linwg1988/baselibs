package stoneonline.framework.http;

import android.content.Context;

/**
 * Created by wengui on 2016/4/5.
 */
public interface Api {

    void doRequest(HttpRequest request, HttpListener listener);

}
