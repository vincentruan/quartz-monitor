package com.quartz.monitor.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quartz.monitor.object.JMXInput;
import com.quartz.monitor.object.Job;
import com.quartz.monitor.object.JobDetail;
import com.quartz.monitor.object.QuartzInstance;
import com.quartz.monitor.object.Scheduler;
import com.quartz.monitor.object.Trigger;
import com.quartz.monitor.util.JMXUtil;
import com.quartz.monitor.util.QuartzUtil;
import com.quartz.monitor.util.Tools;

/**
 * http://www.quartz-scheduler.org/api/2.0.0/index.html?org/quartz/jobs/ee/jmx/JMXInvokerJob.html
 * @author guolei
 *
 */
public class QuartzJMXAdapterImpl implements QuartzJMXAdapter {

	private static final Logger log = LoggerFactory.getLogger(QuartzJMXAdapterImpl.class);

	@Override
	public String getVersion(QuartzInstance quartzInstance, ObjectName objectName) {
		String version = "UNKNOWN";
		try {
			version = (String) quartzInstance.getMBeanServerConnection().getAttribute(objectName, "Version");
		} catch (Exception e) {
			log.error("Unable to get version!", e);
		}
		
		return version;
	}

	@Override
	public List<Job> getJobDetails(QuartzInstance quartzInstance, Scheduler scheduler)
			throws Exception {
		List<Job> jobs = null;
		JMXInput jmxInput = new JMXInput(quartzInstance, null, "AllJobDetails", null,
				scheduler.getObjectName());
		TabularDataSupport tdata = (TabularDataSupport) JMXUtil.callJMXAttribute(jmxInput);
		if (tdata != null) {
			jobs = new ArrayList<Job>();
			for (Iterator<Object> it = tdata.values().iterator(); it.hasNext();) {
				Object object = it.next();
				if (!(object instanceof CompositeDataSupport)) {
					continue;
				}
				CompositeDataSupport compositeDataSupport = (CompositeDataSupport) object;
				Job job = new Job();
				job.setSchedulerName(scheduler.getName());
				job.setQuartzInstanceId(scheduler.getQuartzInstanceUUID());
				job.setSchedulerInstanceId(scheduler.getInstanceId());
				job.setJobName((String) JMXUtil.convertToType(compositeDataSupport, "name"));
				log.info("job name:"+job.getJobName());
				job.setDescription((String) JMXUtil.convertToType(compositeDataSupport,"description"));
				job.setDurability(((Boolean) JMXUtil.convertToType(compositeDataSupport,"durability")).booleanValue());
				job.setShouldRecover(((Boolean) JMXUtil.convertToType(compositeDataSupport,"shouldRecover")).booleanValue());
				job.setGroup((String) JMXUtil.convertToType(compositeDataSupport, "group"));
				job.setJobClass((String) JMXUtil.convertToType(compositeDataSupport, "jobClass"));

				// get Next Fire Time for job
				List<Trigger> triggers = this.getTriggersForJob(quartzInstance, scheduler,
						job.getJobName(), job.getGroup());
				
				if(triggers == null || triggers.size() == 0){
					job.setState("NONE");
				}else{
					job.setState(getTriggerState(quartzInstance,scheduler,triggers.get(0)));
				}
				
				log.info("job state:"+job.getState());
				try {
					if (triggers != null && triggers.size() > 0) {
						Date nextFireTime = QuartzUtil.getNextFireTimeForJob(triggers);
						job.setNextFireTime(nextFireTime);
						job.setNumTriggers(triggers.size());
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}

				log.debug("Loaded job: " + job);
				jobs.add(job);
			}
		}
		return jobs;
	}

	@Override
	public Scheduler getSchedulerById(QuartzInstance quartzInstance, String scheduleID)
			throws Exception {
		
		List<Scheduler> list = quartzInstance.getSchedulerList();
		if (list != null && list.size() > 0)
	      {
	         for (int i = 0; i < list.size(); i++)
	         {
	            Scheduler s = list.get(i);
	            if (s.getInstanceId().equals(scheduleID))
	            {
	               return s;
	            }
	         }
	      }
		return null;
	}

	@Override
	public List<Trigger> getTriggersForJob(QuartzInstance quartzInstance, Scheduler scheduler,
			String jobName, String groupName) throws Exception {
		
	      List<Trigger> triggers = null;

	      JMXInput jmxInput = new JMXInput(quartzInstance, new String[]{String.class.getName(), String.class.getName()}, "getTriggersOfJob", new Object[]{jobName, groupName}, scheduler.getObjectName());
	      @SuppressWarnings("unchecked")
		 List<CompositeDataSupport> list = (List<CompositeDataSupport>) JMXUtil.callJMXOperation(jmxInput);
	      if (list != null && list.size() > 0)
	      {
	    	 log.info("-------"+jobName+" trigger size:"+list.size());
	         triggers = new ArrayList<Trigger>();
	         for (int i = 0; i < list.size(); i++)
	         {
	            CompositeDataSupport compositeDataSupport = list.get(i);
	            Trigger trigger = new Trigger();
	            trigger.setCalendarName((String) JMXUtil.convertToType(compositeDataSupport, "calendarName"));
	            log.info("-------"+jobName+" trigger's calendar name:"+trigger.getCalendarName());
	            trigger.setDescription((String) JMXUtil.convertToType(compositeDataSupport, "description"));
	            trigger.setEndTime((Date) JMXUtil.convertToType(compositeDataSupport, "endTime"));
	            trigger.setFinalFireTime((Date) JMXUtil.convertToType(compositeDataSupport, "finalFireTime"));
	            trigger.setFireInstanceId((String) JMXUtil.convertToType(compositeDataSupport, "fireInstanceId"));
	            trigger.setGroup((String) JMXUtil.convertToType(compositeDataSupport, "group"));
	            trigger.setJobGroup((String) JMXUtil.convertToType(compositeDataSupport, "jobGroup"));
	            trigger.setJobName((String) JMXUtil.convertToType(compositeDataSupport, "jobName"));
	            log.info("-------"+jobName+" trigger's job name:"+trigger.getJobName());
	            trigger.setMisfireInstruction(((Integer) JMXUtil.convertToType(compositeDataSupport, "misfireInstruction")).intValue());
	            trigger.setName((String) JMXUtil.convertToType(compositeDataSupport, "name"));
	            log.info("-------"+jobName+" trigger's  name:"+trigger.getName());
	            trigger.setNextFireTime((Date) JMXUtil.convertToType(compositeDataSupport, "nextFireTime"));
	            log.info("-------"+jobName+" trigger's  nextFireTime:"+trigger.getNextFireTime());
	            trigger.setPreviousFireTime((Date) JMXUtil.convertToType(compositeDataSupport, "previousFireTime"));
	            trigger.setPriority(((Integer) JMXUtil.convertToType(compositeDataSupport, "priority")).intValue());
	            trigger.setStartTime((Date) JMXUtil.convertToType(compositeDataSupport, "startTime"));

	            
	            try 
	            {
	               JMXInput stateJmxInput = new JMXInput(quartzInstance, new String[]{String.class.getName(), String.class.getName()}, "getTriggerState", new Object[]{trigger.getName(), trigger.getGroup()}, scheduler.getObjectName());
	               String state = (String) JMXUtil.callJMXOperation(stateJmxInput);
	               trigger.setSTriggerState(state);
	            }
	            catch (Throwable tt)
	            {
	               trigger.setSTriggerState(Trigger.STATE_GET_ERROR);
	            }

	            //删除group为"now"的trigger
	            if(trigger.getGroup().equals("now")){
	            	deleteTrigger(quartzInstance, scheduler, trigger);
	            }else{
	            	 triggers.add(trigger);
	            }
	         }
	      }
	      return triggers;
	}

	@Override
	public void attachListener(QuartzInstance quartzInstance, String scheduleID) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Scheduler getSchedulerByJmx(QuartzInstance quartzInstance, ObjectName objectName) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		  Scheduler scheduler = new Scheduler();
	      MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
	      scheduler.setObjectName(objectName);
	      scheduler.setName((String) connection.getAttribute(objectName, "SchedulerName"));
	      scheduler.setInstanceId((String) connection.getAttribute(objectName, "SchedulerInstanceId"));
	      scheduler.setJobStoreClassName((String) connection.getAttribute(objectName, "JobStoreClassName"));
	      scheduler.setThreadPoolClassName((String) connection.getAttribute(objectName, "ThreadPoolClassName"));
	      scheduler.setThreadPoolSize((Integer) connection.getAttribute(objectName, "ThreadPoolSize"));
	      scheduler.setShutdown((Boolean) connection.getAttribute(objectName, "Shutdown"));
	      scheduler.setStarted((Boolean) connection.getAttribute(objectName, "Started"));
	      scheduler.setStandByMode((Boolean) connection.getAttribute(objectName, "StandbyMode"));
	      scheduler.setQuartzInstanceUUID(quartzInstance.getUuid());
	      scheduler.setVersion(this.getVersion(quartzInstance, objectName));
	      return scheduler;
	}

	@Override
	public void startJobNow(QuartzInstance quartzInstance, Scheduler scheduler, Job job) throws Exception {
		MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
		connection.invoke(
				scheduler.getObjectName(), 
				"triggerJob", 
				new Object[]{job.getJobName(), job.getGroup()}, 
				new String[]{"java.lang.String", "java.lang.String"});
	}

	@Override
	public void deleteJob(QuartzInstance quartzInstance, Scheduler scheduler, Job job) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		deleteJob(quartzInstance, scheduler, job.getJobName(), job.getGroup());
	}

	@Override
	public void deleteTrigger(QuartzInstance quartzInstance, Scheduler scheduler, Trigger trigger)
			throws Exception {

	    JMXInput jmxInput1 = new JMXInput(quartzInstance, new String[]{"java.lang.String","java.lang.String"}, "unscheduleJob", new Object[]{trigger.getName(),trigger.getGroup()}, scheduler.getObjectName());
	    JMXUtil.callJMXOperation(jmxInput1);
		
	}
	
	@Override
	public String getTriggerState(QuartzInstance quartzInstance, Scheduler scheduler, Trigger trigger)
			throws Exception {
	    JMXInput jmxInput = new JMXInput(quartzInstance, new String[]{"java.lang.String","java.lang.String"}, "getTriggerState", new Object[]{trigger.getName(),trigger.getGroup()}, scheduler.getObjectName());
	    String state = (String)JMXUtil.callJMXOperation(jmxInput);
	    return state;
	}

	@Override
	public void addTriggerForJob(QuartzInstance quartzInstance, Scheduler scheduler, Job job,
			Map<String,Object> triggerMap) throws Exception {
		//Map<String,Object> jobMap = QuartzUtil.convertJob2Map(job);
		
		/**
		jobMap.put("name", jobMap.get("name")+"Test");
		
		triggerMap.put("jobName", jobMap.get("name"));
		//这种方法会删除job原有的trigger
		JMXInput jmxInput = new JMXInput(quartzInstance, new String[]{"java.util.Map","java.util.Map"}, "scheduleBasicJob", new Object[]{jobMap,triggerMap}, scheduler.getObjectName());
	    JMXUtil.callJMXOperation(jmxInput);
	    **/
		//必须指定trigger的class，也就是必须有存在的trigger
		
		//JMXInput jmxInput = new JMXInput(quartzInstance, new String[]{"java.util.Map","java.util.Map"}, "scheduleJob", new Object[]{jobMap,triggerMap}, scheduler.getObjectName());
		JMXInput jmxInput = new JMXInput(quartzInstance, new String[]{"java.lang.String","java.lang.String","java.util.Map"}, "scheduleJob", new Object[]{job.getJobName(),job.getGroup(),triggerMap}, scheduler.getObjectName());
	    JMXUtil.callJMXOperation(jmxInput);
//		JMXInput jmxInput = new JMXInput(quartzInstance, new String[]{"java.util.Map"}, "newTrigger", new Object[]{triggerMap}, scheduler.getObjectName());
//	    JMXUtil.callJMXOperation(jmxInput);
	}

	@Override
	public void pauseJob(QuartzInstance quartzInstance, Scheduler scheduler, Job job) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		pauseJob(quartzInstance, scheduler, job.getJobName(), job.getGroup());
	}

	@Override
	public void resumeJob(QuartzInstance quartzInstance, Scheduler scheduler, Job job) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		resumeJob(quartzInstance, scheduler, job.getJobName(), job.getGroup());
	}

	@Override
	public void addJob(QuartzInstance quartzInstance, Scheduler scheduler, Map<String, Object> jobMap) throws Exception {
		String jobName = (String) jobMap.get("name");
		String jobGroup = (String) jobMap.get("group");
		String jobDescription = (String) jobMap.get("description");
		String jobClass = (String) jobMap.get("jobClass");
		
		MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
		
		// 构建JobDetail
		Object[] params = new Object[] {
			jobName,
			jobGroup,
			jobClass,
			Boolean.TRUE, // durability
			Boolean.FALSE // recover
		};
		
		String[] signature = new String[] {
			"java.lang.String",
			"java.lang.String",
			"java.lang.String",
			"boolean",
			"boolean"
		};
		
		// 添加作业
		connection.invoke(
				scheduler.getObjectName(),
				"addJob",
				params,
				signature);
		
		// 设置描述信息
		if (jobDescription != null) {
			connection.invoke(
					scheduler.getObjectName(),
					"setJobDescription",
					new Object[] { jobName, jobGroup, jobDescription },
					new String[] { "java.lang.String", "java.lang.String", "java.lang.String" });
		}
	}

	@Override
	public Set<Job> queryAllJobs(QuartzInstance quartzInstance, Scheduler scheduler) {
		Set<Job> jobs = new HashSet<Job>();
		
		try {
			MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
			
			TabularData jobGroupsData = (TabularData) connection.invoke(scheduler.getObjectName(), "getJobGroupNames", null, null);
			
			// Each job group
			for (Object jobGroupObj : jobGroupsData.values()) {
				
				CompositeData jobGroupData = (CompositeData) jobGroupObj;
				
				String jobGroupName = (String) jobGroupData.get("name");
				
				TabularData jobNamesData = (TabularData) connection.invoke(scheduler.getObjectName(), "getJobNames", new Object[]{jobGroupName}, new String[]{"java.lang.String"});
				
				// Each job in current job group
				for (Object jobNameObj : jobNamesData.values()) {
					CompositeData jobNameData = (CompositeData) jobNameObj;
					String jobName = (String) jobNameData.get("name");
					
					CompositeData jobDetailData = (CompositeData) connection.invoke(
							scheduler.getObjectName(), 
							"getJobDetail", 
							new Object[]{jobName, jobGroupName}, 
							new String[]{"java.lang.String", "java.lang.String"});
					
					String jobDescription = (String) jobDetailData.get("description");
					String jobClassName = getJobClass((CompositeDataSupport) jobDetailData);
					
					Job job = new Job();
					job.setJobGroup(jobGroupName);
					job.setJobName(jobName);
					job.setJobDescription(jobDescription);
					job.setJobClass(jobClassName);
					job.setQuartzUUID(quartzInstance.getUuid());
					job.setSchedulerName(scheduler.getName());
					job.setUuid(job.getQuartzUUID() + "@@" + scheduler.getName() + "@@" + jobGroupName + "@@" + jobName);
					
					jobs.add(job);
					
				}
				
			}
			
		} catch (Exception e) {
			log.error("Unable to query job from JMX", e);
		}
		
		return jobs;
	}

	private String getJobClass(CompositeDataSupport jobDetailData) {
		if (jobDetailData != null) {
			Object val = jobDetailData.get("jobClass");
			if (val != null) {
				return val.toString();
			}
		}

		return null;
	}

	@Override
	public Job getJobById(QuartzInstance quartzInstance, Scheduler scheduler, String group, String name) {
		try {
			MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
			CompositeData jobDetailData = (CompositeData) connection.invoke(
					scheduler.getObjectName(), 
					"getJobDetail", 
					new Object[]{name, group}, 
					new String[]{"java.lang.String", "java.lang.String"});
			
			String jobDescription = (String) jobDetailData.get("description");
			String jobClassName = getJobClass((CompositeDataSupport) jobDetailData);
			
			Job job = new Job();
			job.setJobGroup(group);
			job.setJobName(name);
			job.setJobDescription(jobDescription);
			job.setJobClass(jobClassName);
			job.setQuartzUUID(quartzInstance.getUuid());
			job.setSchedulerName(scheduler.getName());
			job.setUuid(job.getQuartzUUID() + "@@" + scheduler.getName() + "@@" + group + "@@" + name);
			
			return job;
		} catch (Exception e) {
			log.error("Unable to query job detail from JMX", e);
		}
		return null;
	}

	@Override
	public JobDetail getJobDetailById(QuartzInstance quartzInstance, Scheduler scheduler, String group, String name) {
		try {
			MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
			CompositeData jobDetailData = (CompositeData) connection.invoke(
					scheduler.getObjectName(), 
					"getJobDetail", 
					new Object[]{name, group}, 
					new String[]{"java.lang.String", "java.lang.String"});
			
			HashMap<String, String> jobData = new HashMap<String, String>();
			if(jobDetailData.containsKey("jobDataMap")) {
				CompositeData jobDataMapData = (CompositeData) jobDetailData.get("jobDataMap");
				if(jobDataMapData.containsKey("map")) {
					TabularData mapData = (TabularData) jobDataMapData.get("map");
					
					for (Object entryObj : mapData.values()) {
						CompositeData entryData = (CompositeData) entryObj;
						Object keyObj = entryData.get("key");
						Object valObj = entryData.get("value");
						
						jobData.put(keyObj.toString(), valObj.toString());
					}
				}
			}
			
			JobDetail jobDetail = new JobDetail();
			jobDetail.setJobDataMap(jobData);
			
			String jobDescription = (String) jobDetailData.get("description");
			String jobClassName = getJobClass((CompositeDataSupport) jobDetailData);
			
			Job job = new Job();
			job.setJobGroup(group);
			job.setJobName(name);
			job.setJobDescription(jobDescription);
			job.setJobClass(jobClassName);
			job.setQuartzUUID(quartzInstance.getUuid());
			job.setSchedulerName(scheduler.getName());
			job.setUuid(job.getQuartzUUID() + "@@" + scheduler.getName() + "@@" + group + "@@" + name);
			
			jobDetail.setJob(job);
			
			Set<Trigger> triggers = getJobTriggers(quartzInstance, scheduler, name, group);
			jobDetail.setTriggers(triggers);
			
			if (triggers != null && !triggers.isEmpty()) {
				for (Trigger trigger : triggers) {
					trigger.setJobId(job.getUuid());
				}
			}
			
			return jobDetail;
			
		} catch (Exception e) {
			log.error("Unable to query job detail from JMX", e);
		}
		return null;
	}

	@Override
	public Set<Trigger> getJobTriggers(QuartzInstance quartzInstance, Scheduler scheduler, String jobName, String jobGroup) {
		Set<Trigger> triggers = new HashSet<Trigger>();
		try {
			MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
			CompositeData currentJob = (CompositeData) connection.invoke(
					scheduler.getObjectName(), 
					"getCurrentlyExecutingJob", 
					new Object[]{jobName, jobGroup}, 
					new String[]{"java.lang.String", "java.lang.String"});
			
			boolean isRunning = (currentJob != null);
			
			TabularData triggersData = (TabularData) connection.invoke(
					scheduler.getObjectName(), 
					"getTriggersOfJob", 
					new Object[]{jobName, jobGroup}, 
					new String[]{"java.lang.String", "java.lang.String"});
			
			for (Object triggerObj : triggersData.values()) {
				CompositeData triggerData = (CompositeData) triggerObj;
				
				String name = (String) triggerData.get("name");
				String group = (String) triggerData.get("group");
				String description = (String) triggerData.get("description");
				String type = (String) triggerData.get("type");
				String state = (String) triggerData.get("state");
				String expression = null;
				if(type.contains("CronTrigger")) {
					expression = (String) triggerData.get("cronExpression");
				}
				
				Trigger trigger = new Trigger();
				trigger.setTriggerName(name);
				trigger.setTriggerGroup(group);
				trigger.setTriggerDescription(description);
				trigger.setTriggerExpression(expression);
				trigger.setTriggerState(state);
				trigger.setTriggerType(type);
				trigger.setQuartzUUID(quartzInstance.getUuid());
				trigger.setSchedulerName(scheduler.getName());
				trigger.setRunning(isRunning);
				trigger.setUuid(trigger.getQuartzUUID() + "@@" + scheduler.getName() + "@@" + group + "@@" + name);
				
				triggers.add(trigger);
			}
			
		} catch (Exception e) {
			log.error("Unable to query triggers from JMX", e);
		}
		
		return triggers;
	}

	@Override
	public void pauseJob(QuartzInstance quartzInstance, Scheduler scheduler, String name, String group) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
		connection.invoke(
				scheduler.getObjectName(), 
				"pauseJob", 
				new Object[]{name, group}, 
				new String[]{"java.lang.String", "java.lang.String"});
	}

	@Override
	public void resumeJob(QuartzInstance quartzInstance, Scheduler scheduler, String name, String group) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
		connection.invoke(
				scheduler.getObjectName(), 
				"resumeJob", 
				new Object[]{name, group}, 
				new String[]{"java.lang.String", "java.lang.String"});
	}

	@Override
	public void triggerJob(QuartzInstance quartzInstance, Scheduler scheduler, String name, String group) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
		connection.invoke(
				scheduler.getObjectName(), 
				"triggerJob", 
				new Object[]{name, group}, 
				new String[]{"java.lang.String", "java.lang.String"});
	}

	@Override
	public void deleteJob(QuartzInstance quartzInstance, Scheduler scheduler, String name, String group) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
		connection.invoke(
				scheduler.getObjectName(), 
				"deleteJob", 
				new Object[]{name, group}, 
				new String[]{"java.lang.String", "java.lang.String"});
	}

	@Override
	public void pauseTrigger(QuartzInstance quartzInstance, Scheduler scheduler, String name, String group) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
		connection.invoke(
				scheduler.getObjectName(), 
				"pauseTrigger", 
				new Object[]{name, group}, 
				new String[]{"java.lang.String", "java.lang.String"});
	}

	@Override
	public void resumeTrigger(QuartzInstance quartzInstance, Scheduler scheduler, String name, String group) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
		connection.invoke(
				scheduler.getObjectName(), 
				"resumeTrigger", 
				new Object[]{name, group}, 
				new String[]{"java.lang.String", "java.lang.String"});
	}

	@Override
	public boolean scheduleJob(QuartzInstance quartzInstance, Scheduler scheduler, String jobName, String jobGroup, String triggerName, String triggerGroup, String triggerCronExpression, Map<String, Object> dataMap) {
		try {
			MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
			
			log.info("Check to see if job exists?");
			// Now add the job into the scheduler
			CompositeData jobDetailData = (CompositeData) connection.invoke(
					scheduler.getObjectName(), 
					"getJobDetail", 
					new Object[]{jobName, jobGroup}, 
					new String[]{"java.lang.String", "java.lang.String"});
			
			// If it exists, ok to proceed.
			
			// If it does not exist, do something else.
			if(jobDetailData == null) {
				log.info("Job does not exist, cannot proceed");
				return false;
			} else {
				log.info("Job exists, proceeding to create trigger...");
			}
			
			// Create the trigger
			connection.invoke(
					scheduler.getObjectName(), 
					"scheduleJob", 
					new Object[]{triggerName, triggerGroup, triggerCronExpression, jobName, jobGroup, dataMap}, 
					new String[]{"java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String", "java.util.Map"});
			
			return true;
		} catch (Exception e) {
			log.error("Unable to schedule job", e);
			return false;
		}
	}

	@Override
	public boolean unScheduleJob(QuartzInstance quartzInstance, Scheduler scheduler, String triggerName, String triggerGroup) {
		try {
			MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
			connection.invoke(
					scheduler.getObjectName(), 
					"unscheduleJob", 
					new Object[]{triggerName, triggerGroup}, 
					new String[]{"java.lang.String", "java.lang.String"});
			return true;
		} catch (Exception e) {
			log.error("Unable to unschedule job", e);
			return false;
		}
	}

	@Override
	public List<Map<String, Object>> getCalendarNames(QuartzInstance quartzInstance, Scheduler scheduler) {
		List<Map<String, Object>> calendars = new ArrayList<Map<String, Object>>();
		try {
			MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
			
			TabularData calendarNamesData = (TabularData) connection.invoke(scheduler.getObjectName(), "getCalendarNames", null, null);
			
			// Each job in current job group
			for (Object calendarObj : calendarNamesData.values()) {
				CompositeData calendarNameData = (CompositeData) calendarObj;
				String calendarName = (String) calendarNameData.get("name");
				
				
				Map<String, Object> calendar = new HashMap<String, Object>();
				calendar.put("calendarName", calendarName);
				
				calendars.add(calendar);
			}
		} catch (Exception e) {
			log.error("Unable to get calendar names!", e);
		}
		
		return calendars;
	}

	@Override
	public void interrupt(QuartzInstance quartzInstance, Scheduler scheduler, String jobName, String jobGroup) {
		try {
			MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
			connection.invoke(
					scheduler.getObjectName(), 
					"interrupt", 
					new Object[]{jobName, jobGroup}, 
					new String[]{"java.lang.String", "java.lang.String"});
		} catch (Exception e) {
			log.error("Unable to interrupt", e);
		}
	}

	@Override
	public List<String> getJobListenerNames(QuartzInstance quartzInstance, Scheduler scheduler) {
		List<String> listeners = new ArrayList<String>();
		
		try {
			MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
			
			TabularData listenersData = (TabularData) connection.invoke(scheduler.getObjectName(), "getJobListenerNames", null, null);
			
			// For each listener name
			for (Object listenerObj : listenersData.values()) {
				CompositeData listenerNameData = (CompositeData) listenerObj;
				String listenerName = (String) listenerNameData.get("name");
				
				if(listenerName != null) {
					listeners.add(listenerName);
				}
			}
			
		} catch (Exception e) {
			log.error("Unable to get job listener names.", e);
		}
		
		return listeners;
	}

	@Override
	public List<String> getTriggerListenerNames(QuartzInstance quartzInstance, Scheduler scheduler) {
		List<String> listeners = new ArrayList<String>();
		
		try {
			MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
			
			TabularData listenersData = (TabularData) connection.invoke(scheduler.getObjectName(), "getTriggerListenerNames", null, null);
			
			// For each listener name
			for (Object listenerObj : listenersData.values()) {
				CompositeData listenerNameData = (CompositeData) listenerObj;
				String listenerName = (String) listenerNameData.get("name");
				
				if(listenerName != null) {
					listeners.add(listenerName);
				}
			}
			
		} catch (Exception e) {
			log.error("Unable to get trigger listener names.", e);
		}
		
		return listeners;
	}

	@Override
	public List<String> getSchedulerListenerNames(QuartzInstance quartzInstance, Scheduler scheduler) {
		List<String> listeners = new ArrayList<String>();
		
		try {
			MBeanServerConnection connection = quartzInstance.getMBeanServerConnection();
			
			TabularData listenersData = (TabularData) connection.invoke(scheduler.getObjectName(), "getSchedulerListenerNames", null, null);
			
			// For each listener name
			for (Object listenerObj : listenersData.values()) {
				CompositeData listenerNameData = (CompositeData) listenerObj;
				String listenerName = (String) listenerNameData.get("name");
				
				if(listenerName != null) {
					listeners.add(listenerName);
				}
			}
			
		} catch (Exception e) {
			log.error("Unable to get scheduler listener names.", e);
		}
		
		return listeners;
	}
}
