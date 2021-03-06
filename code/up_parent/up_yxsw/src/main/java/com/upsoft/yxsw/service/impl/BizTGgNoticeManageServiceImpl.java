package com.upsoft.yxsw.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upsoft.login.support.webservice.ServiceReceiver;
import com.upsoft.system.bean.PageBean;
import com.upsoft.system.bean.WSLoginInfoBean;
import com.upsoft.system.constant.CommonConstant;
import com.upsoft.system.entity.ComTAttachment;
import com.upsoft.system.entity.SysUser;
import com.upsoft.system.service.AttachmentService;
import com.upsoft.system.service.BaseServiceImpl;
import com.upsoft.system.util.DateUtil;
import com.upsoft.yxsw.constant.Constant;
import com.upsoft.yxsw.constant.DictConstant;
import com.upsoft.yxsw.dao.BizTGgNoticeManageDAO;
import com.upsoft.yxsw.entity.BizTGgNoticeManage;
import com.upsoft.yxsw.mobile.bean.AttachmentPathAndNameBean;
import com.upsoft.yxsw.service.BizTGgNoticeManageService;


@Service
public class BizTGgNoticeManageServiceImpl extends BaseServiceImpl implements BizTGgNoticeManageService {

	@Autowired
	private BizTGgNoticeManageDAO bizTGgNoticeManageDAO;
	@Autowired
	private AttachmentService  attachmentService;
	
	@Override
	public Map<String, Object> getNoticeData(PageBean pageBean,
			Map<String, Object> params,WSLoginInfoBean loginInfo) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT D1.DATA1 IMPORTANT_LEVEL_NAME,D2.DATA1 GG_TYPE_NAME,D3.DATA1 IS_ALIVE_NAME,M.IS_ALIVE,M.NOTICE_ID,M.TITLE,M.CREATE_TIMESTEMP,M.LIMIT_DATE ");
		sql.append(" FROM BIZ_T_GG_NOTICE_MANAGE M ");
		sql.append(" LEFT JOIN SYS_DICT_TREE_DATA D1 ON D1.PARENTNODEID='"+DictConstant.IMPORTANT_LEVEL.getValue()+"' AND D1.CODE = M.IMPORTANT_LEVEL ");
		sql.append(" LEFT JOIN SYS_DICT_TREE_DATA D2 ON D2.PARENTNODEID='"+DictConstant.NOTICE_TYPE.getValue()+"' AND D2.CODE = M.GG_TYPE ");
		sql.append(" LEFT JOIN SYS_DICT_TREE_DATA D3 ON D3.PARENTNODEID='"+DictConstant.CHECKITEM_SFMR.getValue()+"' AND D3.CODE = M.IS_ALIVE ");
		sql.append(" WHERE 1=1 ");
		sql.append(" AND M.BELONG_WSC_ID IN ");
		sql.append(" (SELECT O.ORGID FROM SYS_ORG O START WITH O.ORGID = '" + loginInfo.getCsOrgId() + "' CONNECT BY PRIOR O.ORGID = O.PARENTORGID)");
		
		if(null != params){
			if(params.containsKey("title")){
				sql.append(" AND M.TITLE LIKE '%"+params.get("title").toString()+"%' ");
			}
			if(params.containsKey("ggType")){
				sql.append(" AND M.GG_TYPE = '"+ params.get("ggType").toString() +"' ");
			}
			if(params.containsKey("importantLevel")){
				sql.append(" AND M.IMPORTANT_LEVEL = '"+ params.get("importantLevel").toString() +"' ");
			}
			if(params.containsKey("isAlive")){
				sql.append(" AND M.IS_ALIVE = '"+ params.get("isAlive").toString() +"' ");
			}
			if(params.containsKey("startTime")){
				sql.append(" AND M.CREATE_TIMESTEMP >= '"+ params.get("startTime").toString().replaceAll("-", "") + "000000" +"' ");
			}
			if(params.containsKey("endTime")){
				sql.append(" AND M.CREATE_TIMESTEMP <= '"+ params.get("endTime").toString().replaceAll("-", "") + "235959" +"' ");
			}
		}
		sql.append(" ORDER BY M.IS_ALIVE DESC, M.CREATE_TIMESTEMP DESC ");
		 
		return bizTGgNoticeManageDAO.queryPaginationListBySql(sql.toString(), new HashMap<String,Object>(), pageBean);
	}
	
	@Override
	public void saveNotice(BizTGgNoticeManage bizTGgNoticeManage, WSLoginInfoBean loginInfo,String attachmentList) {
		String limitDate = bizTGgNoticeManage.getLimitDate();
		Date date = new Date();
		limitDate = limitDate.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
		bizTGgNoticeManage.setLimitDate(limitDate);
		bizTGgNoticeManage.setCreatorAccount(loginInfo.getUser().getUserId());
		bizTGgNoticeManage.setCreatorName(loginInfo.getUser().getUserName());
		bizTGgNoticeManage.setCreateTimestemp(DateUtil.dateToString(date, "yyyyMMddHHmmss"));
		bizTGgNoticeManage.setBelongDept(loginInfo.getUser().getOrgId());
		bizTGgNoticeManage.setBelongWscId(loginInfo.getCsOrgId());
		bizTGgNoticeManage.setBelongWscName(loginInfo.getCsOrgName());
		bizTGgNoticeManage.setUpdatorAccount(loginInfo.getUser().getUserId());
		bizTGgNoticeManage.setUpdatorName(loginInfo.getUser().getUserName());
		bizTGgNoticeManage.setUpdateTimestemp(DateUtil.dateToString(date, "yyyyMMddHHmmss"));
		
		BizTGgNoticeManage nBizTGgNoticeManage = bizTGgNoticeManageDAO.save(bizTGgNoticeManage);
		//保存附件
		String noticeId = nBizTGgNoticeManage.getNoticeId();
		attachmentService.saveAttachment(noticeId, attachmentList, loginInfo);
	}

	@Override
	public int updateNoticeByIds(List<String> idArray, SysUser user) {
		int result = 0;
		for (String id : idArray) {
			BizTGgNoticeManage bizTGgNoticeManage = bizTGgNoticeManageDAO.findOne(id);
			bizTGgNoticeManage.setIsAlive((Long.parseLong(CommonConstant.STATUS_DISABLE)));
			String userId = user.getUserId();
			String userName = user.getUserName();
			Date date = new Date();
			bizTGgNoticeManage.setUpdatorAccount(userId);
			bizTGgNoticeManage.setUpdatorName(userName);
			bizTGgNoticeManage.setUpdateTimestemp(DateUtil.dateToString(date, "yyyyMMddHHmmss"));
			
			bizTGgNoticeManageDAO.save(bizTGgNoticeManage);
			result ++;
		}
		return result;
	}

	@Override
	public BizTGgNoticeManage findOneById(String id) {
		
		BizTGgNoticeManage bizTGgNoticeManage = new BizTGgNoticeManage();
		if(StringUtils.isNotBlank(id)){
			bizTGgNoticeManage = bizTGgNoticeManageDAO.findOne(id);
		}
		return bizTGgNoticeManage;
	}

	@Override
	public void updateNotice(BizTGgNoticeManage bizTGgNoticeManage, WSLoginInfoBean loginInfo,String attachmentList,String delAttachment) {
		
		String limitDate = bizTGgNoticeManage.getLimitDate();
		Date date = new Date();
		limitDate = limitDate.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
		bizTGgNoticeManage.setLimitDate(limitDate);
		
		bizTGgNoticeManage.setUpdatorAccount(loginInfo.getUser().getUserId());
		bizTGgNoticeManage.setUpdatorName(loginInfo.getUser().getUserName());
		bizTGgNoticeManage.setUpdateTimestemp(DateUtil.dateToString(date, "yyyyMMddHHmmss"));
		
		bizTGgNoticeManageDAO.save(bizTGgNoticeManage);
		
		//保存附件
		String noticeId = bizTGgNoticeManage.getNoticeId();
		attachmentService.saveAttachment(noticeId, attachmentList, loginInfo);
		//删除附件
		attachmentService.delAttachment(delAttachment);
	}

	@Override
	public Map<String,Object> getNoticeData(PageBean pageBean, SysUser user,String contextPath) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT T.LIMIT_DATE, T.IS_ALIVE, T.TITLE_ICO, T.NOTICE_DESC, T.CREATE_TIMESTEMP, T.CREATOR_ACCOUNT, T.CREATOR_NAME, T.BELONG_DEPT, T.BELONG_WSC_ID, T.BELONG_WSC_NAME, T.UPDATOR_ACCOUNT, T.UPDATOR_NAME, T.UPDATE_TIMESTEMP, T.NOTICE_ID, T.TITLE, T.CONTENT, T.IMPORTANT_LEVEL, T.GG_TYPE");
		sql.append(" FROM BIZ_T_GG_NOTICE_MANAGE T WHERE 1=1 AND T.IS_ALIVE='"+Constant.YES_NO.YES.getValue()+"' ");
		// 获取需要查询的机构（反向追溯）
		String orgSql = "SELECT O.ORGID FROM SYS_ORG O START WITH O.ORGID = '" + user.getOrgId() + "' CONNECT BY PRIOR O.PARENTORGID = O.ORGID";
		List<Map<String,Object>> orgList = ServiceReceiver.getListDataBySql(orgSql, new HashMap<String,Object>());
		List<String> orgIdList = new ArrayList<String>(); 
		for (Map<String, Object> map : orgList) {
			orgIdList.add(map.get("orgid").toString());
		}
		Map<String,Object> params = new HashMap<String,Object>();
		sql.append(" AND T.BELONG_WSC_ID IN (:ORGIDLIST)");
		params.put("ORGIDLIST", orgIdList);
		
		sql.append(" ORDER BY T.IMPORTANT_LEVEL DESC,T.CREATE_TIMESTEMP DESC");
		// 查询公告数据
		List<BizTGgNoticeManage> list = bizTGgNoticeManageDAO.queryListBySql(sql.toString(),pageBean.getStartIndex(), pageBean.getPageSize(), params, BizTGgNoticeManage.class);
		if(list.size()==0){
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("total",0);
			data.put("list", new ArrayList<BizTGgNoticeManage>());
			return data;
		}
		long total = bizTGgNoticeManageDAO.queryCountBySql("SELECT COUNT(1) FROM ("+sql.toString()+")", params);
		Map<String, Object> importantLevelDictMap = ServiceReceiver.getDictKeyValueMap(DictConstant.IMPORTANT_LEVEL.getValue());
		Map<String, Object> noticeTypeDictMap = ServiceReceiver.getDictKeyValueMap(DictConstant.NOTICE_TYPE.getValue());
		List<String> noticeIdList = new ArrayList<String>();
		for (BizTGgNoticeManage notice : list) {
			noticeIdList.add(notice.getNoticeId());
			notice.setImportantLevelName(importantLevelDictMap.get(notice.getImportantLevel()).toString());
			notice.setNoticeTypeName(noticeTypeDictMap.get(notice.getGgType()).toString());
		}
		// 查询附件
		List<ComTAttachment> attachments = attachmentService.getAttachmentList(noticeIdList);
		Map<String,List<AttachmentPathAndNameBean>> attachmentMap = new HashMap<String,List<AttachmentPathAndNameBean>>();
		for (ComTAttachment atta : attachments) {
			List<AttachmentPathAndNameBean> tmpList = null;
			if(attachmentMap.containsKey(atta.getBusinessId())){
				tmpList = attachmentMap.get(atta.getBusinessId());
			}else{
				tmpList = new ArrayList<AttachmentPathAndNameBean>();
			}
			AttachmentPathAndNameBean tmp = new AttachmentPathAndNameBean();
			tmp.setName(atta.getOldAttachmentName()+"."+atta.getAttachmentSuffix());
			tmp.setPath(contextPath+atta.getAttachmentPath());
			tmpList.add(tmp);
			attachmentMap.put(atta.getBusinessId(), tmpList);
		}
		// 组装返回数据
		for (BizTGgNoticeManage notice : list) {
			notice.setAttachments(attachmentMap.get(notice.getNoticeId()));
		}
		
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("total",total);
		data.put("list", list);
		return data;
	}

	@Override
	public List<BizTGgNoticeManage> getTopTwoNotice(String orgId,String basePath) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT T.LIMIT_DATE, T.IS_ALIVE, T.TITLE_ICO, T.NOTICE_DESC, T.CREATE_TIMESTEMP, T.CREATOR_ACCOUNT, T.CREATOR_NAME, T.BELONG_DEPT, T.BELONG_WSC_ID, T.BELONG_WSC_NAME, T.UPDATOR_ACCOUNT, T.UPDATOR_NAME, T.UPDATE_TIMESTEMP, T.NOTICE_ID, T.TITLE, T.CONTENT, T.IMPORTANT_LEVEL, T.GG_TYPE");
		sql.append(" FROM BIZ_T_GG_NOTICE_MANAGE T WHERE 1=1 AND T.IS_ALIVE='"+Constant.YES_NO.YES.getValue()+"' ");
		// 获取需要查询的机构（反向追溯）
		String orgSql = "SELECT O.ORGID FROM SYS_ORG O START WITH O.ORGID = '" + orgId + "' CONNECT BY PRIOR O.PARENTORGID = O.ORGID";
		List<Map<String,Object>> orgList = ServiceReceiver.getListDataBySql(orgSql, new HashMap<String,Object>());
		List<String> orgIdList = new ArrayList<String>(); 
		for (Map<String, Object> map : orgList) {
			orgIdList.add(map.get("orgid").toString());
		}
		Map<String,Object> params = new HashMap<String,Object>();
		sql.append(" AND T.BELONG_WSC_ID IN (:ORGIDLIST)");
		params.put("ORGIDLIST", orgIdList);
		
		sql.append(" ORDER BY T.IMPORTANT_LEVEL DESC,T.CREATE_TIMESTEMP DESC");
		// 查询公告数据
		List<BizTGgNoticeManage> list = bizTGgNoticeManageDAO.queryListBySql(sql.toString(), 0,6,params, BizTGgNoticeManage.class);
		
		List<String> noticeIdList = new ArrayList<String>();
		for (BizTGgNoticeManage notice : list) {
			noticeIdList.add(notice.getNoticeId());
		}
		if(noticeIdList.size()==0){
			return list;
		}
		// 查询附件
		List<ComTAttachment> attachments = attachmentService.getAttachmentList(noticeIdList);
		Map<String,List<AttachmentPathAndNameBean>> attachmentMap = new HashMap<String,List<AttachmentPathAndNameBean>>();
		for (ComTAttachment atta : attachments) {
			List<AttachmentPathAndNameBean> tmpList = null;
			if(attachmentMap.containsKey(atta.getBusinessId())){
				tmpList = attachmentMap.get(atta.getBusinessId());
			}else{
				tmpList = new ArrayList<AttachmentPathAndNameBean>();
			}
			AttachmentPathAndNameBean tmp = new AttachmentPathAndNameBean();
			tmp.setName(atta.getOldAttachmentName()+"."+atta.getAttachmentSuffix());
			tmp.setPath(basePath+atta.getAttachmentPath());
			tmpList.add(tmp);
			attachmentMap.put(atta.getBusinessId(), tmpList);
		}
		// 组装返回数据
		for (BizTGgNoticeManage notice : list) {
			notice.setAttachments(attachmentMap.get(notice.getNoticeId()));
		}
		return list;
	}

	@Override
	public List<BizTGgNoticeManage> getDqGgList() {
		Date date = new Date();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT * FROM BIZ_T_GG_NOTICE_MANAGE T WHERE T.IS_ALIVE='1' AND T.LIMIT_DATE <= '"+ DateUtil.dateToString(date, "yyyyMMddHHmm") +"' ");
		return bizTGgNoticeManageDAO.queryListBySql(sql.toString(), new Object[]{}, BizTGgNoticeManage.class);
	}

	@Override
	public void saveCXTaskBySchedule(List<BizTGgNoticeManage> list) {
		for (BizTGgNoticeManage bizTGgNoticeManage : list) {
			bizTGgNoticeManage.setIsAlive(Long.parseLong(CommonConstant.STATUS_DISABLE));
			
			bizTGgNoticeManageDAO.save(bizTGgNoticeManage);
		}
	}
}
