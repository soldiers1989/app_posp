<%@ page language="java" contentType="text/html; charset=UTF-8" import="xdt.util.UtilDate" 
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
版本号:<input type="text" name="v_version" class="v_version" value="1.0.0.0"><br />
商户号:<input type="text" name="v_mid" class="v_mid" value="100341512318531"><br />
代付时间： <input type="text" class="v_time" name="v_time" value="<%=UtilDate.getOrderNum()%>"/><br />
代付类型： <input type="text" class="v_type" name="v_type" value="0"/><br />
<input type="button" value="提交" onclick="tijiao()"><br />
<div id ="div"></div>
<br />
</center>
<script type="text/javascript">
	function tijiao(){
		    var v_version = $(".v_version").val();
			var v_mid = $(".v_mid").val();
			var v_time = $(".v_time").val();
			var v_type =$(".v_type").val();
			var data={"v_version":v_version,
					  "v_mid":v_mid,
					  "v_time":v_time,
					  "v_type":v_type};
			$.ajax({
						url : "${pageContext.request.contextPath}/totalPayController/balanceSign.action",
						type : 'post',
						data : data,
						success : function(data) {
							console.info(data);
							var datas={"v_version":v_version,
									  "v_mid":v_mid,
									  "v_time":v_time,
									  "v_type":v_type,
									  "v_sign":data};
							$.ajax({
										url : "${pageContext.request.contextPath}/totalPayController/balance/scan_param.action",
										type : 'post',
										data :datas,
										success : function(data) {
											console.info(data);
											$("#div").text(data);
										}
									});
						}
					});
	}
	/* function tijiao(){
	   
	} */
</script>
</body>

</html>