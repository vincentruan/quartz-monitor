package com.quartz.monitor.action;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionSupport;
import com.quartz.monitor.core.CronExpressionParser;
import com.quartz.monitor.util.DateFormateUtil;
import com.quartz.monitor.vo.Result;

public class CronExpAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1757405396551202573L;

	private static Logger log = LoggerFactory.getLogger(CronExpAction.class);
	
	private static final String SINGLE_SPACE = " ";
	
	private static final String PER_TYPE = "0";
	
	private static final String ASSIGN_TYPE = "1";
	
	/**
	 * 分钟
	 */
	private String minuteexptype = PER_TYPE;
	
	private Integer startMinute;
	
	private Integer everyMinute;
	
	private List<Integer> assignMins;
	
	/**
	 * 小时
	 */
	private String hourexptype;
	
	private List<Integer> assignHours;
	
	/**
	 * 天
	 */
	private String dayexptype;
	
	private List<Integer> assignDays;
	
	/**
	 * month
	 */
	private String monthexptype;
	
	private List<Integer> assignMonths;
	
	/**
	 * week
	 */
	private String useweekck;
	
	private String weekexptype;
	
	private List<Integer> assignWeeks;
	
	/**
	 * year
	 */
	private String useyearck;
	
	private String yearexptype;
	
	private String assignYearCron;
	
	private String cronExpression;
	
	private String startDate;
	
	private List<String> schedulerNextResults;
	
	private String secLabel;
	private String minLabel;
	private String hhLabel;
	private String dayLabel;
	private String monthLabel;
	private String weekLabel;
	private String yearLabel;
	
	private Result result;
	
	public String parseCronExp() throws ParseException {
		log.debug("Cron Expression is ========>>>>>{}", this.cronExpression);
		if(StringUtils.isNotBlank(this.cronExpression)) {
			CronExpressionParser exp = new CronExpressionParser(this.cronExpression.trim());
			
			Date dd = new Date();
			
			//拆分表达式
			this.secLabel = exp.getSecondsField();
			this.minLabel = exp.getMinutesField();
			this.hhLabel = exp.getHoursField();
			this.dayLabel = exp.getDaysOfMonthField();
			this.monthLabel = exp.getMonthsField();
			this.weekLabel = exp.getDaysOfWeekField();
			this.yearLabel = exp.getYearField();
			
			if(null == exp.getTimeAfter(dd)) {
				//配置的表达式在当前时间之前，这类调度任务永远不会运行
				log.warn("Cron Expression [{}] are set the trigger before current date, it will nerver be trigger!", this.cronExpression);
				
				TreeSet<Integer> yearSet = exp.getYearSet();
				//获取最小的年份
				int year = yearSet.first();
				//获取当年第一天
				Calendar calendar = Calendar.getInstance();
				calendar.clear();
				calendar.set(Calendar.YEAR, year);
				dd = calendar.getTime();
				
			}

			this.startDate = DateFormateUtil.format("yyyy-MM-dd HH:mm:ss", dd);
			
			schedulerNextResults = new ArrayList<String>();
			for (int i = 1; i <= 8; i++) {
				try {
					dd = exp.getNextValidTimeAfter(dd);
					
					schedulerNextResults.add(DateFormateUtil.format("yyyy-MM-dd HH:mm:ss", dd));
					
					dd = new Date(dd.getTime() + 1000);
				} catch (Exception e) {
					schedulerNextResults.add("");
				}
				
			}
			
			//设置分钟
			this.setMinuteTab(exp);
			
			//设置小时
			this.setHourTab(exp);
			
			//设置day
			this.setDayTab(exp);
			
			//设置month
			this.setMonthTab(exp);
			
			//设置week
			this.setWeekTab(exp);
			
			//设置year
			this.setYearTab(exp);
			
			this.result = new Result();
			
		} else {
			this.result = new Result();
			this.result.setStatusCode("500");
			this.result.setMessage("Cron Expression can not be empty");
		}
		
		return SUCCESS;
	}
	
	/**
	 * 设置年份展示
	 * @param exp
	 */
	private void setYearTab(CronExpressionParser exp) {
		//设置分钟
		TreeSet<Integer> yearTreeSet = exp.getYearSet();
		
		if(yearTreeSet.contains(CronExpressionParser.NO_SPEC)) {
			//is ?
			this.useyearck = "0";
			
			this.yearexptype = PER_TYPE;
		} else if(yearTreeSet.contains(CronExpressionParser.ALL_SPEC)) {
			//is *
			this.useyearck = "1";
			
			this.yearexptype = PER_TYPE;
		} else {
			this.useyearck = "1";
			this.yearexptype = ASSIGN_TYPE;
			
			StringBuilder bud = new StringBuilder();
			if(CollectionUtils.isNotEmpty(yearTreeSet)) {
				for (Integer year : yearTreeSet) {
					bud.append(year).append(",");
				}
			}
			
			this.assignYearCron = bud.substring(0, bud.length() - 1);
		}
	}

	/**
	 * 设置分钟展示信息
	 * @param exp
	 */
	private void setMinuteTab(CronExpressionParser exp) {
		//设置分钟
		TreeSet<Integer> minuteSet = exp.getMinutesSet();
		
		if(minuteSet.contains(CronExpressionParser.ALL_SPEC)) {
			//is *
			this.minuteexptype = PER_TYPE;
			this.startMinute = 0;
			this.everyMinute = 1;
		} else {
			Iterator<Integer> minuteItr = minuteSet.iterator();
			
			if(null != minLabel && minLabel.contains("/")) {
				this.minuteexptype = PER_TYPE;
				
				Integer iFrom = (Integer) minuteItr.next();
	            Integer iTo = (Integer) minuteItr.next();
	            int interval = iTo - iFrom;
	            this.startMinute = iFrom;
				this.everyMinute = interval;
			} else {
				this.minuteexptype = ASSIGN_TYPE;
				
				assignMins = new ArrayList<Integer>();
				while(minuteItr.hasNext()) {
					assignMins.add(minuteItr.next());
				}
			}
		}
	}
	
	/**
	 * 设置小时的展示
	 * @param exp
	 */
	private void setHourTab(CronExpressionParser exp) {
		TreeSet<Integer> hourSet = exp.getHoursSet();
		
		if(hourSet.contains(CronExpressionParser.ALL_SPEC)) {
			//is *
			this.hourexptype = PER_TYPE;
		} else {
			Iterator<Integer> hourItr = hourSet.iterator();
			
			this.hourexptype = ASSIGN_TYPE;
			assignHours = new ArrayList<Integer>();
			while(hourItr.hasNext()) {
				assignHours.add(hourItr.next());
			}
		}
	}
	
	/**
	 * 设置天的展示
	 * @param exp
	 */
	private void setDayTab(CronExpressionParser exp) {
		TreeSet<Integer> daySet = exp.getDaysOfMonthSet();
		
		if(daySet.contains(CronExpressionParser.NO_SPEC)) {
			//is ?
			//do nothing
		} else if (daySet.contains(CronExpressionParser.ALL_SPEC)) {
			//is *
			this.dayexptype = PER_TYPE;
		} else {
			Iterator<Integer> dayItr = daySet.iterator();
			
			this.dayexptype = ASSIGN_TYPE;
			
			this.assignDays = new ArrayList<Integer>();
			while(dayItr.hasNext()) {
				this.assignDays.add(dayItr.next());
			}
		}
	}
	
	/**
	 * 设置月展示信息
	 * @param exp
	 */
	private void setMonthTab(CronExpressionParser exp) {
		//设置分钟
		TreeSet<Integer> monthSet = exp.getMonthsSet();
		
		if(monthSet.contains(CronExpressionParser.ALL_SPEC)) {
			//is *
			this.monthexptype = PER_TYPE;
		} else {
			Iterator<Integer> monthItr = monthSet.iterator();
			
			this.monthexptype = ASSIGN_TYPE;
			
			this.assignMonths = new ArrayList<Integer>();
			while(monthItr.hasNext()) {
				this.assignMonths.add(monthItr.next());
			}
		}
	}
	
	/**
	 * 设置礼拜展示信息
	 * @param exp
	 */
	private void setWeekTab(CronExpressionParser exp) {
		//设置分钟
		TreeSet<Integer> weekSet = exp.getDaysOfWeekSet();
		
		if(weekSet.contains(CronExpressionParser.NO_SPEC)) {
			//is ?
			this.useweekck = "0";
			
			this.weekexptype = PER_TYPE;
		} else if(weekSet.contains(CronExpressionParser.ALL_SPEC)) {
			//is *
			this.useweekck = "1";
			
			this.weekexptype = PER_TYPE;
		} else {
			Iterator<Integer> weekItr = weekSet.iterator();
			
			this.useweekck = "1";
			this.weekexptype = ASSIGN_TYPE;
			
			this.assignWeeks = new ArrayList<Integer>();
			while(weekItr.hasNext()) {
				this.assignWeeks.add(weekItr.next());
			}
		}
	}
	
	public String generateCronExp() throws ParseException {
		this.secLabel = "0";
		
		//分钟表达式
		if(StringUtils.equals(PER_TYPE, this.minuteexptype)) {
			this.minLabel = this.startMinute + "/" + this.everyMinute;
		} else {
			if(CollectionUtils.isEmpty(this.assignMins)) {
	        	throw new VerifyError("Minute must be specified!");
	        }
			
			StringBuilder buf = new StringBuilder();
	        for (int key : this.assignMins) {
	        	buf.append(key).append(",");
	        }
	        
	        this.minLabel = buf.substring(0, buf.length() - 1);
	        
		}
		
		//设置小时
		if(StringUtils.equals(PER_TYPE, this.hourexptype)) {
			this.hhLabel = "*";
		} else {
			if (CollectionUtils.isEmpty(this.assignHours)) {
				throw new VerifyError("Hour must be specified!");
			}
			
			StringBuilder buf = new StringBuilder();
			for (int key : this.assignHours) {
				buf.append(key).append(",");
			}
			this.hhLabel = buf.substring(0, buf.length() - 1);
		}
		
		//设置礼拜、天
		if(StringUtils.equals("1", this.useweekck)) {
			this.dayLabel = "?";
			
			if(StringUtils.equals(PER_TYPE, this.weekexptype)) {
				this.weekLabel = "*";
			} else {
				if (CollectionUtils.isEmpty(this.assignWeeks)) {
					throw new VerifyError("Week must be specified!");
				}
				
				StringBuilder buf = new StringBuilder();
				for (int key : this.assignWeeks) {
					buf.append(key).append(",");
				}

				this.weekLabel = buf.substring(0, buf.length() - 1);
			}
		} else {
			this.weekLabel = "?";
			
			if(StringUtils.equals(PER_TYPE, this.dayexptype)) {
				this.dayLabel = "*";
				
			} else {
				if (CollectionUtils.isEmpty(this.assignDays)) {
					throw new VerifyError("Day of Month must be specified!");
				}
				
				StringBuilder buf = new StringBuilder();
				for (int key : this.assignDays) {
					buf.append(key).append(",");
				}

				this.dayLabel = buf.substring(0, buf.length() - 1);
				
			}
		}
		
		//设置月份
		if(StringUtils.equals(PER_TYPE, this.monthexptype)) {
			this.monthLabel = "*";
		} else {
			if (CollectionUtils.isEmpty(this.assignMonths)) {
				throw new VerifyError("Month must be specified!");
			}
			
			StringBuilder buf = new StringBuilder();
			for (int key : this.assignMonths) {
				buf.append(key).append(",");
			}
			this.monthLabel = buf.substring(0, buf.length() - 1);
			
		}
		
		//设置年份
		boolean useYearCorn = false;
		if(StringUtils.equals("1", this.useyearck) && StringUtils.equals(ASSIGN_TYPE, this.yearexptype) && StringUtils.isNotBlank(this.assignYearCron)) {
			useYearCorn = true;
			this.yearLabel = this.assignYearCron;
		} else {
			this.yearLabel = "*";
		}
		
		String cronExp = this.secLabel + SINGLE_SPACE + this.minLabel
				+ SINGLE_SPACE + this.hhLabel + SINGLE_SPACE
				+ this.dayLabel + SINGLE_SPACE + this.monthLabel
				+ SINGLE_SPACE + this.weekLabel
				+ (useYearCorn ? SINGLE_SPACE + this.yearLabel : "");
		CronExpressionParser exp = new CronExpressionParser(cronExp);
		this.cronExpression = exp.toString();
		
		log.debug("Generate Cron Expression ====>>>>>{}", this.cronExpression);
		
		Date dd = new Date();
		startDate = DateFormateUtil.format("yyyy-MM-dd HH:mm:ss", dd);
		
		schedulerNextResults = new ArrayList<String>();
		for (int i = 1; i <= 8; i++) {
			dd = exp.getNextValidTimeAfter(dd);
			
			schedulerNextResults.add(DateFormateUtil.format("yyyy-MM-dd HH:mm:ss", dd));
			dd = new Date(dd.getTime() + 1000);
		}
		return SUCCESS;
	}

	public String getMinuteexptype() {
		return minuteexptype;
	}

	public void setMinuteexptype(String minuteexptype) {
		this.minuteexptype = minuteexptype;
	}

	public Integer getEveryMinute() {
		return everyMinute;
	}

	public void setEveryMinute(Integer everyMinute) {
		this.everyMinute = everyMinute;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public static String getPerType() {
		return PER_TYPE;
	}

	public static String getAssignType() {
		return ASSIGN_TYPE;
	}

	public String getStartDate() {
		return startDate;
	}

	public List<String> getSchedulerNextResults() {
		return schedulerNextResults;
	}

	public String getSecLabel() {
		return secLabel;
	}

	public String getMinLabel() {
		return minLabel;
	}

	public String getHhLabel() {
		return hhLabel;
	}

	public String getDayLabel() {
		return dayLabel;
	}

	public String getMonthLabel() {
		return monthLabel;
	}

	public String getWeekLabel() {
		return weekLabel;
	}

	public List<Integer> getAssignMins() {
		return assignMins;
	}

	public void setAssignMins(List<Integer> assignMins) {
		this.assignMins = assignMins;
	}

	public String getHourexptype() {
		return hourexptype;
	}

	public void setHourexptype(String hourexptype) {
		this.hourexptype = hourexptype;
	}

	public List<Integer> getAssignHours() {
		return assignHours;
	}

	public void setAssignHours(List<Integer> assignHours) {
		this.assignHours = assignHours;
	}

	public String getDayexptype() {
		return dayexptype;
	}

	public void setDayexptype(String dayexptype) {
		this.dayexptype = dayexptype;
	}

	public List<Integer> getAssignDays() {
		return assignDays;
	}

	public void setAssignDays(List<Integer> assignDays) {
		this.assignDays = assignDays;
	}

	public String getMonthexptype() {
		return monthexptype;
	}

	public void setMonthexptype(String monthexptype) {
		this.monthexptype = monthexptype;
	}

	public List<Integer> getAssignMonths() {
		return assignMonths;
	}

	public void setAssignMonths(List<Integer> assignMonths) {
		this.assignMonths = assignMonths;
	}

	public String isUseweekck() {
		return useweekck;
	}

	public void setUseweekck(String useweekck) {
		this.useweekck = useweekck;
	}

	public String getWeekexptype() {
		return weekexptype;
	}

	public void setWeekexptype(String weekexptype) {
		this.weekexptype = weekexptype;
	}

	public List<Integer> getAssignWeeks() {
		return assignWeeks;
	}

	public void setAssignWeeks(List<Integer> assignWeeks) {
		this.assignWeeks = assignWeeks;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public void setSchedulerNextResults(List<String> schedulerNextResults) {
		this.schedulerNextResults = schedulerNextResults;
	}

	public void setSecLabel(String secLabel) {
		this.secLabel = secLabel;
	}

	public void setMinLabel(String minLabel) {
		this.minLabel = minLabel;
	}

	public void setHhLabel(String hhLabel) {
		this.hhLabel = hhLabel;
	}

	public void setDayLabel(String dayLabel) {
		this.dayLabel = dayLabel;
	}

	public void setMonthLabel(String monthLabel) {
		this.monthLabel = monthLabel;
	}

	public void setWeekLabel(String weekLabel) {
		this.weekLabel = weekLabel;
	}

	public Integer getStartMinute() {
		return startMinute;
	}

	public void setStartMinute(Integer startMinute) {
		this.startMinute = startMinute;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public String getUseweekck() {
		return useweekck;
	}

	public String getYearLabel() {
		return yearLabel;
	}

	public void setYearLabel(String yearLabel) {
		this.yearLabel = yearLabel;
	}

	public String getUseyearck() {
		return useyearck;
	}

	public void setUseyearck(String useyearck) {
		this.useyearck = useyearck;
	}

	public String getYearexptype() {
		return yearexptype;
	}

	public void setYearexptype(String yearexptype) {
		this.yearexptype = yearexptype;
	}

	public String getAssignYearCron() {
		return assignYearCron;
	}

	public void setAssignYearCron(String assignYearCron) {
		this.assignYearCron = assignYearCron;
	}
	
	
}
