package com.upsoft.yxsw.service;

import java.util.List;
import java.util.Map;

import com.upsoft.system.bean.PageBean;
import com.upsoft.system.bean.WSLoginInfoBean;
import com.upsoft.system.service.BaseService;
import com.upsoft.yxsw.entity.BizTGgMsgManage;


/**
 * Copyright (c) 2015,重庆扬讯软件技术有限公司<br>
 * All rights reserved.<br>
 *
 * 文件名称：BizTGgMsgManageService.java<br>
 * 摘要：<br>
 * -------------------------------------------------------<br>
 * 当前版本：1.1.0<br>
 * 作者：<br>
 * 完成日期：2017-09-11 <br>
 */
public interface BizTGgMsgManageService  extends BaseService {

	/**
	 * 获取消息接收数据
	 * 
	 * @date 2017年9月11日 下午1:59:52
	 * @author 陈涛
	 * @param pageBean
	 * @param params
	 * @return 
	 */
	Map<String, Object> getMsgReceiveData(PageBean pageBean,
			Map<String, Object> params);

	/**
	 * 新增消息
	 * 
	 * @date 2017年9月11日 下午3:56:12
	 * @author 陈涛
	 * @param bizTGgMsgManage
	 * @param user 
	 */
	void saveMsg(String title, String content, String reciverId,
			String importantLevel,WSLoginInfoBean loginInfo);

	/**
	 * 获取发送消息数据
	 * 
	 * @date 2017年9月11日 下午3:57:34
	 * @author 陈涛
	 * @param pageBean
	 * @param params
	 * @return 
	 */
	Map<String, Object> getMsgSendData(PageBean pageBean,
			Map<String, Object> params);

	/**
	 * 通过ID获取消息
	 * 
	 * @date 2017年9月11日 下午6:01:22
	 * @author 陈涛
	 * @param id
	 * @return 
	 */
	Map<String, Object> findOneById(String id);

	/**
	 * 通过ID获取消息并设置为已读
	 * 
	 * @date 2017年9月11日 下午7:59:45
	 * @author 陈涛
	 * @param id
	 * @return 
	 */
	BizTGgMsgManage updateMsg(String id);

	/**
	 * 单个或批量设为已读
	 * 
	 * @date 2017年9月11日 下午8:22:59
	 * @author 陈涛
	 * @param idArray
	 * @return 
	 */
	int updateMsgByIds(List<String> idArray);

	/**
	 * 获取我发送的消息和接收到的消息
	 * @date 2017年9月19日 上午10:19:33
	 * @author 胡毅
	 * @param bean 
	 * @param userId
	 * @return
	 */
	Map<String,Object> getMyMessageData(PageBean bean,String userId);

	/**
	 * 获取我接收到的未读消息条数
	 * @param userId
	 * @return
	 */
	long getMyReceivedUnreadMsgCount(String userId);

	/**
	 * 系统自动发送的消息
	 * @date 2017年10月11日 下午4:13:01
	 * @author 胡毅
	 * @param title
	 * @param content
	 * @param reciverIds
	 * @param level
	 */
	void saveSystemMsg(String title, String content, String reciverIds, String level);

}
