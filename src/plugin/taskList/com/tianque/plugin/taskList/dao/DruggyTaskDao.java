package com.tianque.plugin.taskList.dao;

import java.util.List;

import com.tianque.core.vo.PageInfo;
import com.tianque.plugin.taskList.domain.DruggyTask;

public interface DruggyTaskDao {

	/**
	 * 修改吸毒人员走访信息
	 * @param druggyTask
	 */
	public void updateDruggyTask(DruggyTask druggyTask);

	/**
	 * 新增吸毒人员走访信息
	 * @param druggyTask
	 * @return
	 */
	public DruggyTask addDruggyTask(DruggyTask druggyTask);

	/**
	 * 删除吸毒人员走访信息
	 * @param ids
	 */
	public void deleteDruggyTaskByIds(List ids);

	/**
	 * 获取列表
	 * @param druggyTask
	 * @param page
	 * @param rows
	 * @param sidx
	 * @param sord
	 * @return
	 */
	public PageInfo<DruggyTask> getDruggyTaskList(DruggyTask druggyTask, Integer page,
			Integer rows, String sidx, String sord);
	

	/**
	 * 根据id获取某条吸毒信息
	 * @param id
	 * @return
	 */
	public DruggyTask getDruggyTaskById(Long id);

	/**
	 * 高级搜索 搜索
	 * @param druggyTask
	 * @param page
	 * @param rows
	 * @param sidx
	 * @param sord
	 * @return
	 */
	public PageInfo searchDruggyTaskList(DruggyTask druggyTask, Integer page, Integer rows,
			String sidx, String sord);

	/**
	 * 根据姓名检索
	 * @param druggyTask
	 * @return
	 */
	public List<DruggyTask> searchDruggyTaskByName(DruggyTask druggyTask);

}