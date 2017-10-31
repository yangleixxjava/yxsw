<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="path" value="${pageContext.request.contextPath}"></c:set>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>设施设备关联配置</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@include file="/system/common/header_splitmode.jsp" %>
<!--布局控件start-->
<script type="text/javascript" src="${path}/libs/js/nav/layout.js"></script>
<!--数据表格start-->
<script src="${path}/libs/js/table/quiGrid.js" type="text/javascript"></script>
<!-- 表单start -->
<script src="${path}/libs/js/form/form.js" type="text/javascript"></script>
<!--弹窗组件start-->
<script type="text/javascript" src="${path}/libs/js/popup/drag.js"></script>
<script type="text/javascript" src="${path}/libs/js/popup/dialog.js"></script>
<!-- 日期选择框start -->
<script type="text/javascript" src="${path}/libs/js/form/datePicker/WdatePicker.js"></script>
<!-- 树组件start -->
<script type="text/javascript" src="${path}/libs/js/tree/ztree/ztree.js"></script>
<link type="text/css" rel="stylesheet" href="${path}/libs/js/tree/ztree/ztree.css"></link>
<!-- 树形下拉框start -->
<script type="text/javascript" src="${path }/libs/js/form/selectTree.js"></script>
<script type="text/javascript" src="${path }/js/ssattachsb/eq_list.js"></script>
<script type="text/javascript" src="${path }/js/plug-in/stringutil.js"></script>

<script type="text/javascript">
	var path = "${path}";
	var EQ_TYPE = JSON.parse('${EQ_TYPE }');
	var ZY_STATUS = JSON.parse('${ZY_STATUS }');
</script>
</head>	
<body>
	<div id="layout1">
		<div id="centerCon" position="center" style="">
	    	<div class="padding5">
				<form action="${path}/" id="queryForm" method="post">
					<table style="width:100%">
						<tr>
							<td style="width:25%;text-align:right">设备名称：</td>
							<td style="width:16%;text-align:left"><input type="text" id="sbName" class="input119" name="sbName" value="${sbName }"/></td>
							<td style="width:12;text-align:right">在用状态：</td>
							<td style="width:18%;text-align:left">
								<select labelField="value" valueField="key" selWidth="143" name="zyStatus" prompt="请选择" selectedValue="${zyStatus }" url="${path}/platform/getSupportData" params='{"parentNodeId":"UM000005"}'></select>
							</td>
							<td rowspan="2" valign="bottom" style="text-align:left">
								<button type="button" onclick="searchHandler()" id="search"><span class="icon_find">查询</span></button>
								<button type="button" onclick="resetSearch()"><span class="icon_reload">重置</span></button>
							</td>
						</tr>
						<tr>
							<td style="text-align:right">设备类别：</td>
							<td style="text-align:left"><select labelField="key" valueField="value" selWidth="143" name="sbSort" prompt="请选择" selectedValue="${sbSort }" url="${path}/platform/getSupportData" params='{"parentNodeId":"UM000002"}'></select></td>
							<td style="text-align:right">设备类型：</td>
							<td style="text-align:left"><div name="sbTypeId" class="selectTree" selWidth="143" selectedvalue="${sbTypeId }" boxHeight="200" url="${path }/equipmentType/getEqTypeTreeAll?clickExpand=false"></div></td>
						</tr>
					</table>
					</form>
				</div>
				<div class="padding_right5">
					<div id="dataBasic"></div>
				</div>
	        </div>
		</div>
	</body>
</html>