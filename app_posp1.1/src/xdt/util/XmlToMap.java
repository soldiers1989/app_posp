package xdt.util;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import com.alibaba.fastjson.JSON;

public class XmlToMap {

	public static Map<String, String> strXmlToMap(String xml){
		Map<String, String> map =new HashMap<>();
		
		 Document doc = null;
	        try {

	            // 下面的是通过解析xml字符串的
	            doc = DocumentHelper.parseText(xml); // 将字符串转为XML

	            Element rootElt = doc.getRootElement(); // 获取根节点
	            System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
	            Iterator iter =rootElt.elementIterator();
	            while (iter.hasNext()) {
	            	Element recordEle = (Element) iter.next();
	            	Iterator iterss = recordEle.elementIterator();
	            	if(rootElt.elementText(recordEle.getName())==null||"".equals(rootElt.elementText(recordEle.getName()))) {
	            		System.out.println(recordEle.getName());
	            		//Element recordEless = (Element) iterss.next();
	            		//Iterator iterss1 = recordEless.elementIterator();
	            		map.putAll(elToMap(recordEle));
	            	}else {
	            		System.out.println(recordEle.getName());
	            		map.put(recordEle.getName(), rootElt.elementText(recordEle.getName()));
	            	}
	            	
	            }
	            
	        } catch (DocumentException e) {
	            e.printStackTrace();

	        } catch (Exception e) {
	            e.printStackTrace();

	        }
	        
	      return map;
	}
	public static Map<String, String> elToMap(Element element) {
		Map<String, String> map =new HashMap<>();
		Iterator iterss =element.elementIterator();
		while (iterss.hasNext()) {
			Element recordEless =(Element) iterss.next();
			if(element.elementText(recordEless.getName())==null||"".equals(element.elementText(recordEless.getName()))) {
				//elToMap(recordEless);
				map.putAll(elToMap(recordEless));
			}else {
				//System.out.println(recordEless.getName()+":"+element.elementText(recordEless.getName()));
				map.put(recordEless.getName(), element.elementText(recordEless.getName()));
			}
			
		}
		return map;
	} 
	
	/**
     * 转XMLmap
     * @author  
     * @param xmlBytes
     * @param charset
     * @return
     * @throws Exception
     */
    public static Map<String, String> toMap(byte[] xmlBytes,String charset) throws Exception{
    	 Map<String, String> params = new HashMap<>();
        SAXReader reader = new SAXReader(false);
        InputSource source = new InputSource(new ByteArrayInputStream(xmlBytes));
        source.setEncoding(charset);
        Document doc = reader.read(source);
        List<Element> els = doc.getRootElement().elements();
        for(Element el : els){
        	params.put(el.getName().toLowerCase(), el.getTextTrim());
        }
        return params;
    }
	public static void main(String[] args) {
		 String xmlString = "<html>" + "<head>" + "<title>dom4j解析一个例子</title>"
	                + "<script>" + "<username>yangrong</username>"
	                + "<password>123456</password>" + "</script>" + "</head>"
	                + "<body>" + "<result>0</result>" + "<form>"
	                + "<banlce>1000</banlce>" + "<subID>36242519880716</subID>"
	                + "</form>" + "</body>" + "</html>";
		 
		Map<String, String> map = strXmlToMap(xmlString);
		System.out.println(JSON.toJSON(map));
	}
}
