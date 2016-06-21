package stoneonline.framework.utils.file;

import android.volley.toolbox.ResponseAndErrorListener;

/**
 * Created by wengui on 2016/2/24.
 */
public class RequestCall {

    private RequestBuilder request;

    public RequestCall(RequestBuilder builder){
        this.request = builder;
    }

    public boolean isUpload(){
        return request.buildType() == RequestBuilder.BUILD_UPLOAD;
    }

    public boolean isDownload(){
        return request.buildType() == RequestBuilder.BUILD_DOWNLOAD;
    }

    public void execute(ResponseAndErrorListener listener){
        FileTransportUtil.getInstance().execute(this,listener);
    }

    public RequestBuilder getOptions() {
        return request;
    }
}
