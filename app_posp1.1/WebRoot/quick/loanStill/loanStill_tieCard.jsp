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
	<form action="${pageContext.request.contextPath }/loanStill/tieCard/ls/sign.action" method="post">
	    <input type="text" name="v_version" value="1.0.0.0"> <br>
	    <input type="text" name="v_mid" value="100341512318531">商户号<br>
	    <input type="text" name="v_organizationId" value="683453865000203">机构编号<br>
	    <input type="text" name="v_oid" value="<%=UtilDate.PayRandomOrder()%>">商户订单号(不为空，只允许使用字母、数字、-、_,并以字母或数字开头，长度小于32，成功支付的订单不允许重复支付)<br>
		<input type="text" name="v_orderInfo" value="测试商品12">商品名称<br>
		<input type="text" name="v_cardNo" value="6222380049124441">卡号<br>
		<input type="text" name="v_realName" value="刘文静">帐户名<br>
		<input type="text" name="v_cert_no" value="120104198505133824">证件号<br>
		<input type="text" name="v_cvn2" value="805">卡背面三位数(信用卡是需要填写)<br>
		<input type="text" name="v_phone" value="13602199513">手机号<br>
	    <input type="text" name="v_notify_url" value="http://60.28.24.164:8102/app_posp/TFBController/returnUrl.action">支付结果前台通知页面(不为空，必须是合法URL,字节数不超过256)<br>
		<input type="text" name="v_url" value="http://60.28.24.164:8102/app_posp/TFBController/returnUrl.action">支付结果后台通知地址(必须是合法URL,字节数不超过256)<br>
		<input type="submit" value="sharingPay">
	</form>
</body>
</html>
