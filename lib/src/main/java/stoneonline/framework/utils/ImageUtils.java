package stoneonline.framework.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import www.stoneonline.com.lib.R;

public class ImageUtils {
    @SuppressWarnings("deprecation")
    public static Bitmap compressImageFromFile(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int widthSrc = newOpts.outWidth;
        int heightSrc = newOpts.outHeight;
        float heightDest = 280f;
        float widthDest = 240f;
        int be = 1;
        if (widthSrc > heightSrc && widthSrc > widthDest) {
            be = (int) (newOpts.outWidth / widthDest);
        } else if (widthSrc < heightSrc && heightSrc > heightDest) {
            be = (int) (newOpts.outHeight / heightDest);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;

        newOpts.inPreferredConfig = Config.ARGB_8888;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return bitmap;
    }

    public static Bitmap compressBmpFromBmp(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            options -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    public static void compressBmpToFile(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > 500) {
            baos.reset();
            options -= 10;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        try {
            FileOutputStream fos = new FileOutputStream(imagePath);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmap.recycle();
    }

    public static void compressBmp(String imagePath) {

    }

    public static byte[] compressBitmapFromFile(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int widthSrc = newOpts.outWidth;
        int heightSrc = newOpts.outHeight;
        float heightDest = 800f;//
        float widthDest = 480f;//
        int be = 1;
        if (widthSrc > heightSrc && widthSrc > widthDest) {
            be = (int) (newOpts.outWidth / widthDest);
        } else if (widthSrc < heightSrc && heightSrc > heightDest) {
            be = (int) (newOpts.outHeight / heightDest);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;
        newOpts.inPreferredConfig = Config.RGB_565;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        if (getExifOrientation(srcPath) == 90) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 75, baos);
        byte[] byteArray = baos.toByteArray();
        try {
            bitmap.recycle();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    public static void createWatermark(Bitmap src, Context context) {

        if (src == null) {
            T.show(context, R.string.save_fail_please_retry, Toast.LENGTH_SHORT);
            return;
        }
        Bitmap watermark = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_logo);
        int w = src.getWidth();
        int h = src.getHeight();
        int ww = watermark.getWidth();
        int wh = watermark.getHeight();
        Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas cv = new Canvas(newb);
        cv.drawBitmap(src, 0, 0, null);
        cv.drawBitmap(watermark, w - ww - 15, h - wh - 15, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();
        saveBitmap(context, newb);
    }

    public static void saveBitmap(Context context, Bitmap bitmap) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        try {
            String path = Environment.getExternalStorageDirectory().toString()
                    + "/ruishiimage/";
            File path1 = new File(path);
            if (!path1.exists()) {
                path1.mkdirs();
            }
            String imagename = System.currentTimeMillis() + ".jpg";
            File myCaptureFile = new File(path + imagename);
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(myCaptureFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            progressDialog.dismiss();
            T.show(context, context.getResources().getString(R.string.save_success_pic_is_locate) + path + context.getResources().getString(R.string.catalog), Toast.LENGTH_SHORT);
            scanFile(context, path, myCaptureFile, imagename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void scanFile(Context context, String path, File myCaptureFile, String imagename) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            MediaScannerConnection.scanFile(context, new String[]{path}, null, null);
        } else {
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        myCaptureFile.getAbsolutePath(), imagename, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
        }
    }

    public static byte[] degreeBitmap(String filepath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inPreferredConfig = Config.RGB_565;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filepath, newOpts);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bmpbytes = baos.toByteArray();
        return bmpbytes;
    }

    public static byte[] degreeBitmapDec(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int widthSrc = newOpts.outWidth;
        int heightSrc = newOpts.outHeight;
        float heightDest = 240f;//
        float widthDest = 320f;//
        int be = 1;
        if (widthSrc > heightSrc && widthSrc > widthDest) {
            be = (int) (newOpts.outWidth / widthDest);
        } else if (widthSrc < heightSrc && heightSrc > heightDest) {
            be = (int) (newOpts.outHeight / heightDest);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;
        newOpts.inPreferredConfig = Config.RGB_565;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        try {
            bitmap.recycle();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            Log.e("ExifOrientation", "cannot read exif", ex);
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                // We only recognize a subset of orientation tag values.
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }
        return degree;
    }
}
