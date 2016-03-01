<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="/PopGrid-taglib" prefix="pop"%>
<%@ include file="/includes/baseInclude.jsp"%>
<input type="hidden" id="keyType"
value="<s:property value="#parameters.keyType"/>" />
<%request.setAttribute("keyType", "securityKey");%>
<jsp:include page="/baseinfo/commonPopulation/jsFunctionInclude.jsp">
	<jsp:param value="SafetyProductionKey" name="moduleName"/>
</jsp:include>
<script type="text/javascript">
	<pop:formatterProperty name="type" domainName="@com.tianque.domain.property.PropertyTypes@SECURITY_TYPE" />
	<pop:formatterProperty name="attentionExtent" domainName="@com.tianque.domain.property.PropertyTypes@ATTENTION_EXTENT" />
	function isRiskEnterprise(el, options, rowData){
		if(true == rowData.riskEnterprise){
			return "是";
		}else{
			return "否";
		}
	}
	var gridOption ={
		colModel:[
			{name:"id", index:"id", hidden:true },
			{name:"encryptId", index:"id", hidden:true,frozen:true },
	    	{name:"operator", index:'id',label:'操作',formatter:operateFormatter,width:80,frozen:true,sortable:false,align:'center' },
	    	{name:"attentionExtent",index:"attentionExtent",label:"关注程度",sortable:true,width:100,formatter:attentionExtentColor,frozen:true},
			{name:"name-Font", index:"name", label: '治安重点名称',frozen:true,sortable:true,formatter:nameFont,width:200},
			{name:"name", index:"name", label: '治安重点名称',hidden:true,sortable:true,frozen:true,width:150},
			{name:"address",sortable:true,label:"治安重点地址",width:200},
			{name:"legalPerson", index:"legalPerson", sortable:true,label:'负责人',width:100},
			{name:"type", index:"type", label:'治安重点类型', formatter:typeFormatter,width:100},
			{name:'organization.orgName',index:'orgInternalCode',label:'所属网格',width:150, hidden:true},
			{name:"riskEnterprise", sortable:false, label:'是否存在隐患', formatter:isRiskEnterprise,width:100,align:'center'},
			{name:"hiddenCases", sortable:true, label:'隐患情况',width:200},
			{name:"hiddenRectification", sortable:true, label:'隐患整改情况',width:200,hidden:true},
			{name:"renovateType", sortable:false, label:'挂牌整治',formatter:renovateTypeFormatter,width:150,hidden:true},
			{name:'hasServiceTeamMember', sortable:true, label:'有无治安负责人', width:140,align:'center',formatter:hasServiceTeamMemberFormatter},
			{name:'lastRecordTime', sortable:true, label:'最新巡场时间',align:'center', width:140},
			
			{name:'sourcesState',index:'sourcesState',label:"数据来源",sortable:false,hidden:true,formatter:sourceStateFormatter,width:80,align:'center'},
			{name:'updateDate', sortable:false, label:'数据更新时间', width:140},
			{name:"telephone",sortable:true,label:"负责人联系电话",hidden:true,width:120,align:'center'},
			{name:"mobileNumber",sortable:true,label:"负责人手机号码",hidden:true,width:100,align:'center'},
			{name:"isEmphasis",sortable:false,label:"是否关注",hidden:true,hidedlg:true,width:100}
		]
	};	
	function renovateTypeFormatter(el, options, rowData){
		var renovateType = rowData.renovateType;
		if(renovateType=='<s:property value="@com.tianque.domain.property.RenovateType@RENOVATE_TYPE_PROVINCE"/>'){
			return '<s:property value="@com.tianque.domain.property.RenovateType@PROVINCE_NAME"/>';
		}else if(renovateType=='<s:property value="@com.tianque.domain.property.RenovateType@RENOVATE_TYPE_CITY"/>'){
			return '<s:property value="@com.tianque.domain.property.RenovateType@CITY_NAME"/>';
		}else if(renovateType=='<s:property value="@com.tianque.domain.property.RenovateType@RENOVATE_TYPE_COUNTY"/>'){
			return '<s:property value="@com.tianque.domain.property.RenovateType@COUNTY_NAME"/>';
		}else{
			return "";
		}
	}
	var dialogWidth=810;
	var dialogHeight=600;
	
	function getLocationName(rowData){
		return rowData.name;
	}
</script>

<jsp:include page="/baseinfo/commonPopulation/attentionLocationList.jsp">
	<jsp:param value="SafetyProductionKey" name="moduleName"/>
	<jsp:param value="治安重点" name="moduleCName"/>
	<jsp:param value="治安负责人" name="supervisorPerson"/>
	<jsp:param value="${keyType}" name="keyType"/>
</jsp:include>
<div style="display:none;">
<input type="hidden" id="_locationType_" name="population.populationType" value='<s:property value="@com.tianque.core.util.BaseInfoTables@SECURITYKEY_KEY"/>'/>
		<input type="hidden" id="_supervisorName_" 									  
	value='<s:property value="@com.tianque.service.util.ServiceTeamMemberOrRecord@getMemberAlias(@com.tianque.core.util.BaseInfoTables@ENTERPRISEKEY_KEY)" escape="false"/>'/>
	<input type="hidden" id="_superviceRecordName_" 
	value='<s:property value="@com.tianque.service.util.ServiceTeamMemberOrRecord@getRecordAlias(@com.tianque.core.util.BaseInfoTables@ENTERPRISEKEY_KEY)" escape="false"/>'/>
</div>