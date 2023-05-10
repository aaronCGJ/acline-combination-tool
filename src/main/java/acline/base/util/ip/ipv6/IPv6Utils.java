package acline.base.util.ip.ipv6;

import acline.base.util.ip.MaskEnum;
import acline.base.util.ip.ipv6.baseclass.IPv6Network;

import java.math.BigDecimal;

import static acline.base.util.ip.ipv4.IPv4Utils.appendIpFullName;

/**
 * @author Aaron Chen
 */
public class IPv6Utils {

    /**
     * 获取获取IP段中，网关IP 的长整型数值
     *
     * @param ipAddr
     * @return
     * @throws Exception
     */
    public static BigDecimal getGatewayIpToLong(String ipAddr, Integer ipMask) {
        if (MaskEnum.MASK_127.getMask().equals(ipMask) || MaskEnum.MASK_128.getMask().equals(ipMask)) {
            throw new RuntimeException("The Ip of mask bit 127 and 128 have no gateway address");
        }
        return new BigDecimal(IPv6Network.fromString(appendIpFullName(ipAddr, ipMask)).getFirst().toBigInteger());

    }
}
