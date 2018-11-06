<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>快捷绑卡验证短信</title>
</head>
<body onload="sharingPay()">
	<form id="pay_form" action="../quickPayAction/quickCardInit.action" method="post">
	    <input type="text" name="v_version" value="${temp.v_version}"> <br>
	    <input type="text" name="v_mid" value="${temp.v_mid}"><br>
	    <input type="text" name="v_oid" value="${temp.v_oid}"><br>
		<input type="text" name="v_time" value="${temp.v_time}"><br>
		绑卡协议号:<input type="text" name="v_verifyId" value="${temp.v_verifyId}"><br>
		短信验证码:<input type="text" name="v_smsCode" value="${temp.v_smsCode}"><br>
		<input type="submit" value="提交">
	</form>
</body>
</html>
