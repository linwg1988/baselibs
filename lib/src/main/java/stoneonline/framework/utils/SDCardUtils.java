package stoneonline.framework.utils;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

public class SDCardUtils {
    private SDCardUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

    }

    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    public static long getSDCardAllSize() {
        if (isSDCardEnable()) {
            StatFs stat = new StatFs(getSDCardPath());
            long availableBlocks = (long) stat.getAvailableBlocks() - 4;
            long freeBlocks = stat.getAvailableBlocks();
            return freeBlocks * availableBlocks;
        }
        return 0;
    }

    public static long getFreeBytes(String filePath) {
        if (filePath.startsWith(getSDCardPath())) {
            filePath = getSDCardPath();
        } else {
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    public static long calcCache(File cachePath) {
        long fileSize = 0;
        if (!cachePath.isDirectory()) {
            return cachePath.length();
        }
        File[] files = cachePath.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                return fileSize + calcCache(files[i]);
            } else {
                fileSize += files[i].length();
            }
        }
        return fileSize;
    }

    public static void clearCache(File cachePath) {
        if (!cachePath.isDirectory()) {
            cachePath.delete();
        }
        File[] files = cachePath.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                clearCache(files[i]);
            } else {
                files[i].delete();
            }
        }
    }
}
