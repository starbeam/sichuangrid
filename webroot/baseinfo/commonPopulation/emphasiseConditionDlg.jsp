<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ include file="/includes/baseInclude.jsp"%>
<%@ taglib uri="/PopGrid-taglib" prefix="pop"%>
 <% String dailogName = request.getParameter("dailogName");
	request.setAttribute("dailogName", dailogName);

 	request.setAttribute("type", request.getParameter("type"));
	request.setAttribute("temp",request.getParameter("temp"));
	%>

<div id="emphasis-form" class="container container_24">
   	<form id="emphasisForm" method="post">
   	<pop:token />
   	    <input type="hidden" name="dailogName" id="dailogName" value="<%=request.getParameter("dailogName")%>" />
   		<input type="hidden" name="locationIds" id="locationIds" value="<%=request.getParameter("locationIds")%>" />
   		<input type="hidden" name="location.isEmphasis" id="isEmphasisValue" value="<%=request.getParameter("isEmphasis")%>" />
   		<input type="hidden" name="location.logOutTime" id="logOutTimeValue" value="" />
   		<input type="hidden" name="orgId" id="orgId" value="<%=request.getParameter("orgId")%>" />
   		
	<s:if test="#attr.type.equals('attention')">
	<div class='clearLine'></div>
     <div class="grid_7 lable-right">
         <label class="form-lbl">取消关注时间：</label>
     </div>
     <div class="grid_17">
         <input type="text"  id="logOutTime"  disabled="disabled" class="dialogtip form-txt"
        maxlength="20" />
     </div>
     	<div class='clearLine'></div>
    <div class="grid_7 lable-right">
    		<em class="form-req">*</em>
         <label class="form-lbl">取消关注原因：</label>
     </div>

     <div class="grid_17">
			<input type="text" name="location.logOutReason" id="logOutReason"     maxlength="20"
			class="form-txt {required:true,maxlength:20,messages:{required:'请输入取消关注原因',maxlength:$.format('取消关注原因最多只能输入{0}个字符')}}"  />
     </div>
	</s:if>
    <s:else>

<!--	<div class="grid_5 lable-right">-->
<!--		<label class="form-lbl">取消关注标志：</label>-->
<!--	</div>-->
<!--	<div class="grid_4">-->
<!--		<input type="text" id="isEmphasis"  name="location.isEmphasis" value="注销" disabled="disabled" class="form-txt" maxlength="20"/>-->
<!--	</div>-->


     <div class="grid_7 lable-right">
         <label class="form-lbl">&nbsp;&nbsp;取消关注时间：</label>
     </div>
     <div class="grid_17">
         <input type="text" name="logOutTime" id="logOutTime"  disabled="disabled" class="dialogtip form-txt"
        maxlength="20" />
     </div>
     	<div class='clearLine'></div>
    <div class="grid_7 lable-right">
    		<em class="form-req">*</em>
         <label class="form-lbl">取消关注原因：</label>
     </div>

     <div class="grid_17">
			<input type="text" name="location.logOutReason" id="logOutReason" style="width:99%" value="${location.logOutReason}" maxlength="20"
			class="form-txt {required:true,maxlength:20,messages:{required:'请输入注销原因',maxlength:$.format('备注最多只能输入{0}个字符')}}"  />
     </div>
     </s:else>
	</form>
</div>
<script type="text/javascript">


$(function(){
    //if($("#dailogName").val()=='school'){
      //  $("#emphasisForm").attr("action","${path}/school/schoolManage/updateEmphasiseById.action" );
    //}else{
        $("#emphasisForm").attr("action","${path}/baseinfo/${dailogName}Manage/updateEmphasiseById.action" );
  //  }

	var t=document.getElementById("logOutTime");
	d=new Date();
	t.value=d.getFullYear()+"-"+(d.getMonth()+1)+"-"+d.getDate();

	t.onfocus=function(){
	 var d=new Date();
	 t.value=d.getHours()+":"+d.getMinutes()
	}
	$("#logOutTimeValue").val($("#logOutTime").val());
	$("#emphasisForm").formValidate({
		promptPosition: "bottomLeft",
		submitHandler: function(form){
			$(form).ajaxSubmit({
				success:function(data){
					if(data=="您输入了非法脚本，请重新输入！"){
						$.messageBox({
							message:data,
							level: "error"
						});
	        			return;
					}
					if(null=="${temp}" || ""=="${temp}"){
						 $.messageBox({message:document.title+"取消关注成功"});
					}else{
						 notExecute="${temp}".split(",");
						 $.messageBox({level:'warn',message:"除选中的红色数据外,其余"+document.title+"取消关注成功"});
					}
					$("#${dailogName}List").trigger("reloadGrid");
					 $("#${dailogName}Dialog").dialog("close");
					 doAction("<s:property value='#parameters.dailogName[0]'/>",data.id);
				},
				error:function(XMLHttpRequest, textStatus, errorThrown){
		      			alert("提交数据时发生错误");
	   		    }
			});
		},
		rules:{},
		messages:{}
	});

})



</script>
