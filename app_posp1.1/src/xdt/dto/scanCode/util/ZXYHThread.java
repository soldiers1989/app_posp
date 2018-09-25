package xdt.dto.scanCode.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import xdt.dto.scanCode.entity.ScanCodeResponseEntity;
import xdt.service.IScanCodeService;
import xdt.util.BeanToMapUtil;

public class ZXYHThread extends Thread {

Logger log = Logger.getLogger(this.getClass());
	
	@Resource
	private IScanCodeService service;
	private String mer;
	
	private String orderId;

	public ZXYHThread(IScanCodeService service, String mer, String orderId) {
		super();
		this.service = service;
		this.mer = mer;
		this.orderId = orderId;
	}

	@Override
	public void run() {
		Map<String, String> map =new HashMap<>();
		try {
			Thread.sleep(300000);
			for (int i = 0; i < 6; i++) {
			 map= service.zxyhQuick(mer, orderId);
			 log.info("查询订单订单返数据:"+JSON.toJSONString(map));
			 if(map!=null){
				 if("00".equals(map.get("v_code"))) {
					 if("0000".equals(map.get("v_status"))) {
						 ScanCodeResponseEntity consume = (ScanCodeResponseEntity) BeanToMapUtil
									.convertMap(ScanCodeResponseEntity.class, map);
							try {
								service.otherInvoke(consume);
							} catch (Exception e1) {
								log.info("中信银行修改状态成功");
								e1.printStackTrace();
							}
				 		 break;
					 }else if("1003".equals(map.get("v_status"))) {
						Map<String, String>maps = service.zxyhClick(mer, orderId);
						log.info("关闭订单返数据:"+JSON.toJSONString(maps));
						if("00".equals(maps.get("v_code"))) {
							if("0000".equals(maps.get("v_status"))) {
								map.put("v_status", "1003");
								ScanCodeResponseEntity consume = (ScanCodeResponseEntity) BeanToMapUtil
										.convertMap(ScanCodeResponseEntity.class, map);
								try {
									service.otherInvoke(consume);
								} catch (Exception e1) {
									log.info("中信银行修改状态失败");
									e1.printStackTrace();
								}
					 		 break;
							}else if("1002".equals(maps.get("v_status"))) {
								map.put("v_status", "1002");
								ScanCodeResponseEntity consume = (ScanCodeResponseEntity) BeanToMapUtil
										.convertMap(ScanCodeResponseEntity.class, map);
								try {
									service.otherInvoke(consume);
								} catch (Exception e1) {
									log.info("中信银行修改状态失败");
									e1.printStackTrace();
								}
					 		 break;
							}
						}
					
					 }else if("1001".equals(map.get("v_status"))) {
						 ScanCodeResponseEntity consume = (ScanCodeResponseEntity) BeanToMapUtil
									.convertMap(ScanCodeResponseEntity.class, map);
							try {
								service.otherInvoke(consume);
							} catch (Exception e1) {
								log.info("中信银行修改状态成功");
								e1.printStackTrace();
							}
				 		 break;
					 }
				 }
				 	
			 }
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
