package stoneonline.framework.dialog;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import java.util.ArrayList;

import stoneonline.framework.utils.CurstomUtils;
import stoneonline.framework.view.CirclePageIndicator;
import stoneonline.framework.view.HackyViewPager;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import www.stoneonline.com.lib.R;

/**
 * ImageBrowser Created by wengui on 2016/3/9.
 */
public class ImageBrowser extends DialogFragment {
    private int mode = Mode.NORMAL;
    private ArrayList<String> imageUrls;
    private String currentUrl;
    private ArrayList<String> thumbUrls;
    private boolean isLocal;
    private ImageView ivOption;
    private ImagePagerAdapter imagePagerAdapter;
    private CirclePageIndicator circlePageIndicator;
    private int indexOf;
    private ArrayList<View> viewList = new ArrayList<View>();
    private int thumbSize;
    private String header;

    public ImageBrowser() {
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mode = savedInstanceState.getInt("MODE");
            thumbSize = savedInstanceState.getInt("THUMB_SIZE");
            imageUrls = savedInstanceState.getStringArrayList("URLS");
            currentUrl = savedInstanceState.getString("CURRENT_URL");
            header = savedInstanceState.getString("HEADER");
            thumbUrls = savedInstanceState.getStringArrayList("THUMB_URLS");
            isLocal = savedInstanceState.getBoolean("IS_LOCAL");
        } else {
            final Bundle arguments = getArguments();
            mode = arguments.getInt("MODE");
            header = arguments.getString("HEADER");
            thumbSize = arguments.getInt("THUMB_SIZE");
            imageUrls = arguments.getStringArrayList("URLS");
            currentUrl = arguments.getString("CURRENT_URL");
            thumbUrls = arguments.getStringArrayList("THUMB_URLS");
            isLocal = arguments.getBoolean("IS_LOCAL");
        }
        if (thumbSize == 0) {
            thumbSize = CurstomUtils.getInstance().dip2px(getActivity(), 100);
        }
        return inflater.inflate(R.layout.fragment_iamge_browser, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivOption = (ImageView) view.findViewById(R.id.ivOption);
        viewList.clear();
        for (int i = 0; i < imageUrls.size(); i++) {
            View imageLayout = getActivity().getLayoutInflater().inflate(R.layout.item_image_browser, null);
            final View iv_thumbnail = imageLayout.findViewById(R.id.iv_thumbnail);
            final ViewGroup.LayoutParams params = iv_thumbnail.getLayoutParams();
            params.height = thumbSize;
            params.width = thumbSize;
            viewList.add(imageLayout);
        }
        switch (mode) {
            case Mode.NORMAL:
                ivOption.setVisibility(View.GONE);
                break;
            case Mode.DELETE:
                ivOption.setImageResource(R.drawable.lib_delete_selector);
                ivOption.setVisibility(View.VISIBLE);
                break;
            case Mode.DOWNLOAD:
                ivOption.setImageResource(R.drawable.save_image_selector);
                ivOption.setVisibility(View.VISIBLE);
                break;
        }

        HackyViewPager mViewPager = (HackyViewPager) view.findViewById(R.id.vp_image);
        mViewPager.setOffscreenPageLimit(1);
        imagePagerAdapter = new ImagePagerAdapter();
        mViewPager.setAdapter(imagePagerAdapter);
        indexOf = imageUrls.indexOf(currentUrl);
        mViewPager.setCurrentItem(indexOf);
        circlePageIndicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
        circlePageIndicator.setViewPager(mViewPager);

        ivOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode == Mode.NORMAL)
                    return;

                if (mode == Mode.DELETE) {
                    if (mOnDeleteClickListener != null) {
                        LayoutInflater inflater = LayoutInflater.from(getActivity());
                        View view = inflater.inflate(R.layout.dialog_delete_hint, null);
                        final Dialog dialog = new Dialog(getActivity(), R.style.lib_dialog_style);
                        TextView dialogTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
                        String worm = getResources().getString(R.string.Worm);
                        String sureToRemovePic = getResources().getString(R.string.SureToRemovePic);
                        String confirm = getResources().getString(R.string.Confirm);
                        dialogTitle.setText(worm);
                        TextView mHint = (TextView) view.findViewById(R.id.tv_hint);
                        mHint.setText(sureToRemovePic);
                        TextView commit = (TextView) view.findViewById(R.id.tv_commit);
                        final TextView cancel = (TextView) view.findViewById(R.id.tv_cancel);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        commit.setText(confirm);
                        commit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String url = imageUrls.get(indexOf);
                                mOnDeleteClickListener.onClick(indexOf, url);
                                imagePagerAdapter.notifyDataSetChanged();
                                if (imageUrls.size() == 0) {
                                    dismiss();
                                }
                                dialog.cancel();
                            }
                        });
                        int deviceWidth = CurstomUtils.getInstance().getDeviceWidth(getActivity());
                        int dialogWidth = deviceWidth * 90 / 100;
                        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dialogWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                        dialog.addContentView(view, params);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();
                    }
                    return;
                }

                if (mode == Mode.DOWNLOAD) {
                    if (mOnDownloadClickListener != null) {
                        final PhotoView view = (PhotoView) viewList.get(indexOf).findViewById(R.id.photo_view);
                        Drawable drawable1 = view.getDrawable();
                        if (drawable1 != null && drawable1 instanceof GlideBitmapDrawable) {
                            mOnDownloadClickListener.onDownloadBtnClick(((GlideBitmapDrawable) drawable1).getBitmap());
                        }
                    }
                }
            }
        });
        circlePageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                indexOf = position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("URLS", imageUrls);
        outState.putStringArrayList("THUMB_URLS", thumbUrls);
        outState.putString("CURRENT_URL", currentUrl);
        outState.putString("HEADER", header);
        outState.putBoolean("IS_LOCAL", isLocal);
        outState.putInt("MODE", mode);
        outState.putInt("THUMB_SIZE", thumbSize);
    }

    public static class Mode {
        public static final int NORMAL = -1;
        public static final int DOWNLOAD = -2;
        public static final int DELETE = -3;
    }

    public static class Builder {
        private int mode = Mode.NORMAL;

        private ArrayList<String> urls;

        private String currentUrl;

        private ArrayList<String> thumbUrls;

        private boolean isLocal;

        private int thumbSize;
        private String header;

        public Builder() {

        }

        public Builder mode(int mode) {
            this.mode = mode;
            return this;
        }

        public Builder url(String url) {
            if (urls == null)
                urls = new ArrayList<>();
            urls.clear();
            urls.add(url);
            currentUrl = url;
            return this;
        }

        public Builder thumbUrl(String thumbUrl) {
            if (thumbUrls == null)
                thumbUrls = new ArrayList<>();
            thumbUrls.clear();
            thumbUrls.add(thumbUrl);
            return this;
        }

        public Builder header(String header) {
            this.header = header;
            return this;
        }

        public Builder urls(ArrayList<String> urls) {
            this.urls = urls;
            return this;
        }

        public Builder thumbUrls(ArrayList<String> thumbUrls) {
            this.thumbUrls = thumbUrls;
            return this;
        }

        public Builder currentUrl(String currentUrl) {
            this.currentUrl = currentUrl;
            return this;
        }

        public Builder isLocal(boolean isLocal) {
            this.isLocal = isLocal;
            return this;
        }

        public Builder thumbSize(int thumbSize) {
            this.thumbSize = thumbSize;
            return this;
        }

        public Builder target(ViewGroup parent, View target) {
            final int childCount = parent.getChildCount();
            Rect[] rects = new Rect[childCount];
            for (int i = 0; i < childCount; i++) {
                int[] locat = new int[2];
                final View child = parent.getChildAt(i);
                child.getLocationOnScreen(locat);
                rects[i] = new Rect(locat[0], locat[1], locat[0] + child.getWidth(), locat[1] + child.getHeight());
            }

            return this;
        }

        public ImageBrowser build() {
            final ImageBrowser imageBrowser = new ImageBrowser();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("URLS", urls);
            bundle.putStringArrayList("THUMB_URLS", thumbUrls);
            bundle.putString("CURRENT_URL", currentUrl);
            bundle.putString("HEADER", header);
            bundle.putBoolean("IS_LOCAL", isLocal);
            bundle.putInt("MODE", mode);
            bundle.putInt("THUMB_SIZE", thumbSize);
            imageBrowser.setArguments(bundle);
            return imageBrowser;
        }

    }

    OnDownloadClickListener mOnDownloadClickListener;
    OnDeleteClickListener mOnDeleteClickListener;

    public void setmOnDownloadClickListener(OnDownloadClickListener mOnDownloadClickListener) {
        this.mOnDownloadClickListener = mOnDownloadClickListener;
    }

    public void setmOnDeleteClickListener(OnDeleteClickListener mOnDeleteClickListener) {
        this.mOnDeleteClickListener = mOnDeleteClickListener;
    }

    public interface OnDownloadClickListener {
        void onDownloadBtnClick(Bitmap bitmap);
    }

    public interface OnDeleteClickListener {
        void onClick(int index, String url);
    }

    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View view = viewList.get(position);
            PhotoView photoView = (PhotoView) view.findViewById(R.id.photo_view);

            final ProgressBar mProgressBar = (ProgressBar) view.findViewById(R.id.pb_img_detail);
            final ImageView mThumbnail = (ImageView) view.findViewById(R.id.iv_thumbnail);
            photoView.setOnMatrixChangeListener(new PhotoViewAttacher.OnMatrixChangedListener() {
                @Override
                public void onMatrixChanged(RectF rect) {
                    if (rect.width() < thumbSize) {
                        mProgressBar.setVisibility(View.GONE);
                        mThumbnail.setVisibility(View.GONE);
                    }

                }
            });
            String imageUrl = "";
            String imageThumUrl = "";
            String url = imageUrls.get(position);
            if (isLocal) {
                imageUrl = "file://" + url;
            } else {
                if (url.startsWith("file://") || url.startsWith("http") || url.startsWith("drawable://")) {
                    imageUrl = url;
                } else {
                    imageUrl = TextUtils.isEmpty(header) ? url : header + url;
                }
                if (thumbUrls != null) {
                    imageThumUrl = thumbUrls.get(position);
                }
            }

            mProgressBar.setVisibility(View.VISIBLE);
            Glide.with(ImageBrowser.this).load(imageThumUrl).dontAnimate().into(mThumbnail);
            Glide.with(ImageBrowser.this).load(imageUrl).dontAnimate().into(photoView);

            photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {

                @Override
                public void onViewTap(View view, float x, float y) {
                    dismiss();
                }
            });
            container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        return super.show(transaction, tag);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public class DetailTransition extends TransitionSet {
        public DetailTransition() {
            init();
        }

        public DetailTransition(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            setOrdering(ORDERING_TOGETHER);
            addTransition(new ChangeTransform()).addTransition(new ChangeBounds())
                    .addTransition(new ChangeImageTransform());
        }
    }
}
