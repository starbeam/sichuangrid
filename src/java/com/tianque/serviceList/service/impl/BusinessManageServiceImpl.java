/**
 * 
 */
package com.tianque.serviceList.service.impl;


import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tinygroup.commons.tools.StringUtil;

import com.tianque.controller.ControllerHelper;
import com.tianque.core.systemLog.util.ModuleConstants;
import com.tianque.core.util.ThreadVariable;
import com.tianque.core.vo.PageInfo;
import com.tianque.domain.Organization;
import com.tianque.domain.PropertyDict;
import com.tianque.domain.User;
import com.tianque.domain.property.OrganizationType;
import com.tianque.domain.property.PropertyTypes;
import com.tianque.exception.base.BusinessValidationException;
import com.tianque.exception.base.ServiceValidationException;
import com.tianque.plugin.taskList.constants.Constants;
import com.tianque.plugin.taskList.service.GridConfigTaskService;
import com.tianque.serviceList.dao.BusinessManageDao;
import com.tianque.serviceList.domain.BusinessManage;
import com.tianque.serviceList.domain.DrugsSafty;
import com.tianque.serviceList.domain.PyramidSalesManage;
import com.tianque.serviceList.domain.Reply;
import com.tianque.serviceList.domain.ServiceListEnum;
import com.tianque.serviceList.service.BusinessManageService;
import com.tianque.serviceList.service.ReplyAttachService;
import com.tianque.serviceList.service.ReplyService;
import com.tianque.serviceList.service.ServiceListAttachService;
import com.tianque.serviceList.validater.BusinessManageValidatorImpl;
import com.tianque.sysadmin.service.OrganizationDubboService;
import com.tianque.sysadmin.service.PropertyDictService;
import com.tianque.userAuth.api.PermissionDubboService;

@Service("businessManageServiceImpl")
@Transactional
public class BusinessManageServiceImpl implements BusinessManageService{
	@Autowired 
	private BusinessManageDao businessManageDao;
	@Autowired
	private OrganizationDubboService organizationDubboService;
	@Autowired
	private ServiceListAttachService attachService;
	@Autowired
	private ReplyAttachService replyAttachService;
	@Autowired
	private ReplyService replyService;
	@Autowired
	private BusinessManageValidatorImpl validatorImpl;
	@Autowired
	private PropertyDictService propertyDictService;
	@Autowired 
	private GridConfigTaskService configTaskService;
	@Autowired
	private PermissionDubboService permissionDubboService;
	
	private static final Integer TYPE=ServiceListEnum.getValue("bussiness");
	@Override
	public BusinessManage addBusinessManage(BusinessManage info) {
		if (info == null) {
			throw new BusinessValidationException("新增失败！");
		}
		try {
			fillBusinessManageOrg(info);
			info.setTelephone(ThreadVariable.getUser().getMobile());
			String[] attachFileNames = info.getAttachFileNames();
			if(info.getIsSign()==null){
				info.setIsSign(0);
			}
			if(info.getIsSign()==null){
				info.setIsReply(0);
			}
			validatorImpl.validatorBusinessManage(info);
			info= businessManageDao.add(info);
			attachService.addServiceListAttch(attachFileNames, info.getId(),TYPE);
			return info;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceValidationException("BusinessManageServiceImpl的addBusinessManage方法出错，原因：",
					"新增信息不全", e);
		}
	}
	
	@Override
	public PageInfo<BusinessManage> getBusinessManageListByQuery(
			BusinessManage businessManage, Integer page, Integer rows,
			String sidx, String sord) {
		if(businessManage==null){
			businessManage=new BusinessManage();
		}
		PropertyDict orgTypeDict = propertyDictService
				.findPropertyDictByDomainNameAndDictDisplayName(
						PropertyTypes.ORGANIZATION_TYPE,
						OrganizationType.FUNCTION_KEY);
		fillBusinessManageOrg(businessManage);
		if(isGridConfigTaskSearch(businessManage)){
			if(!StringUtil.isEmpty(businessManage.getMode())&&
					"true".equals(businessManage.getMode())){
				businessManage.setFunOrgId(businessManage.getOrganization().getId());
				businessManage.setMode("gridConfigService");
			}
			businessManage.getOrganization().setOrgInternalCode(null);
		}else if(orgTypeDict.getId().equals(businessManage.getOrganization().getOrgType().getId())){
			businessManage.getOrganization().setOrgInternalCode(organizationDubboService
						.getOrgInternalCodeById(businessManage.getOrganization().getParentOrg().getId()));
		}else{
			businessManage.getOrganization().setOrgInternalCode(businessManage.getOrganization().getOrgInternalCode());
		}
		PageInfo<BusinessManage> infos=businessManageDao.findPagerBySearchVo(businessManage, page, rows, sidx, sord);
		//设置督办时间
//		infos=supervise(infos);
		return infos;
	}
	//设置督办时间
	private PageInfo<BusinessManage> supervise(PageInfo<BusinessManage> infos ){
		List<BusinessManage> businessManages= infos.getResult();
		for (BusinessManage businessManage:businessManages) {
//			propaganda.setSupervise(5);
		}
		return infos;
	}
	public void fillBusinessManageOrg(BusinessManage businessManage) {
		if (businessManage.getOrganization().getId() == null) {
			throw new BusinessValidationException("组织结构获取异常！");
		}
		Organization organization = organizationDubboService.getFullOrgById(businessManage.getOrganization()
				.getId());
		String[] orgs = organization.getFullOrgName().split(ModuleConstants.SEPARATOR);
		if (orgs.length > 2) {
			String org = orgs[orgs.length - 2] + ModuleConstants.SEPARATOR + orgs[orgs.length - 1];
			organization.setFullOrgName(org);
		}
		businessManage.setOrganization(organization);
	}
	
	@Override
	public BusinessManage updateBusinessManage(
			BusinessManage businessManage) {
		fillBusinessManageOrg(businessManage);
		validatorImpl.validatorBusinessManage(businessManage);
		String[] attachIds=businessManage.getDeleteAttachIds().split(",");
		for (int i = 0; i < attachIds.length; i++) {
			if(!attachIds[i].equals("")){
				attachService.deleteServiceListAttch(Long.parseLong(attachIds[i]));
			}
		}
		attachService.addServiceListAttch(businessManage.getAttachFileNames(), businessManage.getId(),TYPE);
		return businessManageDao.update(businessManage);
	}

	@Override
	public void deleteBusinessManageByIds(String ids) {
		String[]id= ids.split(",");
		for (String objId:id){
			List<Reply>replies=replyService.getReplyByIdAndType(Long.parseLong(objId), TYPE);
			for (Reply reply:replies) {
				replyAttachService.deleteReplyAttchByIds(reply.getId(), TYPE);
			}
			replyService.deleteReplyByIds(Long.parseLong(objId), TYPE);
			businessManageDao.delete(Long.parseLong(objId));
			attachService.deleteServiceListAttchByIds(Long.parseLong(objId),TYPE);
		}
	}

	@Override
	public BusinessManage getBusinessManageById(Long id) {
		BusinessManage businessManage=businessManageDao.get(id);
		if(businessManage!=null){
			businessManage.setPhotos(attachService.getServiceListAttchByIdAndType(id, TYPE));
			User user = permissionDubboService.getFullUserByUerName(businessManage.getCreateUser());
			if(user!=null){
				businessManage.setCreateUser(user.getName());
			}
		}
		fillOrganization(businessManage);
		return businessManage;
	}
	
	public void fillOrganization(BusinessManage businessManage) {
		if (businessManage.getOrganization().getId() == null) {
			throw new BusinessValidationException("组织结构获取异常！");
		}
		Organization organization = organizationDubboService.getFullOrgById(businessManage
				.getOrganization().getId());
		organization = ControllerHelper.proccessRelativeOrgNameByOrg(organization, organizationDubboService);
		businessManage.setOrganization(organization);
	}

	@Override
	public BusinessManage signBusinessManage(
			BusinessManage businessManage) {
		BusinessManage fSafty=businessManageDao.get(businessManage.getId());
		fSafty.setUpdateDate(new Date());
		fSafty.setUpdateUser(businessManage.getSignPeople());
		fSafty.setSignContent(businessManage.getSignContent());
		fSafty.setSignDate(businessManage.getSignDate());
		fSafty.setSignPeople(businessManage.getSignPeople());
		fSafty.setIsSign(1);
		return businessManageDao.update(fSafty);
	}

	@Override
	public BusinessManage replyBusinessManage(
			BusinessManage businessManage) {
		BusinessManage fSafty=businessManageDao.get(businessManage.getId());
		fSafty.setIsReply(1);
		businessManageDao.update(fSafty);
		Reply reply=businessManage.getReply();
		reply.setServiceId(businessManage.getId());
		reply.setServiceType(TYPE);
		reply=replyService.addReply(reply);
		replyAttachService.addReplyAttch(businessManage.getAttachFileNames(), reply.getId(),TYPE);
		return businessManage;
	}
	
	//判断是否为网格配置后的查询
			private boolean isGridConfigTaskSearch(BusinessManage businessManage){
				Long funId=businessManage.getOrganization().getId();
				if(businessManage.getMode()==null){
					return false;
				}
				if(businessManage.getMode().equals("gridConfigService")&&
						funId.equals(ThreadVariable.getOrganization().getId())){
					return true;
				}
				if(businessManage.getMode().equals("true")&&configTaskService.isHasGridTaskList(funId,Constants.TASKLIST_KEY)){
					return true;
				}
				return false;
			}
}