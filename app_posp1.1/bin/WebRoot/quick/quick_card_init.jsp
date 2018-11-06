<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>快捷绑卡验证短信获取签名</title>
</head>
<body>
	<form id="pay_form" action="../quickPayAction/quickCardScanInit.action" method="post">
	   	版本号:<input type="text" name="v_version" value=""> <br>
	            商户号:<input type="text" name="v_mid" value=""><br>
	            订单号:<input type="text" name="v_oid" value=""><br>
		请求时间:<input type="text" name="v_time" value=""><br>
		绑卡协议号:<input type="text" name="v_verifyId" value=""><br>
		短信验证码:<input type="text" name="v_smsCode()" value=""><br>
		<input type="submit" value="提交">
	</form>
</body>
</html>
