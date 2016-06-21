package stoneonline.framework.utils;

import android.app.Activity;
import android.content.Context;
import android.glidecompat.CircleBitmapTransformation;
import android.glidecompat.RoundBitmapTransformation;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;

/**
 * Created by wengui on 2016/2/26.
 */
public final class ImageLoaderUtil {

    private static final int INVALID_RES = -1;
    private ImageLoaderUtil(){}

    public static void loadImage(Context context,String url, ImageView imageView){
        loadImage(context,url,imageView,INVALID_RES);
    }

    public static void loadImage(Activity activity,String url, ImageView imageView){
        loadImage(activity,url,imageView,INVALID_RES);
    }

    public static void loadImage(Fragment fragment,String url, ImageView imageView){
        loadImage(fragment,url,imageView,INVALID_RES);
    }

    public static void loadImage(Context context,String url, ImageView imageView,int defaultRes){
        final DrawableTypeRequest<String> request = Glide.with(context).load(url);
        if(defaultRes != INVALID_RES){
            request.placeholder(defaultRes);
        }
        request.into(imageView);
    }

    public static void loadImage(Activity activity, String url, ImageView imageView, int defaultRes){
        final DrawableTypeRequest<String> request = Glide.with(activity).load(url);
        if(defaultRes != INVALID_RES){
            request.placeholder(defaultRes);
        }
        request.into(imageView);
    }

    public static void loadImage(Fragment fragment, String url, ImageView imageView, int defaultRes){
        final DrawableTypeRequest<String> request = Glide.with(fragment).load(url);
        if(defaultRes != INVALID_RES){
            request.placeholder(defaultRes);
        }
        request.into(imageView);
    }

    public static void loadRoundImage(Fragment fragment, String url, ImageView imageView,int radiuDp){
        loadRoundImage(fragment,url,imageView,radiuDp,INVALID_RES);
    }

    public static void loadRoundImage(Activity activity, String url, ImageView imageView,int radiuDp){
        loadRoundImage(activity,url,imageView,radiuDp,INVALID_RES);
    }

    public static void loadRoundImage(Context context, String url, ImageView imageView,int radiuDp){
        loadRoundImage(context,url,imageView,radiuDp,INVALID_RES);
    }

    public static void loadRoundImage(Fragment fragment, String url, ImageView imageView,int radiuDp, int defaultRes){
        final DrawableTypeRequest<String> request = Glide.with(fragment).load(url);
        if(defaultRes != INVALID_RES){
            request.placeholder(defaultRes);
        }
        request.transform(new RoundBitmapTransformation(fragment.getContext(),radiuDp));
        request.into(imageView);
    }

    public static void loadRoundImage(Activity activity, String url, ImageView imageView,int radiuDp, int defaultRes){
        final DrawableTypeRequest<String> request = Glide.with(activity).load(url);
        if(defaultRes != INVALID_RES){
            request.placeholder(defaultRes);
        }
        request.transform(new RoundBitmapTransformation(activity,radiuDp));
        request.into(imageView);
    }

    public static void loadRoundImage(Context context, String url, ImageView imageView,int radiuDp, int defaultRes){
        final DrawableTypeRequest<String> request = Glide.with(context).load(url);
        if(defaultRes != INVALID_RES){
            request.placeholder(defaultRes);
        }
        request.transform(new RoundBitmapTransformation(context,radiuDp));
        request.into(imageView);
    }

    public static void loadCircleImage(Fragment fragment, String url, ImageView imageView, int defaultRes){
        final DrawableTypeRequest<String> request = Glide.with(fragment).load(url);
        if(defaultRes != INVALID_RES){
            request.placeholder(defaultRes);
        }
        request.transform(new CircleBitmapTransformation(fragment.getContext()));
        request.into(imageView);
    }

    public static void loadCircleImage(Fragment fragment, String url, ImageView imageView){
        loadCircleImage(fragment,url,imageView,INVALID_RES);
    }

    public static void loadCircleImage(Activity activity, String url, ImageView imageView){
        loadCircleImage(activity,url,imageView,INVALID_RES);
    }

    public static void loadCircleImage(Context context, String url, ImageView imageView){
        loadCircleImage(context,url,imageView,INVALID_RES);
    }

    public static void loadCircleImage(Context context, String url, ImageView imageView, int defaultRes){
        final DrawableTypeRequest<String> request = Glide.with(context).load(url);
        if(defaultRes != INVALID_RES){
            request.placeholder(defaultRes);
        }
        request.transform(new CircleBitmapTransformation(context));
        request.into(imageView);
    }

    public static void loadCircleImage(Activity activity, String url, ImageView imageView, int defaultRes){
        final DrawableTypeRequest<String> request = Glide.with(activity).load(url);
        if(defaultRes != INVALID_RES){
            request.placeholder(defaultRes);
        }
        request.transform(new CircleBitmapTransformation(activity));
        request.into(imageView);
    }
}


