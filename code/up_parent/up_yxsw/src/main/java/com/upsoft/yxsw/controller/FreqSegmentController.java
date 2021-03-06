package com.upsoft.yxsw.controller;

import java.util.ArrayList;
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
import com.upsoft.yxsw.constant.Constant;
import com.upsoft.yxsw.entity.BizTXjFreqSegment;
import com.upsoft.yxsw.entity.BizTXjFreqSegmentSbss;
import com.upsoft.yxsw.service.BizTSbBaseinfoService;
import com.upsoft.yxsw.service.BizTSsBaseinfoService;
import com.upsoft.yxsw.service.BizTXjFreqSegmentSbssService;
import com.upsoft.yxsw.service.BizTXjFreqSegmentService;
import com.upsoft.yxsw.service.BizTXjWorkGroupService;

/**
 * Copyright (c) 2015,重庆扬讯软件技术有限公司<br>
 * All rights reserved.<br>
 *
 * 文件名称：BizTSbBaseinfoController.java<br>
 * 摘要：<br>
 * -------------------------------------------------------<br>
 * 当前版本：1.1.0<br>
 * 作者：<br>
 * 完成日期：2017-09-08 <br>
 */
@Controller
@RequestMapping(FreqSegmentController.FORWARD_PREFIX)
public class FreqSegmentController  extends BaseController {
	
	protected static final Logger logger = Logger.getLogger(FreqSegmentController.class);
	protected static final String FORWARD_PREFIX = "/freq";
	private static final String JSP_PREFIX = "/WEB-INF/jsp/freq_mgt";
	
	@Autowired
	private BizTXjFreqSegmentService freqSegmentService;
	@Autowired
	private BizTXjFreqSegmentSbssService freqSegmentSbssService;
	@Autowired
	private BizTXjWorkGroupService workGroupService;
	@Autowired
	private BizTSbBaseinfoService sbBaseinfoService;
	@Autowired
	private BizTSsBaseinfoService ssBaseService;
	
	/**
	 * 巡检任务内容页
	 * @param map
	 * @return
	 */
	@RequestMapping("/init")
	public String init(HttpServletRequest request, ModelMap map){
		super.findMenuResourcePermission(request, map);
		map.addAllAttributes(WebUtils.getParametersStartingWith(request, null));
		return JSP_PREFIX + "/freq_list";
	}
	
	
	/**
	 * 获取任务排班列表
	 * @date 2017年9月9日 上午10:29:21
	 * @param request
	 * @param map
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getFreqList")
	public Map<String,Object> getFreqList(HttpServletRequest request, ModelMap map){
		
		WSLoginInfoBean userLoginInfo = SysUtils.getLoginInfo(request);
		PageBean bean = new PageBean(request);
		Map<String, Object> params = new HashMap<String, Object>();
		//任务名称
		params.put("freqSegmentName", WebUtils.findParameterValue(request, "freqSegmentName"));
		params.put("detailId", WebUtils.findParameterValue(request, "detailId"));
		if(StringUtils.equals(userLoginInfo.getCsOrgTypeId(), CommonConstant.orgType.FACTORY.getKey())){
			params.put("orgCode", userLoginInfo.getCsOrgId());
		}
		return freqSegmentService.getList(bean, params);
	}
	
	/**
	 * 跳转任务排班新增页面
	 * @date 2017年9月11日 上午11:12:40
	 * @param request
	 * @param map
	 * @return
	 */
	@RequestMapping("/toAddFreq")
	public String toAddFreq(HttpServletRequest request,ModelMap map){
		
		map.put("backURL", WebUtils.findParameterValue(request, "backURL"));
		return JSP_PREFIX.concat("/freq_add");
	}
	
	/**
	 * 保存设备信息
	 * @date 2017年9月9日 下午2:50:11
	 * @param request
	 * @param map
	 * @param sbBaseinfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveFreq")
	public ResultBean saveFreq(HttpServletRequest request,ModelMap map, BizTXjFreqSegment freqSegment){
		
		ResultBean resultBean = new ResultBean();
		Date date = new Date();
		WSLoginInfoBean userLoginInfo = SysUtils.getLoginInfo(request);
		freqSegment.setCreateTimestemp(DateUtil.dateToString(date, DateUtil.DATE_FULL_FORMAT_N));
		freqSegment.setUpdateTimestemp(DateUtil.dateToString(date, DateUtil.DATE_FULL_FORMAT_N));
		freqSegment.setCreatorAccount(userLoginInfo.getUser().getUserId());
		freqSegment.setCreatorName(userLoginInfo.getUser().getUserName());
		freqSegment.setBelongWscId(userLoginInfo.getCsOrgId());
		freqSegment.setBelongWscName(userLoginInfo.getCsOrgName());
		freqSegment.setStartTime(DateUtil.stringToString(freqSegment.getStartTime(), "HH:mm", "HHmm"));
		freqSegment.setEndTime(DateUtil.stringToString(freqSegment.getEndTime(), "HH:mm", "HHmm"));
		freqSegment.setDelFlag(Long.valueOf(CommonConstant.STATUS_YoN.NO));
		
		try {
			freqSegment = freqSegmentService.save(freqSegment);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("freqSegmentId", freqSegment.getFreqSegmentId());
			resultBean.setFlag(true);
			resultBean.setMessage("新建任务排班成功，请关联任务巡检设备和设施");
			resultBean.setData(data);
		} catch (Exception e) {
			resultBean.setFlag(false);
			resultBean.setMessage("新建任务排班失败");
			logger.error("新增任务排班失败", e);
		}
		return resultBean;
	}
	
	/**
	 * 跳转到设备档案编辑页面
	 * @date 2017年9月11日 上午11:12:28
	 * @param request
	 * @param map
	 * @return
	 */
	@RequestMapping("/toEditFreq")
	public String toEditFreq(@RequestParam(value="freqId",required=true)String freqId, HttpServletRequest request,ModelMap map){
		
		BizTXjFreqSegment freq = freqSegmentService.getBizTXjFreqSegmentById(freqId);
		freq.setStartTime(DateUtil.stringToString(freq.getStartTime(), "HHmm", "HH:mm"));
		freq.setEndTime(DateUtil.stringToString(freq.getEndTime(), "HHmm", "HH:mm"));
		map.put("freq", freq);
		map.put("backURL", WebUtils.findParameterValue(request, "backURL"));
		return JSP_PREFIX.concat("/freq_edit");
	}
	
	/**
	 * 保存编辑后的设备档案信息
	 * @date 2017年9月11日 下午8:00:10
	 * @param request
	 * @param map
	 * @param sbBaseinfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveEditFreq")
	public ResultBean saveEditFreq(HttpServletRequest request, ModelMap map, BizTXjFreqSegment freqSegment){
		
		ResultBean result = new ResultBean();
		WSLoginInfoBean loginInfoBean = SysUtils.getLoginInfo(request);
		BizTXjFreqSegment freqOri = freqSegmentService.getBizTXjFreqSegmentById(freqSegment.getFreqSegmentId());
		if(null == freqOri || !StringUtils.equals(freqOri.getUpdateTimestemp(), freqSegment.getUpdateTimestemp())){
			result.setFlag(false);
			result.setMessage("保存失败，数据已更新，请刷新后重试");
		}else{
			try {
				BeanUtil.copyPropertiesIgnoreNull(freqSegment, freqOri);
				freqOri.setStartTime(DateUtil.stringToString(freqSegment.getStartTime(), "HH:mm", "HHmm"));
				freqOri.setEndTime(DateUtil.stringToString(freqSegment.getEndTime(), "HH:mm", "HHmm"));
				freqOri.setUpdateTimestemp(DateUtil.dateToString(new Date(), DateUtil.DATE_FULL_FORMAT_N));
				freqOri.setUpdatorAccount(loginInfoBean.getUser().getUserId());
				freqOri.setUpdatorName(loginInfoBean.getUser().getUserName());
				freqSegmentService.save(freqOri);
				result.setFlag(true);
				result.setMessage("保存成功");
			} catch (Exception e) {
				result.setFlag(false);
				result.setMessage("保存失败，系统发生错误");
				logger.error("任务排班信息修改失败", e);
			}
		}
		return result;
	}
	
	/**
	 * 跳转到查看设备信息页面
	 * @date 2017年9月11日 下午8:35:55
	 * @param sbId
	 * @param request
	 * @param map
	 * @return
	 */
	@RequestMapping("/toViewFreq")
	public String toViewFreq(@RequestParam(value="freqId",required=true)String freqId, HttpServletRequest request,ModelMap map){
		
		BizTXjFreqSegment freq = freqSegmentService.getBizTXjFreqSegmentById(freqId);
		freq.setStartTime(DateUtil.stringToString(freq.getStartTime(), "HHmm", "HH:mm"));
		freq.setEndTime(DateUtil.stringToString(freq.getEndTime(), "HHmm", "HH:mm"));
		map.put("freq", freq);
		map.put("backURL", WebUtils.findParameterValue(request, "backURL"));
		return JSP_PREFIX.concat("/freq_view");
	}
	
	/**
	 * 删除设备信息，为逻辑删除
	 * @date 2017年9月11日 下午8:36:18
	 * @param sbId
	 * @param request
	 * @param map
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/doDelFreq")
	public ResultBean doDelFreq(@RequestParam(value="freqId",required=true)String freqId, @RequestParam(value="updateTimestemp",required=true)String updateTimestemp, HttpServletRequest request,ModelMap map){
		
		ResultBean result = new ResultBean();
		WSLoginInfoBean loginInfoBean = SysUtils.getLoginInfo(request);
		BizTXjFreqSegment freqOri = freqSegmentService.getBizTXjFreqSegmentById(freqId);
		if(null == freqOri || !StringUtils.equals(freqOri.getUpdateTimestemp(), updateTimestemp)){
			result.setFlag(false);
			result.setMessage("删除失败，数据已更新，请刷新后重试");
		}else{
			try {
				freqOri.setUpdateTimestemp(String.valueOf(new Date().getTime()));
				freqOri.setUpdatorAccount(loginInfoBean.getUser().getUserId());
				freqOri.setUpdatorName(loginInfoBean.getUser().getUserName());
				freqOri.setDelFlag(Long.valueOf(CommonConstant.STATUS_YoN.YES));
				freqSegmentService.save(freqOri);
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
	 * 任务关联设备列表
	 * @date 2017年9月22日 下午3:52:37
	 * @param request
	 * @param map
	 * @return
	 */
	@RequestMapping("/toFreqEpList")
	public String toFreqEqList(HttpServletRequest request, ModelMap map, @RequestParam(value="freqId", required=true) String freqId){
		
		map.put("csType", SysUtils.getLoginInfo(request).getCsOrgTypeId());
		map.put("freqId", freqId);
		return JSP_PREFIX.concat("/freq_list_eq");
	}
	
	/**
	 * 已关联设备列表
	 * @date 2017年9月26日 上午11:19:41
	 * @param request
	 * @param map
	 * @param freqId
	 * @return
	 */
	@RequestMapping("/toFreqEpListView")
	public String toFreqEqListView(HttpServletRequest request, ModelMap map, @RequestParam(value="freqId", required=true) String freqId){
		
		map.put("csType", SysUtils.getLoginInfo(request).getCsOrgTypeId());
		map.put("freqId", freqId);
		return JSP_PREFIX.concat("/freq_list_eq_view");
	}
	
	/**
	 * 获取任务已关联的设备
	 * @date 2017年9月22日 下午6:00:42
	 * @param request
	 * @param map
	 * @param freqId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getFreqEpList")
	public Map<String, Object> getFreqEpList(HttpServletRequest request, ModelMap map, @RequestParam(value="freqId", required=true) String freqId){
		
		PageBean bean = new PageBean(request);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("freqId", freqId);
		params.put("sbssType", Constant.DETAIL_TYPE.EQUIPMENT.getValue());
		return freqSegmentSbssService.getEqList(bean, params);
	}
	
	/**
	 * 跳转到新增任务关联设备列表
	 * @date 2017年9月22日 下午7:44:55
	 * @param request
	 * @param map
	 * @param freqId
	 * @return
	 */
	@RequestMapping("/toAddFreqEqList")
	public String toAddFreqEqList(HttpServletRequest request, ModelMap map, @RequestParam(value="freqId", required=true) String freqId){
		
		map.put("csType", SysUtils.getLoginInfo(request).getCsOrgTypeId());
		map.put("freqId", freqId);
		return JSP_PREFIX.concat("/freq_list_eq_add");
	}
	
	/**
	 * 获取未关联设备列表
	 * @date 2017年9月22日 下午7:44:55
	 * @param request
	 * @param map
	 * @param freqId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getAddFreqEqList")
	public Map<String, Object> getAddFreqEqList(HttpServletRequest request, ModelMap map, @RequestParam(value="freqId", required=true) String freqId){
		
		PageBean bean = new PageBean(request);
		
		WSLoginInfoBean loginInfoBean = SysUtils.getLoginInfo(request);
		Map<String, Object> params = new HashMap<String, Object>();
		//任务名称
		params.put("sbName", WebUtils.findParameterValue(request, "sbName"));
		//设备类别
		params.put("sbSort", WebUtils.findParameterValue(request, "sbSort"));
		if(StringUtils.equals(loginInfoBean.getCsOrgTypeId(), CommonConstant.orgType.FACTORY.getKey())){
			params.put("orgId", loginInfoBean.getCsOrgId());
		}
		
		return sbBaseinfoService.getFreqNoReEqList(bean, freqId, params);
	}
	
	/**
	 * 关联选中设备
	 * @date 2017年9月25日 上午10:29:12
	 * @param request
	 * @param map
	 * @param eqList
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveFreqRelateEq")
	public ResultBean saveFreqRelateEq(HttpServletRequest request, ModelMap map, String freqId, String eqList){
		
		ResultBean result = new ResultBean();
		try {
			List<Map<String, String>> freqEqList = new Gson().fromJson(eqList, new TypeToken<List<Map<String, String>>>() {}.getType());
			BizTXjFreqSegmentSbss sbss;
			List<BizTXjFreqSegmentSbss> sbssList = new ArrayList<BizTXjFreqSegmentSbss>();
			for (Map<String, String> eq : freqEqList) {
				sbss = new BizTXjFreqSegmentSbss();
				sbss.setFreqSegmentId(freqId);
				sbss.setSbssId(eq.get("sbss_id"));
				sbss.setSbssName(eq.get("sb_name"));
				sbss.setTechnicsId(eq.get("technics_id"));
				sbss.setTechnicsName(eq.get("technics_name"));
				sbss.setXjdItemId(eq.get("xjd_item_id"));
				sbss.setXjdItemName(eq.get("xjd_item_name"));
				sbss.setSbssType(Constant.DETAIL_TYPE.EQUIPMENT.getValue());
				sbssList.add(sbss);
			}
			freqSegmentSbssService.saveBizTXjFreqSegmentSbssList(sbssList);
			result.setFlag(true);
			result.setMessage("关联设备成功");
		} catch (Exception e) {
			result.setFlag(false);
			result.setMessage("关联设备失败");
			logger.error("厂巡任务关联设备失败", e);
		}
		return result;
	}
	
	/**
	 * 删除设备设施与厂巡任务的关联
	 * @date 2017年9月25日 下午1:58:47
	 * @param request
	 * @param map
	 * @param freqSbssId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/delFreqRelate")
	public ResultBean delFreqRelate(HttpServletRequest request, ModelMap map, String ids){
		
		ResultBean result = new ResultBean();
		try {
			if(freqSegmentSbssService.delAll(ids.substring(0, ids.lastIndexOf(",")))){
				result.setFlag(true);
				result.setMessage("删除关联成功");
			}else{
				result.setFlag(false);
				result.setMessage("删除关联失败");
				logger.error("删除任务关联设备设施失败:"+ids);
			}
		} catch (Exception e) {
			result.setFlag(false);
			result.setMessage("删除关联失败");
			logger.error("删除任务关联设备设施失败", e);
		}
		return result;
	}
	/**
	 * 任务关联设施列表
	 * @date 2017年9月22日 下午7:44:55
	 * @param request
	 * @param map
	 * @param freqId
	 * @return
	 */
	@RequestMapping("/toFreqSsList")
	public String toFreqSsList(HttpServletRequest request, ModelMap map, @RequestParam(value="freqId", required=true) String freqId){
		
		map.put("csType", SysUtils.getLoginInfo(request).getCsOrgTypeId());
		map.put("freqId", freqId);
		return JSP_PREFIX.concat("/freq_list_ss");
	}
	
	/**
	 * 已关联设备列表
	 * @date 2017年9月26日 上午11:22:03
	 * @param request
	 * @param map
	 * @param freqId
	 * @return
	 */
	@RequestMapping("/toFreqSsListView")
	public String toFreqSsListView(HttpServletRequest request, ModelMap map, @RequestParam(value="freqId", required=true) String freqId){
		
		map.put("csType", SysUtils.getLoginInfo(request).getCsOrgTypeId());
		map.put("freqId", freqId);
		return JSP_PREFIX.concat("/freq_list_ss_view");
	}
	
	/**
	 * 获取任务已关联的设施
	 * @date 2017年9月22日 下午6:00:42
	 * @param request
	 * @param map
	 * @param freqId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/getFreqSsList")
	@ResponseBody
	public Map<String, Object> getFreqSsList(HttpServletRequest request, ModelMap map, @RequestParam(value="freqId", required=true) String freqId){
		
		PageBean bean = new PageBean(request);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("freqId", freqId);
		params.put("sbssType", Constant.DETAIL_TYPE.FACILITY.getValue());
		
		Map<String,Object> result = freqSegmentSbssService.getSsList(bean, params);
		List<Map<String,Object>> rows = (List<Map<String, Object>>) result.get("rows");
		if(rows.size() > 0){
			for (Map<String, Object> map2 : rows) {
				if(null != map2.get("layer")){
					Map<String,Object> bizTSsEntity = ssBaseService.getSSLayer(map2.get("layer").toString());;
					map2.put("layer",bizTSsEntity.get("layername"));
				}
			}
		}
		return result;
	}
	
	/**
	 * 跳转到新增任务关联设备列表
	 * @date 2017年9月22日 下午7:44:55
	 * @param request
	 * @param map
	 * @param freqId
	 * @return
	 */
	@RequestMapping("/toAddFreqSsList")
	public String toAddFreqSsList(HttpServletRequest request, ModelMap map, @RequestParam(value="freqId", required=true) String freqId){
		
		map.put("csType", SysUtils.getLoginInfo(request).getCsOrgTypeId());
		map.put("freqId", freqId);
		return JSP_PREFIX.concat("/freq_list_ss_add");
	}
	
	/**
	 * 获取未关联设备列表
	 * @date 2017年9月22日 下午7:44:55
	 * @param request
	 * @param map
	 * @param freqId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/getAddFreqSsList")
	public Map<String, Object> getAddFreqSsList(HttpServletRequest request, ModelMap map, @RequestParam(value="freqId", required=true) String freqId){
		
		PageBean bean = new PageBean(request);
		
		WSLoginInfoBean loginInfoBean = SysUtils.getLoginInfo(request);
		Map<String, Object> params = new HashMap<String, Object>();
		//设施名称
		params.put("name", WebUtils.findParameterValue(request, "name"));
		params.put("code", WebUtils.findParameterValue(request, "code"));
		if(StringUtils.equals(loginInfoBean.getCsOrgTypeId(), CommonConstant.orgType.FACTORY.getKey())){
			params.put("csOrgId", loginInfoBean.getCsOrgId());
		}
		Map<String,Object> result = ssBaseService.getFreqNoReSsList(bean, freqId, params);
		List<Map<String,Object>> rows = (List<Map<String, Object>>) result.get("rows");
		if(rows.size() > 0){
			for (Map<String, Object> map2 : rows) {
				if(null != map2.get("layer")){
					Map<String,Object> bizTSsEntity = ssBaseService.getSSLayer(map2.get("layer").toString());;
					map2.put("layer",bizTSsEntity.get("layername"));
				}
			}
		}
		return result;
	}
	
	/**
	 * 关联选中设备
	 * @date 2017年9月25日 上午10:29:12
	 * @param request
	 * @param map
	 * @param eqList
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveFreqRelateSs")
	public ResultBean saveFreqRelateSs(HttpServletRequest request, ModelMap map, String freqId, String ssList){
		
		ResultBean result = new ResultBean();
		try {
			List<Map<String, String>> freqSsList = new Gson().fromJson(ssList, new TypeToken<List<Map<String, String>>>() {}.getType());
			BizTXjFreqSegmentSbss sbss;
			List<BizTXjFreqSegmentSbss> sbssList = new ArrayList<BizTXjFreqSegmentSbss>();
			for (Map<String, String> eq : freqSsList) {
				sbss = new BizTXjFreqSegmentSbss();
				sbss.setFreqSegmentId(freqId);
				sbss.setSbssId(eq.get("sbss_id"));
				sbss.setSbssName(eq.get("sb_name"));
				sbss.setTechnicsId(eq.get("technics_id"));
				sbss.setTechnicsName(eq.get("technics_name"));
				sbss.setXjdItemId(eq.get("xjd_item_id"));
				sbss.setXjdItemName(eq.get("xjd_item_name"));
				sbss.setSbssType(Constant.DETAIL_TYPE.FACILITY.getValue());
				sbssList.add(sbss);
			}
			freqSegmentSbssService.saveBizTXjFreqSegmentSbssList(sbssList);
			result.setFlag(true);
			result.setMessage("关联设施成功");
		} catch (Exception e) {
			result.setFlag(false);
			result.setMessage("关联设施失败");
			logger.error("厂巡任务关联设施失败", e);
		}
		return result;
	}
}
