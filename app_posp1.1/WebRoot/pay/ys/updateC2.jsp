<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>C2</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
	商户号:<input type="text" name="merchantId" class="merchantId" value="10032061473"/><br /><br />
	订单号:<input type="text" name="orderId" class="orderId " value="<%=System.currentTimeMillis()%>"/><br /><br />
	身份证号:<input type="text" name="liceneceNo" class="liceneceNo " value="120105197510055420"/><br /><br />
	银行卡号:<input type="text" name="acctNo" class="acctNo" value="6228450028016697770"/><br /><br />
	手机号:<input type="text" name="phone" class="phone" value="13323358548"/><br /><br />
	姓名:<input type="text" name="acctName" class="acctName" value="李娟"/><br /><br />
	账户类型:<input type="text" name="businessType" class="businessType" value="0"/><br /><br />
	借记卡费率:<input type="text" name="debitRate" class="debitRate" value="0.0045" /><br /><br />
	单笔提现手续费:<input type="text" name="withdrawDepositSingleFee" class="withdrawDepositSingleFee" value="200" /><br /><br />
	类别:<input type="text" name="changeType" class="changeType" value="1" /><br /><br />
	类别:<input type="text" name="merchantCode" class="merchantCode" value="e6a62e84647b419b8401622357bd3a30" /><br /><br />
	类型:<input type="text" name="type" class="type" value="cj005" /><br /><br />
	<input type="button" onclick="daifu()" value="注册"/><br /><br />
	<div class="div"></div>
</center>
<script type="text/javascript">
	function daifu(){
		console.info($("input[name]").serialize());
		$.ajax({
			url:"${pageContext.request.contextPath}/YSController/paySign.action",
			type:"post",
			data:$("input[name]").serialize(),
			success:function(data){
				console.info(data);
				$.ajax({
					url:"${pageContext.request.contextPath}/YSController/quickPay.action",
					type:"post",
					data:$("input[name]").serialize()+"&sign="+data,
					success:function(data){
						console.info(data);
						$(".div").html(data);
					}
				});
			}
		});
	}
</script>
</body>
</html>