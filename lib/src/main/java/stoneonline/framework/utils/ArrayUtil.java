package stoneonline.framework.utils;

import java.util.List;

/**
 * Created by wengui on 2016/3/30.
 */
public class ArrayUtil {

    public static <T> T get(T[] array,int index){
        if(array == null)
            return null;
        int length = array.length;
        if(length <= index){
            return null;
        }
        return array[index];
    }

    public static int get(int[] array,int index){
        if(array == null)
            return -1;
        int length = array.length;
        if(length <= index){
            return -1;
        }
        return array[index];
    }

    public static <T> T get(List<T> list,int index){
        if(list == null)
            return null;
        int length = list.size();
        if(length <= index){
            return null;
        }
        return list.get(index);
    }
}
