<#assign pop=JspTaglibs["/WEB-INF/taglib/pop-taglib.tld"]>
<#assign s=JspTaglibs["/WEB-INF/taglib/struts-tags.tld"]>
<@s.include value="/includes/baseInclude.jsp" />
<style>
    .greenLimitWarn {
        width: 22px;
        height: 22px;
        display: block;
        margin: 0 auto;
        vertical-align: top;
        background: url(/resource/system/images/issue/icon_gLamp.png) no-repeat;
    }
    .yellowLimitWarn {
        width: 22px;
        height: 22px;
        display: block;
        margin: 0 auto;
        vertical-align: top;
        background: url(/resource/system/images/issue/icon_yLamp.png) no-repeat;
    }
    .redLimitWarn {
        width: 22px;
        height: 22px;
        display: block;
        margin: 0 auto;
        vertical-align: top;
        background: url(/resource/system/images/issue/icon_rLamp.png) no-repeat;
    }
</style>
<div class="content">
	<div class="ui-corner-all contentNav" id="nav">
	<div class="btnbanner btnbannerData">
		<div class="btnbanner">
		 <#--	 <@s.include value="/common/orgSelectedComponent.jsp"/>-->
		    <@s.include value="/common/orgSelectedTaskListComponent.jsp"/>
	    </div>
		<div class="ui-widget autosearch">
				<input class="basic-input" type="text" value="请输入地点或姓名" id="searchByCondition" maxlength="18" style="width: 150px;" onblur="value=(this.value=='')?'请输入地点或姓名':this.value;" onfocus="value=(this.value=='请输入地点或姓名')?'':this.value;">
				<button id="refreshSearchKey" class="ui-icon ui-icon-refresh searchbtnico"></button>
			</div>
		</div>
		<a href="javascript:;" id="fastSearchButton"><span>搜索</span></a>
		<@pop.JugePermissionTag ename='searchDruggyTask'>
		<a id="search" href="javascript:void(0)"><span><strong class="ui-ico-cx"></strong>高级搜索</span></a>
		</@pop.JugePermissionTag>		
		<span class="lineBetween "></span>
		<@pop.JugePermissionTag ename='addDruggyTask'>
			<a id="addDruggyTask"  href="javascript:void(0)"><span>新增</span></a>
		</@pop.JugePermissionTag>
		<@pop.JugePermissionTag ename='refreshDruggyTask'>
		<a id="refresh"  href="javascript:void(0)"><span>刷新</span></a>
		</@pop.JugePermissionTag>
		<@pop.JugePermissionTag ename='deleteDruggyTask'>
		<a id="delete" href="javascript:void(0)"><span><strong
					class="ui-ico-sc"></strong>批量删除</span></a>
		</@pop.JugePermissionTag>
	</div>
		<div style="clear: both;"></div>
		<div style="width: 100%">
			<table id="druggyTaskList"></table>
			<div id="druggyTaskListPager"></div>
		</div>
		<div id="druggyTaskDialog"></div>
		<div id="importFamilyInfoDialog"></div>
		<div id="searchFamilyInfoDialog"></div>
		<div id="addTaskListReplyDlg"></div>
</div>
<script type="text/javascript">

function nameFormatter(el,options,rowData){
	var logOut = rowData.logOut;
	if(logOut=''||logOut == 0 || logOut =='0'){
		return "<@pop.JugePermissionTag ename='viewDruggyTask'><a href='javascript:;' onclick='viewDruggyTask("+rowData.id+");'><span>"+rowData.name+"</span></a></@pop.JugePermissionTag>";
	}else{
		return "<@pop.JugePermissionTag ename='viewDruggyTask'><a href='javascript:;' onclick='viewDruggyTask("+rowData.id+");' style='color:#888888;' ><span>"+rowData.name+"</span></a></@pop.JugePermissionTag>";
	}
}

var orgId=selectConfigTaskOrg();
var title="吸毒人员信息";
var taskListTimeStandard;
var serverTime = new Date().getTime();
function getTaskListTimeStandardByItemName(){
    $.get(PATH + "/taskListTimeStandardManage/getTaskListTimeStandardByItemName.action", {
        'orgId':selectConfigTaskOrg(),
        'itemNameDictInternalId':<@pop.static value="@com.tianque.plugin.taskList.constant.TaskListItemNameInternalId@DRUGGY_TASKVISIT_MANAGEMENT"/>
    }, function (data) {
        taskListTimeStandard = data[0];
        serverTime = taskListTimeStandard?taskListTimeStandard.sysCurrTime:new Date().getTime();
        // 有配置的才显示亮牌
        if(taskListTimeStandard){
            $("#druggyTaskList").jqGrid("showCol", ["signDate","replayDate"]);
        }
    });
}
$(document).ready(function () {
    getTaskListTimeStandardByItemName();
    var postData = {
        "druggyTask.organization.id": orgId
    };
    if (isConfigTaskSelect()) {
        $.extend(postData, {"druggyTask.mode": "gridConfigTask", "druggyTask.funOrgId": $("#funOrgId").val()})
    }
	$("#druggyTaskList").jqGridFunction({
		url:'${path}/baseInfo/druggyTaskManage/searchDruggyTask.action?onlyHasException=true',
		datatype: "json",
		postData:postData,
	   	colModel:[
	   	          {name:"id",index:"id",sortable:false,hidden:true,frozen:true},
			<@pop.JugePermissionTag ename="signDruggyTask">
				{name:'signDate',label:'签收督办',sortable:true,formatter:signSuperviseFormatter,align:'center',hidden:true,hidedlg:true,width:130},
			</@pop.JugePermissionTag>
			<@pop.JugePermissionTag ename="replyDruggyTask">
				{name:'replayDate',label:'回复督办',sortable:true,formatter:replaySuperviseFormatter,align:'center',hidden:true,hidedlg:true,width:130},
			</@pop.JugePermissionTag>
                  {name:"name",index:"name",label:'姓名', width:100,sortable:false,hidden:false,frozen:true},
                  {name:"idCard",index:"idCard",label:'身份证号码', width:130,sortable:false,hidden:false,frozen:false},
                  {name:"phone",index:"phone",label:'电话号码', width:110,sortable:false,hidden:false,frozen:false},
                  {name:"time",index:"time",label:'时间',sortable:false,hidden:false,frozen:false},
                  {name:"place",index:"place",label:'地点',sortable:false,hidden:false,frozen:false},
                  {name:"helpPeople",index:"helpPeople",label:'帮扶人员',sortable:false,hidden:false,frozen:false},
                  {name:"exception",index:"exception",label:'异常情况',sortable:false,hidden:false,frozen:false},
                  {name:"status",index:"status",label:'签收状态',formatter:statusFormatter,sortable:false,hidden:false,frozen:false,align:"center"},
                  {name:'hasReplay',label:'是否回复',sortable:false,align:"center", width:150,formatter:addTaskListReplyFormatter},
                  {name:'hasException',label:'有无异常',sortable:false,align:"center", width:150,hidden:true},
                  {name:"organization.orgName",index:"org",label:'所属网格',sortable:false,hidden:false,frozen:false},
                  {name:"familyTel",index:"familyTel",label:'家属联系电话',sortable:false,hidden:false,frozen:false},
                  {name:"reporterTel",index:"reporterTel",label:'网格员联系电话',sortable:false,hidden:false,frozen:false},
                  {name:"remark",index:"remark",label:'备注',sortable:false,hidden:false,frozen:false},
                  {name:"signDate",index:"signDate",label:'签收时间',sortable:false,hidden:false,frozen:false},
                  {name:"attitude",index:"attitude",label:'签收意见',sortable:false,hidden:false,frozen:false},
			{name:'createDate',label:'创建时间',sortable:true,align:'center',hidden:true,hidedlg:true,width:120}
		   ],
		multiselect:true,
		onSelectAll:function(aRowids,status){},
		showColModelButton:true,
		ondblClickRow:viewDruggyTask,
		onSelectRow:function(){}
	});
    function signSuperviseFormatter(el,options,rowData){
        if(rowData.status==1){
            return "-";
        }else{
            if(!taskListTimeStandard){
                return '<span title="未配置">err</span>';
            }
            var createDateStr =rowData.createDate;
            if(!createDateStr){
                return 'date err';
            }
            var useTime =(serverTime - Date.parse(createDateStr.replace(/-/g, "/")))/(1000*60*60);
            if(useTime>taskListTimeStandard.signRedLimit){
                return '<strong class="redLimitWarn" title="红牌超时"/>';
            }else if(useTime>taskListTimeStandard.signYellowLimit){
                return '<strong class="yellowLimitWarn" title="黄牌超时"/>';
            }else{
                var title = "剩"+Math.ceil(taskListTimeStandard.signYellowLimit-useTime)+'小时时限';
                return '<strong class="greenLimitWarn" title="'+title+'"/>';
            }
        }
    }
    function replaySuperviseFormatter(el,options,rowData) {
        if (rowData.hasReplay == 1 || rowData.hasException != 1) {
            return "-";
        }
        if (rowData.status != 1) {
            return '<span title="未签收">...</span>';
        }
        if (!taskListTimeStandard) {
            return '<span title="未配置">err</span>';
        }
        var signDateStr = rowData.signDate;
        var useTime = (serverTime - Date.parse(signDateStr.replace(/-/g, "/"))) / (1000 * 60 * 60);
        if (useTime > taskListTimeStandard.replayRedLimit) {
            return '<strong class="redLimitWarn" title="红牌超时"/>';
        } else if (useTime > taskListTimeStandard.replayYellowLimit) {
            return '<strong class="yellowLimitWarn" title="黄牌超时"/>';
        } else {
            var title = "剩" + Math.ceil(taskListTimeStandard.replayYellowLimit - useTime) + '小时时限';
            return '<strong class="greenLimitWarn" title="' + title + '"/>';
        }

    }
	$("#refresh").click(function(event){
	$("#searchByCondition").attr("value","请输入地点或姓名");
		onOrgChanged();
	});
	
	$("#fastSearchButton").click(function(e){
	var orgId=selectConfigTaskOrg();
	fastSearch(orgId);
	});
	
	$("#refreshSearchKey").click(function(){
		$("#searchByCondition").attr("value","请输入地点或姓名");
	});
	
	$("#search").click(function(){
		$("#searchFamilyInfoDialog").createDialog({
				width: 500,
				height: 350,
				title: '高级查询',
				url:'${path}baseInfo/druggyTaskManage/dispatch.action?mode=search',
				buttons: {
					"查询" : function(){
						searchDruggyTask();
						$(this).dialog("close");
					},
					"关闭" : function(){
						$(this).dialog("close");
					}
				}
			});
	});
	
	$("#delete").click(function(){
	    var selectedId = $("#druggyTaskList").jqGrid("getGridParam", "selarrrow");
	    if(selectedId.length==0){
	       $.messageBox({level:"warn",message:"请至少选择一条记录进行删除！"});
			return;
	    }
	    $.confirm({
		title:"确认删除该"+title,
		message:"该"+title+"删除后，将不可恢复，您确定要删除该"+title+"吗？",
		width: 400,
		okFunc: function(){
		$.ajax({
		url : "${path}/baseInfo/druggyTaskManage/deleteDruggyTask.action?ids="+selectedId,
		success : function(data) {
			onOrgChanged(selectConfigTaskOrg(),isConfigTaskGrid());
		  }
	     });
		}
	    });
	    var evt = event || window.event;
	    if (window.event) {
	    evt.cancelBubble=true;
	    } else {
	    evt.stopPropagation();
	    }
	})
	
	$("#addDruggyTask").click(function(){
		if(!isConfigTaskGrid()){
			$.messageBox({level:"warn",message:"请先选择网格级别组织机构进行新增！"});
		 return;
		}
		$("#druggyTaskDialog").createDialog({
				width: 600,
				height: 520,
				title: '新增吸毒人员记录',
				url:'${path}/baseInfo/druggyTaskManage/dispatch.action?addFlag=false&mode=add&orgId='+selectConfigTaskOrg(),
				buttons: {
					"保存" : function(){
						$("#maintainForm").submit();
					},
					"关闭" : function(){
						$(this).dialog("close");
					}
				}
			});
	});
	
	
})


function statusFormatter(el, options, rowData){
		var flag = "<@pop.JugePermissionTag ename='signDruggyTask'>true</@pop.JugePermissionTag>";
		if(rowData.status == 0 && flag ){
			return "<@pop.JugePermissionTag ename='signDruggyTask'><a href='javascript:' onclick='updateOperator("+rowData.id+")'><span style='color:#ff0000;'>签收</span></a></@pop.JugePermissionTag>";
		}else if(rowData.status == 0 && flag != 'true'){
			return "否";
		}
		if(rowData.status == 1){
			return "是";
		}
	}
function addTaskListReplyFormatter(el, options, rowData){
	var flag = '<@pop.JugePermissionTag ename="replyDruggyTask">true</@pop.JugePermissionTag>';
	if(rowData.hasException==1 && rowData.status == 1 && flag == 'true'){
		if(rowData.hasReplay==1){
			return "<a href='javascript:void(0);' onclick='addTaskListReply("+rowData.id+")'><span style='color:#999999;'>已回复</span></a>";
		}
		return "<a href='javascript:void(0);' onclick='addTaskListReply("+rowData.id+")'><span style='color:#ff0000;'>回复</span></a>";
	}else if(rowData.hasReplay == 0 && flag != 'true'){
		return "否";
	}
	if(rowData.hasReplay == 1){
		return "是";
	}
	return "";
}


function updateOperator(selectedId){
	if(selectedId==null){
		return;
	}
	var builddata=$("#druggyTaskList").getRowData(selectedId);
	$("#druggyTaskDialog").createDialog({
		width: 600,
		height:470,
		title: '签收',
		url:'${path}/baseInfo/druggyTaskManage/dispatch.action?mode=sign&orgId='+ selectConfigTaskOrg()+'&druggyTaskId='+selectedId,
		buttons: {
			"签收" : function(){
				$("#maintainForm").submit();
			},
			"关闭" : function(){
				$(this).dialog("close");
			}
		}
	});
	var evt = event || window.event;
	if(typeof evt!="object"){return false;}
	if (window.event) { 
	evt.cancelBubble=true; 
	} else { 
	evt.stopPropagation(); 
	} 
}

function viewDruggyTask(rowid){
	if(rowid==null){
 		return;
	}
	$("#druggyTaskDialog").createDialog({
		width: 600,
		height: 400,
		title:'查看吸毒走访记录',
		modal : true,
		url:'${path}/baseInfo/druggyTaskManage/viewDruggyTask.action?id='+rowid,
		buttons: {
		   "关闭" : function(){
		        $(this).dialog("close");
		   }
		}
	});
}

//高级搜索
	function searchDruggyTask()
	{   
	 var initParam = {
		"druggyTask.organization.id":selectConfigTaskOrg(),
		"druggyTask.name": $("#conditionName").val(),
		"druggyTask.place": $("#conditionPlace").val(),
		"druggyTask.conditionStartDate":$("#conditionStartDate").val(),
		"druggyTask.conditionEndDate":$("#conditionEndDate").val(),
		"druggyTask.conditionSignStartDate":$("#conditionSignStartDate").val(),
		"druggyTask.conditionSignEndDate":$("#conditionSignEndDate").val(),
		"druggyTask.status":$("#status").val(),
		"druggyTask.hasReplay":$("#hasReplay").val(),
		"druggyTask.hasException":$("#hasException").val(),
		"druggyTask.helpPeople":$("#helpPeople").val(),
		"druggyTask.idCard":$("#idCard").val(),
		"druggyTask.phone":$("#phone").val()
	}
	if(isConfigTaskSelect()){
		$.extend(initParam,{"druggyTask.mode":"gridConfigTask","druggyTask.funOrgId": $("#funOrgId").val()})
	}
	$("#druggyTaskList").setGridParam({
		url:"${path}/baseInfo/druggyTaskManage/searchDruggyTask.action?onlyHasException=true",
		datatype: "json",
		page:1
	});
	    $("#druggyTaskList").setPostData(initParam);
		$("#druggyTaskList").trigger("reloadGrid");
	}
	
	
 function refreshList(searchText){
     getTaskListTimeStandardByItemName();
	var orgId=selectConfigTaskOrg();
	$("#druggyTaskList").setGridParam({
		url:'${path}/baseInfo/druggyTaskManage/searchDruggyTask.action?onlyHasException=true',
		datatype: "json",
		page:1
	});
   	var postData={
		"druggyTask.organization.id":orgId
        };
	if(isConfigTaskSelect()){
		$.extend(postData,{"druggyTask.mode":"gridConfigTask","druggyTask.funOrgId": $("#funOrgId").val()})
	}
	$("#druggyTaskList").setPostData(postData);
	$("#druggyTaskList").trigger("reloadGrid");
} 

function fastSearch(orgId){
	var condition = $("#searchByCondition").val();

	if(condition == '请输入地点或姓名') return;
	var initParam = {
	"druggyTask.organization.id":orgId,
	"druggyTask.fastSearchKeyWords":condition
	}
	if(isConfigTaskSelect()){
		$.extend(initParam,{"druggyTask.mode":"gridConfigTask","druggyTask.funOrgId": $("#funOrgId").val()})
	}
	$("#druggyTaskList").setGridParam({
		url:'${path}/baseInfo/druggyTaskManage/searchDruggyTask.action?onlyHasException=true',
		datatype: "json",
		page:1
	});
	$("#druggyTaskList").setPostData(initParam);
	$("#druggyTaskList").trigger("reloadGrid");
}
function onOrgChanged(){
    getTaskListTimeStandardByItemName();
    var orgId = selectConfigTaskOrg();
		$("#druggyTaskList").setGridParam({
			url:'${path}/baseInfo/druggyTaskManage/searchDruggyTask.action?onlyHasException=true',
			datatype: "json",
			page:1
		});
  var postData={
			"druggyTask.organization.id":orgId
        };
	if(isConfigTaskSelect()){
		$.extend(postData,{"druggyTask.mode":"gridConfigTask","druggyTask.funOrgId": $("#funOrgId").val()})
	}
	    $("#druggyTaskList").setPostData(postData);
		$("#druggyTaskList").trigger("reloadGrid");
}
function addTaskListReply(id){
	$("#addTaskListReplyDlg").createDialog({
		width: 600,
		height: 400,
		title: '回复',
		url:"${path}/plugin/taskListManage/common/addTaskListReplyDlg.action?taskListReply.moduleKey=reply_druggyTask&taskListReply.taskId="+id,
		buttons: {
			"回复" : function(){
				$("#maintainForm").submit();
			},
			"关闭" : function(){
				$(this).dialog("close");
			}
		}
	});
}

</script>