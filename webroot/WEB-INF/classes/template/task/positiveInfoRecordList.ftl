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
	<div class="ui-corner-all" id="nav">
		<div class="btnbanner btnbannerData">
			<#-- <@s.include value="/common/orgSelectedComponent.jsp"/>-->
		    <@s.include value="/common/orgSelectedTaskListComponent.jsp"/>
			<div class="ui-widget autosearch">
			    <input class="basic-input searchtxt" type="text" value="请输入姓名或地点" name="positiveInfoRecordVo.fastSearchCondition" id="searchText" maxlength="18" onblur="value=(this.value=='')?'请输入姓名或地点':this.value;" onfocus="value=(this.value=='请输入姓名或地点')?'':this.value;"/>
				<button id="refreshSearchKey" class="ui-icon ui-icon-refresh searchbtnico"></button>
			</div>
			<a href="javascript:;" id="fastSearchButton"><span>搜索</span></a>
			<@pop.JugePermissionTag ename="searchPositiveInfoRecord">
				<a id="search" href="javascript:void(0)"><span><strong
						class="ui-ico-cx"></strong>高级搜索</span></a>
				<span class="lineBetween"></span>
			</@pop.JugePermissionTag>
			<span class="lineBetween"></span>
		</div>
		<@pop.JugePermissionTag ename="addPositiveInfoRecord">
			<a id="add" href="javascript:void(0)"><span><strong
					class="ui-ico-xz"></strong>新增</span></a>
		</@pop.JugePermissionTag>
		<@pop.JugePermissionTag ename="deletePositiveInfoRecord">
			<a id="delete" href="javascript:void(0)"><span><strong
					class="ui-ico-sc"></strong>批量删除</span></a>
		</@pop.JugePermissionTag>
		<a id="reload" href="javascript:void(0)"><span><strong
				class="ui-ico-refresh"></strong>刷新</span></a>
		<!--
		<@pop.JugePermissionTag ename="signPositiveInfoRecord">
			<a id="transfer" href="javascript:void(0)"><span>签收</span></a>
		</@pop.JugePermissionTag>
		-->
	</div>
	<input type="hidden" name="" id="flag" value="${(flag)!}" />
	<div style="width: 100%;" class="">
		<table id="positiveInfoRecordList"></table>
		<div id="positiveInfoRecordListPager"></div>
	</div>
	<div id="positiveInfoRecordDialog"></div>
	<div id="addTaskListReplyDlg"></div>
</div>
<script type="text/javascript">
    var taskListTimeStandard;
    var serverTime = new Date().getTime();
    function getTaskListTimeStandardByItemName(){
        $.get(PATH + "/taskListTimeStandardManage/getTaskListTimeStandardByItemName.action", {
            'orgId': selectConfigTaskOrg(),
            'itemNameDictInternalId':<@pop.static value="@com.tianque.plugin.taskList.constant.TaskListItemNameInternalId@POSITIVE_INFORECORD_MANAGE"/>
        }, function (data) {
            taskListTimeStandard = data[0];
            serverTime = taskListTimeStandard?taskListTimeStandard.sysCurrTime:new Date().getTime();
            // 有配置的才显示亮牌
            if(taskListTimeStandard){
                $("#positiveInfoRecordList").jqGrid("showCol", ["signDate","replayDate"]);
            }
        });
    }
    $(document).ready(function () {
        getTaskListTimeStandardByItemName();
	<@pop.formatterProperty name="livelihoodWay" domainName="@com.tianque.domain.property.PropertyTypes@DRUGGY_LIFE_RESOURCE" />
	$("#positiveInfoRecordList").jqGridFunction({
		datatype: "local",
		multiselect:true,
		colModel:[
			{name:"id",index:"id",sortable:false,hidden:true},
			/**
		    {name:"operation",index:"id",label:"操作",sortable:false,width:80,align:"center",formatter:operaterFormatter},
		    **/
		<@pop.JugePermissionTag ename="signPositiveInfoRecord">
            {name:'signDate',label:'签收督办',sortable:true,formatter:signSuperviseFormatter,align:'center',hidden:true,hidedlg:true,width:130},
		</@pop.JugePermissionTag>
		<@pop.JugePermissionTag ename="replyPositiveInfoRecord">
            {name:'replayDate',label:'回复督办',sortable:true,formatter:replaySuperviseFormatter,align:'center',hidden:true,hidedlg:true,width:130},
		</@pop.JugePermissionTag>
		    {name:'name',index:"name",label:'姓名',sortable:false,width:100,align:"center"},
		    {name:"idCard",index:"idCard",label:'身份证号码', width:130,sortable:false,hidden:false,frozen:false},
            {name:"phone",index:"phone",label:'电话号码', width:110,sortable:false,hidden:false,frozen:false},
		    {name:'recordDate',index:"recordDate",label:'时间',sortable:true,width:150},
		    {name:'address',index:'address',label:'地点',sortable:false,width:200},
		    {name:"helpPeople",index:"helpPeople",label:'帮扶人员',sortable:false,hidden:false,frozen:false},
		    {name:'livelihoodWay',label:'生活来源方式',sortable:false,width:100,formatter:livelihoodWayFormatter},
		     {name:'status',label:'是否签收',align:"center",sortable:false, width:150,formatter:statusFormatter},
		     {name:'hasReplay',label:'是否回复',sortable:false,align:"center", width:150,formatter:addTaskListReplyFormatter},
		     {name:'hasException',label:'有无异常',sortable:false,align:"center", width:150,hidden:true},
		    {name:'exceptionSituationInfo',index:'exceptionSituationInfo',label:'异常情况',sortable:false,width:240},
		    {name:'mark',index:'mark',label:'备注',sortable:false,width:200}

		],
		ondblClickRow: viewPositiveInfoRecord
	});
	function signSuperviseFormatter(el,options,rowData){
		if(rowData.status==1){
			return "-";
		}else{
			if(!taskListTimeStandard){
				return '<span title="未配置">err</span>';
			}
			var createDateStr =rowData.createDate;
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
	$("#positiveInfoRecordList").jqGrid('setFrozenColumns');
	//新增按钮事件
	$("#add").click(function(event){
		if(!isConfigTaskGrid()){
			$.messageBox({level:"warn",message:"请先选择网格级别组织机构进行新增！"});
			return;
		}
		$("#positiveInfoRecordDialog").createDialog({
			title:"新增刑释人员记录",
			width: 600,
			height: 475,
			url:"${path}/plugin/taskListManage/positiveInfoRecord/dispatch.action?mode=add&addFlag=false",
			buttons: {
				"保存" : function(event){
		   			$("#positiveInfoRecordForm").submit();
				},
				"关闭" : function(event){
		   			$(this).dialog("close");
				}
			}
		});
	});
	//刷新按钮事件绑定
	$("#reload").click(function(event){
		$("#searchText").val("请输入姓名或地点");
		getPositiveInfoRecordList();
	});

	$("#refreshSearchKey").click(function(){
		$("#searchText").val("请输入姓名或地点");
	});

	$("#fastSearchButton").click(function(event){
		var fastSearchCondition = $("#searchText").val();
		if(fastSearchCondition == "请输入姓名或地点"){

		}else {
			var postData={
				"positiveInfoRecordVo.organization.id":selectConfigTaskOrg(),
				"positiveInfoRecordVo.fastSearchCondition":fastSearchCondition
			};
			if(isConfigTaskSelect()){
				$.extend(postData,{"positiveInfoRecordVo.mode":"gridConfigTask","positiveInfoRecordVo.funOrgId": $("#funOrgId").val()})
			}
			$("#positiveInfoRecordList").setPostData(postData);
			$("#positiveInfoRecordList").trigger("reloadGrid");
		}
	});

		getPositiveInfoRecordList();

	//高级搜索对话框
	$("#search").click(function(event){
		$("#positiveInfoRecordDialog").createDialog({
			title:"社区刑释人员记录查询-请输入查询条件",
			width: 700,
			height: 300,
			url:"${path}/plugin/taskListManage/positiveInfoRecord/dispatch.action?mode=search",
			buttons: {
				"查询" : function(event){
					searchPositiveInfoRecords();
					$(this).dialog("close");
				},
				"关闭" : function(){
		        	$(this).dialog("close");
				}
			}

		});
	});

	$("#delete").click(function(){
		var ids = $("#positiveInfoRecordList").jqGrid("getGridParam", "selarrrow");
		if(ids.length < 1){
			$.messageBox({level:'warn',message:"没有选中数据，无法对刑释人员记录进行删除操作！"});
		}else {
			deletePositiveInfoRecordOperator(ids);
		}
	});
});

	//列表显示（包括快速过滤）
	function getPositiveInfoRecordList(){
		$("#positiveInfoRecordList").setGridParam({
			url:"${path}/plugin/taskListManage/positiveInfoRecord/findPositiveInfoRecords.action?onlyHasException=true",
			datatype: "json",
			page:1,
			mytype:"post"
		});
		var postData={
			"positiveInfoRecordVo.organization.id":selectConfigTaskOrg()
		};
		if(isConfigTaskSelect()){
			$.extend(postData,{"positiveInfoRecordVo.mode":"gridConfigTask","positiveInfoRecordVo.funOrgId": $("#funOrgId").val()})
		}
		$("#positiveInfoRecordList").setPostData(postData);
		$("#positiveInfoRecordList").trigger("reloadGrid");
	}

	//删除服务记录
	function deletePositiveInfoRecordOperator(selectedIds){
		var flag1 = false;
		var flag2= false;
		for(var i=0;i<selectedIds.length;i++){
			var positiveInfoRecord =  $("#positiveInfoRecordList").getRowData(selectedIds[i]);
			if(positiveInfoRecord.internalId>USER_ORG_LEVEL){
				flag1 = true;
			}
			if(positiveInfoRecord.status == '是'){
				flag2 = true;
			}
		}
		if(flag1){
			$.messageBox({level:'warn',message:"选中的刑释人员记录层级高于当前登录层级，无法对该刑释人员记录进行删除操作！"});
			return;
		}
	    if(flag2){
			$.messageBox({level:'warn',message:"选中的刑释人员记录已签收，无法对该刑释人员记录进行删除操作！"});
			return;
		}
		$.confirm({
			title:"确认删除",
			message:"确定要删除吗?",
			okFunc: function(){
				$.ajax({
					url:"${path}/plugin/taskListManage/positiveInfoRecord/deletePositiveInfoRecords.action?ids="+selectedIds,
					success:function(data){
						if(data>0){
						    $.messageBox({message:"成功删刑释人员记录!"});
							$("#positiveInfoRecordList").trigger("reloadGrid");
						}else{
							$.messageBox({
								message:"删除刑释人员记录出错!",
								level:"warn"
							});
						}
					}
				});
			}
		});
	}

	//高级搜索
	function searchPositiveInfoRecords()
	{
		$("#positiveInfoRecordList").setGridParam({
			url:"${path}/plugin/taskListManage/positiveInfoRecord/findPositiveInfoRecords.action?onlyHasException=true",
			datatype: "json",
			page:1,
			mtype:"post"
		});
		var data=$("#searchPositiveInfoRecordForm").serializeArray();
		var dataJson={};
		for(i=0;i<data.length;i++){
	 		if (dataJson[data[i].name]) {
				dataJson[data[i].name]=dataJson[data[i].name]+","+data[i].value;
			} else {
				dataJson[data[i].name] = data[i].value;
			}
		}
		var postData={
			"positiveInfoRecordVo.organization.id":selectConfigTaskOrg()
		};
		if(isConfigTaskSelect()){
			$.extend(postData,{"positiveInfoRecordVo.mode":"gridConfigTask","positiveInfoRecordVo.funOrgId": $("#funOrgId").val()})
		}
		$("#positiveInfoRecordList").setPostData(
			$.extend(dataJson,postData));
		$("#positiveInfoRecordList").trigger("reloadGrid");
	}

	function viewPositiveInfoRecord(selectedId) {
		$("#positiveInfoRecordDialog").createDialog({
			width: 600,
			height: 500,
			title: '查看刑释人员记录信息',
			url:"${path}/plugin/taskListManage/positiveInfoRecord/viewPositiveInfoRecord.action?mode=view&id="+selectedId,
			buttons: {
				"关闭" : function(){
					$(this).dialog("close");
				}
			}
		});
	}

	function statusFormatter(el, options, rowData){
		var flag = "<@pop.JugePermissionTag ename='signPositiveInfoRecord'>true</@pop.JugePermissionTag>";
		if(rowData.status == 0 && flag == 'true'){
			return "<@pop.JugePermissionTag ename='signPositiveInfoRecord'><a href='javascript:' onclick='signRecord("+rowData.id+")'><span style='color:#ff0000;'>签收</span></a></@pop.JugePermissionTag>";
		}else if(rowData.status == 0 && flag != 'true'){
			return "否";
		}
		if(rowData.status == 1){
			return "是";
		}
	}

 function refreshList(searchText){
     getTaskListTimeStandardByItemName();
	var orgId=selectConfigTaskOrg();
	$("#positiveInfoRecordList").setGridParam({
	url:"${path}/plugin/taskListManage/positiveInfoRecord/findPositiveInfoRecords.action?onlyHasException=true",
		datatype: "json",
		page:1
	});
	var postData={
		"positiveInfoRecordVo.organization.id":selectConfigTaskOrg()
	};
	if(isConfigTaskSelect()){
		$.extend(postData,{"positiveInfoRecordVo.mode":"gridConfigTask","positiveInfoRecordVo.funOrgId": $("#funOrgId").val()})
	}
	$("#positiveInfoRecordList").setPostData(postData);
	$("#positiveInfoRecordList").trigger("reloadGrid");
  }

	function operaterFormatter(el, options, rowData){
		if(rowData.status == 0){
			return "<@pop.JugePermissionTag ename='deletePositiveInfoRecord'><a href='javascript:' onclick='deletePositiveInfoRecordOperator("+rowData.id+")'><span>删除</span></a></@pop.JugePermissionTag>";
		}else {
			return "无";
		}
	}

	function signRecord(id){
		$("#positiveInfoRecordDialog").createDialog({
			width: 600,
			height: 610,
			title: '刑释人员记录签收',
			url:"${path}/plugin/taskListManage/positiveInfoRecord/dispatch.action?mode=sign&id="+id,
			buttons: {
				"签收" : function(){
					$("#positiveInfoRecordForm").submit();
				},
				"关闭" : function(){
					$(this).dialog("close");
				}
			}
		});
	}

	function addTaskListReplyFormatter(el, options, rowData){
		var flag = '<@pop.JugePermissionTag ename="replyPositiveInfoRecord">true</@pop.JugePermissionTag>';
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
	function addTaskListReply(id){
		$("#addTaskListReplyDlg").createDialog({
			width: 600,
			height: 400,
			title: '回复',
			url:"${path}/plugin/taskListManage/common/addTaskListReplyDlg.action?taskListReply.moduleKey=reply_positiveInfoRecord&taskListReply.taskId="+id,
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
