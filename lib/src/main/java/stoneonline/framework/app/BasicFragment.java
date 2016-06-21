package stoneonline.framework.app;


import android.support.v4.app.Fragment;
import android.widget.Toast;

import stoneonline.framework.utils.T;

/**
 * Created by wengui on 2016/2/17.
 */
public abstract class BasicFragment extends Fragment {

    public BasicActivity getBasicActivity(){
        return (BasicActivity) getActivity();
    }

    public void toast(int i){
        if(isAdded()){
            T.show(getActivity(),String.valueOf(i), Toast.LENGTH_SHORT);
        }
    }

    public void toast(String s){
        if(isAdded()){
            T.show(getActivity(),s, Toast.LENGTH_SHORT);
        }
    }
}
