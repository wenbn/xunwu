package www.ucforward.com.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2018/7/11.
 */
public class DateUtil {


    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    public static  String addDateMinut(String day,int x){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");// 24小时制
        /*
        引号里面个格式也可以是 HH:mm:ss或者HH:mm等等，很随意的，不过在主函数调用时，要和输入的变
        量day格式一致
        */
        Date date = null;
        try {
            date = format.parse(day);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (date == null)
            return "";
        System.out.println("front:" + format.format(date)); //显示输入的日期
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, x);// 24小时制
        date = cal.getTime();
        System.out.println("after:" + format.format(date));  //显示更新后的日期
        cal = null;
        return format.format(date);
    }

    /**
     * 获取当前时间之前或几分钟 minute
     * @param day
     * @param x
     * @param operation 0,之前，1：之后
     * @return
     */
    public static  String getTimeByMinute(String day,int x,int operation){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制
        /*
        引号里面个格式也可以是 HH:mm:ss或者HH:mm等等，很随意的，不过在主函数调用时，要和输入的变
        量day格式一致
        */
        Date date = null;
        try {
            date = format.parse(day);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (date == null)
            return "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if(operation == 0){
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - x);// 让日期加1
        }else if(operation == 1){
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + x);// 让日期加1
        }
        date = calendar.getTime();
        System.out.println("after:" + format.format(date));  //显示更新后的日期
        calendar = null;
        return format.format(date);
    }

    /**
     * 获取当前时间前几小时
     * @param day
     * @param x
     * @return
     */
    public static String getTimeByHour(String day,int x){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制
        /*
        引号里面个格式也可以是 HH:mm:ss或者HH:mm等等，很随意的，不过在主函数调用时，要和输入的变
        量day格式一致
        */
        Date date = null;
        try {
            date = format.parse(day);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (date == null)
            return "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) - x);// 让日期加1
        date = calendar.getTime();
        System.out.println("after:" + format.format(date));  //显示更新后的日期
        calendar = null;
        return format.format(date);
    }

    /**
     * 获取当前时间之前几天 day
     * @param day
     * @param x
     * @return
     */
    public static String getTimeByDay(String day,int x){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制
        /*
        引号里面个格式也可以是 HH:mm:ss或者HH:mm等等，很随意的，不过在主函数调用时，要和输入的变
        量day格式一致
        */
        Date date = null;
        try {
            date = format.parse(day);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (date == null)
            return "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - x);// 让日期加1
        date = calendar.getTime();
        System.out.println("after:" + format.format(date));  //显示更新后的日期
        calendar = null;
        return format.format(date);
    }


    /**
     * 日期转换为周
     * @param datetime
     * @return
     */
    public static String dateToWeek(String datetime) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = { "Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat" };
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date datet = null;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }


    /**
     * java获取 当月所有的日期集合
     * @return
     */
    public static List<Date> getDayListOfMonth() {
        List list = new ArrayList();
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        int year = aCalendar.get(Calendar.YEAR);//年份
        int month = aCalendar.get(Calendar.MONTH) + 1;//月份
        int day = aCalendar.getActualMaximum(Calendar.DATE);
        String monthStr="0";
        if(month<10){
            monthStr="0"+month;
        }else{
            monthStr=String.valueOf(month);
        }
        for (int i = 1; i <= day; i++) {
            String days="0";
            if(i<10){
                days="0"+i;
            }else {
                days=String.valueOf(i);
            }
            String aDate = String.valueOf(year)+"-"+monthStr+"-"+days;
            SimpleDateFormat sp=new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = sp.parse(aDate);
                list.add(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return list;
    }

    /**
     * java获取 当月所有的日期集合
     * @param year 年份
     * @param month 月份
     * @return
     */
    public static List<String> getDayListOfMonths(int year,int month) {
        List list = new ArrayList();
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        //int month = aCalendar.get(Calendar.MONTH) + 1+_month;//月份
        int day = aCalendar.getActualMaximum(Calendar.DATE);
        String monthStr="0";
        if(month<10){
            monthStr="0"+month;
        }else{
            monthStr=String.valueOf(month);
        }
        for (int i = 1; i <= day; i++) {
            String days="0";
            if(i<10){
                days="0"+i;
            }else {
                days=String.valueOf(i);
            }
            list.add(String.valueOf(year)+"-"+monthStr+"-"+days);
        }
        return list;
    }

    /**
     * 获取当月的所有周末
     * @param year
     * @param month
     * @return
     */
    public static List getWeekendInMonth(int year, int month) {
        List list = new ArrayList();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);// 不设置的话默认为当年
        calendar.set(Calendar.MONTH, month - 1);// 设置月份
        calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为当月第一天
        int daySize = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);// 当月最大天数
        for (int i = 0; i < daySize-1; i++) {
            calendar.add(Calendar.DATE, 1);//在第一天的基础上加1
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            if (week == Calendar.SATURDAY || week == Calendar.SUNDAY) {// 1代表周日，7代表周六 判断这是一个星期的第几天从而判断是否是周末
                list.add(year+"-"+month+"-"+calendar.get(Calendar.DAY_OF_MONTH));// 得到当天是一个月的第几天
            }
        }
        return list;
    }

    public static List<String> getMonthFullDays(int queryMonthsAgo){
        List<String> fullDayList = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);//年份
        int month = calendar.get(Calendar.MONTH) + 1;//月份
        calendar.set(Calendar.YEAR, year);// 不设置的话默认为当年
        calendar.set(Calendar.MONTH, month - 1);// 设置月份
        calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为当月第一天
        month = month+queryMonthsAgo;
        if(month<=0){
            month = month+12;
            year = year-1;
        }
        return getMonthFullDay(year,month,0);
    }



    /**
     * 某一年某个月的每一天
     */
    public static List<String> getMonthFullDay(int year , int month,int day){
        List<String> fullDayList = new ArrayList<String>();
        if(day <= 0 ) day = 1;
        Calendar cal = Calendar.getInstance();// 获得当前日期对象
        cal.clear();// 清除信息
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);// 1月从0开始
        cal.set(Calendar.DAY_OF_MONTH, day);// 设置为1号,当前日期既为本月第一天
        int count = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int j = 0; j <= (count-1);) {
            if(sdf.format(cal.getTime()).equals(getLastDay(year, month)))
                break;
            cal.add(Calendar.DAY_OF_MONTH, j == 0 ? +0 : +1);
            j++;
            fullDayList.add(sdf.format(cal.getTime()));
        }
        return fullDayList;
    }

    public static String getLastDay(int year,int month){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 0);
        return sdf.format(cal.getTime());
    }


    /**获取上n个小时整点小时时间
     * @param date
     * @return
     */
    public static String getLastHourTime(Date date,int n){
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ca.set(Calendar.HOUR_OF_DAY, ca.get(Calendar.HOUR_OF_DAY)-n);
        date = ca.getTime();
        return sdf.format(date);
    }

    /**获取当前时间的整点小时时间
     * @param date
     * @return
     */
    public static String getCurrHourTime(Date date){
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        date = ca.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static void main(String[] args) {
//        List<String> dayListOfMonth = DateUtil.getDayListOfMonths(2017 ,2);
        List<String> dayListOfMonth = DateUtil.getMonthFullDays(-10);
        System.out.println(dayListOfMonth);
        System.out.println(DateUtil.getCurrHourTime(new Date()));
        System.out.println(DateUtil.getLastHourTime(new Date(),-1));
    }
}
