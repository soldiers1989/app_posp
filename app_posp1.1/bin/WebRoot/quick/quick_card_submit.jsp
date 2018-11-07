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
	<form action="${pageContext.request.contextPath }/quickPayAction/quickCard.action" method="post">
	    <input type="text" name="v_version" value="${temp.v_version}"> <br>
	    <input type="text" name="v_mid" value="${temp.v_mid}">商户号<br>
	    <input type="text" name="v_oid" value="${temp.v_oid}">商户订单号(不为空，只允许使用字母、数字、-、_,并以字母或数字开头，长度小于32，成功支付的订单不允许重复支付)<br>
		<input type="text" name="v_time" value="${temp.v_time}">订单时间(不为空,一共14 位，格式为：年[4位]月[2 位]日[2 位]时[2 位]分[2 位]秒[2位])<br>
		<input type="text" name="v_userId" value="${temp.v_userId}">userId<br>
		<input type="text" name="v_cert_no" value="${temp.v_cert_no}">证件号<br>
		<input type="text" name="v_realName" value="${temp.v_realName}">帐户名<br>
		<input type="text" name="v_phone" value="${temp.v_phone}">手机号<br>
		<input type="text" name="v_cardNo" value="${temp.v_cardNo}"><br>
		<input type="hidden" name="v_accountType" value="${temp.v_accountType}"><br>		  
		<input type="text" name="v_expired" value="${temp.v_expired}">卡有效期(信用卡是需要填写)<br>
		<input type="text" name="v_cvn2" value="${temp.v_cvn2}">卡背面三位数(信用卡是需要填写)<br>
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
>>>>>>> branch 'master' of https://github.com/xinhemaijie/app_posp.git
	</form>
</body>
</html>
