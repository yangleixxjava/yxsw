<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="path" value="${pageContext.request.contextPath}"></c:set>
 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>作业票流程</title>
<link href="${path }/css/core.css" rel="stylesheet" type="text/css"/>
<script src="${path }/js/jquery-1.7.2.min.js" type="text/javascript"></script>
<script type="text/javascript">
	jQuery(function(){
		jQuery(".done:eq(0)").addClass("selected");
		jQuery(".done").on("click",function(){
			var lis = jQuery(".plc-ul .done");
			jQuery.each(lis,function(i,n){
				jQuery(n).removeClass("selected");
			});
			jQuery(this).addClass("selected");
		});
	});
</script>
<style type="text/css">
*{margin: 0;padding: 0;border: 0;}
.plc-detail .detail-ul table tr th,
.plc-detail .detail-ul table tr td {
	vertical-align: middle;
}
</style>
</head>

<body>
	<div class="list-max">
        <!-- end  -->
        <!--plc-content  -->
        <div class="plc-content" style="bottom: 0px;">
            <!-- plc-ul -->
            <div class="plc-ul"  style="bottom: 0px;">
                <ul>
                	<c:forEach items="${processNav }" var="nav" varStatus="i">
                		<li class="done">
                			<span class="plc-ul-time">
	                            <table>
	                                <tr>
	                                    <td>${nav[0] }<br/>${nav[1] }</td>
	                                </tr>
	                            </table>                        
	                        </span>
	                        <span class="plc-ul-icon">
	                            <span class="plc-smallicon"></span>
	                        </span>
	                        <span class="plc-ul-status">
	                            <a href="#a${i.index+1 }">${nav[2]}</a>
	                        </span>
                		</li>
                	</c:forEach>
                </ul>
            </div>
            <!-- plc-detail -->
            <div class="plc-detail">
                <ul class="detail-ul">
                	<c:forEach items="${processContent }" var="content" varStatus="i">
	                    <li style="height: inherit;">
	                        <a name="a${i.index+1 }">${content.title }</a>
	                        <table>
	                            <tr>
	                            	<th>操作人：</th>
	                                <td>${content.creator }</td>
	                                <th>操作内容：</th>
	                                <td>${content.optContent }</td>
	                            </tr>
	                            <tr >
	                            	<th>操作时间：</th>
	                                <td>${content.createTime }</td>
	                            </tr>
	                            <tr >
	                                <th>操作说明：</th>
	                                <td colspan="3">${content.description }</td>
	                            </tr>
	                        </table>
	                    </li>
                	</c:forEach>
                </ul>
            </div>
        </div>
    </div>   
</body>
</html>
