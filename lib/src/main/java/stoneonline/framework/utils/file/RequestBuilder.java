package stoneonline.framework.utils.file;

import java.util.Map;

/**
 * Created by wengui on 2016/2/24.
 */
public abstract class RequestBuilder {

    public static final int BUILD_DOWNLOAD = 0;
    public static final int BUILD_UPLOAD = 1;

    protected String url;

    protected Object tag;

    protected Map<String ,Object> params;

    public abstract int buildType();

    public abstract RequestBuilder params(Map<String ,Object> params);

    public abstract RequestBuilder tag(Object tag);

    public abstract RequestBuilder url(String url);

    public abstract RequestCall build();
}
