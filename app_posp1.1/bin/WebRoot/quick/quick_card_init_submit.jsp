<<<<<<< HEAD
<%@ page language="java" contentType="text/html; charset=UTF-8" import="xdt.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>商户订单数据(模拟)</title>
</head>
<body>
	商户订单数据
	<form action="${pageContext.request.contextPath }/quickPayAction/quickCardInit.action" method="post">
	    <input type="text" name="v_version" value="${temp.v_version}"> <br>
	    <input type="text" name="v_mid" value="${temp.v_mid}">商户号<br>
	    <input type="text" name="v_oid" value="${temp.v_oid}">商户订单号(不为空，只允许使用字母、数字、-、_,并以字母或数字开头，长度小于32，成功支付的订单不允许重复支付)<br>
		<input type="text" name="v_time" value="${temp.v_time}">订单时间(不为空,一共14 位，格式为：年[4位]月[2 位]日[2 位]时[2 位]分[2 位]秒[2位])<br>
		<input type="text" name="v_verifyId" value="${temp.v_verifyId}">协议号<br>
		<input type="text" name="v_smsCode" value="${temp.v_smsCode}">验证码<br>
		<input type="text" name="v_sign" value="${temp.v_sign}"><br>
		<input type="submit" value="sharingPay">
=======
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
>>>>>>> branch 'master' of https://github.com/xinhemaijie/app_posp.git
	</form>
</body>
</html>
