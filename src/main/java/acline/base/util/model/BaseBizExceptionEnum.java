package acline.base.util.model;

/**
 * @author Aaron Chen
 * @date：2022/11/16 16:59
 * @Description:
 */
public enum BaseBizExceptionEnum implements BizExceptionEnumInterface {
    UNEXPECTED_ERROR(1, "UNEXPECTED_ERROR", "UNEXPECTED_ERROR"),
    ;

    private String code;
    private String msg;
    private int ret;

    BaseBizExceptionEnum(int ret, String code, String msg) {

        this.code = code;
        this.msg = msg;
        this.ret = ret;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public int getRet() {
        return ret;
    }
}
