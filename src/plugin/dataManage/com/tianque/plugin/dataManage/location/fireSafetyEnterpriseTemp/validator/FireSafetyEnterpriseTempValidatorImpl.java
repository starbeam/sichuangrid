package com.tianque.plugin.dataManage.location.fireSafetyEnterpriseTemp.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tianque.core.datatransfer.ExcelImportHelper;
import com.tianque.core.datatransfer.dataconvert.ValidateHelper;
import com.tianque.core.validate.ValidateResult;
import com.tianque.domain.PropertyDict;
import com.tianque.plugin.dataManage.location.fireSafetyEnterpriseTemp.domain.FireSafetyEnterpriseTemp;
import com.tianque.plugin.dataManage.validate.DomainValidatorTemp;
import com.tianque.sysadmin.service.OrganizationDubboService;

@Component("fireSafetyEnterpriseTempValidator")
public class FireSafetyEnterpriseTempValidatorImpl implements
		DomainValidatorTemp<FireSafetyEnterpriseTemp> {
	@Autowired
	private ValidateHelper validateHelper;
	@Autowired
	private OrganizationDubboService organizationDubboService;

	public void setValidateHelper(ValidateHelper validateHelper) {
		this.validateHelper = validateHelper;
	}

	private boolean typeIsNotNull(PropertyDict p) {
		if (p == null || p.getId() == null) {
			return false;
		}
		return true;
	}

	@Override
	public ValidateResult validate(FireSafetyEnterpriseTemp domain) {
		ValidateResult validateResult = new ValidateResult();
		boolean nameEmpty = validateHelper.emptyString(domain.getName());
		if (nameEmpty) {
			validateResult.addErrorMessage(getColumNo("name") + "场所名称必须输入");
		}
		if (!nameEmpty
				&& validateHelper.illegalStringLength(2, 50, domain.getName())) {
			validateResult.addErrorMessage(getColumNo("name")
					+ "场所名称只能输入2-50个字符");
		}

		if (!typeIsNotNull(domain.getType())) {
			validateResult.addErrorMessage(getColumNo("type") + "企业类型必须输入");
		}
		boolean legalPersonEmpty = validateHelper.emptyString(domain
				.getManager());
		if (legalPersonEmpty) {
			validateResult.addErrorMessage(getColumNo("manager") + "负责人必须输入");
		}
		if (!legalPersonEmpty
				&& validateHelper.illegalStringLength(2, 20,
						domain.getManager())) {
			validateResult.addErrorMessage(getColumNo("manager")
					+ "负责人只能输入2-20个字符");
		}

		boolean addressEmpty = validateHelper.emptyString(domain.getAddress());
		if (addressEmpty) {
			validateResult.addErrorMessage(getColumNo("address") + "场所地址必须输入");
		}
		if (!nameEmpty
				&& validateHelper.illegalStringLength(2, 50,
						domain.getAddress())) {
			validateResult.addErrorMessage(getColumNo("address")
					+ "场所地址只能输入2-50个字符");
		}
		if (!validateHelper.emptyString(domain.getTelephone())
				&& validateHelper.illegalTelephone(domain.getTelephone())) {
			validateResult.addErrorMessage(getColumNo("telephone")
					+ "负责人联系电话输入不正确");
		}
		if (!validateHelper.emptyString(domain.getMobileNumber())
				&& validateHelper.illegalMobilePhone(domain.getMobileNumber())) {
			validateResult.addErrorMessage(getColumNo("mobileNumber")
					+ "负责人手机号码输入不正确");
		}
		if (validateHelper.illegalStringLength(0, 100, domain.getHiddenCases())) {
			validateResult.addErrorMessage(getColumNo("hiddenCases")
					+ "隐患情况不能输入大于100字符");
		}
		if (validateHelper.illegalStringLength(0, 100,
				domain.getHiddenRectification())) {
			validateResult.addErrorMessage(getColumNo("hiddenRectification")
					+ "隐患整改情况不能输入大于100字符");
		}
		if (validateHelper.illegalStringLength(0, 200, domain.getRemark())) {
			validateResult.addErrorMessage(getColumNo("remark")
					+ "备注不能输入大于200字符");
		}
		return validateResult;
	}

	public String getColumNo(String key) {
		return ExcelImportHelper.getColumNo(key);
	}

}