package com.envista.msi.api.domain.freight;
// Generated Jul 6, 2015 6:18:32 PM by Hibernate Tools 4.3.1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ShpNspCcLinkedTb generated by hbm2java
 */
@Entity
@Table(name = "SHP_NSP_CC_LINKED_TB")
public class ShpNspCcLinkedTb implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ShpNspCcLinkedTbId id;

	public ShpNspCcLinkedTb() {
	}

	public ShpNspCcLinkedTb(ShpNspCcLinkedTbId id) {
		this.id = id;
	}

	@EmbeddedId

	@AttributeOverrides({
			@AttributeOverride(name = "ccLinkedId", column = @Column(name = "CC_LINKED_ID", precision = 22, scale = 0) ),
			@AttributeOverride(name = "creditId", column = @Column(name = "CREDIT_ID", precision = 22, scale = 0) ),
			@AttributeOverride(name = "invoiceId", column = @Column(name = "INVOICE_ID", precision = 22, scale = 0) ),
			@AttributeOverride(name = "glCode", column = @Column(name = "GL_CODE", length = 20) ),
			@AttributeOverride(name = "creditAmount", column = @Column(name = "CREDIT_AMOUNT", precision = 22, scale = 0) ),
			@AttributeOverride(name = "linkSeq", column = @Column(name = "LINK_SEQ", precision = 22, scale = 0) ),
			@AttributeOverride(name = "status", column = @Column(name = "STATUS", precision = 22, scale = 0) ),
			@AttributeOverride(name = "createDate", column = @Column(name = "CREATE_DATE", length = 7) ),
			@AttributeOverride(name = "createUser", column = @Column(name = "CREATE_USER", length = 20) ),
			@AttributeOverride(name = "lastUpdateDate", column = @Column(name = "LAST_UPDATE_DATE", length = 7) ),
			@AttributeOverride(name = "lastUpdateUser", column = @Column(name = "LAST_UPDATE_USER", length = 20) ),
			@AttributeOverride(name = "usedCrAmt", column = @Column(name = "USED_CR_AMT", precision = 22, scale = 0) ),
			@AttributeOverride(name = "displayLink", column = @Column(name = "DISPLAY_LINK", length = 500) ) })
	public ShpNspCcLinkedTbId getId() {
		return this.id;
	}

	public void setId(ShpNspCcLinkedTbId id) {
		this.id = id;
	}

}
