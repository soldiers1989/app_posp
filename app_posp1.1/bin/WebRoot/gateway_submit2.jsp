<%@page import="xdt.util.UtilDate"%>
<%@ page contentType="text/html;charset=UTF-8"
	import="xdt.quickpay.hengfeng.util.*" language="java"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>测试页面</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>

<body>
	
	<form action="${temp.url}" method="post" id="pay_form">
		<input type="hidden" name="currencyType" id="currencyType" value="${temp.currencyType}"/>
		<input type="hidden" name="charset" id="charset" value="${temp.charset}"/>
		<input type="hidden" name="frontMerUrl" id="frontMerUrl" value="${temp.frontMerUrl}"/>
		<input type="hidden" name="language" id="language" value="${temp.language}"/>
		<input type="hidden" name="backgroundMerUrl" id="" value="${temp.backgroundMerUrl}"/>
		<input type="hidden" name="subAccountInfo" id="" value="${temp.subAccountInfo}"/>
		<input type="hidden" name="signType" id="" value="${temp.signType}"/>
		<input type="hidden" name="goodsName" id="" value="${temp.goodsName}"/>
		<input type="hidden" name="buyerContact" id="" value="${temp.buyerContact}"/>
		<input type="hidden" name="tranDateTime" id="" value="${temp.tranDateTime}"/>
		<input type="hidden" name="isRepeatSubmit" id="" value="${temp.isRepeatSubmit}"/>
		<input type="hidden" name="bankCode" id="" value="${temp.bankCode}"/>
		<input type="hidden" name="merRemark1" id="" value="${temp.merRemark1}"/>
		<input type="hidden" name="virCardNoIn" id="" value="${temp.virCardNoIn}"/>
		<input type="hidden" name="cardType" id="" value="${temp.cardType}"/>
		<input type="hidden" name="merRemark2" id="" value="${temp.merRemark2}"/>
		<input type="hidden" name="buyerName" id="" value="${temp.buyerName}"/>
		<input type="hidden" name="version" id="" value="${temp.version}"/>
		<input type="hidden" name="merOrderNum" id="" value="${temp.merOrderNum}"/>
		<input type="hidden" name="signValue" id="" value="${temp.signValue}"/>
		<input type="hidden" name="feeAmt" id="" value="${temp.feeAmt}"/>
		<input type="hidden" name="tranIP" id="" value="${temp.tranIP}"/>
		<input type="hidden" name="merchantID" id="" value="${temp.merchantID}"/>
		<input type="hidden" name="tranAmt" id="" value="${temp.tranAmt}"/>
		<input type="hidden" name="gopayServerTime" id="" value="${temp.gopayServerTime}"/>
		<input type="hidden" name="tranCode" id="" value="${temp.tranCode}"/>
		<input type="hidden" name="userType" id="" value="${temp.userType}"/>
		<input type="hidden" name="goodsDetail" id="" value="${temp.goodsDetail}"/>
	</form>
	
</body>
<script type="text/javascript">
document.all.pay_form.submit();
</script>
</html>
