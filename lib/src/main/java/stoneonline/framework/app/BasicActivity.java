package stoneonline.framework.app;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import stoneonline.framework.utils.T;

/**
 * Created by wengui on 2016/2/17.
 */
public abstract class BasicActivity extends AppCompatActivity {

    public void toast(String content){
        T.show(this.getApplicationContext(),content, Toast.LENGTH_SHORT);
    }
}
