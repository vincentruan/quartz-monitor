<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<script type="text/javascript" src="js/jquery.zclip.js"></script>
<script type="text/javascript">
$(function() {
	$('#expressLabel').keyup(function(event) {
		if(event.keyCode == 13){
            resolve2UI();
        }
	});
	
	$("a#copyCronExpBtn").zclip({ 
		path:'<%=request.getContextPath()%>/js/swf/ZeroClipboard.swf', 
		copy:function(){
			return $('#expressLabel').val();
		},
		beforeCopy:function() {
			if($.trim($('#expressLabel').val()) == '') {
				alertMsg.error('Crop Expression为空，无法复制到剪切板');
				return false;
			}
		},
		afterCopy:function() {
			alertMsg.correct('已将 [' + $('#expressLabel').val() + ']复制到剪切板');
		}
	});
});

function resolve2UI() {
	$.ajax({
		url : '<%=request.getContextPath()%>/json/parseCronExp.action',
		type : 'post',
		dataType: "json",
		data : {'cronExpression' : $('#expressLabel').val()},
		success: function(data, textStatus) {
			if(data.result.statusCode == "500") {
				alertMsg.error(data.result.message);
				return false;
			}
			//alertMsg.correct(textStatus);
			
			$('#secLabel').val(data.secLabel);
			$('#minLabel').val(data.minLabel);
			$('#hhLabel').val(data.hhLabel);
			$('#dayLabel').val(data.dayLabel);
			$('#monthLabel').val(data.monthLabel);
			$('#weekLabel').val(data.weekLabel);
			$('#yearLabel').val(data.yearLabel);
			
			$('#startDateLabel').val(data.startDate);
			
			$('table#schedulerListTable tbody').empty();
			
			if(!$.isEmptyObject(data.schedulerNextResults)) {
				var style_tr = '';
				$.each(data.schedulerNextResults, function(i, n) {
					style_tr = (i % 2 == 0) ? ' class="trbg selected"' : '';
					$('table#schedulerListTable tbody').append('<tr' + style_tr + '>'
						+ '<td align="center">' + (i + 1) + '</td><td>'
						+ n + '</td></tr>');
				});
			}
			
			//set minute
			$(':checkbox[name=assignMins]').removeAttr("checked");
			if(data.minuteexptype == '0') {
				$("#cycle_exp_min_type").attr("checked",true);
				
				$('#startMinute').val(data.startMinute);
				$('#everyMinute').val(data.everyMinute);
			} else {
				$("#assign_exp_min_type").attr("checked",true);
				
				$.each(data.assignMins, function(i, n) {
					$("#assignMins_" + n).attr("checked", true);
				});
			}
			
			//set hour
			$(':checkbox[name=assignHour]').removeAttr("checked");
			if(data.hourexptype == '0') {
				$("#exp_hh_type_0").attr("checked",true);
			} else {
				$("#exp_hh_type_1").attr("checked",true);
				
				$.each(data.assignHours, function(i, n) {
					$("#assignHours_" + n).attr("checked", true);
				});
			}
			
			//set day
			$(':checkbox[name=assignDays]').removeAttr("checked");
			if('undefined' != data.dayexptype && null != data.dayexptype) {
				if(data.dayexptype == '0') {
					$("#exp_day_type_0").attr("checked",true);
				} else {
					$("#exp_day_type_1").attr("checked",true);
					$.each(data.assignDays, function(i, n) {
						$("#assignDays_" + n).attr("checked", true);
					});
				}
			
			}
			
			//set month
			$(':checkbox[name=assignMonths]').removeAttr("checked");
			if(data.monthexptype == '0') {
				$("#exp_month_type_0").attr("checked",true);
			} else {
				$("#exp_month_type_1").attr("checked",true);
				
				$.each(data.assignMonths, function(i, n) {
					$("#assignMonths_" + n).attr("checked", true);
				});
			}
			
			//set week
			$(':checkbox[name=assignWeeks]').removeAttr("checked");
			if(data.useweekck == '1') {
				$("#use_week_ck").attr("checked",true);
			}
			if(data.weekexptype == '0') {
				$("#exp_week_type_0").attr("checked",true);
			} else {
				$("#exp_week_type_1").attr("checked",true);
				
				$.each(data.assignWeeks, function(i, n) {
					$("#assignWeeks_" + n).attr("checked", true);
				});
			}
			
			//set year
			if(data.useyearck == '1') {
				$("#use_year_ck").attr("checked",true);
			} else {
				$("#use_year_ck").removeAttr("checked");
			}
			if(data.yearexptype == '0') {
				$("#exp_year_type_0").attr("checked",true);
				$('#yearCronExp').val('');
			} else {
				$("#exp_year_type_1").attr("checked",true);
				
				$('#yearCronExp').val(data.yearSet);
			}
		},
		error: DWZ.ajaxError
	});
}

function createCronExp() {
	$.ajax({
		url : '<%=request.getContextPath()%>/json/generateCronExp.action',
		type : 'post',
		dataType: "json",
		data : $('form[name=cronExpForm]').serializeArray(),
		success: function(data, textStatus) {
			//alertMsg.correct(textStatus);
			
			$('#secLabel').val(data.secLabel);
			$('#minLabel').val(data.minLabel);
			$('#hhLabel').val(data.hhLabel);
			$('#dayLabel').val(data.dayLabel);
			$('#monthLabel').val(data.monthLabel);
			$('#weekLabel').val(data.weekLabel);
			$('#yearLabel').val(data.yearLabel);
			
			$('#expressLabel').val(data.cronExpression);
			
			$('#startDateLabel').val(data.startDate);
			
			$('table#schedulerListTable tbody').empty();
			if(!$.isEmptyObject(data.schedulerNextResults)) {
				var style_tr = '';
				$.each(data.schedulerNextResults, function(i, n) {
					style_tr = (i % 2 == 0) ? ' class="trbg selected"' : '';
					$('table#schedulerListTable tbody').append('<tr' + style_tr + '>'
						+ '<td align="center">' + (i + 1) + '</td><td>'
						+ n + '</td></tr>');
				});
			}
			
			alertMsg.correct("Generate Cron Expression [" + data.cronExpression + "]");
		},
		error: DWZ.ajaxError
	});
}
</script>
<!--
<div class="pageHeader">
	<div class="searchBar">
		<ul class="searchContent">
			<li>
				Cron Expression验证工具
			</li>
		</ul>
	</div>
</div>
-->
<div class="pageContent">
<form action="json/generateCronExp.action" name="cronExpForm" method="post" class="pageForm required-validate" onsubmit="return validateCallback(this)">
	<div class="pageFormContent" layoutH="100">
	<div class="searchBar">
		<a class="buttonActive" href="javascript:createCronExp();"><span>Create Cron Expression</span></a>
		<a class="buttonActive" id="copyCronExpBtn" ><span>Copy Cron Expression</span></a>
	</div>
	<div class="panel collapse">
		<h1>Plan Execute Time</h1>
		<div class="tabs" currentIndex="0" eventType="click">
		<div class="tabsHeader">
			<div class="tabsHeaderContent">
				<ul>
					<li><a href="javascript:;"><span>Minute</span></a></li>
					<li><a href="javascript:;"><span>Hour</span></a></li>
					<li><a href="javascript:;"><span>Day</span></a></li>
					<li><a href="javascript:;"><span>Month</span></a></li>
					<li><a href="javascript:;"><span>Week</span></a></li>
					<li><a href="javascript:;"><span>Year</span></a></li>
				</ul>
			</div>
		</div>
		<div class="tabsContent" style="height:210px;">
		<div class="nowrap">
			<dl>
				<dt>
					<label><input type="radio" name="minuteexptype" value="0" id="cycle_exp_min_type" checked="checked"/>Cycle:</label>
				</dt>
				<dd>
					<span class="info">From</span>
					<input id="startMinute" name="startMinute" class="digits" max="59" min="0" type="text" value="0" alt=""/>
					<span class="info">Minute start, Every</span>
					<input id="everyMinute" name="everyMinute" type="text" class="digits" max="59" min="0" value="0" alt=""/>
					<span class="info">Minute Execute</span>
				</dd>
			</dl>
			<div class="divider"></div>
			<dl>
				<dt>
					<label><input type="radio" name="minuteexptype" value="1" id="assign_exp_min_type"/>Assign:</label>
				</dt>
				<dd>
					<c:forEach var="i" begin="0" end="59" step="1">
						<label style="width: 50px;"><input type="checkbox" name="assignMins" value="${i}" id="assignMins_${i}"/>${i}</label>
					</c:forEach>
				</dd>
			</dl>
		</div>
		<!-- 2 -->
		<div class="nowrap">
			<dl>
				<dt>
					<label><input type="radio" name="hourexptype" value="0" id="exp_hh_type_0" checked="checked"/>Per Hour</label>
				</dt>
			</dl>
			<div class="divider"></div>
			<dl>
				<dt>
					<label><input type="radio" name="hourexptype" value="1" id="exp_hh_type_1"/>Assign:</label>
				</dt>
				<dd>
					<c:forEach var="i" begin="0" end="23" step="1">
						<label style="width: 50px;"><input type="checkbox" name="assignHours" value="${i}" id="assignHours_${i}"/>
						<c:choose>
						<c:when test="${i < 12}"><font color="blue">${i}</font></c:when>
						<c:otherwise><font color="green">${i}</font></c:otherwise>
						</c:choose>
						</label>
					</c:forEach>
				</dd>
			</dl>
		</div>
		<!-- 3 -->
		<div class="nowrap">
			<dl>
				<dt>
					<label><input type="radio" name="dayexptype" value="0" id="exp_day_type_0" checked="checked"/>Per Day:</label>
				</dt>
			</dl>
			<div class="divider"></div>
			<dl>
				<dt>
					<label><input type="radio" name="dayexptype" value="1" id="exp_day_type_1"/>Assign:</label>
				</dt>
				<dd>
					<c:forEach var="i" begin="0" end="31" step="1">
						<label style="width: 50px;"><input type="checkbox" name="assignDays" value="${i}" id="assignDays_${i}"/>${i}</label>
					</c:forEach>
				</dd>
			</dl>
		</div>
		<!-- 4 -->
		<div class="nowrap">
			<dl>
				<dt>
					<label><input type="radio" name="monthexptype" value="0" id="exp_month_type_0" checked="checked"/>Per Month:</label>
				</dt>
			</dl>
			<div class="divider"></div>
			<dl>
				<dt>
					<label><input type="radio" name="monthexptype" value="1" id="exp_month_type_1"/>Assign:</label>
				</dt>
				<dd>
					<c:forEach var="i" begin="1" end="12" step="1">
						<label style="width: 50px;"><input type="checkbox" name="assignMonths" value="${i}" id="assignMonths_${i}"/>${i}</label>
					</c:forEach>
				</dd>
			</dl>
		</div>
		<!-- week -->
		<div class="nowrap">
			<dl>
				<dt>
					<label><input type="checkbox" name="useweekck" value="1" id="use_week_ck" />Use Week</label>
				</dt>
			</dl>
			<div class="divider"></div>
			<dl>
				<dt>
					<label><input type="radio" name="weekexptype" value="0" id="exp_week_type_0" checked="checked"/>Per Week:</label>
				</dt>
			</dl>
			<dl>
				<dt>
					<label><input type="radio" name="weekexptype" value="1" id="exp_week_type_1"/>Assign:</label>
				</dt>
				<dd>
					<label style="width: 100px;"><input type="checkbox" name="assignWeeks" value="1" id="assignWeeks_1"/>Sunday</label>
					<label style="width: 100px;"><input type="checkbox" name="assignWeeks" value="2" id="assignWeeks_2"/>Monday</label>
					<label style="width: 100px;"><input type="checkbox" name="assignWeeks" value="3" id="assignWeeks_3"/>Tuesday</label>
					<label style="width: 100px;"><input type="checkbox" name="assignWeeks" value="4" id="assignWeeks_4"/>Wednesday</label>
					<label style="width: 100px;"><input type="checkbox" name="assignWeeks" value="5" id="assignWeeks_5"/>Thursday</label>
					<label style="width: 100px;"><input type="checkbox" name="assignWeeks" value="6" id="assignWeeks_6"/>Friday</label>
					<label style="width: 100px;"><input type="checkbox" name="assignWeeks" value="7" id="assignWeeks_7"/>Saturday</label>
				</dd>
			</dl>
		</div>
		<!-- year -->
		<div class="nowrap">
			<dl>
				<dt>
					<label><input type="checkbox" name="useyearck" value="1" id="use_year_ck" />Use Year</label>
				</dt>
			</dl>
			<div class="divider"></div>
			<dl>
				<dt>
					<label><input type="radio" name="yearexptype" value="0" id="exp_year_type_0"/>Per Year:</label>
				</dt>
			</dl>
			<dl>
				<dt>
					<label><input type="radio" name="yearexptype" value="1" id="exp_year_type_1"/>Assign:</label>
				</dt>
				<dd>
					<span class="info">year</span>
					<input id="yearCronExp" name="assignYearCron" type="text" value="0" alt="请输入Quart的Cron的年份(不是必须项)"/>
				</dd>
			</dl>
		</div>
		</div>
		<div class="tabsFooter">
			<div class="tabsFooterContent"></div>
		</div>
	</div>
	</div>
	
	<div class="panel" defH="80">
		<h1>Expression</h1>
		<div>
			<table class="list" width="98%">
				<thead>
					<tr align="center">
						<th width="120"></th>
						<th>Second</th>
						<th>Minute</th>
						<th>Hour</th>
						<th>Day</th>
						<th>Month</th>
						<th>Week</th>
						<th>Year</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td align="right">Field:</td>
						<td><input type="text" id="secLabel"/></td>
						<td><input type="text" id="minLabel"/></td>
						<td><input type="text" id="hhLabel"/></td>
						<td><input type="text" id="dayLabel"/></td>
						<td><input type="text" id="monthLabel"/></td>
						<td><input type="text" id="weekLabel"/></td>
						<td><input type="text" id="yearLabel"/></td>
					</tr>
					<tr>
						<td align="right">Crop Expression:</td>
						<td colspan="5"><input type="text" id="expressLabel" size="100"/></td>
						<td><a class="buttonActive" href="javascript:resolve2UI();"><span>Resolve To UI</span></a></td>
					</tr>
				</tbody>
			</table>
		</div>
		</div>
		
		<div class="panel collapse">
		<h1>Plan Execute Time</h1>
		<div>
			<div><font color="red">Start Time:</font><input type="text" id="startDateLabel" readonly="readonly" style="float: inherit; width: 500px; margin-left: 3em;"/></div>
			<div style="margin: 1em 0 5px 0; font-size: large;">Execute Time</div>
			<table class="list" width="98%" id="schedulerListTable">
				<thead>
					<tr align="center">
						<th width="30"></th>
						<th>Reference Fire Time</th>
					</tr>
				</thead>
				<tbody></tbody>
			</table>
		</div>
	</div>
</div>
</form>
</div>