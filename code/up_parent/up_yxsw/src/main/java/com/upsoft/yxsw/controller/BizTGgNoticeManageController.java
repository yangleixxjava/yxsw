package com.upsoft.yxsw.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import com.upsoft.login.support.webservice.ServiceReceiver;
import com.upsoft.login.support.webservice.SysUtils;
import com.upsoft.system.bean.PageBean;
import com.upsoft.system.bean.WSLoginInfoBean;
import com.upsoft.system.entity.ComTAttachment;
import com.upsoft.system.entity.SysUser;
import com.upsoft.system.service.AttachmentService;
import com.upsoft.system.util.CommonUtils;
import com.upsoft.yxsw.constant.DictConstant;
import com.upsoft.yxsw.entity.BizTGgNoticeManage;
import com.upsoft.yxsw.service.BizTGgNoticeManageService;

/**
* Copyright (c) 2017,重庆扬讯软件技术股份有限公司<br>
* All rights reserved.<br>
*
* 文件名称：BizTGgNoticeManageController.java<br>
* 摘要：公告管理Controller<br>
* -------------------------------------------------------<br>
* 当前版本：1.0<br>
* 作者：陈涛<br>
* 完成日期：2017年9月12日<br>
* -------------------------------------------------------<br>
*/
@Controller
@RequestMapping(BizTGgNoticeManageController.FORWARD_PREFIX)
public class BizTGgNoticeManageController  {
	protected static final Logger logger = Logger.getLogger(BizTGgNoticeManageController.class);
	protected static final String FORWARD_PREFIX = "/noticeManage";
	protected static final String JSP_PR = "/WEB-INF/jsp/noticeManage";
	
	@Autowired
	public BizTGgNoticeManageService bizTGgNoticeManageService;
	@Autowired
	private AttachmentService  attachmentService;
	/**
	 *  跳转到初始页面
	 *  
	 * @param map
	 * @return
	 */
	@RequestMapping("/init")
	public String init(HttpServletRequest request,ModelMap map){
		//公告类型
		map.put("gg_type", DictConstant.NOTICE_TYPE.getValue());
		//重要程度
		map.put("important_level", DictConstant.IMPORTANT_LEVEL.getValue());
		//是否有效
		map.put("is_alive", DictConstant.CHECKITEM_SFMR.getValue());
		
		//获取标题
		String title  = WebUtils.findParameterValue(request, "title");
		//获取公告类型
		String ggType = WebUtils.findParameterValue(request, "ggType");
		//获取重要程度
		String importantLevel = WebUtils.findParameterValue(request, "importantLevel");
		//获取创建时间
		String startTime = WebUtils.findParameterValue(request, "startTime");
		//获取结束时间
		String endTime = WebUtils.findParameterValue(request, "endTime");
		//获取结束时间
		String isAlive = WebUtils.findParameterValue(request, "isAlive");
		
		map.put("title", title);
		map.put("ggType", ggType);
		map.put("importantLevel", importantLevel);
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		map.put("isAlive", isAlive);
		
		return JSP_PR + "/main";
	}
	
	/**
	 * 获取公告数据
	 * 
	 * @date 2017年9月12日 上午11:12:57
	 * @author 陈涛
	 * @param request
	 * @param map
	 * @return 
	 */
	@RequestMapping("/getNoticeData")
	@ResponseBody
	public Map<String, Object> getNoticeData(HttpServletRequest request,ModelMap map){
		
		PageBean pageBean = new PageBean(request);
		
		Map<String, Object> params = new HashMap<String, Object>();
		//获取标题
		String title  = WebUtils.findParameterValue(request, "title");
		//获取公告类型
		String ggType = WebUtils.findParameterValue(request, "ggType");
		//获取重要程度
		String importantLevel = WebUtils.findParameterValue(request, "importantLevel");
		//获取创建时间
		String startTime = WebUtils.findParameterValue(request, "startTime");
		//获取结束时间
		String endTime = WebUtils.findParameterValue(request, "endTime");
		//获取结束时间
		String isAlive = WebUtils.findParameterValue(request, "isAlive");
		//获取用户信息
		WSLoginInfoBean loginInfo = SysUtils.getLoginInfo(request);
		if (StringUtils.isNotBlank(title)) {
			params.put("title", title);
		}
		if (StringUtils.isNotBlank(ggType)) {
			params.put("ggType", ggType);
		}
		if (StringUtils.isNotBlank(importantLevel)) {
			params.put("importantLevel", importantLevel);
		}
		if (StringUtils.isNotBlank(startTime)) {
			params.put("startTime", startTime);
		}
		if (StringUtils.isNotBlank(endTime)) {
			params.put("endTime", endTime);
		}
		if (StringUtils.isNotBlank(isAlive)) {
			params.put("isAlive", isAlive);
		}
		Map<String,Object> result = bizTGgNoticeManageService.getNoticeData(pageBean, params,loginInfo);
		result.remove("content");
		return bizTGgNoticeManageService.getNoticeData(pageBean, params,loginInfo);
	}
	
	/**
	 * 跳转到新增页面
	 * 
	 * @date 2017年9月12日 下午2:15:16
	 * @author 陈涛
	 * @param request
	 * @param map
	 * @return 
	 */
	@RequestMapping("/toAddNotice")
	public String toAddNotice(HttpServletRequest request,ModelMap map){
		
		//公告类型
		map.put("gg_type", DictConstant.NOTICE_TYPE.getValue());
		//重要程度
		map.put("important_level", DictConstant.IMPORTANT_LEVEL.getValue());
		//是否有效
		map.put("is_alive", DictConstant.CHECKITEM_SFMR.getValue());
				
		return JSP_PR + "/addNotice";
	}
	
	/**
	 * 执行新增
	 * 
	 * @date 2017年9月12日 下午4:52:49
	 * @author 陈涛
	 * @param request
	 * @param map
	 * @param bizTGgNoticeManage
	 * @return 
	 */
	@RequestMapping("/doAddNotice")
	@ResponseBody
	public Map<String, Object> doAddNotice(HttpServletRequest request,ModelMap map,BizTGgNoticeManage bizTGgNoticeManage){
		Map<String, Object> msg = new HashMap<String,Object>();
		//获取用户信息
		WSLoginInfoBean loginInfo = SysUtils.getLoginInfo(request);
		//获取结束时间
		String attachmentList = WebUtils.findParameterValue(request, "attachmentList");
		try{
			
			bizTGgNoticeManageService.saveNotice(bizTGgNoticeManage,loginInfo,attachmentList);
			msg.put("message", "新增公告信息成功！");
		}catch(Exception e){
			msg.put("message", "新增公告信息失败!");
			logger.error("新增公告信息失败，" + e.getMessage());
		}
		return msg;
	}
	
	/**
	 * 单个或者批量停用公告
	 * 
	 * @date 2017年9月12日 下午7:40:18
	 * @author 陈涛
	 * @param request
	 * @param ids
	 * @return 
	 */
	@RequestMapping("/stopNotice")
	@ResponseBody
	public Map<String, Object> stopNotice(HttpServletRequest request,String ids){
		SysUser user = SysUtils.getLoginSysUser(request);
		String[] idsArray = ids.split(",");
		List<String> idArray = new ArrayList<String>();
		for (int i = 0; i < idsArray.length; i++) {
			idArray.add(idsArray[i]);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			
			int result = bizTGgNoticeManageService.updateNoticeByIds(idArray,user);
			if (result == idsArray.length) {
				map.put("status", "公告设为无效成功!");
			}else{
				map.put("status", "公告设为无效失败!");
				logger.error("公告设为无效失败！");
			}
		} catch (Exception e) {
			map.put("status", "公告设为无效失败!");
			logger.error("公告设为无效失败，" + e.getMessage());
		}
		return map;
	}
	
	/**
	 * 查看详情
	 * 
	 * @date 2017年9月13日 上午10:49:33
	 * @author 陈涛
	 * @param id
	 * @param backURL
	 * @param request
	 * @param mod
	 * @return 
	 */
	@RequestMapping("/noticeDetail")
	public String noticeDetail(@RequestParam(value="id",required=true)String id,
			String backURL,HttpServletRequest request,ModelMap mod){
		BizTGgNoticeManage result = new BizTGgNoticeManage();
		result = bizTGgNoticeManageService.findOneById(id);
		//获取公告类型
		List<Map<String,Object>> ggTypeList = ServiceReceiver.getDictSelect(DictConstant.NOTICE_TYPE.getValue());
		//获取公告重要程度
		List<Map<String,Object>> importantLevelList = ServiceReceiver.getDictSelect(DictConstant.IMPORTANT_LEVEL.getValue());
		for (Map<String, Object> type : ggTypeList) {
			String key = type.get("key").toString();
			if(key.equals(result.getGgType())){
				result.setGgType(type.get("value").toString());
			}
		}
		for (Map<String, Object> important : importantLevelList) {
			String key = important.get("key").toString();
			if(key.equals(result.getImportantLevel())){
				result.setImportantLevel(important.get("value").toString());
			}
		}
		//获取附件
		String noticeId = result.getNoticeId();
		List<ComTAttachment> attachmentList = attachmentService.getAttachmentList(noticeId);
		mod.put("attachmentList", attachmentList);
		mod.put("ServerURL",CommonUtils.getWebappsURLPath(request));
 		mod.put("result", result);
		mod.put("backURL", backURL);
		
		return JSP_PR+"/detail";
	}
	
	/**
	 * 跳转到修改界面
	 * 
	 * @date 2017年9月13日 下午3:22:44
	 * @author 陈涛
	 * @param id
	 * @param backURL
	 * @param request
	 * @param map
	 * @return 
	 */
	@RequestMapping("/toModifyNotice")
	public String toModifyNotice(@RequestParam(value="id",required=true)String id,String backURL,HttpServletRequest request,ModelMap map){
		BizTGgNoticeManage result = new BizTGgNoticeManage();
		result = bizTGgNoticeManageService.findOneById(id);
		
		//获取附件
		String noticeId = result.getNoticeId();
		List<ComTAttachment> attachmentList = attachmentService.getAttachmentList(noticeId);
		map.put("attachmentList", attachmentList);
		map.put("ServerURL",CommonUtils.getWebappsURLPath(request));
				
		map.put("result", result);
		map.put("backURL", backURL);
		
		//公告类型
		map.put("gg_type", DictConstant.NOTICE_TYPE.getValue());
		//重要程度
		map.put("important_level", DictConstant.IMPORTANT_LEVEL.getValue());
		//是否有效
		map.put("is_alive", DictConstant.CHECKITEM_SFMR.getValue());
				
		return JSP_PR+"/modifyNotice";
	}
	
	/**
	 * @date 2017年9月25日 下午4:19:34
	 * @author 陈涛
	 * @param request
	 * @param map
	 * @param bizTGgNoticeManage
	 * @return 
	 */
	@RequestMapping("/doModifyNotice")
	@ResponseBody
	public Map<String, Object> doModifyNotice(HttpServletRequest request,ModelMap map,BizTGgNoticeManage bizTGgNoticeManage){
		Map<String, Object> msg = new HashMap<String,Object>();
		//获取用户信息
		WSLoginInfoBean loginInfo = SysUtils.getLoginInfo(request);
		//获取新增附件
		String attachmentList = WebUtils.findParameterValue(request, "attachmentList");
		//获取删除附件
		String delAttachment = WebUtils.findParameterValue(request, "delAttachment");
				
		try{
			bizTGgNoticeManageService.updateNotice(bizTGgNoticeManage,loginInfo,attachmentList,delAttachment);
			msg.put("message", "修改公告信息成功！");
		}catch(Exception e){
			msg.put("message", "修改公告信息失败!");
			logger.error("修改公告信息失败，" + e.getMessage());
		}
		return msg;
	}
}
