package com.upsoft.yxsw.controller;

import java.util.Date;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.upsoft.login.controller.BaseController;
import com.upsoft.login.support.webservice.SysUtils;
import com.upsoft.system.bean.PageBean;
import com.upsoft.system.bean.ResultBean;
import com.upsoft.system.bean.WSLoginInfoBean;
import com.upsoft.system.constant.CommonConstant;
import com.upsoft.system.util.BeanUtil;
import com.upsoft.system.util.DateUtil;
import com.upsoft.yxsw.entity.BizTXjZypTemplate;
import com.upsoft.yxsw.entity.BizTXjZypTemplateItm;
import com.upsoft.yxsw.entity.BizTXjZypTemplateItmDet;
import com.upsoft.yxsw.service.BizTSbBaseinfoService;
import com.upsoft.yxsw.service.BizTSsBaseinfoService;
import com.upsoft.yxsw.service.BizTXjWorkGroupService;
import com.upsoft.yxsw.service.BizTXjZypTemplateItmDetService;
import com.upsoft.yxsw.service.BizTXjZypTemplateItmService;
import com.upsoft.yxsw.service.BizTXjZypTemplateService;

/**
 * Copyright (c) 2015,重庆扬讯软件技术有限公司<br>
 * All rights reserved.<br>
 *
 * 文件名称：BizTSbBaseinfoController.java<br>
 * 摘要：作业票模版管理<br>
 * -------------------------------------------------------<br>
 * 当前版本：1.1.0<br>
 * 作者：<br>
 * 完成日期：2017-09-08 <br>
 */
@Controller
@RequestMapping(ZypTemplateController.FORWARD_PREFIX)
public class ZypTemplateController  extends BaseController {
	
	protected static final Logger logger = Logger.getLogger(ZypTemplateController.class);
	protected static final String FORWARD_PREFIX = "/zypTemp";
	private static final String JSP_PREFIX = "/WEB-INF/jsp/zyp_temp";
	
	@Autowired
	private BizTXjZypTemplateService zypTemplateService;
	@Autowired
	private BizTXjZypTemplateItmService zypTemplateItmService;
	@Autowired
	private BizTXjZypTemplateItmDetService zypTemplateItmDetService;
	@Autowired
	private BizTXjWorkGroupService workGroupService;
	@Autowired
	private BizTSbBaseinfoService sbBaseinfoService;
	@Autowired
	private BizTSsBaseinfoService ssBaseService;
	
	/**
	 * 跳转到作业票配置列表页
	 * @param map
	 * @return
	 */
	@RequestMapping("/init")
	public String init(HttpServletRequest request, ModelMap map){
		super.findMenuResourcePermission(request, map);
		map.addAllAttributes(WebUtils.getParametersStartingWith(request, null));
		return JSP_PREFIX + "/zyptemp_list";
	}
	
	
	/**
	 * 获取作业票配置列表
	 * @date 2017年9月9日 上午10:29:21
	 * @param request
	 * @param map
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getZypTempList")
	public Map<String,Object> getZypTempList(HttpServletRequest request, ModelMap map){
		
		WSLoginInfoBean userLoginInfo = SysUtils.getLoginInfo(request);
		PageBean bean = new PageBean(request);
		Map<String, Object> params = new HashMap<String, Object>();
		//任务名称
		params.put("tempName", WebUtils.findParameterValue(request, "tempName"));
		if(StringUtils.equals(userLoginInfo.getCsOrgTypeId(), CommonConstant.orgType.FACTORY.getKey())){
			params.put("orgId", userLoginInfo.getCsOrgId());
		}
		return zypTemplateService.getList(bean, params);
	}
	
	/**
	 * 跳转作业票配置新增页面
	 * @date 2017年9月11日 上午11:12:40
	 * @param request
	 * @param map
	 * @return
	 */
	@RequestMapping("/toAddZypTemp")
	public String toAddFreq(HttpServletRequest request,ModelMap map){
		
		map.put("backURL", WebUtils.findParameterValue(request, "backURL"));
		return JSP_PREFIX.concat("/zyptemp_add");
	}
	
	/**
	 * 保存作业票配置信息
	 * @date 2017年9月9日 下午2:50:11
	 * @param request
	 * @param map
	 * @param sbBaseinfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveZypTemp")
	public ResultBean saveFreq(HttpServletRequest request,ModelMap map, BizTXjZypTemplate zypTemplate){
		
		ResultBean resultBean = new ResultBean();
		Date date = new Date();
		WSLoginInfoBean userLoginInfo = SysUtils.getLoginInfo(request);
		zypTemplate.setCreateTimestemp(DateUtil.dateToString(date, DateUtil.DATE_FULL_FORMAT_N));
		zypTemplate.setUpdateTimestemp(DateUtil.dateToString(date, DateUtil.DATE_FULL_FORMAT_N));
		zypTemplate.setCreatorAccount(userLoginInfo.getUser().getUserId());
		zypTemplate.setCreatorName(userLoginInfo.getUser().getUserName());
		zypTemplate.setBelongWscId(userLoginInfo.getCsOrgId());
		zypTemplate.setBelongWscName(userLoginInfo.getCsOrgName());
		zypTemplate.setTempVersion(1L);
		zypTemplate.setIsVaild(Long.valueOf(CommonConstant.STATUS_YoN.YES));
		
		try {
			zypTemplate = zypTemplateService.save(zypTemplate);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("tempId", zypTemplate.getTempId());
			resultBean.setFlag(true);
			resultBean.setMessage("新建作业票配置成功，请完善作业票配置内容");
			resultBean.setData(data);
		} catch (Exception e) {
			resultBean.setFlag(false);
			resultBean.setMessage("新建作业票配置失败");
			logger.error("新增作业票配置失败", e);
		}
		return resultBean;
	}
	
	/**
	 * 跳转到作业票配置信息编辑页面
	 * @date 2017年9月11日 上午11:12:28
	 * @param request
	 * @param map
	 * @return
	 */
	@RequestMapping("/toEditZypTemp")
	public String toEditFreq(@RequestParam(value="tempId",required=true)String tempId, HttpServletRequest request,ModelMap map){
		
		BizTXjZypTemplate zypTemplate = zypTemplateService.getBizTXjZypTemplateById(tempId);
		map.put("zypTemplate", zypTemplate);
		map.put("yy", WebUtils.findParameterValue(request, "yy"));	//保存并应用按钮是否显示
		map.put("backURL", WebUtils.findParameterValue(request, "backURL"));
		return JSP_PREFIX.concat("/zyptemp_edit");
	}
	
	/**
	 * 保存编辑后的作业票配置信息
	 * @date 2017年9月11日 下午8:00:10
	 * @param request
	 * @param map
	 * @param sbBaseinfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveEditZypTemp")
	public ResultBean saveEditZypTemp(HttpServletRequest request, ModelMap map, BizTXjZypTemplate zypTemplate, String submitType){
		
		ResultBean result = new ResultBean();
		WSLoginInfoBean loginInfoBean = SysUtils.getLoginInfo(request);
		BizTXjZypTemplate zypTemplateOri = zypTemplateService.getBizTXjZypTemplateById(zypTemplate.getTempId());
		if(null == zypTemplateOri || !StringUtils.equals(zypTemplateOri.getUpdateTimestemp(), zypTemplate.getUpdateTimestemp())){
			result.setFlag(false);
			result.setMessage("保存失败，数据已更新，请刷新后重试");
		}else{
			try {
				BeanUtil.copyPropertiesIgnoreNull(zypTemplate, zypTemplateOri);
				zypTemplateOri.setUpdateTimestemp(DateUtil.dateToString(new Date(), DateUtil.DATE_FULL_FORMAT_N));
				zypTemplateOri.setUpdatorAccount(loginInfoBean.getUser().getUserId());
				zypTemplateOri.setUpdatorName(loginInfoBean.getUser().getUserName());
				if(StringUtils.equals(submitType, "1")){	//保存并应用时版本号+1
					zypTemplateOri.setTempVersion(zypTemplateOri.getTempVersion()+1);
				}
				zypTemplateService.save(zypTemplateOri);
				result.setFlag(true);
				result.setMessage("保存成功");
			} catch (Exception e) {
				result.setFlag(false);
				result.setMessage("保存失败，系统发生错误");
				logger.error("作业票配置信息修改失败", e);
			}
		}
		return result;
	}
	
	/**
	 * 跳转到查看作业票配置信息页面
	 * @date 2017年9月11日 下午8:35:55
	 * @param sbId
	 * @param request
	 * @param map
	 * @return
	 */
	@RequestMapping("/toViewZypTemp")
	public String toViewZypTemp(@RequestParam(value="tempId",required=true)String tempId, HttpServletRequest request,ModelMap map){
		
		BizTXjZypTemplate zypTemplate = zypTemplateService.getBizTXjZypTemplateById(tempId);
		map.put("zypTemplate", zypTemplate);
		map.put("backURL", WebUtils.findParameterValue(request, "backURL"));
		return JSP_PREFIX.concat("/zyptemp_view");
	}
	
	/**
	 * 删除作业票配置，为逻辑删除
	 * @date 2017年9月11日 下午8:36:18
	 * @param sbId
	 * @param request
	 * @param map
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/doDelZypTemp")
	@Deprecated
	public ResultBean doDelFreq(@RequestParam(value="tempId",required=true)String tempId, @RequestParam(value="updateTimestemp",required=true)String updateTimestemp, HttpServletRequest request,ModelMap map){
		
		ResultBean result = new ResultBean();
		WSLoginInfoBean loginInfoBean = SysUtils.getLoginInfo(request);
		BizTXjZypTemplate zypTemplate = zypTemplateService.getBizTXjZypTemplateById(tempId);
		if(null == zypTemplate || !StringUtils.equals(zypTemplate.getUpdateTimestemp(), updateTimestemp)){
			result.setFlag(false);
			result.setMessage("删除失败，数据已更新，请刷新后重试");
		}else{
			try {
//				zypTemplate.setUpdatorAccount(loginInfoBean.getUser().getUserId());
//				zypTemplate.setUpdatorName(loginInfoBean.getUser().getUserName());
//				zypTemplate.setUpdateTimestemp(DateUtil.dateToString(new Date(), DateUtil.DATE_FULL_FORMAT_N));
				zypTemplateService.delete(BizTXjZypTemplate.class, tempId);
				result.setFlag(true);
				result.setMessage("删除成功");
			} catch (Exception e) {
				result.setFlag(false);
				result.setMessage("删除失败，系统发生错误");
				logger.error("任务排班信息删除失败", e);
			}
		}
		return result;
	}
	
	/**
	 * 获取作业票配置详情列表
	 * @date 2017年9月29日 下午1:35:17
	 * @param request
	 * @param map
	 * @param tempId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getZypTempItemList")
	public Map<String,Object> getZypTempItemList(HttpServletRequest request, ModelMap map, @RequestParam(value="tempId",required=true)String tempId){
		
		PageBean bean = new PageBean(request);
		bean.setPageSize(0);	//不分页
		Map<String, Object> params = new HashMap<String, Object>();
		//配置ID
		params.put("tempId", tempId);
		return zypTemplateItmService.getList(bean, params);
	}
	
	/**
	 * 跳转到新增作业票配置内容页面
	 * @date 2017年10月9日 上午10:19:05
	 * @param request
	 * @param map
	 * @param tempId
	 * @return
	 */
	@RequestMapping("/toAddTempItem")
	public String toAddTempItem(HttpServletRequest request, ModelMap map, @RequestParam(value="tempId",required=true)String tempId, String zxpTempSort){
		
		map.put("tempId", tempId);
		map.put("zxpTempSort", zxpTempSort);
		return JSP_PREFIX.concat("/zyptemp_item_add");
	}
	
	/**
	 * 保存作业票配置内容
	 * @date 2017年10月9日 下午4:55:51
	 * @param request
	 * @param map
	 * @param zypTemplateItm
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveTempItem")
	public ResultBean saveTempItem(HttpServletRequest request,ModelMap map, BizTXjZypTemplateItm zypTemplateItm){
		
		ResultBean resultBean = new ResultBean();
		Date date = new Date();
		WSLoginInfoBean userLoginInfo = SysUtils.getLoginInfo(request);
		if(zypTemplateItm.getJlxdz() == null){
			 zypTemplateItm.setJlxdz(0L);
		}
		if(zypTemplateItm.getJlsbz() == null){
			zypTemplateItm.setJlsbz(0L);
		}
		if(zypTemplateItm.getJlqcl() == null){
			zypTemplateItm.setJlqcl(0L);
		}
		zypTemplateItm.setCreateTimestemp(DateUtil.dateToString(date, DateUtil.DATE_FULL_FORMAT_N));
		zypTemplateItm.setUpdateTimestemp(DateUtil.dateToString(date, DateUtil.DATE_FULL_FORMAT_N));
		zypTemplateItm.setCreatorAccount(userLoginInfo.getUser().getUserId());
		zypTemplateItm.setCreatorName(userLoginInfo.getUser().getUserName());
		
		try {
			zypTemplateItm = zypTemplateItmService.save(zypTemplateItm);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("zxpTempItmId", zypTemplateItm.getZxpTempItmId());
			resultBean.setFlag(true);
			resultBean.setMessage("新建成功，请完善指标项");
			resultBean.setData(data);
		} catch (Exception e) {
			resultBean.setFlag(false);
			resultBean.setMessage("新建失败");
			logger.error("新增作业票配置内容失败", e);
		}
		return resultBean;
	}
	
	/**
	 * 跳转到编辑配置内容页面
	 * @date 2017年10月9日 下午5:07:30
	 * @param request
	 * @param map
	 * @param tempId
	 * @return
	 */
	@RequestMapping("/toEditTempItem")
	public String toEditTempItem(HttpServletRequest request, ModelMap map, @RequestParam(value="zxpTempItmId",required=true)String zxpTempItmId){
		
		map.put("zxpTempItm", zypTemplateItmService.getBizTXjZypTemplateItmById(zxpTempItmId));
		return JSP_PREFIX.concat("/zyptemp_item_edit");
	}
	
	/**
	 * 保存编辑后的作业票配置项
	 * @date 2017年10月10日 下午7:54:05
	 * @param request
	 * @param map
	 * @param zypTempItm
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveEditTempItem")
	public ResultBean saveEditTempItem(HttpServletRequest request,ModelMap map, BizTXjZypTemplateItm zypTempItm){
		
		ResultBean resultBean = new ResultBean();
		WSLoginInfoBean loginInfoBean = SysUtils.getLoginInfo(request);
		BizTXjZypTemplateItm zypTempItmOri = zypTemplateItmService.getBizTXjZypTemplateItmById(zypTempItm.getZxpTempItmId());
		if(null == zypTempItmOri || !StringUtils.equals(zypTempItmOri.getUpdateTimestemp(), zypTempItm.getUpdateTimestemp())){
			resultBean.setFlag(false);
			resultBean.setMessage("保存失败，数据已更新，请刷新后重试");
		}else{
			try {
				BeanUtil.copyPropertiesIgnoreNull(zypTempItm, zypTempItmOri);
				zypTempItmOri.setUpdateTimestemp(String.valueOf(new Date().getTime()));
				zypTempItmOri.setUpdatorAccount(loginInfoBean.getUser().getUserId());
				zypTempItmOri.setUpdatorName(loginInfoBean.getUser().getUserName());
				
				if(zypTempItm.getJlxdz() == null){
					zypTempItmOri.setJlxdz(0L);
				}else{
					zypTempItmOri.setJlxdz(zypTempItm.getJlxdz());
				}
				
				if(zypTempItm.getJlsbz() == null){
					zypTempItmOri.setJlsbz(0L);
				}else{
					zypTempItmOri.setJlsbz(zypTempItm.getJlsbz());
				}
				
				if(zypTempItm.getJlqcl() == null){
					zypTempItmOri.setJlqcl(0L);
				}else{
					zypTempItmOri.setJlqcl(zypTempItm.getJlxdz());
				}
				zypTemplateItmService.save(zypTempItmOri);
				resultBean.setFlag(true);
				resultBean.setMessage("保存成功");
			} catch (Exception e) {
				resultBean.setFlag(false);
				resultBean.setMessage("保存失败，系统发生错误");
				logger.error("作业票配置项信息修改失败", e);
			}
		}
		return resultBean;
	}
	
	/**
	 * 查看作业票配置项页面
	 * @date 2017年10月10日 下午9:00:39
	 * @param request
	 * @param map
	 * @param zxpTempItmId
	 * @return
	 */
	@RequestMapping("/toViewTempItem")
	public String toViewTempItem(HttpServletRequest request, ModelMap map, @RequestParam(value="zxpTempItmId",required=true)String zxpTempItmId){
		
		map.put("zxpTempItm", zypTemplateItmService.getBizTXjZypTemplateItmById(zxpTempItmId));
		return JSP_PREFIX.concat("/zyptemp_item_view");
	}
	
	/**
	 * 删除作业票配置项
	 * @date 2017年10月10日 下午7:16:25
	 * @param request
	 * @param map
	 * @param zxpTempItmId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/doDelTempItem")
	public ResultBean doDelTempItem(HttpServletRequest request, ModelMap map, @RequestParam(value="zxpTempItmId",required=true)String zxpTempItmId){
		ResultBean result = new ResultBean();
		
		try {
			
			zypTemplateItmService.deleteById(zxpTempItmId);
			result.setFlag(true);
			result.setMessage("删除成功");
		} catch (Exception e) {
			result.setFlag(false);
			result.setMessage("删除失败");
			logger.error("删除作业票配置项失败", e);
		}
		return result;
	}
	/**
	 * 获取配置关联指标项
	 * @date 2017年10月9日 下午3:01:57
	 * @param request
	 * @param map
	 * @param detailId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getItemZbxList")
	public Map<String,Object> getItemZbxList(HttpServletRequest request, ModelMap map, @RequestParam(value="zxpTempItmId",required=true)String zxpTempItmId){
		
		PageBean bean = new PageBean(request);
		Map<String, Object> params = new HashMap<String, Object>();
		//配置内容ID
		params.put("zxpTempItmId", zxpTempItmId);
		return zypTemplateItmService.getZbxList(bean, params);
	}
	
	/**
	 * 获取指标项列表，过滤掉已关联指标项
	 * @date 2017年10月9日 下午9:01:27
	 * @param request
	 * @param map
	 * @param zxpTempItmId
	 * @return
	 */
	@RequestMapping("/toAddZbx")
	public String toIndList(HttpServletRequest request, ModelMap map, @RequestParam(value="zxpTempItmId",required=true)String zxpTempItmId){
		
		map.put("zxpTempItmId", zxpTempItmId);
		return JSP_PREFIX.concat("/indList");
	}
	
	/**
	 * @date 2017年10月10日 下午2:46:06
	 * @param request
	 * @param map
	 * @param itmDets
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/doAddZbx")
	public ResultBean doAddZbx(HttpServletRequest request, ModelMap map, String itmDets){
		ResultBean resultBean = new ResultBean();
		
		try {
			
			List<BizTXjZypTemplateItmDet> itmDetList = new Gson().fromJson(itmDets, new TypeToken<List<BizTXjZypTemplateItmDet>>() {}.getType());
			zypTemplateItmDetService.save(itmDetList);
			resultBean.setFlag(true);
			resultBean.setMessage("关联指标项成功");
		} catch (Exception e) {
			resultBean.setFlag(false);
			resultBean.setMessage("关联指标项失败");
			logger.error("关联指标项失败", e);
		}
		return resultBean;
	}
	
	@ResponseBody
	@RequestMapping("/doDelZbx")
	public ResultBean doDelZbx(HttpServletRequest request, ModelMap map, String detailId){
		ResultBean resultBean = new ResultBean();
		
		try {
			zypTemplateItmDetService.delete(BizTXjZypTemplateItmDet.class, detailId);
			resultBean.setFlag(true);
			resultBean.setMessage("删除指标项成功");
		} catch (Exception e) {
			resultBean.setFlag(false);
			resultBean.setMessage("删除指标项失败");
			logger.error("删除指标项关联失败", e);
		}
		return resultBean;
	}
}
