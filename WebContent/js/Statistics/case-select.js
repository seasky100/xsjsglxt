/**
 * 
 */

var secondCase = [ '',
		[ '', '入室盗窃案', '盗窃汽车案', '盗窃车内物品案', '电缆盗窃案', '单位盗窃案', '其它盗窃案' ],
		[ '', '入户抢劫案', '拦路抢劫案', '麻醉抢劫案', '其它抢劫案' ],
		[ '', '尾随抢夺案', '飞车抢夺案', '其它抢夺案' ], '', '', '', '', '', '', '', '', '',
		'' ];

var changeSecondSelect = function(event) {
	console.log("触发了方法");
	var selectDiv = document.getElementById("caseSelect");
	if (document.getElementById("secondCase") != null) {
		selectDiv.removeChild(document.getElementById("secondCase"));
	}
	if (event.selectedIndex > 0 && event.selectedIndex <= 3) {
		var currCase = secondCase[event.selectedIndex];
		var caseSelect = document.createElement("select");
		caseSelect.setAttribute("class", "form-control");
		caseSelect.setAttribute("id", "secondCase");
		caseSelect
				.setAttribute("style", "width: 200px; display: inline-block;");
		for (var int = 0; int < currCase.length; int++) {
			var caseOption = document.createElement("option");
			caseOption.setAttribute("value", currCase[int]);
			if (int == 0) {
				caseOption.innerHTML = "请选择子类别";
			} else {
				caseOption.innerHTML = currCase[int];
			}
			caseSelect.appendChild(caseOption);
		}
		selectDiv.appendChild(caseSelect);
	}
}

var highLightShow = function(event) {
	var 
}