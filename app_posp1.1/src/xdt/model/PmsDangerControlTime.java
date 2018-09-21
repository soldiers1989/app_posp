package xdt.model;

public class PmsDangerControlTime {
	private Integer id;//商户编号
	private String mercId;//商户编号
	private String mercName;//商户名称
	private String symbol;//判断符号
	private String amount;//金额
	private String transactionTime;//时间限制
	private String addtime;//添加日期
	private String updateTime;//修改时间
	private String addAgentNumber;//添加人
	private String updateAgentNumber;//修改人
	private String quotaType;//限额类型 0：商户 1：通道
	private String remarks;//添加备注
	private String startDate;//开始时间
	private String endDate;//结束时间
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
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
	public String getMercId() {
		return mercId;
	}
	public void setMercId(String mercId) {
		this.mercId = mercId;
	}
	public String getMercName() {
		return mercName;
	}
	public void setMercName(String mercName) {
		this.mercName = mercName;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}
	public String getAddtime() {
		return addtime;
	}
	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getAddAgentNumber() {
		return addAgentNumber;
	}
	public void setAddAgentNumber(String addAgentNumber) {
		this.addAgentNumber = addAgentNumber;
	}
	public String getUpdateAgentNumber() {
		return updateAgentNumber;
	}
	public void setUpdateAgentNumber(String updateAgentNumber) {
		this.updateAgentNumber = updateAgentNumber;
	}
	public String getQuotaType() {
		return quotaType;
	}
	public void setQuotaType(String quotaType) {
		this.quotaType = quotaType;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	@Override
	public String toString() {
		return "PmsDangerControlTime [mercId=" + mercId + ", mercName=" + mercName + ", symbol=" + symbol + ", amount="
				+ amount + ", transactionTime=" + transactionTime + ", addtime=" + addtime + ", updateTime="
				+ updateTime + ", addAgentNumber=" + addAgentNumber + ", updateAgentNumber=" + updateAgentNumber
				+ ", quotaType=" + quotaType + ", remarks=" + remarks + "]";
	}
	
	
}
