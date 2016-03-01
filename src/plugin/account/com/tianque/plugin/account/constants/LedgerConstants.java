package com.tianque.plugin.account.constants;

import java.util.HashMap;
import java.util.Map;

public interface LedgerConstants {

	/*
	 * 民生信息
	 */
	public static final int PEOPLEASPIRATION = 1;
	/*
	 * 困难群众
	 */
	public static final int POORPEOPLE = 2;
	/*
	 * 安定
	 */
	public static final int STEADYWORK = 3;
	/*
	 * 水利
	 */
	public static final int PEOPLEASPIRATION_WATER = 11;
	/*
	 * 交通
	 */
	public static final int PEOPLEASPIRATION_TRAFFIC = 12;
	/*
	 * 教育
	 */
	public static final int PEOPLEASPIRATION_EDUCATION = 13;
	/*
	 * 医疗
	 */
	public static final int PEOPLEASPIRATION_MEDICAL = 14;
	/*
	 * 农业
	 */
	public static final int PEOPLEASPIRATION_AGRICULTURE = 15;
	/*
	 * 能源
	 */
	public static final int PEOPLEASPIRATION_ENERGY = 16;
	/*
	 * 劳动
	 */
	public static final int PEOPLEASPIRATION_LABOR = 17;
	/*
	 * 环境信息
	 */
	public static final int PEOPLEASPIRATION_ENVIRONMENT = 18;
	/*
	 * 城乡规划建设信息
	 */
	public static final int PEOPLEASPIRATION_TOWN = 19;
	/*
	 * 科技文体
	 */
	public static final int PEOPLEASPIRATION_SCIENCE = 110;
	/*
	 * 其它信息
	 */

	public static final int PEOPLEASPIRATION_OTHER = 111;
	/*
	 * 附件类型
	 */
	final String MODULE_PLATFORMACCOUNT_TYPE = "for_platformAccount";
	
	/*
	 * 台账编号民生前缀
	 */
	public static final Map<Integer,String> peopleAsparitionPreKeyMap = new HashMap<Integer,String>(){

		private static final long serialVersionUID = 1L;

		{
			put(PEOPLEASPIRATION_WATER, "01");
			put(PEOPLEASPIRATION_TRAFFIC, "02");
			put(PEOPLEASPIRATION_ENERGY, "03");
			put(PEOPLEASPIRATION_EDUCATION, "04");
			put(PEOPLEASPIRATION_SCIENCE, "05");
			put(PEOPLEASPIRATION_MEDICAL, "06");
			put(PEOPLEASPIRATION_LABOR, "07");
			put(PEOPLEASPIRATION_ENVIRONMENT, "08");
			put(PEOPLEASPIRATION_TOWN, "09");
			put(PEOPLEASPIRATION_AGRICULTURE, "10");
			put(PEOPLEASPIRATION_OTHER, "11");
		}
	};
	
	/*
	 * 台账编号民生前缀
	 */
	//public static final String PRE_KEY_PEOPLE_ASPARITION = "01";
	/*
	 * 台账编号困难群众前缀
	 */
	public static final String PRE_KEY_POOR_PEOPLE = "12";
	/*
	 * 台账编号安定前缀
	 */
	public static final String PRE_KEY_STEADY_WORK = "13";
	// 台账编号
	public static final String LEDGER = "ledger";
	// 呈报单编号
	public static final String REPORT_FORM = "reportForm";
	// 申报单编号
	public static final String DECLARE_FORM = "declarForm";
	// 回复单编号
	public static final String REPLY_FORM = "replyForms";
	// 转办编号
	public static final String TURN_FORM = "turn00Form";
	// 交办编号
	public static final String ASSGIN_FORM = "assginForm";
	// 联席交办编号
	public static final String JOINT_ASSGIN_FORM = "joint0Form";
	// 县委交办编号
	public static final String COUNTY_ASSGIN_FORM = "countyForm";

	// 无交办类型
	public static final int COUNTY_NO_ASSIGN_TYPE = 0;
	// 县联席会议部门交办类型
	public static final int COUNTY_JOINT_ASSIGN_TYPE = 1;
	// 县委县政府部门交办类型
	public static final int COUNTY_COMMITTEE_ASSIGN_TYPE = 2;

	// 台账可以反馈
	public static final int LEDGER_FEEDBACK_YES = 0;
	// 台账不可以反馈
	public static final int LEDGER_FEEDBACK_NO = 1;

}