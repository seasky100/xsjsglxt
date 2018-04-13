/**
 * 
 */

var deleteScheduling = function() {
	$
			.confirm({
				title : '确定要删除吗！',
				type : 'red',
				content : '',
				autoClose : "cancel|10000",
				buttons : {
					'delete' : {
						text : '确定',
						action : function() {
							var formData = new FormData();
							var che = document
									.getElementsByName("chooseCheckBox");
							for (var int = 0; int < che.length; int++) {
								if (che[int].checked) {
									formData.append("scheduling_id",
											che[int].value);
								}
							}
							$
									.ajax({
										url : '/xsjsglxt/scheduling/Scheduling_deleteScheduling',
										type : 'POST',
										data : formData,
										contentType : false,
										processData : false,
										success : function(data) {
											if (data == "deleteSuccess") {
												toastr.success("删除成功");
												queryConditionTemp.currPage = 1;
												loadScheduling();
											} else
												toastr.error("删除失败");
										}
									});
						}
					},
					'cancel' : {
						text : '取消',
						btnClass : 'btn-red'
					}
				}
			})
}
var updateScheduling = function(event) {
	$
			.confirm({
				boxWidth : '500px',
				useBootstrap : false,
				title : '<i class="fa fa-pencil-square-o"></i>修改排班',
				type : 'green',
				content : '<table class="table bordered-table"><tr><td>带班领导：</td><td style="text-align:center;" class="loadingLay"><i class="fa fa-spinner fa-spin fa-3x fa-fw"></i></td><td class="contentName" style="display:none;"><select id="leader" style="width: 250px;" class="form-control selectpicker" data-live-search="true" title="请选择主班领导"></select></td></tr>'
						+ '<tr><td>主班：</td><td class="loadingLay" style="text-align:center;"><i class="fa fa-spinner fa-spin fa-3x fa-fw"></i></td></td><td class="contentName" style="display:none;"><select id="main" style="width: 250px;" class="form-control selectpicker" data-live-search="true" title="请选择主班"></select></td></tr>'
						+ '<tr><td>副班：</td><td class="loadingLay" style="text-align:center;"><i class="fa fa-spinner fa-spin fa-3x fa-fw"></i></td><td class="contentName" style="display:none;"><select id="assistant" style="width: 250px;" class="form-control selectpicker" data-live-search="true" title="请选择副班"></select></td></tr>'
						+ '<tr><td>值班时间：</td><td><input id="scheTime" style="250px" class="form-control timeDate"></td></tr></table>',
				buttons : {
					save : {
						text : '<i class="fa fa-upload" aria-hidden="true"></i>修改',
						btnClass : 'btn-blue',
						action : function() {
							if ($('#leader').val() != ''
									&& $('#main').val() != ''
									&& $('#assistant').val() != ''
									&& $('#scheTime').val() != '') {
								var postData = {
									'scheduling.scheduling_leader' : $(
											'#leader').val(),
									'scheduling.scheduling_main' : $('#main')
											.val(),
									'scheduling.scheduling_assistant' : $(
											'#assistant').val(),
									'scheduling.scheduling_time' : $(
											'#scheTime').val(),
									'scheduling.xsjsglxt_scheduling_id' : event.id
								}
								$
										.ajax({
											url : '/xsjsglxt/scheduling/Scheduling_updateScheduling',
											type : 'POST',
											data : postData,
											success : function(data) {
												if (data == "updateSuccess") {
													toastr.success("修改成功");
													loadScheduling();
												} else {
													toastr.error("您修改的日期已经被占用");
													return false;
												}
											}
										});
							} else {
								toastr.error("不能有空项");
								return false;
							}
						}
					},
					close : {
						text : "<i class='fa fa-times' aria-hidden='true'></i>关闭",
						btnClass : "btn-red",
						action : function() {

						}
					}
				},
				onContentReady : function() {
					// 为下拉列表添加姓名
					$
							.ajax({
								url : '/xsjsglxt/team/Staff_getSchedulingStaff',
								type : 'GET',
								success : function(data) {
									var result = JSON.parse(data);
									for (var i = 0; i < result.staffLeader.length; i++) {
										$('#leader')
												.append(
														"<option value='"
																+ result.staffLeader[i].xsjsglxt_name
																+ "'>"
																+ result.staffLeader[i].xsjsglxt_name
																+ "</option>");
									}
									for (var i = 0; i < result.staffMain.length; i++) {
										$('#main')
												.append(
														"<option value='"
																+ result.staffMain[i].xsjsglxt_name
																+ "'>"
																+ result.staffMain[i].xsjsglxt_name
																+ "</option>");
									}
									for (var i = 0; i < result.staffAssitant.length; i++) {
										$('#assistant')
												.append(
														"<option value='"
																+ result.staffAssitant[i].xsjsglxt_name
																+ "'>"
																+ result.staffAssitant[i].xsjsglxt_name
																+ "</option>");
									}
									var d = {
										'scheduling.xsjsglxt_scheduling_id' : event.id
									};
									$
											.ajax({
												url : '/xsjsglxt/scheduling/Scheduling_getSchedulingById',
												type : 'POST',
												data : d,
												success : function(data) {
													var result = JSON
															.parse(data);
													$('#leader')
															.selectpicker(
																	'val',
																	result.scheduling_leader);
													$('#main')
															.selectpicker(
																	'val',
																	result.scheduling_main);
													$('#assistant')
															.selectpicker(
																	'val',
																	result.scheduling_assistant);
													$('#scheTime')
															.val(
																	result.scheduling_time);
													$(".selectpicker")
															.selectpicker(
																	'refresh');
													$(".loadingLay").hide();
													$(".contentName").show();
												}

											});

								}
							});

					$.datetimepicker.setLocale('ch');
					$('.timeDate').datetimepicker({
						yearStart : 1900, // 设置最小年份
						yearEnd : 2100, // 设置最大年份
						yearOffset : 0, // 年偏差
						timepicker : false, // 关闭时间选项
						format : 'Y-m-d', // 格式化日期年-月-日
						minDate : '1900/01/01', // 设置最小日期
						maxDate : '2030/01/01', // 设置最大日期
					});
				}
			});

}