<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.quartz.monitor.util.JMXUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<div class="pageContent">
	<form method="post" action="<%=request.getContextPath()%>/quartz/add.action" class="pageForm required-validate" onsubmit="return validateCallback(this,dialogAjaxDone);">
		<div class="pageFormContent" layoutH="56">
			<div class="unit">
				<label>Quarzt容器类型：</label>
				<select name="container" class="combox" >
					<c:forEach items="<%=JMXUtil.getAllAuthContainers()%>" var="container">
						<option value="${container}">${container}</option>
					</c:forEach>
				</select>
			</div>
			<div class="unit">
				<label>主机ip：</label><input name="host" class="required" type="text" size="30"  alt="请输入主机ip" value="127.0.0.1"/>
			</div>
			<div class="unit">
				<label>主机端口：</label><input type="text" class="required" name="port" alt="请输入主机端口" value="2911"/>
			</div>
			<div class="unit">
				<label>用户名：</label><input type="text" name="username"  class="textInput" alt="请输入用户名">
			</div>
			<div class="unit">
				<label>密码：</label><input name="password" class="text" type="text" size="30" alt="请输入密码"/>
			</div>
		</div>
		<div class="formBar">
			<ul>
				<li><div class="buttonActive"><div class="buttonContent"><button type="submit">保存</button></div></div></li>
				<li>
					<div class="button"><div class="buttonContent"><button type="button" class="close">取消</button></div></div>
				</li>
			</ul>
		</div>
	</form>
</div>
