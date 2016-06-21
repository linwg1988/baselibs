package stoneonline.framework.defines;

/**
 * Created by wengui on 2016/2/18.
 */
public class ErrorMessage {
    public int errCode;
    public String errMsg;

    public ErrorMessage() {
        this.errCode = -1;
        this.errMsg = "Network error";
    }

    public ErrorMessage(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
}
