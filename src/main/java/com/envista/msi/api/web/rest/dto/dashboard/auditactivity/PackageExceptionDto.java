package com.envista.msi.api.web.rest.dto.dashboard.auditactivity;

import com.envista.msi.api.domain.util.DashboardSroredProcParam;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Sujit kumar on 13/02/2017.
 */

@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "PackageExceptionDto.getPackageException", procedureName = "SHP_DB_PKG_EXCP_RPOC",
                resultSetMappings = "PackageExceptionDto.PackageExceptionMapping",
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.DATE_TYPE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.CUSTOMER_IDS_CSV_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.CARRIER_IDS_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.MODES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.SERVICES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.LANES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.FROM_DATE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.TO_DATE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.ACCESSORIAL_NAME_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.TOP_TEN_ACCESSORIAL_PARAM, type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = DashboardSroredProcParam.PackageExceptionParams.PACKAGE_EXCEPTION_DATA_PARAM, type = Void.class)
                }
        ),
        @NamedStoredProcedureQuery(name = "PackageExceptionDto.getPackageExceptionByCarrier", procedureName = "SHP_DB_PKG_EXCP_CARR_RPOC",
                resultSetMappings = "PackageExceptionDto.PackageExceptionByCarrierMapping",
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.DATE_TYPE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.CUSTOMER_IDS_CSV_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.CARRIER_IDS_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.MODES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.SERVICES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.LANES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.FROM_DATE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.TO_DATE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.ACCESSORIAL_NAME_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.TOP_TEN_ACCESSORIAL_PARAM, type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.DELIVERY_FLAG_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = DashboardSroredProcParam.PackageExceptionParams.PACKAGE_EXCEPTION_DATA_PARAM, type = Void.class)
                }
        ),
        @NamedStoredProcedureQuery(name = "PackageExceptionDto.getPackageExceptionByMonth", procedureName = "SHP_DB_PKG_EXCP_MNTH_RPOC",
                resultSetMappings = "PackageExceptionDto.PackageExceptionByMonthMapping",
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.DATE_TYPE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.CUSTOMER_IDS_CSV_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.CARRIER_IDS_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.MODES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.SERVICES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.LANES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.FROM_DATE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.TO_DATE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.ACCESSORIAL_NAME_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.TOP_TEN_ACCESSORIAL_PARAM, type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardSroredProcParam.PackageExceptionParams.DELIVERY_FLAG_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = DashboardSroredProcParam.PackageExceptionParams.PACKAGE_EXCEPTION_DATA_PARAM, type = Void.class)
                }
        )
})

@SqlResultSetMappings({
        @SqlResultSetMapping(name = "PackageExceptionDto.PackageExceptionMapping",
            classes = {
                    @ConstructorResult(targetClass = PackageExceptionDto.class,
                    columns = {
                            @ColumnResult(name = "BILLING_DATE", type = String.class),
                            @ColumnResult(name = "DELIVERY_FLAG", type = String.class),
                            @ColumnResult(name = "COUNT_DEL_FLAG", type = Integer.class)
                    })
            }),
        @SqlResultSetMapping(name = "PackageExceptionDto.PackageExceptionByCarrierMapping",
                classes = {
                        @ConstructorResult(targetClass = PackageExceptionDto.class,
                                columns = {
                                        @ColumnResult(name = "ID", type = Long.class),
                                        @ColumnResult(name = "NAME", type = String.class),
                                        @ColumnResult(name = "VALUE", type = Double.class)
                                })
                }),
        @SqlResultSetMapping(name = "PackageExceptionDto.PackageExceptionByMonthMapping",
                classes = {
                        @ConstructorResult(targetClass = PackageExceptionDto.class,
                                columns = {
                                        @ColumnResult(name = "BILL_DATE", type = Date.class),
                                        @ColumnResult(name = "AMOUNT", type = Double.class)
                                })
                })
})

@Entity
public class PackageExceptionDto implements Serializable{
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "VALUE")
    private Double value;

    @Column(name = "BILL_DATE")
    private Date billDate;

    @Column(name = "AMOUNT")
    private Double amount;

    @Column(name = "BILLING_DATE")
    private String billingDate;

    @Column(name = "DELIVERY_FLAG")
    private String deliveryFlag;

    @Column(name = "COUNT_DEL_FLAG")
    private Integer deliveryFlagCount;

    public PackageExceptionDto(){}

    public PackageExceptionDto(String billingDate, String deliveryFlag, Integer deliveryFlagCount) {
        this.billingDate = billingDate;
        this.deliveryFlag = deliveryFlag;
        this.deliveryFlagCount = deliveryFlagCount;
    }

    public PackageExceptionDto(Date billDate, Double amount) {
        this.billDate = billDate;
        this.amount = amount;
    }

    public PackageExceptionDto(Long id, String name, Double value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getDeliveryFlagCount() {
        return deliveryFlagCount;
    }

    public void setDeliveryFlagCount(Integer deliveryFlagCount) {
        this.deliveryFlagCount = deliveryFlagCount;
    }

    public String getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(String billingDate) {
        this.billingDate = billingDate;
    }

    public String getDeliveryFlag() {
        return deliveryFlag;
    }

    public void setDeliveryFlag(String deliveryFlag) {
        this.deliveryFlag = deliveryFlag;
    }
}
