package acline.base.util.model;/**
 * @author Aaron Chen
 * @date：2022/11/16 17:04
 * @Description:TODO
 * @Version 1.0
 */

import java.io.Serializable;

/**
 * @author Aaron Chen
 * @date：2022/11/16 17:04
 * @Description:TODO
 */
public class BizException extends RuntimeException implements Serializable {

    private  static final long serialVersionUID=1L;

    private String exceptionCode;
    private Integer exceptionRet;


    public BizException(String exceptionCode, String exceptionMsg, Integer exceptionRet) {
        super(exceptionMsg);
        this.exceptionCode = exceptionCode;
        this.exceptionRet = exceptionRet;
    }

    public BizException(BizExceptionEnumInterface e) {
        this(e.getCode(), e.getMsg(), e.getRet());
    }

    public BizException(BizExceptionEnumInterface e, String msg) {
        this(e.getCode(), msg, e.getRet());
    }

    public String getExceptionCode() {
        return exceptionCode;
    }

    public Integer getExceptionRet() {
        return exceptionRet;
    }

}
