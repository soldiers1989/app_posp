<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>快捷绑卡获取短信</title>
</head>
<body onload="sharingPay()">
	<form id="pay_form" action="../quickPayAction/message.action" method="post">
	    <input type="text" name="v_version" value="${temp.v_version}"> <br>
	    <input type="text" name="v_mid" value="${temp.v_mid}"><br>
	    <input type="text" name="v_oid" value="${temp.v_oid}"><br>
		<input type="text" name="v_time" value="${temp.v_time}"><br>
		<input type="text" name="v_cardNo" value="${temp.v_cardNo}"><br>
		<input type="text" name="v_realName" value="${temp.v_realName}"><br>
		<input type="text" name="v_accountType" value="${temp.v_accountType}"><br>		 
		<input type="text" name="v_cert_no" value="${temp.v_cert_no}"><br>
		<input type="text" name="v_phone" value="${temp.v_phone}"><br>
		<input type="text" name="v_cvn2" value="${temp.v_cvn2}"><br>
		<input type="text" name="v_expired" value="${temp.v_expired}"><br>
		<input type="text" name="v_userId" value="${temp.v_userId}"><br>
		<input type="submit" value="提交">
	</form>
</body>
</html>
