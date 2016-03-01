<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pop" uri="/PopGrid-taglib"%>
<%@ include file="/includes/baseInclude.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%> 
<%
if(ThreadVariable.getUser()!=null){
	request.setAttribute("NAME",ThreadVariable.getUser().getName());
}
%>
<s:action name="getLoginInfo" var="loginAction" executeResult="false"
	namespace="/sessionManage" />
<s:action name="getFullOrgById" var="getFullOrgById"
	executeResult="false" namespace="/sysadmin/orgManage">
	<s:param name="organization.id"
		value="#loginAction.user.organization.id"></s:param>
</s:action>
<s:if test="#request.type=='convert'">
		<s:action name="dispatch" namespace="/threeRecords/ledgerConvert" executeResult="true">
			<s:param name="id" value="#request.keyId"></s:param>
			<s:param name="mode" >view</s:param>
		</s:action>
	</s:if>
<div id="dialog-form" title="困难群众台账维护" class="container container_24" style="overflow:hidden;">
	<form id="maintainForm" method="post" 
	    action="<s:if test='"edit".equals(mode)'>/threeRecordsIssue/ledgerPoorPeopleManage/updateLedgerPoorPeople.action</s:if><s:elseif test='"add".equals(mode)'>/threeRecordsIssue/ledgerPoorPeopleManage/addLedgerPoorPeople.action</s:elseif>" >
	<pop:token/>
	<input type="hidden" id="organizationId" name="ledgerPoorPeople.organization.id" value="${ledgerPoorPeople.organization.id}"/>
	<input type="hidden" id="occurOrgId" name="ledgerPoorPeople.occurOrg.id" value="${ledgerPoorPeople.occurOrg.id}" />
	<input type="hidden" name="ledgerPoorPeople.id" id="id" value="${ledgerPoorPeople.id}" />
	<input type="hidden" name="ledgerPoorPeople.bookingUnit" id="bookingUnit" value="${ledgerPoorPeople.bookingUnit}" />
    <input type="hidden" name="ledgerPoorPeople.poorType" id="poorType_"  value="${ledgerPoorPeople.poorType}" /> 
    <input type="hidden" name="ledgerPoorPeople.poorSource" id="poorSource_" value="${ledgerPoorPeople.poorSource}" />
    <input type="hidden" name="ledgerPoorPeople.requiredType" id="requiredType_" value="${ledgerPoorPeople.requiredType}" />
	<s:if test="#request.type=='convert'">
			<input type="hidden" id="oldIssueId" name="ledgerPoorPeople.oldIssueId" value="${param.oldIssueId}"/>
			<input type="hidden" id="convertId" name="ledgerPoorPeople.convertId" value="${param.keyId}"/>
		</s:if>
	<div class="grid_4 lable-right">
   		<em class="form-req">*</em>
	    <label class="form-lbl">身份证号：</label>
	</div>
	<div class="grid_7">
   		<input type="text" name="ledgerPoorPeople.idCardNo" id="idCardNo" value="${ledgerPoorPeople.idCardNo }" maxlength="18" class="form-txt {required:true,idCard:true,messages:{required:'身份证号必须输入',idCard:$.format('请输入一个合法的身份证号码'),exsistedIdCard:function(){return idCardNoData;}}}" />
	</div>
	<div class="grid_4 lable-right">
   		<em class="form-req">*</em>
   		<label class="form-lbl">姓名：</label></div>
    <div class="grid_7">
   		<input type="text" id="name" name="ledgerPoorPeople.name" value="${ledgerPoorPeople.name }" maxlength="20" class="form-txt {required:true,exculdeParticalChar:true,maxlength:20,minlength:2,messages:{required:'姓名必须输入',exculdeParticalChar:'不能输入非法字符',minlength:$.format('姓名至少需要输入{0}个字符'),maxlength:$.format('姓名最多需要输入{0}个字符')}}" />
    </div>
    <div class='clearLine'>&nbsp;</div>
    
	<div class="grid_4 lable-right">
		<em class="form-req">*</em>
	    <label class="form-lbl">性别：</label>
	</div>
	<div class="grid_7">
   		<select id="gender" name="ledgerPoorPeople.gender.id" class="form-txt">
	   		<pop:OptionTag name="@com.tianque.plugin.account.property.PropertyTypes@GENDER" defaultValue="${ledgerPoorPeople.gender.id}"/></select>
	   	<input type="hidden" id="populationGender" value="${ledgerPoorPeople.gender.id}"/>
	</div>
	<div class="grid_4 lable-right">
			<em class="form-req">*</em>
			<label class="form-lbl">建卡类型：</label>
	 	</div>
	<div class="grid_7">
			<select name="ledgerPoorPeople.createTableType.id" id="createTableType" class='form-txt' <s:if test='"view".equals(mode)'>disabled</s:if>>
		   		<pop:OptionTag name="@com.tianque.plugin.account.property.PropertyTypes@LEDGER_PEOPLEASPIRATION_CREATE_TABLE_TYPE"  defaultValue="${ledgerPoorPeople.createTableType.id}" />
			</select>
	</div>
	<div class='clearLine'>&nbsp;</div>
		
	<div  class="grid_4 lable-right" >
		<em class="form-req">*</em>
		<label class="form-lbl">编号： </label>
	</div>
	<div class="grid_7" id="userDiv">
   		<input type="text" name="ledgerPoorPeople.serialNumber" value="${ledgerPoorPeople.serialNumber}" readonly="readonly" maxlength="15" class="form-txt {required:true,messages:{required:'编号必须输入'}}" />
	</div>
	<div class="grid_4 lable-right">
		<em class="form-req">*</em>
	    <label class="form-lbl">职业或身份：</label>
	</div>
	<div class="grid_7">
   		<select id="position" name="ledgerPoorPeople.position.id" class="form-txt">
	   		<pop:OptionTag name="@com.tianque.plugin.account.property.PropertyTypes@POSITION_OR_STATUS" defaultValue="${ledgerPoorPeople.position.id}"/></select>
	</div>
	<div class="grid_4 lable-right">
	     <label class="form-lbl">常住地址：</label>
	</div>
    <div class="grid_7">
   		<input type="text" id='permanentAddress' name="ledgerPoorPeople.permanentAddress" value="${ledgerPoorPeople.permanentAddress }" maxlength="50" class="form-txt {validatorNativePlaceAddress:true, messages:{validatorNativePlaceAddress:'常住地址不能输入特殊字符'}}" />
    </div>
    <div class="grid_4 lable-right">
    	<em class="form-req">*</em>
		<label class="form-lbl">发生网格：</label>
	</div>
	<div class="grid_7">
		<input type="text" id="occurOrg" class="form-txt {isNOTNULL:true,messages:{isNOTNULL:'请选择发生网格'}}" style="color:red"/>
    </div>
    <div class='clearLine'>&nbsp;</div>
    
	<div class="grid_4 lable-right">
	    <label class="form-lbl">联系电话：</label>
	</div>
	<div class="grid_7">
   		<input type="text" name="ledgerPoorPeople.mobileNumber" id="serverTelephone" maxlength="15"  value="${ledgerPoorPeople.mobileNumber }"
   			class='form-txt {telephone:true,messages:{telephone:$.format("联系电话不合法，只能输数字和横杠(-)")}}' title="请输入由数字和-组成的联系电话,例如：0577-88888888"/>
	</div>
	<div class="grid_4 lable-right">
	 	<label class="form-lbl">是否户主：</label>
	</div>
    <div class="grid_7">
   		<select name="ledgerPoorPeople.owner" class="form-txt">
   			<option value="false" <c:if test="false == ledgerPoorPeople.owner">selected="selected"</c:if>>否</option>
   			<option value="true" <c:if test="true == ledgerPoorPeople.owner">selected="selected"</c:if>>是</option>
   		</select>
    </div>
    <div class='clearLine'>&nbsp;</div>
    
    <div class="grid_4 lable-right">
			<label class="form-lbl">户口号：</label>
	</div>
	<div class="grid_7">
			<input type="text" name="ledgerPoorPeople.accountNo" id="accountNo" maxlength="16" value="${ledgerPoorPeople.accountNo}" 
			class="form-txt dialogtip {maxlength:20,messages:{maxlength:$.format('户口号最多需要输入{0}个字符')}}" />
	</div>
	<div class="grid_4 lable-right">
	    <label class="form-lbl">家庭人口：</label>
	</div>
	<div class="grid_7">
   		<input type="text" name="ledgerPoorPeople.memberNo" value="${ledgerPoorPeople.memberNo }" maxlength="10" class='form-txt {positiveInteger:true,messages:{positiveInteger:$.format("家庭人口输入不合法，只能输入正整数")}}' title="请输入由数字" />
	</div>
	<div class='clearLine'>&nbsp;</div>
	
	<div class="grid_4 lable-right">
		<label class="form-lbl">保障类型：</label>
	</div>
	<div class="grid_7">
	   <select name="ledgerPoorPeople.securityType.id" class="form-txt" id="securityType">
	   		<pop:OptionTag name="@com.tianque.plugin.account.property.PropertyTypes@LEDGER_POOR_PEOPLE_SECURITY_TYPE"  defaultValue="${ledgerPoorPeople.securityType.id}" />
	   </select>
	</div>
	<div class="grid_4 lable-right">
	    <label class="form-lbl">人均年收入：</label>
	</div>
	<div class="grid_7">
		<input type="text" name="ledgerPoorPeople.annualPerCapitaIncome" value="${ledgerPoorPeople.annualPerCapitaIncome}" maxlength="10" class="form-txt" />
	</div>
	<div class="grid_2">万元</div>
	<div class='clearLine'>&nbsp;</div>
	
	 <div class="grid_4 lable-right">
			<label class="form-lbl">登记人：</label>
	 </div>
	<div class="grid_7">
			<input type="text"  name="ledgerPoorPeople.registrant" id="registrant"  maxlength="20" value="${ledgerPoorPeople.registrant}" 
				class='form-txt' />
	</div>	
	<div class="grid_4 lable-right">
	    <em class="form-req">*</em><label class="form-lbl">登记时间：</label>
	</div>
	<div class="grid_7" id="birthdayDiv">
   		<input type="text" name="ledgerPoorPeople.registrationTime" id="registrationTime" value="<s:date name="ledgerPoorPeople.registrationTime" format="yyyy-MM-dd"/>" class="form-txt" readonly/>
	</div>
	<div class='clearLine'>&nbsp;</div>
	
	<div class="grid_4 lable-right">
	    <label class="form-lbl">出生年月：</label>
	</div>
	<div class="grid_7" id="birthdayDiv">
   		<input type="text" name="ledgerPoorPeople.birthDay" id="birthDayTime" value="<s:date name="ledgerPoorPeople.birthDay" format="yyyy-MM-dd"/>" class="form-txt" readonly/>
	</div>
	<div class="grid_4 lable-right">
	    <label class="form-lbl">困难程度：</label>
	</div>
	<div class="grid_7">
		<input type="text" name="ledgerPoorPeople.difficultyDegree" value="${ledgerPoorPeople.difficultyDegree }" maxlength="10" class="form-txt {exculdeParticalChar:true,maxlength:10,minlength:0,messages:{exculdeParticalChar:'不能输入非法字符',minlength:$.format('困难程度至少需要输入{0}个字符'),maxlength:$.format('困难程度最多需要输入{0}个字符')}}" />
	</div>
	<div class='clearLine'>&nbsp;</div>
	
	<div class="grid_4 lable-right">
	    <label class="form-lbl">关注程度：</label>
	</div>
	<div class="grid_7">
		<input type="text" name="ledgerPoorPeople.attentionDegree" value="${ledgerPoorPeople.attentionDegree}" maxlength="10" class="form-txt {exculdeParticalChar:true,maxlength:10,minlength:0,messages:{exculdeParticalChar:'不能输入非法字符',minlength:$.format('关注程度至少需要输入{0}个字符'),maxlength:$.format('关注程度最多需要输入{0}个字符')}}" />
	</div>
	<div class="grid_4 lable-right">
	    <label class="form-lbl">户籍地址：</label>
	</div>
	<div class="grid_7">
		<input type="text" id='censusRegisterAddress' name="ledgerPoorPeople.censusRegisterAddress" value="${ledgerPoorPeople.censusRegisterAddress}" maxlength="20" class="form-txt {exculdeParticalChar:true,maxlength:20,minlength:0,messages:{exculdeParticalChar:'不能输入非法字符',minlength:$.format('户籍地址至少需要输入{0}个字符'),maxlength:$.format('户籍地址最多需要输入{0}个字符')}}" />
	</div>
	<div class='clearLine'>&nbsp;</div>
	
	<div class="grid_4 lable-right">
	    <label class="form-lbl">户籍性质：</label>
	</div>
	<div class="grid_7">
		<input type="text" name="ledgerPoorPeople.censusRegisterNature" value="${ledgerPoorPeople.censusRegisterNature}" maxlength="20" class="form-txt {exculdeParticalChar:true,maxlength:20,minlength:0,messages:{exculdeParticalChar:'不能输入非法字符',minlength:$.format('户籍性质至少需要输入{0}个字符'),maxlength:$.format('户籍性质最多需要输入{0}个字符')}}" />
	</div>
	<div class="grid_4 lable-right">
	 	<label class="form-lbl">是否党员：</label>
	</div>
    <div class="grid_7">
   		<select name="ledgerPoorPeople.isPartyMember" class="form-txt">
   			<option value="false" <s:if test="false == ledgerPoorPeople.isPartyMember">selected="selected"</s:if>>否</option>
   			<option value="true" <s:if test="true == ledgerPoorPeople.isPartyMember">selected="selected"</s:if>>是</option>
   		</select>
    </div>
    <div class='clearLine'>&nbsp;</div>
    
    <div class="grid_4 lable-right">
	 	<label class="form-lbl">民族：</label>
	</div>
    <div class="grid_7">
   		<select  class="form-txt" id="conditionNation" name="ledgerPoorPeople.national.id">
			<pop:OptionTag name="@com.tianque.plugin.account.property.PropertyTypes@NATION" defaultValue="${ledgerPoorPeople.national.id}" />
		</select>
    </div>
    <div class="grid_4 lable-right">
		<label class="form-lbl">文化程度： </label>
	</div>
	<div class="grid_7">
		<select  class="form-txt" id="conditionSchooling" name="ledgerPoorPeople.levelEducation.id">
			   	<pop:OptionTag name="@com.tianque.plugin.account.property.PropertyTypes@SCHOOLING" defaultValue="${ledgerPoorPeople.levelEducation.id}" />
		</select>
	</div>
    <div class='clearLine'>&nbsp;</div>
    
    <div class="grid_4 lable-right">
	   <label class="form-lbl">婚姻状况： </label>
	</div>
	<div class="grid_7">
		<select class="form-txt" id="conditionMaritalState" name="ledgerPoorPeople.maritalStatus.id">
			<pop:OptionTag name="@com.tianque.plugin.account.property.PropertyTypes@MARITAL_STATUS" defaultValue="${ledgerPoorPeople.maritalStatus.id}"  />
		</select>
	</div>
	<div class="grid_4 lable-right">
	    <label class="form-lbl">健康状况：</label>
	</div>
	<div class="grid_7">
		<input type="text" name="ledgerPoorPeople.healthCondition" value="${ledgerPoorPeople.healthCondition}" maxlength="10" class="form-txt {exculdeParticalChar:true,maxlength:10,minlength:0,messages:{exculdeParticalChar:'不能输入非法字符',minlength:$.format('健康状况至少需要输入{0}个字符'),maxlength:$.format('健康状况最多需要输入{0}个字符')}}" />
	</div>
	<div class='clearLine'>&nbsp;</div>
	
	<div class="grid_4 lable-right">
	    <label class="form-lbl">外出及外出原因：</label>
	</div>
	<div class="grid_7">
		<input type="text" name="ledgerPoorPeople.goOutReason" value="${ledgerPoorPeople.goOutReason}" maxlength="10" class="form-txt {exculdeParticalChar:true,maxlength:10,minlength:0,messages:{exculdeParticalChar:'不能输入非法字符',minlength:$.format('外出及外出原因至少需要输入{0}个字符'),maxlength:$.format('外出及外出原因最多需要输入{0}个字符')}}" />
	</div>
	<div class="grid_4 lable-right">
	    <label class="form-lbl">技能特长：</label>
	</div>
	<div class="grid_7">
		<input type="text" name="ledgerPoorPeople.skillsSpeciality" id="skillsSpeciality" value="${ledgerPoorPeople.skillsSpeciality}" maxlength="10" class="form-txt {exculdeParticalChar:true,maxlength:10,minlength:0,messages:{exculdeParticalChar:'不能输入非法字符',minlength:$.format('技能特长至少需要输入{0}个字符'),maxlength:$.format('技能特长最多需要输入{0}个字符')}}" />
	</div>
	<div class='clearLine'>&nbsp;</div>
	
	<div class="grid_4 lable-right">
		<em class="form-req">*</em>
		<label class="form-lbl">具体需求：</label>
	</div>
	<div class="grid_20 heightAuto">
	   <pop:PropertyDictMultiCheckbox  name="requiredTypeList" column="8" domainName="@com.tianque.domain.property.PropertyTypes@LEDGER_POOR_PEOPLE_SPECIFIC_NEED"  />
	</div>
	<div class='clearLine'>&nbsp;</div>
    
    <div class="grid_4 lable-right">
   		<em class="form-req">*</em>
   		<label class="form-lbl">困难类型：</label>
   	</div>
    <div class="grid_18" id="poorTypes">
     	<pop:PropertyDictMultiCheckbox  name="poorTypeList" column="6" domainName="@com.tianque.domain.property.PropertyTypes@LEDGER_POOR_PEOPLE_DIFFICULT_TYPE"  />
    </div>
    
    <div class='clearLine'>&nbsp;</div>
   	<div class="grid_4 lable-right" id="causeText" style='display:none'>
   		<em class="form-req">*</em>
   		<label class="form-lbl">困难原因：</label>
   	</div>
    
    <select  id="causeId" class='form-txt ' style='display:none' >
		   	<pop:OptionTag name="@com.tianque.plugin.account.property.PropertyTypes@LEDGER_POOR_PEOPLE_DIFFICULT_CAUSE" showInternalId="true" />
	</select>
    
  <div id="itemsDiv" class=" grid_20 cf heightAuto" style="margin:10px 0;">
  </div>    
  <div class='clearLine'>&nbsp;</div>   
    <div class="grid_4 lable-right">
		<label class="form-lbl">备注：</label>
    </div>
	<div class="grid_18 heightAuto">
		<textarea  id="appealContent" name="ledgerPoorPeople.remark"  maxlength="200" class='form-txt' >${ledgerPoorPeople.remark}</textarea>
	</div>
  <div class='clearLine'>&nbsp;</div>  
    
    <div id ="dynamicAddModule" ></div>
	<div class='clearLine'>&nbsp;</div>
	
	<div class="grid_22 lable-center"></div>
	<div class='clearLine'>&nbsp;</div>
    <div style=" overflow:scroll;border-collapse: collapse;border: solid #000 1px;height:145;width:690;" >
  		<div class="grid_4 lable-right">
		    <label class="form-lbl">家庭成员：</label>
	    </div>
	    <div class="grid_5"></div>
	    <div class="grid_4 lable-right">
	        <button type="button" id="addFamilyMembers">增加</button>
	    </div>
	    <div class="grid_1"></div>
	    <div class="grid_5">
	     	<button type="button" id="delFamilyMembers">删除</button>
	    </div>
	    <div class="grid_5"></div>
	    
	  <div id="copyAddFamilyMembersParent">
	    <c:if test="${fn:length(ledgerPoorPeople.ledgerPoorPeopleMembers)<1}">
	  	<div id="copyAddFamilyMembers">
	  	<div class="grid_3 lable-right">
   			<label class="form-lbl">姓名：</label>
   		</div>
    	<div class="grid_5">
   			<input type="text" id="ledgerPoorPeopleMembersName" name="ledgerPoorPeopleMembers.name" value="${ledgerPoorPeopleMembers.name }" maxlength="20" class="form-txt {exculdeParticalChar:true,maxlength:20,minlength:2,messages:{exculdeParticalChar:'不能输入非法字符',minlength:$.format('姓名至少需要输入{0}个字符'),maxlength:$.format('姓名最多需要输入{0}个字符')}}" />
    	</div>
		<div class="grid_2 lable-right">
	    	<label class="form-lbl">性别：</label>
		</div>
		<div class="grid_5">
   			<select id="ledgerPoorPeopleMembersGender" name="ledgerPoorPeopleMembers.gender.id" class="form-txt">
	   			<pop:OptionTag name="@com.tianque.plugin.account.property.PropertyTypes@GENDER" defaultValue="${ledgerPoorPeopleMembers.gender.id}"/></select>
		</div>
	    <div class="grid_3 lable-right">
	    	<label class="form-lbl">身份证号：</label>
		</div>
		<div class="grid_6"><!-- ,exsistedIdCard:false -->
   			<input type="text" id="ledgerPoorPeopleMembersIdCardNo" name="ledgerPoorPeopleMembersIdCardNo" value="${ledgerPoorPeopleMembers.idCardNo }" maxlength="18" class="form-txt {idCard:true,messages:{idCard:$.format('请输入一个合法的身份证号码'),exsistedIdCard:function(){return idCardNoData;}}}" />
		</div>
	    
	    <div class='clearLine'>&nbsp;</div>
	    <div class="grid_3 lable-right">
			<label class="form-lbl">保障类型：</label>
		</div>
		<div class="grid_4">
	  	 	<select id="ledgerPoorPeopleMembersSecurityType" name="ledgerPoorPeopleMembers.securityType.id" class="form-txt">
	   			<pop:OptionTag name="@com.tianque.plugin.account.property.PropertyTypes@LEDGER_POOR_PEOPLE_SECURITY_TYPE"  defaultValue="${ledgerPoorPeopleMembers.securityType.id}" />
	  		 </select>
		</div>
		
		<div class="grid_3 lable-right">
	    	<label class="form-lbl">出生年月：</label>
		</div>
		<div class="grid_5" id="birthdayDiv">
   			<input type="text" name="ledgerPoorPeopleMembers.birthday" id="birthDayTimeMembers" value="<fmt:formatDate value="${ledgerPoorPeopleMembers.birthday }" pattern='yyyy-MM-dd'/>" class="form-txt" readonly/>
		</div>
		<div class="grid_3 lable-right">
	    	<label class="form-lbl">与户主关系：</label>
		</div>
		<div class="grid_6">
   			<input type="text"  name="ledgerPoorPeopleMembers.headHouseholdRelation" id="headHouseholdRelationMembers" value="${ledgerPoorPeopleMembers.headHouseholdRelation }" maxlength="10" class="form-txt {exculdeParticalChar:true,maxlength:10,minlength:0,messages:{exculdeParticalChar:'不能输入非法字符',minlength:$.format('与户主关系至少需要输入{0}个字符'),maxlength:$.format('与户主关系最多需要输入{0}个字符')}}" />
		</div>
		
		<div class='clearLine'>&nbsp;</div>
		<div class="grid_3 lable-right">
	 	   <label class="form-lbl">是否失业：</label>
	    </div>
        <div class="grid_4">
   		    <select name="ledgerPoorPeopleMembers.unemployment" id="unemploymentMembers" class="form-txt">
   		       <option value="" <c:if test="${ledgerPoorPeopleMembers.unemployment == ''}">selected="selected"</c:if>>请选择</option>
   			   <option value="false" <c:if test="${ledgerPoorPeopleMembers.unemployment == false}">selected="selected"</c:if>>否</option>
   			   <option value="true" <c:if test="${ledgerPoorPeopleMembers.unemployment == true}">selected="selected"</c:if>>是</option>
   		    </select>
        </div>
	    <div class="grid_3 lable-right">
	    	<label class="form-lbl">健康状况：</label>
	    </div>
	    <div class="grid_5">
   			<input type="text" name="ledgerPoorPeopleMembers.healthCondition" id="healthConditionMembers" value="${ledgerPoorPeopleMembers.healthCondition }" maxlength="10" class="form-txt {exculdeParticalChar:true,maxlength:10,minlength:0,messages:{exculdeParticalChar:'不能输入非法字符',minlength:$.format('健康状况至少需要输入{0}个字符'),maxlength:$.format('健康状况最多需要输入{0}个字符')}}" />
	    </div>
	    
	    <div class="grid_3 lable-right">
	   	 <label class="form-lbl">技能特长：</label>
		</div>
		<div class="grid_6">
			<input type="text" name="ledgerPoorPeopleMembers.skillsSpeciality" id="_skillsSpeciality" value="${ledgerPoorPeopleMembers.skillsSpeciality}" maxlength="10" class="form-txt {exculdeParticalChar:true,maxlength:10,minlength:0,messages:{exculdeParticalChar:'不能输入非法字符',minlength:$.format('技能特长至少需要输入{0}个字符'),maxlength:$.format('技能特长最多需要输入{0}个字符')}}" />
		</div>
	    <div class='clearLine'>&nbsp;</div>
	    <hr style="height:1px;border:none;border-top:1px dashed black;" />
	  </div>
	  </c:if>
	  
	  <c:forEach items="${ledgerPoorPeople.ledgerPoorPeopleMembers}" var="ledgerPoorPeopleMembers">
	   <div id="copyAddFamilyMembers">
	    <div class="grid_3 lable-right">
   			<label class="form-lbl">姓名：</label>
   		</div>
    	<div class="grid_5">
   			<input type="text" id="ledgerPoorPeopleMembersName" name="ledgerPoorPeopleMembers.name" value="${ledgerPoorPeopleMembers.name }" maxlength="20" class="form-txt {exculdeParticalChar:true,maxlength:20,minlength:2,messages:{exculdeParticalChar:'不能输入非法字符',minlength:$.format('姓名至少需要输入{0}个字符'),maxlength:$.format('姓名最多需要输入{0}个字符')}}" />
    	</div>
		<div class="grid_2 lable-right">
	    	<label class="form-lbl">性别：</label>
		</div>
		<div class="grid_5">
   			<select id="ledgerPoorPeopleMembersGender" name="ledgerPoorPeopleMembers.gender.id" class="form-txt">
	   			<pop:OptionTag name="@com.tianque.plugin.account.property.PropertyTypes@GENDER" defaultValue="${ledgerPoorPeopleMembers.gender.id}"/></select>
		</div>
	    <div class="grid_3 lable-right">
	    	<label class="form-lbl">身份证号：</label>
		</div>
		<div class="grid_6"><!-- ,exsistedIdCard:false -->
   			<input type="text" id="ledgerPoorPeopleMembersIdCardNo" name="ledgerPoorPeopleMembersIdCardNo" value="${ledgerPoorPeopleMembers.idCardNo }" maxlength="18" class="form-txt {idCard:true,messages:{idCard:$.format('请输入一个合法的身份证号码'),exsistedIdCard:function(){return idCardNoData;}}}" />
		</div>
	    
	    <div class='clearLine'>&nbsp;</div>
	    <div class="grid_3 lable-right">
			<label class="form-lbl">保障类型：</label>
		</div>
		<div class="grid_4">
	  	 	<select id="ledgerPoorPeopleMembersSecurityType" name="ledgerPoorPeopleMembers.securityType.id" class="form-txt">
	   			<pop:OptionTag name="@com.tianque.plugin.account.property.PropertyTypes@LEDGER_POOR_PEOPLE_SECURITY_TYPE"  defaultValue="${ledgerPoorPeopleMembers.securityType.id}" />
	  		 </select>
		</div>
		
		<div class="grid_3 lable-right">
	    	<label class="form-lbl">出生年月：</label>
		</div>
		<div class="grid_5" id="birthdayDiv">
   			<input type="text" name="ledgerPoorPeopleMembers.birthday" id="birthDayTimeMembers" value="<fmt:formatDate value="${ledgerPoorPeopleMembers.birthday }" pattern='yyyy-MM-dd'/>" class="form-txt" readonly/>
		</div>
		<div class="grid_3 lable-right">
	    	<label class="form-lbl">与户主关系：</label>
		</div>
		<div class="grid_6">
   			<input type="text"  name="ledgerPoorPeopleMembers.headHouseholdRelation" id="headHouseholdRelationMembers" value="${ledgerPoorPeopleMembers.headHouseholdRelation }" maxlength="10" class="form-txt {exculdeParticalChar:true,maxlength:10,minlength:0,messages:{exculdeParticalChar:'不能输入非法字符',minlength:$.format('与户主关系至少需要输入{0}个字符'),maxlength:$.format('与户主关系最多需要输入{0}个字符')}}" />
		</div>
		
		<div class='clearLine'>&nbsp;</div>
		<div class="grid_3 lable-right">
	 	   <label class="form-lbl">是否失业：</label>
	    </div>
        <div class="grid_4">
   		    <select name="ledgerPoorPeopleMembers.unemployment" id="unemploymentMembers" class="form-txt">
   		       <option value="" <c:if test="${ledgerPoorPeopleMembers.unemployment == ''}">selected="selected"</c:if>>请选择</option>
   			   <option value="false" <c:if test="${ledgerPoorPeopleMembers.unemployment == false}">selected="selected"</c:if>>否</option>
   			   <option value="true" <c:if test="${ledgerPoorPeopleMembers.unemployment == true}">selected="selected"</c:if>>是</option>
   		    </select>
        </div>
	    <div class="grid_3 lable-right">
	    	<label class="form-lbl">健康状况：</label>
	    </div>
	    <div class="grid_5">
   			<input type="text" name="ledgerPoorPeopleMembers.healthCondition" id="healthConditionMembers" value="${ledgerPoorPeopleMembers.healthCondition }" maxlength="10" class="form-txt {exculdeParticalChar:true,maxlength:10,minlength:0,messages:{exculdeParticalChar:'不能输入非法字符',minlength:$.format('健康状况至少需要输入{0}个字符'),maxlength:$.format('健康状况最多需要输入{0}个字符')}}" />
	    </div>
	    
	    <div class="grid_3 lable-right">
	   	 <label class="form-lbl">技能特长：</label>
		</div>
		<div class="grid_6">
			<input type="text" name="ledgerPoorPeopleMembers.skillsSpeciality" id="_skillsSpeciality" value="${ledgerPoorPeopleMembers.skillsSpeciality}" maxlength="10" class="form-txt {exculdeParticalChar:true,maxlength:10,minlength:0,messages:{exculdeParticalChar:'不能输入非法字符',minlength:$.format('技能特长至少需要输入{0}个字符'),maxlength:$.format('技能特长最多需要输入{0}个字符')}}" />
		</div>
	    <div class='clearLine'>&nbsp;</div>
	    <hr style="height:1px;border:none;border-top:1px dashed black;" />
	  </div>		
	  </c:forEach>
	  </div>
	  </div>
  	
    <div class='clearLine'>&nbsp;</div>
	
	</form>
</div>
<script type="text/javascript">

function idCardChanged(idCardNoObj){
	var info = getBirthDayTextFromIdCard(idCardNoObj.val());
	resetBirthdayField(info, $($(idCardNoObj.parent()).parent()).find("#birthDayTimeMembers"));
	_fillGenderByIdCardNo(idCardNoObj);
	/*
	var text = idCardNoObj.val();
	$.ajax({
		async: false ,
		url:"${path}/threeRecordsIssue/ledgerPoorPeopleManage/getCountrymenByIdCardNoAndOrgInternalCode.action",
	   	data:{
			"householdStaffVo.idCardNo": text,
        },
		success:function(responseData){
			if(null == responseData ||　"" == responseData || responseData.idCardNo == null){
				$.messageBox({message:"该人员未录入人口模块的人口基本信息！",level: "error"});
				return;
			}else{
				resetBirthdayField(responseData.name, $($(idCardNoObj.parent()).parent()).find("#ledgerPoorPeopleMembersName"));
				resetBirthdayField(responseData.gender.id, $($(idCardNoObj.parent()).parent()).find("#ledgerPoorPeopleMembersGender"));
			}
		}
	});*/
}

function _fillGenderByIdCardNo(select){
    var sex;
    var idCardNo = select.val();
    if(idCardNo==null||idCardNo=="" || typeof(idCardNo)=="undefined"){
        return;
    }
    if(idCardNo.length!=15 && idCardNo.length!=18){
        return;
    }

    if (15 == idCardNo.length) { //15位身份证号码
        if (parseInt(idCardNo.charAt(14) / 2) * 2 != idCardNo.charAt(14))
            sex = '男';
        else
            sex = '女';
     }
    if (18 == idCardNo.length) { //18位身份证号码
        if (parseInt(idCardNo.charAt(16) / 2) * 2 != idCardNo.charAt(16))
           sex = '男';
        else
          sex = '女';
    }
    for(var i = 0; i < $($(select.parent()).parent()).find("#ledgerPoorPeopleMembersGender").find('option').length; i++){
    	if($($(select.parent()).parent()).find("#ledgerPoorPeopleMembersGender").find('option')[i].text == sex){
			$($(select.parent()).parent()).find("#ledgerPoorPeopleMembersGender option").eq(i).attr("selected",true);
			break;
    	}
    }
}

function resetBirthdayField(text, objDiv){
	if (text != "" && objDiv != undefined && objDiv != null){
		objDiv.val(text);
	}
}

//根据身份证得到出生日期
function getBirthDayTextFromIdCard(idCard){
	if(idCard!=null&&idCard.length==18){
		idCard=idCard.substring(6,14);
		if(idCard.substring(4,6)<=0||idCard.substring(4,6)>12){
			return "";
		}else if(idCard.substring(6,8)<=0||idCard.substring(6,8)>31){
			return "";
		}else{
			return idCard.substring(0,4)+"-"+idCard.substring(4,6)+"-"+idCard.substring(6,8);
		}
	}else if(idCard!=null&&idCard.length==15){
		idCard=idCard.substring(6,12);
		if(idCard.substring(2,4)<=0||idCard.substring(2,4)>12){
			return "";
		}else if(idCard.substring(4,6)<=0||idCard.substring(4,6)>31){
			return "";
		}else{
			return "19"+idCard.substring(0,2)+"-"+idCard.substring(2,4)+"-"+idCard.substring(4,6);
		}
	}
	return "";
}

function initOccurOrgSelector(){
	var tree=$("#occurOrg").treeSelect({
		inputName:"ledgerPoorPeople.occurOrg.id",
		loadCom:function(){
			if(<s:property value='!"add".equals(mode)'/>){
				$.setTreeValue(getDefaultOccurOrg(),tree); 
			}
			if($('#id').val()=="")
				$("#occurOrg").val("请选择");
		}
	});
}

function getDefaultOccurOrg(){
	<s:if test="null!=ledgerPoorPeople.occurOrg && null!=ledgerPoorPeople.occurOrg.id">
		return "${ledgerPoorPeople.occurOrg.id}";
	</s:if>
	<s:else>
		return -1;
	</s:else>
}
<s:if test='"add".equals(mode)'>
$.ajax({
	async: false,
	url: "${path}/sysadmin/orgManage/getOrgRelativePath.action",
	data:{
		"organization.id":$("#organizationId").val()
	},
	success:function(responseData){
		$("#bookingUnit").val(responseData);
		
	}
});
</s:if>
//电话号码
jQuery.validator.addMethod("phoneAndMobile", function(value, element){
	if(value==null||value==undefined||value=="" ){return true};
	var mobile = /^(1[3|4|5|7|8][0-9])+\d{8}$/;
	var length = value.length;
	if(length == 11 && mobile.test(value)){return true;}
	var phone = /^([0-9]{3,4}-)+[0-9]{7,8}$/;	
	if (value.match(phone)==null) {return false;}
	return true
});
jQuery.validator.addMethod("validatorNativePlaceAddress", function (value,element){
	if(value==null||value==undefined||value==""){return true}
	var pattern = new RegExp("[`~!@%#$^&*()=|{}':;',　\\[\\]<>/? \\.；：%……+￥（）【】‘”“'。，、？ ！]");
	return this.optional(element)||!pattern.test(value) ; 
});
jQuery.validator.addMethod("isNOTNULL", function (value,element){
	if(value==null||value==undefined||value==""||value=="请选择"){return false}
	return true ; 
});

jQuery.validator.addMethod("validatorLastYearMemberIncome", function (value,element){
	if(value==null||value==undefined||value==""){return true}
	var pattern = /^\d{1,6}(\.\d{1,2})?$/;
	return pattern.test(value) ; 
});
var idCardNoData;
jQuery.validator.addMethod("exsistedIdCard", function(value, element){
	var value=$('#idCardNo').val();
	if(value==null||value==undefined||value==""){return true}
	$.ajax({
		async: false ,
		url:"${path}/threeRecordsIssue/ledgerPoorPeopleManage/existedPoorPeopleByIdCardNo.action",
	   	data:{
			"idCardNo": $('#idCardNo').val(),
        },
		success:function(responseData){
			idCardNoData = responseData;
		}
	});
	if(!(idCardNoData==null||idCardNoData=="")){
		return false;
	}
	return true;
});

function initFamilyMembersValue(objs){
	if(null != objs){
		objs.find("#ledgerPoorPeopleMembersName").val(null);
		objs.find("#ledgerPoorPeopleMembersGender").val(0);
		objs.find("#ledgerPoorPeopleMembersIdCardNo").val(null);
		objs.find("#ledgerPoorPeopleMembersSecurityType").val(0);
		objs.find("#birthDayTimeMembers").val(null);
		objs.find("#healthConditionMembers").val(null);
		objs.find("#headHouseholdRelationMembers").val(null);
		objs.find("#unemploymentMembers").get(0).options[0].selected = true;
		objs.find("#_skillsSpeciality").val(null);
	}
}

$(document).ready(function(){
	
	$("#registrant").val('${NAME}');
	if($("#convertId").val()!=undefined){
		$("#name").val($('#convert_name').text());
		$("#serverTelephone").val($('#convert_mobile').text());
		$("#appealContent").val($('#convert_description').text());					
	}
	$('#addFamilyMembers').click(function(){
		var childrens = $("#copyAddFamilyMembersParent").children();
		var objs = $(childrens[childrens.length - 1]).clone();
		initFamilyMembersValue(objs);
		objs.prependTo($("#copyAddFamilyMembersParent"));
		
		objs.find("#ledgerPoorPeopleMembersIdCardNo").bind("blur",function(){
			  var idCard = objs.find("#ledgerPoorPeopleMembersIdCardNo").val();
			  if(null != idCard && "" != idCard){
				  idCardChanged(objs.find("#ledgerPoorPeopleMembersIdCardNo"));
			  }
		});
	});
	
	$('#delFamilyMembers').click(function(){
		var childrens = $("#copyAddFamilyMembersParent").children();
		if(childrens.length > 1){
			$(childrens[0]).remove();
		}else if(childrens.length == 1){
			var objs = $(childrens[0]);
			initFamilyMembersValue(objs);
		}
	});
	
	$('#registrationTime').datePicker({
		yearRange:'1930:2060',
		dateFormat:'yy-mm-dd',
		maxDate:'%y-%M-#{%d}',
		minDate:'-'+(new Date().getDate()-1)+'d'
	});
	
	$('#birthDayTime').datePicker({
		yearRange: '1900:2030',
		dateFormat: 'yy-mm-dd',
        maxDate:'+0d'
    });
	initOccurOrgSelector();
	

	$("#idCardNo").blur(function(){
		var idCard = $("#idCardNo").val();
		if(null != idCard && "" != idCard){
	/*		$.ajax({
				async: false ,
				url:"${path}/threeRecordsIssue/ledgerPoorPeopleManage/getCountrymenByIdCardNoAndOrgInternalCode.action",
			   	data:{
					"householdStaffVo.idCardNo": idCard,
		        },
				success:function(responseData){
					if(null == responseData ||　"" == responseData || responseData.idCardNo == null){
						var text = getBirthDayTextFromIdCard(idCard);
						resetBirthdayField(text, $("#birthDayTime"));
						$.messageBox({message:"该人员未录入人口模块的人口基本信息，请先录入！",level: "error"});
						return;
					}else{
						$("#name").val(responseData.name);
						$("#gender").val(responseData.gender.id);*/
						var text = getBirthDayTextFromIdCard(idCard);
						resetBirthdayField(text, $("#birthDayTime"));
						fillGenderByIdCardNo(idCard,"gender","populationGender",true);
						//$("#birthDayTime").val(responseData.birthDay);
	/*					$("#position").val(responseData.career);
						$("#permanentAddress").val(responseData.currentAddress);
						$("#conditionNation").val(responseData.nation.id);
						$("#serverTelephone").val(responseData.mobileNumber);
						$("#accountNo").val(responseData.houseCode);
						$("#censusRegisterAddress").val(responseData.nativePlaceAddress);
						$("#conditionMaritalState").find("option").each(function(){
					        if($(this).text() ==responseData.maritalState.displayName) 
					           $(this).attr('selected',"selected");
					   });
					   $("#conditionSchooling").find("option").each(function(){
					        if($(this).text() ==responseData.schooling.displayName) 
					           $(this).attr('selected',"selected");
					   });
						
					}
				}
			});*/
		  }
	  });
	
	
	$("input[name='ledgerPoorPeopleMembersIdCardNo']").blur(function(){
		var idCard = $(this).val();
		if(null != idCard && "" != idCard){
			 idCardChanged($(this));
		}
	 });
	
	
	function getParm(){
		    var parm = "";
			var childers = $("#copyAddFamilyMembersParent").children();
			if(null != childers && childers.length > 0){
				for(var i = 0; i < childers.length; i++){
					var childer = $(childers[i]);
					var name = childer.find("#ledgerPoorPeopleMembersName").val();
					var gender = childer.find("#ledgerPoorPeopleMembersGender").val();
					var idCardNo = childer.find("#ledgerPoorPeopleMembersIdCardNo").val();
					var securityType = childer.find("#ledgerPoorPeopleMembersSecurityType").val();
					var birthDay = childer.find("#birthDayTimeMembers").val();
					var healthCondition = childer.find("#healthConditionMembers").val();
					var headHouseholdRelation = childer.find("#headHouseholdRelationMembers").val();
					var unemployment = childer.find("#unemploymentMembers").val();
					var skillsSpeciality = childer.find("#_skillsSpeciality").val()
					
					if(null != name && '' != name){
						parm += "name:"+name+",";
					}
					if(null != gender && '' != gender){
						parm += "gender:"+gender+",";
					}
					if(null != idCardNo && '' != idCardNo){
						parm += "idCardNo:"+idCardNo+",";
					}
					if(null != securityType && '' != securityType){
						parm += "securityType:"+securityType+",";
					}
					if(null != birthDay && '' != birthDay){
						parm += "birthday:"+birthDay+",";
					}
					if(null != healthCondition && '' != healthCondition){
						parm += "healthCondition:"+healthCondition+",";
					}
					if(null != headHouseholdRelation && '' != headHouseholdRelation){
						parm += "headHouseholdRelation:"+headHouseholdRelation+",";
					}
					if(null != unemployment && '' != unemployment){
						parm += "unemployment:"+unemployment+",";
					}
					if(null != skillsSpeciality && '' != skillsSpeciality){
						parm += "skillsSpeciality:"+skillsSpeciality+",";
					}
					if(null != parm && '' != parm){
						parm += "},";
					}
				}
			}
			return parm;
	}
	
	$("#maintainForm").formValidate({
		promptPosition: "bottomLeft",
		submitHandler: function(form) {
			$(form).ajaxSubmit({
				data : {
					"ledgerPoorPeopleMembersParm" :  getParm(),
				},
	             success: function(data){
                     if(data==null || !data.id){
                    	 $.messageBox({ message:data, level: "error" });
                     	return;
                     }
        	   		 <s:if test='"add".equals(mode) || "copy".equals(mode) '>
				    	$.messageBox({message:"新增成功!"});
				     </s:if>
				     <s:if test='"edit".equals(mode)'>
				    	$.messageBox({message:"修改成功!"});
				     </s:if>

				     <s:if test="#request.type=='convert'">	
						$("#ledgerConvertDialog").dialog("close");
			        	$("#ledgerConvertList").trigger("reloadGrid");
				     </s:if>
				     <s:else>
						 $("#ledgerPoorPeopleList").trigger("reloadGrid");
					     $("#steadyWorkDialog").dialog("close");
					     $("#ledgerPoorPeopleList").setSelection(data.id);
				     </s:else>
	      	   },
	      	   error: function(XMLHttpRequest, textStatus, errorThrown){
	      	      alert("提交错误");
	      	   }
			});
		},
		rules:{
			"ledgerPoorPeople.registrant":{
				required:true,
				exculdeParticalChar:true,
				minlength:2,
				maxlength:20
			},
			"ledgerPoorPeople.remark":{
				maxlength:200
			},
			"ledgerPoorPeople.position.id":{
				required:true
			},
			"ledgerPoorPeople.createTableType.id":{
				required:true
			},
			"requiredTypeList":{
				required:true
			},
			"poorTypeList":{
				required:true
			},
			"causeTypeList":{
				required:true
			},
			"ledgerPoorPeople.gender.id":{
				required:true
			},"ledgerPoorPeople.mobileNumber":{
				phoneAndMobile:true
			},
			"ledgerPoorPeople.registrationTime":{
				required:true
			}
		},
		messages:{
			"ledgerPoorPeople.registrant":{
				required:"请输入登记人姓名",
				exculdeParticalChar:"不能输入非法字符",
				minlength:$.format("登记人姓名至少需要输入{0}个字符"),
				maxlength:$.format("登记人姓名最多需要输入{0}个字符")
			},
			"ledgerPoorPeople.createTableType.id":{
				required:"请选择建卡类型"
			},"ledgerPoorPeople.mobileNumber":{
				phoneAndMobile:"请输入以固定电话：028-87653333或者手机：15102888888为格式的号码"	
			},"ledgerPoorPeople.remark":{
				maxlength:$.format("备注最多需要输入{0}个字符")
			},
			"ledgerPoorPeople.registrationTime":{
				required:"请选择登记时间"
			}
		},
		ignore:':hidden'
	});
	<s:if test='"add".equals(mode)'>
		$("#createTableType option").eq(1).attr("selected", true);
	</s:if>
	

	$("input[name='poorTypeList']").each(function(i){
		$(this).change(function(event){
			  if($(this).attr("checked")=="checked") {
		           $("#_"+i).show();
		           initPoorBigInfoChange($(this).parent().text(),0);
		      }else{
		    	  $("#_"+i).hide();
		    	  $("#_"+i).find("input[name='causeTypeList']").each(function(){
		    		  $(this).removeAttr("checked");
			     })
			     initPoorBigInfoChange($(this).parent().text(),1);
			  }
			  setTypeAndSource();
			 
		})
   });

	 addCauseItem();

	$("input[name='causeTypeList']").each(function(){
		$(this).change(function(event){
			setTypeAndSource();
		})
   });

	$("input[name='requiredTypeList']").each(function(){
		$(this).change(function(event){
			setRequiredType();
		})
   });
	initTypeAndSource();
	initRequired();
	setUnEdit();
});


function setRequiredType(){
	var type="";
	$("input[name='requiredTypeList']").each(function(){
	  if($(this).attr("checked")=="checked") {
        	type+=$(this).val()+",";
      }
   })
	$('#requiredType_').val(type);
	
}

function setTypeAndSource(){
	var type="";
	var source="";
	$("#causeText").hide();
	$("input[name='poorTypeList']").each(function(){
	  if($(this).attr("checked")=="checked") {
        	type+=$(this).val()+",";
        	$("#causeText").show();
      }
   })
   $("input[name='causeTypeList']").each(function(){
    	if($(this).attr("checked")=="checked") {
    		source+=$(this).val()+",";
      }
	})
	$('#poorType_').val(type);
	$('#poorSource_').val(source);
	
}

function initRequired(){
	var types=$('#requiredType_').val().split(',');
	if(types!=null){
		$("input[name='requiredTypeList']").each(function(){
			for(var i=0 ;i<types.length;i++){
				  if(types[i]== $(this).val()){
					  $(this).attr("checked","checked");
				  }
			}
		 })
	}
}



function initTypeAndSource(){
	var types=$('#poorType_').val().split(',');
	var sources=$('#poorSource_').val().split(',');
	if(types!=null){
		$("input[name='poorTypeList']").each(function(n){
			initPoorBigInfoChange($(this).parent().text(),1);
			var hidden =1;
			for(var i=0 ;i<types.length;i++){
				  if(types[i]== $(this).val()){
					  $(this).attr("checked","checked");
					  $("#_"+n).show();
					  $("#causeText").show();
					  hidden =0;
				  }
			} 
			initPoorBigInfoChange($(this).parent().text(),hidden);
		 })
	}
	if(sources!=null){
		$("input[name='causeTypeList']").each(function(){
			for(var i=0 ;i<sources.length;i++){
				  if(sources[i]== $(this).val()){
					  $(this).attr("checked","checked");
				  }
			}
		 })
	}
}

function initPoorBigInfoChange(poorBigInfo,hidden){
	// var poorBigInfo = $("#_poorBigInfo").find("option:selected").text();
     if(undefined != poorBigInfo && null != poorBigInfo){
     	var obj = $("#dynamicAddModule");
     	if(poorBigInfo == "生活"){
     		obj.append(addOrphan(hidden));
     		obj.append(addLonelinessOld(hidden));
     		obj.append(addClearLine(hidden));
     		//obj.append(addSkillsSpeciality(hidden));
     	}
		if(poorBigInfo == "医疗"){
			obj.append(addOtherInfo(hidden));
     	}
		if(poorBigInfo == "住房"){
			obj.append(addHousing(hidden));
     		obj.append(addHousingStructure(hidden));
     		obj.append(addClearLine(hidden));
     		obj.append(addHousingArea(hidden));
     		obj.append(addBuildHouseDate(hidden));
     		obj.append(addClearLine(hidden));
     		initBuildHouseDate(hidden);
		}
		if(poorBigInfo == "就学"){
			obj.append(addOrphan(hidden));
		}
		if(poorBigInfo == "就业"){
			obj.append(addUnemploymentDate(hidden));
     		obj.append(addUnemploymentReason(hidden));
     		obj.append(addClearLine());
     		obj.append(addRegistrationCardNumber(hidden));
     		//obj.append(addSkillsSpeciality(hidden));
     		obj.append(addClearLine(hidden));
     		initUnemploymentDate(hidden);
		}
     }
}


function  getOccurOrgId(){
	return $("#occurOrgId").val();
} 


function addCauseItem(){
     var items="";
     for(var i=0;i<6;i++){
         items+="<div id='_"+i+"' style='display:none' class='grid_22 heightAuto'>"
		 $("#causeId option").each(function(n){
			if($(this).attr("internalid")==i){
				items+="<input id='causeTypeList["+n+"]'  name='causeTypeList' type='checkbox' value='"+$(this).attr("value")+"'>" + $(this).text();
			};
		})
		items+="</div> <div class='clearLine'>&nbsp;</div>"
    }
 	$("#itemsDiv").append(items);
}

function addLonelinessOld(hidden){
	var oldHtml="";
	if($('#lonelinessOldDiv').text()==""){
		oldHtml = "<div id='lonelinessOldDiv'>"
                  +"<div class='grid_4 lable-right'>"
	   					+"<label class='form-lbl'>是否孤老：</label>"
 				  +"</div>"
 				  +"<div class='grid_7'>"
	    		  		+"<select name='ledgerPoorPeople.lonelinessOld' id='lonelinessOld' class='form-txt'>"
		   					+"<option value='false' <s:if test='false == ledgerPoorPeople.lonelinessOld'>selected='selected'</s:if>>否</option>"
		   					+"<option value='true' <s:if test='true == ledgerPoorPeople.lonelinessOld'>selected='selected'</s:if>>是</option>"
	    		 		 +"</select>"
 				  +"</div>"
 				 +"</div>";
	}
	if(hidden==0){
		$('#lonelinessOldDiv').show();
	}else{
		$('#lonelinessOldDiv').hide();
	}
 	return oldHtml;
}

function addOrphan(hidden){
	var oldHtml="";
	if($('#orphanDiv').text()==""){
		oldHtml = "<div id='orphanDiv'>"
                  +"<div class='grid_4 lable-right'>"
	   					+"<label class='form-lbl'>是否孤儿：</label>"
 				  +"</div>"
 				  +"<div class='grid_7'>"
	    		  		+"<select name='ledgerPoorPeople.orphan' id='orphan' class='form-txt'>"
		   					+"<option value='false' <s:if test='false == ledgerPoorPeople.orphan'>selected='selected'</s:if>>否</option>"
		   					+"<option value='true' <s:if test='true == ledgerPoorPeople.orphan'>selected='selected'</s:if>>是</option>"
	    		 		 +"</select>"
 				  +"</div>"
 				 +"</div>";
	}
	if(hidden==0){
		$('#orphanDiv').show();
	}else{
		if($('#poorTypeList[0]').attr("checked")=="checked"||$('#poorTypeList[3]').attr("checked")=="checked"){
			return;
		}else{
			$('#orphanDiv').hide();
		}
	}
 	return oldHtml;
}

function addSkillsSpeciality(hidden){
	var oldHtml="";
	
	if($('#skillsSpecialityDiv').text()==""){
		oldHtml = "<div id='skillsSpecialityDiv'>"
		          +"<div class='grid_4 lable-right'>"
					+"<label class='form-lbl'>技能特长：</label>"
	 			  +"</div>"
	  			  +"<div class='grid_7'>"
  					+"<input type='text' name='ledgerPoorPeople.skillsSpeciality' id='skillsSpeciality'  maxlength='10' value='${ledgerPoorPeople.skillsSpeciality}'  class='form-txt {exculdeParticalChar:true,maxlength:10,minlength:2,messages:{exculdeParticalChar:'不能输入非法字符',minlength:$.format('技能特长至少需要输入{0}个字符'),maxlength:$.format('技能特长最多需要输入{0}个字符')}}' />"
	 			  +"</div>"
	 			 +"</div>";
	}
	if(hidden==0){
		$('#skillsSpecialityDiv').show();
	}else{
		if($('#poorTypeList[0]').attr("checked")=="checked"||$('#poorTypeList[4]').attr("checked")=="checked"){
			return;
		}else{
			$('#skillsSpecialityDiv').hide();
		}
	}
	return oldHtml;
}

function addHousing(hidden){
	var oldHtml="";
	
	if($('#housingDiv').text()==""){
		oldHtml = "<div id='housingDiv'>"
				  +"<div class='grid_4 lable-right'>"
	   					+"<label class='form-lbl'>有无住房：</label>"
 				  +"</div>"
 				  +"<div class='grid_7'>"
	    		  		+"<select name='ledgerPoorPeople.housing' id='housing' class='form-txt'>"
		   					+"<option value='false' <s:if test='false == ledgerPoorPeople.housing'>selected='selected'</s:if>>无</option>"
		   					+"<option value='true' <s:if test='true == ledgerPoorPeople.housing'>selected='selected'</s:if>>有</option>"
	    		 		 +"</select>"
 				  +"</div>";
 				  +"</div>"
	}
	if(hidden==0){
		$('#housingDiv').show();
	}else{
		$('#housingDiv').hide();
	}
 	return oldHtml;
}

function addHousingStructure(hidden){
	var oldHtml="";
	
	if($('#housingStructureDiv').text()==""){
		oldHtml = "<div id='housingStructureDiv'>"
		          +"<div class='grid_4 lable-right'>"
					+"<label class='form-lbl'>住房结构：</label>"
	 			  +"</div>"
	  			  +"<div class='grid_7'>"
  					+"<input type='text' name='ledgerPoorPeople.housingStructure' id='housingStructure'  maxlength='10' value='${ledgerPoorPeople.housingStructure}'  class='form-txt {exculdeParticalChar:true,maxlength:10,minlength:2,messages:{exculdeParticalChar:'不能输入非法字符',minlength:$.format('住房结构至少需要输入{0}个字符'),maxlength:$.format('住房结构最多需要输入{0}个字符')}}' />"
	 			  +"</div>"
	 			 +"</div>";
	}
	if(hidden==0){
		$('#housingStructureDiv').show();
	}else{
		$('#housingStructureDiv').hide();
	}
	return oldHtml;
}

function addHousingArea(hidden){
	var oldHtml="";
	
	if($('#housingAreaDiv').text()==""){
		oldHtml = "<div id='housingAreaDiv'>"
		          +"<div class='grid_4 lable-right'>"
					+"<label class='form-lbl'>住房面积：</label>"
	 			  +"</div>"
	  			  +"<div class='grid_7'>"
  					+"<input type='text' name='ledgerPoorPeople.housingArea' id='housingArea'  maxlength='10' value='${ledgerPoorPeople.housingArea}'  class='form-txt {maxlength:10,minlength:2,messages:{minlength:$.format('住房面积至少需要输入{0}个字符'),maxlength:$.format('住房面积最多需要输入{0}个字符')}}' />"
	 			  +"</div>"
	 			 +"</div>";
	}
	if(hidden==0){
		$('#housingAreaDiv').show();
	}else{
		$('#housingAreaDiv').hide();
	}
	return oldHtml;
}

function addBuildHouseDate(hidden){
	var oldHtml="";
	
	if($('#buildHouseDateDiv').text()==""){
		oldHtml = "<div id='buildHouseDateDiv'>"
		          +"<div class='grid_4 lable-right'>"
					+"<label class='form-lbl'>建房年月：</label>"
	 			  +"</div>"
	  			  +"<div class='grid_7'>"
  					+"<input type='text' name='ledgerPoorPeople.buildHouseDate' id='buildHouseDate' value='<s:date name='ledgerPoorPeople.buildHouseDate' format='yyyy-MM-dd'/>' class='form-txt' readonly/>"
	 			  +"</div>"
	 			 +"</div>";
	}
	if(hidden==0){
		$('#buildHouseDateDiv').show();
	}else{
		$('#buildHouseDateDiv').hide();
	}
	return oldHtml;
}

function initBuildHouseDate(){
	$('#buildHouseDate').datePicker({
		yearRange: '1900:2030',
		dateFormat: 'yy-mm-dd',
        maxDate:'+0d'
    });
}

function addUnemploymentDate(hidden){
	var oldHtml="";
	
	if($('#unemploymentDateDiv').text()==""){
		oldHtml = "<div id='unemploymentDateDiv'>"
		          +"<div class='grid_4 lable-right'>"
					+"<label class='form-lbl'>失业时间：</label>"
	 			  +"</div>"
	  			  +"<div class='grid_7'>"
  					+"<input type='text' name='ledgerPoorPeople.unemploymentDate' id='unemploymentDate' value='<s:date name='ledgerPoorPeople.unemploymentDate' format='yyyy-MM-dd'/>' class='form-txt' readonly/>"
	 			  +"</div>"
	 			 +"</div>";
	}
	if(hidden==0){
		$('#unemploymentDateDiv').show();
	}else{
		$('#unemploymentDateDiv').hide();
	}
	return oldHtml;
}

function initUnemploymentDate(){
	$('#unemploymentDate').datePicker({
		yearRange: '1900:2030',
		dateFormat: 'yy-mm-dd',
        maxDate:'+0d'
    });
}

function addUnemploymentReason(hidden){
	var oldHtml="";
	
	if($('#unemploymentReasonDiv').text()==""){
	   oldHtml = "<div id='unemploymentReasonDiv'>"
		          +"<div class='grid_4 lable-right'>"
					+"<label class='form-lbl'>失业原因：</label>"
	 			  +"</div>"
	  			  +"<div class='grid_7'>"
  					+"<input type='text' name='ledgerPoorPeople.unemploymentReason' id='unemploymentReason'  maxlength='10' value='${ledgerPoorPeople.unemploymentReason}'  class='form-txt {exculdeParticalChar:true,maxlength:10,minlength:2,messages:{exculdeParticalChar:'不能输入非法字符',minlength:$.format('失业原因至少需要输入{0}个字符'),maxlength:$.format('失业原因最多需要输入{0}个字符')}}' />"
	 			  +"</div>"
	 			 +"</div>";
	}
	if(hidden==0){
		$('#unemploymentReasonDiv').show();
	}else{
		$('#unemploymentReasonDiv').hide();
	}
	return oldHtml;
}

function addRegistrationCardNumber(hidden){
	var oldHtml="";
	
	if($('#registrationCardNumberDiv').text()==""){
		oldHtml = "<div id='registrationCardNumberDiv'>"
		          +"<div class='grid_4 lable-right'>"
					+"<label class='form-lbl'>登记证号：</label>"
	 			  +"</div>"
	  			  +"<div class='grid_7'>"
  					+"<input type='text' name='ledgerPoorPeople.registrationCardNumber' id='registrationCardNumber'  maxlength='10' value='${ledgerPoorPeople.registrationCardNumber}'  class='form-txt {exculdeParticalChar:true,maxlength:10,minlength:2,messages:{exculdeParticalChar:'不能输入非法字符',minlength:$.format('登记证号至少需要输入{0}个字符'),maxlength:$.format('登记证号最多需要输入{0}个字符')}}' />"
	 			  +"</div>"
	 			 +"</div>";
	}
	if(hidden==0){
		$('#registrationCardNumberDiv').show();
	}else{
		$('#registrationCardNumberDiv').hide();
	}
	return oldHtml;
}

function addOtherInfo(hidden){
	var oldHtml="";
	
	if($('#otherInfoDiv').text()==""){
		oldHtml = "<div id='otherInfoDiv'>"
		          +"<div class='grid_4 lable-right'>"
					+"<label class='form-lbl'>其他：</label>"
	 			  +"</div>"
	  			  +"<div class='grid_7'>"
  					+"<input type='text' name='ledgerPoorPeople.otherInfo' id='otherInfo'  maxlength='10' value='${ledgerPoorPeople.otherInfo}'  class='form-txt {maxlength:10,minlength:2,messages:{minlength:$.format('至少需要输入{0}个字符'),maxlength:$.format('最多需要输入{0}个字符')}}' />"
	 			  +"</div>"
	 			 +"</div>";
	}
	if(hidden==0){
		$('#otherInfoDiv').show();
	}else{
		$('#otherInfoDiv').hide();
	}
	return oldHtml;
}
function addClearLine(){
	return "<div class='clearLine'>&nbsp;</div>";
}
$("#createTableType option").each(function(){
	if($(this).text().indexOf("上年接转")>-1){
		$(this).hide();
	}
	<s:if test="#getFullOrgById.organization.orgLevel.internalId == @com.tianque.domain.property.OrganizationLevel@DISTRICT">
		if($(this).text().indexOf("县委县政府")>-1){
			$(this).hide();
		}
	</s:if>
	<s:if test="#getFullOrgById.organization.orgLevel.internalId == @com.tianque.domain.property.OrganizationLevel@TOWN">
		if($(this).text().indexOf("上级主管部门")>-1){
			$(this).hide();
		}
	</s:if>
	<s:if test="#getFullOrgById.organization.orgLevel.internalId < @com.tianque.domain.property.OrganizationLevel@TOWN">
		if($(this).text().indexOf("上级主管部门")>-1){
			$(this).hide();
		}
		if($(this).text().indexOf("县")>-1){
			$(this).hide();
		}
	</s:if>
})


function setUnEdit(){
//	if($("#id").val()!=''){
	//	$("#name").attr("readOnly","readOnly");
	//	$("#poorTypes input[type='checkbox']").each(function(){
	//		$(this).attr("disabled","disabled");
	//	})
	//	$("#itemsDiv input[type='checkbox']").each(function(){
	//		$(this).attr("disabled","disabled");
	//	})
	//}
}

</script>