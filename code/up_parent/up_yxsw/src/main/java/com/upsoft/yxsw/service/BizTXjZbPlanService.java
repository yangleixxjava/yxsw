package com.upsoft.yxsw.service;

import java.util.List;
import java.util.Map;

import com.upsoft.system.bean.PageBean;
import com.upsoft.system.bean.WSLoginInfoBean;
import com.upsoft.system.service.BaseService;
import com.upsoft.yxsw.entity.BizTXjZbPlan;



/**
* Copyright (c) 2017,重庆扬讯软件技术股份有限公司<br>
* All rights reserved.<br>
*
* 文件名称：BizTXjZbPlanService.java<br>
* 摘要：简要描述本文件的内容<br>
* -------------------------------------------------------<br>
* 当前版本：1.0<br>
* 作者：陈涛<br>
* 完成日期：2017年9月20日<br>
* -------------------------------------------------------<br>
*/
public interface BizTXjZbPlanService  extends BaseService {

	/**
	 * 获取排班数据
	 * 
	 * @date 2017年9月20日 上午10:32:20
	 * @author 陈涛
	 * @param pageBean
	 * @param params
	 * @return 
	 */
	Map<String, Object> getZbPlanData(PageBean pageBean,
			Map<String, Object> params,WSLoginInfoBean loginInfo);

	/**
	 * 获取班次开始和结束时间
	 * 
	 * @date 2017年9月20日 下午3:37:29
	 * @author 陈涛
	 * @param csOrgId
	 * @return 
	 */
	Map<String, Object> getTimes(String csOrgId);

	/**
	 * 查询是否已经排过班
	 * 
	 * @date 2017年9月20日 下午4:37:43
	 * @author 陈涛
	 * @param validateValue
	 * @return 
	 */
	Boolean dateExists(String validateValue,String csOrgId,String zbPlanId);

	/**
	 * 获取本厂站的班次
	 * 
	 * @date 2017年9月20日 下午7:15:24
	 * @author 陈涛
	 * @param csOrgId
	 * @return 
	 */
	List<Map<String, Object>> getBc(String csOrgId);

	/**
	 * 获取本厂站下的工艺段
	 * 
	 * @date 2017年9月20日 下午7:17:17
	 * @author 陈涛
	 * @param csOrgId
	 * @return 
	 */
	List<Map<String, Object>> getGyd(String csOrgId);

	/**
	 * 新增排班信息
	 * 
	 * @date 2017年9月21日 上午10:12:27
	 * @author 陈涛
	 * @param resultList
	 * @param zbDate
	 * @param zbFzrId
	 * @param zbFzrName
	 * @param startTime
	 * @param endTime
	 * @param loginInfo 
	 */
	void saveZbPlan(List<Map<String,Object>> resultList, String zbDate, 
			 String startTime, String endTime,
			WSLoginInfoBean loginInfo);

	/**
	 * 通过id获取从表信息
	 * 
	 * @date 2017年9月21日 下午2:46:07
	 * @author 陈涛
	 * @param zbPlanId
	 * @return 
	 */
	List<Map<String, Object>> findInfoList(String zbPlanId);

	/**
	 * 删除排班信息
	 * 
	 * @date 2017年9月21日 下午8:15:28
	 * @author 陈涛
	 * @param id 
	 */
	void delZbPlan(String id);

	/**
	 *在已安排的排班中获取工艺段
	 *
	 * @date 2017年9月22日 上午9:00:18
	 * @author 陈涛
	 * @param zbPlanId
	 * @return 
	 */
	List<Map<String, Object>> getGydIndetail(String zbPlanId);

	/**
	 * 在已安排的排班中获取班次
	 * 
	 * @date 2017年9月22日 上午9:02:03
	 * @author 陈涛
	 * @param zbPlanId
	 * @return 
	 */
	List<Map<String, Object>> getBcIndetail(String zbPlanId);

	/**
	 * 修改排班
	 * 
	 * @date 2017年9月22日 下午2:42:59
	 * @author 陈涛
	 * @param bizTXjZbPlan
	 * @param zbDate
	 * @param zbFzrId
	 * @param zbFzrName
	 * @param startTime
	 * @param endTime
	 * @param detailList
	 * @param loginInfo 
	 */
	void updateZbPlan(BizTXjZbPlan bizTXjZbPlan, String zbDate,
			String startTime, String endTime,
			List<Map<String, Object>> detailList, WSLoginInfoBean loginInfo);
	
	/**
	 * 班次设置发生改变时，删除未生效排班
	 * @date 2017年9月27日 下午3:30:23
	 * @param periodDetailIds
	 */
	void deleteZbPlanByPeriod(List<String> periodDetailIds);

	/**
	 * 获取未生效的厂巡排班列表
	 * 
	 * @date 2017年9月29日 下午4:20:35
	 * @author 陈涛
	 * @return 
	 */
	List<BizTXjZbPlan> getNoEffectList(String csOrgId);
	
}