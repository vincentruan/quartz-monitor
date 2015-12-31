package com.quartz.monitor.core;

import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.quartz.CronExpression;

public class CronExpressionParser extends CronExpression {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6567345831921239317L;
	
	public static final Integer ALL_SPEC = new Integer(ALL_SPEC_INT);// '*'
	
	public static final Integer NO_SPEC = Integer.valueOf(NO_SPEC_INT);// '?'

	private String secondsExp;
	private String minutesExp;
	private String hoursExp;
	private String daysOfMonthExp;
	private String monthsExp;
	private String daysOfWeekExp;
	private String yearExp;

	public CronExpressionParser(String cronExpression) throws ParseException {
		super(cronExpression);

		StringTokenizer exprsTok = new StringTokenizer(cronExpression, " \t",
				false);
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

	public TreeSet<Integer> getSecondsSet() {
		return super.getSet(SECOND);
	}

	public String getSecondsField() {
		return super.getExpressionSetSummary(this.getSecondsSet());
	}

	public TreeSet<Integer> getMinutesSet() {
		return super.getSet(MINUTE);
	}

	public String getMinutesField() {
		return super.getExpressionSetSummary(this.getMinutesSet());
	}

	public TreeSet<Integer> getHoursSet() {
		return super.getSet(HOUR);
	}

	public String getHoursField() {
		return getExpressionSetSummary(this.getHoursSet());
	}

	public TreeSet<Integer> getDaysOfMonthSet() {
		return super.getSet(DAY_OF_MONTH);
	}

	public String getDaysOfMonthField() {
		return getExpressionSetSummary(this.getDaysOfMonthSet());
	}

	public TreeSet<Integer> getMonthsSet() {
		return super.getSet(MONTH);
	}

	public String getMonthsField() {
		return getExpressionSetSummary(this.getMonthsSet());
	}

	public TreeSet<Integer> getDaysOfWeekSet() {
		return super.getSet(DAY_OF_WEEK);
	}
	
	public String getDaysOfWeekField() {
		return getExpressionSetSummary(this.getDaysOfWeekSet());
	}
	
	public TreeSet<Integer> getYearSet() {
		return super.getSet(YEAR);
	}
	
	public String getYearField() {
		return getExpressionSetSummary(this.getYearSet());
	}
	
	
}
