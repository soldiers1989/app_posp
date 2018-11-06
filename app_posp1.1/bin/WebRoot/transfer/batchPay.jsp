<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxfileupload.js"></script>
</head>
<body>
	<input type="file" id="files" name="file">
	<input type="button" value="批量化代付" onclick="dianji()"><br />
</body>
<script type="text/javascript">

	function dianji(){
		var obj=document.getElementById('files'); 
		var stuff=obj.value.match(/^(.*)(\.)(.{1,8})$/)[3];
		console.info(stuff);
		if(stuff !='xls'&&stuff !='xlsx'){
			alert("请添加xls表格文件");
		}else{
			$.ajaxFileUpload({//ajaxfileupload.js是用来上传文件的，自定义form已经被写定  method=“post”，所以没有“method”或者“type”
	            url:"${pageContext.request.contextPath}/totalPayController/merchant/virement/mer_payment.action",//需要链接到服务器地址  
	            secureuri:false,
	            isMore:false, //是否为多文件上传
	            fileElementId:$("input[id^=files]")[0].id,//文件选择框的id属性，多文件为id[]/单文件为'id' 
	            dataType:"json",//接收JSON类型
	           /*  contentType:"serial",//发送类型“serial”,为空默认为JSON */
	            data:{"v_mid":v_mid},
	            success: function(data,status){ 
	            //上传成功之后的操作
	            console.info('laiel');
	            console.info(data);
	            },error: function (data,status){ 
	            //上传失败之后的操作
	            console.info(data);
	            }  
	        }); 
		}
	}
	
</script>
</html>