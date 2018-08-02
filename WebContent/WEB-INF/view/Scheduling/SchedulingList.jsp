<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<title>排班管理</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<script src="<%=basePath%>js/Scheduling/createScheduling.js"></script>
<script src="<%=basePath%>js/Scheduling/showSchedulingList.js"></script>
<script src="<%=basePath%>js/Scheduling/selectCheckBox.js"></script>
<script src="<%=basePath%>js/Scheduling/managerScheduling.js"></script>
<style type="text/css">
.pageOperation {
	cursor: pointer;
}

a {
	cursor: pointer;
}

i {
	cursor: pointer;
}

#no_choose li:hover {
	cursor: pointer;
	background-color: #ddd;
}

#no_choose {
	padding: 0;
}

#no_choose li {
	list-style-type: none;
	color: black;
	font-size: 16px;
	padding-left: 5px;
}

#choose_ li:hover {
	cursor: pointer;
	background-color: #ddd;
}

#choose_ {
	padding: 0;
}

#choose_ li {
	list-style-type: none;
	color: black;
	font-size: 16px;
	padding-left: 5px;
}
</style>
</head>
<body>
	<!-----------------------------------------引入导航条 ------------------------------------------------------>
	<s:action name="User_navbar" namespace="/user" executeResult="true" />
	<!-----------------------------------------主面板---------------------------------------------------------  -->
	<div style="margin: 80px 0 0 0; float: left; width: 100%;">
		<div class="panel"
			style="width: 95%; margin: 20px auto; padding-left: 20px; padding-right: 20px;">
			<div id="queryCondition"
				style="margin-left: 10px; margin-right: 10px; margin-top: 10px;">
				<button class="btn btn-default managerRole"
					onclick="createScheduling()">
					<i class="fa fa-pencil-square-o"></i>增加值班
				</button>
				<button class="btn btn-default managerRole"
					onclick="printCurrPage()">
					<i class="fa fa-print"></i>导出值班表
				</button>
				<button class="btn btn-default managerRole" onclick="intoStastics()">
					<i class="fa fa-arrow-circle-right"></i>统计
				</button>
				<button class="btn btn-danger managerRole"
					onclick="deleteScheduling()">
					<i class="fa fa-trash-o"></i>删除值班
				</button>
				<div id="query" style="float: right;">
					<label>时间筛选</label> <input class="form-control startTime"
						onchange="changeSort()" type="text" id="timeStart"
						style="width: 150px; display: inline-block;">至 <input
						class="form-control startTime" onchange="changeSort()" type="text"
						style="width: 150px; display: inline-block;" id="timeEnd">
					<label>查询</label> <input type="text" id="searchInput"
						class="form-control" style="width: 250px; display: inline-block;"
						oninput="changeSort()" placeholder="请输入搜索内容">
				</div>
			</div>
			<div id="loadingLayer" style="margin: 0 auto; width: 45px;">
				<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i>
			</div>
			<div id="tableContent" style="margin-top: 30px; display: none;">
				<table class="table table-hover table-condensed"
					style="text-align: center;">
					<thead>
						<tr style="height: 30; color: black;">
							<td rowspan="2"><label class="fancy-checkbox" style="cursor: pointer;"> <input
									onclick="selectAll(this)" type="checkbox"> 全选
							</label></td>
							<td rowspan="2">日期</td>
							<td rowspan="2">带班领导</td>
							<td colspan="5">值班人员</td>
							<td rowspan="2">巡逻人员</td>
							<td rowspan="2">加班人员</td>
							<td rowspan="2">外协人员</td>
							<td rowspan="2">出差人员</td>
							<td rowspan="2">值班记录</td>
						</tr>
						<tr style="height: 30; color: black;">
							<td>侦查值班</td>
							<td>技术值班</td>
							<td>法医值班</td>
							<td>侦查辅警</td>
							<td>技术辅警</td>
						</tr>
					</thead>
					<tbody>
						<template v-for="schedulingDTO in vo.schedulingDTOList">
						<tr style="height: 30;">
							<td><label class="fancy-checkbox"><input type="checkbox"
									name="chooseCheckBox"
									:value="schedulingDTO.xsjsglxt_scheduling_id"><span></span></label></td>
							<td><a onclick="updateScheduling(this)"
								:id="schedulingDTO.xsjsglxt_scheduling_id">{{
									schedulingDTO.scheduling_time }}</a></td>
							<td>{{ schedulingDTO.scheduling_leader }}</td>
							<td>{{ schedulingDTO.scheduling_main }}</td>
							<td>{{ schedulingDTO.scheduling_mainTec }}</td>
							<td>{{ schedulingDTO.scheduling_main_doctor }}</td>
							<td>{{ schedulingDTO.scheduling_assistant_spy }}</td>
							<td>{{ schedulingDTO.scheduling_assistant_tech }}</td>
							<td>{{ schedulingDTO.scheduling_patrol }}</td>
							<td>{{ schedulingDTO.scheduling_overtime }}</td>
							<td>{{ schedulingDTO.scheduling_out_help }}</td>
							<td>{{ schedulingDTO.scheduling_evection }}</td>
							<td>
								
								<template v-if="nowDate==schedulingDTO.scheduling_time">
									<a :id="schedulingDTO.xsjsglxt_scheduling_id" onclick="updateLog(this)">修改记录</a>
								</template>
								<template v-else>
									<s:if test='#session.userSession.user_army_manager_power=="jurisdiction_admin"'>
										<a :id="schedulingDTO.xsjsglxt_scheduling_id" onclick="updateLog(this)">修改记录</a>
									</s:if>
									<s:else>
										<a :id="schedulingDTO.xsjsglxt_scheduling_id" onclick="viewLog(this)">查看记录</a>
									</s:else>
								</template>
								<%-- <s:if test='#session.userSession.user_army_manager_power=="jurisdiction_admin"'>
									<template v-if="nowDate==schedulingDTO.scheduling_time">
										<a :id="schedulingDTO.xsjsglxt_scheduling_id" onclick="updateLog(this)">修改记录</a>
									</template>
									<template v-else>
										<a :id="schedulingDTO.xsjsglxt_scheduling_id" onclick="viewLog(this)">查看记录</a>
									</template>
								</s:if> 
								<s:else>
									<a :id="schedulingDTO.xsjsglxt_scheduling_id" onclick="viewLog(this)">查看记录</a>
								</s:else> --%>
							</td>
						</tr>
						</template>
					</tbody>
				</table>
				<div id="bottomPage" style="padding: 20px;">
					<span>当前页数:<span id="currPage">{{ vo.currPage }}</span></span> <span>共:<span
						id="totalPage">{{ vo.totalPage }}</span>页
					</span> <span onclick="skipToIndexPage()" id="indexPage"
						class="pageOperation">首页</span> <span
						onclick="skipToPrimaryPage()" id="previousPage"
						class="pageOperation">上一页</span> <span onclick="skipToNextPage()"
						id="nextPage" class="pageOperation">下一页</span> <span
						onclick="skipToLastPage()" id="lastPage" class="pageOperation">末页</span>
					<span> <input id="skipPage" class="form-control" type="text"
						style="display: inline-block; text-align: center; width: 60px; height: 30px;"
						class="queryInput">
						<button onclick="skipToArbitrarilyPage()" class="btn btn-default"
							style="height: 30px;">跳转</button>
					</span>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript">
		$.datetimepicker.setLocale('ch');
		$('.startTime').datetimepicker({
			yearStart : 1900, // 设置最小年份
			yearEnd : 2100, // 设置最大年份
			yearOffset : 0, // 年偏差
			timepicker : false, // 关闭时间选项
			format : 'Y-m-d', // 格式化日期年-月-日
			minDate : '1900/01/01', // 设置最小日期
			maxDate : '2030/01/01', // 设置最大日期
		});
		$(function() {
			$("tr").css("height", "30px");
		})
	</script>
</body>
	<link rel="stylesheet" href="<%=basePath%>css/square/blue.css" />
	<script type="text/javascript" src="<%=basePath%>js/icheck.js"></script>
</html>