var query_data = {
	"page_list_HandleInformation.pageIndex" : "1",
	//拘留起始时间
	"page_list_HandleInformation.handle_StartTimeaOfDetention_start_time" : "",
	"page_list_HandleInformation.handle_StartTimeaOfDetention_stop_time" : "",
	//逮捕时间
	"page_list_HandleInformation.handle_arrestTime_start_time" : "",
	"page_list_HandleInformation.handle_arrestTime_stop_time" : "",
	//起诉时间
	"page_list_HandleInformation.handle_prosecuteTime_start_time" : "",
	"page_list_HandleInformation.handle_prosecuteTime_stop_time" : "",
	//退查时间
	"page_list_HandleInformation.handle_checkbackTime_start_time" : "",
	"page_list_HandleInformation.handle_checkbackTime_stop_time" : "",
	//取保候审时间
	"page_list_HandleInformation.handle_pbatTime_start_time" : "",
	"page_list_HandleInformation.handle_pbatTime_stop_time" : "",
	//监视居住时间
	"page_list_HandleInformation.handle_lhusTime_start_time" : "",
	"page_list_HandleInformation.handle_lhusTime_stop_time" : "",
	//拘留天数
	"page_list_HandleInformation.handle_detentionDay" : "",
	//行政案件名称
	"page_list_HandleInformation.handle_administrativeCase" : "",
	//中队长
	"page_list_HandleInformation.handle_squadronleader" : "",
	//办案民警
	"page_list_HandleInformation.handle_PoliceInHandlingCases" : "",
};
//当前页面分页信息
var page_infomantion = {
	pageIndex : 1,
	totalRecords : 1,
	pageSize : 20,
	totalPages : 1,
	HavePrePage : false,
	HaveNextPage : false,
}


var Vm = new Vue({
	el : "#vue-main",
	data : {
		peopleNum : [ '0' ],
		dayShow : [ false ],
		caseCategory : 1 //案件类别
	},
	methods : {
		processingTypeChoose (event, index) {
			if (this.caseCategory == 1) {
				if (event.target.value == "1") {
					this.dayShow[index] = true;
				} else {
					this.dayShow[index] = false;
				}
			}
		}
	}
});

// 选择全部
var selectAll = function(event) {
	if (event.checked) {
		var che = document.getElementsByName("chooseCheckBox");
		for (var int = 0; int < che.length; int++) {
			che[int].checked = true;
		}
	} else {
		var che = document.getElementsByName("chooseCheckBox");
		for (var int = 0; int < che.length; int++) {
			che[int].checked = false;
		}
	}
}

var Handle_delete = function() {
	var formData = new FormData();
	$('.Handle_table_info tbody input:checked').each(function() {
		formData.append("useHandleInformationNumList", $(this).attr('id'));
	});
	$.confirm({
		title : '确定删除?',
		smoothContent : false,
		content : false,
		autoClose : 'cancelAction|10000',
		buttons : {
			deleteUser : {
				btnClass : 'btn-danger',
				text : '确认',
				action : function() {
					$.ajax({
						url : '/xsjsglxt/case/Handle_remove_HandleInformationList',
						type : 'post',
						data : formData,
						processData : false,
						contentType : false,
						dataType : 'text',
						success : function(data, text) {
							if (text == "success") {
								toastr.success("删除成功！");
								//获取对应option中的value值
								get_ListHandleInformationByPageAndSearch(query_data);
							} else {
								toastr.error("删除失败！");
							}
						}
					});
				}
			},
			cancelAction : {
				btnClass : 'btn-blue',
				text : '取消',
			}
		}
	});
}


//办案查询
var handle_query = function() {
	$.each($('#Handle_query form').serializeArray(), function(k, v) {
		query_data[v.name] = v.value;
	});
	$("#Handle_query").modal('hide');
	get_ListHandleInformationByPageAndSearch(query_data);
	toastr.success('查询成功!');
}

$(function() {
	get_ListHandleInformationByPageAndSearch(query_data);

	//Handle_delete删除办案管理事件
	$('.Handle_delete').click(Handle_delete);

	//办案添加
	$('#Handle_input').on('show.bs.modal', function() {
		if (!$('input[name="handle.handle_orderNumber"]').val()) {
			$.post('/xsjsglxt/case/Handle_xuhao', function(json_data, text_data) {
				$('input[name="handle.handle_orderNumber"]').val(json_data);
			}, 'json');
		}
	})
	//清除内容
	$(".modal").on('hidden.bs.modal', function() {
		$(this).find('input[type!="radio"][type!="hidden"]').val('');
		$(this).find('select').find('option:first-child').attr("selected", "selected");
		$('#Handle_input').find('.handle_modify').hide();
		$('#Handle_input').find('.handle_input').show();
	})
	//中对长和办案民警
	$("#Handle_input").on('show.bs.modal', function() {
		/*$.post('/xsjsglxt/case/Case_AllCase', {}, function(Case_data) {
			//所有案件循环
			var option = '';
			for (var len = 0; len < Case_data.length; len++) {
				option += '<option value="' + Case_data[len].xsjsglxt_case_id + '">' + Case_data[len].case_name + '</option>';
			}
			$('select[name="handle.handle_Case"]').html(option).selectpicker('refresh');
		}, 'json');*/
		$.post('/xsjsglxt/team/Staff_getHandleCenter', {}, function(msg) {
			$('select[name="handle.handle_squadronleader"]').html(function() {
				var option = "";
				for (const key in msg) {
					option += `<option value="${msg[key]["xsjsglxt_name"]}">${msg[key]["xsjsglxt_name"]}</option>`;
				}
				return option;
			}).selectpicker('refresh');
		}, 'json');
		$.post('/xsjsglxt/team/Staff_getHandleCheck', {}, function(msg) {
			$('select[name="handle.handle_PoliceInHandlingCases"]').html(function() {
				var option = "";
				for (const key in msg) {
					option += `<option value="${msg[key]["xsjsglxt_name"]}">${msg[key]["xsjsglxt_name"]}</option>`;
				}
				return option;
			}).selectpicker('refresh');
		}, 'json');
	})

	$('.handle_input').click(function() {
		var handl_data = $('#Handle_input form').serialize();
		var dta = $('input[name="handle.handle_StartTimeaOfDetention"]').val();
		var day = $('select[name="handle.handle_detentionDay"]').val();
		var time_end = '&handle.handle_EndTimeaOfDetention=' + addDate(dta, day);
		//handl_data += '&handle.handle_administrativeCase=' + $('select[name="handle.handle_Case"] option:selected').text();
		handl_data += time_end;
		$.post('/xsjsglxt/case/Handle_saveHandle', handl_data, function(xhr) {
			$('#Handle_input').modal('hide');
			if (xhr == "success") {
				toastr.success("添加成功！");
				//获取对应option中的value值
				get_ListHandleInformationByPageAndSearch(query_data);
			} else {
				toastr.error("添加失败！");
			}
		}, 'text');
	});

	$('.handle_modify').click(function() {
		var handl_data = $('#Handle_input form').serialize();
		var dta = $('input[name="handle.handle_StartTimeaOfDetention"]').val();
		var day = $('select[name="handle.handle_detentionDay"]').val();
		var time_end = '&handle.handle_EndTimeaOfDetention=' + addDate(dta, day);
		var id = $('.handle_modify').attr('id');
		//handl_data += '&handle.handle_administrativeCase=' + $('select[name="handle.handle_Case"] option:selected').text();
		handl_data += time_end;
		handl_data += '&handle.xsjsglxt_handle_id=' + id;
		$.post('/xsjsglxt/case/Handle_updateHandleInformation', handl_data, function(xhr) {
			$('#Handle_input').modal('hide');
			if (xhr == "success") {
				toastr.success("修改成功！");
				get_ListHandleInformationByPageAndSearch(query_data);
			} else {
				toastr.error("添加失败！");
			}
		}, 'text');
	});

	//办案查询
	$('.handle_query').click(handle_query);
	//清空查询
	$('.handle_empty').click(function() {
		$('#Handle_query input,select').val("");
	});

	//tr事件委托
	$('.Handle_table_info tbody').on('click', function(e) {
		var target = e.target;
		var na = e.target.tagName;
		if (na == 'INPUT') {
			e.stopPropagation();
		} else if (na == 'TD') {
			var ID = $(target).parents('tr').attr('id');
			$.post('/xsjsglxt/case/Handle_HandleInformationOne', {
				'handle.xsjsglxt_handle_id' : ID
			}, function(msg) {
				//input非单选框
				$('#Handle_input form').find('input').each(function() {
					if (this.name) {
						var ele_name = this.name;
						var key = ele_name.split('.')[1];
						if (msg[key] == '是') {
							$(this).val(msg[key]);
							$(this).siblings('label').find('input[type="radio"][value="是"]').attr('checked', 'true');
						} else if (msg[key] == '否') {
							$(this).val(msg[key]);
							$(this).siblings('label').find('input[type="radio"][value="否"]').attr('checked', 'true');
						} else {
							$(this).val(msg[key]);
						}
					}

				});
				/*//input单选框
				$('#Handle_input form').find('input[type="radio"]').each(function() {
					var ele_name = $(this).attr('name');
					var key = ele_name.split('.')[1];
					console.log(key);
					console.log(msg[key]);
					if (msg[key] == '是') {
						$(this).sblings('lable').find('input[type=="radio"][value="是"]').attr('checked', 'true');
					} else if (msg[key] == '否') {
						$(this).sblings('lable').find('input[type=="radio"][value="否"]').attr('checked', 'true');
					}
				});*/
				//select
				$('#Handle_input form').find('select').each(function() {
					if (this.name) {
						var ele_name = this.name;
						var key = ele_name.split('.')[1];
						$(this).selectpicker('val', msg[key]);
					}
				});
				$('.handle_modify').attr('id', msg['xsjsglxt_handle_id']);
				$('#Handle_input').find('.handle_modify').show();
				$('#Handle_input').find('.handle_input').hide();
				$('#Handle_input').modal('show');
			}, 'json');
		}
	});

})

function get_ListHandleInformationByPageAndSearch(data) {
	$.post('/xsjsglxt/case/Handle_ListHandleInformationByPageAndSearch', data, function(xhr_data) {
		var Handle = xhr_data.listHandle;
		var str = '';
		for (var len = 0; len < Handle.length; len++) {
			str += '<tr class="tr_select" id="' + Handle[len].xsjsglxt_handle_id + '">';
			str += '<td style="padding-left: 5px;"><input name="chooseCheckBox" id="' + Handle[len].xsjsglxt_handle_id + '" type="checkbox"></td>'
			str += '<td style="padding-left: 5px;">' + Handle[len].handle_orderNumber + '</td>';
			str += '<td>' + (Handle[len].handle_administrativeCase).replace('萍乡市安源区', '') + '</td>';
			str += '<td>' + Handle[len].handle_suspectName + '</td>';
			str += '<td>' + Handle[len].handle_administrativeAttachment + '</td>';
			str += '<td>' + Handle[len].handle_arrest + '</td>';
			str += '<td>' + Handle[len].handle_prosecute + '</td>';
			str += '<td>' + Handle[len].handle_checkback + '</td>';
			str += '<td>' + Handle[len].handle_pbat + '</td>';
			str += '<td>' + Handle[len].handle_lhus + '</td>';
			str += '<td>' + Handle[len].handle_FinancialName + '</td>';
			str += '<td>' + Handle[len].handle_handTime + '</td>';
			str += '<td>' + Handle[len].handle_handleTime + '</td>';
			str += '<td>' + Handle[len].handle_processMode + '</td>';
			str += '<td>' + Handle[len].handle_squadronleader + '</td>';
			str += '<td  style="padding-left: 5px;">' + Handle[len].handle_PoliceInHandlingCases + '</td>';
			str += '</tr>';
		}
		$('.Handle_table_info tbody').html(str);
		//当前页数:1 共:1页
		$('.info').html('当前页数:' + xhr_data.pageIndex + ' 共:' + xhr_data.totalPages);
		// 分页信息存入page_infomantion中
		page_infomantion.pageIndex = xhr_data.pageIndex; // 当前页数
		page_infomantion.totalRecords = xhr_data.totalRecords; // 总页数
		page_infomantion.pageSize = xhr_data.pageSize; // 每页记录数
		page_infomantion.totalPages = xhr_data.totalPages; // 总记录数
		page_infomantion.HavePrePage = xhr_data.HavePrePage; // 是否有上一页
		page_infomantion.HaveNextPage = xhr_data.HaveNextPage; // 是否有下一页
	}, 'json')
}

/*//input反选
function invertSelection(ele) {
	console.log(type);
	var type = $(ele).val();
	if (type == '是') {
		$(ele).sblings('lable').find('input[type=="radio"][value="是"]').attr('checked', 'true');
	} else if (type == '否') {
		$(ele).sblings('lable').find('input[type=="radio"][value="否"]').attr('checked', 'true');
	}
}*/
//日期加减
function addDate(date, days) {
	var d = new Date(date);
	d.setDate(d.getDate() + parseInt(days));
	var m_ = d.getMonth() + 1;
	var day_ = d.getDate();
	if (day_ < 10) {
		day_ = '0' + day_;
	}
	if (m_ < 10) {
		m_ = '0' + m_;
	}
	return d.getFullYear() + '-' + m_ + '-' + day_;
}

//radio
function chose(obj) {
	$(obj).parent().siblings('input').val($(obj).val());
}

//首页
function firstPage() {
	if (page_infomantion.pageIndex == 1) {
		toastr.error('已经是第一页！');
		return;
	}
	query_data['page_list_HandleInformation.pageIndex'] = 1;
	get_ListHandleInformationByPageAndSearch(query_data);
}
//上一页
function prePage() {
	if (!page_infomantion.HavePrePage) {
		toastr.error('已经是第一页！');
		return;
	}
	query_data['page_list_HandleInformation.pageIndex'] = page_infomantion.pageIndex - 1;
	get_ListHandleInformationByPageAndSearch(query_data);
}
//下一页
function nextPage() {
	if (!page_infomantion.HaveNextPage) {
		toastr.error('已经是最后一页！');
		return;
	}
	query_data['page_list_HandleInformation.pageIndex'] = page_infomantion.pageIndex + 1;
	get_ListHandleInformationByPageAndSearch(query_data);
}
//尾页
function lastPage() {
	if (page_infomantion.pageIndex == page_infomantion.totalPages) {
		toastr.error('已经是最后一页！');
		return;
	}
	query_data['page_list_HandleInformation.pageIndex'] = page_infomantion.totalPages;
	get_ListHandleInformationByPageAndSearch(query_data);
}
//跳转到n页
function toPage() {
	var topage = $('#skipPage').val();
	if (topage > page_infomantion.totalPages || topage < 0) {
		toastr.info('页码有误，请重新输入');
		return;
	}
	query_data['page_list_HandleInformation.pageIndex'] = topage;
	get_ListHandleInformationByPageAndSearch(query_data);
}