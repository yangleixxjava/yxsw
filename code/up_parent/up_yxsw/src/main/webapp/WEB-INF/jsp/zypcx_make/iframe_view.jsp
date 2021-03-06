<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>作业票详情和流程</title>
<%@include file="/system/common/header_splitmode.jsp" %>
<!--基本选项卡start-->
<script type="text/javascript" src="${path }/libs/js/nav/basicTab.js"></script>
<!--基本选项卡end-->
<link href="${path }/css/core.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
jQuery(function(){
	var height0 = jQuery("body").height();
	var list_btmH = jQuery(".list-btm").outerHeight();
	jQuery(".basicTab").height(height0-list_btmH);
	var basicTab_topH = jQuery(".basicTab_top").outerHeight();
	jQuery(".basicTab_con").height((height0-list_btmH)-(basicTab_topH+12));
});

function goBack(){
    window.location = path+"${backURL}?queryParam="+encodeURIComponent('${queryParam }');
}
</script>
<style type="text/css">
.basicTab_con{
	overflow: hidden !important;
}
.basicTab_con iframe{
	height:100% !important;
}

</style>
</head>
<body>
	<div class="basicTab" style="height: 100%;" iframeMode="true" 
				data='{"list":[{"name":"作业票详情","url":"${path }/zypcxMake/toView?cxMakeId=${cxMakeId}"},
		      			{"name":"处理流程","url":"${path }/cxmake/detailAndProcess/process?cxMakeId=${cxMakeId}"}]}'>
	    <div>
	        <IFRAME scrolling="no" width="100%" style="overflow: hidden;" frameBorder=0 id=frmrightChild name=frmrightChild onload="iframeHeight('frmrightChild')" allowTransparency="true"></IFRAME>
	    </div>
	</div>
	<!-- 底部按钮 -->
	<div class="list-btm">
	  	<div class="list-btm-div">
	        <div class="div-btn-sbt">
<!-- 				<button class="btm-button" type="button"  onclick="saveAndSubmit(1);"><span class="ok-icon">保存</span></button> -->
<!-- 				<button class="btm-button" type="button"  onclick="saveAndSubmit(2);"><span class="ok-icon">保存并提交</span></button> -->
				<button class="btm-button" type="button"  onclick="goBack();"><span class="back-icon">返回</span></button>
	        </div>
	    </div>
	</div>
		<script type="text/javascript">
		function saveAndSubmit(saveType){
			window.frames[0].saveAndSubmit(saveType, goBack);
		}
		
		function goBack(){
			location.href = "${backURL}";
		}
	</script>
</body>
</html>