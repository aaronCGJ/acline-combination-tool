package acline.base.util.model;/**
 * @author Aaron Chen
 * @date：2022/11/16 16:57
 * @Description:TODO
 * @Version 1.0
 */

/**
 * @author Aaron Chen
 */
public interface BizExceptionEnumInterface {
    /**
     * 返回错误代码
     *
     * @return
     */
    String getCode();

    /**
     * 返回错误信息
     *
     * @return
     */
    String getMsg();

    /**
     * 返回证书的结果值
     *
     * @return
     */
    int getRet();

}
