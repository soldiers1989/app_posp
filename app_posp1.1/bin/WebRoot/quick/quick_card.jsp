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
	<form action="${pageContext.request.contextPath }/quickPayAction/quickCardScan.action" method="post">
	    <input type="text" name="v_version" value="1.0.0.0"> <br>
	    <input type="text" name="v_mid" value="10032061473">商户号<br>
	    <input type="text" name="v_oid" value="<%=UtilDate.PayRandomOrder()%>">商户订单号(不为空，只允许使用字母、数字、-、_,并以字母或数字开头，长度小于32，成功支付的订单不允许重复支付)<br>
		<input type="text" name="v_time" value="<%=UtilDate.getOrderNum()%>">订单时间(不为空,一共14 位，格式为：年[4位]月[2 位]日[2 位]时[2 位]分[2 位]秒[2位])<br>
		<input type="text" name="v_userId" value="<%=System.currentTimeMillis()%>">userId<br>
		<input type="text" name="v_cert_no" value="120224199303303413">证件号<br>
		<input type="text" name="v_realName" value="高立明">帐户名<br>
		<input type="text" name="v_phone" value="18322276803">手机号<br>
		<input type="text" name="v_cardNo" value="6228480020721272316"><br>
		<select name="v_accountType">
			<option value="1" selected="selected">借记卡</option>
			<option value="2">贷记卡</option>
		</select><br>		 
		<input type="text" name="v_expired" value="">卡有效期(信用卡是需要填写)<br>
		<input type="text" name="v_cvn2" value="">卡背面三位数(信用卡是需要填写)<br>
		
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
<title>快捷绑卡获取短信获取签名</title>
</head>
<body>
	<form id="pay_form" action="../quickPayAction/quickCardScan.action" method="post">
	   	版本号:<input type="text" name="v_version" value=""> <br>
	            商户号:<input type="text" name="v_mid" value=""><br>
	            订单号:<input type="text" name="v_oid" value=""><br>
		请求时间:<input type="text" name="v_time" value=""><br>
		卡号:<input type="text" name="v_cardNo" value=""><br>
		卡姓名:<input type="text" name="v_realName" value=""><br>
		卡类型:<input type="text" name="v_accountType" value="0"><br>		 
		证件号:<input type="text" name="v_cert_no" value=""><br>
		手机号码:<input type="text" name="v_phone" value=""><br>
		cvn2:<input type="text" name="v_cvn2" value=""><br>
		信用卡有效期:<input type="text" name="v_expired" value=""><br>
		用户id:<input type="text" name="v_userId" value=""><br>
		<input type="submit" value="提交">
>>>>>>> branch 'master' of https://github.com/xinhemaijie/app_posp.git
	</form>
</body>
</html>
