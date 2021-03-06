<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="path" value="${pageContext.request.contextPath}"></c:set>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>人员巡检情况统计</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@include file="/system/common/header_splitmode.jsp"%>
<!--树组件start -->
<script type="text/javascript" src="${path}/libs/js/tree/ztree/ztree.js"></script>
<link href="${path}/libs/js/tree/ztree/ztree.css" rel="stylesheet"
	type="text/css" />
<!--弹窗组件start-->
<script type="text/javascript" src="${path}/libs/js/popup/drag.js"></script>
<script type="text/javascript" src="${path}/libs/js/popup/dialog.js"></script>
<!--布局控件start-->
<script type="text/javascript" src="${path }/libs/js/nav/layout.js"></script>
<!--布局控件end-->
<!--echart图start  -->
<script type="text/javascript" src="${path }/libs/js/echarts/echarts.js"></script>
<!--echart图end  -->
<!--基本选项卡start-->
<script type="text/javascript" src="${path }/libs/js/nav/basicTab.js"></script>
<!--基本选项卡end-->
<script type="text/javascript"
	src="${path }/libs/js/form/datePicker/WdatePicker.js"></script>
<!--引入chart 配置项  start-->
<script type="text/javascript" src="${path }/libs/js/form/selectTree.js"></script>
<script type="text/javascript" src="${path}/libs/js/tree/ztree/ztree.js"></script>
<link href="${path}/libs/js/tree/ztree/ztree.css" rel="stylesheet" type="text/css"/>
<!-- 树形下拉框start -->
<script type="text/javascript" src="${path }/libs/js/form/selectTree.js"></script>

<script type="text/javascript" src="${path }/js/dateutil.js"></script>
<!--引入chart 配置项    start-->
<style>
.l-layout-center {
	border: none !important;
}

.l-layout-left {
	border-bottom: none !important;
}

.l-layout-drophandle-left {
	width: 10px;
}

/* iframe{border: red 1px solid; */
/*     margin-bottom: 100px;} */
</style>


</head>
<body>
 <div id="layout1" style="width:100%">
	  <div id="centerCon" position="center" style="width:100%">
	  	<div class="box2" panelTitle="设备统计" showStatus="false"> 
				<form action="${path}/piss/init" id="queryForm" method="post">
					<table align="center">
				
						<tr>
						<td>设备类型：</td>
						<td>
						<div name="sbTypeId" id="sbTypeId" class="selectTree" selectedvalue="${sbTypeId}" selWidth="120" boxHeight="200" url="${path }/equipmentType/getEquipmentTypeTree"></div>
						</td>
					
						<td>所属厂站：</td>
						<td>
							<select labelField="value" valueField="key" selWidth="120" id="_select" name="csOrgId" prompt="请选择" selectedValue="${csOrgId}" url="${path}/SsSafe/getSupportData" ></select>
						</td>
						<td>
						设备状态：
						</td>
						<td>
				    	<select labelField="value" valueField="key" class="validate[required]" selWidth="143" name="sbStatus" prompt="请选择" selectedValue="${sbStatus}" url="${path}/platform/getSupportData" params='{"parentNodeId":"UM000005"}'></select>
		    			</td>
						<td >
						<button type="button" onclick="searchHandler()" id="search"><span class="icon_find">查询</span></button>
						<button type="button" onclick="resetSearch()"><span class="icon_reload">重置</span></button>
						<button  id="disBtn"   type="button" onclick="back()"><span  class="back-icon">返回</span></button>
					</td>
						</tr>
					</table>
				</form>
				</div>
				</div>
				</div>

	  <div name="数据" >
				    <div class="padding_right5">
					<div id="data">
							<table class="tableStyle" mode="list">

								
							</table>
						</div>
					</div>
			    </div>
	
	
</body>
<script type="text/javascript">
jQuery(function(){

	putList();
	
	var backUrl='${backUrl}';
		var disBtn=$("#disBtn");
	if(backUrl==''){
	    disBtn.hide();
		
	}else{
		 disBtn.show();
	}
});
	
   function putList(){
	    var jsonStr1 = '${csNameList }';
		var objBc = 	$.parseJSON(jsonStr1);
		var jsonStr2 = '${data }';
		var objGyd = 	$.parseJSON(jsonStr2);
		var jsonStr3= '${sbTypeIdList}';
		var objTypeId = $.parseJSON(jsonStr3);
		var jsonStr4= '${listA}';
		var listA=$.parseJSON(jsonStr4);
		console.log(listA+"listA");
		var listul = $(".tableStyle");
		var list = '<tr align="center">';
		if('[]'==jsonStr2){
			
			list+='<td width="12.5%">'+"暂无数据"+'</td>';
		}else{
		list += '<td width="12.5%">设备类型</td>';
		
		
		for(var i =0 ; i < objBc.length;i++){
			list += '<td width="12.5%">'+objBc[i]+'</td>';
		}
		}
		list += '</tr>';
		for(var i =0 ; i < objGyd.length;i++){
			var mm = objGyd[i];
			var id=objTypeId[i];
		    var a=listA[i]
				list += '<tr>';
			for(var j=0; j<mm.length;j++){
				if(j == 0){
					if(a=='1'){
						list += '<td align="center"  width="12.5%" class="business"><a onClick="getSbDetail(\''+id+'\');" href="#">'+mm[j]+'</a></td>';
					}else{
						
						list += '<td align="center"  width="12.5%" class="business"><a   style="text-decoration: none;color:#000">'+mm[j]+'</a></td>';
					}
					//list += '<td align="center"  width="12.5%" class="business"><a onClick="getSbDetail(\''+id+'\');" href="#">'+mm[j]+'</a></td>';
				}else{
					list += '<td align="center" width="12.5%">'+mm[j]+'</td>';
				}
			}
				list += '</tr>';
		}
		listul.append(list);
		
   }
   
  
	   function searchHandler(){
		 	 $("#queryForm").attr("action","${path}/sbtj/init");
			var query = $("#queryForm").toArray(); 
			jQuery("#queryForm").submit();
			
			console.log(query+"array"); 
			 
	
		
			
 	}
    
	
	//重置查询
	function resetSearch(){
		$("#queryForm select").attr("selectedValue","");
		$("#queryForm select").resetValue();
		$("#sbTypeId").attr("selectedValue","");
		$("#sbTypeId").resetValue();
		searchHandler();
		
	}
	
   //返回上一级
   function back(){
	   
	 
	 location.href='${path}/sbtj/init';
	
   }
function getSbDetail(id){
	var obj={};
	obj.id=id;
	 $.ajax({
		type:'POST',
		url:'${path}/sbtj/validate',
		data:obj,
		success:function(data){
			console.log(data.status)
			if(data.status=='1'){
				   
			        location.href='${path}/sbtj/init?id=' + id+"&backUrl=1"; 
			      
			}
			else{
				Dialog.alert("暂无数据");
			}
			
		}
		 
		 
	 });
 
}
</script>
</html>