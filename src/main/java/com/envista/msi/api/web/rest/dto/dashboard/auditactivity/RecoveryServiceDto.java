package com.envista.msi.api.web.rest.dto.dashboard.auditactivity;

import com.envista.msi.api.domain.util.DashboardStoredProcParam;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Sujit kumar on 13/02/2017.
 */

@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = RecoveryServiceDto.Config.StoredProcedureQueryName.RECOVERY_SERVICE,
                procedureName = RecoveryServiceDto.Config.StoredProcedureName.RECOVERY_SERVICE,
                resultSetMappings = RecoveryServiceDto.Config.ResultMappings.RECOVERY_SERVICE_MAPPING,
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.DATE_TYPE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.CONVERTED_CURRENCY_ID_PARAM, type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.CONVERTED_CURRENCY_CODE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.CUSTOMER_IDS_CSV_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.CARRIER_IDS_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.MODES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.SERVICES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.LANES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.FROM_DATE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.TO_DATE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.ACCESSORIAL_NAME_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.MODE_NAMES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.TOP_TEN_ACCESSORIAL_PARAM, type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = DashboardStoredProcParam.RecoveryServiceParams.RECOVERY_SERVICE_DATA_PARAM, type = Void.class)
                }),
        @NamedStoredProcedureQuery(name = RecoveryServiceDto.Config.StoredProcedureQueryName.RECOVERY_SERVICE_BY_MONTH,
                procedureName = RecoveryServiceDto.Config.StoredProcedureName.RECOVERY_SERVICE_BY_MONTH,
                resultSetMappings = RecoveryServiceDto.Config.ResultMappings.RECOVERY_SERVICE_BY_MONTH_MAPPING,
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.DATE_TYPE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.CONVERTED_CURRENCY_ID_PARAM, type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.CONVERTED_CURRENCY_CODE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.CUSTOMER_IDS_CSV_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.CARRIER_IDS_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.MODES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.SERVICES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.LANES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.FROM_DATE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.TO_DATE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.ACCESSORIAL_NAME_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.MODE_NAMES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.TOP_TEN_ACCESSORIAL_PARAM, type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.RecoveryServiceParams.SERVICE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = DashboardStoredProcParam.RecoveryServiceParams.RECOVERY_SERVICE_DATA_PARAM, type = Void.class)
                })
})

@SqlResultSetMappings({
        @SqlResultSetMapping(name = RecoveryServiceDto.Config.ResultMappings.RECOVERY_SERVICE_MAPPING, classes = {
                @ConstructorResult(
                        targetClass = RecoveryServiceDto.class,
                        columns = {
                                @ColumnResult(name = "CARRIER_ID", type = Long.class),
                                @ColumnResult(name = "CARRIER_NAME", type = String.class),
                                @ColumnResult(name = "BUCKET_TYPE", type = String.class),
                                @ColumnResult(name = "CREDIT_AMOUNT", type = Double.class)
                        }
                )
        }),
        @SqlResultSetMapping(name = RecoveryServiceDto.Config.ResultMappings.RECOVERY_SERVICE_BY_MONTH_MAPPING, classes = {
                @ConstructorResult(
                        targetClass = RecoveryServiceDto.class,
                        columns = {
                                @ColumnResult(name = "BILL_DATE", type = Date.class),
                                @ColumnResult(name = "AMOUNT", type = Double.class)
                        }
                )
        })
})

@Entity
public class RecoveryServiceDto implements Serializable {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "VALUE")
    private Double value;

    @Column(name = "CARRIER_ID")
    private Long carrierId;

    @Column(name = "CARRIER_NAME")
    private String carrierName;

    @Column(name = "BUCKET_TYPE")
    private String bucketType;

    @Column(name = "CREDIT_AMOUNT")
    private Double creditAmount;

    @Column(name = "BILL_DATE")
    private Date billDate;

    @Column(name = "AMOUNT")
    private Double amount;

    public RecoveryServiceDto(){}

    public RecoveryServiceDto(Long carrierId, String carrierName, String bucketType, Double creditAmount) {
        this.carrierId = carrierId;
        this.carrierName = carrierName;
        this.bucketType = bucketType;
        this.creditAmount = creditAmount;
    }

    public RecoveryServiceDto(Long id, String name, Double value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public RecoveryServiceDto(Date billDate, Double amount) {
        this.billDate = billDate;
        this.amount = amount;
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

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getBucketType() {
        return bucketType;
    }

    public void setBucketType(String bucketType) {
        this.bucketType = bucketType;
    }

    public Double getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(Double creditAmount) {
        this.creditAmount = creditAmount;
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

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public static class Config{
        static class ResultMappings{
            static final String RECOVERY_SERVICE_MAPPING = "RecoveryServiceDto.RecoveryServiceMapping";
            static final String RECOVERY_SERVICE_BY_MONTH_MAPPING = "RecoveryServiceDto.RecoveryServiceByMonthMapping";
        }

        static class StoredProcedureName{
            static final String RECOVERY_SERVICE = "SHP_DB_RECVRY_SERVC_PROC";
            static final String RECOVERY_SERVICE_BY_MONTH = "SHP_DB_RECVRY_SERVC_MNTH_PROC";
        }

        public static class StoredProcedureQueryName{
            public static final String RECOVERY_SERVICE = "RecoveryServiceDto.getRecoveryService";
            public static final String RECOVERY_SERVICE_BY_MONTH = "RecoveryServiceDto.getRecoveryServiceByMonth";
        }
    }
}
