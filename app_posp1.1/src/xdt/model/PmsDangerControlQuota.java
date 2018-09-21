package xdt.model;

public class PmsDangerControlQuota {
	
	private Integer id;//商户编号
	private String mercId;//商户编号
	private String mercName;//商户名称
	private String minQuota;//单笔最小额度
	private String maxQuota;//单笔最大额度
	private String dayQuota;//单日限额
	private String monthQuota;//单月限额
	private String totalQuota;//总限额
	private String timeQuota;//限制时间
	private String updateTimeQuota;//修改时间
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
	public String getMinQuota() {
		return minQuota;
	}
	public void setMinQuota(String minQuota) {
		this.minQuota = minQuota;
	}
	public String getMaxQuota() {
		return maxQuota;
	}
	public void setMaxQuota(String maxQuota) {
		this.maxQuota = maxQuota;
	}
	public String getDayQuota() {
		return dayQuota;
	}
	public void setDayQuota(String dayQuota) {
		this.dayQuota = dayQuota;
	}
	public String getMonthQuota() {
		return monthQuota;
	}
	public void setMonthQuota(String monthQuota) {
		this.monthQuota = monthQuota;
	}
	public String getTotalQuota() {
		return totalQuota;
	}
	public void setTotalQuota(String totalQuota) {
		this.totalQuota = totalQuota;
	}
	public String getTimeQuota() {
		return timeQuota;
	}
	public void setTimeQuota(String timeQuota) {
		this.timeQuota = timeQuota;
	}
	public String getUpdateTimeQuota() {
		return updateTimeQuota;
	}
	public void setUpdateTimeQuota(String updateTimeQuota) {
		this.updateTimeQuota = updateTimeQuota;
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
		return "PmsDangerControlQuota [mercId=" + mercId + ", mercName=" + mercName + ", minQuota=" + minQuota
				+ ", maxQuota=" + maxQuota + ", dayQuota=" + dayQuota + ", monthQuota=" + monthQuota + ", totalQuota="
				+ totalQuota + ", timeQuota=" + timeQuota + ", updateTimeQuota=" + updateTimeQuota + ", addAgentNumber="
				+ addAgentNumber + ", updateAgentNumber=" + updateAgentNumber + ", quotaType=" + quotaType
				+ ", remarks=" + remarks + "]";
	}
	
}
