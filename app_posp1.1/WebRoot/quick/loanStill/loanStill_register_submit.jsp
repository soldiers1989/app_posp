<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
<body onload="sharingPay()">
	<form id="pay_form" action="${pageContext.request.contextPath }/loanStill/register/ls/submit.action" method="post">
	    <input type="hidden" name="v_version" value="${temp.v_version}"> <br>
	    <input type="hidden" name="v_mid" value="${temp.v_mid}"><br>
	    <input type="hidden" name="v_merName" value="${temp.v_merName}"><br>
	    <input type="hidden" name="v_settleType" value="${temp.v_settleType}"><br>
		<input type="hidden" name="v_settleCardNo" value="${temp.v_settleCardNo}"><br>
		<input type="hidden" name="v_settleName" value="${temp.v_settleName}"><br>
		<input type="hidden" name="v_settlePmsBankNo" value="${temp.v_settlePmsBankNo}"><br>
		<input type="hidden" name="v_settlePhone" value="${temp.v_settlePhone}"><br>
		<input type="hidden" name="v_legalName" value="${temp.v_legalName}"><br>
		<input type="hidden" name="v_legalContact" value="${temp.v_legalContact}"><br>		 
		<input type="hidden" name="v_legalId" value="${temp.v_legalId}"><br>
		<input type="hidden" name="v_idStartTime" value="${temp.v_idStartTime}"><br>
		<input type="hidden" name="v_idEndTime" value="${temp.v_idEndTime}"><br>
		<input type="hidden" name="v_licenseNo" value="${temp.v_licenseNo}"><br>
		<input type="hidden" name="v_licenseName" value="${temp.v_licenseName}"><br>
		<input type="hidden" name="v_licenseStartTime" value="${temp.v_licenseStartTime}"><br>
	    <input type="hidden" name="v_licenseEndTime" value="${temp.v_licenseEndTime}"><br>
		<input type="hidden" name="v_bizDomain" value="${temp.v_bizDomain}"><br>
		<input type="hidden" name="v_mainBusiness" value="${temp.v_mainBusiness}"><br>
		<input type="hidden" name="v_prodType" value="${temp.v_prodType}"><br>
		<input type="hidden" name="v_rate" value="${temp.v_rate}"><br>
		<input type="hidden" name="v_cRate" value="${temp.v_cRate}"><br>
		<input type="hidden" name="v_sign" value="${temp.v_sign}"><br>
		
	</form>
</body>
<script type="text/javascript">

  function sharingPay(){
	  
	  document.all.pay_form.submit();
  }
</script>
</html>
