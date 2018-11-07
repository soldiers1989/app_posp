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
	<form action="${pageContext.request.contextPath }/loanStill/register/ls/sign.action" method="post">
	    <input type="text" name="v_version" value="1.0.0.0">版本号 <br>
	    <input type="text" name="v_mid" value="100341512318531">商户号<br>
	    <input type="text" name="v_merName" value="重庆小面馆">商户名称<br>
	      <input type="text" name="v_settleType" value="PERSONAL">结算类型<br>
	    <input type="text" name="v_settleCardNo" value="6230520020020273971">结算卡号<br>
		<input type="text" name="v_settleName" value="刘文静">结算帐户名<br>
		<input type="text" name="v_settlePmsBankNo" value="103110025032">接算卡联行号<br>
		<input type="text" name="v_settlePhone" value="13602199513">结算帐手机号<br>
		<input type="text" name="v_legalName" value="刘文静">法人姓名<br>
		<input type="text" name="v_legalContact" value="13602199513">法人手机号<br>
		<input type="text" name="v_legalId" value="120104198505133824">法人证件号<br>
	    <input type="text" name="v_idStartTime" value="2018-10-01">身份证生效日期<br>
		<input type="text" name="v_idEndTime" value="2018-10-01">身份证失效日期<br>
		 <input type="text" name="v_licenseNo" value="123123123123123">营业执照号<br>
		<input type="text" name="v_licenseName" value="243463836">营业执照名<br>
		<input type="text" name="v_licenseStartTime" value="2018-01-01">营业执照生效日期<br>
		<input type="text" name="v_licenseEndTime" value="2018-01-01">营业执照失效日期<br>
		<input type="text" name="v_bizDomain" value="243463836">经营范围<br>		 
		<input type="text" name="v_mainBusiness" value="243463836">主营业务<br>
		<input type="text" name="v_prodType" value="000006">产品类型<br>
		<input type="text" name="v_rate" value="0.0061">借记卡费率<br>
		<input type="text" name="v_cRate" value="0.0061">贷记卡费率<br>
		<input type="submit" value="sharingPay">
	</form>
</body>
</html>
