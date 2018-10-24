package xdt.dto.transfer_accounts.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import xdt.dto.transfer_accounts.entity.DaifuRequestEntity;
import xdt.model.PmsMerchantInfo;
import xdt.service.ITotalPayService;

public class TTFThread extends Thread{

	
	Logger log = Logger.getLogger(this.getClass());
	
	@Resource
	private ITotalPayService service;

	private DaifuRequestEntity payRequest;
	
	private PmsMerchantInfo merchantinfo;

	public TTFThread(ITotalPayService service, DaifuRequestEntity payRequest, PmsMerchantInfo merchantinfo) {
		super();
		this.service = service;
		this.payRequest = payRequest;
		this.merchantinfo = merchantinfo;
	}

	@Override
	public void run() {
		log.info("统统付线程查询进来了");
		Map<String, String> map =new HashMap<>();
		try {
			Thread.sleep(2000);
			for (int i = 0; i < 30; i++) {
			log.info("进入循环了");
			 map= service.yttQuick(payRequest.getV_mid(), payRequest.getV_batch_no());
			 log.info("查询完了");
			 if(map!=null){
				 if("00".equals(map.get("v_code"))) {
					 if("1001".equals(map.get("v_status"))) {
						 service.UpdateDaifu(payRequest.getV_batch_no(), "02");
						 	Map<String, String> maps =new HashMap<>();
						 	maps.put("machId",payRequest.getV_mid());
							maps.put("payMoney",Double.parseDouble(payRequest.getV_sum_amount())*100+Double.parseDouble(merchantinfo.getPoundage())*100+"");
							int nus =0;
							if("0".equals(payRequest.getV_type())) {
								nus =service.updataPay(maps);
							}else if("1".equals(payRequest.getV_type())){
								nus =service.updataPayT1(maps);
							}
				 			if(nus==1) {
				 				log.info("统统付代付补款成功");
				 				DaifuRequestEntity entity =new DaifuRequestEntity();
				 				entity.setV_mid(payRequest.getV_mid());
				 				entity.setV_batch_no(payRequest.getV_batch_no()+"/A");
				 				entity.setV_amount(payRequest.getV_sum_amount());
				 				entity.setV_sum_amount(payRequest.getV_sum_amount());
				 				entity.setV_identity(payRequest.getV_identity());
				 				entity.setV_cardNo(payRequest.getV_cardNo());
				 				entity.setV_city(payRequest.getV_city());
				 				entity.setV_province(payRequest.getV_province());
				 				entity.setV_type(payRequest.getV_type());
				 				entity.setV_pmsBankNo(payRequest.getV_pmsBankNo());
				 				entity.setV_realName(payRequest.getV_realName());
								int ii =service.add(entity, merchantinfo, maps, "00");
								log.info("统统付补款订单状态："+ii);
				 			} 
				 		 break;
					 }else if("0000".equals(map.get("v_status"))){
						 service.UpdateDaifu(payRequest.getV_batch_no(), "00");
						 break;
					 }
				 }
				 	
			 }
			 Thread.sleep(60000);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
