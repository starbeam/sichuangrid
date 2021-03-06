<#assign pop=JspTaglibs["/WEB-INF/taglib/pop-taglib.tld"]>
<#assign s=JspTaglibs["/WEB-INF/taglib/struts-tags.tld"]>
<@s.include value="/includes/baseInclude.jsp"/>

<div class="content">
	<div style="clear: both;"></div>
	<div style="width: 100%;">
		<table id="tqSearchDetailList"> </table>
		<div id="tqSearchDetailListPager"></div>
	</div>
	<div id="detailViewDialog"></div>
</div>
<script type="text/javascript">
$(document).ready(function(){
	$("#tqSearchDetailList").jqGridFunction({
		datatype: "local",
		multiselect:true,
		height:350,
		colModel:[
			{name:"id",sortable:false,hidden:true},
			{name:"keyId",sortable:false,hidden:true},
			{name:"operation",label:"操作",sortable:false,formatter:operateFormatter,width:90,frozen :true,align:"center"},
		    {name:'serialNumber',label:'诉求编码',width:200,align:"center"},
		    {name:'conditionDescription',index:"occurDate",label:'情况描述',width:430},
		    {name:'dataType',hidden:true,hidedlg:true}
		],
		loadComplete:function(){},
		ondblClickRow: viewTqSearchDetail
	});
	$("#tqSearchDetailList").jqGrid('setFrozenColumns');
	findTqSearchDetailList();
	
	function operateFormatter(el,options,rowData){
		return "<a href='javascript:;' onclick='viewTqSearchDetail(\""+rowData.id+"\")'><span>查看</span></a>";
	}
	
	function findTqSearchDetailList(){
		$("#tqSearchDetailList").setGridParam({
			url:'${path}/baseinfo/tqSearch/tqSearchDetailList.action',
			datatype: "json",
			page:1,
			mytype:"post"
		});
		$("#tqSearchDetailList").setPostData({
			"searchs.id":"${(searchs.id)!}",
			"searchs.type":"${(searchs.type)!}",
			"searchs.searchType":"${(searchs.searchType)!}",
			"searchs.searchField":"${(searchs.searchField)!}"
		});
		$("#tqSearchDetailList").trigger("reloadGrid"); 
	}
});
function viewTqSearchDetail(selectedId) {
	var rowData = $("#tqSearchDetailList").getRowData(selectedId);
	$("#detailViewDialog").createDialog({
		width: 830,
		height: 610,
		title: '查看详情',
		url:'${path}/account/evaluateFeedbacksManage/dispatchOperate.action?accountType='+rowData.dataType+'&mode=view&evaluateMode=view&accountId='+rowData.keyId+'&id='+rowData.keyId,
		buttons: {
			"关闭" : function(){
				$(this).dialog("close");
			}
		}
	});
}
</script>