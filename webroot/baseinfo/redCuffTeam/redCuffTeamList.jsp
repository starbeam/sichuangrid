<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pop" uri="/PopGrid-taglib" %>
<jsp:include page="/includes/baseInclude.jsp" />

<div class="content">
	<div class="ui-corner-all" id="nav">
		<div class="btnbanner btnbannerData">
			<jsp:include page="/common/orgSelectedComponent.jsp"/>
		</div>
<%-- 		<select id="screeningFourteams" class="basic-input" onchange="screeningFourteamsFunction()"> --%>
<%-- 			 <pop:OptionTag name="@com.tianque.domain.property.PropertyTypes@RED_CUFF_TEAM_TYPE"/> --%>
<%-- 		</select> --%>
		<pop:JugePermissionTag ename="searchRedCuffMemeber">
		<a id="search" href="javascript:void(0)"><span><strong class="ui-ico-cx"></strong>高级搜索</span></a>
		</pop:JugePermissionTag>
		
		<pop:JugePermissionTag ename="addRedCuffMemeber">
		<a id="addRedCuffMemeber" href="javascript:void(0)"><span><strong class="ui-ico-xz"></strong>新增</span></a>
		</pop:JugePermissionTag>
		
		<pop:JugePermissionTag ename="deleteRedCuffMemeber">
		<a id="deleteRedCuffMemeber" href="javascript:void(0)"><span><strong class="ui-ico-xz"></strong>批量删除</span></a>
		</pop:JugePermissionTag>
		
		<pop:JugePermissionTag ename="importRedCuffMemeber">
		<a id="importRedCuffMemeber" href="javascript:void(0)"><span><strong class="ui-ico-xz"></strong>导入</span></a>
		</pop:JugePermissionTag>
		
		<a id="reload" href="javascript:void(0)"><span><strong class="ui-ico-refresh"></strong>刷新</span></a>
		
		<pop:JugePermissionTag ename="cancellationOfCertification">
		<a id="cancellationOfCertification" href="javascript:void(0)"><span><strong class="ui-ico-xz"></strong>注销认证</span></a>
		</pop:JugePermissionTag>
	</div>
	<div style="clear: both;"></div>
	<div style="width: 100%;">
		<table id="redCuffMemeberList">
		</table>
		<div id="redCuffMemeberListPager"></div>
	</div>
	<div id="redCuffMemeberDialog"></div>
	<div id="addRedCuffMemeberDialog"></div>
</div>
<script type="text/javascript">
<pop:formatterProperty name="gender" domainName="@com.tianque.domain.property.PropertyTypes@GENDER" />
<pop:formatterProperty name="political" domainName="@com.tianque.domain.property.PropertyTypes@POLITICAL_BACKGROUND" />
<pop:formatterProperty name="nation" domainName="@com.tianque.domain.property.PropertyTypes@NATION" />
<pop:formatterProperty name="teamType" domainName="@com.tianque.domain.property.PropertyTypes@RED_CUFF_TEAM_TYPE" />
<pop:formatterProperty name="subTeamType" domainName="@com.tianque.domain.property.PropertyTypes@RED_CUFF_TEAM_SUB_TYPE" />
<pop:formatterProperty name="education" domainName="@com.tianque.domain.property.PropertyTypes@SCHOOLING" />
var dialogWidth=850;
var dialogHeight=600;
var currentOrgId=getCurrentOrgId();

function screeningFourteamsFunction(){
	 var	initParam = {
				"redCuffTeam.organization.id":currentOrgId,
				"redCuffTeam.teamType.id":$("#screeningFourteams").val()
			}

			$("#redCuffMemeberList").setGridParam({
				url:'${path}/baseinfo/redCuffTeamManage/findRedCuffTeamForPageResult.action',
				datatype:'json',
				page:1
			});
			$("#redCuffMemeberList").setPostData(initParam);
			$("#redCuffMemeberList").trigger("reloadGrid");
}
function selectRow(){
	var count = $("#redCuffMemeberList").jqGrid("getGridParam","records");
	var selectedCounts = getActualjqGridMultiSelectCount("redCuffMemeberList");
	if(selectedCounts == count){
		jqGridMultiSelectState("redCuffMemeberList", true);
	}else{
		jqGridMultiSelectState("redCuffMemeberList", false);
	}
}   

function onOrgChanged(orgId){
    var	initParam = {
		"redCuffTeam.organization.id":currentOrgId
	}

	$("#redCuffMemeberList").setGridParam({
		url:'${path}/baseinfo/redCuffTeamManage/findRedCuffTeamForPageResult.action',
		datatype:'json',
		page:1
	});
	$("#redCuffMemeberList").setPostData(initParam);
	$("#redCuffMemeberList").trigger("reloadGrid");
}

function operatorFormatter(el,options,rowData){
	return "<pop:JugePermissionTag ename='updateRedCuffMemeber'><a href='javascript:;' onclick='updateRedCuffTeam("+rowData.id+");'><span>修改</span></a> | </pop:JugePermissionTag><pop:JugePermissionTag ename='deleteRedCuffMemeber'><a href='javascript:;' onclick='deleteRedCuffTeam("+rowData.id+");'><span>删除</span></a></pop:JugePermissionTag>";
}

function viewFormatter(el,options,rowData){
	return "<a href='javascript:viewRedCuffTeam(\""+rowData.id+"\")'><span>"+rowData.memeberName+"</span></a>";
}
	
	
$(function(){
	$("#redCuffMemeberList").jqGridFunction({
		datatype: "local",
		mytype:"post",
	    colModel:[
				{name:"id", index:"id",hidden:true,sortable:false, frozen :true},
				{name:"operator", index:'id',label:'操作',formatter:operatorFormatter,width:80,align:'center'},
				{name:"organization.orgName", index:'organization.orgName',label:'所属组织机构',width:100,align:'center'},
				{name:"memeberName", index:'memeberName',label:'姓名',formatter:viewFormatter,width:80,align:'center'},
				{name:"teamType", index:'teamType',label:'队伍类型',formatter:teamTypeFormatter,width:100,align:'center'},
				{name:"subTeamType", index:'subTeamType',label:'队伍类别',formatter:subTeamTypeFormatter,width:100,align:'center'},
				{name:"idCardNo", index:'idCardNo',label:'身份证号码',width:150,align:'center'},
				{name:"gender", index:'gender',label:'性别',formatter:genderFormatter,width:80,align:'center'},
				{name:"birthDate", index:'birthDate',label:'出生年月',width:100,align:'center'},
				{name:"phoneNumber", index:'phoneNumber',label:'联系电话',width:100,align:'center'},
				{name:"nation", index:'nation',label:'民族',formatter:nationFormatter,width:100,align:'center'},
				{name:"political", index:'political',label:'政治面貌',formatter:politicalFormatter,width:100,align:'center'},
				{name:"education", index:'education',label:'文化程度',formatter:educationFormatter,width:100,align:'center'},
				{name:"occupation", index:'occupation',label:'职业',width:100,align:'center'},
				{name:"fimalyAddress", index:'fimalyAddress',label:'家庭住址',width:150,align:'center'},
				{name:"registeredPerson", index:'registeredPerson',label:'登记人',width:100,align:'center'},
				{name:"registeredDate", index:'registeredDate',label:'登记时间',width:100,align:'center'}
				
		],
		multiselect:true,
	  	onSelectAll:function(aRowids,status){},
	  	onSelectRow:function(){selectRow();},
		ondblClickRow:function (rowid){
			viewRedCuffTeam(rowid);
		},
	});
	jQuery("#redCuffMemeberList").jqGrid('setFrozenColumns');
	onOrgChanged(getCurrentOrgId());
	
	
	//导入
	$("#importRedCuffMemeber").click(function(event){
			$("#addRedCuffMemeberDialog").createDialog({
				width: 400,
				height: 230,
				title:"数据导入",
				url:"${path}/common/import.jsp?isNew=1&dataType=redCuffTeam&dialog=addRedCuffMemeberDialog&startRow=6&templates=REDCUFFTEAM&listName=redCuffMemeberList&module="+currentOrgId,
				buttons:{
					"导入" : function(event){
						$("#mForm").submit();
					},
				   	"关闭" : function(){
				   		$(this).dialog("close");
				   		$("#redCuffMemeberList").trigger("reloadGrid");
				   	}
				},
				shouldEmptyHtml:false
			});
		});
	
	//高级查询
	$("#search").click(function(){
		$("#addRedCuffMemeberDialog").createDialog({
			width: 800,
			height: 300,
			title:"高级搜索",
			url:"${path}/baseinfo/redCuffTeamManage/dispatchOperate.action?mode=search&redCuffTeam.organization.id="+currentOrgId,
			buttons:{
				"查询" : function(event){
					$("#searchForm").submit();
				},
			   	"关闭" : function(){
			   		$(this).dialog("close");
			   	}
			}
		});
	});
	
	$("#addRedCuffMemeber").click(function(){
		$("#redCuffMemeberDialog").createDialog({
			width: 800,
			height: 500,
			title:'新增红袖套成员',
			url:'${path}/baseinfo/redCuffTeamManage/dispatchOperate.action?mode=add&redCuffTeam.organization.id='+currentOrgId,
			buttons: {
		   		"保存" : function(event){
		   			$("#maintainForm").submit();
		   		},
		   		"关闭" : function(){
		        	$(this).dialog("close");
		   		}
			}
		});		
	});
	
	$("#reload").click(function(){
		$("#screeningFourteams").find("option:first").attr("selected",true); 
		onOrgChanged(currentOrgId);
	});
	
	$("#deleteRedCuffMemeber").click(function(){
		var rowIds = $("#redCuffMemeberList").jqGrid("getGridParam", "selarrrow");
		if(rowIds.length ==0){
			 $.messageBox({level:"warn",message:"请选择一条或多条记录，再进行删除！"});
			 return;
		}
		deleteRedCuffTeam(rowIds);
	});
	
	$("#cancellationOfCertification").click(function(){
		var rowIds = $("#redCuffMemeberList").jqGrid("getGridParam", "selarrrow");
		if(rowIds.length ==0){
			 $.messageBox({level:"warn",message:"请选择一条或多条记录，再进行注销认证！"});
			 return;
		}
		cancellationOfCertification(rowIds);
	});
	
});


function viewRedCuffTeam(id){
	$("#redCuffMemeberDialog").createDialog({
		width: 800,
		height: 500,
		title:'查看红袖套成员',
		url:'${path}/baseinfo/redCuffTeamManage/dispatchOperate.action?mode=view&redCuffTeam.id='+id,
		buttons: {
	   		"关闭" : function(){
	        	$(this).dialog("close");
	   		}
		}
	});	
}

function updateRedCuffTeam(selectIds){
	if(selectIds==null || selectIds==''){
		 $.messageBox({level:"warn",message:"请选择一条记录进行修改"});
		 return;
	}
	$("#redCuffMemeberDialog").createDialog({
		width: 800,
		height: 500,
		title:'修改红袖套成员',
		url:'${path}/baseinfo/redCuffTeamManage/dispatchOperate.action?mode=edit&redCuffTeam.id='+selectIds,
		buttons: {
	   		"保存" : function(event){
	   			$("#maintainForm").submit();
	   		},
	   		"关闭" : function(){
	        	$(this).dialog("close");
	   		}
		}
	});	
}
function deleteRedCuffTeam(selectIds){
	if(selectIds==null || selectIds==''){
		 $.messageBox({level:"warn",message:"请至少选择一条记录进行删除"});
		 return;
	}
	$.confirm({
		title:"确认删除",
		message:"确定要删除吗?一经删除，数据无法恢复",
		okFunc: function(){
			$.ajax({
				url:'${path}/baseinfo/redCuffTeamManage/deleteRedCuffTeamByIds.action',
				type:"post",
				data:{
					"ids":selectIds+""
				},
				success:function(data){
					$("#redCuffMemeberList").trigger("reloadGrid");
				    $.messageBox({message:"已经成功删除该信息!"});
			    }
		    });
	   }
 	});
}
function cancellationOfCertification(selectIds){
	if(selectIds==null || selectIds==''){
		 $.messageBox({level:"warn",message:"请至少选择一条记录进行注销认证"});
		 return;
	}
	$.confirm({
		title:"确认注销认证",
		message:"确定要注销认证吗?一经注销，数据无法恢复",
		okFunc: function(){
			$.ajax({
				url:'${path}/baseinfo/redCuffTeamManage/cancellationOfCertification.action',
				type:"post",
				data:{
					"ids":selectIds+""
				},
				success:function(data){
					$("#redCuffMemeberList").trigger("reloadGrid");
				    $.messageBox({message:"已经成功注销该信息!"});
			    }
		    });
	   }
 	});
}
</script>