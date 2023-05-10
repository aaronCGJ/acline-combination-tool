package acline.base.util.ip.ipv4;

import acline.base.util.common.StringUtils;
import acline.base.util.ip.MaskEnum;
import acline.base.util.ip.ipformat.IpConstant;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.HexUtil;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;



/**
 * ipV4基本工具类
 *
 */
public class IPv4Utils {

    public final static String OBLIQUE_LINE = "/";

    public static String getIpHexStr(String ip) {
        String hexStr = HexUtil.toHex(getIpLongFromSeg(getIpAddr(ip)));
        if (MaskEnum.MASK_32.getMask() > hexStr.length()) {
            for (int i = hexStr.length(); i < MaskEnum.MASK_32.getMask(); i++) {
                hexStr = "0" + hexStr;
            }
        }
        return hexStr;
    }

    public static String getIpAssignmentsAddr(String ip, Integer mask) {
        return getIpHexStr(ip) + "/" + (mask - MaskEnum.MASK_32.getMask() + 128);
    }

    public static String getIpAssignmentsV6Addr(String ip, Integer mask) {
        ip = ip.trim().replaceAll(" ", "");
        String end = "/128";
        if (ip.indexOf("/") > 0) {
            int index = ip.indexOf("/");
            String newip = ip.substring(0, index);
            int Num = Integer.parseInt(ip.substring(index + 1));
            int b = 128 - (32 - Num);
            end = "/" + b;
            if (Num == 24) {
                int i = newip.lastIndexOf(".");
                newip = newip.substring(0, i) + ".0";
            }
            ip = newip;
        }
        String head = "000000000000000000000000";
        String[] h = ip.split("[.]");
        String hexIP = "";
        for (int i = 0; i < h.length; i++) {
            String hex = Integer.toHexString(Integer.parseInt(h[i]));
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            hexIP += hex;
        }
        String ipHex = head + hexIP + end;
        return ipHex;
    }

    public static String getIpAssignmentsAddr(String ipFullName) {
        return getIpAssignmentsAddr(getIpAddr(ipFullName), getIpMask(ipFullName));
    }

    /**
     * 获取IP段中，网关IP的地址
     * @param ipSegment
     * @return
     * @throws Exception
     */
    public static String getGatewayIpToAddress(String ipSegment) {
        return getIpFromLong(getGatewayIpToLong(ipSegment));
    }

    /**
     * 获取获取IP段中，网关IP 的长整型数值
     * @param ipSegment
     * @return
     * @throws Exception
     */
    public static Long getGatewayIpToLong(String ipSegment) {
        String ipAddr = getIpAddr(ipSegment);
        Integer mask = getIpMask(ipSegment);
        if (MaskEnum.MASK_31.getMask().equals(mask) || MaskEnum.MASK_32.getMask().equals(mask)) {
            throw new RuntimeException("The Ip of mask bit 31 and 32 have no gateway address");
        }
        return getBeginIpLong(ipAddr, mask) + 1;
    }

//    /**
//     *  分页拆分 IP段
//     * @param rootIpAddr    源IP
//     * @param rootMask      源Ip掩码位
//     * @param targetMask    拆到的掩码位
//
//     * @return
//     */
//    public static List<T> splitIpSegment(String rootIpAddr, Integer rootMask, Integer targetMask) {
//        if (rootMask > targetMask) {
//            return null;
//        }
//        Integer total = 1 << (targetMask - rootMask);
//
//        // 最后一页处理，默认返回最后一页
//
//        // 源ip段，网络位，开始位
//        Long beginIpLong = getBeginIpLong(rootIpAddr, rootMask);
//        List<T> ipList = new ArrayList<T>();
//            SplitIpDTO dto = new SplitIpDTO();
//            String ipAddr = getResultIpSegment(beginIpLong, i, targetMask);
//            if (MaskEnum.MASK_32.getMask().equals(targetMask)) {
//                dto.setIpFullName(ipAddr);
//            } else {
//                dto.setIpFullName(appendIpFullName(ipAddr, targetMask));
//            }
//            dto.setIpAddr(ipAddr);
//            dto.setIpMask(targetMask);
//            ipList.add(dto);
//
//        return ipList;
//    }

    private static String getResultIpSegment(long beginIpLong, long index, Integer mask) {
        int count = getIpCount(mask);
        return getIpFromLong(beginIpLong + (index * count));
    }

    /**
     * 拆分Ip段
     * @param rootIpSeg     源IP段
     * @param targetMask    拆到的掩码位
     * @return
     */
    public static LinkedList<String> getIpSegmentSplitList(String rootIpSeg, Integer targetMask) {
        String rootIpAddr = getIpAddr(rootIpSeg);
        Integer rootMask = getIpMask(rootIpSeg);
        return getIpSegmentSplitList(rootIpAddr, rootMask, targetMask);
    }

    /**
     * 拆分Ip段
     * @param rootIp    源Ip网络位
     * @param rootMask  源掩码位
     * @param targetMask 拆到的掩码位
     * @return
     */
    public static LinkedList<String> getIpSegmentSplitList(String rootIp, Integer rootMask, Integer targetMask) {
        if (targetMask < rootMask) {
            return null;
        }
        Integer total = 1 << (targetMask - rootMask);
        Long beginIpLong = getBeginIpLong(rootIp, rootMask);
        int count = getIpCount(targetMask);

        LinkedList<String> ipList = new LinkedList<>();
        for (int i = 0; i < total; i++) {
            String ip = getIpFromLong(beginIpLong + ((i) * count));
            if (MaskEnum.MASK_32.getMask().equals(targetMask)) {
                ipList.add(ip);
            } else {
                ipList.add(ip + "/" + targetMask);
            }
        }
        return ipList;
    }

    /**
     *  返回 parentIps 中，除去childrenIps 的Ip列表. 两个列表Ip的掩码 都必须一致
     * @param parentIps
     * @param childrenIps
     * @return
     */
    public static LinkedList<String> cutSameMaskIpSegment(LinkedList<String> parentIps, LinkedList<String> childrenIps) {
        LinkedList<String> cutResult = new LinkedList<>();
        if (CollectionUtil.isEmpty(childrenIps)) {
            return parentIps;
        }
        if (CollectionUtil.isNotEmpty(parentIps)) {
            for (String parentIp : parentIps) {
                boolean flag = true;
                for (String childrenIp : childrenIps) {
                    if (parentIp.equals(childrenIp)) {
                        flag = false;
                    }
                }
                if (flag) {
                    cutResult.addLast(parentIp);
                }
            }
            return cutResult;
        }
        return new LinkedList<>();
    }

    /**
     * 用 otherIps 切割 rootIpSeg 段, 并合并切割结果的IP段
     * @param rootIpSeg
     * @param otherIps
     * @return
     */
    public static List<String> splitAndMergeIpSegmentFromOtherSegment(String rootIpSeg, List<String> otherIps) {
        List<List<String>> lists = splitIpSegmentFromOtherSegment(rootIpSeg, otherIps);

        List<String> resIp = Lists.newArrayList();
        for (List<String> list : lists) {
            if (CollectionUtil.isNotEmpty(list)) {
                resIp.addAll(list);
            }
        }
        return mergeIps(resIp, IPv4Utils.getIpMask(rootIpSeg));
    }

    /**
     * 用 otherIps 切割 rootIpSeg 段, 返回 两个Ip列表。不合并
     * @param ipSegment
     * @param otherIps
     * @return
     */
    public static List<List<String>> splitIpSegmentFromOtherSegment(String ipSegment, List<String> otherIps) {

        // 校验掩码 都一样吗
        Integer checkMask = null;
        for (String ipSeg : otherIps) {
            Integer mask = IPv4Utils.getIpMask(ipSeg);
            if (null != checkMask) {
                if (!checkMask.equals(mask)) {
                    throw new RuntimeException("Exception: IP list net mask must be same");
                }
            } else {
                checkMask = mask;
            }
        }

        CollectionUtil.sort(otherIps, Comparator.comparing(IPv4Utils::getIpLongFromSeg));
        List<List<String>> result = Lists.newArrayListWithCapacity(2);

        LinkedList<String> beSplitUpPri = Lists.newLinkedList();
        LinkedList<String> beSplitUpSec = Lists.newLinkedList();
        LinkedList<String> splitIps = Lists.newLinkedList(otherIps);

        // 拆分根Ip
        LinkedList<String> rootAllSplitIps = getIpSegmentSplitList(ipSegment, checkMask);

        // 把拆分源IP分成 2-3段
        if (rootAllSplitIps.getFirst().equals(splitIps.getFirst())) {
            int index = rootAllSplitIps.indexOf(splitIps.getLast());
            for (int i = index + 1; i < rootAllSplitIps.size(); i++) {
                beSplitUpPri.add(rootAllSplitIps.get(i));
            }
        } else if (rootAllSplitIps.getLast().equals(splitIps.getLast())) {
            int index = rootAllSplitIps.indexOf(splitIps.getFirst());
            for (int i = 0; i < index; i++) {
                beSplitUpPri.add(rootAllSplitIps.get(i));
            }
        } else {
            int indexFirst = rootAllSplitIps.indexOf(splitIps.getFirst());
            for (int i = 0; i < indexFirst; i++) {
                beSplitUpPri.add(rootAllSplitIps.get(i));
            }
            int indexLast = rootAllSplitIps.indexOf(splitIps.getLast());
            for (int i = indexLast + 1; i < rootAllSplitIps.size(); i++) {
                beSplitUpSec.add(rootAllSplitIps.get(i));
            }
        }

        result.add(beSplitUpPri);
        result.add(beSplitUpSec);
        return result;

    }

    /**
     * 根据 IP段,获取起 网络位的 long数值
     * @param ipSegment
     * @return
     */
    public static Long getIpLongFromSeg(String ipSegment) {
        return IPv4Utils.getIpFromString(IPv4Utils.getIpAddr(ipSegment));
    }

    /**
     * 合并 掩码位一样的 Ip段列表
     * @param sourceIps  源Ip段列表，一定是掩码 一样的 的Ip段
     * @param targetMask 要合并成的目标掩码位
     * @return
     */
    public static List<String> mergeIps(List<String> sourceIps, Integer targetMask) {
        // 合并Ip列表为空，则返回空列表
        if (CollectionUtil.isEmpty(sourceIps)) {
            return Lists.newArrayList();
        }
        // 防止 传参未排序, 重新升序排序
        CollectionUtil.sort(sourceIps, Comparator.comparing(IPv4Utils::getIpLongFromSeg));
        // 获取第一个元素的 ip掩码位 (所有元素的IP掩码都是一样的)
        Integer sourceMask = getIpMask(sourceIps.get(0));
        // 新建一个链表, 存放合并结果
        LinkedList<String> mergeIpList = Lists.newLinkedList();
        // 遍历
        for (int i = 0; i < sourceIps.size(); i++) {
            // 获取元素
            String ipSeg = sourceIps.get(i);
            // 获取IP 网络位
            String ipAddr = getIpAddr(ipSeg);
            // 获取IP 掩码位
            Integer ipMask = getIpMask(ipSeg);
            // 校验 掩码是否都一致, 不一样就返回空列表
            if (!Objects.equals(ipMask, sourceMask)) {
                return Lists.newArrayList();
            }
            // 开始合并，从范围最大的掩码，往 小范围的掩码 方向，逐次尝试合并
            for (int j = targetMask; j <= sourceMask; j++) {
                // 计算出 一个目标掩码位IP段，需要多少个 源掩码IP段。
                int count = 1 << (sourceMask - j);
                // 待合并的Ip 剩余列表大小，小于 count, 则此掩码合并失败
                if (sourceIps.size() - i < count) {
                    continue;
                }
                // 计算ipAddr 在目标掩码位上的 网络位IP
                String beginIpStr = getBeginIpStr(ipAddr, j);
                // 若 ipAddr 不为 网络位IP, 则退出此次合并
                if (!ipAddr.equals(beginIpStr)) {
                    continue;
                }
                // 合并段, begin已经匹配到, 获取合并段结尾IP
                int endIndex = i + count - 1;
                String endIpSeg = sourceIps.get(endIndex);
                // 校验结尾
                if (!Objects.equals(getEndIpLong(ipAddr, j), getEndIpLong(getIpAddr(endIpSeg), getIpMask(endIpSeg)))) {
                    continue;
                }
                // 所有校验通过, 合并成功结构, 放出 List
                mergeIpList.add(beginIpStr + "/" + j);
                // sourceIps 遍历，跳过count 个元素。
                i += (count - 1);
                break;
            }
        }
        return mergeIpList;
    }

    /**
     * 把一段任意掩码位的Ip段，尝试进行合并，到目标掩码位
     * @param sourceIps
     * @param targetMask
     * @return
     */
    public static List<String> mergeAnyMaskIpList(List<String> sourceIps, Integer targetMask) {
        if (null == targetMask) {
            targetMask = MaskEnum.MASK_1.getMask();
        }
        // 获取最小范围的 掩码号
        Integer minRangeMask = 0;
        for (String ipSeg : sourceIps) {
            Integer mask = Optional.ofNullable(IPv4Utils.getIpMask(ipSeg)).orElse(MaskEnum.MASK_32.getMask());
            if (mask.compareTo(minRangeMask) > 0) {
                minRangeMask = mask;
            }
        }
        // 先拆分
        LinkedList<String> allSplitIp = new LinkedList<>();
        for (String ipSeg : sourceIps) {
            LinkedList<String> splitList = getIpSegmentSplitList(ipSeg, minRangeMask);
            if (CollectionUtil.isNotEmpty(splitList)) {
                allSplitIp.addAll(splitList);
            }
        }
        // 排序
        CollectionUtil.sort(allSplitIp, Comparator.comparing(IPv4Utils::getIpLongFromSeg));
        // 再合并
        return mergeIps(allSplitIp, targetMask);
    }

    /**
     * 获取ip段的 ip地址
     * @param ipSeg
     * @return
     */
    public static String getIpAddr(String ipSeg) {
        if (StringUtils.isBlank(ipSeg)) {
            return "";
        }
        if (!ipSeg.contains(OBLIQUE_LINE)) {
            return ipSeg;
        }
        String ipAddr = ipSeg.substring(0, ipSeg.indexOf(OBLIQUE_LINE));
        return isIP(ipAddr) ? ipAddr : "";
    }

    /**
     * 获取ip段的 掩码位
     * @param ipSeg
     * @return
     */
    public static Integer getIpMask(String ipSeg) {
        if (StringUtils.isBlank(ipSeg)) {
            return 0;
        }
        if (!ipSeg.contains(OBLIQUE_LINE)) {
            return MaskEnum.MASK_32.getMask();
        }
        String maskStr = ipSeg.substring(ipSeg.indexOf(OBLIQUE_LINE) + 1);
        try {
            return Integer.parseInt(maskStr);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 功能：判断一个IP是不是在一个网段下的
     * 格式：isInRange("192.168.8.3", "192.168.9.10/22");
     */
    public static boolean isInRange(String ip, String cidr) {
        String[] ips = ip.split("\\.");
        int ipAddr = (Integer.parseInt(ips[0]) << 24) | (Integer.parseInt(ips[1]) << 16) | (Integer.parseInt(ips[2]) << 8) | Integer
                .parseInt(ips[3]);
        int type = Integer.parseInt(cidr.replaceAll(".*/", ""));
        int mask = 0xFFFFFFFF << (32 - type);
        String cidrIp = cidr.replaceAll("/.*", "");
        String[] cidrIps = cidrIp.split("\\.");
        int cidrIpAddr = (Integer.parseInt(cidrIps[0]) << 24) | (Integer.parseInt(cidrIps[1]) << 16) | (Integer.parseInt(cidrIps[2]) << 8)
                | Integer.parseInt(cidrIps[3]);
        return (ipAddr & mask) == (cidrIpAddr & mask);
    }

    /**
     * smallerSeg  段是否在 largerSeg 段内
     * @param smallerSeg
     * @param largerSeg
     * @return
     */
    public static boolean ipSegmentIsInRange(String smallerSeg, String largerSeg) {
        if (getIpMask(smallerSeg) < getIpMask(largerSeg)) {
            return false;
        }
        String beginIpStr = getBeginIpStr(getIpAddr(smallerSeg), getIpMask(largerSeg));
        return beginIpStr.equals(getIpAddr(largerSeg));
    }

    /**
     * 功能：根据IP和位数返回该IP网段的所有IP
     * <br>
     * 格式：parseIpMaskRange("192.192.192.1.", "23")
     */
    public static List<String> parseIpMaskRange(String ip, Integer mask) {
        List<String> list = new ArrayList<>();
        if ("32".equals(mask)) {
            list.add(ip);
        } else {
            String startIp = getBeginIpStr(ip, mask);
            String endIp = getEndIpStr(ip, mask);
            if (!"31".equals(mask)) {
                String subStart = startIp.split("\\.")[0] + "." + startIp.split("\\.")[1] + "." + startIp.split("\\.")[2] + ".";
                String subEnd = endIp.split("\\.")[0] + "." + endIp.split("\\.")[1] + "." + endIp.split("\\.")[2] + ".";
                startIp = subStart + (Integer.parseInt(startIp.split("\\.")[3]) + 1);
                endIp = subEnd + (Integer.parseInt(endIp.split("\\.")[3]) - 1);
            }
            list = parseIpRange(startIp, endIp);
        }
        return list;
    }

    /**
     * 功能：根据掩码位返回 IP段内的Ip总数
     * <br>
     * 格式：parseIpMaskRange("192.192.192.1", "23")
     */
    public static int getIpCount(Integer mask) {
        if (null == mask) {
            return 0;
        }
        //IP总数，去小数点
        return BigDecimal.valueOf(Math.pow(2, 32 - mask)).setScale(0, BigDecimal.ROUND_DOWN).intValue();
    }

    /**
     * 功能：根据位数返回IP总数
     * 格式：isIP("192.192.192.1")
     */
    public static boolean isIP(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(str).matches();
    }

    /**
     *  获取起止IP内的，所有ip地址
     * @param ipfrom
     * @param ipto
     * @return
     */
    public static List<String> parseIpRange(String ipfrom, String ipto) {
        List<String> ips = new ArrayList<String>();
        String[] ipfromd = ipfrom.split("\\.");
        String[] iptod = ipto.split("\\.");
        int[] int_ipf = new int[4];
        int[] int_ipt = new int[4];
        for (int i = 0; i < 4; i++) {
            int_ipf[i] = Integer.parseInt(ipfromd[i]);
            int_ipt[i] = Integer.parseInt(iptod[i]);
        }
        for (int A = int_ipf[0]; A <= int_ipt[0]; A++) {
            for (int B = (A == int_ipf[0] ? int_ipf[1] : 0); B <= (A == int_ipt[0] ? int_ipt[1] : 255); B++) {
                for (int C = (B == int_ipf[1] ? int_ipf[2] : 0); C <= (B == int_ipt[1] ? int_ipt[2] : 255); C++) {
                    for (int D = (C == int_ipf[2] ? int_ipf[3] : 0); D <= (C == int_ipt[2] ? int_ipt[3] : 255); D++) {
                        ips.add(A + "." + B + "." + C + "." + D);
                    }
                }
            }
        }
        return ips;
    }

    /**
     * 把long类型的Ip转为一般Ip类型：xx.xx.xx.xx
     * @param ip
     * @return
     */
    public static String getIpFromLong(Long ip) {
        String s1 = String.valueOf((ip & 4278190080L) / 16777216L);
        String s2 = String.valueOf((ip & 16711680L) / 65536L);
        String s3 = String.valueOf((ip & 65280L) / 256L);
        String s4 = String.valueOf(ip & 255L);
        return s1 + "." + s2 + "." + s3 + "." + s4;
    }

    /**
     * 把xx.xx.xx.xx类型的转为long类型的
     * @param ip
     * @return
     */
    public static Long getIpFromString(String ip) {
        Long ipLong = 0L;
        String ipTemp = ip;
        ipLong = ipLong * 256 + Long.parseLong(ipTemp.substring(0, ipTemp.indexOf('.')));
        ipTemp = ipTemp.substring(ipTemp.indexOf('.') + 1, ipTemp.length());
        ipLong = ipLong * 256 + Long.parseLong(ipTemp.substring(0, ipTemp.indexOf('.')));
        ipTemp = ipTemp.substring(ipTemp.indexOf(".") + 1, ipTemp.length());
        ipLong = ipLong * 256 + Long.parseLong(ipTemp.substring(0, ipTemp.indexOf('.')));
        ipTemp = ipTemp.substring(ipTemp.indexOf('.') + 1, ipTemp.length());
        ipLong = ipLong * 256 + Long.parseLong(ipTemp);
        return ipLong;
    }

    /**
     * 根据掩码位获取掩码
     *
     * @param maskBit 掩码位数，如"28"、"30"
     * @return 如  255.255.255.0
     */
    public static String getMaskByMaskBit(Integer maskBit) {
        return Objects.isNull(maskBit) ? "error, maskBit is null !" : getMaskMap(maskBit);
    }

    /**
     * 根据 ip/掩码位 计算IP段的起始IP 如 IP串 218.240.38.69/30
     *
     * @param ip 给定的IP，如218.240.38.69
     * @param maskBit 给定的掩码位，如30
     * @return 起始IP的字符串表示
     */
    public static String getBeginIpStr(String ip, Integer maskBit) {
        return getIpFromLong(getBeginIpLong(ip, maskBit));
    }

    public static String getBeginIpStr(String ipSegment) {
        return getIpFromLong(getBeginIpLong(getIpAddr(ipSegment), getIpMask(ipSegment)));
    }

    /**
     * 根据 ip/掩码位 计算IP段的起始IP 如 IP串 218.240.38.69/30
     *
     * @param ip 给定的IP，如218.240.38.69
     * @param maskBit 给定的掩码位，如30
     * @return 起始IP的长整型表示
     */
    public static Long getBeginIpLong(String ip, Integer maskBit) {
        return getIpFromString(ip) & getIpFromString(getMaskByMaskBit(maskBit));
    }

    /**
     * 获取Ip段的，首个Ip的长整型数值
     * @param ipFullName ip地址/掩码
     * @return
     */
    public static Long getBeginIpLong(String ipFullName) {
        return getIpFromString(getIpAddr(ipFullName)) & getIpFromString(getMaskByMaskBit(getIpMask(ipFullName)));
    }

    /**
     * 根据 ip/掩码位 计算IP段的终止IP 如 IP串 218.240.38.69/30
     *
     * @param ip 给定的IP，如218.240.38.69
     * @param maskBit  给定的掩码位，如30
     * @return 终止IP的字符串表示
     */
    public static String getEndIpStr(String ip, Integer maskBit) {
        return getIpFromLong(getEndIpLong(ip, maskBit));
    }

    /**
     * 根据 ip/掩码位 计算IP段的终止IP 如 IP串 218.240.38.69/30
     *
     * @param ip 给定的IP，如218.240.38.69
     * @param maskBit 给定的掩码位，如30
     * @return 终止IP的长整型表示
     */
    public static Long getEndIpLong(String ip, Integer maskBit) {
        return getBeginIpLong(ip, maskBit) + getIpCount(maskBit) - 1;
    }

    /**
     *
     * @param ipFullName ip地址/掩码
     * @return
     */
    public static Long getEndIpLong(String ipFullName) {
        return getBeginIpLong(getIpAddr(ipFullName), getIpMask(ipFullName)) + getIpCount(getIpMask(ipFullName)) - 1;
    }

    /**
     * 根据子网掩码转换为掩码位 如 255.255.255.252转换为掩码位 为 30
     * @param netmarks
     * @return
     */
    public static int getNetMask(String netmarks) {
        StringBuilder sbf;
        String str;
        int inetmask = 0;
        int count = 0;
        String[] ipList = netmarks.split("\\.");
        for (int n = 0; n < ipList.length; n++) {
            sbf = toBin(Integer.parseInt(ipList[n]));
            str = sbf.reverse().toString();
            count = 0;
            for (int i = 0; i < str.length(); i++) {
                i = str.indexOf('1', i);
                if (i == -1) {
                    break;
                }
                count++;
            }
            inetmask += count;
        }
        return inetmask;
    }

    private static StringBuilder toBin(int x) {
        StringBuilder result = new StringBuilder();
        result.append(x % 2);
        x /= 2;
        while (x > 0) {
            result.append(x % 2);
            x /= 2;
        }
        return result;
    }

    /**
     * 根据掩码位，返回子网掩码Ip
     * @param maskBit
     * @return
     */
    public static String getMaskMap(Integer maskBit) {
        if (MaskEnum.MASK_1.getMask().equals(maskBit)) {
            return "128.0.0.0";
        }
        if (MaskEnum.MASK_2.getMask().equals(maskBit)) {
            return "192.0.0.0";
        }
        if (MaskEnum.MASK_3.getMask().equals(maskBit)) {
            return "224.0.0.0";
        }
        if (MaskEnum.MASK_4.getMask().equals(maskBit)) {
            return "240.0.0.0";
        }
        if (MaskEnum.MASK_5.getMask().equals(maskBit)) {
            return "248.0.0.0";
        }
        if (MaskEnum.MASK_6.getMask().equals(maskBit)) {
            return "252.0.0.0";
        }
        if (MaskEnum.MASK_7.getMask().equals(maskBit)) {
            return "254.0.0.0";
        }
        if (MaskEnum.MASK_8.getMask().equals(maskBit)) {
            return "255.0.0.0";
        }
        if (MaskEnum.MASK_9.getMask().equals(maskBit)) {
            return "255.128.0.0";
        }
        if (MaskEnum.MASK_10.getMask().equals(maskBit)) {
            return "255.192.0.0";
        }
        if (MaskEnum.MASK_11.getMask().equals(maskBit)) {
            return "255.224.0.0";
        }
        if (MaskEnum.MASK_12.getMask().equals(maskBit)) {
            return "255.240.0.0";
        }
        if (MaskEnum.MASK_13.getMask().equals(maskBit)) {
            return "255.248.0.0";
        }
        if (MaskEnum.MASK_14.getMask().equals(maskBit)) {
            return "255.252.0.0";
        }
        if (MaskEnum.MASK_15.getMask().equals(maskBit)) {
            return "255.254.0.0";
        }
        if (MaskEnum.MASK_16.getMask().equals(maskBit)) {
            return "255.255.0.0";
        }
        if (MaskEnum.MASK_17.getMask().equals(maskBit)) {
            return "255.255.128.0";
        }
        if (MaskEnum.MASK_18.getMask().equals(maskBit)) {
            return "255.255.192.0";
        }
        if (MaskEnum.MASK_19.getMask().equals(maskBit)) {
            return "255.255.224.0";
        }
        if (MaskEnum.MASK_20.getMask().equals(maskBit)) {
            return "255.255.240.0";
        }
        if (MaskEnum.MASK_21.getMask().equals(maskBit)) {
            return "255.255.248.0";
        }
        if (MaskEnum.MASK_22.getMask().equals(maskBit)) {
            return "255.255.252.0";
        }
        if (MaskEnum.MASK_23.getMask().equals(maskBit)) {
            return "255.255.254.0";
        }
        if (MaskEnum.MASK_24.getMask().equals(maskBit)) {
            return "255.255.255.0";
        }
        if (MaskEnum.MASK_25.getMask().equals(maskBit)) {
            return "255.255.255.128";
        }
        if (MaskEnum.MASK_26.getMask().equals(maskBit)) {
            return "255.255.255.192";
        }
        if (MaskEnum.MASK_27.getMask().equals(maskBit)) {
            return "255.255.255.224";
        }
        if (MaskEnum.MASK_28.getMask().equals(maskBit)) {
            return "255.255.255.240";
        }
        if (MaskEnum.MASK_29.getMask().equals(maskBit)) {
            return "255.255.255.248";
        }
        if (MaskEnum.MASK_30.getMask().equals(maskBit)) {
            return "255.255.255.252";
        }
        if (MaskEnum.MASK_31.getMask().equals(maskBit)) {
            return "255.255.255.254";
        }
        if (MaskEnum.MASK_32.getMask().equals(maskBit)) {
            return "255.255.255.255";
        }
        return "-1";
    }

    /**
     * IPv4转成 浮点数值类型，此方法未使用过
     * @param ip
     * @return
     */
    public static double ipToDouble(String ip) {
        String[] arr = ip.split("\\.");
        double d1 = Double.parseDouble(arr[0]);
        double d2 = Double.parseDouble(arr[1]);
        double d3 = Double.parseDouble(arr[2]);
        double d4 = Double.parseDouble(arr[3]);
        return d1 * Math.pow(256, 3) + d2 * Math.pow(256, 2) + d3 * 256 + d4;
    }

    /**
     * 拼接出 带掩码位的 IP全名
     * @param ip
     * @param netMask
     * @return
     */
    public static String appendIpFullName(String ip, Integer netMask) {
        return ip + IpConstant.SEPARATOR_LINE_OBLIQUE + netMask;
    }

    /**
     * 使用小段子Ip列表，切割大段子Ip列表，返回切割的结果，并进行合并
     * @param smallerIps
     * @param largerIps
     * @return
     */
    public static List<String> cutAndMergeSegments(List<String> smallerIps, List<String> largerIps) {

        List<String> resultIps = Lists.newArrayList();
        for (String largerIp : largerIps) {
            for (String smallerIp : smallerIps) {
                if (ipSegmentIsInRange(smallerIp, largerIp)) {
                    resultIps.addAll(splitAndMergeIpSegmentFromOtherSegment(largerIp, Lists.newArrayList(smallerIp)));
                }
            }
        }
        return resultIps;
    }



    
}
