package com.quartz.monitor.core;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cron表达式解析器，针对Quartz 2.3.2版本
 */
public class CronExpressionParser {
    
    private static final Logger log = LoggerFactory.getLogger(CronExpressionParser.class);

    public static final Integer ALL_SPEC = Integer.valueOf(0); // '*'
    public static final Integer NO_SPEC = Integer.valueOf(-1); // '?'

    private CronExpression cronExpression;
    private String secondsExp;
    private String minutesExp;
    private String hoursExp;
    private String daysOfMonthExp;
    private String monthsExp;
    private String daysOfWeekExp;
    private String yearExp;

    // 存储Cron表达式的各部分解析结果
    private final TreeSet<Integer> seconds = new TreeSet<>();
    private final TreeSet<Integer> minutes = new TreeSet<>();
    private final TreeSet<Integer> hours = new TreeSet<>();
    private final TreeSet<Integer> daysOfMonth = new TreeSet<>();
    private final TreeSet<Integer> months = new TreeSet<>();
    private final TreeSet<Integer> daysOfWeek = new TreeSet<>();
    private final TreeSet<Integer> years = new TreeSet<>();

    public CronExpressionParser(String cronExpressionString) throws ParseException {
        this.cronExpression = new CronExpression(cronExpressionString);

        StringTokenizer exprsTok = new StringTokenizer(cronExpressionString, " \t", false);
        secondsExp = exprsTok.nextToken().trim();
        minutesExp = exprsTok.nextToken().trim();
        hoursExp = exprsTok.nextToken().trim();
        daysOfMonthExp = exprsTok.nextToken().trim();
        monthsExp = exprsTok.nextToken().trim();
        daysOfWeekExp = exprsTok.nextToken().trim();
        yearExp = exprsTok.hasMoreTokens() ? exprsTok.nextToken().trim() : "*";

        // 解析各部分
        parseField(secondsExp, seconds, 0, 59);
        parseField(minutesExp, minutes, 0, 59);
        parseField(hoursExp, hours, 0, 23);
        parseField(daysOfMonthExp, daysOfMonth, 1, 31);
        parseField(monthsExp, months, 1, 12);
        parseField(daysOfWeekExp, daysOfWeek, 1, 7);
        parseField(yearExp, years, 1970, 2099);
    }

    /**
     * 解析单个字段
     */
    private void parseField(String fieldExp, TreeSet<Integer> set, int min, int max) {
        if (fieldExp.equals("*")) {
            set.add(ALL_SPEC);
            return;
        }
        
        if (fieldExp.equals("?")) {
            set.add(NO_SPEC);
            return;
        }
        
        // 处理范围、列表、步长等
        if (fieldExp.contains("/")) {
            // 步长: 0/15
            String[] parts = fieldExp.split("/");
            String rangeStr = parts[0];
            int step = Integer.parseInt(parts[1]);
            
            int start = rangeStr.equals("*") ? min : Integer.parseInt(rangeStr);
            for (int i = start; i <= max; i += step) {
                set.add(i);
            }
        } else if (fieldExp.contains("-")) {
            // 范围: 1-5
            String[] parts = fieldExp.split("-");
            int start = Integer.parseInt(parts[0]);
            int end = Integer.parseInt(parts[1]);
            
            for (int i = start; i <= end; i++) {
                set.add(i);
            }
        } else if (fieldExp.contains(",")) {
            // 列表: 1,3,5
            String[] parts = fieldExp.split(",");
            for (String part : parts) {
                set.add(Integer.parseInt(part));
            }
        } else {
            // 单值
            set.add(Integer.parseInt(fieldExp));
        }
    }

    public String getExpressionSetSummary(Set<Integer> set) {
        if (set.contains(ALL_SPEC)) {
            return "*";
        }
        
        if (set.contains(NO_SPEC)) {
            return "?";
        }
        
        StringBuilder buf = new StringBuilder();
        for (Integer i : set) {
            buf.append(i).append(",");
        }
        return buf.substring(0, buf.length() - 1);
    }

    /**
     * 获取下一个有效时间
     */
    public Date getNextValidTimeAfter(Date date) {
        return cronExpression.getNextValidTimeAfter(date);
    }

    /**
     * 获取给定时间之后的时间
     */
    public Date getTimeAfter(Date date) {
        return cronExpression.getTimeAfter(date);
    }

    public String getSecondsExp() {
        return secondsExp;
    }

    public String getMinutesExp() {
        return minutesExp;
    }

    public String getHoursExp() {
        return hoursExp;
    }

    public String getDaysOfMonthExp() {
        return daysOfMonthExp;
    }

    public String getMonthsExp() {
        return monthsExp;
    }

    public String getDaysOfWeekExp() {
        return daysOfWeekExp;
    }

    public String getYearExp() {
        return yearExp;
    }

    public TreeSet<Integer> getSecondsSet() {
        return seconds;
    }

    public String getSecondsField() {
        return getExpressionSetSummary(seconds);
    }

    public TreeSet<Integer> getMinutesSet() {
        return minutes;
    }

    public String getMinutesField() {
        return getExpressionSetSummary(minutes);
    }

    public TreeSet<Integer> getHoursSet() {
        return hours;
    }

    public String getHoursField() {
        return getExpressionSetSummary(hours);
    }

    public TreeSet<Integer> getDaysOfMonthSet() {
        return daysOfMonth;
    }

    public String getDaysOfMonthField() {
        return getExpressionSetSummary(daysOfMonth);
    }

    public TreeSet<Integer> getMonthsSet() {
        return months;
    }

    public String getMonthsField() {
        return getExpressionSetSummary(months);
    }

    public TreeSet<Integer> getDaysOfWeekSet() {
        return daysOfWeek;
    }

    public String getDaysOfWeekField() {
        return getExpressionSetSummary(daysOfWeek);
    }

    public TreeSet<Integer> getYearSet() {
        return years;
    }

    public String getYearField() {
        return getExpressionSetSummary(years);
    }

    @Override
    public String toString() {
        return cronExpression.toString();
    }
}
