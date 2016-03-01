package com.tianque.plugin.weChat.domain.user;

import com.tianque.core.base.BaseDomain;
import com.tianque.domain.Organization;

/**微信群组类*/
public class WeChatGroup extends BaseDomain {
	/**群组id*/
	private Long weChatGroupId;
	/**群组名称*/
	private String name;
	/**微信公众号id（微信处的id）*/
	private String weChatUserId;
	/**微信方的groupId(weChatUserId+groupId唯一确定一个群组)*/
	private Long groupId;
	
	private Organization org;
	
	private Long countFan;

	public Long getCountFan() {
		return countFan;
	}

	public void setCountFan(Long countFan) {
		this.countFan = countFan;
	}

	public Organization getOrg() {
		return org;
	}

	public void setOrg(Organization org) {
		this.org = org;
	}

	public Long getWeChatGroupId() {
		return weChatGroupId;
	}

	public void setWeChatGroupId(Long weChatGroupId) {
		this.weChatGroupId = weChatGroupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWeChatUserId() {
		return weChatUserId;
	}

	public void setWeChatUserId(String weChatUserId) {
		this.weChatUserId = weChatUserId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
}