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
	</form>
</body>
</html>
