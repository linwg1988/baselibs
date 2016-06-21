package stoneonline.framework.utils.file;

import java.io.File;

/**
 * Created by wengui on 2016/2/24.
 */
public interface DownloadListener extends ProgressListener{
    void onStart();
    void onSuccess(File file);
    void onFail(Exception e);
}
