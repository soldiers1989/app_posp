/*
 * Copyright 2015-2102 MrSimple(http://www.mrsimple.cn) Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xdt.dto.lhzf;

import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * <b>功能说明:商户API工具类
 * </b>
 * @author  Lakey
 *   
 */
public class MerchantApiUtil {

    /**
     * 获取参数签名
     * 1. 对所有参数名进行排序
     * 2. 用‘&’符号连接所有非空值参数：‘参数名=参数值’
     * 3. 用‘&’符合连接‘paySecret=商户签名密钥’
     * 4. 对整个字符串做MD5签名
     * @param paramMap  签名参数
     * @param paySecret 签名密钥
     * @return
     */
    public static String  getSign(Map<String , String> paramMap , String paySecret){
        SortedMap<String, Object> smap = new TreeMap<String, Object>(paramMap);
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, Object> m : smap.entrySet()) {
            Object value = m.getValue();
            if (value != null && StringUtils.isNotBlank(String.valueOf(value))){
                stringBuffer.append(m.getKey()).append("=").append(m.getValue()).append("&");
            }
        }
        stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length());

        String argPreSign = stringBuffer.append("&paySecret=").append(paySecret).toString();
        
        System.out.println("Sign========================================================================");
        System.out.println(argPreSign);
        
        String signStr = MD5Util.encode(argPreSign).toUpperCase();

        return signStr;
    }

    /**
     * 获取参数拼接串
     * @param paramMap
     * @return
     */
    public static String  getParamStr(Map<String , String> paramMap){
        SortedMap<String, Object> smap = new TreeMap<String, Object>(paramMap);
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, Object> m : smap.entrySet()) {
            Object value = m.getValue();
            if (value != null && StringUtils.isNotBlank(String.valueOf(value))){
                stringBuffer.append(m.getKey()).append("=").append(value).append("&");
            }
        }
        stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length());

        return stringBuffer.toString();
    }


    /**
     * 验证商户签名
     * @param paramMap  签名参数
     * @param paySecret 签名私钥
     * @param signStr   原始签名密文
     * @return
     */
    public static boolean isRightSign(Map<String , String> paramMap , String paySecret ,String signStr){

        if (StringUtils.isBlank(signStr)){
            return false;
        }

        String sign = getSign(paramMap, paySecret);
        if(signStr.equals(sign)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 建立请求，以表单HTML形式构造（默认）
     * @param sParaTemp 请求参数数组
     * @param strMethod 提交方式。两个值可选：post、get
     * @param strButtonName 确认按钮显示文字
     * @return 提交表单HTML文本
     */
    public static String buildRequest(Map<String, String> sParaTemp, String strMethod, String strButtonName , String actionUrl) {
        //待请求参数数组
        List<String> keys = new ArrayList<String>(sParaTemp.keySet());
        StringBuffer sbHtml = new StringBuffer();

        sbHtml.append("<form id=\"rppaysubmit\" name=\"rppaysubmit\" action=\"" + actionUrl + "\" method=\"" + strMethod
                + "\">");

        for (int i = 0; i < keys.size(); i++) {
            String name = (String) keys.get(i);
            Object object = sParaTemp.get(name);
            String value = "";

            if (object != null){
                value = (String) sParaTemp.get(name);
            }

            sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
        }

        //submit按钮控件请不要含有name属性
        sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + "\" style=\"display:none;\"></form>");
        sbHtml.append("<script>document.forms['rppaysubmit'].submit();</script>");

        return sbHtml.toString();
    }
    
	public static String generateAutoSubmitHtml(String actionUrl, Map<String, String> paramMap) {

		StringBuilder html = new StringBuilder();
		html.append("<html><head></head><body>").append("<form id='pay_form' name='pay_form' action='")
				.append(actionUrl).append("' method='POST'>\n");

		for (String key : paramMap.keySet()) {
			String value = paramMap.get(key);
			if( value!= null && !value.isEmpty()){
				html.append("<input type='hidden' name='" + key + "' value='" + value + "'>\n");
			}
		}

		html.append("</form>\n")
				.append("<script language='javascript'>window.onload=function(){document.pay_form.submit();}</script>\n")
				.append("</body></html>");

		return html.toString();
	}
}
