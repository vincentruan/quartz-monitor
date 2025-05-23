package com.quartz.monitor.core;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.quartz.monitor.vo.Job;
import com.quartz.monitor.vo.QuartzInstance;
import com.quartz.monitor.vo.Scheduler;
import com.quartz.monitor.vo.Trigger;

/**
 * 从JMX获取Quartz信息
 * @author guolei
 *
 */
public interface QuartzJMXAdapter
{
   public String getVersion(QuartzInstance quartzInstance, ObjectName objectName) throws Exception;

   public List<Job> getJobDetails(QuartzInstance quartzInstance, Scheduler scheduler) throws Exception;

   public Scheduler getSchedulerById(QuartzInstance quartzInstance, String scheduleID) throws Exception;

   public List<Trigger> getTriggersForJob(QuartzInstance quartzInstance, Scheduler scheduler, String jobName, String groupName) throws Exception;

   public void attachListener(QuartzInstance quartzInstance, String scheduleID) throws Exception;
   
   public Scheduler getSchedulerByJmx(QuartzInstance quartzInstance, ObjectName objectName) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException;
   
   public void startJobNow(QuartzInstance quartzInstance, Scheduler scheduler, Job job) throws Exception;
   
   public void pauseJob(QuartzInstance quartzInstance, Scheduler scheduler, Job job) throws Exception;
   
   public void deleteJob(QuartzInstance quartzInstance, Scheduler scheduler, Job job) throws Exception;
   
   public void addJob(QuartzInstance instance, Scheduler schedulerByName, Map<String, Object> jobMap) throws Exception;
   
   public void deleteTrigger(QuartzInstance quartzInstance, Scheduler scheduler, Trigger trigger) throws Exception;
  
   public String getTriggerState(QuartzInstance quartzInstance, Scheduler scheduler, Trigger trigger) throws Exception;
   
   public void addTriggerForJob(QuartzInstance quartzInstance, Scheduler scheduler, Job job, Map<String, Object> triggerMap) throws Exception;

   public void resumeJob(QuartzInstance instance, Scheduler scheduler, Job job) throws Exception;
   
}