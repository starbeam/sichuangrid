package com.tianque.baseInfo.primaryOrg.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tianque.baseInfo.primaryOrg.constant.GridTeamConstant;
import com.tianque.baseInfo.primaryOrg.dao.GridTeamDao;
import com.tianque.baseInfo.primaryOrg.domain.GridTeam;
import com.tianque.baseInfo.primaryOrg.domain.vo.GridTeamVo;
import com.tianque.baseInfo.primaryOrg.service.GridTeamService;
import com.tianque.controller.ControllerHelper;
import com.tianque.core.validate.DomainValidator;
import com.tianque.core.validate.ValidateResult;
import com.tianque.core.vo.PageInfo;
import com.tianque.domain.Organization;
import com.tianque.domain.PropertyDict;
import com.tianque.domain.User;
import com.tianque.domain.property.OrganizationLevel;
import com.tianque.domain.property.PropertyTypes;
import com.tianque.exception.base.BusinessValidationException;
import com.tianque.exception.base.ServiceValidationException;
import com.tianque.sysadmin.service.OrganizationDubboService;
import com.tianque.sysadmin.service.PermissionService;
import com.tianque.sysadmin.service.PropertyDictService;

@Service("gridTeamService")
@Transactional
public class GridTeamServiceImpl implements GridTeamService {
	@Autowired
	private OrganizationDubboService organziationDubboService;
	@Autowired
	private GridTeamDao gridTeamDao;
	@Autowired
	private PermissionService permissionService;

	@Autowired
	private PropertyDictService propertyDictService;

	@Qualifier("gridTeamValidator")
	@Autowired
	private DomainValidator<GridTeam> validator;

	@Override
	public PageInfo<GridTeam> findGridTeamForList(GridTeam gridTeam,
			Integer page, Integer rows, String sidx, String sord) {
		if (gridTeam == null || gridTeam.getOrganization() == null
				|| gridTeam.getOrganization().getId() == null) {
			throw new BusinessValidationException("列表查询失败，未获得组织机构参数");
		}
		Organization organization = organziationDubboService
				.getSimpleOrgById(gridTeam.getOrganization().getId());
		gridTeam.setOrganization(organization);
		PageInfo<GridTeam> pageInfo = gridTeamDao.findGridTeamForList(gridTeam,
				page, rows, sidx, sord);
		return pageInfo;
	}

	@Override
	public GridTeam addGridTeam(GridTeam gridTeam) {
		ValidateResult result = validator.validate(gridTeam);
		if (result.hasError()) {
			throw new BusinessValidationException(result.getErrorMessages());
		}
		gridTeam.setIsActivated(true);//新增自动激活
		gridTeam.setActivatedTime(new Date());
		Organization organization = organziationDubboService
				.getSimpleOrgById(gridTeam.getOrganization().getId());
		gridTeam.setOrganization(organization);//补全组织机构信息
		try {
			return gridTeamDao.addGridTeam(gridTeam);
		} catch (Exception e) {
			throw new ServiceValidationException("新增数据失败", e);
		}
	}

	@Override
	public GridTeam addUserToGridTeam(User user) {
		GridTeam gridTeam = new GridTeam(user.getOrganization(),
				user.getName(), user.getMobile());
		gridTeam.setUser(user);
		//参数校验
		if (gridTeam.getMemeberName() == null
				|| "".equals(gridTeam.getMemeberName().trim())
				|| gridTeam.getPhoneNumber() == null
				|| gridTeam.getOrganization() == null) {
			throw new BusinessValidationException("参数不全,新增网格员失败");
		}
		Organization organization = organziationDubboService
				.getSimpleOrgById(gridTeam.getOrganization().getId());
		gridTeam.setOrganization(organization);//补全组织机构信息
		try {
			return gridTeamDao.addGridTeam(gridTeam);
		} catch (Exception e) {
			throw new ServiceValidationException("新增数据失败", e);
		}
	}

	@Override
	public GridTeam updateGridTeam(GridTeam gridTeam) {
		//参数校验
		ValidateResult result = validator.validate(gridTeam);
		if (result.hasError()) {
			throw new BusinessValidationException(result.getErrorMessages());
		}
		try {
			GridTeam gridTeamById = gridTeamDao.getGridTeamById(gridTeam
					.getId());
			//修改组织机构时,相应组织机构code更改
			Organization organization = organziationDubboService
					.getSimpleOrgById(gridTeam.getOrganization().getId());
			gridTeam.setOrganization(organization);
			if (gridTeamById != null && gridTeamById.getIsActivated()) {
				gridTeam.setActivatedTime(gridTeamById.getActivatedTime());
			}

			if (gridTeamById != null && !gridTeamById.getIsActivated()) {
				gridTeam.setIsActivated(true);//激活
				gridTeam.setActivatedTime(new Date());
			}
			//修改网格员时,同时修改用户信息
			if (gridTeamById != null && gridTeamById.getUser() != null
					&& gridTeamById.getUser().getId() != null
					&& isChangeSysGridTeamInfo(gridTeamById, gridTeam)) {
				User user = new User();
				user.setId(gridTeamById.getUser().getId());
				user.setMobile(gridTeam.getPhoneNumber());
				user.setName(gridTeam.getMemeberName());
				permissionService.updateUserFromGridTeam(user);
			}
			return gridTeamDao.updateGridTeam(gridTeam);
		} catch (Exception e) {
			throw new ServiceValidationException("修改数据失败", e);
		}
	}

	@Override
	public void deleteGridTeamByIds(String[] ids) {
		if (ids == null || ids.length == 0) {
			throw new BusinessValidationException("数据删除失败，未获得需要删除的数据");
		}
		try {
			gridTeamDao.deleteGridTeamByIds(ids);
		} catch (Exception e) {
			throw new ServiceValidationException("数据删除失败", e);
		}
	}

	@Override
	public void deleteGridTeamByUserIds(Long[] userIds) {
		if (userIds == null || userIds.length == 0) {
			throw new BusinessValidationException("数据删除失败，未获得需要删除的数据");
		}
		try {
			gridTeamDao.deleteGridTeamByUserIds(userIds);
		} catch (Exception e) {
			throw new ServiceValidationException("数据删除失败", e);
		}
	}

	@Override
	public GridTeam getGridTeamById(Long id) {
		if (id == null) {
			throw new BusinessValidationException("数据查询失败，未获得关键参数");
		}
		GridTeam gridTeam = gridTeamDao.getGridTeamById(id);
		Organization organization = organziationDubboService
				.getSimpleOrgById(gridTeam.getOrganization().getId());
		gridTeam.setOrganization(organization);
		gridTeam.getOrganization().setFullOrgName(
				ControllerHelper.getOrganizationRelativeName(gridTeam
						.getOrganization().getId(), organziationDubboService));
		return gridTeam;
	}

	@Override
	public List<GridTeam> getGridTeamByPhoneNumber(String phoneNumber) {
		if (phoneNumber == null) {
			throw new BusinessValidationException("数据查询失败，未获得关键参数");
		}
		return gridTeamDao.getGridTeamByPhoneNumber(phoneNumber);
	}

	@Override
	public List<GridTeamVo> gridTeamStatistics(Long orgId) {
		Organization currentOrg = organziationDubboService
				.getFullOrgById(orgId);
		if (currentOrg.getOrgLevel().getInternalId() == OrganizationLevel.GRID
				|| currentOrg.getOrgLevel().getInternalId() == OrganizationLevel.VILLAGE) {
			throw new BusinessValidationException("街道及以上层级才有网格员统计相关信息");
		}
		List<PropertyDict> dictGrid = propertyDictService
				.findPropertyDictByDomainNameAndInternalId(
						OrganizationLevel.ORG_LEVEL_KEY,
						OrganizationLevel.GRID);
		Long gridOrgLevelId = null != dictGrid && dictGrid.size() == 1 ? dictGrid.get(0).getId() : 0L;
		Long fullTimeId = propertyDictService
				.findPropertyDictByDomainNameAndDictDisplayName(
						PropertyTypes.GRID_POSITIONTYPE,
						GridTeamConstant.GRID_TEAM_POSITION_TYPE_FULL).getId();
		Long partTimeId = propertyDictService
				.findPropertyDictByDomainNameAndDictDisplayName(
						PropertyTypes.GRID_POSITIONTYPE,
						GridTeamConstant.GRID_TEAM_POSITION_TYPE_PART).getId();
		return gridTeamDao.statisticsGridTeam(currentOrg.getId(), gridOrgLevelId, fullTimeId, partTimeId);
	}

	@Override
	public GridTeam getGridTeamByIdCardNo(Long orgId, String idCardNo) {
		return gridTeamDao.getGridTeamByIdCardNo(orgId, idCardNo);
	}

	@Override
	public GridTeam getGridTeamByUserId(Long userId) {
		return gridTeamDao.getGridTeamByUserId(userId);
	}

	public boolean isChangeSysGridTeamInfo(GridTeam oldGridTeam,
			GridTeam newGridTeam) {
		if (oldGridTeam.getPhoneNumber() == null
				&& newGridTeam.getPhoneNumber() == null) {
			return !oldGridTeam.getMemeberName().equals(
					newGridTeam.getMemeberName());
		}
		if (oldGridTeam.getPhoneNumber() != null
				&& newGridTeam.getPhoneNumber() != null) {
			return !(oldGridTeam.getMemeberName().equals(
					newGridTeam.getMemeberName()) && oldGridTeam
					.getPhoneNumber().equals(newGridTeam.getPhoneNumber()));
		}
		return true;
	}

	public void bindUserToGridTeam(User user) {
		GridTeam gridTeam = getGridTeamByUserId(user.getId());
		if (gridTeam == null) {
			addUserToGridTeam(user);//新增
		} else {
			gridTeam.setMemeberName(user.getName());
			gridTeam.setPhoneNumber(user.getMobile());
			gridTeam.setOrganization(organziationDubboService
					.getSimpleOrgById(user.getOrganization().getId()));
			updateGridTeamWhenUserChange(gridTeam);//更新
		}
	}

	public GridTeam updateGridTeamWhenUserChange(GridTeam gridTeam) {
		//参数校验
		try {
			return gridTeamDao.updateGridTeam(gridTeam);
		} catch (Exception e) {
			throw new ServiceValidationException("修改数据失败", e);
		}
	}
}