package com.envista.msi.api.web.rest.dto.dashboard.shipmentoverview;

import com.envista.msi.api.domain.util.DashboardStoredProcParam;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Siddhant on 14/02/2017.
 */
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = AverageWeightModeShipmtDto.Config.StoredProcedureQueryName.AVG_WEIGHT_MODE_SHIPMENT,
                procedureName = AverageWeightModeShipmtDto.Config.StoredProcedureName.AVG_WEIGHT_MODE_SHIPMENT,
                resultSetMappings = {AverageWeightModeShipmtDto.Config.ResultMappings.AVG_WEIGHT_MODE_SHIPMENT_MAPPING},
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightByModeShipmtParam.DATE_TYPE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightByModeShipmtParam.CONVERTED_WEIGHT_UNIT_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightByModeShipmtParam.MODES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightByModeShipmtParam.SERVICES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightByModeShipmtParam.ACCESSORIAL_NAME_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightByModeShipmtParam.LANES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightByModeShipmtParam.FROM_DATE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightByModeShipmtParam.TO_DATE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightByModeShipmtParam.CARRIER_IDS_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightByModeShipmtParam.CUSTOMER_IDS_CSV_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightByModeShipmtParam.TOP_TEN_ACCESSORIAL_PARAM, type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = DashboardStoredProcParam.AverageWeightByModeShipmtParam.AVG_WEGT_MODE_SHIPMT_PARAM, type = Void.class)
                }),
        @NamedStoredProcedureQuery(name = AverageWeightModeShipmtDto.Config.StoredProcedureQueryName.AVG_WEIGHT_MODE_SHIPMENT_BY_CARRIER,
                procedureName = AverageWeightModeShipmtDto.Config.StoredProcedureName.AVG_WEIGHT_MODE_SHIPMENT_BY_CARRIER,
                resultSetMappings = {AverageWeightModeShipmtDto.Config.ResultMappings.AVG_WEIGHT_MODE_SHIPMENT_BY_CARRIER_MAPPING},
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByCarrierParam.DATE_TYPE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByCarrierParam.MODES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByCarrierParam.SERVICES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByCarrierParam.ACCESSORIAL_NAME_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByCarrierParam.LANES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByCarrierParam.FROM_DATE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByCarrierParam.TO_DATE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByCarrierParam.ORIGINAL_FROM_DATE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByCarrierParam.ORIGINAL_TO_DATE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByCarrierParam.CONVERTED_WEIGHT_UNIT_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByCarrierParam.CARRIER_IDS_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByCarrierParam.CUSTOMER_IDS_CSV_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByCarrierParam.TOP_TEN_ACCESSORIAL_PARAM, type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = DashboardStoredProcParam.AverageWeightShipmentByCarrierParam.AVG_WGT_SHIPMT_BY_CARR_PARAM, type = Void.class)
                }),
        @NamedStoredProcedureQuery(name = AverageWeightModeShipmtDto.Config.StoredProcedureQueryName.AVG_WEIGHT_MODE_SHIPMENT_BY_MONTH,
                procedureName = AverageWeightModeShipmtDto.Config.StoredProcedureName.AVG_WEIGHT_MODE_SHIPMENT_BY_MONTH,
                resultSetMappings = {AverageWeightModeShipmtDto.Config.ResultMappings.AVG_WEIGHT_MODE_SHIPMENT_BY_MONTH_MAPPING},
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByMonthParam.DATE_TYPE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByMonthParam.MODES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByMonthParam.SERVICES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByMonthParam.ACCESSORIAL_NAME_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByMonthParam.LANES_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByMonthParam.FROM_DATE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByMonthParam.TO_DATE_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByMonthParam.CONVERTED_WEIGHT_UNIT_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByMonthParam.CARRIER_IDS_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByMonthParam.CUSTOMER_IDS_CSV_PARAM, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = DashboardStoredProcParam.AverageWeightShipmentByMonthParam.TOP_TEN_ACCESSORIAL_PARAM, type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = DashboardStoredProcParam.AverageWeightShipmentByMonthParam.AVG_WGT_SHIPMT_BY_MNTH_PARAM, type = Void.class)
                })
})

@SqlResultSetMappings({
        @SqlResultSetMapping(name = AverageWeightModeShipmtDto.Config.ResultMappings.AVG_WEIGHT_MODE_SHIPMENT_MAPPING, classes = {
                @ConstructorResult(
                        targetClass = AverageWeightModeShipmtDto.class,
                        columns = {
                                @ColumnResult(name = "BILLING_DATE", type = String.class),
                                @ColumnResult(name = "MODES", type = String.class),
                                @ColumnResult(name = "NET_WEIGHT", type = Double.class)
                        }
                )
        }),
        @SqlResultSetMapping(name = AverageWeightModeShipmtDto.Config.ResultMappings.AVG_WEIGHT_MODE_SHIPMENT_BY_CARRIER_MAPPING, classes = {
                @ConstructorResult(
                        targetClass = AverageWeightModeShipmtDto.class,
                        columns = {
                                @ColumnResult(name = "ID", type = Long.class),
                                @ColumnResult(name = "NAME", type = String.class),
                                @ColumnResult(name = "VALUE", type = Double.class)
                        }
                )
        }),
        @SqlResultSetMapping(name = AverageWeightModeShipmtDto.Config.ResultMappings.AVG_WEIGHT_MODE_SHIPMENT_BY_MONTH_MAPPING, classes = {
                @ConstructorResult(
                        targetClass = AverageWeightModeShipmtDto.class,
                        columns = {
                                @ColumnResult(name = "BILL_DATE", type = Date.class),
                                @ColumnResult(name = "AMOUNT", type = Double.class),
                        }
                )
        })
})

@Entity
public class AverageWeightModeShipmtDto implements Serializable {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "VALUE")
    private Double value;

    @Column(name = "BILLING_DATE")
    private String billingDate;

    @Column(name = "MODES")
    private String modes;

    @Column(name = "NET_WEIGHT")
    private Double netWeight;

    @Column(name = "BILLL_DATE")
    private Date billDate;

    @Column(name = "AMOUNT")
    private Double amount;

    public AverageWeightModeShipmtDto() {    }

    public AverageWeightModeShipmtDto(String billingDate, String modes, Double netWeight) {
        this.billingDate = billingDate;
        this.modes = modes;
        this.netWeight = netWeight;
    }

    public AverageWeightModeShipmtDto(Long id, String name, Double value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public AverageWeightModeShipmtDto(Date billDate, Double amount) {
        this.billDate = billDate;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(String billingDate) {
        this.billingDate = billingDate;
    }

    public String getModes() {
        return modes;
    }

    public void setModes(String modes) {
        this.modes = modes;
    }

    public Double getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(Double netWeight) {
        this.netWeight = netWeight;
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

    public static class Config{
        static class ResultMappings{
            static final String AVG_WEIGHT_MODE_SHIPMENT_MAPPING = "AverageWeightModeShipmtDto.AvgWeightByModeShipmentMapping";
            static final String AVG_WEIGHT_MODE_SHIPMENT_BY_CARRIER_MAPPING = "AverageWeightModeShipmtDto.AvgWeightModeByCarrierMapping";
            static final String AVG_WEIGHT_MODE_SHIPMENT_BY_MONTH_MAPPING = "AverageWeightModeShipmtDto.AvgWeightModetByMonthMapping";
        }

        static class StoredProcedureName{
            static final String AVG_WEIGHT_MODE_SHIPMENT = "SHP_DB_AVG_WGT_PROC";
            static final String AVG_WEIGHT_MODE_SHIPMENT_BY_CARRIER = "SHP_DB_AVG_WGT_BY_CARR_PROC";
            static final String AVG_WEIGHT_MODE_SHIPMENT_BY_MONTH = "SHP_DB_AVG_WGT_BY_MNTH_PROC";
        }

        public static class StoredProcedureQueryName{
            public static final String AVG_WEIGHT_MODE_SHIPMENT = "AverageWeightModeShipmtDto.getAverageWeightModeShipment";
            public static final String AVG_WEIGHT_MODE_SHIPMENT_BY_CARRIER = "AverageWeightModeShipmtDto.getAverageWeightModeByCarrier";
            public static final String AVG_WEIGHT_MODE_SHIPMENT_BY_MONTH = "AverageWeightModeShipmtDto.getAverageWeightModeByMonth";
        }
    }
}
