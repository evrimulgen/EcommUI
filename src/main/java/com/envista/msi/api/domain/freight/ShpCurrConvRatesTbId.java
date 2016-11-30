package com.envista.msi.api.domain.freight;
// Generated Jul 6, 2015 6:18:32 PM by Hibernate Tools 4.3.1

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * ShpCurrConvRatesTbId generated by hbm2java
 */
@Embeddable
public class ShpCurrConvRatesTbId implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal rateId;
	private Date rateDate;
	private BigDecimal currencyCode;
	private BigDecimal usdExRate;
	private String field1;
	private String field2;
	private String field3;
	private Date createDate;
	private String createUser;

	public ShpCurrConvRatesTbId() {
	}

	public ShpCurrConvRatesTbId(BigDecimal rateId, Date rateDate, BigDecimal currencyCode, BigDecimal usdExRate,
			String field1, String field2, String field3, Date createDate, String createUser) {
		this.rateId = rateId;
		this.rateDate = rateDate;
		this.currencyCode = currencyCode;
		this.usdExRate = usdExRate;
		this.field1 = field1;
		this.field2 = field2;
		this.field3 = field3;
		this.createDate = createDate;
		this.createUser = createUser;
	}

	@Column(name = "RATE_ID", precision = 22, scale = 0)
	public BigDecimal getRateId() {
		return this.rateId;
	}

	public void setRateId(BigDecimal rateId) {
		this.rateId = rateId;
	}

	@Column(name = "RATE_DATE", length = 7)
	public Date getRateDate() {
		return this.rateDate;
	}

	public void setRateDate(Date rateDate) {
		this.rateDate = rateDate;
	}

	@Column(name = "CURRENCY_CODE", precision = 22, scale = 0)
	public BigDecimal getCurrencyCode() {
		return this.currencyCode;
	}

	public void setCurrencyCode(BigDecimal currencyCode) {
		this.currencyCode = currencyCode;
	}

	@Column(name = "USD_EX_RATE", precision = 22, scale = 0)
	public BigDecimal getUsdExRate() {
		return this.usdExRate;
	}

	public void setUsdExRate(BigDecimal usdExRate) {
		this.usdExRate = usdExRate;
	}

	@Column(name = "FIELD1", length = 50)
	public String getField1() {
		return this.field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	@Column(name = "FIELD2", length = 50)
	public String getField2() {
		return this.field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	@Column(name = "FIELD3", length = 50)
	public String getField3() {
		return this.field3;
	}

	public void setField3(String field3) {
		this.field3 = field3;
	}

	@Column(name = "CREATE_DATE", length = 7)
	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Column(name = "CREATE_USER", length = 100)
	public String getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ShpCurrConvRatesTbId))
			return false;
		ShpCurrConvRatesTbId castOther = (ShpCurrConvRatesTbId) other;

		return ((this.getRateId() == castOther.getRateId()) || (this.getRateId() != null
				&& castOther.getRateId() != null && this.getRateId().equals(castOther.getRateId())))
				&& ((this.getRateDate() == castOther.getRateDate()) || (this.getRateDate() != null
						&& castOther.getRateDate() != null && this.getRateDate().equals(castOther.getRateDate())))
				&& ((this.getCurrencyCode() == castOther.getCurrencyCode())
						|| (this.getCurrencyCode() != null && castOther.getCurrencyCode() != null
								&& this.getCurrencyCode().equals(castOther.getCurrencyCode())))
				&& ((this.getUsdExRate() == castOther.getUsdExRate()) || (this.getUsdExRate() != null
						&& castOther.getUsdExRate() != null && this.getUsdExRate().equals(castOther.getUsdExRate())))
				&& ((this.getField1() == castOther.getField1()) || (this.getField1() != null
						&& castOther.getField1() != null && this.getField1().equals(castOther.getField1())))
				&& ((this.getField2() == castOther.getField2()) || (this.getField2() != null
						&& castOther.getField2() != null && this.getField2().equals(castOther.getField2())))
				&& ((this.getField3() == castOther.getField3()) || (this.getField3() != null
						&& castOther.getField3() != null && this.getField3().equals(castOther.getField3())))
				&& ((this.getCreateDate() == castOther.getCreateDate()) || (this.getCreateDate() != null
						&& castOther.getCreateDate() != null && this.getCreateDate().equals(castOther.getCreateDate())))
				&& ((this.getCreateUser() == castOther.getCreateUser())
						|| (this.getCreateUser() != null && castOther.getCreateUser() != null
								&& this.getCreateUser().equals(castOther.getCreateUser())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getRateId() == null ? 0 : this.getRateId().hashCode());
		result = 37 * result + (getRateDate() == null ? 0 : this.getRateDate().hashCode());
		result = 37 * result + (getCurrencyCode() == null ? 0 : this.getCurrencyCode().hashCode());
		result = 37 * result + (getUsdExRate() == null ? 0 : this.getUsdExRate().hashCode());
		result = 37 * result + (getField1() == null ? 0 : this.getField1().hashCode());
		result = 37 * result + (getField2() == null ? 0 : this.getField2().hashCode());
		result = 37 * result + (getField3() == null ? 0 : this.getField3().hashCode());
		result = 37 * result + (getCreateDate() == null ? 0 : this.getCreateDate().hashCode());
		result = 37 * result + (getCreateUser() == null ? 0 : this.getCreateUser().hashCode());
		return result;
	}

}
