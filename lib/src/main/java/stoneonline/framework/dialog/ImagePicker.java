package stoneonline.framework.dialog;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import stoneonline.framework.utils.DensityUtils;
import stoneonline.framework.utils.ImageLoaderUtil;
import stoneonline.framework.utils.ImageUtils;
import stoneonline.framework.utils.ScreenUtils;
import stoneonline.framework.utils.T;
import stoneonline.framework.utils.ViewHolderUtils;
import www.stoneonline.com.lib.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by wengui on 2016/2/26.
 */
public class ImagePicker extends DialogFragment {

    public static class ImageFloder {
        /**
         * image's dir
         */
        private String dir;

        /**
         * the first image's path
         */
        private String firstImagePath;

        /**
         * folders name
         */
        private String name;

        /**
         * number of image
         */
        private int count;

        public void setName(String name) {
            this.name = name;
        }

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
            int lastIndexOf = this.dir.lastIndexOf("/");
            this.name = this.dir.substring(lastIndexOf);
        }

        public String getFirstImagePath() {
            return firstImagePath;
        }

        public void setFirstImagePath(String firstImagePath) {
            this.firstImagePath = firstImagePath;
        }

        public String getName() {
            return name;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

    }

    /**
     * Number of picture in dir.
     */
    private int mPicsSize;
    /**
     * At the first this is the largest dir ,and then it is the choice.
     */
    private File mImgDir;

    private String mImgDirPath;
    /**
     * All of the pictures.
     */
    private List<String> mImgs = new ArrayList<String>();

    /**
     * Tem helper to prevent rescan of a file.
     */
    private HashSet<String> mDirPaths;

    private OnImagePickerListener mOnImagePickerListener;

    private final int PHOTOHRAPH = 100;

    /**
     * If start photo by camera, this path string will be callback by listener
     * while the image is done;
     */
    private String imagePath;

    /**
     * User for whether open camera to pick a now time picture;
     */
    private boolean openCamera;
    /**
     * Limit of the picked picture
     */
    private int maxPictureNumber;
    /**
     * The hole path of the user choices.
     */
    public ArrayList<String> mSelectedImage = new ArrayList<String>();

    private GridView mGirdView;

    /**
     * Scan sdcard and get all folders that contains image.
     */
    private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

    private RelativeLayout mBottomLy;

    private TextView mChooseDir;
    private TextView mImageCount;
    int totalCount = 0;

    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            imgProgressBar.setVisibility(View.GONE);
            getImages();
            dataView();
            initListDirPopupWindw();
        }
    };

    private ImageView backButton;

    private TextView selectNum;

    private ImageAdapter imageAdapter;

    private PopupWindow imagePopWindow;

    private ProgressBar imgProgressBar;
    private PopAdapter popAdapter;


    public ImagePicker() {
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Black_NoTitleBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mSelectedImage = savedInstanceState.getStringArrayList("mSelectedImage");
            imagePath = savedInstanceState.getString("imagePath");
            mImgDirPath = savedInstanceState.getString("mImgDirPath");
        }
        return inflater.inflate(R.layout.fragment_select_image, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        init(view);
    }

    private void init(View view) {
        final Bundle arguments = getArguments();
        this.mSelectedImage = arguments.getStringArrayList("mSelectedImage");
        this.maxPictureNumber = arguments.getInt("maxPictureNumber");
        this.openCamera = arguments.getBoolean("openCamera");
        this.mImgDirPath = arguments.getString("mImgDirPath");
        backButton = (ImageView) view.findViewById(R.id.title_bar_left_menu);
        selectNum = (TextView) view.findViewById(R.id.title_bar_right_text);
        selectNum.setVisibility(View.VISIBLE);
        imgProgressBar = (ProgressBar) view.findViewById(R.id.pb_load_img);

        selectNum.setText(mSelectedImage.size() + "/" + maxPictureNumber + getResources().getString(R.string.Done));

        mGirdView = (GridView) view.findViewById(R.id.id_gridView);
        mChooseDir = (TextView) view.findViewById(R.id.id_choose_dir);
        mImageCount = (TextView) view.findViewById(R.id.id_total_count);
        mBottomLy = (RelativeLayout) view.findViewById(R.id.id_bottom_ly);

        mBottomLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imagePopWindow.setAnimationStyle(R.style.anim_popup_dir);
                imagePopWindow.showAsDropDown(mBottomLy, 0, 0);

                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = .3f;
                getActivity().getWindow().setAttributes(lp);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        selectNum.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mSelectedImage.size() == 0) {
                    toast(getResources().getString(R.string.NoPicChoice));
                    return;
                }
                if (mOnImagePickerListener != null) {
                    mOnImagePickerListener.onImagesPicke(mSelectedImage, mImgDir.getAbsolutePath());
                }
                dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.postDelayed(mRunnable, 200);
    }

    private void toast(String content) {
        T.show(getContext(), content, Toast.LENGTH_SHORT);
    }

    private void dataView() {

        if (mImgDirPath != null) {
            mImgDirPath = mImgDirPath + "/temp.jpg";
            mImgDir = new File(mImgDirPath).getParentFile();
        }
        if (mImgDir == null) {
            T.show(getContext(), R.string.ForderNotExist, Toast.LENGTH_SHORT);
            imageAdapter = new ImageAdapter();
            mGirdView.setAdapter(imageAdapter);
            return;
        }
        String[] list = mImgDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                boolean b;
                // if (Build.VERSION.SDK_INT >=
                // Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                // b = filename.endsWith(".jpg")
                // || filename.endsWith(".png")
                // || filename.endsWith(".jpeg")
                // || filename.endsWith(".gif")
                // || filename.endsWith(".webp");
                // } else {
                b = filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg")
                        || filename.endsWith(".gif");
                // }
                if (b)
                    return true;
                return false;
            }
        });
        mImgs = Arrays.asList(list);
        imageAdapter = new ImageAdapter();
        mGirdView.setAdapter(imageAdapter);
        mChooseDir.setText(mImgDir.getAbsolutePath().substring(mImgDir.getAbsolutePath().lastIndexOf("/")));
        mImageCount.setText(mImgs.size() + getResources().getString(R.string.piece));
    }

    private void getImages() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            toast(getResources().getString(R.string.no_extra_storage));
            return;
        }

        // new Thread(new Runnable() {
        // @Override
        // public void run() {

        String firstImage = null;

        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = getActivity().getContentResolver();

        // 4.0+ support webp picture
        String selection = "";
        String[] selectionArgs;
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        // {
        // selection = MediaStore.Images.Media.MIME_TYPE + "=? or "
        // + MediaStore.Images.Media.MIME_TYPE + "=? or "
        // + MediaStore.Images.Media.MIME_TYPE + "=? or "
        // + MediaStore.Images.Media.MIME_TYPE + "=?";
        // selectionArgs = new String[] { "image/jpeg", "image/png",
        // "image/gif", "image/webp" };
        // } else {
        selection = MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?  or "
                + MediaStore.Images.Media.MIME_TYPE + "=?";
        selectionArgs = new String[]{"image/jpeg", "image/png", "image/gif"};
        // }
        Cursor mCursor = mContentResolver.query(mImageUri, null, selection, selectionArgs,
                MediaStore.Images.Media.DATE_MODIFIED);

        if (mDirPaths == null) {
            mDirPaths = new HashSet<>();
        }
        while (mCursor.moveToNext()) {
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

            if (firstImage == null)
                firstImage = path;
            File parentFile = new File(path).getParentFile();
            if (parentFile == null)
                continue;
            String dirPath = parentFile.getAbsolutePath();
            ImageFloder imageFloder = null;
            if (mDirPaths.contains(dirPath)) {
                continue;
            } else {
                mDirPaths.add(dirPath);
                imageFloder = new ImageFloder();
                imageFloder.setDir(dirPath);
                imageFloder.setFirstImagePath(path);
            }

            String[] list = parentFile.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    boolean b;
                    // if (Build.VERSION.SDK_INT >=
                    // Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    // b = filename.endsWith(".jpg")
                    // || filename.endsWith(".png")
                    // || filename.endsWith(".jpeg")
                    // || filename.endsWith(".gif")
                    // || filename.endsWith(".webp");
                    // } else {
                    b = filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg")
                            || filename.endsWith(".gif");
                    // }
                    if (b)
                        return true;
                    return false;
                }
            });
            int picSize = list == null ? 0 : list.length;
            totalCount += picSize;

            imageFloder.setCount(picSize);
            mImageFloders.add(imageFloder);

            if (picSize > mPicsSize) {
                mPicsSize = picSize;
                mImgDir = parentFile;
            }
        }
        mCursor.close();

        mDirPaths.clear();
        mDirPaths = null;
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("deprecation")
    private void initListDirPopupWindw() {
        View popView = getLayoutInflater().inflate(R.layout.list_select_image_pop, null);
        imagePopWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (ScreenUtils.getScreenHeight(getActivity()) * 0.7));
        imagePopWindow.setBackgroundDrawable(new BitmapDrawable());
        imagePopWindow.setFocusable(true);

        ListView mListDir = (ListView) popView.findViewById(R.id.id_list_dir);
        popAdapter = new PopAdapter();
        mListDir.setAdapter(popAdapter);
        imagePopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1.0f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
        mListDir.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected(mImageFloders.get(position));
            }
        });
    }

    public void selected(ImageFloder floder) {

        mImgDir = new File(floder.getDir());
        String[] list = mImgDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                boolean b;
                // if (Build.VERSION.SDK_INT >=
                // Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                // b = filename.endsWith(".jpg") ||
                // filename.endsWith(".png")
                // || filename.endsWith(".jpeg")
                // || filename.endsWith(".gif")
                // || filename.endsWith(".webp");
                // } else {
                b = filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg")
                        || filename.endsWith(".gif");
                // }
                if (b)
                    return true;
                return false;
            }
        });
        if (list != null) {
            mImgs = Arrays.asList(list);
            mImageCount.setText(floder.getCount() + getString(R.string.piece));
            mChooseDir.setText(floder.getName());
            imageAdapter.notifyDataSetChanged();
        } else {
            mImageFloders.remove(floder);
            popAdapter.notifyDataSetChanged();
            T.show(getActivity(), R.string.ForderNotExist, Toast.LENGTH_SHORT);
        }
        imagePopWindow.dismiss();
    }

    class ImageAdapter extends BaseAdapter {
        int width;

        public ImageAdapter() {
            width = ScreenUtils.getScreenWidth(getActivity()) / 4 - DensityUtils.dp2px(getActivity(), 4);
        }

        @Override
        public int getViewTypeCount() {
            if (openCamera) {
                return 2;
            }
            return 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (openCamera) {
                if (position == 0) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                if (getItemViewType(position) == 0) {
                    convertView = getLayoutInflater().inflate(R.layout.grid_item_first_select_image, parent, false);
                } else {
                    convertView = getLayoutInflater().inflate(R.layout.grid_item_select_image, parent, false);
                }

                ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
                layoutParams.width = width;
                layoutParams.height = width;
                convertView.setLayoutParams(layoutParams);
            }

            if (getItemViewType(position) == 0) {
                final ImageView mImageView = ViewHolderUtils.get(convertView, R.id.id_item_image);

                mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startPhoto();
                    }
                });
            }

            if (getItemViewType(position) == 1) {
                final ImageView mImageView = ViewHolderUtils.get(convertView, R.id.id_item_image);
                final ImageView mSelect = ViewHolderUtils.get(convertView, R.id.id_item_select);

                selectNum.setText(mSelectedImage.size() + "/" + maxPictureNumber + getResources().getString(R.string.Done));
                final int pos;
                if (openCamera) {
                    pos = position - 1;
                } else {
                    pos = position;
                }

                ImageLoaderUtil.loadImage(ImagePicker.this,
                        "file://" + mImgDir.getAbsolutePath() + "/" + mImgs.get(pos), mImageView);

                if (mSelectedImage.contains(mImgDir.getAbsolutePath() + "/" + mImgs.get(pos))) {
                    mSelect.setImageResource(R.drawable.icon_selected);
                    mImageView.setColorFilter(Color.parseColor("#77000000"));
                } else {
                    mSelect.setImageResource(R.drawable.lib_icon_unselected);
                    mImageView.setColorFilter(null);
                }
                mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mSelectedImage.contains(mImgDir.getAbsolutePath() + "/" + mImgs.get(pos))) {
                            mSelectedImage.remove(mImgDir.getAbsolutePath() + "/" + mImgs.get(pos));
                            mSelect.setImageResource(R.drawable.lib_icon_unselected);
                            mImageView.setColorFilter(null);
                            selectNum.setText(mSelectedImage.size() + "/" + maxPictureNumber + getResources().getString(R.string.Done));
                        } else {
                            if (mSelectedImage.size() == maxPictureNumber) {
                                T.show(getActivity(), getResources().getString(R.string.most_choice) + maxPictureNumber + getString(R.string.piece_pic), Toast.LENGTH_SHORT);
                                return;
                            }
                            mSelectedImage.add(mImgDir.getAbsolutePath() + "/" + mImgs.get(pos));
                            mSelect.setImageResource(R.drawable.icon_selected);
                            mImageView.setColorFilter(Color.parseColor("#77000000"));
                            selectNum.setText(mSelectedImage.size() + "/" + maxPictureNumber + getResources().getString(R.string.Done));
                        }
                    }
                });
            }
            return convertView;
        }

        @Override
        public int getCount() {
            if (openCamera) {
                if (mImgs.size() == 0) {

                    return 1;
                }
                return mImgs.size() + 1;
            }
            return mImgs.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    private LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getActivity());
    }

    private void startPhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String path = Environment.getExternalStorageDirectory().toString() + "/ruishiimage/";
        File path1 = new File(path);
        if (!path1.exists()) {
            path1.mkdirs();
        }
        imagePath = path + System.currentTimeMillis() + ".jpg";
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imagePath)));
        startActivityForResult(intent, PHOTOHRAPH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        if (requestCode == PHOTOHRAPH && Activity.RESULT_OK == resultCode) {
            String path = Environment.getExternalStorageDirectory().toString()
                    + "/ruishiimage/";
            final File ab = new File(path + imagePath);
            ImageUtils.scanFile(getContext(), path, ab, imagePath);
            FragmentActivity ac = getActivity();
            if (ac != null && ac instanceof OnImagePickerListener) {
                ((OnImagePickerListener) ac).onCameraCallBack(imagePath);
            }
            dismiss();
        }
    }

    public void setOnImagePickerListener(OnImagePickerListener l) {
        this.mOnImagePickerListener = l;
    }

    public interface OnImagePickerListener {
        void onImagesPicke(List<String> imgPaths, String selectedDir);

        void onCameraCallBack(String imgPath);
    }

    public static class Builder {
        /**
         * User for whether open camera to pick a now time picture;
         */
        private boolean openCamera = true;
        /**
         * Limit of the picked picture
         */
        private int maxPictureNumber;
        private ArrayList<String> mSelectedImage = new ArrayList<String>();

        private String mImgDirPath;

        public Builder openCamera(boolean open) {
            this.openCamera = open;
            return this;
        }

        public Builder maxPictureNumber(int max) {
            if (max < 0) {
                max = 1;
            }
            this.maxPictureNumber = max;
            return this;
        }

        public Builder imgDirPath(String path) {
            this.mImgDirPath = path;
            return this;
        }

        public Builder selectedImages(List<String> selected) {
            this.mSelectedImage.clear();
            this.mSelectedImage.addAll(selected);
            return this;
        }

        public ImagePicker build() {
            final ImagePicker picker = new ImagePicker();
            Bundle argus = new Bundle();
            argus.putInt("maxPictureNumber", maxPictureNumber);
            argus.putBoolean("openCamera", openCamera);
            argus.putStringArrayList("mSelectedImage", mSelectedImage);
            argus.putString("mImgDirPath", mImgDirPath);
            picker.setArguments(argus);
            return picker;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("mSelectedImage", mSelectedImage);
        outState.putString("imagePath", imagePath);
        outState.putString("mImgDirPath", mImgDirPath);
    }

    @SuppressLint("InflateParams")
    class PopAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                convertView = getLayoutInflater().inflate(R.layout.list_dir_item_select_image, null);
            }

            TextView itemName = ViewHolderUtils.get(convertView, R.id.id_dir_item_name);
            TextView itemCount = ViewHolderUtils.get(convertView, R.id.id_dir_item_count);
            ImageView itemImage = ViewHolderUtils.get(convertView, R.id.id_dir_item_image);
            ImageView itemChoose = ViewHolderUtils.get(convertView, R.id.iv_item_choose);

            if (mImgDir.getAbsolutePath().toString().equals(mImageFloders.get(position).getDir())) {
                itemChoose.setImageResource(R.drawable.lib_dir_choose);
            } else {
                itemChoose.setImageResource(R.drawable.transition);
            }

            itemName.setText(mImageFloders.get(position).getName());
            itemCount.setText(mImageFloders.get(position).getCount() + getResources().getString(R.string.piece));
            ImageLoaderUtil.loadImage(ImagePicker.this, "file://" + mImageFloders.get(position).getFirstImagePath(),
                    itemImage);
            return convertView;
        }

        @Override
        public int getCount() {
            return mImageFloders.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
}
