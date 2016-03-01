package com.tianque.plugin.taskList.serviceImpl;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tinygroup.commons.tools.StringUtil;

import com.tianque.core.systemLog.util.ModuleConstants;
import com.tianque.core.util.DialogMode;
import com.tianque.core.util.FileUtil;
import com.tianque.core.util.GridProperties;
import com.tianque.core.util.StoredFile;
import com.tianque.core.util.ThreadVariable;
import com.tianque.core.validate.ValidateResult;
import com.tianque.core.vo.PageInfo;
import com.tianque.domain.Organization;
import com.tianque.domain.PropertyDict;
import com.tianque.domain.User;
import com.tianque.domain.property.OrganizationType;
import com.tianque.domain.property.PropertyTypes;
import com.tianque.exception.base.BusinessValidationException;
import com.tianque.exception.base.ServiceValidationException;
import com.tianque.plugin.taskList.constants.Constants;
import com.tianque.plugin.taskList.dao.PositiveInfoRecordDao;
import com.tianque.plugin.taskList.domain.PositiveInfoRecord;
import com.tianque.plugin.taskList.domain.PositiveInfoRecordVo;
import com.tianque.plugin.taskList.domain.TaskListAttachFile;
import com.tianque.plugin.taskList.service.GridConfigTaskService;
import com.tianque.plugin.taskList.service.PositiveInfoRecordService;
import com.tianque.plugin.taskList.service.TaskListAttachFileService;
import com.tianque.plugin.taskList.validate.PositiveInfoRecordValidatorImpl;
import com.tianque.sysadmin.service.OrganizationDubboService;
import com.tianque.sysadmin.service.PropertyDictService;
import com.tianque.userAuth.api.PermissionDubboService;
import com.tianque.util.IdCardUtil;

@Transactional
@Service("positiveInfoRecordService")
public class PositiveInfoRecordServiceImpl implements PositiveInfoRecordService {
	@Autowired
	private PositiveInfoRecordDao positiveInfoRecordDao;
	@Autowired
	private PositiveInfoRecordValidatorImpl positiveInfoRecordValidator;
	@Autowired
	private OrganizationDubboService organizationDubboService;
	@Autowired
	private TaskListAttachFileService taskListAttachFileService;
	@Autowired
	private PropertyDictService propertyDictService;
	@Autowired
	private GridConfigTaskService configTaskService;
	@Autowired
	private PermissionDubboService permissionDubboService;
	
	@Override
	public PositiveInfoRecord addPositiveInfoRecord(PositiveInfoRecord positiveInfoRecord) {
		if (positiveInfoRecord == null) {
			throw new BusinessValidationException("刑释人员记录信息为空，新增记录失败！");
		}
		if (positiveInfoRecord.getHasException() == null) {
			throw new BusinessValidationException("有无异常不能为空");
		}
		if (positiveInfoRecord.getHasException() == 1
				&& StringUtils.isBlank(positiveInfoRecord.getExceptionSituationInfo())) {
			throw new BusinessValidationException("有异常时，异常情况不能为空");
		}
		positiveInfoRecordValidator(positiveInfoRecord);
		try {
			fillPositiveInfoRecord(positiveInfoRecord, DialogMode.ADD_MODE);
			//把帮扶人员的id和-去掉,例：23-asdfasf,22-sdfsdf变成asdfasf,sdfsdf
			if(com.tianque.core.util.StringUtil.isStringAvaliable(positiveInfoRecord.getHelpPeople())){
				fillDruggyHelpPeople(positiveInfoRecord);
			}
			String[] attachFileNames = positiveInfoRecord.getAttachFileNames();
			PositiveInfoRecord positiveInfoRecordTemp = positiveInfoRecordDao
					.addPositiveInfoRecord(positiveInfoRecord);
			addAttachFile(attachFileNames, positiveInfoRecordTemp.getId());
			return positiveInfoRecordDao.getPositiveInfoRecordById(positiveInfoRecord.getId());
		} catch (Exception e) {
			throw new ServiceValidationException(
					"类PositiveInfoRecordServiceImpl的addPositiveInfoRecord方法出现异常，原因：",
					"刑释人员记录信息新增出现错误", e);
		}
	}
	//把帮扶人员的id和-去掉
	private void fillDruggyHelpPeople(PositiveInfoRecord positiveInfoRecord){
		String helpPeople=positiveInfoRecord.getHelpPeople();
		//手机新增是没有-的，那边是直接传拼好的，直接返回即可
		if(helpPeople.indexOf("-")<=0){
			return;
		}
		if(helpPeople.indexOf(",")<=0){
			positiveInfoRecord.setHelpPeople(helpPeople.split("-")[1]);
			return;
		}
		String[] array=helpPeople.split(",");
		String str="";
		for(int i=0;i<array.length;i++){
			str=str+","+array[i].split("-")[1];
		}
		positiveInfoRecord.setHelpPeople(str.substring(1));
	}
	@Override
	public PositiveInfoRecord getPositiveInfoRecordById(Long id) {
		if (id == null) {
			throw new BusinessValidationException("参数为空，获取刑释记录信息失败！");
		}
		try {
			PositiveInfoRecord positiveInfoRecord = positiveInfoRecordDao
					.getPositiveInfoRecordById(id);
			fillPositiveInfoRecord(positiveInfoRecord, DialogMode.VIEW_MODE);
			positiveInfoRecord.setTaskListAttachFiles(taskListAttachFileService
					.queryTaskListAttachFilesByBusinessId(id, Constants.POSITIVEINFORECORD_KEY));
			//判断权限，有权限不隐藏
			if(permissionDubboService.
					isUserHasPermission(ThreadVariable.getUser().getId(), "isPositiveInfoRecordNotHidCard")){
				return positiveInfoRecord;
			}
			positiveInfoRecord.setIdCard(IdCardUtil.hiddenIdCard(positiveInfoRecord.getIdCard()));
			return positiveInfoRecord;
		} catch (Exception e) {
			throw new ServiceValidationException(
					"类PositiveInfoRecordServiceImpl的getPositiveInfoRecordById方法出现异常，原因：",
					"获取刑释人员记录信息出现错误", e);
		}
	}

	@Override
	public PageInfo<PositiveInfoRecord> findPositiveInfoRecords(
			PositiveInfoRecordVo positiveInfoRecordVo, Integer pageNum, Integer pageSize,
			String sidx, String sord) {
		if (positiveInfoRecordVo == null || pageNum == null || pageSize == null
				|| positiveInfoRecordVo.getOrganization() == null
				|| positiveInfoRecordVo.getOrganization().getId() == null) {
			throw new BusinessValidationException("参数为空，获取刑释人员记录信息失败！");
		}
		try {

			PropertyDict orgTypeDict = propertyDictService
					.findPropertyDictByDomainNameAndDictDisplayName(
							PropertyTypes.ORGANIZATION_TYPE, OrganizationType.FUNCTION_KEY);
			Organization org = organizationDubboService.getFullOrgById(positiveInfoRecordVo
					.getOrganization().getId());
			fillSearchArgs(positiveInfoRecordVo);
			if (isGridConfigTaskSearch(positiveInfoRecordVo)) {
				if (!StringUtil.isEmpty(positiveInfoRecordVo.getMode())
						&& "true".equals(positiveInfoRecordVo.getMode())) {
					positiveInfoRecordVo.setFunOrgId(org.getId());
					positiveInfoRecordVo.setMode("gridConfigTask");
				}
				positiveInfoRecordVo.setMode("gridConfigTask");
				positiveInfoRecordVo.getOrganization().setOrgInternalCode(null);
			} else if (orgTypeDict.getId().equals(org.getOrgType().getId())) {
				positiveInfoRecordVo.getOrganization()
						.setOrgInternalCode(
								organizationDubboService.getOrgInternalCodeById(org.getParentOrg()
										.getId()));
			} else {
				positiveInfoRecordVo.getOrganization().setOrgInternalCode(org.getOrgInternalCode());
			}
			PageInfo<PositiveInfoRecord> pageInfo = positiveInfoRecordDao.findPositiveInfoRecords(positiveInfoRecordVo, pageNum,
					pageSize, sidx, sord);
			//隐藏身份证中间4位
			pageInfo=hiddenIdCard(pageInfo);
			return pageInfo;

		} catch (Exception e) {
			throw new ServiceValidationException(
					"类PositiveInfoRecordServiceImpl的findPositiveInfoRecords方法出现异常，原因：",
					"获取刑释人员记录信息出现错误", e);
		}
	}
	
	//隐藏身份证中间4位
		private PageInfo<PositiveInfoRecord> hiddenIdCard(PageInfo<PositiveInfoRecord> pageInfo){
			//判断权限，有权限不隐藏
			if(permissionDubboService.
					isUserHasPermission(ThreadVariable.getUser().getId(), "isPositiveInfoRecordNotHidCard")){
				return pageInfo;
			}
			List<PositiveInfoRecord> list = pageInfo.getResult();
			int index=0;
			for (PositiveInfoRecord verification:list) {
				verification.setIdCard(IdCardUtil.hiddenIdCard(verification.getIdCard()));
				list.set(index, verification);
				index++;
			}
			pageInfo.setResult(list);
			return pageInfo;
	}

	//判断是否为网格配置后的查询
	private boolean isGridConfigTaskSearch(PositiveInfoRecordVo positiveInfoRecordVo) {
		Long funId = positiveInfoRecordVo.getOrganization().getId();
		if (positiveInfoRecordVo.getMode() == null) {
			return false;
		}
		if (positiveInfoRecordVo.getMode().equals("gridConfigTask")
				&& funId.equals(ThreadVariable.getOrganization().getId())) {
			return true;
		}
		if (positiveInfoRecordVo.getMode().equals("true")
				&& configTaskService.isHasGridTaskList(funId,Constants.TASKLIST_KEY)) {
			return true;
		}
		return false;
	}

	@Override
	public PositiveInfoRecord updatePositiveInfoRecord(PositiveInfoRecord positiveInfoRecord) {
		if (positiveInfoRecord == null) {
			throw new BusinessValidationException("参数为空，获取刑释人员记录信息失败！");
		}
		positiveInfoRecordValidator(positiveInfoRecord);
		try {
			String[] attachFileNames = positiveInfoRecord.getAttachFileNames();
			positiveInfoRecord = positiveInfoRecordDao.updatePositiveInfoRecord(positiveInfoRecord);
			updateAttachFile(attachFileNames, positiveInfoRecord.getId());
			return positiveInfoRecordDao.getPositiveInfoRecordById(positiveInfoRecord.getId());
		} catch (Exception e) {
			throw new ServiceValidationException(
					"类PositiveInfoRecordServiceImpl的updatePositiveInfoRecord方法出现异常，原因：",
					"更新刑释人员记录信息出现错误", e);
		}
	}

	@Override
	public Integer deletePositiveInfoRecords(List<Long> ids) {
		if (ids == null || ids.size() < 1) {
			throw new BusinessValidationException("参数为空，获取刑释人员记录信息失败！");
		}
		try {
			taskListAttachFileService.deleteTaskListAttachFilesByBusinessIds(ids,
					Constants.POSITIVEINFORECORD_KEY);
			return positiveInfoRecordDao.deletePositiveInfoRecords(ids);
		} catch (Exception e) {
			throw new ServiceValidationException(
					"类PositiveInfoRecordServiceImpl的deletePositiveInfoRecord方法出现异常，原因：",
					"删除刑释人员记录信息出现错误", e);
		}
	}

	@Override
	public List<PositiveInfoRecord> findPositiveInfoRecordsByName(
			PositiveInfoRecordVo positiveInfoRecordVo) {
		if (positiveInfoRecordVo == null || StringUtil.isEmpty(positiveInfoRecordVo.getName())
				|| positiveInfoRecordVo.getOrganization() == null
				|| positiveInfoRecordVo.getOrganization().getId() == null) {
			throw new BusinessValidationException("参数为空，获取刑释人员记录信息失败！");
		}
		try {
			fillSearchArgs(positiveInfoRecordVo);
			return positiveInfoRecordDao.findPositiveInfoRecordsByName(positiveInfoRecordVo);
		} catch (Exception e) {
			throw new ServiceValidationException(
					"类PositiveInfoRecordServiceImpl的findPositiveInfoRecordsByName方法出现异常，原因：",
					"获取刑释人员记录信息出现错误", e);
		}
	}

	private void positiveInfoRecordValidator(PositiveInfoRecord positiveInfoRecord) {
		ValidateResult dataValidateResult = positiveInfoRecordValidator
				.validate(positiveInfoRecord);
		if (dataValidateResult.hasError()) {
			throw new BusinessValidationException(dataValidateResult.getErrorMessages());
		}
	}

	private void fillSearchArgs(PositiveInfoRecordVo positiveInfoRecordVo) {
		Organization organization = organizationDubboService.getSimpleOrgById(positiveInfoRecordVo
				.getOrganization().getId());
		positiveInfoRecordVo.setOrganization(organization);
	}

	private void fillPositiveInfoRecord(PositiveInfoRecord positiveInfoRecord, String type) {
		Organization organization = organizationDubboService.getFullOrgById(positiveInfoRecord
				.getOrganization().getId());
		String fullOrgName = "";
		if (organization != null) {
			fullOrgName = organization.getFullOrgName();
		}
		String[] strs = fullOrgName.split(ModuleConstants.SEPARATOR);
		if (strs.length > 2) {
			fullOrgName = strs[strs.length - 2] + ModuleConstants.SEPARATOR + strs[strs.length - 1];
		}
		organization.setFullOrgName(fullOrgName);
		positiveInfoRecord.setOrganization(organization);
		User user = ThreadVariable.getUser();
		if (user != null && DialogMode.ADD_MODE.equals(type)) {
			positiveInfoRecord.setGridMemberPhone(user.getMobile());
		}
		//positiveInfoRecord.setSignDate(CalendarUtil.now());
	}

	private void addAttachFile(String[] attachFileNames, Long objectId) {
		if (attachFileNames != null && attachFileNames.length > 0) {
			TaskListAttachFile taskListAttachFile = null;
			StoredFile storedFile = null;
			for (String attachFileName : attachFileNames) {
				if (attachFileName.charAt(0) == ',') {
					attachFileName = attachFileName.substring(1);
					try {
						storedFile = FileUtil.copyTmpFileToStoredFile(attachFileName,
								GridProperties.TASKLIST_PATH);
					} catch (Exception e) {
						e.printStackTrace();
					}
					taskListAttachFile = new TaskListAttachFile();
					taskListAttachFile.setBusinessId(objectId);
					taskListAttachFile.setPhysicsFullFileName(storedFile.getStoredFilePath()
							+ File.separator + storedFile.getStoredFileName());
					taskListAttachFile.setFileName(attachFileName);
					taskListAttachFile.setModuleKey(Constants.POSITIVEINFORECORD_KEY);
					taskListAttachFileService.addTaskListAttachFile(taskListAttachFile);
				}
			}
		}
	}

	private void updateAttachFile(String[] attachFileNames, Long objectId) {
		if (attachFileNames != null && attachFileNames.length > 0) {
			taskListAttachFileService.deleteTaskListAttachFilesByBusinessId(objectId,
					Constants.POSITIVEINFORECORD_KEY);
			addAttachFile(attachFileNames, objectId);
		}
	}
	
	@Override
	public void updateTransferPositiveInfoRecord(
			Long id,String orgCode,Long orgId) {
			 positiveInfoRecordDao.updateTransferPositiveInfoRecord(id, orgId, orgCode);
	}
}