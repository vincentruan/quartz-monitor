quartz-monitor
==============

quartz-monitor 是一个基于DWZ的Quartz管理工具，可以实时动态的管理Job和trigger，具体功能主要包括：

1. 配置管理
针对不同环境的Quartz，需要有一个统一的入口去管理这些配置，来满足对不同环境的任务的管理。
2. Job管理
提供对Job的管理和维护功能。Monitor提供对Job的基本管理，包括对其状态、执行时间、基本信息的管理以及提供基于Job的基本操作。
3. Trigger管理
提供对trigger的管理和维护功能。可以查看某个job的trigger信息，并添加和修改trigger。
4. Cron Expression校验
Cron Expression虽然简单却非常容易写错，所以我们提供了对其的校验功能。

配置方法：

1）配置quartz支持JMX

在需要管理的应用的quartz.properties中加入配置：
```xml
org.quartz.scheduler.jmx.export = true
```

2）配置应用容器支持JMX

A) tomcat
比如我使用的是TOMCAT，并且在Linux上，我需要往catalina.sh中加入：
```xml
JAVA_OPTS='-Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.port=2911 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dorg.quartz.scheduler.jmx.export=true'
```
B) weblogic
使用的是weblogic时，只需开启t3协议(默认t3/JMX已经开启)

3）配置Quartz-Monitor

运行方法：
(1)tomcat服务器运行
将quartz-monitor放入tomcat，配置好远程quartz的jmx信息，如jmx端口和ip，即可使用。
（2）maven整合tomcat7插件运行
mvn tomcat7:run

forked from:
https://code.google.com/archive/p/jwatch/
https://github.com/xishuixixia/quartz-monitor

Installation Instructions

Note: At present, JWatch can only interact with Quartz 2.0+. Future releases will support older versions of Quartz.

Get the most recent WAR file from the Downloads Section. It should be labeled something like jwatch-(VERSION).zip
Unpack the zip file and deploy the war within it to your application server.
Point your browser to http://HOST:PORT/jwatch
Note: You must have JMX enabled in your application for JWatch to communicate with your Quartz instances:
Configuring JMX in your Application

JWatch communicates with Quartz Instances using JMX, so the JVM Quartz is running on, will have to have JMX enabled, and Quartz must also be exposing its internal MBeans.

Sample JVM parameters: -Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.port=2911 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dorg.quartz.scheduler.jmx.export=true

Creating a Quartz Monitor

Click the "Add New Quartz Instance" link on the top-right of the user-interface.
Configure your Quartz Instance using the dialogue popup: http://jwatch.googlecode.com/svn/trunk/site/images/ss/addnewinstance.png' />
Configuration Options

To increase the amount of executed jobs that JWatch keeps in the queue, you will need to modify the jwatch.war web.xml file. By default, JWatch keeps 1000 executed jobs in memory.

<context-param> <param-name>maxevents</param-name> <param-value>1000</param-value> <description>How many job execution events to keep in the queue.</description> </context-param>