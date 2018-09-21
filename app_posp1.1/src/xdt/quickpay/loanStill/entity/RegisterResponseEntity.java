package xdt.quickpay.loanStill.entity;

public class RegisterResponseEntity {
		
	private String v_merNo;  //机构号
	
	private String v_reqKey; //密钥
	
	private String v_dfmerNo; //代付机构号
	
	private String v_dfreqKey; //代付密钥
	
	private String v_code; //状态码
	 
	private String v_msg; //状态描述
	 	
	private String v_sign; //签名

	public String getV_merNo() {
		return v_merNo;
	}

	public void setV_merNo(String v_merNo) {
		this.v_merNo = v_merNo;
	}

	public String getV_reqKey() {
		return v_reqKey;
	}

	public void setV_reqKey(String v_reqKey) {
		this.v_reqKey = v_reqKey;
	}

	public String getV_dfmerNo() {
		return v_dfmerNo;
	}

	public void setV_dfmerNo(String v_dfmerNo) {
		this.v_dfmerNo = v_dfmerNo;
	}
   
	public String getV_dfreqKey() {
		return v_dfreqKey;
	}

	public void setV_dfreqKey(String v_dfreqKey) {
		this.v_dfreqKey = v_dfreqKey;
	}

	public String getV_code() {
		return v_code;
	}

	public void setV_code(String v_code) {
		this.v_code = v_code;
	}

	public String getV_msg() {
		return v_msg;
	}

	public void setV_msg(String v_msg) {
		this.v_msg = v_msg;
	}

	public String getV_sign() {
		return v_sign;
	}

	public void setV_sign(String v_sign) {
		this.v_sign = v_sign;
	}

	
	
	

}
