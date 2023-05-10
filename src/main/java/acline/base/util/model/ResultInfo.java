package acline.base.util.model;
/**
 * @author Aaron Chen
 * @date：2022/11/16 16:49
 * @Description:ResultInfo<T>
 * @Version 1.0
 */


import java.io.Serializable;

/**
 * @author Aaron Chen
 * @date：2022/11/16 16:49
 * @Description:ResultInfo
 */
public class ResultInfo<T> implements Serializable {
    private static final long serialVersionUID = 4369785922433783819L;

    private boolean success = false;


    private int ret = BaseBizExceptionEnum.UNEXPECTED_ERROR.getRet();
    private String code = BaseBizExceptionEnum.UNEXPECTED_ERROR.getCode();
    private String msg = BaseBizExceptionEnum.UNEXPECTED_ERROR.getMsg();
    private T data = null;

    /**
     *
     */
    public void copy(ResultInfo<T> other) {
        this.withSuccess(other.isSuccess()).withRet(other.getRet()).withCode(other.getCode()).withMsg(other.getMsg()).withData(
                other.getData());
    }


    public ResultInfo<T> succeed() {
        return succeed(null);
    }

    public ResultInfo<T> succeed(T data) {
        return succeed(data, "SUCCESS", "SUCCESS");
    }

    public ResultInfo<T> succeed(T data, String code, String msg) {
        this.success = true;
        this.code = code;
        this.ret = 0;
        this.msg = msg;
        this.data = data;
        return this;

    }

    public ResultInfo<T> withSuccess(boolean success) {
        this.success = success;
        if (success) {
            this.ret = 0;
        }
        return this;
    }


    /**
     * fail
     */


    public ResultInfo<T> fail(BizExceptionEnumInterface e) {
        this.success = false;
        this.ret = e.getRet();
        this.msg = e.getMsg();
        this.code = e.getCode();
        return this;
    }

    public ResultInfo<T> fail(BizException bizException) {
        this.success = false;
        this.code = bizException.getExceptionCode();
        this.msg = bizException.getLocalizedMessage();
        this.ret = bizException.getExceptionRet();
        return this;
    }

    public ResultInfo<T> fail(ResultInfo<T> other) {
        this.success = false;
        this.code = other.getCode();
        this.msg = other.getMsg();
        this.ret = other.getRet();
        return this;
    }

    public ResultInfo<T> fail(int ret, String code, String msg) {
        this.success = false;
        this.ret = ret;
        this.code = code;
        this.msg = msg;
        return this;
    }


    public ResultInfo<T> withRet(int ret) {
        this.ret = ret;
        return this;
    }

    public ResultInfo<T> withCode(String code) {
        this.code = code;
        return this;
    }

    public ResultInfo<T> withMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public ResultInfo<T> withData(T data) {
        this.data = data;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getRet() {
        return ret;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }


    @Override
    public String toString() {
        return "ResultInfo{" +
                "success=" + success +
                ", ret=" + ret +
                ", code='" + code +
                ", msg='" + msg +
                ", data=" + data +
                '}';
    }
}
