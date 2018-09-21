package xdt.model;

public class PmsDangerControlBlack {
	private Integer id;
	private String cardNo;//卡号
	private String realname;//姓名
	private String certNo;//证件号
	private String addTime;//添加时间
	private String addAgentNumber;//添加人
	private String remarks;//备注
	private String startDate;//开始时间
	private String endDate;//结束时间
	private String mercId; //商户号
	
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getCertNo() {
		return certNo;
	}
	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getAddAgentNumber() {
		return addAgentNumber;
	}
	public void setAddAgentNumber(String addAgentNumber) {
		this.addAgentNumber = addAgentNumber;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getMercId() {
		return mercId;
	}
	public void setMercId(String mercId) {
		this.mercId = mercId;
	}
   	
}
