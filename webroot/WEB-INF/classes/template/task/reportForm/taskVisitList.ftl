<#assign pop=JspTaglibs["/WEB-INF/taglib/pop-taglib.tld"]>
<#assign s=JspTaglibs["/WEB-INF/taglib/struts-tags.tld"]>
<@s.include value="/includes/baseInclude.jsp" />
<style>
#tableWrap{
	overflow-x:auto;
	border-top: 1px solid #ccc;
}
#tableWrap .head{
	border-top: 0 none;
}

</style>
<div id="nav" class="newChartsStyle cf" style="margin-left:5px;">
     <div class="btnbanner btnbannerData">
			<@s.include value="/common/orgSelectedComponent.jsp" />
     </div>
	    时间： <select id="searchType">
	    		<option value="0" selected>按月统计</option>
	    		<option value="1">按周统计</option>
	    		<option value="2">按年统计</option>
	    	</select>
	    	<span id="searchByMonth">
				<select id="year"></select> 
				年 
				<select id="month"></select>
			</span>
			<span id="searchByWeek">
				<select id="week">
					<option value="0">本周</option>
					<option value="1">上周</option>
				</select>
			</span>
			<span id="searchByYear">
				<select id="yearVal"></select> 
				年 
			</span>
	        <a id="search" href="javascript:void(0)"><span>查询</span></a>
	        <a id="viewTaskListRole" href="javascript:void(0)"><span>查询条件规则</span></a>
     <#-- 导出按钮功能 -->
            <a id="export" href="javascript:void(0)"><span>导出</span></a>
</div>
<div>
	<div id="gridbox" class="SigmaReport dd"></div>
	<#--<div class="footerInfo">
		<p>统计规则：</p>
		<p>1.统计分为按月、按周、按年份统计。</p>
		<p>2.所统计数据总数为区间数据(如：选择2015年11月，统计数据则是2015年10月1日到2015年10月31日数据)。</p>
	</div>-->
</div>
<div id="PrintDlg"></div>
<div id="viewTaskListRoleDlg"></div>
<script type="text/javascript">
function getmonth(){
	$.ajax({
		async: false,
		url: "${path }/stat/currentTime/getCurrentTimeForMonth.action?currenYear="+$("#year").val(),
		success:function(responseData){
			for(var i = 0;i<responseData.length;i++){
				$("#month").append("<option value='"+responseData[i]+"'>"+responseData[i]+"   月</option>");
			}
		}
	});
}
function getYear(){
	$.ajax({
		async: false,
		url: "${path }/stat/currentTime/getCurrentTimeForYear.action",
		success:function(responseData){
			for(var i = 0;i<responseData.length;i++){
				$("#year").append("<option value='"+responseData[i]+"'>"+responseData[i]+"</option>"); 
				$("#yearVal").append("<option value='"+responseData[i]+"'>"+responseData[i]+"</option>"); 
			}
			getmonth();
		}
	});
}
function typeChange(){
	var val = $("#searchType").val();
	if(val==0){
		  $("#searchByMonth").show();
		  $("#searchByWeek").hide();
		  $("#searchByYear").hide();
	}else if(val ==1 ){
		  $("#searchByMonth").hide();
		  $("#searchByWeek").show();
		  $("#searchByYear").hide();
	}else if(val==2){
		  $("#searchByMonth").hide();
		  $("#searchByWeek").hide();
		  $("#searchByYear").show();
	}
}
	var fitColumns=true;
		var columns = [
			{name:"orgname",caption:"区域",mode:"string"}
			<@pop.JugePermissionTag ename="propagandaAndVerificationReportForm">
				,
				{name:"general",caption:"流动人口",children:[
				{name:"general",caption:"宣传核查",children:[
					{name:"policeSum",caption:"网格员发送",width:80,mode:"string"},
					{name:"policeVisit",caption:"已签收",width:60,mode:"string"}
				]}
			</@pop.JugePermissionTag>
			<@pop.JugePermissionTag ename="workingSituationReportForm">
				,
				{name:"general",caption:"民警带领开展工作",children:[
				    {name:"publicSum",caption:"网格员发送",width:80,mode:"string"},
					{name:"publicVisit",caption:"已签收",width:60,mode:"string"}
					
				]}
			</@pop.JugePermissionTag>
			<@pop.JugePermissionTag ename="exceptionSituationReportForm">
				,
				{name:"general",caption:"异常情况报告",children:[
				    {name:"exceptionSum",caption:"网格员发送",width:80,mode:"string"},
					{name:"exceptionVisit",caption:"已签收",width:60,mode:"string"},
					{name:"exceptionReply",caption:"已回复",width:60,mode:"string"}
				]}
			]}
			</@pop.JugePermissionTag>
			<@pop.JugePermissionTag ename="druggyTaskReportForm">
				,
				{name:"general",caption:"吸毒人员",children:[
				{name:"general",caption:"",children:[
					{name:"druggySum",caption:"网格员发送",width:80,mode:"string"},
					{name:"druggyVisit",caption:"已签收",width:60,mode:"string"},
					{name:"druggyException",caption:"异常",width:40,mode:"string"},
					{name:"druggyReply",caption:"已回复",width:60,mode:"string"}
				]}]}
			</@pop.JugePermissionTag>
				,
				{name:"general",caption:"严重精神障碍患者",children:[
				{name:"general",caption:"",children:[
				{name:"mentalPatientSum",caption:"网格员发送",width:80,mode:"string"},
				<@pop.JugePermissionTag ename="mentalPatientJusticeTaskReportForm">
					{name:"mentalPatientJusticeVisit",caption:"卫生所签收",width:80,mode:"string"},
				</@pop.JugePermissionTag>
				<@pop.JugePermissionTag ename="mentalPatientPoliceTaskReportForm">
					{name:"mentalPatientPoliceVisit",caption:"派出所签收",width:80,mode:"string"},
				</@pop.JugePermissionTag>
					{name:"mentalPatientException",caption:"异常",width:40,mode:"string"}
				<@pop.JugePermissionTag ename="mentalPatientJusticeTaskReportForm">
					,{name:"mentalPatientJusticeReply",caption:"异常回复",width:100,mode:"string"}
				</@pop.JugePermissionTag>
				<#--<@pop.JugePermissionTag ename="mentalPatientPoliceTaskReportForm">
					,{name:"mentalPatientPoliceReply",caption:"派出所异常回复",width:100,mode:"string"}
				</@pop.JugePermissionTag>-->
				]}]}
			<@pop.JugePermissionTag ename="termerRecordReportForm">
				,
				{name:"general",caption:"社区服刑人员",children:[
				{name:"general",caption:"",children:[
					{name:"rectificativeSum",caption:"网格员发送",width:80,mode:"string"},
					{name:"rectificativeVisit",caption:"已签收",width:60,mode:"string"},
					{name:"rectificativeException",caption:"异常",width:60,mode:"string"},
					{name:"rectificativeReply",caption:"已回复",width:60,mode:"string"}
				]}]}
			</@pop.JugePermissionTag>
			<@pop.JugePermissionTag ename="positiveInfoRecordReportForm">
				,
				{name:"general",caption:"刑释人员",children:[
				{name:"general",id:"rective",caption:"",children:[
					{name:"positiveSum",caption:"网格员发送",width:80,mode:"string"},
					{name:"positiveVisit",caption:"已签收",width:60,mode:"string"},
					{name:"positiveException",caption:"异常",width:60,mode:"string"},
					{name:"positiveReply",caption:"已回复",width:60,mode:"string"}
				]}]}
			</@pop.JugePermissionTag>
			<@pop.JugePermissionTag ename="hiddenDangerReportForm">
				,
				{name:"general",caption:"发现治安隐患",children:[
				{name:"general",caption:"",children:[
					{name:"hiddenDangerSum",caption:"网格员发送",width:80,mode:"string"},
					{name:"hiddenDangerVisit",caption:"已签收",width:60,mode:"string"},
					{name:"hiddenDangerReply",caption:"回复",width:60,mode:"string"}
				]}]}
			</@pop.JugePermissionTag>
		];


var grid = null;

function changeTable(){
   $("table td[caption='吸毒人员']").attr("rowspan","2");
   $("table td[caption='异常情况报告']").next().remove();
   $("table td[caption='严重精神障碍患者']").attr("rowspan","2");
   $("table td[caption='异常情况报告']").next().remove();
   $("table td[caption='社区服刑人员']").attr("rowspan","2");
   $("table td[caption='异常情况报告']").next().remove();
   $("table td[caption='刑释人员']").attr("rowspan","2");
   $("table td[caption='异常情况报告']").next().remove();
   $("table td[caption='发现治安隐患']").attr("rowspan","2");
   $("table td[caption='异常情况报告']").next().remove();
   
   $("table td[caption='吸毒人员']").attr("style","text-align:center;padding:15px;");
   $("table td[caption='严重精神障碍患者']").attr("style","text-align:center;padding:15px;");
   $("table td[caption='社区服刑人员']").attr("style","text-align:center;padding:15px;");
   $("table td[caption='刑释人员']").attr("style","text-align:center;padding:15px;");
   $("table td[caption='发现治安隐患']").attr("style","text-align:center;padding:15px;");
   $("table td[caption='区域']").attr("style","text-align:center;padding:20px;");
}

function onOrgChanged(orgId){
    var orgId=getCurrentOrgId();
	$.ajax({
		dataType:"json",
		url:'${path }/plugin/taskListManage/common/getVisitList.action?orgId='+orgId+'&searchType='+$("#searchType").val()+'&year='+$("#year").val()+'&month='+$("#month").val()+'&week='+$("#week").val()+'&searchYear='+$("#yearVal").val(),
		success:function(data){
			grid.bindData(data);
		}
	})
}

$(document).ready(function(){
$("#year").change(function(){
		$("#month").empty();
		getmonth();
	});
	$("#viewTaskListRole").click(function(){
		$("#viewTaskListRoleDlg").createDialog({
			width: 800,
			height: 400,
			title:"任务清单查询条件规则",
			url:"${path}/task/reportForm/taskListRole.ftl",
			buttons:{
				"关闭" : function(){
					$(this).dialog("close");
				}
			}
			
		});
	});

	var context = {};
	grid = new SigmaReport("gridbox",context,columns,null,null,"任务清单",null,null);
	<#--$("#gridbox").css({"overflow": "auto", "height": document.documentElement.offsetHeight - ($.browser.msie ? 240 : 280)});-->
	//setTimeout('onOrgChanged()',350);
	getYear();
	typeChange();
	onOrgChanged();
	changeTable();
	$(".print").click(function(){
		
		var url = '${path}/task/reportForm/taskListPrint.ftl?parentOrgId='+getCurrentOrgId()+"&moduleName="+document.title;
		$("#PrintDlg").createDialog({
			width: 1200,
			height:490,
			title:document.title,
			url:url,
			buttons: {
			   "打印" : function(){
				print();
		  	   },
			   "关闭" : function(){
			        $("#PrintDlg").dialog("close");
			   }
			}
		})
	});
	
	function print(){
		$("#Print").printArea();
		$("#PrintDlg").dialog("close");
	}
	
	$("#export").click(function(){
		var url = '${path}/plugin/taskListManage/common/downloadTask.action?orgId='+getCurrentOrgId()+'&searchType='+$("#searchType").val()+'&year='+$("#year").val()+'&month='+$("#month").val()+'&week='+$("#week").val()+'&searchYear='+$("#yearVal").val();
		downloadFile(url);
	});
	function downloadFile(url){  
	    var elemIF = document.createElement("iframe");  
	    elemIF.src = url;  
	    elemIF.style.display = "none";  
	    document.body.appendChild(elemIF);  
	}
	
	$("#search").click(function(){
		onOrgChanged();
	});
	
	$("#searchType").change(function(){
		 typeChange();
	});	
	
})
	

</script>
