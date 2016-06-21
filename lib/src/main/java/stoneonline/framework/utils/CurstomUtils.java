package stoneonline.framework.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public final class CurstomUtils {
	private static CurstomUtils util = new CurstomUtils();

	private CurstomUtils() {
	}

	public static CurstomUtils getInstance() {
		return util;
	}

	public String getCurrentDate() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		return year + "-" + month + "-" + day;
	}

	public String getCurrentYear() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);

		return year+"";
	}

	public int getViewHeight(View v) {
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		v.measure(w, h);
		int height = v.getMeasuredHeight();
		return height;
	}

	public int getViewWidth(View v) {
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		v.measure(w, h);
		int width = v.getMeasuredWidth();
		return width;
	}

	public int diptopx(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public int pxtodip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public String getCurrentTime() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minite = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);

		return year + "-" + month + "-" + day + "\t" + hours + ":" + minite + ":" + second
				+ ":";
	}

	public int getDeviceHeight(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager service = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		service.getDefaultDisplay().getMetrics(dm);
		int deviceHeight = dm.heightPixels;
		return deviceHeight;
	}

	public int getDeviceWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager service = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		service.getDefaultDisplay().getMetrics(dm);
		int deviceWidth = dm.widthPixels;
		return deviceWidth;
	}

	public void showToast(Context context, String content) {
		Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
	}

	public void showToastLong(Context context, String content) {
		Toast.makeText(context, content, Toast.LENGTH_LONG).show();
	}

	public boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	public boolean stringFilter(String str) {
		String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5]";
		Pattern p = Pattern.compile(regEx);
		Matcher matcher = p.matcher(str);
		boolean isMatches = matcher.matches();
		return isMatches;
	}

	public boolean isMobileNO(String mobiles) {
		// Pattern p =
		// Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
		// Pattern p = Pattern
		// .compile("^((13[0-9])|(14[5,7])|(15[^4,\\D])|(18[0,2,5-9]))\\d{8}$");
		// Matcher m = p.matcher(mobiles);
		// return m.matches();
		return mobiles.length() == 11;
	}

	public boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	public boolean isZipNum(String zipString) {
		String str = "^[1-9][0-9]{5}$";
		return Pattern.compile(str).matcher(zipString).matches();
	}

	public boolean isStoneAccount(String account) {
		String str = "^(^[0-9])[a-zA-Z0-9]{5,9}$";
		return Pattern.compile(str).matcher(account).matches();
	}

	public boolean isStonePassword(String password) {
		return true;
	}

	public boolean isStoneName(String name) {
		return true;
	}

	public boolean isNetWorking(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connMgr.getActiveNetworkInfo();
		if (info == null || !info.isAvailable()) {
			return false;
		}
		return info.isAvailable();
	}

	public boolean isWifi(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connMgr.getActiveNetworkInfo();
		if (isNetWorking(context)) {
			return info.getType() == ConnectivityManager.TYPE_WIFI;
		}
		return false;
	}

	public long getServerTime(String str) {
		String time = str.substring(6, str.length() - 2);
		return Long.valueOf(time);
	}

	public String getVerifyKey() {
		return "";
	}

	public ArrayList<String> formate(String from) {
		ArrayList<String> to = new ArrayList<String>();
		if(from == null || from.equals("null"))
			return to;
		String[] split = from.split(",");
		for (int i = 0; i < split.length; i++) {
			to.add(split[i]);
		}
		return to;
	}

	public String formate(ArrayList<String> from) {
		StringBuffer to = new StringBuffer();
		if (from == null) {
			return "";
		}
		if (from.size() == 0) {
			return "";
		}
		for (int i = 0; i < from.size(); i++) {
			to.append(from.get(i) + ",");
		}
		String subTo = to.substring(0, to.length() - 1);
		return subTo;
	}

	public boolean isCardId(String cardId) {
		return CarIdUtil.checkIDCard(cardId);
	}

	public int getIndex(String[] array, String element) {
		if (array == null)
			return -1;
		int length = array.length;
		for (int i = 0; i < length; i++) {
			if (array[i].equals(element))
				return i;
		}
		return -1;
	}

	public int getIndex(ArrayList<String> strings, String string) {
		if (strings == null)
			return -1;
		for (int i = 0; i < strings.size(); i++) {
			if (strings.get(i).equals(string))
				return i;
		}
		return -1;
	}

	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// ((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
		listView.setLayoutParams(params);
	}


	public void closeDialog(DialogInterface dialog) {
		try {
			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, true);
			dialog.dismiss();
		} catch (Exception e) {
		}
	}

	public void closeDialog(Dialog dialog) {
		try {
			Field field = dialog.getClass().getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, true);
			dialog.dismiss();
		} catch (Exception e) {
		}
	}

	public void donnotCloseDialog(DialogInterface dialog) {
		try {
			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, false);
			dialog.dismiss();
		} catch (Exception e) {
		}
	}

	public void donnotCloseDialog(Dialog dialog) {
		try {
			Field field = dialog.getClass().getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, false);
			dialog.dismiss();
		} catch (Exception e) {
		}
	}

	public String formateDate(String time) {
		long serverTime = CurstomUtils.getInstance().getServerTime(time);
		Date date = new Date(serverTime);
		return DateFormat.format("yyyy-MM-dd", date).toString();
	}

	public String formateTime(String time) {
		long serverTime = CurstomUtils.getInstance().getServerTime(time);
		Date date = new Date(serverTime);
		Date nowData = new Date();
		long nowTime = nowData.getTime();
		long l = nowTime - serverTime;
		if (l >= 0 && l < 60 * 1000) {

			return "now time";

		} else if (l >= 60 * 1000 && l < 60 * 60 * 1000) {

			int minutes = (int) (l / (60 * 1000));

			return minutes + "minints ago";

		} else if (l >= 60 * 60 * 1000 && l < 24 * 60 * 60 * 1000) {

			int hour = (int) (l / (60 * 60 * 1000));

			return hour + "hours ago";

		} else if (l >= 24 * 60 * 60 * 1000 && l < 2 * 24 * 60 * 60 * 1000) {

			return "yestoday";

		} else {

			return DateFormat.format("yyyy-MM-dd  HH:mm", date).toString();
		}
	}

	public boolean isRunning(Context context, String packageName) {
		boolean isAppRunning = false;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		@SuppressWarnings("deprecation")
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(packageName)
					|| info.baseActivity.getPackageName().equals(packageName)) {
				isAppRunning = true;
				break;
			}
		}
		return isAppRunning;
	}

	@SuppressWarnings("static-access")
	public void hideKeyBoard(Activity context) {
		if (context == null)
			return;
		boolean imeShow = isImeShow(context);
		if (imeShow) {
			View view = context.getWindow().peekDecorView();
			if (view != null && view.getWindowToken() != null) {
				InputMethodManager imm = (InputMethodManager) context
						.getSystemService(context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}
		}
	}

	@SuppressWarnings("static-access")
	public boolean isImeShow(Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(context.INPUT_METHOD_SERVICE);
		return imm.isActive();
	}

	@SuppressWarnings("static-access")
	public void showKeyBoard(Activity context) {
		if (context == null)
			return;
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public void showGuideImg(Activity context, String path, final String argument,
			final boolean hasNext, final int[] nextRes) {
		final SharedPreferences sp = context.getSharedPreferences(path, Context.MODE_PRIVATE);
		boolean fristGuide = sp.getBoolean(argument, true);
		if (fristGuide) {
			final ImageView imgView = new ImageView(context.getApplicationContext());
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
				lp.topMargin = new SystemBarTintManager(context).getConfig().getStatusBarHeight();
			}
			imgView.setLayoutParams(lp);
			imgView.setImageResource(nextRes[0]);
			imgView.setScaleType(ScaleType.FIT_XY);
			final ViewGroup view = ((ViewGroup) context.getWindow().getDecorView().findViewById(android.R.id.content));
			view.addView(imgView);
			imgView.setOnClickListener(new OnClickListener() {
				int i = 0;
				@Override
				public void onClick(View v) {
					i++;
					if(i == nextRes.length){
						sp.edit().putBoolean(argument, false).commit();
						view.removeView(imgView);
						return;
					}
					imgView.setImageResource(nextRes[i]);
				}
			});
		}
	}

	@SuppressWarnings("deprecation")
	public BitmapDrawable setBtimpBackground(Activity context, int resourceID) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(resourceID);
		Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
		BitmapDrawable bd = new BitmapDrawable(context.getResources(), bm);
		return bd;
	}

	@SuppressWarnings("deprecation")
	public Bitmap setBackgroundBitmap(Activity context, int resourceID) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(resourceID);
		Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
		return bm;
	}

	public void setLimitDecimal(EditText mEditText, Editable s, int digit, int length) {
		int index = s.toString().indexOf(".");
		if (index < 0) {
			// editText.setFilters(new InputFilter[] { new
			// InputFilter.LengthFilter(length + 1) });
			if (s.length() > length) {
				s.delete(length, s.length());
			}
			return;
		}
		if (s.toString().length() - index - 1 > digit) {
			// s.delete(index + digit + 1, index + digit + 2);
			s.replace(mEditText.getSelectionStart() - 1, mEditText.getSelectionStart(), "");
		} else if (index == 0) {
			s.delete(index, index + 1);
		} else if (s.length() - digit - 1 > length) {
			s.replace(mEditText.getSelectionStart() - 1, mEditText.getSelectionStart(), "");
		}
	}

	public SpannableStringBuilder getPartRedString(String oldStr, String condition) {
		String old = oldStr.toLowerCase(Locale.getDefault());
		String con = condition.toLowerCase(Locale.getDefault());
		int start = old.indexOf(con);
		if (start == -1)
			return new SpannableStringBuilder(oldStr);
		int end = start + condition.length();
		SpannableStringBuilder style = new SpannableStringBuilder(oldStr);
		style.setSpan(new ForegroundColorSpan(Color.RED), start, end,
				Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		return style;
	}

	

	public int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	public int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}


	public String formateDateWithHours(String time) {
		long serverTime = CurstomUtils.getInstance().getServerTime(time);
		Date date = new Date(serverTime);
		return DateFormat.format("yyyy-MM-dd hh:mm", date).toString();
	}

	public int getExifOrientation(String filepath) {
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
