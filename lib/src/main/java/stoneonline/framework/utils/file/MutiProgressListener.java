package stoneonline.framework.utils.file;

/**
 * Created by wengui on 2016/2/24.
 */
public interface MutiProgressListener extends ProgressListener{
    void inFileProgress(int fileIndex, long progress, long totalLength);
}
