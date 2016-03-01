package com.tianque.threeRecordsIssue.dataTrans.dataImport;

import static com.tianque.threeRecordsIssue.dataTrans.dataImport.Util.getStateFlowRow;
import static com.tianque.threeRecordsIssue.dataTrans.dataImport.Util.getStateWayRow;
import static com.tianque.threeRecordsIssue.dataTrans.dataImport.Util.getType;
import static com.tianque.threeRecordsIssue.dataTrans.dataImport.Util.setRowDate;
import static org.apache.commons.beanutils.PropertyUtils.getProperty;
import static org.apache.commons.beanutils.PropertyUtils.setProperty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.alibaba.fastjson.JSON;
import com.tianque.account.api.AssignFormDubboService;
import com.tianque.account.api.DeclareFormDubboService;
import com.tianque.account.api.LedgerPoorPeopleDubboService;
import com.tianque.account.api.LedgerSteadyWorkDubboService;
import com.tianque.account.api.PeopleAspirationDubboService;
import com.tianque.account.api.ReplyFormDubboService;
import com.tianque.account.api.ReportFormDubboService;
import com.tianque.account.api.ThreeRecordsKeyGeneratorDubboService;
import com.tianque.account.api.TurnFormDubboService;
import com.tianque.baseInfo.base.domain.AttentionPopulation;
import com.tianque.baseInfo.base.domain.Countrymen;
import com.tianque.cache.PageInfoCacheThreadLocal;
import com.tianque.core.datatransfer.ExcelReader;
import com.tianque.core.datatransfer.data.ExcelData;
import com.tianque.core.datatransfer.util.CircularDoubleBufferedQueue;
import com.tianque.core.datatransfer.util.DataImportVo;
import com.tianque.core.util.BaseInfoTables;
import com.tianque.core.util.ConstantsProduct;
import com.tianque.core.util.DateUtil;
import com.tianque.core.util.StringUtil;
import com.tianque.core.util.ThreadVariable;
import com.tianque.core.validate.ValidateResult;
import com.tianque.datatransfer.UpdateTicketInfo;
import com.tianque.domain.Organization;
import com.tianque.domain.Session;
import com.tianque.domain.User;
import com.tianque.domain.property.OrganizationLevel;
import com.tianque.domain.property.OrganizationType;
import com.tianque.exception.base.BusinessValidationException;
import com.tianque.plugin.account.constants.LedgerConstants;
import com.tianque.plugin.account.domain.AssignForm;
import com.tianque.plugin.account.domain.AssignFormReceiver;
import com.tianque.plugin.account.domain.BaseWorking;
import com.tianque.plugin.account.domain.DeclareForm;
import com.tianque.plugin.account.domain.LedgerPeopleAspirations;
import com.tianque.plugin.account.domain.LedgerPoorPeople;
import com.tianque.plugin.account.domain.LedgerSteadyWork;
import com.tianque.plugin.account.domain.ReplyForm;
import com.tianque.plugin.account.domain.ReportForm;
import com.tianque.plugin.account.domain.ThreeRecordsIssueStep;
import com.tianque.plugin.account.domain.TurnForm;
import com.tianque.plugin.account.state.ThreeRecordsIssueOperate;
import com.tianque.plugin.account.state.ThreeRecordsIssueState;
import com.tianque.plugin.datatransfer.dataconvert.AbstractTempDataConverter;
import com.tianque.service.util.PopulationType;
import com.tianque.state.TicketState;
import com.tianque.threeRecordsIssue.dataTrans.DataConvertDefine;
import com.tianque.threeRecordsIssue.dataTrans.DataTransferConstants;
import com.tianque.threeRecordsIssue.dataTrans.SheetValidateResult;
import com.tianque.threeRecordsIssue.dataTrans.ThreadPool;
import com.tianque.threeRecordsIssue.dataTrans.ValidateExcelHeader;
import com.tianque.threeRecordsIssue.dataTrans.dataConvert.ThreeCountyDataConverterPoxy;
import com.tianque.threeRecordsIssue.dataTrans.dataConvert.ThreeDataConvert;
import com.tianque.threeRecordsIssue.dataTrans.domain.StepChain;
import com.tianque.threeRecordsIssue.dataTrans.domain.StepLog;
import com.tianque.userAuth.api.OrganizationDubboRemoteService;

public class ExcelDataImport extends Thread {

	public final static Logger logger = LoggerFactory.getLogger(ExcelDataImport.class);

	// private int everyThreadHandleNum = 500;// 每个线程处理的数量

	private String ticketId;// 缓存ID
	private String fileUrl;// 文件路径
	private int firstDataRow;// 第一行数据的行数
	private String templates;
	private String dataType;//台账类别：水利信息、交通信息...DataConvertDefine.java
	private Session session;
	private boolean validateErrorOccur = false;// 验证是否有错误信息
	private ThreeDataConvert converter;// 数据转换类
	private ApplicationContext appContext;
	private FileInputStream file;
	private ExcelData excelDatas;// 导入的工作薄的所有数据（包含隐藏的工作表的数据）
	private ExcelData exporDatas;
	private int totalRow;// 表中的全部数据
	private static final int LIMIT_ROW_NUM = 10000;// 能够导入的数据的最大量

	private boolean isIntercept = false;// 是否中断

	private InputStream inputStream;
	private File storedFile;
	private String exportMessageExcelName;// 错误数据表格的名字
	private Map<Integer, String[][]> dataMap;// 数据表map,装载的是纯粹需要导入的数据
	private Map<Integer, Integer> dataNum;

	private Map<Integer, ThreeDataConvert> converterMap;

	private Integer currentSheetNum;
	private String[][] totalDatas;// 导入表格的主表数据
	private String[][] stepInfoDatas;// 表数据说明
	private String[][] headDatas;// 表头数据
	private String[][] beanDatas;// 对应表字段的数据

	private Integer currentRowInErrorExcel;// 错误数据表格中数据的行数
	private Organization headerOrg;// 表头组织机构
	private Integer currentDealRow = 0;// 当前处理行数
	private int importDataNum;// 导入数据的长度
	private Integer nullRowNum = 0;// 空行的数量
	private ExcelHelper exportExcel;
	private UpdateTicketInfo updateTicketInfo;// 修改缓存的信息
	private CircularDoubleBufferedQueue queue;
	private Integer dataSheetNum;
	private OrganizationDubboRemoteService organizationDubboRemoteService;
	private ReportFormDubboService reportFormDubboService;
	private TurnFormDubboService turnFormDubboService;
	private ReplyFormDubboService replyFormDubboService;
	private DeclareFormDubboService declareFormDubboService;
	private AssignFormDubboService assignFormDubboService;
	private LedgerPoorPeopleDubboService ledgerPoorPeopleDubboService;
	private LedgerSteadyWorkDubboService ledgerSteadyWorkDubboService;
	private PeopleAspirationDubboService peopleAspirationDubboService;
	Organization initOrg;
	/**
	 * 导入功能预留标示符
	 */
	private String module;
	/**
	 * 是否是为乡镇级以上的领导视图提供的导入的
	 */
	private int isTownLeaderImport = 0;
	// private PlatformTransactionManager jtaTransactionManager;

	private String uuid;
	private String fileName;
	private Integer errorFileCount = 1;
	private static final int ERROR_ROW_NUM = 1000;
	private static final int IMPORT_END = 2;// 导入结束标识
	private ThreeRecordsKeyGeneratorDubboService serialKeyGenerator;
	private String ledgerSerialNumber;
	private String ledgerSerialNumberOld;
	private Long ledgerNewId;
	// 台账重复导入编号记录
	private List<String> ledgerSerialNumberOldRepeatRecord = new ArrayList<String>();

	private ValidateExcelHeader validateExcelHeader;

	// 台账办部门编号
	private static final String DEPARTMENT_SUFFIX_COUNTY_LEDGER = "1jg";
	// 县委县政府部门编号
	private static final String DEPARTMENT_SUFFIX_COUNTY_COMMITTEE = "1xw";
	// 县联席会议部门编号
	private static final String DEPARTMENT_SUFFIX_COUNTY_JOINT = "1lx";
	private static final ThreadLocal<Map> threadLocal = new ThreadLocal<Map>();
	ValidateResult vError = null;

	public ExcelDataImport(Session session, ApplicationContext appContext, String ticketId,
						   String fileUrl, String dataType, int firstDataRow, String templates,
						   CircularDoubleBufferedQueue queue, String exportExcelName, String fileName,
						   String module, int isTownLeaderImport,
						   ThreeRecordsKeyGeneratorDubboService serialKeyGenerator) throws Exception {
		this.appContext = appContext;
		this.ticketId = ticketId;
		this.fileUrl = fileUrl;
		this.dataType = dataType;
		this.firstDataRow = firstDataRow;
		this.session = session;
		this.templates = templates;
		this.currentRowInErrorExcel = firstDataRow;
		this.exportMessageExcelName = exportExcelName;
		this.uuid = exportExcelName;
		this.queue = queue;
		this.fileName = fileName;
		this.module = module;
		this.isTownLeaderImport = isTownLeaderImport;
		this.serialKeyGenerator = serialKeyGenerator;
		init();
	}

	private void init() throws Exception {

		file = openFile(fileUrl);
		excelDatas = ExcelReader.readFile(file);
		createExportExcel(exportMessageExcelName);
		updateTicketInfo = new UpdateTicketInfo(appContext);
		converterMap = new HashMap<Integer, ThreeDataConvert>();
		this.organizationDubboRemoteService = (OrganizationDubboRemoteService) appContext
				.getBean("organizationDubboRemoteService");
		this.reportFormDubboService = (ReportFormDubboService) appContext
				.getBean("reportFormDubboService");
		this.turnFormDubboService = (TurnFormDubboService) appContext
				.getBean("turnFormDubboService");
		this.replyFormDubboService = (ReplyFormDubboService) appContext
				.getBean("replyFormDubboService");
		this.declareFormDubboService = (DeclareFormDubboService) appContext
				.getBean("declareFormDubboService");
		this.assignFormDubboService = (AssignFormDubboService) appContext
				.getBean("assignFormDubboService");
		this.ledgerPoorPeopleDubboService = (LedgerPoorPeopleDubboService) appContext
				.getBean("ledgerPoorPeopleDubboService");
		this.ledgerSteadyWorkDubboService = (LedgerSteadyWorkDubboService) appContext
				.getBean("ledgerSteadyWorkDubboService");
		this.peopleAspirationDubboService = (PeopleAspirationDubboService) appContext
				.getBean("peopleAspirationDubboService");
	}

	private void createExportExcel(String exportExcelName) throws Exception {
		exportExcel = new ExcelHelper(templates, dataType, exportExcelName);
		exportExcel.createExportDataExcel();
		exportExcel.createExcelCite();// 创建表的引用
	}

	private long startTime;

	@Override
	public void run() {
		startTime = new Date().getTime();
		createThreadUser();

		if (!validateParams()) {
			return;
		}
		converter = getDataConvert(dataType, appContext);

		if (isConverterNull()) {
			return;
		}
		try {
			pushToQueue();
		} catch (Exception e) {
			logger.error("用户排队时异常：", e);
		}
		try {
			//处理excel数据导入,包括验证和导入两部分
			converterMap = getDataConvertMap(dataType);
			if (excelDatas.getSheetDatas(2).length < 2) {
				updateErrorTitleAndRowMsg("没有台账数据", -1, "没有台账数据");
				return;
			}
			validateExcelDatas();
			resolveAndPersistenceExcelDatas();
		} finally {
			threadLocal.remove();
			ThreadPool.remove(this.getId());
		}

	}

	private void pushToQueue() throws InterruptedException {
		queue.put(ticketId);
		queue.take();
		updateUserTicketNumber();
	}

	private void updateUserTicketNumber() {
		for (Iterator it = DataImportVo.getTable().keySet().iterator(); it.hasNext(); ) {
			String key = (String) it.next();
			Integer value = DataImportVo.getTable().get(key);
			if (value.intValue() > 0) {
				DataImportVo.getTable().put(key, value - 1);
			}
		}
	}

	private void updateNextStepMsgTitle(String title) {
		updateTicketInfo.updateTicketInfo(ticketId, "{msg:'" + title + "'}", currentDealRow,
				importDataNum, currentRowInErrorExcel, firstDataRow, nullRowNum, TicketState.DOING);
	}

	private void validateExcelDatas() {
		updateNextStepMsgTitle("正在解析导入的文件");
		dataMap = new HashMap<Integer, String[][]>();
		try {
			stepInfoDatas = excelDatas.getSheetDatas(6);
			headDatas = excelDatas.getSheetDatas(1);
			totalDatas = excelDatas.getSheetDatas(2);
			beanDatas = excelDatas.getSheetDatas(3);

			if (beanDatas.length == 0) {
				updateErrorTitleAndRowMsg("导入数据时出错，程序已终止，详情参见下方错误信息列表", -1, "数据模板有误，请下载最新模板");
				return;
			}
			formatBeanData(beanDatas);

		} catch (Exception e) {
			updateErrorTitleAndRowMsg("导入数据时出错，程序已终止，详情参见下方错误信息列表", -1, "数据模板有误，请下载最新模板");
			return;
		}
		if (validateVersion()) {
			return;
		}
		importDataNum = totalDatas.length - firstDataRow;

		exportExcel.fullUnit(totalDatas, 1);
		// exportExcel.fullUnit(totalDatas, 2);
		validateExcelHeader = new ValidateExcelHeader(appContext, converter, ticketId, templates,
				dataType, firstDataRow, importDataNum, isTownLeaderImport);
	}

	private void formatBeanData(String[][] beanDatas) {
		Type pt = ((ParameterizedType) converter.getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		ArrayList<String> fieldsName = getClassFieldsName((Class) pt, true);
		for (int i = 0; i < beanDatas[0].length; i++) {
			for (int j = 0; j < fieldsName.size(); j++) {
				if (beanDatas[0][i].toLowerCase().equals(fieldsName.get(j).toLowerCase())) {
					beanDatas[0][i] = fieldsName.get(j);
					break;
				}
			}
		}
	}

	private boolean validateVersion() {
		FileInputStream fExportFile = openFile(exportExcel.getExportFileUrl());
		try {
			exporDatas = ExcelReader.readFile(fExportFile);
		} catch (IOException e) {
			updateErrorTitleAndRowMsg("导入数据时出错，程序已终止，详情参见下方错误信息列表", -1, "读表数据有误");
			return true;
		}
		String[][] exportBeanDatas = exporDatas.getSheetDatas(2);
		/*
		 * if (!Arrays.equals(beanDatas[0], exportBeanDatas[0])) {
		 * updateErrorTitleAndRowMsg("导入数据时出错，程序已终止，详情参见下方错误信息列表", -1,
		 * "数据模板有误，请下载最新模板"); return true; }
		 */
		return false;
	}

	private void resolveAndPersistenceExcelDatas() {
		updateNextStepMsgTitle("文件已解析，准备校验数据格式");

		// 转换数据，验证并添加到数据库
		handleDomains();

		long useTime = (new Date().getTime() - startTime) / 1000;
		String successMsg = "数据已保存，处理完成，耗时：" + useTime / 60 + "分" + useTime % 60 + "秒";
		if (!validateErrorOccur) {
			updateCompleteMsgTitle(successMsg
					+ "["
					+ currentDealRow
					+ "/"
					+ importDataNum
					+ "]。"
					+ (ledgerSerialNumberOldRepeatRecord.size() == 0 ? "" : "以下台账本次未导入（以前已经导入）["
					+ ledgerSerialNumberOldRepeatRecord.size() + "]："
					+ JSON.toJSONString(ledgerSerialNumberOldRepeatRecord)));
		} else {
			if (currentDealRow == importDataNum) {
				updateNextStepMsgTitle(successMsg);
			}
		}

		updateTicketInfo.updateLogNum(uuid, importDataNum, currentRowInErrorExcel - firstDataRow);
		updateTicketInfo.updateLogStatus(uuid, IMPORT_END, fileName);
	}

	/**
	 * 导入数据 创建用户,处理数据导入,输出excel
	 *
	 * @throws Exception
	 */
	private void handleDomains() {
		try {
			updateTicketInfo.addLog(uuid, fileName, dataType, importDataNum, templates);
			createThreadUser();
			handleData();
			exportExcel.outFile(currentDealRow, importDataNum);
			createErrorExcelZip();
			PageInfoCacheThreadLocal.commit();
		} catch (Exception e) {
			if (vError != null) {
				Iterator<String> keySetIterator = vError.getMapMessages().keySet().iterator();
				while (keySetIterator.hasNext()) {
					String key = keySetIterator.next();
					String value = vError.getMapMessages().get(key);
					updateErrorTitleAndRowMsg("导入数据时出错，程序已终止，详情参见下方错误信息列表【"+this.ledgerSerialNumberOld+"】", -1, value);
				}
			}
			updateErrorTitleAndRowMsg("导入数据时出错，程序已终止，详情参见下方错误信息列表【"+this.ledgerSerialNumberOld+"】", -1, e.getMessage());
			if (StringUtils.isNotBlank((String) getThreadLocal("error_msg"))) {
				updateErrorTitleAndRowMsg("导入数据时出错，程序已终止，详情参见下方错误信息列表【"+this.ledgerSerialNumberOld+"】", -1,
						(String) getThreadLocal("error_msg"));
			}
			logger.error("导入数据时出错，程序已终止，详情参见下方错误信息列表【"+this.ledgerSerialNumberOld+"】", e);
			if (DataTransferConstants.POOR_PEOPLE_DATA.equals(dataType)) {
				ledgerPoorPeopleDubboService.deleteLedgerPoorPeopleById(ledgerNewId);
			} else if (DataTransferConstants.STEADY_WORK_DATA.equals(dataType)) {
				ledgerSteadyWorkDubboService.deleteLedgerSteadyWorkById(ledgerNewId);
			} else {
				peopleAspirationDubboService.deletePeopleAspirationById(ledgerNewId);
				if (DataTransferConstants.WATERRESOURCE_DATA.equals(dataType)) {
					peopleAspirationDubboService.deleteWaterLedgerAndStepById(ledgerNewId);
				} else if (DataTransferConstants.TRAFFIC_DATA.equals(dataType)) {
					peopleAspirationDubboService.deleteTrafficLedgerAndStepById(ledgerNewId);
				} else if (DataTransferConstants.ENERGY_DATA.equals(dataType)) {
					peopleAspirationDubboService.deleteEnergyLedgerAndStepById(ledgerNewId);
				} else if (DataTransferConstants.EDUCATION_DATA.equals(dataType)) {
					peopleAspirationDubboService.deleteEducationLedgerAndStepById(ledgerNewId);
				} else if (DataTransferConstants.SCIENCE_DATA.equals(dataType)) {
					peopleAspirationDubboService.deleteScienceLedgerAndStepById(ledgerNewId);
				} else if (DataTransferConstants.MEDICAL_DATA.equals(dataType)) {
					peopleAspirationDubboService.deleteMedicalLedgerAndStepById(ledgerNewId);
				} else if (DataTransferConstants.LABOR_DATA.equals(dataType)) {
					peopleAspirationDubboService.deleteLaborLedgerAndStepById(ledgerNewId);
				} else if (DataTransferConstants.ENVIRONMENT_DATA.equals(dataType)) {
					peopleAspirationDubboService.deleteEnvironmentLedgerAndStepById(ledgerNewId);
				} else if (DataTransferConstants.TOWN_DATA.equals(dataType)) {
					peopleAspirationDubboService.deleteTownLedgerAndStepById(ledgerNewId);
				} else if (DataTransferConstants.AGRICULTURE_DATA.equals(dataType)) {
					peopleAspirationDubboService.deleteAgricultureLedgerAndStepById(ledgerNewId);
				} else if (DataTransferConstants.OTHER_DATA.equals(dataType)) {
					peopleAspirationDubboService.deleteOtherLedgerAndStepById(ledgerNewId);
				}
			}
			PageInfoCacheThreadLocal.rollback();
			return;
		}
	}

	private void handleData() {
		// 台账主记录
		Organization orgRootSet = Util.getOrgByName(organizationDubboRemoteService, headDatas,
				headDatas[2][5]);
		setThreadLocal(ImportConst.ORG_ROOT_SET, orgRootSet);
		// 工作日志数据转换 start
		String[][] logDatas = excelDatas.getSheetDatas(8);
		int logDataLength = logDatas.length;
		Map<String, List<StepLog>> logDataMap = new HashMap<String, List<StepLog>>(logDataLength);
		List<StepLog> currLogDataList = null;
		String[] logHeader = logDatas[0];
		for (int i = 1; i < logDataLength; i++) {
			String[] logRowData = logDatas[i];
			currLogDataList = logDataMap.get(logRowData[0]);
			if (currLogDataList == null) {
				currLogDataList = new ArrayList<StepLog>();
				logDataMap.put(logRowData[0], currLogDataList);
			}
			currLogDataList.add(Util.getStepLog(logHeader, logRowData));
		}
		// 工作日志数据转换 end
		for (int rowIndex = 0; rowIndex < importDataNum; rowIndex++) {
			ledgerNewId = null;
			threadLocal.remove();
			converter = converterMap.get(0);
			beanDatas = excelDatas.getSheetDatas(3);

			if (isIntercept) {
				return;
			}
			long useTime = (new Date().getTime() - startTime) / 1000;
			float rowTime = ((float) useTime)
					/ (rowIndex - ledgerSerialNumberOldRepeatRecord.size());
			// useTime/rowIndex
			String estimateTimeStr = null;
			if (rowIndex - ledgerSerialNumberOldRepeatRecord.size() > 30) {
				int estimateTime = (int) ((importDataNum - rowIndex) * rowTime);
				estimateTimeStr = estimateTime / 60 + "分" + estimateTime % 60 + "秒";
			}
			updateNextStepMsgTitle("正在导入数据,已完成：" + getPercent(rowIndex + 1, importDataNum)
					+ "，已耗时：" + useTime / 60 + "分" + useTime % 60 + "秒"
					+ (estimateTimeStr == null ? "" : "，预估：" + estimateTimeStr));

			ValidateResult result = new ValidateResult();
			String[] rowData = totalDatas[rowIndex + firstDataRow];
			this.ledgerSerialNumberOld = rowData[0];

			//			if (!DataTransferConstants.POOR_PEOPLE_DATA.equals(dataType)) {
			//				rowData[13] = "【" + this.ledgerSerialNumberOld + "】" + rowData[13];
			//			}

			ThreeExcelImportHelper.realRow.set(rowIndex + 1);

			currentSheetNum = 1;
			//检查是否已经导入过
			int hasCount = 0;
			if (DataTransferConstants.POOR_PEOPLE_DATA.equals(dataType)) {
				hasCount = ledgerPoorPeopleDubboService.countLedgerByOldHistoryId(
						this.ledgerSerialNumberOld, orgRootSet.getOrgInternalCode());
			} else if (DataTransferConstants.STEADY_WORK_DATA.equals(dataType)) {
				hasCount = ledgerSteadyWorkDubboService.countLedgerByOldHistoryId(
						this.ledgerSerialNumberOld, orgRootSet.getOrgInternalCode());
			} else {
				hasCount = peopleAspirationDubboService.countLedgerByOldHistoryId(
						this.ledgerSerialNumberOld, orgRootSet.getOrgInternalCode());
			}
			if (hasCount == 0) {
				String[] stepData = Util.getStepRow(this.ledgerSerialNumberOld, stepInfoDatas);
				// 2015/11/02 已提交 且审核已通过的台账作为新建处理，未通过的丢弃
				String Flow_C_State = stepData[2];
				String Flow_Z_SH = stepData[5];
				if (Flow_Z_SH != null && Flow_Z_SH.contains("未通过")) {
					currentDealRow++;// 当导入成功计数
					continue;
				}

				// 调用处理单条数据1320820802130003
				Object fData = null;
				String historyId = "";
				String strPrimaryId = "";
				if (!isBlankRow(rowData)) {
					fData = handleValidData(rowData, rowIndex);
					try {
						setProperty(fData, "status", ThreeRecordsIssueState.DEALING);
						converter.updateDomain(fData);
						historyId = getProperty(fData, "historyId").toString();
						strPrimaryId = getProperty(fData, "id").toString();
						ledgerNewId = Long.parseLong(strPrimaryId);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
				} else {
					nullRowNum++;
				}

				String[][] researchData = excelDatas.getSheetDatas(4);
				int totalSubNum = researchData.length;

				// 研究整理
				for (int i = 0; i < totalSubNum; i++) {
					converter = converterMap.get(1);
					beanDatas = excelDatas.getSheetDatas(5);
					formatBeanData(beanDatas);
					if (researchData[i][0].equals(historyId)) {
						// 调用单条数据处理
						researchData[i][1] = strPrimaryId;
						handleValidData(researchData[i], i);
						// break;
					}
				}

				String[] stepHeader = stepInfoDatas[0];

				// stepInfoDatas[rowIndex + firstDataRow];

				StepChain stepChain = Util.getStepChain(stepHeader, stepData, rowData);
				List<StepLog> stepLogs = logDataMap.get(historyId);

				// 2015/11/02 已提交 且审核已通过的台账作为新建处理
				boolean shFlag = false;
				if ("已提交".equals(stepChain.getcFlow()) && StringUtils.isBlank(stepChain.getcWay())
						&& StringUtils.isBlank(stepChain.getzWay())
						&& StringUtils.isBlank(stepChain.getbWay())
						&& StringUtils.isBlank(stepChain.getSzWay())) {
					shFlag = true;
				}
				if (stepLogs == null || stepLogs.size() == 0 || shFlag) {
					converter = converterMap.get(0);
					converter.registerProcess(fData);
					//				continue;
				} else {
					// log 数据缺失补充
					for (StepLog _log : stepLogs) {
						if (StringUtils.isBlank(_log.getUnit())) {
							_log.setUnit(stepChain.getRegUnit());
						}
					}

					handle(fData, strPrimaryId, stepChain, stepLogs, rowIndex);
				}
			} else {
				ledgerSerialNumberOldRepeatRecord.add(this.ledgerSerialNumberOld);
			}
			currentDealRow++;
			if (isIntercept) {
				return;
			}
			updateTicketErrorMsg(ticketId, result, TicketState.DOING);
			if (rowIndex % 100 == 0 && rowIndex > 0) {
				updateTicketInfo.updateLogNum(uuid, currentDealRow, currentRowInErrorExcel
						- firstDataRow);
			}
			logDataMap.remove(ledgerSerialNumberOld);
		}
	}

	/**
	 * 导入步骤数据
	 *
	 * @param id
	 */
	private void handle(Object mainData, String id, StepChain stepChain, List<StepLog> logs,
						int rowIndex) {
		if (StringUtils.isBlank(stepChain.getServerName())) {
			stepChain.setServerName("[无]");
		}
		Date createDate = null;
		try {
			createDate = (Date) getProperty(mainData, "occurDate");
			if (createDate == null)
				createDate = (Date) getProperty(mainData, "createDate");
		} catch (Exception e1) {
		}
		List<StepLog> stepLogs = new ArrayList<StepLog>();
		List<StepLog> substanceLogs = new ArrayList<StepLog>();
		String cDesc = "";
		String zDesc = "";
		//		String sDesc = "转办";
		String bDesc = "";
		String szDesc = stepChain.getSzWay();
		stepLogs = logs;
		/* 村步骤 */
		String[] cRowData = genArray(16);
		Organization regOrg = this.getOrgByName(stepChain.getRegUnit());
		if (regOrg.getOrgLevel().getInternalId() == OrganizationLevel.VILLAGE) {
			cRowData[0] = id;
			cRowData[1] = stepChain.getRegUnit();
			cRowData[2] = stepChain.getRegUnit();
			if (stepChain.getcDate() != null && !stepChain.getcDate().equals("")) {
				cRowData = setRowDate(cRowData, stepChain.getcDate());
			}
			cRowData[10] = getType(dataType);

			// 新建步骤数据
			String[] newData = Arrays.copyOf(cRowData, cRowData.length);
			newData[2] = newData[1];

			//呈报
			if ("38".equals(stepChain.getcWay())) {
				cRowData[2] = this.getParentUnitName(stepChain.getRegUnit());
			}
			if (stepChain.getcWay() != null && !stepChain.getcWay().equals("")
					&& !("0").equals(stepChain.getcWay())) {
				cRowData = getStateWayRow(cRowData, stepChain.getcWay(), false);
				cDesc = stepChain.getcWay();

				newData = getStateWayRow(newData, stepChain.getcWay(), false);
			} else if (StringUtils.isNotBlank(stepChain.getcFlow())
					&& !"新建".equals(stepChain.getcFlow())) {
				cRowData = getStateFlowRow(cRowData, stepChain.getcFlow(),
						StringUtil.isStringAvaliable(stepChain.getSzWay()));
				cDesc = stepChain.getcFlow();

				newData = getStateFlowRow(newData, stepChain.getcFlow(),
						StringUtil.isStringAvaliable(stepChain.getSzWay()));
			} else {
				cRowData = null;

			}
			newData = getStateFlowRow(newData, "新建", false);

			// 新建步骤
			converter = converterMap.get(2);
			beanDatas = excelDatas.getSheetDatas(7);
			formatBeanData(beanDatas);
			Object fData = handleValidData(newData, rowIndex);
			String[] groupDataRow = genArray(5);

			List<StepLog> villageLogs = new ArrayList<StepLog>();
			StepLog log = new StepLog();
			log.setRegCardCode(stepChain.getRegCardCode());
			log.setContent("新建");
			log.setWorkDate(stepChain.getcDate() != null ? stepChain.getcDate() : stepChain
					.getSzDate());// 村新建后村办结，cDate为空，取实质办结时间
			if (log.getWorkDate() == null) {
				log.setWorkDate(stepChain.getzDate());
			}
			if (log.getWorkDate() == null) {
				if (logs.size() > 0) {
					log.setWorkDate(logs.get(0).getWorkDate());
				}
			}
			if (log.getWorkDate() == null) {
				log.setWorkDate(createDate != null ? DateUtil.formateYMDHMS(createDate) : null);
			}
			if (log.getWorkDate() == null) {
				throw new BusinessValidationException("log.getWorkDate()不能为空");
			}
			log.setUnit(stepChain.getRegUnit());
			log.setServerName(stepChain.getServerName());
			log.setServerTele(stepChain.getServerTele());
			villageLogs.add(log);

			handleLog(id, fData, villageLogs, groupDataRow, false, "新建");

			// 追加其他log
			List<StepLog> commetLogs = new ArrayList<StepLog>();
			for (StepLog _log : logs) {
				if (_log.getUnit().equals(newData[2])
						&& !(StringUtils.isNotBlank(_log.getContent()) && _log.getContent()
						.contains("呈报至"))) {
					_log.setDealType(String.valueOf(ThreeRecordsIssueOperate.COMMENT_CODE));
					commetLogs.add(_log);
				}
			}
			this.addComment(id, fData, newData[2], commetLogs);
		} else {
			cRowData = null;
		}

		/* 镇步骤 */
		String[] zRowData = genArray(16);
		zRowData[0] = id;
		if (cRowData != null || regOrg.getOrgLevel().getInternalId() == OrganizationLevel.TOWN) {
			zRowData[1] = stepChain.getRegUnit();
			zRowData[2] = zRowData[1];

			if (stepChain.getzDate() != null) {
				zRowData = setRowDate(zRowData, stepChain.getzDate());
			}
			zRowData[10] = getType(dataType);

			// 新建步骤数据
			String[] newData = Arrays.copyOf(zRowData, zRowData.length);
			newData[2] = newData[1];

			if (cRowData != null) {
				//村呈报
				if ("38".equals(stepChain.getcWay())) {
					zRowData[1] = organizationDubboRemoteService.getSimpleOrgById(
							regOrg.getParentOrg().getId()).getOrgName();
					zRowData[2] = zRowData[1];
				} else {
					zRowData[2] = organizationDubboRemoteService.getSimpleOrgById(
							regOrg.getParentOrg().getId()).getOrgName();
				}
			}

			if ("39".equals(stepChain.getzWay())) {
				zRowData[2] = "三本台账办公室";
			}

			if (stepChain.getzWay() != null && !stepChain.getzWay().equals("")
					&& !"0".equals(stepChain.getzWay())) {
				zRowData = getStateWayRow(zRowData, stepChain.getzWay(), false);
				zDesc = stepChain.getzWay();

				newData = getStateWayRow(newData, stepChain.getzWay(), false);
			} else if (StringUtils.isNotBlank(stepChain.getzFlow())
					&& !"新建".equals(stepChain.getzFlow())) {// 旧数据中有 zState 是“新建”，但实际注册组织是村，排除掉此情况

				zRowData = getStateFlowRow(zRowData, stepChain.getzFlow(),
						StringUtil.isStringAvaliable(stepChain.getSzWay()));
				zDesc = stepChain.getzFlow();

				newData = getStateFlowRow(newData, stepChain.getzFlow(),
						StringUtil.isStringAvaliable(stepChain.getSzWay()));
			} else {
				zRowData = null;
			}
			newData = getStateFlowRow(newData, "新建", false);
			// 新建步骤
			if (regOrg.getOrgLevel().getInternalId() == OrganizationLevel.TOWN) {
				converter = converterMap.get(2);
				beanDatas = excelDatas.getSheetDatas(7);
				formatBeanData(beanDatas);
				Object fData = handleValidData(newData, rowIndex);
				String[] groupDataRow = genArray(5);

				List<StepLog> villageLogs = new ArrayList<StepLog>();
				StepLog log = new StepLog();
				log.setRegCardCode(stepChain.getRegCardCode());
				log.setContent("新建");
				log.setWorkDate(stepChain.getzDate() != null ? stepChain.getzDate() : stepChain
						.getSzDate());// 镇新建后就办结，zDate为空，取实质办结时间
				if (log.getWorkDate() == null) {
					if (logs.size() > 0) {
						log.setWorkDate(logs.get(0).getWorkDate());
					}
				}
				if (log.getWorkDate() == null) {
					log.setWorkDate(createDate != null ? DateUtil.formateYMDHMS(createDate) : null);
				}
				if (log.getWorkDate() == null) {
					throw new BusinessValidationException("log.getWorkDate()不能为空");
				}
				log.setUnit(stepChain.getRegUnit());
				log.setServerName(stepChain.getServerName());
				log.setServerTele(stepChain.getServerTele());
				villageLogs.add(log);

				handleLog(id, fData, villageLogs, groupDataRow, false, "新建");
				// 追加其他log
				List<StepLog> commetLogs = new ArrayList<StepLog>();
				for (StepLog _log : logs) {
					if (_log.getUnit().equals(newData[1])
							&& !(StringUtils.isNotBlank(_log.getContent()) && _log.getContent()
							.contains("呈报至"))) {
						_log.setDealType(String.valueOf(ThreeRecordsIssueOperate.COMMENT_CODE));
						commetLogs.add(_log);
					}
				}
				this.addComment(id, fData, newData[2], commetLogs);
			}
		} else {
			zRowData = null;
		}
		/* 部门步骤 */
		String[] bRowData = null;// 部门步骤
		//		String[] sRowData = null;// 三本台账办步骤
		String[] sbRowData = null;// 三本台账办流入部门步骤
		String[] _bRowData = null;// 回复时部门步骤
		String[] _zRowData = null;// 回复时镇步骤
		String[] _cRowData = null;// 回复时村步骤
		List<StepLog> blogs = null;
		List<StepLog> zlogs = null;
		List<StepLog> clogs = null;
		StepLog blog = null;// 回复时部门日志
		StepLog zlog = null;// 回复时镇日志
		StepLog clog = null;// 回复时村日志

		//		if ("转办".equals(stepChain.getsFlow())) {
		//			sRowData = genArray(16);
		//			sRowData = getStateFlowRow(sRowData, "转办",
		//					StringUtil.isStringAvaliable(stepChain.getSzWay()));
		//
		//			sRowData[0] = id;
		//			sRowData[1] = zRowData[2];
		//			sRowData[2] = "三本台账办公室";
		//			if (stepChain.getbDate() != null && !stepChain.getbDate().equals("")) {
		//				sRowData = setRowDate(sRowData, stepChain.getbDate());
		//			}
		//			sRowData[10] = getType(dataType);
		//		}

		for (StepLog log : logs) {
			if (StringUtils.isNotBlank(log.getWorkStep())
					&& log.getWorkStep().trim().matches("县台账办转办到.*局")) {
				sbRowData = genArray(16);
				sbRowData = getStateFlowRow(sbRowData, "转办", false);
				sbRowData[0] = id;
				sbRowData[1] = "三本台账办公室";
				sbRowData[2] = log.getWorkStep().trim().substring(7);
				sbRowData = setRowDate(sbRowData, log.getWorkDate());
				sbRowData[10] = getType(dataType);
				break;
			}
		}
		// **局 组织检查
		if (sbRowData != null && this.getOrgByName(sbRowData[2]) == null) {
			for (StepLog log : logs) {
				if (StringUtils.isNotBlank(log.getUnit()) && log.getUnit().matches(".*局")) {
					sbRowData[2] = log.getUnit().trim();
					break;
				}
			}
		}

		for (StepLog log : logs) {
			if (log.getType() == 4) {
				if (log.getLedgerName() != null) {
					_bRowData = genArray(16);
					_bRowData = getStateFlowRow(_bRowData, "已办结", false);
					_bRowData[0] = id;
					_bRowData[1] = log.getUnit();
					_bRowData[2] = log.getUnit();
					_bRowData = setRowDate(_bRowData, log.getWorkDate());
					_bRowData[10] = getType(dataType);
					blogs = new ArrayList();
					blog = new StepLog();
					blog.setRegCardCode(stepChain.getRegCardCode());
					blog.setContent(log.getContent());
					blog.setWorkDate(log.getWorkDate());
					blog.setUnit(_bRowData[2]);

					blog.setServerName(log.getServerName());
					blog.setServerTele(log.getServerTele());
					//					blog.setServerName(stepChain.getServerName());
					//					blog.setServerTele(stepChain.getServerTele());
					blogs.add(blog);
				}
				if (log.getTownName() != null && _bRowData != null) {
					_zRowData = genArray(16);
					_zRowData = getStateFlowRow(_bRowData, "交办", false);
					_zRowData[0] = id;
					_zRowData[1] = log.getLedgerName();
					_zRowData[2] = log.getTownName();
					_zRowData = setRowDate(_bRowData, log.getWorkDate());
					_zRowData[10] = getType(dataType);
					zlogs = new ArrayList();
					zlog = new StepLog();
					zlog.setRegCardCode(stepChain.getRegCardCode());
					zlog.setContent(log.getContent());
					zlog.setWorkDate(log.getWorkDate());
					zlog.setUnit(_zRowData[1]);
					zlog.setServerName(log.getServerName());
					zlog.setServerTele(log.getServerTele());
					//					zlog.setServerName(stepChain.getServerName());
					//					zlog.setServerTele(stepChain.getServerTele());
					zlogs.add(zlog);
				}
				if (log.getVillageName() != null) {
					_cRowData = genArray(16);
					_cRowData = getStateFlowRow(_cRowData, "交办", false);
					_cRowData[0] = id;
					_cRowData[1] = log.getTownName();
					_cRowData[2] = log.getVillageName();
					_cRowData = setRowDate(_cRowData, log.getWorkDate());
					_cRowData[10] = getType(dataType);
					clogs = new ArrayList();
					clog = new StepLog();
					clog.setRegCardCode(stepChain.getRegCardCode());
					clog.setContent(log.getContent());
					clog.setWorkDate(log.getWorkDate());
					clog.setUnit(_cRowData[1]);
					clog.setServerName(log.getServerName());
					clog.setServerTele(log.getServerTele());
					//					clog.setServerName(stepChain.getServerName());
					//					clog.setServerTele(stepChain.getServerTele());
					clogs.add(clog);
				}
				break;
			}
		}

		// 新建步骤数据（部门）
		if (regOrg.getOrgType().getInternalId() == OrganizationType.FUNCTIONAL_ORG) {
			bRowData = genArray(16);
			bRowData[0] = id;
			bRowData[1] = regOrg.getOrgName();
			bRowData[2] = regOrg.getOrgName();
			if (stepChain.getbDate() != null && !stepChain.getbDate().equals("")) {
				bRowData = setRowDate(bRowData, stepChain.getbDate());
			}
			bRowData[10] = getType(dataType);

			// 新建步骤数据
			String[] newData = Arrays.copyOf(bRowData, bRowData.length);
			newData[2] = newData[1];

			if (stepChain.getbWay() != null && !stepChain.getbWay().equals("")) {
				bRowData = getStateWayRow(bRowData, stepChain.getbWay(), false);
				bDesc = stepChain.getbWay();

				newData = getStateWayRow(newData, stepChain.getbWay(), false);
			} else if (stepChain.getbFlow() != null && !stepChain.getbFlow().equals("")) {
				bRowData = getStateFlowRow(bRowData, stepChain.getbFlow(), false);
				bDesc = stepChain.getbFlow();

				newData = getStateFlowRow(newData, stepChain.getbFlow(), false);
			} else {
				bRowData = null;
			}
			newData = getStateFlowRow(newData, "新建", false);

			converter = converterMap.get(2);
			beanDatas = excelDatas.getSheetDatas(7);
			formatBeanData(beanDatas);
			Object fData = handleValidData(newData, rowIndex);
			String[] groupDataRow = genArray(5);

			List<StepLog> bLogs = new ArrayList<StepLog>();
			StepLog log = new StepLog();
			log.setRegCardCode(stepChain.getRegCardCode());
			log.setContent("新建");
			log.setWorkDate(stepChain.getbDate() != null ? stepChain.getbDate() : stepChain
					.getSzDate());// 新建后就办结，cDate为空，取实质办结时间
			if (log.getWorkDate() == null) {
				if (logs.size() > 0) {
					log.setWorkDate(logs.get(0).getWorkDate());
				}
			}
			log.setUnit(stepChain.getRegUnit());
			log.setServerName(stepChain.getServerName());
			log.setServerTele(stepChain.getServerTele());
			bLogs.add(log);

			handleLog(id, fData, bLogs, groupDataRow, false, "新建");

			// 追加其他log
			List<StepLog> commetLogs = new ArrayList<StepLog>();
			for (StepLog _log : logs) {
				if (_log.getUnit().equals(newData[2])) {
					_log.setDealType(String.valueOf(ThreeRecordsIssueOperate.COMMENT_CODE));
					commetLogs.add(_log);
				}
			}
			this.addComment(id, fData, newData[2], commetLogs);
		}
		/* 实质办结步骤 */
		String[] szRowData = null;
		//		if (stepChain.getEnbjCode() != null && !stepChain.getEnbjCode().equals("")
		//				&& StringUtil.isStringAvaliable(stepChain.getSzDate())) {
		if (StringUtil.isStringAvaliable(stepChain.getSzDate())
				&& (StringUtils.isNotBlank(stepChain.getEnbjCode()) || StringUtils
				.isNotBlank(stepChain.getSzWay()))) {
			if (StringUtils.isBlank(stepChain.getEnbjCode())) {
				stepChain.setEnbjCode(regOrg.getOrgName());
			}
			szRowData = genArray(16);
			szRowData = getStateWayRow(szRowData, stepChain.getSzWay(), true);
			szRowData[0] = id;
			szRowData[1] = stepChain.getEnbjCode();
			szRowData[2] = stepChain.getEnbjCode();
			szRowData[10] = getType(dataType);
			szRowData = setRowDate(szRowData, stepChain.getSzDate());
			StepLog log = new StepLog();
			log.setRegCardCode(stepChain.getRegCardCode());
			boolean findLog = false;
			for (StepLog _log : logs) {
				if (_log.getType() == 4) {
					log.setContent(_log.getContent());
					log.setWorkDate(_log.getWorkDate());
					log.setUnit(_log.getUnit());
					log.setServerName(_log.getServerName());
					log.setServerTele(_log.getServerTele());
					findLog = true;
					break;
				}
			}
			if (!findLog) {
				log.setContent(stepChain.getSzContent());
				log.setWorkDate(stepChain.getSzDate());
				log.setUnit(stepChain.getEnbjCode());
				log.setServerName(stepChain.getServerName());
				log.setServerTele(stepChain.getServerTele());
			}
			substanceLogs.add(log);
		}

		//==========================================
		if (cRowData != null) {
			converter = converterMap.get(2);
			beanDatas = excelDatas.getSheetDatas(7);
			formatBeanData(beanDatas);
			Object fData = handleValidData(cRowData, rowIndex);
			String[] groupDataRow = genArray(5);
			//			try {
			//				Organization temp = (Organization) getProperty(fData, "target");
			//				Organization dealOrg = organizationDubboRemoteService.getFullOrgById(temp.getId());
			//				addComment(id, fData, dealOrg.getOrgName(), stepLogs);
			//			} catch (IllegalAccessException e) {
			//				e.printStackTrace();
			//			} catch (InvocationTargetException e) {
			//				e.printStackTrace();
			//			} catch (NoSuchMethodException e) {
			//				e.printStackTrace();
			//			}

			List<StepLog> villageLogs = new ArrayList<StepLog>();

			boolean notFindLog = true;
			for (StepLog _log : logs) {
				if (_log.getUnit().equals(cRowData[1])) {
					if (StringUtils.isNotBlank(_log.getContent())
							&& _log.getContent().matches(".*呈报至.*镇.*")) {
						_log.setTargetOrg(cRowData[2]);
						villageLogs.add(_log);
						notFindLog = false;
						break;
					}
				}
			}
			if (notFindLog) {
				StepLog log = new StepLog();
				log.setRegCardCode(stepChain.getRegCardCode());
				log.setContent(stepChain.getcContent());
				log.setWorkDate(stepChain.getcDate() != null ? stepChain.getcDate() : stepChain
						.getSzDate());
				if (log.getWorkDate() == null) {
					log.setWorkDate(stepChain.getzDate());
				}
				if (log.getWorkDate() == null) {
					log.setWorkDate(createDate != null ? DateUtil.formateYMDHMS(createDate) : null);
				}
				if (log.getWorkDate() == null) {
					throw new BusinessValidationException("log.getWorkDate()不能为空");
				}
				log.setUnit(stepChain.getRegUnit());
				log.setTargetOrg(cRowData[2]);
				log.setServerName(stepChain.getServerName());
				log.setServerTele(stepChain.getServerTele());
				villageLogs.add(log);
			}
			handleLog(id, fData, villageLogs, groupDataRow, false, cDesc);
			// 追加其他log
			if (StringUtil.isStringAvaliable(stepChain.getcDate()) && zRowData != null) {
				List<StepLog> commetLogs = new ArrayList<StepLog>();
				if (!cRowData[1].equals(cRowData[2])) {
					for (StepLog _log : logs) {
						if (_log.getUnit().equals(cRowData[2])
								&& !(StringUtils.isNotBlank(_log.getContent()) && _log.getContent()
								.contains("呈报至"))) {
							_log.setDealType(String.valueOf(ThreeRecordsIssueOperate.COMMENT_CODE));
							commetLogs.add(_log);
						}
					}
				}
				this.addComment(id, fData, cRowData[2], commetLogs);
			}
		}
		if (zRowData != null) {
			converter = converterMap.get(2);
			beanDatas = excelDatas.getSheetDatas(7);
			formatBeanData(beanDatas);
			Object fData = handleValidData(zRowData, rowIndex);
			String[] groupDataRow = genArray(5);
			//			try {
			//				Organization temp = (Organization) getProperty(fData, "target");
			//				Organization dealOrg = organizationDubboRemoteService.getFullOrgById(temp.getId());
			//				addComment(id, fData, dealOrg.getOrgName(), stepLogs);
			//			} catch (IllegalAccessException e) {
			//				e.printStackTrace();
			//			} catch (InvocationTargetException e) {
			//				e.printStackTrace();
			//			} catch (NoSuchMethodException e) {
			//				e.printStackTrace();
			//			}
			//			if (stepChain.getzDate() != null && sRowData == null) {有三本台账办数据时sRowData == null导致镇步骤数据不能加入

			List<StepLog> townLogs = new ArrayList<StepLog>();

			boolean notFindLog = true;
			for (StepLog _log : logs) {
				if (_log.getUnit().equals(zRowData[1])) {
					if (StringUtils.isNotBlank(_log.getContent())
							&& _log.getContent().matches(".*呈报至.*台账办.*")) {
						_log.setTargetOrg(zRowData[2]);
						townLogs.add(_log);
						notFindLog = false;
						break;
					}
				}
			}
			if (notFindLog) {
				StepLog log = new StepLog();
				log.setRegCardCode(stepChain.getRegCardCode());
				log.setContent(stepChain.getzContent());
				log.setWorkDate(stepChain.getzDate() != null ? stepChain.getzDate() : stepChain
						.getSzDate());
				if (log.getWorkDate() == null) {
					log.setWorkDate(createDate != null ? DateUtil.formateYMDHMS(createDate) : null);
				}
				if (log.getWorkDate() == null) {
					throw new BusinessValidationException("log.getWorkDate()不能为空");
				}
				log.setUnit(cRowData == null ? stepChain.getRegUnit() : getParentUnitName(stepChain
						.getRegUnit()));
				log.setTargetOrg(zRowData[2]);
				log.setServerName(stepChain.getServerName());
				log.setServerTele(stepChain.getServerTele());
				townLogs.add(log);
			}
			handleLog(id, fData, townLogs, groupDataRow, false, zDesc);
			// 追加其他log
			if (stepChain.getzDate() != null && sbRowData != null) {
				List<StepLog> commetLogs = new ArrayList<StepLog>();
				for (StepLog _log : logs) {
					if (_log.getUnit().equals(zRowData[2])
							&& !(StringUtils.isNotBlank(_log.getContent()) && _log.getContent()
							.contains("呈报至"))) {
						_log.setDealType(String.valueOf(ThreeRecordsIssueOperate.COMMENT_CODE));
						commetLogs.add(_log);
					}
				}
				this.addComment(id, fData, zRowData[2], commetLogs);
			}

		}
		//		if (sRowData != null) {
		//			converter = converterMap.get(2);
		//			beanDatas = excelDatas.getSheetDatas(7);
		//			formatBeanData(beanDatas);
		//			Object fData = handleValidData(sRowData, rowIndex);
		//			String[] groupDataRow = genArray(5);
		//			boolean hasSubmitToLedgerLog = false;
		//			for (StepLog log : logs) {
		//				if (log.getType() == 2 && log.getUnit().equals(sRowData[1])) {
		//					List<StepLog> townLogs = new ArrayList<StepLog>();
		//					StepLog ledgerLog = new StepLog();
		//					ledgerLog.setRegCardCode(stepChain.getRegCardCode());
		//					ledgerLog.setContent(log.getContent());
		//					ledgerLog.setWorkDate(log.getWorkDate());
		//					ledgerLog.setUnit(sRowData[1]);
		//					ledgerLog.setServerName(stepChain.getServerName());
		//					ledgerLog.setServerTele(stepChain.getServerTele());
		//					townLogs.add(ledgerLog);
		//					handleLog(id, fData, townLogs, groupDataRow, false, "呈报" + sRowData[2]);
		//					hasSubmitToLedgerLog = true;
		//					break;
		//				}
		//			}
		//			if (!hasSubmitToLedgerLog) {
		//				List<StepLog> townLogs = new ArrayList<StepLog>();
		//				StepLog ledgerLog = new StepLog();
		//				ledgerLog.setRegCardCode(stepChain.getRegCardCode());
		//				ledgerLog.setContent(stepChain.getzContent());
		//				ledgerLog.setWorkDate(stepChain.getzDate());
		//				ledgerLog.setUnit(sRowData[1]);
		//				ledgerLog.setServerName(stepChain.getServerName());
		//				ledgerLog.setServerTele(stepChain.getServerTele());
		//				townLogs.add(ledgerLog);
		//				handleLog(id, fData, townLogs, groupDataRow, false, "呈报" + sRowData[2]);
		//			}
		//			try {
		//				Organization temp = (Organization) getProperty(fData, "target");
		//				Organization dealOrg = organizationDubboRemoteService.getFullOrgById(temp.getId());
		//				addComment(id, fData, dealOrg.getOrgName(), stepLogs);
		//			} catch (IllegalAccessException e) {
		//				e.printStackTrace();
		//			} catch (InvocationTargetException e) {
		//				e.printStackTrace();
		//			} catch (NoSuchMethodException e) {
		//				e.printStackTrace();
		//			}
		//
		//		}
		if (sbRowData != null) {
			converter = converterMap.get(2);
			beanDatas = excelDatas.getSheetDatas(7);
			formatBeanData(beanDatas);
			Object fData = handleValidData(sbRowData, rowIndex);
			String[] groupDataRow = genArray(5);
			List<StepLog> functionLogs = new ArrayList<StepLog>();

			for (StepLog stepLog : logs) {
				if (StringUtils.isNotBlank(stepLog.getWorkStep())
						&& stepLog.getWorkStep().trim().matches("县台账办转办到.*局")) {
					StepLog log = new StepLog();
					log.setRegCardCode(stepChain.getRegCardCode());
					log.setContent(stepLog.getContent());
					log.setWorkDate(stepLog.getWorkDate());
					log.setUnit(stepLog.getUnit());
					log.setTargetOrg(sbRowData[2]);
					log.setServerName(stepLog.getServerName());
					log.setServerTele(stepLog.getServerTele());
					functionLogs.add(log);

					handleLog(id, fData, functionLogs, groupDataRow, false, "转办");
					// 追加其他log
					List<StepLog> commetLogs = new ArrayList<StepLog>();
					for (StepLog _log : logs) {
						if (_log.getUnit().equals(sbRowData[2])
								&& !(StringUtils.isNotBlank(_log.getContent()) && _log.getContent()
								.contains("转办到"))) {
							_log.setDealType(String.valueOf(ThreeRecordsIssueOperate.COMMENT_CODE));
							commetLogs.add(_log);
						}
					}
					this.addComment(id, fData, sbRowData[2], commetLogs);
					break;
				}
			}

		}
		if (bRowData != null) {
			converter = converterMap.get(2);
			beanDatas = excelDatas.getSheetDatas(7);
			formatBeanData(beanDatas);
			Object fData = handleValidData(bRowData, rowIndex);
			String[] groupDataRow = genArray(5);
			try {
				Organization temp = (Organization) getProperty(fData, "target");
				Organization dealOrg = organizationDubboRemoteService
						.getSimpleOrgById(temp.getId());
				addComment(id, fData, dealOrg.getOrgName(), stepLogs);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			if (stepChain.getbDate() != null) {
				List<StepLog> functionLogs = new ArrayList<StepLog>();
				StepLog log = new StepLog();
				log.setRegCardCode(stepChain.getRegCardCode());
				log.setContent(stepChain.getbContent());
				log.setWorkDate(stepChain.getbDate());
				log.setUnit(bRowData[1]);
				log.setTargetOrg(bRowData[2]);
				log.setServerName(stepChain.getServerName());
				log.setServerTele(stepChain.getServerTele());
				functionLogs.add(log);
				handleLog(id, fData, functionLogs, groupDataRow, false, bDesc);
			}
		}

		if (_bRowData != null) {
			converter = converterMap.get(2);
			beanDatas = excelDatas.getSheetDatas(7);
			formatBeanData(beanDatas);
			Object fData = handleValidData(_bRowData, rowIndex);
			String[] groupDataRow = genArray(5);
			if (blogs != null) {
				handleLog(id, fData, blogs, groupDataRow, false, "已办结");
			}
		}
		if (_zRowData != null) {
			converter = converterMap.get(2);
			beanDatas = excelDatas.getSheetDatas(7);
			formatBeanData(beanDatas);
			Object fData = handleValidData(_zRowData, rowIndex);
			String[] groupDataRow = genArray(5);
			if (zlogs != null) {
				handleLog(id, fData, zlogs, groupDataRow, false, "交办");
			}
		}
		if (_cRowData != null) {
			converter = converterMap.get(2);
			beanDatas = excelDatas.getSheetDatas(7);
			formatBeanData(beanDatas);
			Object fData = handleValidData(_cRowData, rowIndex);
			String[] groupDataRow = genArray(5);
			if (clogs != null) {
				handleLog(id, fData, clogs, groupDataRow, false, "交办");
			}
		}

		if (szRowData != null) {
			if (substanceLogs != null && substanceLogs.size() != 0) {
				converter = converterMap.get(2);
				beanDatas = excelDatas.getSheetDatas(7);
				formatBeanData(beanDatas);
				Object fData = handleValidData(szRowData, rowIndex);
				String[] groupDataRow = genArray(5);
				handleLog(id, fData, substanceLogs, groupDataRow, true, szDesc);

				setThreadLocal(ImportConst.THREADLOCAL_ISSUBSTANCE, "true");
			}
		}
		if ("true".equals(getThreadLocal(ImportConst.THREADLOCAL_ISSUBSTANCE))) {
			converter = converterMap.get(0);
			if (DataTransferConstants.POOR_PEOPLE_DATA.equals(dataType)) {
				LedgerPoorPeople poorPeople = new LedgerPoorPeople();
				poorPeople.setStatus(ThreeRecordsIssueState.COMPLETE);
				poorPeople.setId(Long.valueOf(id));
				converter.updateDomain(poorPeople);
			} else if (DataTransferConstants.STEADY_WORK_DATA.equals(dataType)) {
				LedgerSteadyWork ledgerSteadyWork = new LedgerSteadyWork();
				ledgerSteadyWork.setStatus(ThreeRecordsIssueState.COMPLETE);
				ledgerSteadyWork.setId(Long.valueOf(id));
				converter.updateDomain(ledgerSteadyWork);
			} else {
				LedgerPeopleAspirations peopleAspiration = new LedgerPeopleAspirations();
				peopleAspiration.setStatus(ThreeRecordsIssueState.COMPLETE);
				peopleAspiration.setId(Long.valueOf(id));
				converter.updateDomain(peopleAspiration);
			}
		}
		if (StringUtil.isStringAvaliable(id) && StringUtil.isStringAvaliable(dataType)) {
			converter = converterMap.get(2);
			ThreeRecordsIssueStep step = new ThreeRecordsIssueStep();
			step.setLedgerId(Long.valueOf(id));
			step.setLedgerType(DataConvertDefine.getType(dataType));
			converter.updateDomain(step);
		}

	}

	/**
	 * 根据excel中定义的单位查找(省级以下)
	 *
	 * @param unitName
	 * @return
	 */
	private Organization getOrgByName(String unitName) {
		return Util.getOrgByName(organizationDubboRemoteService, headDatas, unitName);
	}

	/**
	 * 根据excel中定义的单位查找(省级以下)
	 *
	 * @param unitName
	 * @return
	 */
	private String getParentUnitName(String unitName) {
		Organization nowOrg = this.getOrgByName(unitName);
		if (nowOrg == null) {
			return null;
		}
		String result = organizationDubboRemoteService.getSimpleOrgById(
				nowOrg.getParentOrg().getId()).getOrgName();
		return result;
		//		if (unitName.equals("东北镇迎泉村") || unitName.equals("东北镇白梨村")) {
		//			return "东北镇";
		//		}
		//		if (unitName.equals("南华镇双石村") || unitName.equals("南渡社区居民委员会")) {
		//			return "南华镇";
		//		}
		//		if (unitName.equals("富兴镇富强村") || unitName.equals("富民社区居委会")) {
		//			return "富兴镇";
		//		}
		//		if (unitName.equals("朝阳东路社区居委会") || unitName.equals("小南街社区居委会")) {
		//			return "凯江镇";
		//		}
		//		if (unitName.equals("凯江镇") || unitName.equals("南华镇") || unitName.equals("富兴镇")
		//				|| unitName.equals("东北镇")) {
		//			return unitName;
		//		}
		//		return null;
	}

	private String[] genArray(int num) {
		String[] arr = new String[num];
		for (int i = 0; i < num; i++) {
			arr[i] = "";
		}
		return arr;
	}

	public void handleLog(String id, Object fData, List<StepLog> stepLogs, String[] groupRowData,
						  boolean isLast, String logDesc) {
		try {
			String stepId = getProperty(fData, "id").toString();
			Organization temp = (Organization) getProperty(fData, "target");
			Organization dealOrg = organizationDubboRemoteService.getFullOrgById(temp.getId());
			if (stepId != null && stepId != "" && stepLogs.size() != 0) {
				handleData(id, fData, dealOrg.getOrgName(), stepLogs, groupRowData, isLast, logDesc);

				handleForm(fData, stepLogs, stepId);
			}

		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 单据处理
	 *
	 * @param fData
	 * @param stepLogs
	 * @param stepId
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private void handleForm(Object fData, List<StepLog> stepLogs, String stepId)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Organization sourceOrg = (Organization) getProperty(fData, "source");
		sourceOrg = organizationDubboRemoteService.getFullOrgById(sourceOrg.getId());
		Organization targetOrg = (Organization) getProperty(fData, "target");
		targetOrg = organizationDubboRemoteService.getFullOrgById(targetOrg.getId());
		Long ledgerId = Long.parseLong(getProperty(fData, "ledgerId").toString());
		Long ledgerType = Long.parseLong(getProperty(fData, "ledgerType").toString());
		if ((sourceOrg.getOrgLevel().getInternalId() == OrganizationLevel.VILLAGE && targetOrg
				.getOrgLevel().getInternalId() == OrganizationLevel.TOWN)
				|| (sourceOrg.getOrgLevel().getInternalId() == OrganizationLevel.TOWN && targetOrg
				.getDepartmentNo().endsWith(DEPARTMENT_SUFFIX_COUNTY_LEDGER))) {
			handleReportForm(stepLogs, stepId, sourceOrg, targetOrg, ledgerId, ledgerType);
		} else if (sourceOrg.getOrgLevel().getInternalId() == OrganizationLevel.DISTRICT
				&& targetOrg.getOrgLevel().getInternalId() == OrganizationLevel.DISTRICT) {
			if (sourceOrg.getDepartmentNo().endsWith(DEPARTMENT_SUFFIX_COUNTY_LEDGER)
					&& isGeneralIntelligenceDepartment(targetOrg)) {
				handleTurnForm(stepLogs, stepId, targetOrg, ledgerId, ledgerType);
			} else if (isGeneralIntelligenceDepartment(sourceOrg)
					&& targetOrg.getDepartmentNo().endsWith(DEPARTMENT_SUFFIX_COUNTY_LEDGER)) {
				handleReplyForm(stepLogs, stepId, sourceOrg, targetOrg, ledgerId, ledgerType);
			} else if (isGeneralIntelligenceDepartment(sourceOrg)
					&& (targetOrg.getDepartmentNo().endsWith(DEPARTMENT_SUFFIX_COUNTY_JOINT) || targetOrg
					.getDepartmentNo().endsWith(DEPARTMENT_SUFFIX_COUNTY_COMMITTEE))) {
				handleDeclareForm(stepLogs, stepId, sourceOrg, targetOrg, ledgerId, ledgerType);
			} else if ((sourceOrg.getDepartmentNo().endsWith(DEPARTMENT_SUFFIX_COUNTY_JOINT) || sourceOrg
					.getDepartmentNo().endsWith(DEPARTMENT_SUFFIX_COUNTY_COMMITTEE))
					&& isGeneralIntelligenceDepartment(targetOrg)) {
				handleAssignForm(stepLogs, stepId, sourceOrg, targetOrg, ledgerId, ledgerType);
			}
		}
	}

	/**
	 * 呈报单处理
	 *
	 * @param stepLogs
	 * @param stepId
	 * @param sourceOrg
	 * @param targetOrg
	 * @param ledgerId
	 * @param ledgerType
	 */
	private void handleReportForm(List<StepLog> stepLogs, String stepId, Organization sourceOrg,
								  Organization targetOrg, Long ledgerId, Long ledgerType) {
		// 呈报单（乡到镇、镇到县）
		String[][] reportFormDatas = excelDatas.getSheetDatas(12);
		if (reportFormDatas == null) {
			return;
		}
		for (int i = 1; i < reportFormDatas.length; i++) {
			String[] rowData = reportFormDatas[i];
			if (stepLogs.get(0).getRegCardCode().equals(rowData[1])) {
				if (sourceOrg.getOrgName().equals(rowData[3])
						&& targetOrg.getOrgName().equals(rowData[4])) {
					ReportForm reportForm = new ReportForm();
					reportForm.setLedgerId(ledgerId);
					reportForm.setLedgerType(ledgerType);
					reportForm.setStepId(Long.parseLong(stepId));
					Date reportDate = this.parseDate(rowData[2]);
					reportForm.setSerialNumber(serialKeyGenerator.getNextFormKey(
							this.ledgerSerialNumber.substring(0, 2), LedgerConstants.REPORT_FORM,
							reportDate));
					reportForm.setLedgerSerialNumber(this.ledgerSerialNumber);
					reportForm.setSourceOrg(sourceOrg);
					reportForm.setTargetOrg(targetOrg);
					reportForm.setReason(rowData[5]);
					reportForm.setHandleContent(rowData[6]);
					reportForm.setName(rowData[8]);
					reportForm.setMobile(rowData[9]);
					reportForm.setReportDate(reportDate);
					reportFormDubboService.addReportForm(reportForm);
					break;
				}
			}
		}
	}

	/**
	 * 转办单处理
	 *
	 * @param stepLogs
	 * @param stepId
	 * @param targetOrg
	 * @param ledgerId
	 * @param ledgerType
	 */
	private void handleTurnForm(List<StepLog> stepLogs, String stepId, Organization targetOrg,
								Long ledgerId, Long ledgerType) {
		// 转办单数据
		String[][] reportFormDatas = excelDatas.getSheetDatas(13);
		if (reportFormDatas == null) {
			return;
		}
		for (int i = 1; i < reportFormDatas.length; i++) {
			String[] rowData = reportFormDatas[i];
			if (stepLogs.get(0).getRegCardCode().equals(rowData[1])) {
				if (targetOrg.getOrgName().equals(rowData[4])) {
					TurnForm turnForm = new TurnForm();
					turnForm.setLedgerId(ledgerId);
					turnForm.setLedgerType(ledgerType);
					turnForm.setStepId(Long.parseLong(stepId));
					turnForm.setTargetOrg(targetOrg);
					turnForm.setReason(rowData[5]);
					Date turnDate = this.parseDate(rowData[2]);
					turnForm.setSerialNumber(serialKeyGenerator.getNextFormKey(
							this.ledgerSerialNumber.substring(0, 2), LedgerConstants.TURN_FORM,
							turnDate));
					turnForm.setLedgerSerialNumber(this.ledgerSerialNumber);
					turnForm.setName(rowData[12]);
					turnForm.setMobile(rowData[13]);
					turnForm.setManager(rowData[6]);
					turnForm.setSubOpinion(rowData[9]);
					turnForm.setOpinion(rowData[10]);
					turnForm.setHandleStartDate(this.parseDate(rowData[7]));
					turnForm.setHandleEndDate(this.parseDate(rowData[8]));
					turnForm.setReceiveDate(this.parseDate(rowData[11]));
					turnForm.setHandleResult(rowData[14]);
					turnFormDubboService.addTurnForm(turnForm);
					break;
				}
			}
		}
	}

	/**
	 * 回复单处理
	 *
	 * @param stepLogs
	 * @param stepId
	 * @param sourceOrg
	 * @param targetOrg
	 * @param ledgerId
	 * @param ledgerType
	 */
	private void handleReplyForm(List<StepLog> stepLogs, String stepId, Organization sourceOrg,
								 Organization targetOrg, Long ledgerId, Long ledgerType) {
		// 回复单数据
		String[][] reportFormDatas = excelDatas.getSheetDatas(14);
		if (reportFormDatas == null) {
			return;
		}
		for (int i = 1; i < reportFormDatas.length; i++) {
			String[] rowData = reportFormDatas[i];
			if (stepLogs.get(0).getRegCardCode().equals(rowData[0])) {
				if (sourceOrg.getOrgName().equals(rowData[3])) {
					ReplyForm replyForm = new ReplyForm();
					replyForm.setLedgerId(ledgerId);
					replyForm.setLedgerType(ledgerType);
					replyForm.setStepId(Long.parseLong(stepId));
					Date replyDate = this.parseDate(rowData[1]);
					replyForm.setSerialNumber(serialKeyGenerator.getNextFormKey(
							this.ledgerSerialNumber.substring(0, 2), LedgerConstants.REPLY_FORM,
							replyDate));
					replyForm.setLedgerSerialNumber(this.ledgerSerialNumber);
					replyForm.setSourceOrg(sourceOrg);
					replyForm.setTargetOrg(targetOrg);
					replyForm.setReason(rowData[5]);
					replyForm.setHandleContent(rowData[6]);
					replyForm.setReplyDate(replyDate);
					replyForm.setName(rowData[7]);
					replyForm.setMobile(rowData[8]);
					replyFormDubboService.addReplyForm(replyForm);
					break;
				}
			}
		}
	}

	/**
	 * 申报单处理
	 *
	 * @param stepLogs
	 * @param stepId
	 * @param sourceOrg
	 * @param targetOrg
	 * @param ledgerId
	 * @param ledgerType
	 */
	private void handleDeclareForm(List<StepLog> stepLogs, String stepId, Organization sourceOrg,
								   Organization targetOrg, Long ledgerId, Long ledgerType) {
		// 申报单数据
		String[][] reportFormDatas = excelDatas.getSheetDatas(15);
		if (reportFormDatas == null) {
			return;
		}
		for (int i = 1; i < reportFormDatas.length; i++) {
			String[] rowData = reportFormDatas[i];
			if (stepLogs.get(0).getRegCardCode().equals(rowData[1])) {
				if (sourceOrg.getOrgName().equals(rowData[3])
						&& !this.isGeneralIntelligenceDepartment(targetOrg)) {
					DeclareForm declareForm = new DeclareForm();
					declareForm.setLedgerId(ledgerId);
					declareForm.setLedgerType(ledgerType);
					declareForm.setStepId(Long.parseLong(stepId));
					Date date = this.parseDate(rowData[2]);
					declareForm.setSerialNumber(serialKeyGenerator.getNextFormKey(
							this.ledgerSerialNumber.substring(0, 2), LedgerConstants.DECLARE_FORM,
							date));
					declareForm.setLedgerSerialNumber(this.ledgerSerialNumber);
					declareForm.setSourceOrg(sourceOrg);
					declareForm.setTargetOrg(targetOrg);
					declareForm.setReason(rowData[5]);
					declareForm.setHandleContent(rowData[6]);
					declareForm.setName(rowData[9]);
					declareForm.setMobile(rowData[10]);
					declareForm.setLedgerHandleContent(rowData[7]);
					declareForm.setJointContent(rowData[8]);
					declareForm.setDeclareDate(date);
					declareFormDubboService.addDeclareForm(declareForm);
					break;
				}
			}
		}
	}

	/**
	 * 交办单处理
	 *
	 * @param stepLogs
	 * @param stepId
	 * @param sourceOrg
	 * @param targetOrg
	 * @param ledgerId
	 * @param ledgerType
	 */
	private void handleAssignForm(List<StepLog> stepLogs, String stepId, Organization sourceOrg,
								  Organization targetOrg, Long ledgerId, Long ledgerType) {
		// 交办单数据
		String[][] reportFormDatas = excelDatas.getSheetDatas(16);
		if (reportFormDatas == null) {
			return;
		}
		for (int i = 1; i < reportFormDatas.length; i++) {
			String[] rowData = reportFormDatas[i];
			if (stepLogs.get(0).getRegCardCode().equals(rowData[1])) {
				if (sourceOrg.getOrgName().equals(rowData[3])
						&& !this.isGeneralIntelligenceDepartment(targetOrg)) {
					AssignForm assignForm = new AssignForm();
					assignForm.setLedgerId(ledgerId);
					assignForm.setLedgerType(ledgerType);
					assignForm.setStepId(Long.parseLong(stepId));
					Date date = this.parseDate(rowData[2]);
					assignForm.setSerialNumber(serialKeyGenerator.getNextFormKey(
							this.ledgerSerialNumber.substring(0, 2), LedgerConstants.ASSGIN_FORM,
							date));
					assignForm.setLedgerSerialNumber(this.ledgerSerialNumber);
					assignForm.setSourceOrg(sourceOrg);
					assignForm.setReason(rowData[6]);
					assignForm.setHandleContent(rowData[7]);
					assignForm.setHandleStartDate(this.parseDate(rowData[9]));
					assignForm.setHandleEndDate(this.parseDate(rowData[10]));
					if (sourceOrg.getDepartmentNo().endsWith(DEPARTMENT_SUFFIX_COUNTY_COMMITTEE)) {
						assignForm.setAssignType(LedgerConstants.COUNTY_COMMITTEE_ASSIGN_TYPE);
					} else if (sourceOrg.getDepartmentNo().endsWith(DEPARTMENT_SUFFIX_COUNTY_JOINT)) {
						assignForm.setAssignType(LedgerConstants.COUNTY_JOINT_ASSIGN_TYPE);
					} else {
						assignForm.setAssignType(LedgerConstants.COUNTY_NO_ASSIGN_TYPE);
					}
					AssignFormReceiver assignFormReceiver = new AssignFormReceiver();
					assignFormReceiver.setTargetOrg(targetOrg);
					assignFormReceiver.setStepId(Long.parseLong(stepId));
					assignFormReceiver.setName(rowData[12]);
					assignFormReceiver.setMobile(rowData[13]);
					assignFormReceiver.setReceiveDate(this.parseDate(rowData[11]));
					assignFormReceiver.setManager(rowData[8]);
					assignFormReceiver.setIsManage(true);

					List<AssignFormReceiver> receivers = new ArrayList<AssignFormReceiver>();
					receivers.add(assignFormReceiver);
					assignForm.setReceivers(receivers);

					assignFormDubboService.addAssignForm(assignForm);
					break;
				}
			}
		}
	}

	private Date parseDate(String date) {
		try {
			return DateUtils.parseDate(date, "yyyy-MM-dd HH:mm:ss a", "yyyy-MM-dd HH:mm:ss",
					"yyyy-MM-dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 是否是普通智能部门：排除掉 台账办  1jg  县联席会议  1lx  县委县政府 1xw
	 *
	 * @param org
	 * @return
	 */
	private boolean isGeneralIntelligenceDepartment(Organization org) {
		return !(org.getDepartmentNo().endsWith(DEPARTMENT_SUFFIX_COUNTY_JOINT)
				|| org.getDepartmentNo().endsWith(DEPARTMENT_SUFFIX_COUNTY_LEDGER) || org
				.getDepartmentNo().endsWith(DEPARTMENT_SUFFIX_COUNTY_COMMITTEE));
	}

	/**
	 * 添加普通log
	 *
	 * @param id
	 * @param stepData
	 * @param dealOrg
	 * @param stepLogs
	 */
	private void addComment(String id, Object stepData, String dealOrg, List<StepLog> stepLogs) {

		converter = converterMap.get(3);
		beanDatas = excelDatas.getSheetDatas(9);
		formatBeanData(beanDatas);
		String stepId = "";
		try {
			stepId = getProperty(stepData, "id").toString();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		if (!StringUtil.isStringAvaliable(stepId)) {
			return;
		}
		String[] log;
		if (stepLogs == null || stepLogs.size() == 0)
			return;
		for (int i = 0; i < stepLogs.size(); i++) {
			//			if (dealOrg.equals(stepLogs.get(i).getUnit()) && stepLogs.get(i).getType() == 1) {
			if (dealOrg.equals(stepLogs.get(i).getUnit()) && stepLogs.get(i).getType() != 4) {
				log = genArray(13);
				log[0] = id;
				log[1] = stepId;
				log[2] = stepLogs.get(i).getUnit();
				log[3] = String.valueOf(ThreeRecordsIssueOperate.COMMENT.getCode());
				log[7] = "办理中";
				log[4] = stepLogs.get(i).getContent();
				log[5] = StringUtils.isBlank(stepLogs.get(i).getServerName()) ? "无" : stepLogs.get(
						i).getServerName();
				log[6] = stepLogs.get(i).getServerTele();
				if (log[6] != null && log[6].length() > 15) {
					log[6] = log[6].substring(0, 15);
				}
				log[8] = stepLogs.get(i).getUnit();
				log[9] = getType(dataType);
				log[10] = stepLogs.get(i).getWorkDate();
				log[11] = stepLogs.get(i).getWorkDate();
				handleValidData(log, i);
			}
		}

	}

	/**
	 * 导入工作日志数据
	 *
	 * @param id
	 * @param stepId
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private void handleData(String id, Object stepData, String dealOrg, List<StepLog> stepLogs,
							String[] groupRowData, boolean isLast, String logDesc) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		int j = 0, k = 0;
		String stepLogId = "";
		String stepId = getProperty(stepData, "id").toString();
		String[] log;
		if (stepLogs == null || stepLogs.size() == 0)
			return;
		boolean hasLog = false;
		for (int i = 0; i < stepLogs.size(); i++) {
			StepLog _log = stepLogs.get(i);
			if (_log.getWorkDate() == null) {
				throw new BusinessValidationException("日志日期不能为空");
			}
			if (StringUtils.isBlank(_log.getServerName())) {
				_log.setServerName("无");
			}
			if (_log.getServerTele() != null && _log.getServerTele().length() > 15) {
				_log.setServerTele(_log.getServerTele().substring(0, 15));
			}
			converter = converterMap.get(3);
			beanDatas = excelDatas.getSheetDatas(9);
			formatBeanData(beanDatas);
			// if (stepLogs.get(i).getUnit().equals(dealOrg)
			// && stepLogs.get(i).getType() != 1) {
			log = genArray(13);
			log[0] = id;
			log[1] = stepId;
			log[2] = _log.getUnit();
			//			log[3] = getProperty(stepData, "dealType") == null ? "1" : getProperty(stepData,
			//					"dealType").toString();
			if (StringUtils.isBlank(_log.getDealType())) {
				String dealType = getProperty(stepData, "dealType") == null ? "1" : getProperty(
						stepData, "dealType").toString();
				if (StringUtils.isNotBlank(dealType)) {
					log[3] = dealType;
				} else if (StringUtils.isBlank(log[3])) {
					log[3] = "1";
				}
			} else {
				log[3] = _log.getDealType();
			}
			log[7] = "办理中";
			//			if (!isLast) {
			//				if (log[3].equals(String.valueOf(ThreeRecordsIssueOperate.COMPLETE.getCode()))) {
			//					log[3] = String.valueOf(ThreeRecordsIssueOperate.PROGRAM_COMPLETE.getCode());
			//				}
			//			}
			if (i == stepLogs.size() - 1) {
				//			if (i == 0) {// 步骤log是第一条数据
				log[7] = DataConvertDefine.getLogDesc(logDesc);
				log[12] = DataConvertDefine.getCompeletedCode(logDesc);
			}
			log[4] = _log.getContent();
			log[5] = _log.getServerName();
			log[6] = _log.getServerTele();
			log[8] = StringUtils.isNotBlank(_log.getTargetOrg()) ? _log.getTargetOrg() : _log
					.getUnit();// target org
			log[9] = getType(dataType);
			log[10] = _log.getWorkDate();
			log[11] = _log.getWorkDate();

			Object fData = handleValidData(log, i);
			try {
				stepLogId = getProperty(fData, "id").toString();
				if (stepLogId != null && stepLogId != "") {
					hasLog = true;
					if (j == 0) {
						groupRowData[0] = id;
						groupRowData[1] = stepId;
						groupRowData[2] = stepLogId;
						groupRowData[4] = getType(dataType);
					}
					j++;
					k = i;
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		// }
		if (!hasLog) {
			return;
		}
		groupRowData[3] = stepLogId;
		converter = converterMap.get(4);
		beanDatas = excelDatas.getSheetDatas(11);
		formatBeanData(beanDatas);
		Object group = handleValidData(groupRowData, k);
		if (group != null) {
			updateOutLog(group);
			setProperty(stepData, "groupId", getProperty(group, "id"));
			updateGroupId(stepData);
		}
	}

	private String getPercent(int x, int total) {
		NumberFormat format = NumberFormat.getPercentInstance();// 获取格式化类实例
		format.setMinimumFractionDigits(2);// 设置小数位
		return format.format((float) x / (float) total);
	}

	/**
	 * 处理有效数据将rowData转换为规范化的数据对象
	 *
	 * @param rowData  原始数据
	 * @param rowIndex 数据行数
	 */
	private Object handleValidData(String[] rowData, int rowIndex) {
		Object domain;
		ValidateResult result = new ValidateResult();
		try {
			formatBeanData(beanDatas);
			if (converter.equals(converterMap.get(0))) {
				validateExcelHeader.validateExcelHeader(headDatas, rowData, 1);
				initOrg = validateExcelHeader.getHeaderOrg();
			} else if (converter.equals(converterMap.get(2))) {
				validateExcelHeader.validateExcelHeader(headDatas, rowData, 2);
			} else if (converter.equals(converterMap.get(3))) {
				validateExcelHeader.validateExcelHeader(headDatas, rowData, 2);
			}

			headerOrg = validateExcelHeader.getHeaderOrg();
			domain = converter.convertToDomain(rowData, result, beanDatas, headerOrg);
			try {
				// 联系电话错误置空
				String mobileNumber = (String) getProperty(domain, "mobileNumber");
				if (mobileNumber != null && mobileNumber.length() > 15) {
					setProperty(domain, "mobileNumber", "");
				}
			} catch (Exception e1) {
			}
			if (domain instanceof BaseWorking) {
				BaseWorking b = (BaseWorking) domain;
				this.createThreadUser(b.getOrganization(), b.getCreateUser());
			}
			if (PropertyUtils.isWriteable(domain, "oldHistoryId")) {
				// account_soa : ThreeRecordsSavingOperationLog.java中会用做判断条件来设定步骤log的组织和名字
				setProperty(domain, "oldHistoryId", this.ledgerSerialNumberOld);
			}

			if (converter.equals(converterMap.get(0))) {
				Date createDate = (Date) getProperty(domain, "createDate");
				String prefix = "";
				if (DataTransferConstants.POOR_PEOPLE_DATA.equals(dataType)) {
					prefix = LedgerConstants.PRE_KEY_POOR_PEOPLE;
				} else if (DataTransferConstants.STEADY_WORK_DATA.equals(dataType)) {
					prefix = LedgerConstants.PRE_KEY_STEADY_WORK;
				} else if (DataTransferConstants.WATERRESOURCE_DATA.equals(dataType)) {
					prefix = LedgerConstants.peopleAsparitionPreKeyMap
							.get(LedgerConstants.PEOPLEASPIRATION_WATER);
				} else if (DataTransferConstants.TRAFFIC_DATA.equals(dataType)) {
					prefix = LedgerConstants.peopleAsparitionPreKeyMap
							.get(LedgerConstants.PEOPLEASPIRATION_TRAFFIC);
				} else if (DataTransferConstants.ENERGY_DATA.equals(dataType)) {
					prefix = LedgerConstants.peopleAsparitionPreKeyMap
							.get(LedgerConstants.PEOPLEASPIRATION_ENERGY);
				} else if (DataTransferConstants.EDUCATION_DATA.equals(dataType)) {
					prefix = LedgerConstants.peopleAsparitionPreKeyMap
							.get(LedgerConstants.PEOPLEASPIRATION_EDUCATION);
				} else if (DataTransferConstants.SCIENCE_DATA.equals(dataType)) {
					prefix = LedgerConstants.peopleAsparitionPreKeyMap
							.get(LedgerConstants.PEOPLEASPIRATION_SCIENCE);
				} else if (DataTransferConstants.MEDICAL_DATA.equals(dataType)) {
					prefix = LedgerConstants.peopleAsparitionPreKeyMap
							.get(LedgerConstants.PEOPLEASPIRATION_MEDICAL);
				} else if (DataTransferConstants.LABOR_DATA.equals(dataType)) {
					prefix = LedgerConstants.peopleAsparitionPreKeyMap
							.get(LedgerConstants.PEOPLEASPIRATION_LABOR);
				} else if (DataTransferConstants.ENVIRONMENT_DATA.equals(dataType)) {
					prefix = LedgerConstants.peopleAsparitionPreKeyMap
							.get(LedgerConstants.PEOPLEASPIRATION_ENVIRONMENT);
				} else if (DataTransferConstants.TOWN_DATA.equals(dataType)) {
					prefix = LedgerConstants.peopleAsparitionPreKeyMap
							.get(LedgerConstants.PEOPLEASPIRATION_TOWN);
				} else if (DataTransferConstants.AGRICULTURE_DATA.equals(dataType)) {
					prefix = LedgerConstants.peopleAsparitionPreKeyMap
							.get(LedgerConstants.PEOPLEASPIRATION_AGRICULTURE);
				} else if (DataTransferConstants.OTHER_DATA.equals(dataType)) {
					prefix = LedgerConstants.peopleAsparitionPreKeyMap
							.get(LedgerConstants.PEOPLEASPIRATION_OTHER);
				}
				String serialNumber = serialKeyGenerator.getNextKey(prefix, ThreadVariable
						.getOrganization().getId(), createDate);
				this.ledgerSerialNumber = serialNumber;
				setProperty(domain, "serialNumber", serialNumber);
			}
			headerOrg = validateExcelHeader.getHeaderOrg();

		} catch (Exception e) {
			updateErrorTitleAndRowMsg("导入数据时出错，程序已终止，详情参见下方错误信息列表", -1, "数据转换失败," + e.getMessage());
			logger.error("导入数据时出错，", e);
			isIntercept = true;
			return null;
		}
		if (result.getMapMessages().size() > 0) {
			validateConvertData(result, rowData, domain, rowIndex);
			vError = new ValidateResult();
			vError = result;
			return null;
		} else {
			// 处理数据时需要同步防止导入同步数据
			Object rt = validateRowData(result, domain, rowIndex, rowData);
			updateTicketErrorMsg(ticketId, result, TicketState.DOING);
			return rt;
		}
	}

	/**
	 * 通过convert验证数据具体错误
	 *
	 * @param result
	 * @param rowData  行数据
	 * @param domain   验证对象
	 * @param rowIndex 行顺序码
	 */
	private void validateConvertData(ValidateResult result, String[] rowData, Object domain,
									 int rowIndex) {
		ValidateResult vResult = null;
		try {
			vResult = converter.validate(domain, rowIndex + 1);
		} catch (Exception e) {
			logger.error("处理数据时发生错误！");
		}
		if (vResult != null && StringUtil.isStringAvaliable(vResult.getErrorMessages())) {
			result.getMapMessages().putAll(vResult.getMapMessages());
		}
		handleErrorData(result, rowData);

	}

	/**
	 * 数据验证,通过个性验证逻辑(dataConverter)进行验证
	 *
	 * @param result   验证结果
	 * @param domain   数据字典
	 * @param rowIndex 行顺序码
	 * @param rowData  行数据
	 */
	private Object validateRowData(ValidateResult result, Object domain, int rowIndex,
								   String[] rowData) {
		ValidateResult vResult = null;
		try {
			// fateson add 在验证是否是 户籍还是流动人口的时候用
			converter.setheaderOrg(headerOrg);
			vResult = converter.validate(domain, rowIndex + 1);
		} catch (Exception e) {
			logger.error("处理数据时发生错误！", e);
		}
		if (vResult == null || "".equals(vResult.getMapErrorMessages())) {
			Object returnData = handleCorrectData(domain);
			if (returnData == null) {
				SheetValidateResult svr = (SheetValidateResult) result;
				svr.addErrorMessage(currentSheetNum + 2, rowIndex + firstDataRow,
						"数据保存数据库时失败，请检查数据正确性！");
				handleErrorData(vResult, rowData);
				return null;
			}
			return returnData;
		} else {
			result.addAllErrorMessage(vResult);
			handleErrorData(result, rowData);
			return null;
		}
	}

	/**
	 * 处理单条数据导入
	 *
	 * @param domain 导入数据
	 * @return 生成数据
	 */
	private Object handleCorrectData(Object domain) {
		if (!converter.isRepeatData(domain)) {
			// fateson add 如果这里是国人对象的话那么 还需要 流动转户籍 户籍转流动操作
			if (isCountyMen(domain, converter)) {
				ThreeCountyDataConverterPoxy converterPoxy = createCountyDataConverterPoxy(converter);
				domain = converterPoxy.persistenceDomain(converter, (Countrymen) domain);
			} else {
				if (domain instanceof Countrymen) {
					String actualPopulationType = ((Countrymen) domain).getActualPopulationType();
					((Countrymen) domain)
							.setActualPopulationType("户籍人口".equals(actualPopulationType) ? PopulationType.HOUSEHOLD_STAFF
									: ("流动人口".equals(actualPopulationType) ? PopulationType.FLOATING_POPULATION
									: actualPopulationType));
				}
				domain = converter.persistenceDomain(domain);
			}
		} else {
			// fateson add 如果这里是国人对象的话那么 还需要 流动转户籍 户籍转流动操作
			if (isCountyMen(domain, converter)) {
				ThreeCountyDataConverterPoxy converterPoxy = createCountyDataConverterPoxy(converter);
				domain = converterPoxy.updateDomain(converter, (Countrymen) domain);
			} else {
				domain = converter.updateDomain(domain);
			}
		}
		return domain;
	}

	private Object updateGroupId(Object domain) {
		converter = converterMap.get(2);
		domain = converter.updateDomain(domain);
		return domain;
	}

	private void updateOutLog(Object group) {
		group = converter.updateDomain(group);
	}

	private ThreeCountyDataConverterPoxy createCountyDataConverterPoxy(ThreeDataConvert converter2) {
		ThreeCountyDataConverterPoxy converterPoxy = (ThreeCountyDataConverterPoxy) getDataConvert(
				"countyDataConverterPoxy", appContext);
		converterPoxy.setConvert(converter2);
		return converterPoxy;
	}

	private boolean isCountyMen(Object domain, ThreeDataConvert converter) {
		// 判断不是场所和房屋 既不是 流动 也不是户籍 数据管理中的人员也要排除
		if ((domain instanceof AttentionPopulation)
				&& !(converter instanceof AbstractTempDataConverter)) {
			return true;
		}
		return false;
	}

	/**
	 * 处理错误数据
	 *
	 * @param vResult
	 * @param rowData
	 */
	private void handleErrorData(ValidateResult vResult, String[] rowData) {
		validateErrorOccur = true;
		handleErrorPersistentData(vResult, rowData);
	}

	/**
	 * 创建错误日志文件,处理数据库执行sql时出错的错误信息
	 *
	 * @param vResult 验证结果
	 * @param rowData 数据行
	 */
	private void handleErrorPersistentData(ValidateResult vResult, String[] rowData) {

		if (ERROR_ROW_NUM * errorFileCount <= currentRowInErrorExcel - firstDataRow) {
			try {
				exportExcel.outFile(currentDealRow, currentDealRow);
				createExportExcel(exportMessageExcelName + errorFileCount);
				exportExcel.fullUnit(totalDatas, 2);
				errorFileCount++;
			} catch (Exception e) {
				logger.error("创建错误日志文件时发生错误！", e);
			}
		}

		exportExcel.addErrorMessage(vResult, rowData, currentRowInErrorExcel
				- (ERROR_ROW_NUM * (errorFileCount - 1)));
		currentRowInErrorExcel = currentRowInErrorExcel + 1;
	}

	private void createErrorExcelZip() {
		if (errorFileCount == 1) {
			return;
		}
		try {
			File outFile = exportExcel.createZipFile(uuid + ".zip");
			String fileNameTemp = outFile.getCanonicalPath();
			fileNameTemp = fileNameTemp.substring(0, fileNameTemp.length() - 4);
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFile));
			byte[] buffer = new byte[1024];
			out.setEncoding("gbk");
			for (int j = 0; j < errorFileCount; j++) {
				FileInputStream fis;
				if (j == 0) {
					fis = new FileInputStream((String) fileNameTemp + ".xls");
				} else {
					fis = new FileInputStream((String) fileNameTemp + "" + j + ".xls");
				}
				out.putNextEntry(new ZipEntry(BaseInfoTables.getTypeDisplayNames(dataType)
						+ "错误分析登记表(" + j + ").xls"));
				int len;
				while ((len = fis.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				out.closeEntry();
				fis.close();
			}
			out.close();
		} catch (FileNotFoundException e) {
			logger.error("错误日志文件没有找到错误！", e);
		} catch (IOException e) {
			logger.error("错误日志文件读写异常错误！", e);
		} catch (Exception e) {
			logger.error("压缩错误日志文件异常错误！", e);
		}
	}

	private void updateCompleteMsgTitle(String title) {
		updateTicketInfo.updateCompleteMsgTitle(ticketId, title, currentDealRow, importDataNum,
				currentRowInErrorExcel, firstDataRow, nullRowNum);
	}

	private static boolean isBlankRow(String[] rowData) {
		if (rowData == null)
			return true;
		for (String data : rowData) {
			if (StringUtil.isStringAvaliable(data)) {
				return false;
			}
		}
		return true;
	}

	private boolean validateParams() {
		return isDataTypeLegitimate() && isExcel() && isTotalRowLesserThanLIMIT_ROW_NUM();
	}

	private boolean isConverterNull() {
		if (converter == null) {
			updateErrorTitleAndRowMsg("打开文件出错，程序已终止，详情参见下方错误信息列表", -1,
					"不能导入此格式数据,沒有找到对应的dataConverter");
			return true;
		}
		return false;
	}

	private boolean isTotalRowLesserThanLIMIT_ROW_NUM() {
		totalRow = excelDatas.getSheetDatas(ExcelData.FIRST).length - firstDataRow + 1;
		if (totalRow > LIMIT_ROW_NUM + 1) {
			updateErrorTitleAndRowMsg("导入数据时出错，程序已终止，详情参见下方错误信息列表", -1, "导入数据记录行数不能大于"
					+ LIMIT_ROW_NUM + "行!");
			return false;
		}
		if (totalRow <= 1) {
			updateErrorTitleAndRowMsg("导入数据时出错，程序已终止，详情参见下方错误信息列表", -1, "您不能导入空数据, 请检查您的导入文件!");
			return false;
		}
		return true;
	}

	private void updateErrorTitleAndRowMsg(String title, int row, String msg) {
		updateTicketInfo.updateErrorTitleAndRowMsg(ticketId, title, row, msg, currentDealRow,
				importDataNum, currentRowInErrorExcel, firstDataRow, nullRowNum);
		validateErrorOccur = true;
	}

	private boolean isDataTypeLegitimate() {
		if (!StringUtil.isStringAvaliable(dataType)
				|| DataConvertDefine.getConvertBeanDefine(dataType) == null) {
			updateErrorTitleAndRowMsg("打开文件出错，程序已终止，详情参见下方错误信息列表", 0,
					"不能导入此格式数据,沒有找到对应的dataConverter");
			return false;
		}
		return true;
	}

	private ThreeDataConvert getDataConvert(String dataType, ApplicationContext appContext) {
		return (ThreeDataConvert) appContext.getBean(DataConvertDefine
				.getConvertBeanDefine(dataType));
	}

	private Map<Integer, ThreeDataConvert> getDataConvertMap(String dataType) {
		Map<Integer, String> dtm = DataConvertDefine.getConvertMapBeanDefine(dataType);
		for (Map.Entry<Integer, String> entry : dtm.entrySet()) {
			converterMap.put(entry.getKey(), getDataConvert(entry.getValue(), appContext));
		}
		return converterMap;
	}

	private boolean isExcel() {
		if (!"xls".equals(fileUrl.substring(fileUrl.lastIndexOf(".") + 1))) {
			updateErrorTitleAndRowMsg("上传文件出错，程序已终止，详情参见下方错误信息列表", 0, "只能选择后缀为.xls格式的文件");
			return false;
		}
		return true;
	}

	private void updateTicketErrorMsg(String ticketId, ValidateResult vresult, Integer state) {
		updateTicketInfo.updateTicketErrorMsg(ticketId, vresult, currentDealRow, importDataNum,
				currentRowInErrorExcel, firstDataRow, nullRowNum, state);

	}

	private FileInputStream openFile(String url) {
		logger.info("正在打开文件:{} .....", url);
		File file = new File(url);
		if (file == null || !file.exists()) {
			logger.error("文件{}不存在!", url);
			return null;
		} else {
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				return null;
			}
		}
	}

	class ExcelDatasHandleThread extends Thread {
		private String[][] threadDatas;// 多线程分配的数据

		public ExcelDatasHandleThread(String[][] threadDatas) {
			this.threadDatas = threadDatas;
		}

		@Override
		public void run() {
			createThreadUser();
			try {
				handleDomains();
			} catch (Exception e) {
				logger.error("");
			}// 转换数据，验证并添加到数据库
			if (!validateErrorOccur) {
				updateCompleteMsgTitle("数据已保存，处理完成");
			} else {
				if (currentDealRow == importDataNum) {
					updateNextStepMsgTitle("数据已保存，处理完成");
				}
			}
			updateTicketInfo.updateLogNum(uuid, importDataNum, currentRowInErrorExcel
					- firstDataRow);
			updateTicketInfo.updateLogStatus(uuid, 2, fileName);

		}

	}

	private void createThreadUser() {
		ThreadVariable.setSession(session);
		User user = new User();
		user.setId(session.getUserId());
		user.setOrganization(session.getOrganization());
		ThreadVariable.setUser(user);
		ThreadVariable.setOrganization(session.getOrganization());
		ThreadVariable.setSourcesState(ConstantsProduct.SourcesState.IMPORT);
		ThreadVariable.setModule(module);
	}

	private void createThreadUser(Organization org, String userName) {
		if (org != null) {
			session.setOrganization(org);
		} else {
			session.getOrganization().setOrgName("[系统导入]");
		}
		if (StringUtils.isBlank(userName)) {
			userName = org != null && StringUtils.isNoneBlank(org.getOrgName()) ? org.getOrgName()
					: "[系统导入]";
		}
		session.setUserName(userName);
		session.setUserRealName(userName);

		ThreadVariable.setSession(session);
		User user = new User();
		user.setId(session.getUserId());
		user.setOrganization(session.getOrganization());
		user.setUserName(userName);

		ThreadVariable.setUser(user);
		ThreadVariable.setOrganization(session.getOrganization());
		ThreadVariable.setSourcesState(ConstantsProduct.SourcesState.IMPORT);
		ThreadVariable.setModule(module);
	}

	/**
	 *
	 * @return
	 */

	/**
	 * @param clazz
	 * @param includeParentClass
	 * @return
	 */
	public static ArrayList<String> getClassFieldsName(Class clazz, boolean includeParentClass) {
		Field[] fields = clazz.getDeclaredFields();

		ArrayList<String> arr = new ArrayList();
		for (int i = 0; i < fields.length; i++) {
			arr.add(fields[i].getName());
		}

		if (includeParentClass)
			getParentClassFields(arr, clazz.getSuperclass());

		return arr;
	}

	/**
	 * 获取类实例的父类的属性值
	 *
	 * @param arr   类实例的属性值Map
	 * @param clazz 类名
	 * @return 类名.属性名=属性类型
	 */
	private static ArrayList<String> getParentClassFields(ArrayList<String> arr, Class clazz) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			arr.add(field.getName());
		}
		if (clazz.getSuperclass() == null) {
			return arr;
		}
		getParentClassFields(arr, clazz.getSuperclass());
		return arr;
	}

	/**
	 * 获取类实例的方法
	 *
	 * @param clazz              类名
	 * @param includeParentClass 是否包括父类的方法
	 * @return List
	 */
	public static List<Method> getMethods(Class clazz, boolean includeParentClass) {
		List<Method> list = new ArrayList<Method>();
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			list.add(method);
		}
		if (includeParentClass) {
			getParentClassMethods(list, clazz.getSuperclass());
		}
		return list;
	}

	/**
	 * 获取类实例的父类的方法
	 *
	 * @param list  类实例的方法List
	 * @param clazz 类名
	 * @return List
	 */
	private static List<Method> getParentClassMethods(List<Method> list, Class clazz) {
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			list.add(method);
		}
		if (clazz.getSuperclass() == Object.class) {
			return list;
		}
		getParentClassMethods(list, clazz.getSuperclass());
		return list;
	}

	public boolean isIntercept() {
		return isIntercept;
	}

	public void setIntercept(boolean isIntercept) {
		this.isIntercept = isIntercept;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public File getStoredFile() {
		return storedFile;
	}

	public void setStoredFile(File storedFile) {
		this.storedFile = storedFile;
	}

	public int getTotalRow() {
		return totalRow;
	}

	public void setTotalRow(int totalRow) {
		this.totalRow = totalRow;
	}

	public int getCurrentRowInErrorExcel() {
		return currentRowInErrorExcel;
	}

	public void setCurrentRowInErrorExcel(int currentRowInErrorExcel) {
		this.currentRowInErrorExcel = currentRowInErrorExcel;
	}

	public String getErrorMessageExcelName() {
		return exportMessageExcelName;
	}

	public void setErrorMessageExcelName(String errorMessageExcelName) {
		this.exportMessageExcelName = errorMessageExcelName;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public static void setThreadLocal(String key, Object value) {
		Map map = threadLocal.get();
		if (map == null) {
			map = new HashMap<String, Object>();
			threadLocal.set(map);
		}
		map.put(key, value);
	}

	public static Object getThreadLocal(String key) {
		Map map = threadLocal.get();
		if (map != null) {
			return map.get(key);
		}
		return null;
	}
}