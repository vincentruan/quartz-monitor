package com.quartz.monitor.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quartz.monitor.object.Job;
import com.quartz.monitor.object.Trigger;

public class QuartzUtil
{
	
	private static final Logger log = LoggerFactory.getLogger(QuartzUtil.class);
	
	/**
	 * 获取本机IP
	 * @return
	 */
	public static String getIP() {
		String ip = "127.0.0.1";
	    try{
	      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
	      while (interfaces.hasMoreElements()) {
	        NetworkInterface ni = interfaces.nextElement();
	        Enumeration<InetAddress> addresses = ni.getInetAddresses();
	        while (addresses.hasMoreElements()) {
	          InetAddress addr = addresses.nextElement();
	          String aip = addr.getHostAddress();
	          if (!"127.0.0.1".equals(aip) && aip.indexOf(":") == -1) {
	            ip = aip;
	          }
	        }
	      }
	      if (ip.indexOf("/") > 0) {
	        ip = ip.split("/")[0];
	      }
	    } catch (Exception e){
	      log.error("获取本机IP失败: " + e.getMessage());
	    }
	    
	    return ip;
	}
	
	/**
	 * 获取日期格式化工具
	 * @param pattern
	 * @return
	 */
	public static SimpleDateFormat getSimpleDateFormat(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf;
	}
	
   public static Date getNextFireTimeForJob(List<Trigger> triggers)
   {
	  log.info("get Next FIre Time......");
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