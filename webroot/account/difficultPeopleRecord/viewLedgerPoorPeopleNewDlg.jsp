<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pop" uri="/PopGrid-taglib"%>
<%@ include file="/includes/baseInclude.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%
request.setAttribute("peopleAspirations",request.getAttribute("ledgerPoorPeople"));
%>

<style>
.issueCttLeft{width:50%;float:left;}
.issueCttRight{width:49%;float:right;background:none;border:0;}
.processRecord{margin-top:5px;font:bold 12px/20px "";}
h3.processRecord{color:#000;font-weight:bold!important;}
.issueContentInfo{padding:0;border:0;}
.recordContent,.tableBox,.issuePjCtt{background:#fff;border:1px solid #ccc;border-top:0;}
.recordContent{position:relative;}
.issuePjCtt{padding:10px;}
.tableBox{padding:10px;}
.star{display:inline-block;*display:inline;*zoom:1;vertical-align:middle;height:23px;background:url(${resource_path}/resource/system/images/incident/star.png) no-repeat;}
.star.star1{width:20px;}
.star.star2{width:40px;}
.star.star3{width:60px;}
.star.star4{width:80px;}
.star.star5{width:100px;}
.issueC{font:12px/20px "";}
.issueRight .smallText{padding-left:10px;font:bold 25px/27px ""; color:#000;}
.issueRight .recordList .recordData{height:auto;overflow:hidden;}
.issueRight .recordData .place{width: 360px;padding: 0;color:#5F5F5F; font-weight: normal;}
.issueRight .recordData .handle{position:static;float:right;width: 92px;line-height: 27px;padding-right: 0;}
.issueTitle .pmState, .state, .issueRight .recordData .time{background:none;padding-left:0;}
.viewImage {
background: url(${resource_path}/resource/system/images/issue/images_dw_search.gif) no-repeat;
display: inline-block;
width: 72px;
height: 20px;
vertical-align: middle;
}
.ui-state-disabled, .ui-widget-content .ui-state-disabled, .ui-widget-header .ui-state-disabled{
	opacity: 1;
	filter: Alpha(Opacity=100);
	background-image: none;
}
.newTableList{border-top:1px solid #ccc;border-right: 1px solid #ccc;background:#EFEFF0;width:100%;}
.newTableList td{height:24px;line-height:24px;padding:0 0 0 10px;border:1px solid #ccc;border-top:none;border-right:none;border-collapse:collapse;word-break:break-all; word-warp:break-word;}
.newTableList .title{width:28%;font:bold 12px/20px ""; color:#000; padding:0 0 0 5px;}
.newTableList .text{width:25%;}
.newTableList .contable{width:43%;}
</style>
<div class="issueCttLeft" style="height:380px;overflow:hidden;overflow-y:auto;">
	<div class="issueContentInfo">
		<div class="tableBox">
			<table class="newTableList">
				<tbody>
					<tr>
						<td class="title">编号</td><td class="contable">${ledgerPoorPeople.serialNumber}</td>
					</tr>
					<tr>
						<td class="title">建卡类型</td><td class="contable">${ledgerPoorPeople.createTableType.displayName}</td>
					</tr>
					<tr>
						<td class="title">发生网格</td><td class="contable">${ledgerPoorPeople.occurOrg.orgName}</td>
					</tr>
			        <tr class="resolveTheContradictions securityPrecautions emergencies">
						<td class="title">登记时间</td><td class="contable"><fmt:formatDate value="${ledgerPoorPeople.registrationTime}" type="date" pattern="yyyy-MM-dd" /><c:if test="${ not empty ledgerPoorPeople.hours && ledgerPoorPeople.hours!=null && not empty  ledgerPoorPeople.minute && ledgerPoorPeople.minute!=null}">${ledgerPoorPeople.hours }:${ledgerPoorPeople.minute }</c:if></td>
					</tr>
					<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">主要当事人</td><td class="contable">${ledgerPoorPeople.name}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">当事人身份证号</td><td class="contable">${ledgerPoorPeople.idCardNo}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">当事人联系电话</td><td class="contable">${ledgerPoorPeople.mobileNumber}</td>
			       	</tr>
			       	
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">当事人出生年月</td><td class="contable"><s:date name="ledgerPoorPeople.birthDay" format="yyyy年MM月dd日" /></td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">当事人性别</td><td class="contable">${ledgerPoorPeople.gender.displayName}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">当事人职业或身份</td><td class="contable">${ledgerPoorPeople.position.displayName}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">当事人是否户主</td><td class="contable">
			        		<s:if test="ledgerPoorPeople.owner==true">是</s:if>
	          				<s:elseif test="ledgerPoorPeople.owner==false">否</s:elseif>
	          				<s:else>未知</s:else>
			        	</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">当事人户口号</td><td class="contable">${ledgerPoorPeople.accountNo}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">当事人家庭人口</td><td class="contable">${ledgerPoorPeople.memberNo}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">当事人人均年收入</td><td class="contable">${ledgerPoorPeople.annualPerCapitaIncome}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">常住地址</td><td class="contable">${ledgerPoorPeople.permanentAddress}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">户籍地址</td><td class="contable">${ledgerPoorPeople.censusRegisterAddress}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">户籍性质</td><td class="contable">${ledgerPoorPeople.censusRegisterNature}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">是否党员</td><td class="contable">
			        		<s:if test="ledgerPoorPeople.isPartyMember==1">是</s:if>
	          				<s:elseif test="ledgerPoorPeople.isPartyMember==0">否</s:elseif>
	          				<s:else>未知</s:else>
			        	</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">民族</td><td class="contable">${ledgerPoorPeople.national.displayName}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">文化程度</td><td class="contable">${ledgerPoorPeople.levelEducation.displayName}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">婚姻状况</td><td class="contable">${ledgerPoorPeople.maritalStatus.displayName}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">健康状况</td><td class="contable">${ledgerPoorPeople.healthCondition}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">外出及外出原因</td><td class="contable">${ledgerPoorPeople.goOutReason}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">技能特长</td><td class="contable">${ledgerPoorPeople.skillsSpeciality}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">困难程度</td><td class="contable">${ledgerPoorPeople.difficultyDegree}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">关注程度</td><td class="contable">${ledgerPoorPeople.attentionDegree}</td>
			       	</tr>
			       	
			       	
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">需求类型</td><td class="contable">${ledgerPoorPeople.requiredTypeName}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">保障类型</td><td class="contable">${ledgerPoorPeople.securityType.displayName}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">困难类型</td><td class="contable">${ledgerPoorPeople.poorTypeName}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions emergencies otherManage">
			        	<td class="title">困难原因</td><td class="contable">${ledgerPoorPeople.poorSourceName}</td>
			       	</tr>
			       	<tr class="socialConditions  ">
			        	<td class="title">服务联系人</td><td class="contable">${ledgerPoorPeople.serverContractor} ${ledgerPoorPeople.serverTelephone}</td>
			       	</tr>
			   <!--     	<tr class="specialPopulations">
			        	<td class="title">服务联系电话</td><td class="contable">${peopleAspirations.serverTelephone}</td>
			       	</tr>--> 
			       	<tr class="securityPrecautions">
			        	<td class="title">服务职务</td><td class="contable">${ledgerPoorPeople.serverJob}</td>
			       	</tr>
			       	<tr class="contradiction resolveTheContradictions">
			        	<td class="title">服务联系人单位</td><td class="contable">${ledgerPoorPeople.bookingUnit}</td>
			       	</tr>
			       	<tr>
			       		<c:if test="${issueAttachFiles!=null && fn:length(issueAttachFiles) > 0}">
						<p>
							<td class="title"><div class="grid-accessory"><span class="filetype-clip"></span>附件：</div></td>
							<td class="contable">	
							<div id="gallery">
								<c:forEach items="${issueAttachFiles}" var="issueAttachFile">
									<!--<c:choose>
									    <c:when test="${fn:endsWith(fn:toLowerCase(issueAttachFile.fileActualUrl),'.jpg')||fn:endsWith(fn:toLowerCase(issueAttachFile.fileActualUrl),'.jpge')||fn:endsWith(fn:toLowerCase(issueAttachFile.fileActualUrl),'.png')||fn:endsWith(fn:toLowerCase(issueAttachFile.fileActualUrl),'.gif')}">
									     	<a data-gallery="gallery" style="color: red" class="view" href="${issueAttachFile.fileActualUrl }" title="${issueAttachFile.fileName}">${issueAttachFile.fileName}</a>;
			                        		<div class="clear"></div>
									    </c:when>
									    <c:otherwise>
									    	<a href="${path }/threeRecordsIssues/issueManage/downLoadAttachFile.action?keyId=${issueAttachFile.id}" style="color: red">${issueAttachFile.fileName}</a>;
			                        		<div class="clear"></div>
									    </c:otherwise>
									</c:choose>-->
									<a href="${path }/threeRecordsIssues/issueManage/downLoadAttachFile.action?keyId=${issueAttachFile.id}" style="color: red">${issueAttachFile.fileName}</a>;
		                        </c:forEach>
							</div>
							</td>
						</p>
						</c:if>
			       	</tr>
				</tbody>
			</table>
			
			<div><h3 style="color: blue;">家庭成员：</h3></div>
			<c:forEach items="${ledgerPoorPeople.ledgerPoorPeopleMembers}" var="ledgerPoorPeopleMembers" varStatus="remember">
				<table class="newTableList">
				<tbody>
					<tr>
						<td class="title">姓名</td><td class="contable">${ledgerPoorPeopleMembers.name}</td>
					</tr>
					<tr>
						<td class="title">性别</td><td class="contable">${ledgerPoorPeopleMembers.gender.displayName}</td>
					</tr>
					<tr>
						<td class="title">身份证号</td><td class="contable">${ledgerPoorPeopleMembers.idCardNo}</td>
					</tr>
					<tr>
						<td class="title">保障类型</td><td class="contable">${ledgerPoorPeopleMembers.securityType}</td>
					</tr>
					<tr>
						<td class="title">出生年月</td><td class="contable"><fmt:formatDate value="${ledgerPoorPeopleMembers.birthday }" pattern='yyyy-MM-dd'/></td>
					</tr>
					<tr>
						<td class="title">与户主关系</td><td class="contable">${ledgerPoorPeopleMembers.headHouseholdRelation}</td>
					</tr>
					<tr>
						<td class="title">是否失业</td><td class="contable">
							<c:if test="${ledgerPoorPeopleMembers.unemployment == true}">是</c:if>
	          				<c:if test="${ledgerPoorPeopleMembers.unemployment == false}">否</c:if>
						</td>
					</tr>
					<tr>
						<td class="title">健康状况</td><td class="contable">${ledgerPoorPeopleMembers.healthCondition}</td>
					</tr>
					<tr>
						<td class="title">技能特长</td><td class="contable">${ledgerPoorPeopleMembers.skillsSpeciality}</td>
					</tr>
				</tbody>
			</table>
			<p>&nbsp;</p>
			</c:forEach>
	</div>
	</div>
	<!-- <span class="star star1"></span> -->
</div>


<div class="issueRight issueCttRight" style="overflow:hidden;overflow-y:auto;">
	<div class="dealRecord recordList clearfix">
		<h3 class="processRecord">处理记录
			<div class="processBtnList">
				<a href="javascript:;" class="cur" id="textView"><span class="text"></span>文字视图</a>
				<a href="javascript:;" id="chartView"><span class="chart"></span>图表视图</a>
				<a href="javascript:;" id=feedbacks><span class="text"></span>反馈列表</a>
				<a href="javascript:;" id=replys><span class="text"></span>回复列表</a>
			</div>
		</h3>
			<div class="recordContent clearfix">
				<ul style="display:none;" id="issueTable">
					<s:subset source="issueDealLogs" start="0">
						<s:iterator status="index" >
							<li id="2012-12-11">
								<dl class="recordData clearfix">
									<c:if test="${dealType == 5}"><img src='${resource_path }/resource/system/images/issue/icon_yLampForList.png'></img></c:if>
									<dd class="time">${index.index+1}、<fmt:formatDate value="${operateTime}" type="date" pattern="yy年MM月dd日" /></dd>
									<dd class="place" style="word-wrap:break-word;width:350px">
									<c:choose>
									    <c:when test="${dealUserName == 'admin' || dealUserName =='超级管理员'}">
									     	【系统消息】
									    </c:when>
									    <c:otherwise>
									    	【<s:property value="dealOrg.orgName"/>】&nbsp;&nbsp;&nbsp;&nbsp;<s:property value="dealUserName"/>
									    </c:otherwise>
									</c:choose>
									<s:property value="dealDescription"/>
									</br>
									<s:if test="dealType == @com.tianque.plugin.account.state.ThreeRecordsIssueOperate@SUBMIT.code">
										<a class='reportFormExport' name='reportForm' data='${issueStep.id}'>下载呈报单</a>
										<a class='clickAuto'  dataType='呈报单'><span  id='report${issueStep.id}' class="awindowopen" ></span></a>
									</s:if>
									<s:elseif test="dealType == @com.tianque.plugin.account.state.ThreeRecordsIssueOperate@CONCEPT.code && issueStep.isSupported != 1">
										<a class='acceptFormExport' name='acceptForm' data='${issueStep.id}'>下载受理单</a>
										<a class='clickAuto'  dataType='受理单'><span  id='accept${issueStep.id}' class="awindowopen" ></span></a>
									</s:elseif>
									<s:elseif test="dealType == @com.tianque.plugin.account.state.ThreeRecordsIssueOperate@TURN.code">
										<a class='turnFormExport' name='turnForm' data='${issueStep.id}'>下载转办单</a>
										<a class='clickAuto'  dataType='转办单'><span  id='turn${issueStep.id}' class="awindowopen" ></span></a>
									</s:elseif>
									<s:elseif test="dealType == @com.tianque.plugin.account.state.ThreeRecordsIssueOperate@DECLARE.code">
										<a class='declareFormExport' name='declareForm' data='${issueStep.id}'>下载申报单</a>
										<a class='clickAuto'  dataType='申报单'><span  id='declare${issueStep.id}' class="awindowopen" ></span></a>
									</s:elseif>
									<s:elseif test="dealType == @com.tianque.plugin.account.state.ThreeRecordsIssueOperate@ASSIGN.code">
										<c:if test="${fn:endsWith(dealOrg.departmentNo,'1xw')||fn:endsWith(dealOrg.departmentNo,'1lx')}">
											<a class='assignFormExport' name='assignForm' data='${issueStep.id}'>下载交办单</a>
											<a class='clickAuto'  dataType='交办单'><span  id='assign${issueStep.id}' class="awindowopen" ></span></a>
										</c:if>	
										
									</s:elseif>
									</dd>
								</dl>
								<c:if test="${(content != null && not empty content) || (issueLogAttachFilesMap[id] != null && fn:length(issueLogAttachFilesMap[id])>0 )}">
			                        <div class="smallText" style="word-wrap:break-word;overflow:hidden;">
											<span>工作措施:</span><span>${content }</span>
									</div>
			                        <c:if test="${issueLogAttachFiles[id]!=null && fn:length(issueLogAttachFiles[id]) > 0}">
				                    	<div class="smallText" id="gallery<s:property value="id"/>">
											<span class="filetype-clip"></span>附件：
											<c:forEach items="${issueLogAttachFiles[id]}" var="issueAttachFile">
											<!--<c:choose>
											    <c:when test="${fn:endsWith(fn:toLowerCase(issueAttachFile.fileActualUrl),'.jpg')||fn:endsWith(fn:toLowerCase(issueAttachFile.fileActualUrl),'.jpge')||fn:endsWith(fn:toLowerCase(issueAttachFile.fileActualUrl),'.png')||fn:endsWith(fn:toLowerCase(issueAttachFile.fileActualUrl),'.bmp') ||fn:endsWith(fn:toLowerCase(issueAttachFile.fileActualUrl),'.gif')}">
											     	<a data-gallery="gallery"  class="view" href="${issueAttachFile.fileActualUrl }" title="${issueAttachFile.fileName}">${issueAttachFile.fileName}</a>;
					                        		<div class="clear"></div>
											    </c:when>
											    <c:otherwise>
											    	<a href="${path }/threeRecordsIssues/issueManage/downLoadAttachFile.action?keyId=${issueAttachFile.id}" >${issueAttachFile.fileName}</a>;
											    </c:otherwise>
											</c:choose>-->
											<a href="${path }/threeRecordsIssues/issueManage/downLoadAttachFile.action?keyId=${issueAttachFile.id}" >${issueAttachFile.fileName}</a>;
					                        </c:forEach>
			                        	</div>
		                   			</c:if>
	                    		</c:if>
							</li>
						</s:iterator>
					</s:subset>
				</ul>
			<div id="issueGraph" style="padding-top:45px;overflow: auto;width: 405px;height: 320px;">
			    <s:include value="/account/issueMap/raphael.jsp" ></s:include>
		    </div>
		     <s:include value="/account/peopleAspiration/viewFeedBacksDlg.jsp" ></s:include>
		     <s:include value="/account/peopleAspiration/viewReplysDlg.jsp" ></s:include>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		$(".clickAuto").each(function(){
			var dataType=$(this).attr('dataType');
			$(this).click(function () {
				var result = "data:application/vnd.ms-excel,"+encodeURIComponent($(this).html() );
				this.href = result; this.download = dataType+".xls";
				return true;
			});
		})
		$("#issueTable").show();
		$("#issueGraph").hide();
		$(".processBtnList a").click(function(){
			$(this).addClass("cur").siblings().removeClass("cur");
		});
		$("#chartView").click(function(){
			$("#issueGraph").show();
			$("#issueTable").hide();
			$("#feedbackTable").hide();
			$("#replysTable").hide();
		});
		$("#feedbacks").click(function(){
			$("#issueGraph").hide();
			$("#issueTable").hide();
			$("#feedbackTable").show();
			$("#replysTable").hide();
			
		});
		$("#replys").click(function(){
			$("#issueGraph").hide();
			$("#issueTable").hide();
			$("#feedbackTable").hide();
			$("#replysTable").show();
			
		});
		$("#textView").click(function(){
			$("#issueTable").show();
			$("#issueGraph").hide();
			$("#feedbackTable").hide();
			$("#replysTable").hide();
		});
		$('div .show').each(function(){
			if($(this).attr("id")){
			$(this).raty({
				readOnly:	true,
				start:		$(this).attr("id")
		   });
			}
		});
		$('#gallery').imagegallery({
	        show: "random",
	        zIndex:99999,
	        close:function(){
				$('#gallery').imagegallery('disable');
	        }
	    });
		$('#gallery').imagegallery('disable');
	    $("#viewImages").click(function(){
	    	$('#gallery').imagegallery('enable');
	    	if($('#gallery').find(".view").eq(0).html() == null) {
	    		$('#gallery').find("a:first")[0].click();
	    	} else {
	    		$('#gallery').find(".view").eq(0).click();
	    	}
	    });
	    
	});
	$(".reportFormExport").each(function(){
		$(this).click(function(){
			var id = $(this).attr('data');
			var actionName=$(this).attr('name');
			$.ajax({
				url: "${path}/threeRecords/"+actionName+"/dispatch.action?mode=print",
				data:{
					"id":id
				},
				success:function(responseData){
					$('#report'+id).html(responseData);
					$('#report'+id).hide();
					$('#report'+id).trigger("click");
				}
			});
		})
	});

	$(".acceptFormExport").each(function(){
		$(this).click(function(){
			var id = $(this).attr('data');
			var actionName=$(this).attr('name');
			$.ajax({
				url: "${path}/threeRecords/"+actionName+"/dispatch.action?mode=print",
				data:{
					"id":id
				},
				success:function(responseData){
					$('#accept'+id).html(responseData);
					$('#accept'+id).hide();
					$('#accept'+id).trigger("click");
				}
			});
		})
	});
	
	$(".turnFormExport").each(function(){
		$(this).click(function(){
			var id = $(this).attr('data');
			var actionName=$(this).attr('name');
			$.ajax({
				url: "${path}/threeRecords/"+actionName+"/dispatch.action?mode=print",
				data:{
					"id":id
				},
				success:function(responseData){
					$('#turn'+id).html(responseData);
					$('#turn'+id).hide();
					$('#turn'+id).trigger("click");
				}
			});
		})
	});
	
	$(".declareFormExport").each(function(){
		$(this).click(function(){
			var id = $(this).attr('data');
			var actionName=$(this).attr('name');
			$.ajax({
				url: "${path}/threeRecords/"+actionName+"/dispatch.action?mode=print",
				data:{
					"id":id
				},
				success:function(responseData){
					$('#declare'+id).html(responseData);
					$('#declare'+id).hide();
					$('#declare'+id).trigger("click");
				}
			});
		})
	});
	
	$(".assignFormExport").each(function(){
		$(this).click(function(){
			var id = $(this).attr('data');
			var actionName=$(this).attr('name');
			$.ajax({
				url: "${path}/threeRecords/"+actionName+"/dispatch.action?mode=print",
				data:{
					"id":id
				},
				success:function(responseData){
					$('#assign'+id).html(responseData);
					$('#assign'+id).hide();
					$('#assign'+id).trigger("click");
				}
			});
		})
	});
</script>