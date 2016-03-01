package com.tianque.baseInfo.optimalObject.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tianque.baseInfo.actualHouse.domain.HouseInfo;
import com.tianque.baseInfo.actualHouse.service.ActualHouseService;
import com.tianque.baseInfo.optimalObject.dao.OptimalObjectDao;
import com.tianque.baseInfo.optimalObject.domain.OptimalObject;
import com.tianque.dao.PopulationTypeDao;
import com.tianque.domain.PopulationTypeBean;
import com.tianque.domain.PropertyDict;
import com.tianque.domain.property.PropertyTypes;
import com.tianque.gis.domain.vo.HousePopulationVo;
import com.tianque.gis.domain.vo.PopulationVo;
import com.tianque.gis.service.CommonMark;
import com.tianque.service.helper.ManagePopulationByHouseHelper;
import com.tianque.service.util.PopulationCatalog;
import com.tianque.sysadmin.service.PropertyDictService;

@Service("queryOptimalObjectPopulationForGis")
@Transactional
public class QueryOptimalObjectPopulationForGis extends CommonMark {

	@Autowired
	private OptimalObjectDao optimalObjectDao;
	@Autowired
	private PopulationTypeDao populationTypeDao;
	@Autowired
	private PropertyDictService propertyDictService;
	@Autowired
	private ManagePopulationByHouseHelper managePopulationByHouseHelper;
	@Autowired
	private ActualHouseService actualHouseService;

	@Override
	public List<PopulationVo> findPopulationVosByHousePopulations(
			List<HousePopulationVo> housePopulations) {
		List<PopulationVo> Populations = new ArrayList<PopulationVo>();
		for (HousePopulationVo housePopulation : housePopulations) {
			Populations.add(getPopulationVoByPopulationIdAndHouseId(
					housePopulation.getPopulationId(), housePopulation.getHouseId()));
		}
		return Populations;
	}

	@Override
	public PopulationVo getPopulationVoByPopulationIdAndHouseId(Long populationId, Long houseId) {
		OptimalObject optimalObject = optimalObjectDao.get(populationId);
		PopulationVo populationVo = new PopulationVo();
		if (optimalObject.getIsHaveHouse() != null && optimalObject.getIsHaveHouse()) {
			managePopulationByHouseHelper.loadLivingHouseIfNecc(PopulationCatalog.OPTIMAL_OBJECT,
					optimalObject);
			if (null != optimalObject.getHouseId()) {
				HouseInfo houseInfo = actualHouseService.getHouseInfoById(optimalObject
						.getHouseId());
				populationVo.setHouseId(houseId);
				populationVo.setOrgId(houseInfo.getOrganization().getId());
				populationVo.setAddress(houseInfo.getAddress());
				if (null != houseInfo && null != houseInfo.getGisInfo()) {
					populationVo.setGisInfo(houseInfo.getGisInfo());
					populationVo.setEnableBind(true);
				} else {
					populationVo.setEnableBind(false);
				}
			}
		} else {
			populationVo.setEnableBind(false);
			if (null != optimalObject.getNoHouseReason()) {
				populationVo.setNoHouseReason(optimalObject.getNoHouseReason());
			} else {
				populationVo.setNoHouseReason("暂未填写");
			}
		}
		String populationType = PopulationCatalog.OPTIMAL_OBJECT.getCatalog();
		PopulationTypeBean populationTypeBean = populationTypeDao
				.getPopulationTypeByPopulationIdAndType(optimalObject.getId(), populationType);
		populationVo.setKeyPersonType(populationTypeBean.getActualType());
		populationVo.setIsHaveHouse(optimalObject.getIsHaveHouse());
		populationVo.setGender(optimalObject.getGender());
		populationVo.setGenderName(getPropertyDictText(PropertyTypes.GENDER, optimalObject
				.getGender().getId()));
		populationVo.setIdCardNo(optimalObject.getIdCardNo());
		populationVo.setImgUrl(optimalObject.getImgUrl());
		populationVo.setName(optimalObject.getName());
		populationVo.setPersonId(populationTypeBean.getActualId());
		return populationVo;
	}

	private String getPropertyDictText(String domainName, Long id) {
		if (null == id) {
			return "";
		} else {
			PropertyDict dict = propertyDictService.getPropertyDictById(id);
			return dict == null ? "" : dict.getDisplayName();
		}
	}

}