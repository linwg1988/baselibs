package stoneonline.framework.http;

import java.util.HashMap;
import java.util.Map;

import stoneonline.framework.utils.HttpUtils;

/**
 * Created by wengui on 2016/4/5.
 */
public class HttpApi implements Api {
    protected ParameterCreator creator;

    private HttpApi(){
    }

    private static class ApiHolder{
        private static HttpApi api = new HttpApi();
    }

    public static HttpApi getInstance(){
        return ApiHolder.api;
    }

    public void setParameterCreator(ParameterCreator creator) {
        this.creator = creator;
    }

    @Override
    public void doRequest(HttpRequest request,HttpListener listener) {
        Map<String, String> params = getParams();
        params.put("Data", request.params);
        HttpUtils.getUtil().doPost(request.requestUrl,params,new ResponseListenerImpl(listener,request));
    }

    protected Map<String,String> getParams(){
        if(creator == null){
            creator = new SimpleParameterCreator();
        }
        return creator.create();
    }

    public static class SimpleParameterCreator implements ParameterCreator{

        @Override
        public Map<String, String> create() {
            return new HashMap<>();
        }
    }
}
