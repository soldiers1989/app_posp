package xdt.quickpay.conformityQucikPay.entity;

public class ConformityQucikPayRequestEntity {
	
	  private String v_version;//版本号
	  private String v_mid;//商户号
	  private String v_userId;//用户表示
	  private String v_oid;//订单号
	  private String v_time;//请求时间
	  private String v_card;//卡号
	  private String v_txnAmt;//金额
	  private String v_cardType;//
	  private String v_type;//D0 T1
	  private String v_productDesc;//说明
	  private String v_notify_url;//异步地址
	  private String v_url;//同步地址
	  private String v_attach;//上传参数
	  private String v_sign;
	public String getV_version() {
		return v_version;
	}
	public void setV_version(String v_version) {
		this.v_version = v_version;
	}
	public String getV_mid() {
		return v_mid;
	}
	public void setV_mid(String v_mid) {
		this.v_mid = v_mid;
	}
	public String getV_userId() {
		return v_userId;
	}
	public void setV_userId(String v_userId) {
		this.v_userId = v_userId;
	}
	public String getV_oid() {
		return v_oid;
	}
	public void setV_oid(String v_oid) {
		this.v_oid = v_oid;
	}
	public String getV_time() {
		return v_time;
	}
	public void setV_time(String v_time) {
		this.v_time = v_time;
	}
	public String getV_card() {
		return v_card;
	}
	public void setV_card(String v_card) {
		this.v_card = v_card;
	}
	public String getV_txnAmt() {
		return v_txnAmt;
	}
	public void setV_txnAmt(String v_txnAmt) {
		this.v_txnAmt = v_txnAmt;
	}
	public String getV_cardType() {
		return v_cardType;
	}
	public void setV_cardType(String v_cardType) {
		this.v_cardType = v_cardType;
	}
	public String getV_type() {
		return v_type;
	}
	public void setV_type(String v_type) {
		this.v_type = v_type;
	}
	public String getV_productDesc() {
		return v_productDesc;
	}
	public void setV_productDesc(String v_productDesc) {
		this.v_productDesc = v_productDesc;
	}
	public String getV_notify_url() {
		return v_notify_url;
	}
	public void setV_notify_url(String v_notify_url) {
		this.v_notify_url = v_notify_url;
	}
	public String getV_url() {
		return v_url;
	}
	public void setV_url(String v_url) {
		this.v_url = v_url;
	}
	public String getV_attach() {
		return v_attach;
	}
	public void setV_attach(String v_attach) {
		this.v_attach = v_attach;
	}
	public String getV_sign() {
		return v_sign;
	}
	public void setV_sign(String v_sign) {
		this.v_sign = v_sign;
	}
	  
	  

}
