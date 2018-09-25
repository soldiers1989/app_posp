package xdt.quickpay.conformityQucikPay.base;

import java.util.Date;

public class CertificateInfo {
	/**
	 * 版本，cX.509 版本号
	 */
	private String version;
	
	/**
	 * 序列号 ,证书颁发机构 (CA) 分配给证书的唯一序列号。序列号对于给定 CA 颁发的所有证书都是唯一的
	 */
	private String serialNumber;
	
	/**
	 * 有效起始日期，证书有效期的开始日期
	 */
	private Date notBefore;
	
	/**
	 * 有效终止日期，证书有效期的最终日期
	 */
	private Date notAfter;
	
	/**
	 * 颁发者，有关颁发证书的 CA 的信息
	 */
	private String issuerDN;
	
	/**
	 * 主题。向其颁发证书的个人、计算机、设备或 CA 的名称。如果企业中的域成员服务器上存在颁发 CA，则它在企业中将是可分辨名称。
	 * 否则，它可以是全名和电子邮件名或其他个人标识符
	 */
	private String subjectDN;
	
	/**
	 * 公钥。与证书关联的公钥类型和长度
	 */
	private String publicKey;
	
	/**
	 * 指纹算法。为数字签名生成数据摘要（即指纹）的哈希算法。
	 */
	private String algorithm;
	
	/**
	 * 签名算法。CA 用于对证书进行数字签名的哈希算法。
	 */
	private String sign;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Date getNotBefore() {
		return notBefore;
	}

	public void setNotBefore(Date notBefore) {
		this.notBefore = notBefore;
	}

	public Date getNotAfter() {
		return notAfter;
	}

	public void setNotAfter(Date notAfter) {
		this.notAfter = notAfter;
	}

	public String getIssuerDN() {
		return issuerDN;
	}

	public void setIssuerDN(String issuerDN) {
		this.issuerDN = issuerDN;
	}

	public String getSubjectDN() {
		return subjectDN;
	}

	public void setSubjectDN(String subjectDN) {
		this.subjectDN = subjectDN;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
	

}
