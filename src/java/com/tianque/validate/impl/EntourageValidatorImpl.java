package com.tianque.validate.impl;

import com.tianque.core.datatransfer.dataconvert.ValidateHelper;
import com.tianque.core.validate.DomainValidator;
import com.tianque.core.validate.ValidateResult;
import com.tianque.domain.Entourage;
import com.tianque.domain.PropertyDict;

public class EntourageValidatorImpl implements DomainValidator<Entourage> {

	private ValidateHelper validateHelper;

	public ValidateHelper getValidateHelper() {
		return validateHelper;
	}

	public void setValidateHelper(ValidateHelper validateHelper) {
		this.validateHelper = validateHelper;
	}

	private boolean genderIsNotNull(PropertyDict sex) {
		if (sex == null || sex.getId() == null) {
			return false;
		}
		return true;
	}

	public ValidateResult validate(Entourage domain) {
		ValidateResult validateResult = new ValidateResult();
		if (validateHelper.emptyString(domain.getName())) {
			validateResult.addErrorMessage("姓名必须输入");
		} else if (validateHelper.illegalStringLength(2, 20, domain.getName())) {
			validateResult.addErrorMessage("姓名只能输入2-20个字符");
		}
		// else if (validateHelper.illegalInputName(domain.getName())) {
		// validateResult.addErrorMessage("姓名只能输入字母，数字和中文字符");
		// }
		if (validateHelper.emptyString(domain.getIdCardNo())) {
			validateResult.addErrorMessage("身份证号码必须输入。");
		} else {
			if (validateHelper.illegalIdcard(domain.getIdCardNo())) {
				validateResult.addErrorMessage("身份证号码输入不正确。");
			}
		}
		if (!genderIsNotNull(domain.getGender())) {
			validateResult.addErrorMessage("性別不能为空");
		}
		if (validateHelper.emptyString(domain.getRelation())) {
			validateResult.addErrorMessage("关系必须输入");
		} else if (validateHelper.illegalStringLength(1, 20,
				domain.getRelation())) {
			validateResult.addErrorMessage("关系只能输入1-20个字符");
		}
		return validateResult;
	}

}