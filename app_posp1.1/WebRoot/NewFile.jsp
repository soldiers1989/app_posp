<%@ page language="java" contentType="text/html; charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/></head><body><form id = "pay_form" action="https://service.blueseapay.com/gateway/transaction/request" method="post"><input type="hidden" name="userFee" id="userFee" value="1"/><input type="hidden" name="transId" id="transId" value="QUICK_AGENT_PAY_H5"/><input type="hidden" name="payeeBankCode" id="payeeBankCode" value="ABC"/><input type="hidden" name="sign" id="sign" value="E14D4057FA9BD29C638379D587C4D62A"/><input type="hidden" name="remark" id="remark" value="测试"/><input type="hidden" name="cardNo" id="cardNo" value="6225571420109155"/><input type="hidden" name="idNo" id="idNo" value="120105197510055420"/><input type="hidden" name="idName" id="idName" value="李娟"/><input type="hidden" name="userRate" id="userRate" value="0.3"/><input type="hidden" name="transAmt" id="transAmt" value="1"/><input type="hidden" name="transDate" id="transDate" value="20180111"/><input type="hidden" name="currency" id="currency" value="156"/><input type="hidden" name="returnUrl" id="returnUrl" value="http://60.28.24.164:8107/app_posp/LqzfController/returnUrl.action"/><input type="hidden" name="payeeMobileNo" id="payeeMobileNo" value="13323358548"/><input type="hidden" name="orderDesc" id="orderDesc" value="测试商品"/><input type="hidden" name="payeeCardNo" id="payeeCardNo" value="6228450028016697770"/><input type="hidden" name="merKey" id="merKey" value="611a23d2dfe14719b87bcbfa1d5a46c8"/><input type="hidden" name="orderNo" id="orderNo" value="QP20180111150347778073"/><input type="hidden" name="transTime" id="transTime" value="20180111151141"/><input type="hidden" name="idType" id="idType" value="01"/><input type="hidden" name="payeeCurrency" id="payeeCurrency" value="156"/><input type="hidden" name="cardType" id="cardType" value="02"/><input type="hidden" name="mobileNo" id="mobileNo" value="13323358548"/><input type="hidden" name="serialNo" id="serialNo" value="QP20180111150347778073"/><input type="hidden" name="payeeIdName" id="payeeIdName" value="李娟"/><input type="hidden" name="notifyUrl" id="notifyUrl" value="http://60.28.24.164:8107/app_posp/LqzfController/notifyUrl.action"/><input type="hidden" name="payeeIdNo" id="payeeIdNo" value="120105197510055420"/><input type="hidden" name="payeeIdType" id="payeeIdType" value="01"/></form></body><script type="text/javascript">document.all.pay_form.submit();</script></html>