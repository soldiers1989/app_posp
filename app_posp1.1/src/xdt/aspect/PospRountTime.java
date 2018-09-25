package xdt.aspect;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import xdt.model.PospRouteInfo;
import xdt.service.IScanCodeService;
@Component
public class PospRountTime {

	Logger log =Logger.getLogger(PayTiming.class);
	
	@Resource
	private IScanCodeService scanCodeService;
	
	public void updateStatus() throws Exception{
		String str[] = {"ZXYH"};
		for (int i = 0; i < str.length; i++) {
			PospRouteInfo info =new PospRouteInfo();
			info.setChannelCode(str[i]);
			info.setStatus(new BigDecimal("1"));
			int ii =scanCodeService.updateStatus(info);
			if(ii==1) {
				log.info("修改全部路由成功");
			}else {
				log.info("开启路由失败");
			}
		}
		
	}
}
