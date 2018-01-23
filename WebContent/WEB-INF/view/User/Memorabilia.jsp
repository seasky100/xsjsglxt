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
<title>大事记</title>
<!-- -----------------模态框js文件引入--------------------------------  -->
<script type="text/javascript" src="<%=basePath %>js/memorabilia/createMemorabilia.js"></script>
</head>
<body>
	<!-----------------------------------------引入导航条 ------------------------------------------------------>
	<s:action name="User_navbar" namespace="/user" executeResult="true" />
	<!-----------------------------------------主面板---------------------------------------------------------  -->
	<div style="margin: 80px 0 0 0; float: left; width: 100%;">
		<div class="panel" style="width: 95%; margin: 20px auto; padding-left:20px; padding-right:20px;">
			<div class="headDiv">
				<button class="btn btn-default" onclick="createConfirm()"><i class="fa fa-pencil-square-o"></i>填写大事记</button>
			</div>
			
			
			
			
			<div id="bottomPage" style="padding: 20px;">
				<span>当前页数:<span id="currPage">1</span></span>
				<span>共:<span id="totalPage">2</span>页</span>
				<span  onclick="skipToIndexPage()" id="indexPage" class="pageOperation">首页</span>
				<span onclick="skipToPrimaryPage()" id="previousPage" class="pageOperation">上一页</span>
				<span onclick="skipToNextPage()" id="nextPage" class="pageOperation">下一页</span>
				<span onclick="skipToLastPage()" id="lastPage" class="pageOperation">末页</span>
				<span>
					<input  id="skipPage" class="form-control" type="text" style="display:inline-block; text-align: center; width: 60px; height: 30px;" class="queryInput">
					<button onclick="skipToArbitrarilyPage()" class="btn btn-default" style="height:30px;">跳转</button>
				</span>
			</div>
		
		</div>
	</div>
	<script type="text/javascript">
	$.datetimepicker.setLocale('ch');
	$('.startTime').datetimepicker({
		yearStart : 1990, // 设置最小年份
		yearEnd : 2050, // 设置最大年份
		yearOffset : 0, // 年偏差
		timepicker : false, // 关闭时间选项
		format : 'Y-m-d H:i', // 格式化日期年-月-日
		minDate : '1900/01/01 00:00', // 设置最小日期
		maxDate : '2030/01/01 00:00', // 设置最大日期
	});
	</script>
	
</body>
</html>