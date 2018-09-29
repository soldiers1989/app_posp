<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body >
<center><!-- style="display: none;" -->
	<font>正在跳转请稍等...</font>
	<form action="<%=request.getParameter("pay_url") %>" method="post" id="from" style="display: none;" ><!-- -->
		<input type="text" name="partner" value="<%=request.getParameter("partner") %>"/>
		<input type="text" name="out_trade_no" value="<%=request.getParameter("out_trade_no") %>"/>
		<input type="text" name="total_fee" value="<%=request.getParameter("total_fee") %>"/>
		<input type="text" name="currency_type" value="<%=request.getParameter("currency_type") %>"/>
		<input type="text" name="return_url" value="<%=request.getParameter("return_url") %>"/>
		<input type="text" name="notify_url" value="<%=request.getParameter("notify_url") %>"/>
		<input type="text" name="pay_id" value="<%=request.getParameter("pay_id") %>"/>
		<input type="text" name="version" value="<%=request.getParameter("version") %>"/>
		<input type="text" name="sign_type" value="<%=request.getParameter("sign_type") %>"/>
		<input type="text" name="certId" value="<%=request.getParameter("certId") %>"/>
		<input type="text" name="sign" value="<%=request.getParameter("sign") %>"/>
	</form>
</center>
<script type="text/javascript">
	 // $("#from").submit();  
	  document.getElementById("from").submit();
</script>
</body>

</html>
