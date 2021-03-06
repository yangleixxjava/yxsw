<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="path" value="${pageContext.request.contextPath}"></c:set>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>安全提醒定义</title>
<!-- 框架分离模式start -->
<link rel="stylesheet" type="text/css" href="${path }/libs/css/import_basic.css"/>
<link rel="stylesheet" type="text/css" id="skin" prePath="${path }/" splitMode="true" href="${path }/libs/skins/blue/style.css"/>
<link rel="stylesheet" type="text/css" id="customSkin" href="${path }/system/layout/skin/style.css"/>
<script type="text/javascript" src="${path }/libs/js/jquery.js"></script>
<script type="text/javascript" src="${path }/libs/js/language/cn.js"></script>
<script type="text/javascript" src="${path }/libs/js/framework.js"></script>
<script type="text/javascript" src="${path }/libs/js/main.js"></script>
<script type="text/javascript" src="${path }/libs/js/table/quiGrid.js"></script>
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
<!-- 树形下拉框start -->
<script type="text/javascript" src="${path }/libs/js/form/selectTree.js"></script>

<script type="text/javascript" src="${path }/js/dateutil.js"></script>

<style>
.l-layout-center{
    border:none!important;
}
.l-layout-left{
    border-bottom:none!important;
}
.l-layout-drophandle-left{
    width: 10px;
}

.ex-icon{
	width:1.2rem;
	height:1.2rem;
	text-align:center;
	line-height:1.2rem;
	border-radius:50%;
	background:#f00;
	color:#fff;
	border:solid 1px #f00;
	font-size:0.7rem;
	top:0;
	/* right:0.2rem; */
}
</style>
</head>
<body>
	<div id="layout1">
		<div id="centerCon" position="center" style="">
        	<div class="box2" panelTitle="查询" showStatus="false">
			<form action="${path}/warning/getWarningList" id="queryForm" method="post">
			<input type="hidden" id="orderCloseFlag" name="orderCloseFlag" value="2"/>
			<input type="hidden" id="warningTypeDefault" name="warningTypeDefault" value="1,8,9"/>
			<input type="hidden" id="initStartTime" value="${initStartTime }" />
			<input type="hidden" id="initEndTime" value="${initEndTime }" />
			<table style="width:100%">
				<tr>
				
					<tr>
					
					<td  style="width:15%;text-align:right">安全提醒标题：</td>
					<td  style="width:15%;text-align:left"><input type="text" id="title" class="input119" name="title" value="${title }"/></td>
					<td  style="width:15%;text-align:right">安全提醒内容：</td>
					<td  style="width:15%;text-align:left"><input type="text" id="content" class="input119" name="content" value="${content}"/></td>
				</tr>
					<td  style="width:15%;text-align:right">提醒类型：</td>
					<td  style="width:15%;text-align:left"><select  selWidth="143"  labelField="value"  name="warningType" prompt="请选择" selectedValue="${warningType }" url="${path}/platform/getSupportData" params='{"parentNodeId":"UM000008"}'></select></td>
					<td  style="width:15%;text-align:right">是否重要提醒：</td>
					<td  style="width:15%;text-align:left"><select  selWidth="143"  labelField="value"  name="is_important" prompt="请选择" selectedValue="${is_important }" url="${path}/platform/getSupportData" params='{"parentNodeId":"UM000006"}'></select></td>
				
					<!-- <td rowspan="2" style="text-align: left;"  valign="bottom">
						<button type="button" onclick="searchHandler()" ><span class="icon_find" style="font-size: 3px" >查询</span></button>
						<button type="button" onclick="resetSearch()"><span class="icon_reload" style="font-size: 3px">重置</span></button>
					</td> -->
					   <td rowspan="2" style="text-align:left;" valign="bottom">
                        <button type="button" onclick="searchHandler()"><span class="icon_find" style="font-family: 宋体;font-size: 12px">查询</span></button>
                        <button type="button" onclick="resetSearch()"><span class="icon_reload" style="font-family: 宋体;font-size: 12px">重置</span></button>
                    </td>
				</tr>
				
			</table>
			</form>
			</div>
			<div class="padding_right5">
				<div id="dataBasic"></div>
			</div>
        </div>
	</div>
<script type="text/javascript">
	
	var grid = null;
	var typeId='${typeId}'//通过结构类型判断是否渲染图标
	    var  trueOrFalse    
	    if(typeId<=2){
	      
	        trueOrFalse=false
	    }else{
	        
	        trueOrFalse=true 
	    }
	
	//框架初始化函数
	function initComplete(){
		//初始化grid组件
		var params = [
						{name:"warningType",value:"${warningType }"},
						{name:"is_important",value:"${is_important }"},
						{name:"reportTimeStart",value:"${reportTimeStart }"},
						{name:"reportTimeEnd",value:"${reportTimeEnd }"},
						{name:"title",value:"${title}"}
				]
		initGrid(params);
	}
	
	//初始化grid组件
	function initGrid(params){
		grid = $("#dataBasic").quiGrid({
			columns:[	
					    { display: '安全提醒标题', name: 'title', align: 'center', width: "25%", showTitle:true,
					    	
					    },
					   { display: '安全提醒内容', name: 'content', align: 'left', width:"25%", showTitle:true},
					    { display: '是否重要提醒', name: 'is_important', type:"important", align: 'center', width: "25%",  showTitle:true},
					    { display: '提醒类型', name: 'tx_type',type:"txtype",   align: 'center', width: "25%", showTitle:true},
					    { display: '操作',	isAllowHide: false, align: 'left', width:80,
						
			                 render:function(rowdata,rowindex,value,column){
			                	 var html='<div class="padding_top4 padding_left5">'
			                		 + '<span class="img_list hand" title="查看" onclick="onView(\'' + rowdata.warning_id + '\')"></span>';
			                		 
			                		 if(typeId>2){
			                			 
			                		 
			                		html +='<span class="img_edit hand" title="修改" onclick="upWarning(\'' + rowdata.warning_id + '\')"></span>' 
			                		 html += '<span class="img_delete hand" title="删除" onclick="onDelete(\'' + rowdata.warning_id+'\')"></span>'
			                		 }
			                		 return html;
			                	 
			                 } 
			            }
					  
					  
					   
		    		],
		    url: '${path}/warning/getWarningList',
		    params:params,
		    rownumbers:true,
		    checkbox:false,
		    selectRowButtonOnly:false,
		    height: '100%',
		    width:"100%",
		    pageSize:20,
		    percentWidthMode:true,		 
		    toolbar:{
		           	 items:[
		           	        {text: '新增', click: addWarning, iconClass: 'icon_add',visible:trueOrFalse},
		           	      
						   ]
		   }
		});
	}
	
	//grid时间row格式化
    jQuery.quiDefaults.Grid.formatters['time'] = function (value, column) {
		
		return dateStr2Str(value,"$1-$2-$3 $4:$5");
	}
	//对类型格式化
    $.quiDefaults.Grid.formatters['important'] = function (value, column) {
		if(value=="1"){	
		return "是"	;
		}else if (value=="0"){		
		return "否";
		}else{
			
			return "出现未知错误";
		}
	
    }
  //对重要性格式化
    $.quiDefaults.Grid.formatters['txtype'] = function (value, column) {
       if(value=="1"){
    	   return "设备安全";
       }
       else if(value=="2"){
    	   return "设施安全"; 
       }else if(value=="3"){ 
    	   return " 巡检点安全";
       }else{
    	   return "出现未知错误";
       }
       
    }

	//查询
    function searchHandler(){
    	var query = $("#queryForm").formToArray(); 
		grid.setOptions({ params : query});
		//页号重置为1
		grid.setNewPage(1);
		//刷新表格数据 
		grid.loadData();
    }
	
	//重置查询
	function resetSearch(){
		$("#queryForm select").attr("selectedValue","");
		$("#queryForm select").resetValue();
		$("#title").val("");
		$("#reportTimeStart").val("");
		$("#reportTimeEnd").val("");
		$("#content").val("");
		searchHandler();
		
	}
	
	 function onView(rowid){
			Dialog.open({
				URL:"${path }/warning/toShowWarning?warningId=" + rowid,
				Title:"查看",Width:1000, Height:1000});
		}

/*  function addWarning() {
		var diag = new Dialog();
		diag.Title = "新增";
		diag.URL = "${path }/warning/toAddWarning";
		diag.ShowButtonRow=true;
		diag.Height = 421;
		diag.Width = 650;
		diag.OkButtonText=" 确 定 ";
		 diag.OKEvent = function(){
			diag.innerFrame.contentWindow.submitThisForm();
		}; 
		diag.show();
	} */
 
 function addWarning(){
	 
	 location.href= "${path}/warning/toAddWarning";
	 
	 
 }
/*  function upWarning(rowid) {
		var diag = new Dialog();
		diag.Title = "修改";
		diag.URL = "${path }/warning/toUpWarning?warningId=" + rowid;
		diag.ShowButtonRow=true;
		diag.Height = 521;
		diag.Width = 650;
		diag.OkButtonText=" 确 定 ";
		 diag.OKEvent = function(){
			diag.innerFrame.contentWindow.submitThisForm();
		}; 
		diag.show();
	} */
	
	
	function upWarning(rowid){
		
		location.href="${path }/warning/toUpWarning?warningId=" + rowid;
	}
	
	//批量删除
	function deleteUnit() {

	    var rows = grid.getSelectedRows();
	    var rowsLength = rows.length;
	    var messageIds = "";
	    if(rowsLength == 0) {
	        top.Dialog.alert("请选中要删除的消息!");
	        return;
	    }
	    for(var i = 0 ; i < rowsLength ; i++ ){
	    	messageIds += ( rows[i].messageid + "," );
	    }
	    top.Dialog.confirm("确定要删除吗？",function(){
	            $.post("${path}/message/deleteMessage",
	                    //获取所有选中行
	                    { "messageIds" : messageIds.substring(0,messageIds.length - 1)},
	                    function(result){
	                        handleResult(result.status);
	                    },
	                    "json");
	        });
	}
	
/* 	//标记、删除后的提示
	function handleResult(result){
	if(result == 1){
		Dialog.alert("删除成功！",null,null,null,1);
		grid.loadData();
	}else{
		Dialog.alert("删除失败！");
	}
} */
function handleResult(result){
	if(result == 1){
		Dialog.alert("删除成功！",null,null,null,1);
		grid.loadData();
	}else if(result==0){
		Dialog.alert("该安全提醒已经被使用，不可删除");
	}else{	
		Dialog.alert("删除失败")
	}
}
	//发送(新增)消息
	function addUnit(){
		var diag = new Dialog();
		diag.Title = "发送系统消息";
		diag.URL = "${path}/message/toAddMessage";
		diag.ShowButtonRow=true;
		diag.Height = 180;
		diag.Width = 430;
		diag.OkButtonText=" 确 定 ";
		diag.OKEvent = function(){
			diag.innerFrame.contentWindow.submitThisForm();
		};
		diag.show();
	}
	//批量删除
	function deleteUnit() {
		var rows = grid.getSelectedRows();
		var rowsLength = rows.length;
		if(rowsLength == 0) {
			Dialog.alert("请选中要删除的记录!");
			return;
		}else{
			for (var i = 0; i < rows.length; i++) {
				if("admin" == rows[i].loginid){
					Dialog.alert("您要删除的行中包含管理员用户<br />　　　请剔除管理员用户");
					return;
				}
			}
		}
		Dialog.confirm("确定要删除吗？",function(){
			$.post("${path }/warning/delWarning",
					//获取所有选中行
					getSelectIds(grid),
					function(result){
						handleResult(result.status);
					},
					"json");
		});
	}
	/* 
	删除一列
	*/
	function onDelete(rowid){
		Dialog.confirm("确定要删除该记录吗？",function(){
		  	//删除记录
		  	$.post("${path }/warning/delOneWarning",
		  			{"id":rowid},
		  			function(result){
		  				handleResult(result.status);
					},"json");
			});
	}
	//获取所有选中行获取选中行的id 格式为 ids=1&ids=2 
	function getSelectIds(grid) {
		var selectedRows = grid.getSelectedRows();
		var selectedRowsLength = selectedRows.length;
		var ids = "";
		
		for(var i = 0;i<selectedRowsLength;i++) {
			ids += selectedRows[i].warning_id+ ",";
			
			
			console.log(selectedRows[i]);
		}
		return {"ids":ids};
	}
	//刷新表格数据并重置排序和页数
	function refresh(isUpdate){
		if(!isUpdate){
			//重置排序
	    	grid.options.sortName='uid';
	    	grid.options.sortOrder="desc";
	    	//页号重置为1
			grid.setNewPage(1);
		}
		grid.loadData();
	}
</script>
</body>
</html>