package com.upsoft.yxsw.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upsoft.system.bean.PageBean;
import com.upsoft.system.service.BaseServiceImpl;
import com.upsoft.yxsw.dao.BizTSbssRelationDAO;
import com.upsoft.yxsw.dao.BizTXjCxTaskItemDAO;
import com.upsoft.yxsw.dao.BizTXjCxTaskItemSbssDAO;
import com.upsoft.yxsw.dao.BizTXjdItemDao;
import com.upsoft.yxsw.entity.BizTXjdItem;
import com.upsoft.yxsw.service.MobileScanService;
@Service
public class SbssScanServiceImpl extends BaseServiceImpl implements MobileScanService{

	@Autowired
	private BizTSbssRelationDAO bizTSbssRelationDAO;
	@Autowired
	private BizTXjCxTaskItemSbssDAO bizTXjCxTaskItemSbssDAO;
	@Autowired
	private BizTXjdItemDao bizTXjdItemDao;
	@Autowired
	private BizTXjCxTaskItemDAO bizTXjCxTaskItemDAO;
	
	@Override
	public Map<String, Object> getSbss(String sbssCode) {
		String sql = "SELECT T.ID,T.CODE,T.NAME,T.BYZD,T.BELONG_WSC_ID,T.BELONG_WSC_NAME,T.DEL_FLAG,T.SB_OR_SS FROM V_SB_SS T "
				+ " WHERE 1=1 "
				+ " AND T.CODE = '"+ sbssCode +"'";
		List<Map<String,Object>> list = bizTSbssRelationDAO.queryListBySql(sql, new HashMap<String,Object>());
		if(null != list && list.size() > 0){
			return list.get(0);
		}else{
			return new HashMap<String,Object>();
		}
	}

	@Override
	public Map<String, Object> getXjRecordListOnMobile(String sbId,PageBean bean,
			Map<String, Object> params) {
		StringBuffer sql = new StringBuffer();
	   
		sql.append(" SELECT T.*, F_GET_CX_TASK_PERSONS(S.CX_TASK_ID) AS PERSON_NAME FROM BIZ_T_XJ_CX_TASK_ITEM_SBSS T,BIZ_T_XJ_CX_TASK s,BIZ_T_XJ_CX_TASK_ITEM i");
		sql.append(" WHERE 1=1 ");
		sql.append(" AND S.CX_TASK_ID = I.TASK_ID ");
		sql.append(" AND I.TASK_ITEM_ID = T.TASK_ITEM_ID ");
		sql.append(" AND T.HAVE_COMPLETE = '1' ");
		sql.append(" AND T.SBSS_ID = '"+ sbId +"'"); 
		if (params.containsKey("startTime1")) {
			sql.append(" AND T.OPT_TIME >='"+String.valueOf(params.get("startTime1")).replace("-","")+"000000'");
		}
		if (params.containsKey("endTime1")) {
			sql.append(" AND T.OPT_TIME <='"+String.valueOf(params.get("endTime1")).replace("-","")+"235959'");
		}
		if (params.containsKey("content")) {
			sql.append(" AND T.XJ_DESC LIKE '%" +params.get("content") + "%' ");
		}
		if (params.containsKey("status")) {
			sql.append(" AND T.HAVE_COMPLETE = '" +params.get("status") + "' ");
		}
		sql.append(" ORDER BY T.OPT_TIME DESC ");
		return bizTXjCxTaskItemSbssDAO.queryPaginationListBySql(sql.toString(), new HashMap<String, Object>(), bean);
	}

	@Override
	public BizTXjdItem getXjItem(String rfidCode) {
		
		String sql = "SELECT * FROM BIZ_T_XJD_ITEM T WHERE 1=1 AND T.DEL_FLAG='0' AND T.RFID_CODE='"+ rfidCode +"'";
		List<BizTXjdItem>	list = bizTXjdItemDao.queryListBySql(sql, new HashMap<String,Object>(), BizTXjdItem.class);
		if(null != list && list.size() > 0){
			return list.get(0);
		}else{
			return null;
		}
	}

	@Override
	public Map<String, Object> getXjItemRecordListOnMobile(String xjItemId,
			PageBean bean, Map<String, Object> params) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT T.* FROM BIZ_T_XJ_CX_TASK_ITEM T ,BIZ_T_XJ_CX_TASK S ");
		sql.append(" WHERE 1=1 ");
		sql.append(" AND T.TASK_ID = S.CX_TASK_ID AND S.CX_TASK_STATUS = '3' ");
		sql.append(" AND T.XJD_ITEM_ID = '"+ xjItemId +"'"); 
		if (params.containsKey("startTime1")) {
			sql.append(" AND T.OPT_TIME >='"+String.valueOf(params.get("startTime1")).replace("-","")+"000000'");
		}
		if (params.containsKey("endTime1")) {
			sql.append(" AND T.OPT_TIME <='"+String.valueOf(params.get("endTime1")).replace("-","")+"235959'");
		}
		if (params.containsKey("content")) {
			sql.append(" AND T.XJD_ITEM_ADDRESS LIKE '%" +params.get("content") + "%' ");
		}
		sql.append(" ORDER BY T.OPT_TIME DESC ");
		
		return bizTXjCxTaskItemDAO.queryPaginationListBySql(sql.toString(), new HashMap<String, Object>(), bean);
	}

}
