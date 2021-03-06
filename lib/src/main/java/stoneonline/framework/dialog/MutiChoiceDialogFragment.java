package stoneonline.framework.dialog;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import stoneonline.framework.defines.Selectable;
import stoneonline.framework.utils.ScreenUtils;
import stoneonline.framework.utils.ViewHolderUtils;
import www.stoneonline.com.lib.R;


/**
 * Created by wengui on 2016/4/27.
 */
public class MutiChoiceDialogFragment<T extends Parcelable> extends DialogFragment {
    private OnMutiChoiceListener mOnMutiChoiceListener;
    private ListView listView;
    private TextView tvTitle;
    private TextView tvConfirm;
    private SingleListAdapter mAdapter;

    public static class SimpleData implements Selectable {
        public String data;

        @Override
        public String getTitle() {
            return data;
        }

        public SimpleData(String data) {
            this.data = data;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.data);
        }

        public SimpleData() {
        }

        protected SimpleData(Parcel in) {
            this.data = in.readString();
        }

        public static final Creator<SimpleData> CREATOR = new Creator<SimpleData>() {
            @Override
            public SimpleData createFromParcel(Parcel source) {
                return new SimpleData(source);
            }

            @Override
            public SimpleData[] newArray(int size) {
                return new SimpleData[size];
            }
        };
    }

    public interface OnMutiChoiceListener<T> {
        void onItemChoice(boolean[] checks);
    }

    private ArrayList<T> conditions;
    private String title;
    private boolean[] checks;

    public MutiChoiceDialogFragment() {
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog_NoActionBar);
    }

    public static <T extends Parcelable> MutiChoiceDialogFragment newInstance(String title, ArrayList<T> conditions, boolean[] checks) {
        MutiChoiceDialogFragment<T> fragment = new MutiChoiceDialogFragment<>();
        Bundle bundle = new Bundle();
        bundle.putBooleanArray("CHECKS", checks);
        bundle.putString("TITLE", title);
        bundle.putParcelableArrayList("CONDITIONS", conditions);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static <T extends Parcelable> MutiChoiceDialogFragment newInstance(String title, String[] conditionsStrs, boolean[] checks) {
        ArrayList<SimpleData> list = new ArrayList<>();
        for (int i = 0; i < conditionsStrs.length; i++) {
            list.add(new SimpleData(conditionsStrs[i]));
        }
        MutiChoiceDialogFragment<T> fragment = new MutiChoiceDialogFragment<>();
        Bundle bundle = new Bundle();
        bundle.putBooleanArray("CHECKS", checks);
        bundle.putString("TITLE", title);
        bundle.putParcelableArrayList("CONDITIONS", list);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setOnMutiChoiceListener(OnMutiChoiceListener l) {
        this.mOnMutiChoiceListener = l;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("CONDITIONS", conditions);
        outState.putBooleanArray("CHECKS", checks);
        outState.putString("TITLE", title);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            conditions = savedInstanceState.getParcelableArrayList("CONDITIONS");
            checks = savedInstanceState.getBooleanArray("CHECKS");
            title = savedInstanceState.getString("TITLE");
        } else {
            Bundle arguments = getArguments();
            conditions = arguments.getParcelableArrayList("CONDITIONS");
            title = arguments.getString("TITLE");
            checks = arguments.getBooleanArray("CHECKS");
        }
        return inflater.inflate(R.layout.fragment_single_choice, null);
    }

    int maxHeight;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int screenHeight = ScreenUtils.getScreenHeight(view.getContext());
        maxHeight = screenHeight * 2 / 3;
        listView = (ListView) view.findViewById(R.id.list);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvConfirm = (TextView) view.findViewById(R.id.tvConfirm);
        tvTitle.setText(title);
        mAdapter = new SingleListAdapter();
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checks[position] = !checks[position];
                mAdapter.notifyDataSetChanged();
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnMutiChoiceListener != null) {
                    mOnMutiChoiceListener.onItemChoice(checks);
                }
                dismiss();
            }
        });

        int totalHeight = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View listItem = mAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight > maxHeight ? maxHeight : totalHeight;
        listView.setLayoutParams(params);
        final View topShadow = view.findViewById(R.id.topShadow);
        final View bottomShadow = view.findViewById(R.id.bottomShadow);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (listView.getChildCount() > 0) {
                    if (firstVisibleItem == 0 && listView.getChildAt(0).getTop() == 0) {
                        topShadow.setVisibility(View.GONE);
                    } else {
                        topShadow.setVisibility(View.VISIBLE);
                    }

                    if (firstVisibleItem + visibleItemCount == totalItemCount) {
                        if (listView.getChildAt(listView.getChildCount() - 1).getBottom() == listView.getHeight()) {
                            bottomShadow.setVisibility(View.GONE);
                        }
                    } else {
                        bottomShadow.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }


    class SingleListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return conditions.size();
        }

        @Override
        public T getItem(int position) {
            return conditions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_muti_choice, null);
            }
            T item = getItem(position);
            View ivItem = ViewHolderUtils.get(convertView, R.id.ivItem);
            TextView tvItem = ViewHolderUtils.get(convertView, R.id.tvItem);
            ivItem.setSelected(checks[position]);
            if (item instanceof Selectable) {
                tvItem.setText(((Selectable) item).getTitle());
            } else {
                tvItem.setText(item.toString());
            }
            return convertView;
        }
    }
}
