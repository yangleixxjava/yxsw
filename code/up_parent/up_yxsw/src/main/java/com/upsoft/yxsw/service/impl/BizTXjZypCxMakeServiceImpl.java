package com.upsoft.yxsw.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.upsoft.login.support.webservice.ServiceReceiver;
import com.upsoft.system.bean.PageBean;
import com.upsoft.system.bean.WSLoginInfoBean;
import com.upsoft.system.entity.SysUser;
import com.upsoft.system.service.BaseServiceImpl;
import com.upsoft.system.util.DateUtil;
import com.upsoft.system.util.MapUtil;
import com.upsoft.yxsw.constant.Constant;
import com.upsoft.yxsw.constant.DictConstant;
import com.upsoft.yxsw.controller.cxMake.bean.CxMakeDetailPojo;
import com.upsoft.yxsw.controller.cxMake.bean.CxMakePersonHkPojo;
import com.upsoft.yxsw.controller.cxMake.bean.WriteCxMakeSBPojo;
import com.upsoft.yxsw.dao.BizTXjZypCxMakeDAO;
import com.upsoft.yxsw.dao.BizTXjZypCxMakeTmpDAO;
import com.upsoft.yxsw.dao.BizTXjZypCxMakeTmpItemDAO;
import com.upsoft.yxsw.entity.BizTXjWorkGroup;
import com.upsoft.yxsw.entity.BizTXjZypCxMake;
import com.upsoft.yxsw.entity.BizTXjZypCxMakePersonKh;
import com.upsoft.yxsw.entity.BizTXjZypCxMakeTmp;
import com.upsoft.yxsw.entity.BizTXjZypCxMakeTmpItem;
import com.upsoft.yxsw.mobile.bean.zyp.CxMakeDetailBean;
import com.upsoft.yxsw.mobile.bean.zyp.CxMakeListBean;
import com.upsoft.yxsw.mobile.bean.zyp.ZbPlanInCXMakeBean;
import com.upsoft.yxsw.mobile.bean.zyp.ZbPlanInCXMakeDetailBean;
import com.upsoft.yxsw.service.BizTGgMsgManageService;
import com.upsoft.yxsw.service.BizTXjWorkGroupService;
import com.upsoft.yxsw.service.BizTXjZypCxMakeHisService;
import com.upsoft.yxsw.service.BizTXjZypCxMakePersonKhService;
import com.upsoft.yxsw.service.BizTXjZypCxMakeService;


@Service
public class BizTXjZypCxMakeServiceImpl extends BaseServiceImpl implements BizTXjZypCxMakeService {

	@Autowired
	private BizTXjZypCxMakeHisService bizTXjZypCxMakeHisService;
	@Autowired
	private BizTGgMsgManageService bizTGgMsgManageService;
	@Autowired
	private BizTXjZypCxMakePersonKhService bizTXjZypCxMakePersonKhService;
	@Autowired
	private BizTXjWorkGroupService bizTXjWorkGroupService;
//	@Autowired
//	private BizTXjZypCxMakeTmpItemService bizTXjZypCxMakeTmpItemService;
	@Autowired
	private BizTXjZypCxMakeDAO bizTXjZypCxMakeDAO;
	@Autowired
	private BizTXjZypCxMakeTmpDAO bizTXjZypCxMakeTmpDAO;
	@Autowired
	private BizTXjZypCxMakeTmpItemDAO bizTXjZypCxMakeTmpItemDAO;
	
	@Autowired
	private JdbcTemplate jdbcTemplate; 
	
	private String NVL(Object o){ return null!=o?o.toString():""; }
	protected static final Logger logger = Logger.getLogger(BizTXjZypCxMakeServiceImpl.class);
	
	@Override
	public Map<String,Object> getZYPListForAPP(PageBean pageBean, String csOrgId) {
		// 判断当前时间是否是在班次开始时间之前
		List<BizTXjWorkGroup> workGroupList = bizTXjWorkGroupService.getList(csOrgId);
		Date now = new Date();
		Date t1 = DateUtil.stringToDate(DateUtil.dateToString(now,"HHmmss"), "HHmmss");
		Date t2 = null;
		if(workGroupList.size()>0){
			t2 = DateUtil.stringToDate(workGroupList.get(0).getStartTime(), "HHmm");
		}
		if(null!=t2&&t1.before(t2)){
			Calendar cal = Calendar.getInstance();
			cal.setTime(now);
			cal.add(Calendar.DATE, -1);
			now = cal.getTime();
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT T.CX_MAKE_ID,T.ZYP_DATE,T.ZYP_CODE,T.FZR_NAME,T.BELONG_WSC_NAME,T.CXZYP_STATUS  FROM BIZ_T_XJ_ZYP_CX_MAKE T WHERE 1=1");
		sql.append(" AND T.BELONG_WSC_ID='"+csOrgId+"'");// AND T.CXZYP_STATUS='"+Constant.ZYP_STAUTS.AUDITED.getValue()+"' 
		sql.append(" AND T.ZYP_DATE>='"+DateUtil.dateToString(now ,DateUtil.DATE_FORMAT_yyyyMMdd)+"'");
		List<Map<String,Object>> list =  bizTXjZypCxMakeDAO.queryListBySql(sql.toString(),pageBean.getStartIndex(), pageBean.getPageSize(), new HashMap<String,Object>());
		long total = bizTXjZypCxMakeDAO.queryCountBySql("SELECT COUNT(0) FROM ("+sql+")", new HashMap<String,Object>());
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("total", total);
		List<CxMakeListBean> result = new ArrayList<CxMakeListBean>();
		for (Map<String, Object> map : list) {
			CxMakeListBean tmp = new CxMakeListBean();
			tmp.setCxMakeId(NVL(map.get("cx_make_id")));
			tmp.setZypDate(DateUtil.stringToString(NVL(map.get("zyp_date")), "yyyyMMdd", "yyyy-MM-dd"));
			tmp.setZypCode(NVL(map.get("zyp_code")));
			tmp.setFzrName(NVL(map.get("fzr_name")));
			tmp.setBelongWscName(NVL(map.get("belong_wsc_name")));
			String zypStatus = NVL(map.get("cxzyp_status"));
			tmp.setZypStatus(ServiceReceiver.getDictValue1(DictConstant.ZYP_STATUS.getValue(), zypStatus).get(zypStatus).toString());
			result.add(tmp);
		}
		data.put("list", result);
		return data;
	}

	@Override
	public CxMakeDetailBean getZYPDetailForAPP(String cxMakeId,String taskDate,String wscOrgId) {
		String subSql = "";
		if(StringUtils.isNotBlank(taskDate)){
			subSql = " AND T.ZYP_DATE = '"+taskDate+"' AND T.BELONG_WSC_ID='"+wscOrgId+"'";
		}
		if(StringUtils.isNotBlank(cxMakeId)){
			subSql = " AND T.CX_MAKE_ID='"+cxMakeId+"'";
		}
		// 获取作业票基本信息
		String baseInfoSql = "SELECT T.CX_MAKE_ID,T.WARNING, T.ZYP_DATE,T.ZYP_CODE,T.FZR_NAME,T.CXZYP_STATUS  FROM BIZ_T_XJ_ZYP_CX_MAKE T WHERE 1=1 "+subSql;
		List<Map<String,Object>> baseInfo = bizTXjZypCxMakeDAO.queryListBySql(baseInfoSql, new HashMap<String,Object>());
		CxMakeDetailBean detail = new CxMakeDetailBean();
		if(baseInfo.size()==0){
			return null;
		}
		Map<String,Object> baseInfoMap = baseInfo.get(0);
		cxMakeId = NVL(baseInfoMap.get("cx_make_id"));
		// 获取作业票指标组
		String makeTmpSql = "SELECT T.MAKE_TMP_ID, T.CX_MAKE_ID, T.ZXP_TEMP_ITM_ID, T.ZXP_TEMP_ITM_NAME, T.JLXDZ, T.JLSBZ, T.JLQCL, T.BYZD, T.ZXP_TEMP_SORT"
						 + " FROM BIZ_T_XJ_ZYP_CX_MAKE_TMP T WHERE T.CX_MAKE_ID='"+cxMakeId+"'";
		List<BizTXjZypCxMakeTmp> makeTmpList = bizTXjZypCxMakeTmpDAO.queryListBySql(makeTmpSql,  new HashMap<String,Object>(), BizTXjZypCxMakeTmp.class);
		// 获取指标组具体指标
		for (BizTXjZypCxMakeTmp cxMakeTmp : makeTmpList) {
			String cxMakeItemSql = "SELECT T.MAKE_TMP_ITEM_ID, T.MAKE_TMP_ID, T.CXZB_ID, T.CXZB_NAME, T.CXZB_JC, T.JLXDZ, T.JLXDZFD, T.JLSBZ, T.JLQCL, T.BYZD, ZB.CXZB_TBLX FROM BIZ_T_XJ_ZYP_CX_MAKE_TMP_ITEM T "
								 + "  LEFT JOIN BIZ_T_XJ_ZB_ITEM ZB ON ZB.CXZB_ID=T.CXZB_ID WHERE T.MAKE_TMP_ID='"+cxMakeTmp.getMakeTmpId()+"' ORDER BY ZB.CXZB_TBLX DESC, ZB.CXZB_ID ASC ";
			List<Map<String,Object>> makeTmpItemMapList = bizTXjZypCxMakeTmpItemDAO.queryListBySql(cxMakeItemSql, new HashMap<String,Object>());
			List<BizTXjZypCxMakeTmpItem> makeTmpItemList = new ArrayList<BizTXjZypCxMakeTmpItem>();
			for (Map<String, Object> map : makeTmpItemMapList) {
				BizTXjZypCxMakeTmpItem tmpItem = new BizTXjZypCxMakeTmpItem();
				tmpItem.setMakeTmpItemId(NVL(map.get("make_tmp_item_id")));
				tmpItem.setMakeTmpId(NVL(map.get("make_tmp_id")));
				tmpItem.setCxzbId(NVL(map.get("cxzb_id")));
				tmpItem.setCxzbName(NVL(map.get("cxzb_name")));
				tmpItem.setCxzbJc(NVL(map.get("cxzb_jc")));
				tmpItem.setJlxdz(NVL(map.get("jlxdz")));
				Double fd =  StringUtils.isNotBlank(NVL(map.get("jlxdzfd")))?Double.valueOf(NVL(map.get("jlxdzfd"))):0L;
				tmpItem.setJlxdzfd(fd);
				tmpItem.setJlsbz(NVL(map.get("jlsbz")));
				if(StringUtils.isNotBlank(NVL(map.get("jlqcl")))){
					tmpItem.setJlqcl(Double.valueOf(NVL(map.get("jlqcl"))));
				}
				tmpItem.setByzd(NVL(map.get("byzd")));
				tmpItem.setTblx(NVL(map.get("cxzb_tblx")));
				makeTmpItemList.add(tmpItem);
				
			}
			cxMakeTmp.setMakeTmpItemList(makeTmpItemList);
		}
		
		// 获取班次 yyyyMMdd
		String zbDate = NVL(baseInfoMap.get("zyp_date"));
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT PD.DETAIL_NAME, PD.TECHNICS_NAME, PDP.PERSON_NAME");
		sql.append("  FROM BIZ_T_XJ_ZB_PLAN_DETAIL PD,");
		sql.append("       (SELECT REPLACE(WM_CONCAT(TMP.PERSON_NAME), '?', ',') PERSON_NAME,");
		sql.append("               TMP.PLAN_DETAIL_ID");
		sql.append("          FROM BIZ_T_XJ_ZB_PLAN_DETAIL_PERSON TMP");
		sql.append("         GROUP BY TMP.PLAN_DETAIL_ID) PDP");
		sql.append(" WHERE PD.PLAN_DETAIL_ID = PDP.PLAN_DETAIL_ID");
		sql.append("   AND PD.ZB_PLAN_ID IN (SELECT P.ZB_PLAN_ID");
		sql.append("                           FROM BIZ_T_XJ_ZB_PLAN P");
		sql.append("                          WHERE P.ZB_DATE = '"+zbDate+"' AND P.BELONG_WSC_ID='"+wscOrgId+"')");
		List<Map<String,Object>> zbPlanDetailList = jdbcTemplate.queryForList(sql.toString());
		List<ZbPlanInCXMakeBean> zbPlanList = new ArrayList<ZbPlanInCXMakeBean>();
		Map<String,List<ZbPlanInCXMakeDetailBean>> zbPlanDetailMap = new HashMap<String,List<ZbPlanInCXMakeDetailBean>>();
		for (Map<String, Object> map : zbPlanDetailList) {
			List<ZbPlanInCXMakeDetailBean> tmpList = null;
			if(zbPlanDetailMap.containsKey(NVL(map.get("DETAIL_NAME")))){
				tmpList = zbPlanDetailMap.get(NVL(map.get("DETAIL_NAME")));
			}else{
				tmpList = new ArrayList<ZbPlanInCXMakeDetailBean>();
			}
			ZbPlanInCXMakeDetailBean tmp = new ZbPlanInCXMakeDetailBean();
			tmp.setEchnicsName(NVL(map.get("TECHNICS_NAME")));
			tmp.setPersonName(NVL(map.get("PERSON_NAME")));
			tmpList.add(tmp);
			zbPlanDetailMap.put(NVL(map.get("DETAIL_NAME")), tmpList);
		}
		for (String key : zbPlanDetailMap.keySet()) {
			ZbPlanInCXMakeBean zbPlan = new ZbPlanInCXMakeBean();
			zbPlan.setZbPlanName(key);
			zbPlan.setZbDetailList(zbPlanDetailMap.get(key));
			zbPlanList.add(zbPlan);
		}
		
		
		detail.setFzrName(NVL(baseInfoMap.get("fzr_name")));
		String warning = NVL(baseInfoMap.get("warning"));
		detail.setWarning(NVL(ServiceReceiver.getDictValue1(DictConstant.ZYP_WARNING.getValue(),warning).get(warning)));
		detail.setZypCode(NVL(baseInfoMap.get("zyp_code")));
		detail.setZypDate(DateUtil.stringToString(zbDate, "yyyyMMdd", "yyyy-MM-dd"));
		String zypStatus = NVL(baseInfoMap.get("cxzyp_status"));
		detail.setZypStatus(ServiceReceiver.getDictValue1(DictConstant.ZYP_STATUS.getValue(), zypStatus).get(zypStatus).toString());
		detail.setMakeTmpList(makeTmpList);
		detail.setZbPlanList(zbPlanList);
		
		return detail;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getCxMakeList(PageBean pageBean, Map<String, Object> paramMap) {
		StringBuilder sql = new StringBuilder();
		Map<String,Object> params = new HashMap<String,Object>();
		sql.append("SELECT T.CX_MAKE_ID, T.ZYP_DATE, T.ZYP_CODE, T.WARNING, T.WEATHER, T.BYZD, T.TEMP_VERSION, T.CXZYP_STATUS, T.FZR_ID, T.FZR_NAME, T.ZYP_DESC, T.TBRY_ID, T.TBRY_NAME, T.CREATOR_TIMESTEMP, T.BELONG_WSC_ID, T.BELONG_WSC_NAME, T.UPDATOR_ACCOUNT, T.UPDATOR_NAME, T.UPDATE_TIMESTEMP, ");
		sql.append(" (SELECT COUNT(1) FROM BIZ_T_XJ_ZYP_CX_MAKE_HIS H WHERE H.CX_MAKE_ID = T.CX_MAKE_ID) AS HIS_COUNT ");	//是否已产生流程 0：未产生; 
		sql.append(" FROM  BIZ_T_XJ_ZYP_CX_MAKE T ");
		sql.append("  WHERE 1=1 ");
		
		if(MapUtil.hasParam(paramMap, "FZRID")){
			sql.append(" AND T.FZR_ID = '"+paramMap.get("FZRID")+"'");
		}
		
		if(MapUtil.hasParam(paramMap, "belongWscId")){
			// 作业所属厂站List<String>
			sql.append(" AND T.BELONG_WSC_ID IN (:belongWscId)");
			params.put("belongWscId", paramMap.get("belongWscId"));
		}
		if(MapUtil.hasParam(paramMap, "cxzypStatus")){
			// 作业票状态要求为List<String>
			sql.append(" AND T.CXZYP_STATUS IN (:cxzypStatus)");
			params.put("cxzypStatus", paramMap.get("cxzypStatus"));
		}
		if(MapUtil.hasParam(paramMap, "zypDate")){
			sql.append("  AND T.ZYP_DATE=:zypDate");
			params.put("zypDate", paramMap.get("zypDate").toString().replaceAll("-", ""));
		}
		if(MapUtil.hasParam(paramMap, "zypCode")){
			sql.append("  AND T.ZYP_CODE LIKE '%"+paramMap.get("zypCode")+"%'");
		}
		Map<String, Object> result = bizTXjZypCxMakeDAO.queryPaginationListBySql(sql.toString(), params, pageBean);
		List<Map<String,Object>> rows = (List<Map<String, Object>>) result.get("rows");
		Map<String, Object> cxzypStatusDictMap = ServiceReceiver.getDictKeyValueMap(DictConstant.ZYP_STATUS.getValue());
		
		// 将字典编码写文名称,格式化数据
		for (Map<String, Object> map : rows) {
			map.put("cxzyp_status_name", cxzypStatusDictMap.get(NVL(map.get("cxzyp_status"))));
			map.put("zyp_date",DateUtil.stringToString(NVL(map.get("zyp_date")),"yyyyMMdd", "yyyy-MM-dd"));
		}
		
		return result;
	}

	@Override
	public CxMakeDetailPojo getZYPDetailForPC(String cxMakeId) {
		// 获取作业票基本信息
		String baseInfoSql = "SELECT T.CX_MAKE_ID, T.ZYP_DATE,T.ZYP_CODE, T.FZR_ID, T.FZR_NAME,T.WARNING,T.WEATHER, T.UPDATE_TIMESTEMP, T.ZYP_DESC,T.TEMP_VERSION  FROM BIZ_T_XJ_ZYP_CX_MAKE T WHERE T.CX_MAKE_ID='"+cxMakeId+"'";
		List<Map<String,Object>> baseInfo = bizTXjZypCxMakeDAO.queryListBySql(baseInfoSql, new HashMap<String,Object>());
		Map<String,Object> baseInfoMap = baseInfo.get(0);
		// 获取作业票指标组
		String makeTmpSql = "SELECT T.MAKE_TMP_ID, T.CX_MAKE_ID, T.ZXP_TEMP_ITM_ID, T.ZXP_TEMP_ITM_NAME, T.JLXDZ, T.JLSBZ, T.JLQCL, T.BYZD, T.ZXP_TEMP_SORT"
						 + " FROM BIZ_T_XJ_ZYP_CX_MAKE_TMP T WHERE T.CX_MAKE_ID='"+cxMakeId+"' ORDER BY T.ZXP_TEMP_SORT ASC";
		List<BizTXjZypCxMakeTmp> makeTmpList = bizTXjZypCxMakeTmpDAO.queryListBySql(makeTmpSql,  new HashMap<String,Object>(), BizTXjZypCxMakeTmp.class);
		// 获取指标组具体指标
		for (BizTXjZypCxMakeTmp cxMakeTmp : makeTmpList) {
			String cxMakeItemSql = "SELECT T.MAKE_TMP_ITEM_ID, T.MAKE_TMP_ID, T.CXZB_ID, T.CXZB_NAME, T.CXZB_JC, T.JLXDZ, T.JLXDZFD, T.JLSBZ, T.JLQCL, T.BYZD , ZB.CXZB_TBLX FROM BIZ_T_XJ_ZYP_CX_MAKE_TMP_ITEM T "
								 + "  LEFT JOIN BIZ_T_XJ_ZB_ITEM ZB ON ZB.CXZB_ID=T.CXZB_ID WHERE T.MAKE_TMP_ID='"+cxMakeTmp.getMakeTmpId()+"' ORDER BY ZB.CXZB_TBLX DESC, ZB.CXZB_ID ASC ";
			List<Map<String, Object>> makeTmpItemMapList = bizTXjZypCxMakeTmpItemDAO.queryListBySql(cxMakeItemSql, new HashMap<String,Object>());
			List<BizTXjZypCxMakeTmpItem> makeTmpItemList = new ArrayList<BizTXjZypCxMakeTmpItem>();
			for (Map<String, Object> map : makeTmpItemMapList) {
				BizTXjZypCxMakeTmpItem tmpItem = new BizTXjZypCxMakeTmpItem();
				tmpItem.setMakeTmpItemId(NVL(map.get("make_tmp_item_id")));
				tmpItem.setMakeTmpId(NVL(map.get("make_tmp_id")));
				tmpItem.setCxzbId(NVL(map.get("cxzb_id")));
				tmpItem.setCxzbName(NVL(map.get("cxzb_name")));
				tmpItem.setCxzbJc(NVL(map.get("cxzb_jc")));
				tmpItem.setJlxdz(NVL(map.get("jlxdz")));
				Double fd =  StringUtils.isNotBlank(NVL(map.get("jlxdzfd")))?Double.valueOf(NVL(map.get("jlxdzfd"))):0L;
				tmpItem.setJlxdzfd(fd);
				tmpItem.setJlsbz(NVL(map.get("jlsbz")));
				if(StringUtils.isNotBlank(NVL(map.get("jlqcl")))){
					tmpItem.setJlqcl(Double.valueOf(NVL(map.get("jlqcl"))));
				}
				tmpItem.setByzd(NVL(map.get("byzd")));
				tmpItem.setTblx(NVL(map.get("cxzb_tblx")));
				makeTmpItemList.add(tmpItem);
				
			}
			cxMakeTmp.setMakeTmpItemList(makeTmpItemList);
		}
		// 获取作业票填报后的人员考核信息
		List<BizTXjZypCxMakePersonKh> makePersonKhList = bizTXjZypCxMakePersonKhService.getListByCxMakeId(cxMakeId);
		
		CxMakeDetailPojo detail = new CxMakeDetailPojo();
		String zbDate = NVL(baseInfoMap.get("zyp_date"));
		detail.setCxMakeId(NVL(baseInfoMap.get("cx_make_id")));
		detail.setTempVersion(Long.valueOf(NVL(baseInfoMap.get("temp_version"))));
		detail.setFzrName(NVL(baseInfoMap.get("fzr_name")));
		detail.setFzrId(NVL(baseInfoMap.get("fzr_id")));
		detail.setZypDesc(NVL(baseInfoMap.get("zyp_desc")));
		String warning = NVL(baseInfoMap.get("warning"));
		detail.setWarning(warning);
		detail.setWarningName(NVL(ServiceReceiver.getDictValue1(DictConstant.ZYP_WARNING.getValue(),warning).get(warning)));
		detail.setZypCode(NVL(baseInfoMap.get("zyp_code")));
		detail.setZypDate(DateUtil.stringToString(zbDate, "yyyyMMdd", "yyyy-MM-dd"));
		detail.setUpdateTimestemp(NVL(baseInfoMap.get("update_timestemp")));
		detail.setWeatherCode(NVL(baseInfoMap.get("weather")));
		detail.setWeatherName(NVL(ServiceReceiver.getDictValue1(DictConstant.WEATHER_TYPE.getValue(),detail.getWeatherCode()).get(detail.getWeatherCode())));
		detail.setMakeTmpList(makeTmpList);
		detail.setMakePersonKhList(makePersonKhList);
		return detail;
	}

	@Override
	public void updateZypCheck(String cxMakeId, String radio, String content,WSLoginInfoBean loginInfo) {
		BizTXjZypCxMake bizTXjZypCxMake = bizTXjZypCxMakeDAO.findOne(cxMakeId);
		String optCpntent = "";
		String title="";
		String msgContent="";
		String receivePeopleId="";
		if(radio.equals(Constant.ZYP_CHECK.PASS.getValue())){
			bizTXjZypCxMake.setCxzypStatus(Constant.ZYP_STAUTS.AUDITED.getValue());
			optCpntent = "审核通过";
			title="您有一个审核通过的作业票";
			msgContent= "您有一个审核通过的作业票,作业票编号为："+bizTXjZypCxMake.getZypCode();
			receivePeopleId=bizTXjZypCxMake.getFzrId();
		}else if(radio.equals(Constant.ZYP_CHECK.NOPASS.getValue())){
			bizTXjZypCxMake.setCxzypStatus(Constant.ZYP_STAUTS.PROTOCOL.getValue());
			optCpntent = "审核不通过";
			title = "您有一个审核未通过的作业票";
			msgContent = "您有一个审核未通过的作业票,作业票编号为："+bizTXjZypCxMake.getZypCode()+",请尽快处理！";
			receivePeopleId=bizTXjZypCxMake.getTbryId();
		}
		try {
			
			bizTGgMsgManageService.saveMsg(title,msgContent,receivePeopleId,Constant.MSG_IMPORTANT.IMPORTANT.getValue(),loginInfo);
		} catch (Exception e) {
			logger.error("审核作业票发消息出错，错误是："+e.getMessage());
		}
		bizTXjZypCxMake.setUpdatorAccount(loginInfo.getUser().getUserId());
		bizTXjZypCxMake.setUpdatorName(loginInfo.getUser().getUserName());
		bizTXjZypCxMake.setUpdateTimestemp(DateUtil.dateToString(new Date(), "yyyyMMddHHmmss"));
		bizTXjZypCxMakeDAO.save(bizTXjZypCxMake);
		bizTXjZypCxMakeHisService.saveProcess(cxMakeId,optCpntent,content,Constant.ZYP_OPT_TYPE.AUDIT.getValue(),loginInfo);
		
	}

	@Override
	public void updateZypDiscontinue(String cxMakeId, String content,
			WSLoginInfoBean loginInfo) {
		
		BizTXjZypCxMake bizTXjZypCxMake = bizTXjZypCxMakeDAO.findOne(cxMakeId);
		bizTXjZypCxMake.setCxzypStatus(Constant.ZYP_STAUTS.PROTOCOL.getValue());
		bizTXjZypCxMakeDAO.save(bizTXjZypCxMake);
		bizTXjZypCxMakeHisService.saveProcess(cxMakeId,Constant.ZYP_OPT_TYPE.DISCONTINUE.getName(),content,Constant.ZYP_OPT_TYPE.DISCONTINUE.getValue(),loginInfo);
		//获取最近审核人
		String shrId  = "";
		String sql = "select t.opt_id from BIZ_T_XJ_ZYP_CX_MAKE_HIS t where 1=1 and t.cx_make_id='"+bizTXjZypCxMake.getCxMakeId()+"' and t.opt_type='2' order by t.opt_time desc";
		List<Map<String,Object>> shrIds = bizTXjZypCxMakeDAO.queryListBySql(sql, new HashMap<String,Object>());
		if(null != shrIds && shrIds.size()>0){
			shrId = shrIds.get(0).get("opt_id").toString();
		}
		List<String> idList = new ArrayList<String>();
		idList.add(shrId);
		boolean flag1= true;
		boolean flag2= true;
		String msgJsr = "";
		for (String id : idList) {
			if(bizTXjZypCxMake.getTbryId().equals(id)){
				flag1 = false;
				break;
			}
		}
		if(flag1){
			idList.add(bizTXjZypCxMake.getTbryId());
		}
		for (String id : idList) {
			if(bizTXjZypCxMake.getFzrId().equals(id)){
				flag2 = false;
				break;
			}
		}
		if(flag2){
			idList.add(bizTXjZypCxMake.getFzrId());
		}
		for (String id : idList) {
			msgJsr += id +",";
		}
		try {
			
			String title = "你有一个被中止的作业票";
			String msgContent = "你有一个被中止的作业票,作业票编号为："+bizTXjZypCxMake.getZypCode()+",请尽快处理！";
//			String msgJsr = bizTXjZypCxMake.getTbryId()+","+bizTXjZypCxMake.getFzrId()+","+shrId;
			bizTGgMsgManageService.saveMsg(title,msgContent,msgJsr,Constant.MSG_IMPORTANT.IMPORTANT.getValue(),loginInfo);
		} catch (Exception e) {
			logger.error("中止作业票发消息出错，错误是："+e.getMessage());
		}
	}
	

	/**
	 * 作业票填报，并形成流程
	 * @date 2017年10月10日 下午4:59:44
	 * @author 胡毅
	 * @param makeTmpItemJSONList
	 * @param personKhJSONList
	 * @param loginInfo
	 * @param saveType 保存类型 1 仅保存，2 保存并提交
	 * @param cxMakeId
	 * @param weather
	 */
	@Override
	public void saveCxMakeTmpItemSBValue(List<WriteCxMakeSBPojo> makeTmpItemJSONList,List<CxMakePersonHkPojo> personKhJSONList,
								WSLoginInfoBean loginInfo,String saveType,String cxMakeId,String weather) {
		Map<String,Object> pars = new HashMap<String,Object>();
		// 保存上报值
		for (WriteCxMakeSBPojo pojo : makeTmpItemJSONList) {
			String updateSql = "UPDATE BIZ_T_XJ_ZYP_CX_MAKE_TMP_ITEM T SET T.JLSBZ='"+pojo.getJlsbz()+"',T.JLQCL='"+pojo.getJlqcl()+"' WHERE T.MAKE_TMP_ITEM_ID='"+pojo.getMakeTmpItemId()+"'";
			bizTXjZypCxMakeTmpItemDAO.executeSql(updateSql,pars);
		}
		// 保存人员考核值
		for (CxMakePersonHkPojo pojo : personKhJSONList) {
			if(StringUtils.isNotBlank(pojo.getPersonKhId())){
				BizTXjZypCxMakePersonKh old =bizTXjZypCxMakePersonKhService.findOne(BizTXjZypCxMakePersonKh.class, pojo.getPersonKhId());
				old.setKhDesc(pojo.getKhDesc());
				if(StringUtils.isNotBlank(pojo.getKhScore())){
					old.setKhScore(Double.valueOf(pojo.getKhScore()));
				}
//				old.setHaveConfirm(Long.valueOf(pojo.getHaveConfirm()));
				bizTXjZypCxMakePersonKhService.update(old);
			}else{
				BizTXjZypCxMakePersonKh _new = new BizTXjZypCxMakePersonKh();
				_new.setBkhId(pojo.getBkhId());
				_new.setBkhName(pojo.getBkhName());
				_new.setKhDesc(pojo.getKhDesc());
				if(StringUtils.isNotBlank(pojo.getKhScore())){
					_new.setKhScore(Double.valueOf(pojo.getKhScore()));
				}
//				_new.setHaveConfirm(Long.valueOf(pojo.getHaveConfirm()));
				_new.setHaveConfirm(Long.valueOf(Constant.YES_NO.YES.getValue()));
				_new.setCxMakeId(cxMakeId);
				_new.setConfirmTime(new Date());
				bizTXjZypCxMakePersonKhService.save(_new);
			}
		}
		// 保存作业票流程
		BizTXjZypCxMake cxMake = findOne(BizTXjZypCxMake.class, cxMakeId);
		SysUser user = loginInfo.getUser();
		if(StringUtils.equals(saveType, "1")){
			cxMake.setWeather(weather);
			cxMake.setCxzypStatus(Constant.ZYP_STAUTS.WRITING.getValue());
			cxMake.setUpdatorAccount(user.getUserId());
			cxMake.setUpdatorName(user.getUserName());
			cxMake.setUpdateTimestemp(DateUtil.dateToString(new Date(), DateUtil.DATE_FULL_FORMAT_N));
			save(cxMake);
		}
		if(StringUtils.equals(saveType, "2")){
			cxMake.setWeather(weather);
			cxMake.setCxzypStatus(Constant.ZYP_STAUTS.CHECKING.getValue());
			cxMake.setUpdatorAccount(user.getUserId());
			cxMake.setUpdatorName(user.getUserName());
			cxMake.setUpdateTimestemp(DateUtil.dateToString(new Date(), DateUtil.DATE_FULL_FORMAT_N));
			save(cxMake);
			bizTXjZypCxMakeHisService.saveProcess(cxMakeId,Constant.ZYP_OPT_TYPE.WRITE.getName(),Constant.ZYP_OPT_TYPE.WRITE.getName(),Constant.ZYP_OPT_TYPE.WRITE.getValue(),loginInfo);
		}
	}
	
	@Override
	public void updateZypReceive(String cxMakeId, String radioValue,
			String content, WSLoginInfoBean loginInfo) {
		BizTXjZypCxMake bizTXjZypCxMake = bizTXjZypCxMakeDAO.findOne(cxMakeId);
		String optCpntent = "";
		if(radioValue.equals(Constant.ZYP_CHECK.PASS.getValue())){
			bizTXjZypCxMake.setCxzypStatus(Constant.ZYP_STAUTS.CHECKED.getValue());
			optCpntent="验收通过";
		}else if(radioValue.equals(Constant.ZYP_CHECK.NOPASS.getValue())){
			bizTXjZypCxMake.setCxzypStatus(Constant.ZYP_STAUTS.WRITING.getValue());
			optCpntent="验收不通过";
			try {
				
				String title = "你有一个验收未通过的作业票";
				String msgContent = "你有一个验收未通过的作业票,作业票编号为："+bizTXjZypCxMake.getZypCode()+",请尽快处理！";
				bizTGgMsgManageService.saveMsg(title,msgContent,bizTXjZypCxMake.getFzrId(),Constant.MSG_IMPORTANT.IMPORTANT.getValue(),loginInfo);
			} catch (Exception e) {
				logger.error("验收作业票发消息出错，错误是："+e.getMessage());
			}
		}
		bizTXjZypCxMake.setUpdatorAccount(loginInfo.getUser().getUserId());
		bizTXjZypCxMake.setUpdatorName(loginInfo.getUser().getUserName());
		bizTXjZypCxMake.setUpdateTimestemp(DateUtil.dateToString(new Date(), "yyyyMMddHHmmss"));
		bizTXjZypCxMakeDAO.save(bizTXjZypCxMake);
		bizTXjZypCxMakeHisService.saveProcess(cxMakeId,optCpntent,content,Constant.ZYP_OPT_TYPE.ACCEPTANCED.getValue(),loginInfo);
		
	}

	@Override
	public boolean ZypExists(String zypDate, String csOrgId) {
		String sql = "SELECT T.CX_MAKE_ID, T.ZYP_DATE,T.ZYP_CODE,T.FZR_NAME,T.WARNING,T.WEATHER FROM BIZ_T_XJ_ZYP_CX_MAKE T WHERE T.ZYP_DATE = '" + zypDate + "' "
				+ " AND T.BELONG_WSC_ID = '" + csOrgId + "'";
		List<Map<String, Object>> result = bizTXjZypCxMakeDAO.queryListBySql(sql, new String[]{});
		
		if(result.isEmpty()){
			return false;
		}else{
			return true;
		}
	}

	@Override
	public void saveZypcxMake(BizTXjZypCxMake zypCxMake, List<BizTXjZypCxMakeTmp> makeTmpList, String saveType, WSLoginInfoBean loginInfo) {
		
		zypCxMake = bizTXjZypCxMakeDAO.save(zypCxMake);
		
		for (BizTXjZypCxMakeTmp zypCxMakeTmp : makeTmpList) {
			zypCxMakeTmp.setCxMakeId(zypCxMake.getCxMakeId());
			zypCxMakeTmp = bizTXjZypCxMakeTmpDAO.save(zypCxMakeTmp);
			for (BizTXjZypCxMakeTmpItem item : zypCxMakeTmp.getMakeTmpItemList()) {
				item.setMakeTmpId(zypCxMakeTmp.getMakeTmpId());
			}
			bizTXjZypCxMakeTmpItemDAO.save(zypCxMakeTmp.getMakeTmpItemList());
		}
		
		// 保存作业票流程
		if(StringUtils.equals(saveType, "2")){
			bizTXjZypCxMakeHisService.saveProcess(zypCxMake.getCxMakeId(),Constant.ZYP_OPT_TYPE.PREPARED.getName(),Constant.ZYP_OPT_TYPE.PREPARED.getName(),Constant.ZYP_OPT_TYPE.PREPARED.getValue(),loginInfo);
		}
	}

	@Override
	public void saveEditZypcxMake(BizTXjZypCxMake zypCxMake,  List<BizTXjZypCxMakeTmpItem> makeTmpItemList, String saveType, WSLoginInfoBean loginInfo) {

		bizTXjZypCxMakeDAO.save(zypCxMake);
		List<BizTXjZypCxMakeTmpItem> itemList = new ArrayList<BizTXjZypCxMakeTmpItem>();
		for (BizTXjZypCxMakeTmpItem item : makeTmpItemList) {
			BizTXjZypCxMakeTmpItem itemOri = bizTXjZypCxMakeTmpItemDAO.findOne(item.getMakeTmpItemId());
			itemOri.setJlxdz(item.getJlxdz());
			itemOri.setJlxdzfd(item.getJlxdzfd());
			itemList.add(itemOri);
//			String updateSql = "UPDATE BIZ_T_XJ_ZYP_CX_MAKE_TMP_ITEM T SET T.JLXDZ='"+item.getJlxdz()+"',T.JLXDZFD='"+item.getJlxdzfd()+"' WHERE T.MAKE_TMP_ITEM_ID='"+item.getMakeTmpItemId()+"'";
//			bizTXjZypCxMakeTmpItemDAO.executeSql(updateSql,pars);
		}
		bizTXjZypCxMakeTmpItemDAO.save(itemList);
		// 保存作业票流程
		if(StringUtils.equals(saveType, "2")){
			bizTXjZypCxMakeHisService.saveProcess(zypCxMake.getCxMakeId(),Constant.ZYP_OPT_TYPE.PREPARED.getName(),Constant.ZYP_OPT_TYPE.PREPARED.getName(),Constant.ZYP_OPT_TYPE.PREPARED.getValue(),loginInfo);
		}
	}

	@Override
	public void delZypCxMake(String cxMakeId) {
		
		String itemSql = "DELETE BIZ_T_XJ_ZYP_CX_MAKE_TMP_ITEM TI WHERE TI.MAKE_TMP_ID IN (SELECT T.MAKE_TMP_ID FROM BIZ_T_XJ_ZYP_CX_MAKE_TMP T WHERE T.CX_MAKE_ID = '" + cxMakeId + "')";
		String tmpSql = "DELETE BIZ_T_XJ_ZYP_CX_MAKE_TMP T WHERE T.CX_MAKE_ID = '" + cxMakeId + "'";
		bizTXjZypCxMakeTmpItemDAO.executeSql(itemSql, new HashMap<String,Object>());
		bizTXjZypCxMakeTmpDAO.executeSql(tmpSql, new HashMap<String,Object>());
		bizTXjZypCxMakeDAO.delete(findOne(BizTXjZypCxMake.class, cxMakeId));
	}

	@Override
	public BizTXjZypCxMake getCxMakeLast(String csOrgId, Long version) {
		// TODO Auto-generated method stub
		StringBuilder sql = new StringBuilder();
		Map<String,Object> params = new HashMap<String,Object>();
		sql.append("SELECT T.CX_MAKE_ID, T.ZYP_DATE, T.ZYP_CODE, T.WARNING, T.WEATHER, T.BYZD, T.TEMP_VERSION, T.CXZYP_STATUS, T.FZR_ID, T.FZR_NAME, T.ZYP_DESC, T.TBRY_ID, T.TBRY_NAME, T.CREATOR_TIMESTEMP, T.BELONG_WSC_ID, T.BELONG_WSC_NAME, T.UPDATOR_ACCOUNT, T.UPDATOR_NAME, T.UPDATE_TIMESTEMP ");
		sql.append(" FROM  BIZ_T_XJ_ZYP_CX_MAKE T ");
		sql.append("  WHERE 1=1 ");
		if(null != version){
			sql.append(" AND T.TEMP_VERSION = :version ");
			params.put("version", version);
		}
		if(StringUtils.isNotBlank(csOrgId)){
			sql.append(" AND T.BELONG_WSC_ID = :csOrgId ");
			params.put("csOrgId", csOrgId);
		}
		sql.append(" ORDER BY T.ZYP_DATE DESC ");
		
		List<BizTXjZypCxMake> makeList = bizTXjZypCxMakeDAO.queryListBySql(sql.toString(), params, BizTXjZypCxMake.class);
		
		BizTXjZypCxMake lastMake = null;
		if(!makeList.isEmpty()){
			lastMake = makeList.get(0);
		}
		
		return lastMake;
	}
}