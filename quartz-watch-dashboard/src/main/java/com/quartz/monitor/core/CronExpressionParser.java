package com.quartz.monitor.core;

import java.text.ParseException;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.quartz.CronExpression;

public class CronExpressionParser {
    private final CronExpression cronExpression;
    private final String cronExpressionString;

    public static final Integer ALL_SPEC = Integer.valueOf(99);// '*'
    public static final Integer NO_SPEC = Integer.valueOf(98);// '?'

    private String secondsExp;
    private String minutesExp;
    private String hoursExp;
    private String daysOfMonthExp;
    private String monthsExp;
    private String daysOfWeekExp;
    private String yearExp;

    public CronExpressionParser(String cronExpr) throws ParseException {
        this.cronExpression = new CronExpression(cronExpr);
        this.cronExpressionString = cronExpr;

        StringTokenizer exprsTok = new StringTokenizer(cronExpr, " \t", false);
        secondsExp = exprsTok.nextToken().trim();
        minutesExp = exprsTok.nextToken().trim();
        hoursExp = exprsTok.nextToken().trim();
        daysOfMonthExp = exprsTok.nextToken().trim();
        monthsExp = exprsTok.nextToken().trim();
        daysOfWeekExp = exprsTok.nextToken().trim();
        yearExp = exprsTok.hasMoreTokens() ? exprsTok.nextToken().trim() : "*";
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

    public String getSecondsField() {
        return secondsExp;
    }

    public String getMinutesField() {
        return minutesExp;
    }

    public String getHoursField() {
        return hoursExp;
    }

    public String getDaysOfMonthField() {
        return daysOfMonthExp;
    }

    public String getMonthsField() {
        return monthsExp;
    }

    public String getDaysOfWeekField() {
        return daysOfWeekExp;
    }

    public String getYearField() {
        return yearExp;
    }

    public Date getTimeAfter(Date afterTime) {
        return cronExpression.getTimeAfter(afterTime);
    }

    public Date getNextValidTimeAfter(Date afterTime) {
        return cronExpression.getNextValidTimeAfter(afterTime);
    }

    public TreeSet<Integer> getYearSet() {
        return parseField(yearExp);
    }

    public TreeSet<Integer> getMinutesSet() {
        return parseField(minutesExp);
    }

    public TreeSet<Integer> getHoursSet() {
        return parseField(hoursExp);
    }

    public TreeSet<Integer> getDaysOfMonthSet() {
        return parseField(daysOfMonthExp);
    }

    public TreeSet<Integer> getMonthsSet() {
        return parseField(monthsExp);
    }

    public TreeSet<Integer> getDaysOfWeekSet() {
        return parseField(daysOfWeekExp);
    }

    private TreeSet<Integer> parseField(String field) {
        TreeSet<Integer> set = new TreeSet<>();
        
        if (field.equals("*") || field.equals("?")) {
            set.add(field.equals("*") ? ALL_SPEC : NO_SPEC);
            return set;
        }

        String[] parts = field.split(",");
        for (String part : parts) {
            if (part.contains("/")) {
                String[] range = part.split("/");
                int start = Integer.parseInt(range[0]);
                int interval = Integer.parseInt(range[1]);
                for (int i = start; i <= 59; i += interval) {
                    set.add(i);
                }
            } else if (part.contains("-")) {
                String[] range = part.split("-");
                int start = Integer.parseInt(range[0]);
                int end = Integer.parseInt(range[1]);
                for (int i = start; i <= end; i++) {
                    set.add(i);
                }
            } else {
                set.add(Integer.parseInt(part));
            }
        }
        return set;
    }

    public String getCronExpressionString() {
        return cronExpressionString;
    }
}
