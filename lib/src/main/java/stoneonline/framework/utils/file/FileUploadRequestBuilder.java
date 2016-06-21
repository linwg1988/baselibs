package stoneonline.framework.utils.file;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wengui on 2016/2/24.
 */
public class FileUploadRequestBuilder extends RequestBuilder {

    protected List<String> filePaths = new ArrayList<>();

    protected boolean isImage;

    protected boolean scaleImage = true;

    protected ProgressListener progressListener;

    public FileUploadRequestBuilder progress(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    public FileUploadRequestBuilder scaleImage(boolean scaleImage) {
        this.scaleImage = scaleImage;
        return this;
    }

    public FileUploadRequestBuilder image() {
        this.isImage = true;
        return this;
    }

    public FileUploadRequestBuilder image(boolean isImage) {
        this.isImage = isImage;
        return this;
    }

    public FileUploadRequestBuilder addFile(String filePath) {
        if(!TextUtils.isEmpty(filePath)){
            this.filePaths.add(filePath);
        }
        return this;
    }

    public FileUploadRequestBuilder addFiles(List<String> filePaths) {
        this.filePaths.addAll(filePaths);
        return this;
    }

    @Override
    public FileUploadRequestBuilder params(Map<String ,Object> params) {
        this.params = params;
        return this;
    }

    @Override
    public FileUploadRequestBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public FileUploadRequestBuilder url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public RequestCall build() {
        return new RequestCall(this);
    }

    @Override
    public int buildType() {
        return BUILD_UPLOAD;
    }
}
