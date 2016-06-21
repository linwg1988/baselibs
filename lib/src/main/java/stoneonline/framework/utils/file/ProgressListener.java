package stoneonline.framework.utils.file;

/**
 * Created by wengui on 2016/2/24.
 */
public interface ProgressListener {
    void inProgress(long progress, long totalLength);
}
