package com.tianque.threeRecordsIssue.dataTrans.dataImport;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.tianque.domain.Organization;
import com.tianque.exception.base.BusinessValidationException;
import com.tianque.threeRecordsIssue.dataTrans.DataConvertDefine;
import com.tianque.threeRecordsIssue.dataTrans.domain.StepChain;
import com.tianque.threeRecordsIssue.dataTrans.domain.StepLog;
import com.tianque.userAuth.api.OrganizationDubboRemoteService;

/**
 * Created by daniel on 2015/4/26.
 */
public class Util {
	private final static Logger logger = LoggerFactory.getLogger(Util.class);

	public static StepChain getStepChain(String[] stepHeader, String[] stepData, String[] rowData) {
		StepChain stepChain = new StepChain();
		for (int i = 0; i < stepHeader.length; i++) {
			if (stepHeader[i].equals("Reg_Card_Code")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setRegCardCode(stepData[i]);
				}
			}
			if (stepHeader[i].equals("Reg_Unit")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setRegUnit(stepData[i]);
				}
			}
			if (stepHeader[i].equals("Flow_C_State")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setcFlow(stepData[i]);
				}
			}
			if (stepHeader[i].equals("Flow_Z_State")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setzFlow(stepData[i]);
				}
			}
			if (stepHeader[i].equals("Flow_B_State")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setbFlow(stepData[i]);
				}
			}
			/*
			 * if (stepHeader[i].equals("Flow_Z_SH")) { if (stepData[i] != null
			 * && !stepData[i] .equals("")) { stepChain.setz(stepData[i]); } }
			 */
			if (stepHeader[i].equals("Flow_S_State")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setsFlow(stepData[i]);
				}
			}

			if (stepHeader[i].equals("PJ_Way1")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setcWay(stepData[i]);
				}
			}

			if (stepHeader[i].equals("PJ_Way2")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setzWay(stepData[i]);
				}
			}

			if (stepHeader[i].equals("PJ_Way3")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setbWay(stepData[i]);
				}
			}
			if (stepHeader[i].equals("PJ_Way4")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setSzWay(stepData[i]);
				}
			}

			if (stepHeader[i].equals("PJ_Date1")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setcDate(stepData[i]);
				}
			}
			if (stepHeader[i].equals("PJ_Date2")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setzDate(stepData[i]);
				}
			}
			if (stepHeader[i].equals("PJ_Date3")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setbDate(stepData[i]);
				}
			}
			if (stepHeader[i].equals("PJ_Date4")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setSzDate(stepData[i]);
				}
			}

			if (stepHeader[i].equals("PJ_Result1")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setcContent(stepData[i]);
				}
			}
			if (stepHeader[i].equals("PJ_Result2")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setzContent(stepData[i]);
				}
			}
			if (stepHeader[i].equals("PJ_Result3")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setbContent(stepData[i]);
				}
			}
			if (stepHeader[i].equals("PJ_Result4")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setSzContent(stepData[i]);
				}
			}
			if (stepHeader[i].equals("Change_In_Account")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setFromRegCardCode(stepData[i]);
				}
			}

			if (stepHeader[i].equals("Change_Out_Account1")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setToCRegCardCode(stepData[i]);
				}
			}
			if (stepHeader[i].equals("Change_Out_Account2")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setToZRegCardCode(stepData[i]);
				}
			}
			if (stepHeader[i].equals("Change_Out_Account3")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setToBRegCardCode(stepData[i]);
				}
			}
			if (stepHeader[i].equals("Change_Out_Account3")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setToSZRegCardCode(stepData[i]);
				}
			}

			if (stepHeader[i].equals("P_S_If1")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setcFeedWay(stepData[i]);
				}
			}
			if (stepHeader[i].equals("P_S_If2")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setzFeedWay(stepData[i]);
				}
			}
			if (stepHeader[i].equals("P_S_If3")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setbFeedWay(stepData[i]);
				}
			}
			if (stepHeader[i].equals("P_S_If4")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setSzFeedWay(stepData[i]);
				}
			}

			if (stepHeader[i].equals("P_Suggestion1")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setcFeedResult(stepData[i]);
				}
			}
			if (stepHeader[i].equals("P_Suggestion2")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setzFeedResult(stepData[i]);
				}
			}
			if (stepHeader[i].equals("P_Suggestion3")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setbFeedResult(stepData[i]);
				}
			}
			if (stepHeader[i].equals("P_Suggestion4")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setSzFeedResult(stepData[i]);
				}
			}
			if (stepHeader[i].equals("P_S_Autograph1")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setDepartment(stepData[i]);
				}
			}
			if (stepHeader[i].equals("EnBJCode")) {
				if (stepData[i] != null && !stepData[i].equals("")) {
					stepChain.setEnbjCode(stepData[i]);
				}
			}
			if (!rowData[15].equals("")) {
				stepChain.setServerName(rowData[15]);
			}

			if (!rowData[16].equals("")) {
				stepChain.setServerTele(rowData[16]);
			}
		}
		return stepChain;
	}

	public static String[] getStepRow(String historyId, String[][] stepDatas) {
		for (int i = 0; i < stepDatas.length; i++) {
			if (stepDatas[i][0].equals(historyId)) {
				return stepDatas[i];
			}
		}
		return null;
	}

	public static StepLog getStepLog(String[] logHeader, String[] rowData) {
		StepLog sLog = new StepLog();
		int length = logHeader.length;
		for (int i = 0; i < length; i++) {
			if (logHeader[i].equals("Reg_Card_Code")) {
				if (rowData[i] != null && !rowData[i].equals("")) {
					sLog.setRegCardCode(rowData[i]);
				}
			} else if (logHeader[i].equals("Work_Content")) {
				if (rowData[i] != null && !rowData[i].equals("")) {
					sLog.setContent(rowData[i]);
				}
			} else if (logHeader[i].equals("Work_Date")) {
				if (rowData[i] != null && !rowData[i].equals("")) {
					sLog.setWorkDate(rowData[i]);
				}
			} else if (logHeader[i].equals("Work_Code")) {
				if (rowData[i] != null && !rowData[i].equals("")) {
					sLog.setUnit(rowData[i]);
				}
			} else if (logHeader[i].equals("Type")) {
				if (rowData[i] != null && !rowData[i].equals("")) {
					sLog.setType(Integer.valueOf(rowData[i]));
				}
			} else if (logHeader[i].equals("HF_C_Code")) {
				if (rowData[i] != null && !rowData[i].equals("")) {
					sLog.setVillageName(rowData[i]);
				}
			} else if (logHeader[i].equals("HF_Z_Code")) {
				if (rowData[i] != null && !rowData[i].equals("")) {
					sLog.setTownName(rowData[i]);
				}
			} else if (logHeader[i].equals("HF_B_Code")) {
				if (rowData[i] != null && !rowData[i].equals("")) {
					sLog.setLedgerName(rowData[i]);
				}
			}
			//			if (!rowMainData[15].equals("")) {
			//				sLog.setServerName(rowMainData[15]);
			//			}
			//
			//			if (!rowMainData[16].equals("")) {
			//				sLog.setServerTele(rowMainData[16]);
			//			}
			else if (logHeader[i].equals("Work_Step")) {
				if (rowData[i] != null && !rowData[i].equals("")) {
					sLog.setWorkStep(rowData[i]);
				}
			} else if (logHeader[i].equals("serverName")) {
				if (rowData[i] != null && !rowData[i].equals("")) {
					sLog.setServerName(rowData[i]);
				}
			} else if (logHeader[i].equals("serverTel")) {
				if (rowData[i] != null && !rowData[i].equals("")) {
					sLog.setServerTele(rowData[i]);
				}
			}
		}
		return sLog;
	}

	/**
	 * 根据办结方式获取步骤参数
	 *
	 * @param rowData
	 * @param stateCode
	 * @return
	 */
	public static String[] getStateWayRow(String[] rowData, String stateCode, boolean isSubstance) {
		if (stateCode.equals("34") || stateCode.equals("35") || stateCode.equals("793")
				|| stateCode.equals("779") || stateCode.equals("780") || stateCode.equals("797")) {
			rowData[3] = "com.tianque.plugin.account.state.impl.ThreeRecordsIssuePeriodCompleteState";
			rowData[4] = "600";
			rowData[9] = "92";
		}

		if (stateCode.equals("36") || stateCode.equals("38") || stateCode.equals("39")
				|| stateCode.equals("40") || stateCode.equals("41") || stateCode.equals("42")
				|| stateCode.equals("609")) {
			rowData[3] = "com.tianque.plugin.account.state.impl.ThreeRecordsStepCompleteState";
			rowData[4] = "500";
			if (rowData[1].equals(rowData[2])) {
				rowData[9] = "93";//ThreeRecordsIssueOperate.PROGRAM_CODE (程序办结)
			} else {
				rowData[9] = "41";//ThreeRecordsIssueOperate.SUBMIT_CODE?
			}
		}
		if (stateCode.equals("33") || stateCode.equals("37") || stateCode.equals("42")
				|| stateCode.equals("792") || stateCode.equals("778") || stateCode.equals("794")
				|| stateCode.equals("795") || stateCode.equals("796")) {
			if (isSubstance || stateCode.equals("792")) {
				rowData[3] = "com.tianque.plugin.account.state.impl.ThreeRecordsIssueCompleteState";
				rowData[4] = "700";
				rowData[9] = "31";
				ExcelDataImport.setThreadLocal(ImportConst.THREADLOCAL_ISSUBSTANCE, "true");
			} else {
				rowData[3] = "com.tianque.plugin.account.state.impl.ThreeRecordsIssueProgramCompleteState";
				rowData[4] = "500";
				rowData[9] = "93";
			}
		}
		return rowData;
	}

	/**
	 * 根据阶段状态获取步骤参数（在办结方式为空的情况下使用）
	 *
	 * @param rowData
	 * @param stateCode
	 * @return
	 */
	public static String[] getStateFlowRow(String[] rowData, String stateCode, boolean isSubstance) {
		if (isSubstance) {
			rowData[3] = "com.tianque.plugin.account.state.impl.ThreeRecordsStepCompleteState";
			rowData[4] = "500";
			rowData[9] = "93";
			return rowData;
		}
		if (stateCode.equals("新建")) {
			rowData[3] = "com.tianque.plugin.account.state.impl.ThreeRecordsDealingState";
			rowData[4] = "120";
			rowData[9] = "";
		} else if (stateCode.contains("呈报")) {
			rowData[3] = "com.tianque.plugin.account.state.impl.ThreeRecordsStepCompleteState";
			rowData[4] = "500";
			rowData[9] = "41";
		} else if (stateCode.equals("交办")) {
			rowData[3] = "com.tianque.plugin.account.state.impl.ThreeRecordsStepCompleteState";
			rowData[4] = "500";
			rowData[9] = "21";
		} else if (stateCode.equals("已申报")) {
			rowData[3] = "com.tianque.plugin.account.state.impl.ThreeRecordsStepCompleteState";
			rowData[4] = "500";
			rowData[9] = "41";
		} else if (stateCode.equals("已提交")) {
			rowData[3] = "com.tianque.plugin.account.state.impl.ThreeRecordsStepCompleteState";
			rowData[4] = "500";
			rowData[9] = "41";
		} else if (stateCode.equals("同意接收")) {
			rowData[3] = "com.tianque.plugin.account.state.impl.ThreeRecordsDealingState";
			rowData[4] = "120";
			rowData[9] = "1";
		} else if (stateCode.equals("转办")) {
			rowData[3] = "com.tianque.plugin.account.state.impl.ThreeRecordsStepCompleteState";
			rowData[4] = "500";
			rowData[9] = "50";
		} else if (stateCode.equals("已办结")) {
			rowData[3] = "com.tianque.plugin.account.state.impl.ThreeRecordsIssueProgramCompleteState";
			rowData[4] = "500";
			rowData[9] = "93";
		} else if (stateCode.equals("审核已通过")) {
			rowData[3] = "com.tianque.plugin.account.state.impl.ThreeRecordsDealingState";
			rowData[4] = "120";
			rowData[9] = "";
		} else if (stateCode.equals("未审核")) {
			rowData[3] = "com.tianque.plugin.account.state.impl.ThreeRecordsUnConceptedState";
			rowData[4] = "110";
			rowData[9] = "";
		} else if (stateCode.equals("流转")) {
			rowData[3] = "com.tianque.plugin.account.state.impl.ThreeRecordsStepCompleteState";
			rowData[4] = "500";
			rowData[9] = "1";
		}

		return rowData;
	}

	public static String[] setRowDate(String[] rowData, String date) {
		rowData[5] = date;
		rowData[6] = date;
		rowData[7] = date;
		rowData[13] = date;
		return rowData;
	}

	public static String getType(String dataType) {
		return DataConvertDefine.getType(dataType) + "";
	}

	/**
	 * 根据名字查找组织，根据excel中定义的单位查找(省级以下)
	 *
	 * @param organizationDubboRemoteService
	 * @param headDatas                      :excel sheet[说明及单位] 的数据
	 * @param unitName
	 * @return
	 */
	public static Organization getOrgByName(
			OrganizationDubboRemoteService organizationDubboRemoteService, String[][] headDatas,
			String unitName) {
		if (StringUtils.isBlank(unitName))
			return null;
		unitName = unitName.trim();
		String[] unitNameSplit = unitName.split("->");
		List<Organization> orgList = null;
		if (unitNameSplit.length == 2) {
			orgList = organizationDubboRemoteService.findOrganizationsByOrgName(unitNameSplit[1]);
		} else {
			orgList = organizationDubboRemoteService.findOrganizationsByOrgName(unitName);
		}
		Organization nowOrg = null;

		// excel中设定的单位
		String settingOrg = new StringBuilder("中国->")
				.append(headDatas[2][1].trim())
				.append(StringUtils.isNotBlank(headDatas[2][3]) ? "->" + headDatas[2][3].trim()
						: "")
				.append(StringUtils.isNotBlank(headDatas[2][5]) ? "->" + headDatas[2][5].trim()
						: "")
				.append(StringUtils.isNotBlank(headDatas[2][7]) ? "->" + headDatas[2][7].trim()
						: "").toString();
		if (orgList.size() == 1) {
			nowOrg = orgList.get(0);
		} else if (orgList.size() > 1) {
			for (Organization org : orgList) {
				org = organizationDubboRemoteService.getFullOrgById(org.getId());
				if (org.getFullOrgName().startsWith(settingOrg)
						&& org.getFullOrgName().endsWith(unitName)) {
					nowOrg = org;
					break;
				}
			}
		}
		if (nowOrg != null && StringUtils.isBlank(nowOrg.getFullOrgName())) {
			nowOrg = organizationDubboRemoteService.getFullOrgById(nowOrg.getId());
		}
		if (nowOrg != null
				&& (!nowOrg.getFullOrgName().startsWith(settingOrg) || !nowOrg.getFullOrgName()
				.endsWith(unitName))) {
			nowOrg = null;
		}
		if (nowOrg == null) {
			String errorMsg = "未找到组织：" + settingOrg + "==》" + unitName;
			ExcelDataImport.setThreadLocal("error_msg", errorMsg);
			String maybeOrg = "";
			if (orgList != null) {
				for (Organization o : orgList) {
					maybeOrg += o.getFullOrgName() + ";";
				}
			}
			throw new BusinessValidationException(errorMsg + ",可能有：" + maybeOrg);
		}
		return nowOrg;
	}
}
