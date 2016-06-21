package stoneonline.framework.utils.file;

import android.text.TextUtils;

import java.io.File;
import java.util.List;

/**
 * Created by wengui on 2016/2/24.
 */
public class FileUtil {

    private FileUtil(){}

    public static long getFirstFileLength(List<String> filePaths) {
        if (filePaths == null || filePaths.size() == 0) {
            return 0;
        }
        long len = 0;
        final String filePath = filePaths.get(0);
        if(TextUtils.isEmpty(filePath)){
            return 0;
        }
        final File file = new File(filePath);
        if (file.exists()) {
            len = file.length();
        }
        return len;
    }

    public static long getTotalLength(List<String> filePaths) {
        int len = 0;
        for (String filePath : filePaths) {
            if(TextUtils.isEmpty(filePath)){
                continue;
            }
            final File file = new File(filePath);
            if (file.exists()) {
                len += file.length();
            }
        }
        return len;
    }
}
