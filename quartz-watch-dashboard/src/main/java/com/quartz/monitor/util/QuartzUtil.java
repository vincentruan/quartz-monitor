package com.quartz.monitor.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quartz.monitor.dto.Job;
import com.quartz.monitor.dto.Trigger;

public class QuartzUtil
{
	
	private static final Logger logger = LoggerFactory.getLogger(QuartzUtil.class);
	
   public static Date getNextFireTimeForJob(List<Trigger> triggers)
   {
	  logger.info("get Next FIre Time......");
      Date theNext = null;
      if (triggers != null && triggers.size() > 0)
      {
         for (int i = 0; i < triggers.size(); i++)
         {
            Trigger trigger = triggers.get(i);
            if (trigger.getNextFireTime() == null)
            {
               continue;
            }else{
            	theNext = trigger.getNextFireTime();
            }
            if (theNext != null && trigger.getNextFireTime().before(theNext))
            {
               theNext = trigger.getNextFireTime();
            }
         }
      }
      return theNext;
   }
   
	public static Map<String, Object> convertJob2Map(Job job) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", job.getJobName());
		map.put("group", job.getGroup());
		map.put("description", job.getDescription());
		map.put("jobClass", job.getJobClass());
		map.put("durability", job.isDurability());
		map.put("shouldRecover", job.isShouldRecover());
		map.put("jobDataMap", new HashMap<String, String>());
		return map;
	}
}