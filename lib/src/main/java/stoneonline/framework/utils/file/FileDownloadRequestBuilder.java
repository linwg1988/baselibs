package stoneonline.framework.utils.file;

import android.os.Environment;

import java.util.Map;

/**
 * Created by wengui on 2016/2/24.
 */
public class FileDownloadRequestBuilder extends RequestBuilder {

    protected String savePath = Environment.getExternalStorageState() + "/Download/";

    protected String fileName;

    protected ProgressListener progressListener;

    public FileDownloadRequestBuilder savePath(String savePath){
        this.savePath = savePath;
        return this;
    }

    public FileDownloadRequestBuilder progress(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    public FileDownloadRequestBuilder fileName(String fileName){
        this.fileName = fileName;
        return this;
    }

    @Override
    public int buildType() {
        return BUILD_DOWNLOAD;
    }

    @Override
    public FileDownloadRequestBuilder params(Map<String ,Object> params) {
        this.params = params;
        return this;
    }

    @Override
    public FileDownloadRequestBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public FileDownloadRequestBuilder url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public RequestCall build() {
        return new RequestCall(this);
    }
}
