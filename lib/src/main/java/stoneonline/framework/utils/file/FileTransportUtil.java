package stoneonline.framework.utils.file;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.volley.toolbox.ResponseAndErrorListener;

import com.android.volley.VolleyError;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import stoneonline.framework.utils.ImageUtils;
import stoneonline.framework.utils.L;

/**
 * Created by wengui on 2016/2/24.
 */
public class FileTransportUtil {

    private static FileTransportUtil util;

    private static final int SUCCESS = 1;
    private static final int FAIL = -1;
    private static final int PROGRESS = 0;
    private static final int START = 2;

    private FileTransportUtil(){
    }

    public static FileTransportUtil getInstance() {
        if (util == null) {
            synchronized (FileTransportUtil.class) {
                if (util == null) {
                    util = new FileTransportUtil();
                }
            }
        }
        return util;
    }

    public static FileUploadRequestBuilder uploadFile(){
        return new FileUploadRequestBuilder();
    }

    public static FileDownloadRequestBuilder downloadFile(){
        return new FileDownloadRequestBuilder();
    }

    public void execute(RequestCall requestCall, ResponseAndErrorListener listener) {

        if(requestCall.isDownload()){
            startDownload(requestCall,listener);
        }

        if(requestCall.isUpload()){
            startUpload(requestCall,listener);

        }

    }

    private void startUpload(final RequestCall requestCall,final ResponseAndErrorListener listener) {

        final FileUploadRequestBuilder options = (FileUploadRequestBuilder) requestCall.getOptions();
        final ProgressListener progressListener = options.progressListener;
        final String urlString = options.url;
        final Map<String,Object> params = options.params;
        final List<String> filePaths = options.filePaths;
        final boolean isUploadImage = options.isImage;
        final boolean scaleImage = options.scaleImage;

        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int what = msg.what;
                if (what == SUCCESS) {
                    if (listener != null) {
                        listener.onResponse(String.valueOf(msg.obj));
                    }
                } else if (what == FAIL) {
                    if (listener != null) {
                        listener.onErrorResponse(new VolleyError((Throwable) msg.obj));
                    }
                } else if (what == PROGRESS) {
                    if (progressListener != null) {
                        final ProgressHolder progressHolder = (ProgressHolder) msg.obj;
                        progressListener.inProgress(progressHolder.allFilesProgress, progressHolder.allFilesLength);
                        if (progressListener instanceof MutiProgressListener) {
                            ((MutiProgressListener) progressListener).inFileProgress(progressHolder.fileIndex, progressHolder.currentFileProgress, progressHolder.currentFileLength);
                        }
                    }
                }
            }
        };

        new Thread(){
            @Override
            public void run() {
                String response = "";
                HttpURLConnection conn = null;
                final String BOUNDARY = "---------------------------7da2137580612"; // data divider
                final String endline = "--" + BOUNDARY + "--\r\n";// data end mark
                try {

                    URL url = new URL(options.url);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setConnectTimeout(300000);
                    conn.setReadTimeout(300000);
                    conn.setRequestMethod("POST");
                    conn.setChunkedStreamingMode(0);
                    conn.setRequestProperty("Connection", "Close");
                    conn.setRequestProperty("Charset", "UTF-8");
                    conn.setRequestProperty("Content-Type", "multipart/form-data"
                            + "; boundary=" + BOUNDARY);
                    StringBuilder sb = new StringBuilder();
                    if(params != null){
                        for (Map.Entry<String, Object> entry : params.entrySet()) {
                            sb.append("--");
                            sb.append(BOUNDARY);
                            sb.append("\r\n");
                            sb.append("Content-Disposition: form-data; name=\""
                                    + entry.getKey() + "\"\r\n\r\n");
                            sb.append(entry.getValue());
                            sb.append("\r\n");
                        }
                    }
                    sb.append("--");
                    sb.append(BOUNDARY);
                    sb.append("\r\n");

                    OutputStream outputStream = conn.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(outputStream);
                    dos.write(sb.toString().getBytes());

                    final ProgressHolder progressHolder = new ProgressHolder();
                    progressHolder.allFilesLength = FileUtil.getTotalLength(filePaths);
                    progressHolder.currentFileLength = FileUtil.getFirstFileLength(filePaths);

                    if (progressListener != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(progressHolder.hasDone ){
                                    handler.removeCallbacks(this);
                                    return;
                                }

                                final Message message = handler.obtainMessage();
                                message.what = PROGRESS;
                                message.obj = progressHolder;
                                handler.sendMessage(message);
                                if (progressHolder.isOver()) {
                                    handler.removeCallbacks(this);
                                    return;
                                }
                                handler.postDelayed(this, 500);
                            }
                        });
                    }

                    for (String fielPath : filePaths) {
                        StringBuilder fileEntity = new StringBuilder();
                        fileEntity.append("--");
                        fileEntity.append(BOUNDARY);
                        fileEntity.append("\r\n");
                        fileEntity.append("Content-Disposition: form-data;name=\""
                                + fielPath.substring(fielPath.lastIndexOf("/"))
                                + "\";filename=\"" + fielPath + "\"\r\n");
                        if (isUploadImage) {
                            fileEntity.append("Content-Type: " + "image/jpeg\r\n\r\n");
                        }
                        dos.write(fileEntity.toString().getBytes());

                        File file = new File(fielPath);
                        progressHolder.fileIndex++;
                        progressHolder.currentFileLength = file.length();
                        progressHolder.currentFileProgress = 0;

                        if (isUploadImage && file.length() / 1024 > 250 && scaleImage) {
                            //compress bitmap and write to stream
                            byte[] fileBytes = ImageUtils.compressBitmapFromFile(fielPath);
                            dos.write(fileBytes, 0, fileBytes.length);
                            progressHolder.allFilesLength -= file.length() - fileBytes.length;
                            progressHolder.allFilesProgress += fileBytes.length;
                            progressHolder.currentFileLength = fileBytes.length;
                            progressHolder.currentFileProgress += fileBytes.length;
                        } else {
                            FileInputStream fis = new FileInputStream(fielPath);
                            byte[] buffer = new byte[1024];
                            int len = 0;
                            while ((len = fis.read(buffer, 0, 1024)) != -1) {
                                dos.write(buffer, 0, len);
                                progressHolder.currentFileProgress += len;
                                progressHolder.allFilesProgress += len;
                            }
                            fis.close();
                        }
                        dos.write("\r\n".getBytes());
                    }
                    dos.write(endline.getBytes());
                    InputStream is = conn.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is, "utf-8");
                    BufferedReader reader = new BufferedReader(isr);
                    response = reader.readLine();
                    dos.flush();
                    dos.close();
                    is.close();
                    isr.close();
                    reader.close();

                    progressHolder.hasDone = true;
                    sendResponseMessage(handler,response);
                    L.e("Url", options.url);
                    L.e("Response",response);
                } catch (Exception e) {
                    if (conn != null) {
                        conn.disconnect();
                    }
                    sendFailMessage(handler,e);
                    L.e("Url", options.url);
                    L.e("Response", e.getMessage());
                }
            }
        }.start();
    }

    private void sendFailMessage(Handler handler,Exception e){
        final Message message = handler.obtainMessage();
        message.what = FAIL;
        message.obj = e;
        handler.sendMessage(message);
    }

    private void sendResponseMessage(Handler handler,String response){
        final Message message = handler.obtainMessage();
        message.what = SUCCESS;
        message.obj = response;
        handler.sendMessage(message);
    }


    private void startDownload(RequestCall requestCall,final ResponseAndErrorListener listener) {
        final FileDownloadRequestBuilder options = (FileDownloadRequestBuilder) requestCall.getOptions();
        final ProgressListener progressListener = options.progressListener;
        final String urlString = options.url;
        final Map<String,Object> params = options.params;
        final String savePath = options.savePath;
        final String downloadUrl = options.url;
        final String saveFileName = options.fileName;

        final Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                int what = msg.what;
                if(what == START){
                    if(progressListener != null){
                        if (progressListener instanceof DownloadListener){
                            ((DownloadListener) progressListener).onStart();
                        }
                    }
                }else if (what == SUCCESS) {
                    if (listener != null) {
                        listener.onResponse("");
                    }
                    if(progressListener != null){
                        if (progressListener instanceof DownloadListener){
                            ((DownloadListener) progressListener).onSuccess((File) msg.obj);
                        }
                    }
                } else if (what == FAIL) {
                    if (listener != null) {
                        listener.onErrorResponse(new VolleyError((Throwable) msg.obj));
                    }
                    if(progressListener != null){
                        if (progressListener instanceof DownloadListener){
                            ((DownloadListener) progressListener).onFail((Exception) msg.obj);
                        }
                    }
                } else if (what == PROGRESS) {
                    if (progressListener != null) {
                        final ProgressHolder progressHolder = (ProgressHolder) msg.obj;
                        progressListener.inProgress(progressHolder.allFilesProgress, progressHolder.allFilesLength);
                    }
                }
            }
        };

        new Thread(){
            public void run() {
                try {
                    handler.sendEmptyMessage(START);

                    URL url = new URL(downloadUrl);
                    URLConnection conn = url.openConnection();
                    InputStream is = conn.getInputStream();
                    final int contentLength = conn.getContentLength();
                    L.e("Download", "contentLength = " + contentLength);
                    String dirName = savePath;
                    File file = new File(dirName);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    String fileName = dirName + saveFileName;
                    File file1 = new File(fileName);
                    if (file1.exists()) {
                        file1.delete();
                    }
                    byte[] bs = new byte[1024];
                    int len;
                    int current = 0;

                    OutputStream os = new FileOutputStream(fileName);
                    final ProgressHolder progressHolder = new ProgressHolder();
                    progressHolder.allFilesLength = contentLength;
                    progressHolder.currentFileLength = contentLength;
                    handler.post(new Runnable() {
                        public void run() {
                            if(progressHolder.hasDone){
                                handler.removeCallbacks(this);
                                return;
                            }
                            Message message = handler.obtainMessage();
                            message.what = PROGRESS;
                            message.obj = progressHolder;
                            handler.sendMessage(message);
                            if(progressHolder.isOver()){
                                handler.removeCallbacks(this);
                                return;
                            }else{
                                handler.postDelayed(this, 400);
                            }
                        }
                    });
                    while ((len = is.read(bs)) != -1) {
                        os.write(bs, 0, len);
                        current += len;
                        progressHolder.currentFileProgress = current;
                        progressHolder.allFilesProgress = current;
                    }
                    L.e("Download", "download-finish");
                    os.close();
                    is.close();
                    progressHolder.hasDone = true;

                    final Message message = handler.obtainMessage();
                    message.what = SUCCESS;
                    message.obj = file1;
                    handler.sendMessage(message);

                    L.e("Url", options.url);
                    L.e("Response", "File Download Completed");
                } catch (Exception e) {
                    sendFailMessage(handler,e);

                    L.e("Url", options.url);
                    L.e("Response", e.getMessage());
                }
            };
        }.start();
    }
}
