package com.tianque.statAnalyse.issueManage.listManage.dao.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tianque.dao.impl.BaseDaoImpl;
import com.tianque.domain.Organization;
import com.tianque.issue.constants.IssueTypes;
import com.tianque.statAnalyse.issueManage.listManage.constant.QueryType;
import com.tianque.statAnalyse.issueManage.listManage.dao.IssueAreaStatDao;
import com.tianque.statAnalyse.issueManage.listManage.domain.IssueAreaStat;

@Repository("issueAreaStatDao")
public class IssueAreaStatDaoImpl extends BaseDaoImpl<IssueAreaStat> implements
		IssueAreaStatDao {

	@Override
	public IssueAreaStat getIssueAreaStatsByYearAndMonthAndOrgCode(
			Date startDate, Date endDate, Organization organization,
			Integer queryType) {
		return get(buildQueryMap(startDate, endDate, organization, queryType),
				QueryType.STATMENT.get(queryType));
	}
	
	/**
	 * 各区域办理-历史job 历史表
	 */
	@Override
	public IssueAreaStat getIssueAreaStatsByYearAndMonthAndOrgId_history(Date startDate, Date endDate,Long orgId){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		map.put("orgId", orgId);
		return (IssueAreaStat)getSqlMapClientTemplate().queryForObject("issueAreaStat.getIssueAreaStatsByYearAndMonthAndOrgId_history",map);
	}

	/**
	 * 各区域分类-历史job 历史表
	 */
	@Override
	public IssueAreaStat getIssueAreaTypeStatsByYearAndMonthAndOrgId_history(Date startDate, Date endDate,Long orgId){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		map.put("orgId", orgId);
		map.put("contradiction", IssueTypes.PEOPLELIVE_SERVICE);
		map.put("resolveTheContradiction",
				IssueTypes.RESOLVETHECONTRADICTIONS);
		map.put("securityPrecaution", IssueTypes.SECURITYPRECAUTIONS);
		map.put("specialPopulation", IssueTypes.SPECIALPOPULATIONS);
		map.put("socialCondition", IssueTypes.SOCIALCONDITIONS);
		map.put("policiesAndLaw", IssueTypes.POLICIESANDLAWS);
		map.put("emergencie", IssueTypes.EMERGENCIES);
		map.put("otherManage", IssueTypes.OTHERMANAGE);
		return (IssueAreaStat)getSqlMapClientTemplate().queryForObject("issueAreaStat.getIssueAreaTypeStatsByYearAndMonthAndOrgId_history",map);
	}
	
	/**
	 * 按流程统计-历史job 历史表
	 */
	@Override
	public IssueAreaStat getIssueStepStatsByYearAndMonthAndOrgId_history(Date startDate, Date endDate,Long orgId){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		map.put("orgId", orgId);
		map.put("resolveTheContradiction",
				IssueTypes.RESOLVETHECONTRADICTIONS);
		map.put("securityPrecaution", IssueTypes.SECURITYPRECAUTIONS);
		map.put("emergencie", IssueTypes.EMERGENCIES);
		map.put("otherManage", IssueTypes.OTHERMANAGE);
		return (IssueAreaStat)getSqlMapClientTemplate().queryForObject("issueAreaStat.getIssueStepStatsByYearAndMonthAndOrgId_history",map);
	}
	private Map<String, Object> buildQueryMap(Date startDate, Date endDate,
			Organization organization, Integer queryType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		Calendar startTime = Calendar.getInstance();
		Calendar endTime = Calendar.getInstance();
		startTime.setTime(startDate);
		endTime.setTime(endDate);
		//按年查询
		if(endTime.get(Calendar.YEAR) - startTime.get(Calendar.YEAR)>0 && endTime.get(Calendar.MONTH) == startTime.get(Calendar.MONTH)){
			map.put("monthNum", -12);
		}//按月查询
		else if(endTime.get(Calendar.MONTH) - startTime.get(Calendar.MONTH) ==1 || endTime.get(Calendar.MONTH) - startTime.get(Calendar.MONTH) ==-11){
			map.put("monthNum", -1);
		}//按季度查询
		else{
			map.put("monthNum", -3);
		}
		if (QueryType.HISTORY.contains(queryType)) {
			map.put("orgId", organization.getId());
		} else {
			map.put("orgInternalCode", organization.getOrgInternalCode());
		}

		if (queryType.intValue() == QueryType.AREA_CLASSIFICATION
				|| queryType.intValue() == QueryType.AREA_CLASSIFICATION_HISTORY) {
			map.put("contradiction", IssueTypes.PEOPLELIVE_SERVICE);
			map.put("resolveTheContradiction",
					IssueTypes.RESOLVETHECONTRADICTIONS);
			map.put("securityPrecaution", IssueTypes.SECURITYPRECAUTIONS);
			map.put("specialPopulation", IssueTypes.SPECIALPOPULATIONS);
			map.put("socialCondition", IssueTypes.SOCIALCONDITIONS);
			map.put("policiesAndLaw", IssueTypes.POLICIESANDLAWS);
			map.put("emergencie", IssueTypes.EMERGENCIES);
			map.put("otherManage", IssueTypes.OTHERMANAGE);
		}

		if (queryType.intValue() == QueryType.STEP
				|| queryType.intValue() == QueryType.STEP_HISTORY) {
			map.put("resolveTheContradiction",
					IssueTypes.RESOLVETHECONTRADICTIONS);
			map.put("securityPrecaution", IssueTypes.SECURITYPRECAUTIONS);
			map.put("emergencie", IssueTypes.EMERGENCIES);
			map.put("otherManage", IssueTypes.OTHERMANAGE);
		}
		return map;
	}

	@Override
	public IssueAreaStat getHistoryIssueAreaStatsByYearAndMonthAndOrgCode(
			Date startDate, Date endDate, String orgInternalCode,
			Integer queryType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		map.put("orgInternalCode", orgInternalCode);
		Calendar startTime = Calendar.getInstance();
		Calendar endTime = Calendar.getInstance();
		startTime.setTime(startDate);
		endTime.setTime(endDate);
		//按年查询
		if(endTime.get(Calendar.MONTH) == startTime.get(Calendar.MONTH)){
			map.put("monthNum", -12);
		}//按月查询
		else if(endTime.get(Calendar.MONTH) - startTime.get(Calendar.MONTH) ==1 || endTime.get(Calendar.MONTH) - startTime.get(Calendar.MONTH) ==-11){
			map.put("monthNum", -1);
		}//按季度查询
		else{
			map.put("monthNum", -3);
		}
		return get(map, QueryType.STATMENT.get(queryType) + "History");
	}

	@Override
	public IssueAreaStat getHistoryIssueAreaStatsByYearAndMonthAndOrgCodeNew(
			Date startDate, Date endDate, String orgInternalCode,
			Integer queryType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		map.put("orgInternalCode", orgInternalCode);
		return get(map, "getIssueAreaStatsByYearAndMonthAndOrgCodeHistory");
	}
}