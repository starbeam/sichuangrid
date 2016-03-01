<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<jsp:include page="/includes/baseInclude.jsp" />
<% request.setAttribute("userName1",request.getParameter("userName"));%>
<% request.setAttribute("orgName1",request.getParameter("orgName"));%>
<style>
.countlistHead td{
	width: 54px;
}
</style>

<script type="text/javascript">
var thisTime=new Date();
var addMonth=thisTime.getMonth()+1;
var formatdate= thisTime.getFullYear()+"-"+addMonth+"-"+thisTime.getDate();
$("#time").text("填报日期："+formatdate);
timeShow();

$(function(){//初始化表格
	for(var y = 1; y <= 57; y ++){
		var td = "";
		for(var x = 1; x <= 37; x++){
			td = td + "<td id='"+ y + "_" + x +"'>0</td>";
		}
		$(".rowCount_" + y).append(td).html();
	}
})

function getReportData(){
	timeShow();
	$.ajax({
		url:'${path}/threeRecords/accountReport/getDataReport.action',
		data:{
			"orgId":$("#currentOrgId").attr("value"),
			"year":$("#year").val(),
			"month":$("#month").val()
		},
		success:function(data){
			var yCount = getJsonObjLength(data);
			for(var y = 1; y <= yCount; y ++){
				for(var x = 1; x <= 37; x++){
					$("#" + y + "_" + x).text(data[y.toString()][x.toString()]);
				}
			}
		}
	})
}

function timeShow(){
	$("#timeShow").text('中江县三本台账工作信息收集、处理、办结、情况统计表' + $("#year").val()+'年'+$("#month").val()+'月' + '（全县汇总表）');
	$("#orgName").text("填报单位：" + "${orgName1}");
	//$("#userName").text("填报人：" + "${userName1}");
	//$("#chargePerson").text("负责人：" + "${userName1}");
}

function getJsonObjLength(jsonObj) {
    var Length = 0;
    for (var item in jsonObj) {
        Length++;
    }
    return Length;
}
</script>
<div style="overflow: auto;height: 470px;width: 1150px;" class="reportData">
<table style="width: 2500px" class="countlist" id="monthReport">
	<tr> 
		<td colspan="41" id="timeShow" style="height: 50px;text-align: center;font-size: 22px;color: black;font-weight: bold;">
		中江县三本台账工作信息收集、处理、办结、情况统计表XXXX年XX月（全县汇总表）
		</td>
	</tr>
	<tr>
		<td colspan="8" id="orgName" style="text-align: center;font-size: 14px;height: 35px;">填报单位：${orgName}</td>
		<td colspan="8" id="userName" style="text-align: center;">填报人：</td>
		<td colspan="8" id="chargePerson" style="text-align: center;">负责人：</td>
		<td colspan="8" id="time" style="text-align: center;">填报日期：</td>
		<td colspan="9" style="text-align: center;">(单位：件)</td>
	</tr>


	<tr class="countlistHead">
		<td colspan="4" rowspan="2" style="text-align: center;">内容</td>
		<td rowspan="2" style="text-align: center;">合计</td>
		<td colspan="12" style="text-align: center;">民生工作</td>
		<td colspan="7" style="text-align: center;">困难工作</td>
		<td colspan="17" style="text-align: center;">稳定工作</td>
	</tr>
	<tr class="countlistHead">
		<td>小计</td><td>水利</td><td>交通</td><td>能源</td><td>教育</td><td>科技文本</td><td>医疗卫生</td><td>劳动与社会保障</td>
		<td>环境保护</td><td>城乡规划建设与管理</td><td>农业</td><td>其他</td><td>小计</td><td>住房</td><td>生活</td><td>医疗</td>
		<td>就业</td><td>就学</td><td>其他</td><td>小计</td><td>涉法涉诉</td><td>林水土</td><td>惠农政策及村（社区）政务财务</td>
		<td>民政问题</td><td>人口与医疗卫生</td><td>劳动保障</td><td>交通运输</td><td>城镇及综合执法</td><td>党纪政纪</td><td>教育</td>
		<td>企业改制</td><td>环境保护</td><td>组织人事</td><td>其他类</td><td>重点人员</td><td>其他</td>
	</tr>
	<tr class="rowCount_1">
		<td rowspan="13">全县累计总建账</td>
		<td colspan="3">合计</td>
	</tr>
	<tr class="rowCount_2">
		<td rowspan="3" colspan="2">其中上年接转数</td>
		<td width="150">村级接转数</td>
	</tr>
	<tr class="rowCount_3">
		<td>乡镇接转数</td>
	</tr>
	<tr class="rowCount_4">
		<td>县级部门接转数</td>
	</tr>
	<tr class="rowCount_5">
		<td colspan="3">村级建账数</td>
	</tr>
	<tr class="rowCount_6">
		<td rowspan="4">乡镇累计建账</td>
		<td colspan="2">乡镇建账数</td>
	</tr>
	<tr class="rowCount_7">
		<td rowspan="3">其中</td>
		<td>县委县政府及县领导班子有关领导交办数</td>
	</tr>
	<tr class="rowCount_8">
		<td>县人大议案建议意见交办数</td>
	</tr>
	<tr class="rowCount_9">
		<td>县政协提案建议意见交办数</td>
	</tr>
	<tr class="rowCount_10">
		<td rowspan="4">县级部门累计建账</td>
		<td colspan="2">县级部门建账数</td>
	</tr>
	<tr class="rowCount_11">
		<td rowspan="3">其中</td>
		<td>上级主管部门和县领导班子有关领导交办数</td>
	</tr>
	<tr class="rowCount_12">
		<td>县人大议案建议意见交办数</td>
	</tr>
	<tr class="rowCount_13">
		<td>县政协提案建议意见交办数</td>
	</tr>
	
	<tr class="rowCount_14">
		<td rowspan="13">全县累计总办结</td>
		<td colspan="3">合计</td>
	</tr>
	<tr class="rowCount_15">
		<td colspan="3">村级建账办结总数</td>
	</tr>
	<tr class="rowCount_16">
		<td rowspan="3">其中</td>
		<td colspan="2">实质性办结数</td>
	</tr>
	<tr class="rowCount_17">
		<td colspan="2">阶段性办结数</td>
	</tr>
	<tr class="rowCount_18">
		<td colspan="2">程序性办结数</td>
	</tr>
	<tr class="rowCount_19">
		<td colspan="3">乡镇建账办结总数</td>
	</tr>
	<tr class="rowCount_20">
		<td rowspan="3">其中</td>
		<td colspan="2">实质性办结数</td>
	</tr>
	<tr class="rowCount_21">
		<td colspan="2">阶段性办结数</td>
	</tr>
	<tr class="rowCount_22">
		<td colspan="2">程序性办结数</td>
	</tr>
	<tr class="rowCount_23">
		<td colspan="3">县级部门建账办结总数</td>
	</tr>
	<tr class="rowCount_24">
		<td rowspan="3">其中</td>
		<td colspan="2">实质性办结数</td>
	</tr>
	<tr class="rowCount_25">
		<td colspan="2">阶段性办结数</td>
	</tr>
	<tr class="rowCount_26">
		<td colspan="2">程序性办结数</td>
	</tr>
	
	<tr class="rowCount_27">
		<td rowspan="4">全县上月办理中</td>
		<td colspan="3">合计</td>
	</tr>
	<tr class="rowCount_28">
		<td colspan="3">村级建账上月办理中总数</td>
	</tr>
	<tr class="rowCount_29">
		<td colspan="3">乡镇建账上月办理中总数</td>
	</tr>
	<tr class="rowCount_30">
		<td colspan="3">县级部门建账上月办理中总数</td>
	</tr>
	
	<tr class="rowCount_31">
		<td rowspan="10">全县本月总建账</td>
		<td colspan="3">合计</td>
	</tr>
	<tr class="rowCount_32">
		<td colspan="3">村级建账数</td>
	</tr>
	<tr class="rowCount_33">
		<td colspan="3">乡镇建账数</td>
	</tr>
	<tr class="rowCount_34">
		<td rowspan="3">其中</td>
		<td colspan="2">县委县政府及县级领导班子有关领导交办数</td>
	</tr>
	<tr class="rowCount_35">
		<td colspan="2">县人大议案建议意见交办数</td>
	</tr>
	<tr class="rowCount_36">
		<td colspan="2">县政协提案建议意见交办数</td>
	</tr>
	<tr class="rowCount_37">
		<td colspan="3">县级部门建账数</td>
	</tr>
	<tr class="rowCount_38">
		<td rowspan="3">其中</td>
		<td colspan="2">上级主管部门和县级领导班子有关领导交办数</td>
	</tr>
	<tr class="rowCount_39">
		<td colspan="2">县人大议案建议意见交办数</td>
	</tr>
	<tr class="rowCount_40">
		<td colspan="2">县政协提案建议意见交办数</td>
	</tr>
	
	<tr class="rowCount_41">
		<td rowspan="13">全县本月总办结</td>
		<td colspan="3">合计</td>
	</tr>
	<tr class="rowCount_42">
		<td colspan="3">村级建账本月办结数</td>
	</tr>
	<tr class="rowCount_43">
		<td rowspan="3">其中</td>
		<td colspan="2">实质性办结数</td>
	</tr>
	<tr class="rowCount_44">
		<td colspan="2">阶段性办结数</td>
	</tr>
	<tr class="rowCount_45">
		<td colspan="2">程序性办结数</td>
	</tr>
	<tr class="rowCount_46">
		<td colspan="3">乡镇建账本月办结数</td>
	</tr>
	<tr class="rowCount_47">
		<td rowspan="3">其中</td>
		<td colspan="2">实质性办结数</td>
	</tr>
	<tr class="rowCount_48">
		<td colspan="2">阶段性办结数</td>
	</tr>
	<tr class="rowCount_49">
		<td colspan="2">程序性办结数</td>
	</tr>
	<tr class="rowCount_50">
		<td colspan="3">县级部门建账本月办结数</td>
	</tr>
	<tr class="rowCount_51">
		<td rowspan="3">其中</td>
		<td colspan="2">实质性办结数</td>
	</tr>
	<tr class="rowCount_52">
		<td colspan="2">阶段性办结数</td>
	</tr>
	<tr class="rowCount_53">
		<td colspan="2">程序性办结数</td>
	</tr>
	
	<tr class="rowCount_54">
		<td rowspan="4">全县本月办理中</td>
		<td colspan="3">合计</td>
	</tr>
	<tr class="rowCount_55">
		<td colspan="3">村级建账本月办理中总数</td>
	</tr>
	<tr class="rowCount_56">
		<td colspan="3">乡镇本月办理中总数</td>
	</tr>
	<tr class="rowCount_57">
		<td colspan="3">县级部门本月办理中总数</td>
	</tr>
</table>
</div>