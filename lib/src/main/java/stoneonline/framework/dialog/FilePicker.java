package stoneonline.framework.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stoneonline.framework.utils.ViewHolderUtils;
import www.stoneonline.com.lib.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by wengui on 2016/2/26.
 */
public class FilePicker extends DialogFragment {

	public static final int MUTI_CHOICE = 0;
	public static final int SINGLE_CHOICE = 1;

	private OnFilesPickerListener mOnFilesPickerListener;

	private OnFilePickerListener mOnFilePickerListener;

	private final String path = "/";

	private Map<String, FileItem> caches = new HashMap<>();

	private List<FileItem> pickItems = new ArrayList<>();
	private int mode = SINGLE_CHOICE;

	public FilePicker() {
		setStyle(STYLE_NO_FRAME,android.R.style.Theme_Black_NoTitleBar);
	}

	public FilePicker setMode(int mode) {
		if (mode != MUTI_CHOICE && mode != SINGLE_CHOICE) {
			mode = SINGLE_CHOICE;
		}
		this.mode = mode;
		return this;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dialog_file_piker, null);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if(savedInstanceState != null){
			mode = savedInstanceState.getInt("MODE");
		}else{
			mode = getArguments().getInt("MODE");
		}
		final List<FileItem> files = getFiles(path);
		final ListView mListView = (ListView) view.findViewById(R.id.listFile);
		final FileAdapter fileAdapter = new FileAdapter(getContext(), files);
		mListView.setAdapter(fileAdapter);
		final View mConfirmBtn = view.findViewById(R.id.tvConfirm);
		final View mCancelBtn = view.findViewById(R.id.tvCancel);
		final View vMid = view.findViewById(R.id.vMid);

		if (isSingleMode()) {
			vMid.setVisibility(View.GONE);
			mConfirmBtn.setVisibility(View.GONE);
		}

		mConfirmBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				if (mOnFilesPickerListener != null) {
					mOnFilesPickerListener.onDone(pickItems);
				}
			}
		});
		mCancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	public boolean isSingleMode() {
		return mode == SINGLE_CHOICE;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("MODE",mode);
	}

	private List<FileItem> getFiles(String path) {
		File[] files = null;
		try {
			files = new File(path).listFiles();
		} catch (Exception e) {

		}
		if (files == null) {
			Toast.makeText(getContext(), "No right to access!", Toast.LENGTH_SHORT).show();
			return null;
		}
		final ArrayList<FileItem> fileItems = new ArrayList<>();
		addRoot(fileItems);
		if (!path.equals("/")) {
			addParent(fileItems, path);
		}

		final ArrayList<FileItem> dirItems = new ArrayList<>();
		final ArrayList<FileItem> listItems = new ArrayList<>();

		for (File file : files) {
			final String fileName = file.getName();
			final String absolutePath = file.getAbsolutePath();
			FileItem fileItem = caches.get(absolutePath);
			if (fileItem != null) {
				if (fileItem.filetype == FileItem.DIRETORY) {
					dirItems.add(fileItem);
				} else {
					listItems.add(fileItem);
				}
			} else {
				fileItem = new FileItem();
				fileItem.filePath = file.getPath();
				fileItem.fileName = fileName;
				if (file.isDirectory() && file.listFiles() != null) {
					fileItem.filetype = FileItem.DIRETORY;
					dirItems.add(fileItem);
				} else {
					fileItem.filetype = getFileType(fileName);
					listItems.add(fileItem);
				}
				caches.put(absolutePath, fileItem);
			}
		}

		fileItems.addAll(dirItems);
		fileItems.addAll(listItems);

		return fileItems;
	}

	private int getFileType(String fileName) {
		if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")
				|| fileName.endsWith(".gif") || fileName.endsWith(".bmp")) {
			return FileItem.IMAGE;
		} else if (fileName.endsWith(".txt")) {
			return FileItem.TEXT;
		} else if (fileName.endsWith(".m4a") || fileName.endsWith(".3gp") || fileName.endsWith(".mp3")
				|| fileName.endsWith(".amr") || fileName.endsWith(".ogg") || fileName.endsWith(".mp4")) {
			return FileItem.MUSIC;
		}
		return FileItem.OTHER;
	}

	private void addRoot(ArrayList<FileItem> fileItems) {
		FileItem root = new FileItem();
		root.filePath = "/";
		root.fileName = "/";
		root.filetype = FileItem.ROOT;
		fileItems.add(root);
	}

	private void addParent(ArrayList<FileItem> fileItems, String path) {
		FileItem parent = new FileItem();
		parent.filePath = path;
		parent.fileName = "..";
		parent.filetype = FileItem.PARENT;
		fileItems.add(parent);
	}

	private class FileAdapter extends BaseAdapter {
		List<FileItem> fileItems;

		Context context;

		public FileAdapter(Context context, List<FileItem> fileItems) {
			this.context = context;
			this.fileItems = fileItems;
		}

		public void setFileItems(List<FileItem> fileItems) {
			this.fileItems = fileItems;
		}

		@Override
		public int getCount() {
			return fileItems == null ? 0 : fileItems.size();
		}

		@Override
		public FileItem getItem(int position) {
			return fileItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_file, null);
			}
			final ImageView ivFileType = ViewHolderUtils.get(convertView, R.id.ivFileType);
			final TextView tvFileName = ViewHolderUtils.get(convertView, R.id.tvFileName);
			final TextView tvFilePath = ViewHolderUtils.get(convertView, R.id.tvFilePath);
			final CheckBox cbPick = ViewHolderUtils.get(convertView, R.id.cbPick);

			final FileItem item = getItem(position);
			tvFileName.setText(item.fileName);
			tvFilePath.setText(item.filePath);
			final int filetype = item.filetype;
			setImageType(filetype, ivFileType);

			if (isSingleMode() || item.filetype == FileItem.ROOT || item.filetype == FileItem.DIRETORY
					|| item.filetype == FileItem.PARENT) {
				cbPick.setVisibility(View.GONE);
			} else {
				cbPick.setVisibility(View.VISIBLE);
			}

			cbPick.setOnCheckedChangeListener(null);
			cbPick.setChecked(pickItems.contains(item));
			cbPick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						pickItems.add(item);
					} else {
						pickItems.remove(item);
					}
				}
			});
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (item.filetype == FileItem.ROOT || item.filetype == FileItem.DIRETORY) {

						final List<FileItem> files = getFiles(item.filePath);
						setFileItems(files);
						notifyDataSetChanged();
					} else if (item.filetype == FileItem.PARENT) {
						final String filePath = item.filePath;
						final String parentPath = new File(filePath).getParent();
						final List<FileItem> files = getFiles(parentPath);
						setFileItems(files);
						notifyDataSetChanged();
					} else {
						if (isSingleMode()) {
							dismiss();
							if (mOnFilePickerListener != null) {
								mOnFilePickerListener.onDone(item);
							}
						} else {
							if (pickItems.contains(item)) {
								pickItems.remove(item);
							} else {
								pickItems.add(item);
							}
							notifyDataSetChanged();
						}
					}
				}
			});

			return convertView;
		}
	}

	private void setImageType(int filetype, ImageView imageView) {
		switch (filetype) {
			case FileItem.DIRETORY :
				imageView.setImageResource(R.drawable.folder_dir);
				break;
			case FileItem.IMAGE :
				imageView.setImageResource(R.drawable.file_pic);
				break;
			case FileItem.MUSIC :
				imageView.setImageResource(R.drawable.file_music);
				break;
			case FileItem.OTHER :
				imageView.setImageResource(R.drawable.file_other);
				break;
			case FileItem.PARENT :
				imageView.setImageResource(R.drawable.folder_parent);
				break;
			case FileItem.ROOT :
				imageView.setImageResource(R.drawable.folder_root);
				break;
			case FileItem.TEXT :
				imageView.setImageResource(R.drawable.file_text);
				break;
		}
	}

	public static class Builder{
		private int mode = SINGLE_CHOICE;
		public Builder(){

		}

		public Builder mode(int mode){
			if (mode != MUTI_CHOICE && mode != SINGLE_CHOICE) {
				mode = SINGLE_CHOICE;
			}
			this.mode = mode;
			return this;
		}

		public FilePicker build(){
			final FilePicker filePicker = new FilePicker();
			Bundle argus = new Bundle();
			argus.putInt("MODE",mode);
			filePicker.setArguments(argus);
			return filePicker;
		}
	}

	public static class FileItem {
		static final int ROOT = 0;
		static final int DIRETORY = 1;
		static final int IMAGE = 2;
		static final int MUSIC = 3;
		static final int TEXT = 4;
		static final int OTHER = 5;
		static final int PARENT = 6;

		public String fileName;
		public String filePath;
		int filetype;

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			FileItem fileItem = (FileItem) o;

			if (filetype != fileItem.filetype)
				return false;
			if (!fileName.equals(fileItem.fileName))
				return false;
			return filePath.equals(fileItem.filePath);

		}

		@Override
		public int hashCode() {
			int result = fileName.hashCode();
			result = 31 * result + filePath.hashCode();
			result = 31 * result + filetype;
			return result;
		}

		@Override
		public String toString() {
			return "FileItem{" + "fileName='" + fileName + '\'' + ", filePath='" + filePath + '\'' + ", filetype="
					+ filetype + '}';
		}
	}

	public FilePicker setOnFilesPickerListener(OnFilesPickerListener l) {
		this.mOnFilesPickerListener = l;
		return this;
	}
	public FilePicker setOnFilePickerListener(OnFilePickerListener l) {
		this.mOnFilePickerListener = l;
		return this;
	}

	public interface OnFilesPickerListener {
		void onDone(List<FileItem> files);
	}

	public interface OnFilePickerListener {
		void onDone(FileItem file);
	}
}
