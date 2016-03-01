<#assign pop=JspTaglibs["/WEB-INF/taglib/pop-taglib.tld"]>
<#assign s=JspTaglibs["/WEB-INF/taglib/struts-tags.tld"]>
<@s.include value="/includes/baseInclude.jsp"/>
    <div id="otherLocale" class="container container_24">
    <input id="populationId" type="hidden" name="population.populationId" value="${location.id}" />
		<div id=tabs>
			<ul>
				<li><a href="#commonLocal">基本信息</a> </li>
				<@pop.JugePermissionTag ename="serviceTeamMemberManagement">
			<li><a href="/plugin/serviceTeam/router/routerManage/maintainServiceMemberForLocation.action?populationType=OTHERLOCALE&mode=view1&id=${location.id}">治安负责人</a></li>
			</@pop.JugePermissionTag>
				<@pop.JugePermissionTag ename="serviceRecordManagement">
				<li><a href="/plugin/serviceTeam/router/routerManage/maintainServiceRecordForPopulation.action?mode=page&fromSource=population&id=${location.id}&populationType=OTHERLOCALE">巡场情况</a></li>
				</@pop.JugePermissionTag>
			</ul>
			<div id="commonLocal"></div>
   		</div>
  </div>
	<script>
	$(function() {
		$( "#tabs" ).tabs();
		$.ajax({
			url:"${path}/baseinfo/otherLocaleManage/viewOtherLocale.action?otherLocale.id=${location.id}",
			success:function(data){
				$("#commonLocal").html("");
				$("#commonLocal").html(data);
			}
		});	
	});
	</script>