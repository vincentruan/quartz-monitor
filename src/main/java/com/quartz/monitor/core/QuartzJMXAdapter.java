package com.quartz.monitor.core;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.quartz.monitor.object.Job;
import com.quartz.monitor.object.JobDetail;
import com.quartz.monitor.object.QuartzInstance;
import com.quartz.monitor.object.Scheduler;
import com.quartz.monitor.object.Trigger;

/**
 * 从JMX获取Quartz信息
 * @author guolei
 *
 */
public interface QuartzJMXAdapter
{
   /**
    * 获取Quartz版本
    */
   String getVersion(QuartzInstance quartzInstance, ObjectName objectName);

   /**
    * 获取调度器信息
    */
   Scheduler getSchedulerByJmx(QuartzInstance quartzInstance, ObjectName objectName) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException;
   
   /**
    * 获取作业列表
    */
   List<Job> getJobDetails(QuartzInstance quartzInstance, Scheduler scheduler) throws Exception;
   
   /**
    * 查询所有作业
    */
   Set<Job> queryAllJobs(QuartzInstance quartzInstance, Scheduler scheduler);
   
   /**
    * 根据ID获取作业
    */
   Job getJobById(QuartzInstance quartzInstance, Scheduler scheduler, String group, String name);
   
   /**
    * 获取作业详情
    */
   JobDetail getJobDetailById(QuartzInstance quartzInstance, Scheduler scheduler, String group, String name);
   
   /**
    * 获取作业的触发器
    */
   Set<Trigger> getJobTriggers(QuartzInstance quartzInstance, Scheduler scheduler, String jobName, String jobGroup);
   
   /**
    * 暂停作业
    */
   void pauseJob(QuartzInstance quartzInstance, Scheduler scheduler, String name, String group) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException;
   
   /**
    * 暂停作业
    */
   void pauseJob(QuartzInstance quartzInstance, Scheduler scheduler, Job job) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException;
   
   /**
    * 恢复作业
    */
   void resumeJob(QuartzInstance quartzInstance, Scheduler scheduler, String name, String group) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException;
   
   /**
    * 恢复作业
    */
   void resumeJob(QuartzInstance quartzInstance, Scheduler scheduler, Job job) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException;
   
   /**
    * 触发作业
    */
   void triggerJob(QuartzInstance quartzInstance, Scheduler scheduler, String name, String group) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException;
   
   /**
    * 删除作业
    */
   void deleteJob(QuartzInstance quartzInstance, Scheduler scheduler, String name, String group) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException;
   
   /**
    * 删除作业
    */
   void deleteJob(QuartzInstance quartzInstance, Scheduler scheduler, Job job) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException;
   
   /**
    * 暂停触发器
    */
   void pauseTrigger(QuartzInstance quartzInstance, Scheduler scheduler, String name, String group) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException;
   
   /**
    * 恢复触发器
    */
   void resumeTrigger(QuartzInstance quartzInstance, Scheduler scheduler, String name, String group) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException;
   
   /**
    * 调度作业
    */
   boolean scheduleJob(QuartzInstance quartzInstance, Scheduler scheduler, String jobName, String jobGroup, String triggerName, String triggerGroup, String triggerCronExpression, Map<String, Object> dataMap);
   
   /**
    * 取消调度作业
    */
   boolean unScheduleJob(QuartzInstance quartzInstance, Scheduler scheduler, String triggerName, String triggerGroup);
   
   /**
    * 获取日历名称
    */
   List<Map<String, Object>> getCalendarNames(QuartzInstance quartzInstance, Scheduler scheduler);
   
   /**
    * 中断作业
    */
   void interrupt(QuartzInstance quartzInstance, Scheduler scheduler, String jobName, String jobGroup);
   
   /**
    * 获取作业监听器名称
    */
   List<String> getJobListenerNames(QuartzInstance quartzInstance, Scheduler scheduler);
   
   /**
    * 获取触发器监听器名称
    */
   List<String> getTriggerListenerNames(QuartzInstance quartzInstance, Scheduler scheduler);
   
   /**
    * 获取调度器监听器名称
    */
   List<String> getSchedulerListenerNames(QuartzInstance quartzInstance, Scheduler scheduler);
   
   /**
    * 立即执行作业
    */
   void startJobNow(QuartzInstance quartzInstance, Scheduler scheduler, Job job) throws Exception;
   
   /**
    * 添加作业
    */
   void addJob(QuartzInstance quartzInstance, Scheduler scheduler, Map<String, Object> jobMap) throws Exception;
}