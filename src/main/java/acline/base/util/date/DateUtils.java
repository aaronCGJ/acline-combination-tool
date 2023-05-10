package acline.base.util.date;

import acline.base.util.common.ArrayUtils;
import acline.base.util.date.fomart.DateConstant;
import cn.hutool.core.date.format.FastDateFormat;
import acline.base.util.common.StringUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static acline.base.util.date.BaseDateUtil.formatDate;


/**
 * 个性化扩展日期工具
 *
 * @author Aaron Chen
 */
public class DateUtils {

    /**
     * 时间分隔符获取时间数组
     * <p>
     *
     * @param dateSectionStr
     * @param separator
     * @return
     */
    public static Date[] splitSection(String dateSectionStr, String separator) {
        String[] dateSection = StringUtils.split(dateSectionStr, separator);
        if (ArrayUtils.isEmpty(dateSection)) {
            return null;
        }
        Date[] result = new Date[dateSection.length];
        for (int i = 0; i < dateSection.length; i++) {
            result[i] = BaseDateUtil.parse(dateSection[i]);
        }
        return result;
    }

    public static Date[] splitDate(String dateSectionStr, String separator) {
        return splitSection(dateSectionStr, separator);
    }

    /**
     * 获取当月开始时间戳
     *
     * @param timeStamp 毫秒级时间戳
     * @return
     */
    public static Long getSpecificMonthStartTime(Long timeStamp, TimeZone timeZone, Integer specAmount) {
        Calendar calendar = DateTime.getCalendarInstance(timeStamp, timeZone);
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.MONTH, specAmount);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取当月的结束时间戳
     *
     * @param timeStamp 毫秒级时间戳
     * @return
     */
    public static Long getSpecificMonthEndTime(Long timeStamp, TimeZone timeZone, Integer specAmount) {
        Calendar calendar = DateTime.getCalendarInstance(timeStamp, timeZone);
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.MONTH, specAmount);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    /**
     * String类型日期 转化成 Date类型
     *
     * @param date
     * @param formatString
     * @return
     */
    public static Date stringConvertDate(String date, String formatString, boolean exceptionReturnNow) {

        SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            if (exceptionReturnNow) {
                return new Date();
            }
        }
        return null;
    }

    /**
     * 根据时间戳获取当前时区日期 format：yyyy:MM:dd 00:00:00
     *
     * @param timeStamp
     * @return
     */
    public static Date getDayStartTime(Long timeStamp) {
        return getDayStartTime(timeStamp, TimeZone.getDefault());
    }

    /**
     * 根据时间戳获取指定时区日期 format： yyyy:MM:dd 00:00:00
     *
     * @param timeStamp
     * @param timeZone
     * @return
     */
    public static Date getDayStartTime(Long timeStamp, TimeZone timeZone) {
        Calendar calendar = DateTime.getCalendarInstance(timeStamp, timeZone);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 根据时间戳获取当前时区日期 format： yyyy:MM:dd 23:59:59
     *
     * @param timeStamp
     * @return
     */
    public static Date getDayEndTime(Long timeStamp) {
        return getDayEndTime(timeStamp, TimeZone.getDefault());
    }

    /**
     * 根据时间戳获取指定时区日期 format yyyy:MM:dd 23:59:59
     *
     * @param timeStamp
     * @param timeZone
     * @return
     */
    public static Date getDayEndTime(Long timeStamp, TimeZone timeZone) {
        Calendar calendar = DateTime.getCalendarInstance(timeStamp, timeZone);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 根据时间戳获取指定时区日期字符串 'yyyy:MM:dd 00:00:00'
     *
     * @param timeStamp
     * @param timeZone
     * @return
     */
    public static String getDayStartTimeStr(Long timeStamp, TimeZone timeZone) {
        FastDateFormat fdf = FastDateFormat.getInstance(DateConstant.DATETIME_FORMAT_PATTERN);
        Date dayStartTime = getDayStartTime(timeStamp, timeZone);
        return fdf.format(dayStartTime);
    }

    /**
     * 据时间戳获取指定时区日期字符串 'yyyy:MM:dd 23:59:59'
     *
     * @param timeStamp
     * @param timeZone
     * @return
     */
    public static String getDayEndTimeStr(Long timeStamp, TimeZone timeZone) {
        FastDateFormat fdf = FastDateFormat.getInstance(DateConstant.DATETIME_FORMAT_PATTERN);
        Date dayEndTime = getDayEndTime(timeStamp, timeZone);
        return fdf.format(dayEndTime);
    }


    /**
     * 时区 时间转换方法:将当前时间（可能为其他时区）转化成目标时区对应的时间
     *
     * @param sourceTime 时间格式必须为：yyyy-MM-dd HH:mm:ss
     * @param sourceId   入参的时间的时区id
     * @param targetId   要转换成目标时区id（一般是是零时区：取值UTC）
     * @return string 转化时区后的时间
     */
    public static String timeConvert(String sourceTime, String sourceId, String targetId) {
        // 校验入参是否合法
        if (null == sourceId || "".equals(sourceId) || null == targetId || "".equals(targetId) || null == sourceTime || "".equals(
                sourceTime)) {
            return "";
        }
        // 校验 时间格式必须为：yyyy-MM-dd HH:mm:ss
        String reg = "^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$";
        if (!sourceTime.matches(reg)) {
            return "";
        }

        try {
            // 时间格式
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 根据入参原时区id，获取对应的timezone对象
            TimeZone sourceTimeZone = TimeZone.getTimeZone(sourceId);
            // 设置SimpleDateFormat时区为原时区（否则是本地默认时区），目的:用来将字符串sourceTime转化成原时区对应的date对象
            df.setTimeZone(sourceTimeZone);
            // 将字符串sourceTime转化成原时区对应的date对象
            Date sourceDate = df.parse(sourceTime);

            // 开始转化时区：根据目标时区id设置目标TimeZone
            TimeZone targetTimeZone = TimeZone.getTimeZone(targetId);
            // 设置SimpleDateFormat时区为目标时区（否则是本地默认时区），目的:用来将字符串sourceTime转化成目标时区对应的date对象
            df.setTimeZone(targetTimeZone);
            // 得到目标时间字符串
            String targetTime = df.format(sourceDate);
            return targetTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";

    }

    /**
     * Gets the ID of this time zone.
     *
     * @param timeZoneOffset
     * @return the ID of this time zone.
     */
    public static String timeConvert(String timeZoneOffset) {
        String timeZoneOffsetStr = DateUtils.getTimeZoneStringByOffset(timeZoneOffset);
        if (!StringUtils.isBlank(timeZoneOffsetStr)) {
            return TimeZone.getTimeZone(timeZoneOffsetStr).getID();
        }
        return "";
    }

    /**
     * 用于根据偏移量计算时区zone
     *
     * @param minutes
     * @return
     */
    public static String getTimeZoneStringByOffset(String minutes) {
        if (StringUtils.isBlank(minutes)) {
            return null;
        }
        String sAdd = "";
        int intMinutes = DateUtils.parseInt(minutes) * -1;
        int hour = intMinutes / 60;
        if (hour >= 0) {
            sAdd = "+";
        } else {
            sAdd = "-";
        }
        hour = hour > 0 ? hour : -hour;
        String sHour = hour + "";
        if (sHour.length() == 1) {
            sHour = '0' + sHour;
        }
        double decimals = intMinutes % 60;
        // 显示为09:30分钟形式
        double decimalsZone = (decimals / 60);
        decimalsZone = decimalsZone < 0 ? -decimalsZone : decimalsZone;
        String sDecimalsZone = decimalsZone + "";
        sDecimalsZone = sDecimalsZone.substring(2);
        if (sDecimalsZone.length() == 1) {
            sDecimalsZone = sDecimalsZone + '0';
        } else if (sDecimalsZone.length() >= 3) {
            sDecimalsZone = sDecimalsZone.substring(0, 2);
        }
        return "GMT" + sAdd + sHour + ':' + sDecimalsZone;
    }


    public static int parseInt(Object num) {
        return parseInt(num, 0);
    }

    public static int parseInt(Object num, int defaultValue) {
        int ret = defaultValue;
        if (num == null) {
            return ret;
        }
        String val = num.toString().trim();
        if (val.equals("") || val.equals("null")) {
            return ret;
        }
        try {
            ret = new BigDecimal(val).intValue();
        } catch (Exception ex) {

            throw new RuntimeException("Parse {" + val + "} to Integer error: " + ex.getMessage());
        }

        return ret;
    }

    /**
     * 取当时一小时内的开始结束时间
     *
     * @return
     */
    public static Object[] getHour() {

        Object[] dates = new String[2];
        Date now = new Date();
        long n = now.getTime() - 3600 * 1000;
        Date begin = new Date();
        begin.setTime(n);
        dates[0] = formatDate(begin);
        dates[1] = formatDate(now);
        return dates;
    }

    /**
     * 24小时内时间信息
     *
     * @return
     */
    public static Object[] getDay() {

        Object[] dates = new String[2];
        Date now = new Date();
        long n = now.getTime() - 3600 * 1000 * 24;
        Date begin = new Date();
        begin.setTime(n);
        dates[0] = formatDate(begin);
        dates[1] = formatDate(now);
        return dates;
    }

    // 最近一周
    public static Object[] getWeek() {

        Object[] dates = new String[2];
        Date now = new Date();
        long n = now.getTime() - 3600 * 1000 * 24 * 7;
        Date begin = new Date();
        begin.setTime(n);
        dates[0] = formatDate(begin);
        dates[1] = formatDate(now);
        return dates;
    }

    // 最近n周
    public static Object[] getWeeks(int num) {

        Object[] dates = new String[2];
        Date now = new Date();
        long n = now.getTime() - 3600 * 1000 * 24 * 7 * num;
        Date begin = new Date();
        begin.setTime(n);
        dates[0] = formatDate(begin);
        dates[1] = formatDate(now);
        return dates;
    }

    // 最近一个月
    public static Object[] getOneMonth() {

        Object[] dates = new String[2];
        Date now = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.add(Calendar.MONTH, -1);
        dates[0] = formatDate(c.getTime());
        dates[1] = formatDate(now);
        return dates;
    }

    public static String[] getMonthUTC(int offset) {
        Date now = new Date();
        return getMonthWithZone(now, offset, TimeZone.getTimeZone("UTC"));
    }

    public static String[] getMonthWithZone(Date now, int offset, TimeZone timeZone) {
        String[] dates = new String[2];

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(now);
        if (Objects.nonNull(timeZone)) {
            gc.setTimeZone(timeZone);
        }
        gc.add(Calendar.MONTH, offset);
        int y = gc.get(Calendar.YEAR);

        int m = gc.get(Calendar.MONTH) + 1;

        int days = gc.getActualMaximum(Calendar.DAY_OF_MONTH); // 本月最后一天.
        dates[0] = String.format("%02d-%02d-01 00:00:00", y, m);
        dates[1] = String.format("%02d-%02d-%02d 23:55:00", y, m, days);//采集是五分钟一个点 5分钟为23:55:00
        // 1分钟
        // 23:59;

        return dates;
    }


    // 取UTC时间
    public static Date getUTCTime() {
        Date l_datetime = new Date();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone l_timezone = TimeZone.getTimeZone("GMT-0");
        formatter.setTimeZone(l_timezone);
        return l_datetime;
    }

    public static String getUTCDate() {
        Date l_datetime = new Date();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        TimeZone l_timezone = TimeZone.getTimeZone("GMT-0");
        formatter.setTimeZone(l_timezone);
        return formatter.format(l_datetime);
    }

    public static String addDay(String startDate, int i) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            Date date = df.parse(startDate);
            c.setTime(date);
            c.add(Calendar.DATE, i); //
        } catch (ParseException e) {

            e.printStackTrace();
        }
        return df.format(c.getTime());
    }

    public static String formatDateDuring(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        String msg = "";
        if (days > 0) {
            msg += days + " 天 ";
        }
        if (hours > 0) {
            msg += hours + " 小时 ";
        }

        if (minutes > 0) {
            msg += minutes + " 分钟 ";
        }
        if (seconds > 0) {
            msg += seconds + " 秒 ";
        }
        return msg;
    }


    public static Integer diffDay(Date date1, Date date2) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        int result = 0;
        long diff = date2.getTime() - date1.getTime();
        result = (int) (diff / (1000 * 60 * 60 * 24));
        return result;
    }

    public static int diffDayStr(String date1, String date2) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        int result = 0;
        try {
            Date d1 = df.parse(date1);
            Date d2 = df.parse(date2);
            long diff = d2.getTime() - d1.getTime();
            result = (int) (diff / (1000 * 60 * 60 * 24));

        } catch (ParseException e) {

            e.printStackTrace();
        }
        return result;
    }


}
