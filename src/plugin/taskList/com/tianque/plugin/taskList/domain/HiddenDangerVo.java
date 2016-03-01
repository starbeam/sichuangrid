package com.tianque.plugin.taskList.domain;

import java.util.Date;

public class HiddenDangerVo extends HiddenDanger {

	/**
	 * 快速查询字段
	 */
	private String fastSearchKeyWords;

	/**
	 * 发现时间起始时间
	 */
	private Date discoverDateStart;

	/**
	 * 发现时间结束时间
	 */
	private Date discoverDateEnd;

	/**
	 * 发现时间起始时间
	 */
	private Date signDateStart;

	/**
	 * 发现时间结束时间
	 */
	private Date signDateEnd;

	/**
	 * 异常类型Id
	 */
	private Long exceptionTypeId;

	/**
	 * 判断是否走网格配置清单查询
	 */
	private String mode;

	/**
	 * 登录的职能部门的网格id
	 */
	private Long funOrgId;
	/**是否有回复*/
	private Integer hasReplay;
	/**有无异常*/
	private Integer hasException;

	public Integer getHasException() {
		return hasException;
	}

	public void setHasException(Integer hasException) {
		this.hasException = hasException;
	}

	public Integer getHasReplay() {
		return hasReplay;
	}

	public void setHasReplay(Integer hasReplay) {
		this.hasReplay = hasReplay;
	}

	public String getFastSearchKeyWords() {
		return fastSearchKeyWords;
	}

	public void setFastSearchKeyWords(String fastSearchKeyWords) {
		this.fastSearchKeyWords = fastSearchKeyWords;
	}

	public Date getDiscoverDateStart() {
		return discoverDateStart;
	}

	public void setDiscoverDateStart(Date discoverDateStart) {
		this.discoverDateStart = discoverDateStart;
	}

	public Date getDiscoverDateEnd() {
		return discoverDateEnd;
	}

	public void setDiscoverDateEnd(Date discoverDateEnd) {
		this.discoverDateEnd = discoverDateEnd;
	}

	public Date getSignDateStart() {
		return signDateStart;
	}

	public void setSignDateStart(Date signDateStart) {
		this.signDateStart = signDateStart;
	}

	public Date getSignDateEnd() {
		return signDateEnd;
	}

	public void setSignDateEnd(Date signDateEnd) {
		this.signDateEnd = signDateEnd;
	}

	public Long getExceptionTypeId() {
		return exceptionTypeId;
	}

	public void setExceptionTypeId(Long exceptionTypeId) {
		this.exceptionTypeId = exceptionTypeId;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Long getFunOrgId() {
		return funOrgId;
	}

	public void setFunOrgId(Long funOrgId) {
		this.funOrgId = funOrgId;
	}

}