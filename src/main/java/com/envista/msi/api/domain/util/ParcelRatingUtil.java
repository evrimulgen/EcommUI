package com.envista.msi.api.domain.util;

import com.envista.msi.api.web.rest.dto.rtr.ParcelAuditDetailsDto;
import com.envista.msi.api.web.rest.dto.rtr.ParcelAuditRequestResponseLog;
import com.envista.msi.api.web.rest.dto.rtr.ParcelRateDetailsDto;
import com.envista.msi.api.web.rest.dto.rtr.RatedChargeDetailsDto;
import com.envista.msi.api.web.rest.util.audit.parcel.ParcelAuditConstant;
import com.envista.msi.api.web.rest.util.audit.parcel.ParcelRateRequest;
import com.envista.msi.api.web.rest.util.audit.parcel.ParcelRateResponse;
import com.envista.msi.api.web.rest.util.audit.parcel.ParcelRateResponseParser;
import com.envista.msi.rating.bean.AccessorialDto;
import com.envista.msi.rating.bean.RatingQueueBean;
import com.envista.msi.rating.bean.ServiceFlagAccessorialBean;
import com.envista.msi.rating.dao.DirectJDBCDAO;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Created by Sujit kumar on 20/04/2018.
 */
public class ParcelRatingUtil {

    private static final Logger m_log = LoggerFactory.getLogger(ParcelRatingUtil.class);

    public static ParcelAuditDetailsDto getLatestFrightCharge(List<ParcelAuditDetailsDto> parcelAuditDetails) {
        ParcelAuditDetailsDto parcelAuditDetail = null;
        Long maxEntityId = 0l;
        if (parcelAuditDetails != null && !parcelAuditDetails.isEmpty()) {
            for (ParcelAuditDetailsDto auditDetail : parcelAuditDetails) {
                if (auditDetail != null) {
                    if ("FRT".equalsIgnoreCase(auditDetail.getChargeClassificationCode())) {
                        if (auditDetail.getId() > maxEntityId) {
                            parcelAuditDetail = auditDetail;
                        }
                    }
                }
            }
        }
        return parcelAuditDetail;
    }

    public static ParcelAuditDetailsDto getFirstFrightChargeForNonUpsCarrier(List<ParcelAuditDetailsDto> parcelAuditDetails) {
        if (parcelAuditDetails != null && !parcelAuditDetails.isEmpty()) {
            for (ParcelAuditDetailsDto auditDetail : parcelAuditDetails) {
                if (auditDetail != null) {
                    if ("FRT".equalsIgnoreCase(auditDetail.getChargeClassificationCode())) {
                        return auditDetail;
                    }
                }
            }
        }
        return null;
    }

    public static Map<Long, List<ParcelAuditDetailsDto>> organiseShipmentsByParentId(List<ParcelAuditDetailsDto> parcelAuditDetails) {
        Map<Long, List<ParcelAuditDetailsDto>> shipments = null;
        if (parcelAuditDetails != null && !parcelAuditDetails.isEmpty()) {
            Collections.sort(parcelAuditDetails, new Comparator<ParcelAuditDetailsDto>() {
                @Override
                public int compare(ParcelAuditDetailsDto o1, ParcelAuditDetailsDto o2) {
                    if (o1 != null && o2 != null) {
                        return o1.getParentId().compareTo(o2.getParentId());
                    }
                    return 0;
                }
            });
            shipments = new LinkedHashMap<>();
            for (ParcelAuditDetailsDto auditDetail : parcelAuditDetails) {
                if (auditDetail != null) {
                    if (shipments.containsKey(auditDetail.getParentId())) {
                        shipments.get(auditDetail.getParentId()).add(auditDetail);
                    } else {
                        List<ParcelAuditDetailsDto> shipmentChanges = new ArrayList<>(Arrays.asList(auditDetail));
                        shipments.put(auditDetail.getParentId(), shipmentChanges);
                    }
                }
            }
        }
        return shipments;
    }

    public static boolean containsCharge(String charge, List<ParcelAuditDetailsDto> parcelAuditDetails) {
        if (charge != null && !charge.isEmpty() && parcelAuditDetails != null && !parcelAuditDetails.isEmpty()) {
            for (ParcelAuditDetailsDto auditDetails : parcelAuditDetails) {
                if (auditDetails != null && auditDetails.getChargeDescription() != null && charge.equalsIgnoreCase(auditDetails.getChargeDescription())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static BigDecimal findAmountByChargeClassificationCodeType(String chargeType, List<ParcelAuditDetailsDto> shipmentCharges) {
        BigDecimal amount = new BigDecimal("0");
        if (chargeType != null && !chargeType.isEmpty() && shipmentCharges != null && !shipmentCharges.isEmpty()) {
            for (ParcelAuditDetailsDto ratedCharge : shipmentCharges) {
                if (ratedCharge != null) {
                    if (chargeType.equalsIgnoreCase(ratedCharge.getChargeClassificationCode())) {
                        if (ratedCharge.getRtrAmount() != null) {
                            amount = amount.add(ratedCharge.getRtrAmount());
                        }
                        break;
                    }
                }
            }
        }
        return amount;
    }

    public static BigDecimal findAmountByChargeDescriptionCodeType(String chargeType, List<ParcelAuditDetailsDto> shipmentCharges) {
        BigDecimal amount = new BigDecimal("0");
        if (chargeType != null && !chargeType.isEmpty() && shipmentCharges != null && !shipmentCharges.isEmpty()) {
            for (ParcelAuditDetailsDto ratedCharge : shipmentCharges) {
                if (ratedCharge != null) {
                    if (chargeType.equalsIgnoreCase(ratedCharge.getChargeDescriptionCode())) {
                        if (ratedCharge.getRtrAmount() != null) {
                            amount = amount.add(ratedCharge.getRtrAmount());
                        }
                        break;
                    }
                }
            }
        }
        return amount;
    }

    public static BigDecimal findRtrAmountByChargeClassificationCode(String chargeClassificationCode, List<RatedChargeDetailsDto> shipmentCharges) {
        return findRtrAmountByChargeClassificationCode(chargeClassificationCode, shipmentCharges, null);
    }

    public static BigDecimal findRtrAmountByChargeClassificationCode(String chargeClassificationCode, List<RatedChargeDetailsDto> shipmentCharges, Long excludeGffId) {
        BigDecimal amount = new BigDecimal("0");
        if (chargeClassificationCode != null && !chargeClassificationCode.isEmpty() && shipmentCharges != null && !shipmentCharges.isEmpty()) {
            for (RatedChargeDetailsDto ratedCharge : shipmentCharges) {
                if (ratedCharge != null) {
                    if (excludeGffId != null && ratedCharge.getId().equals(excludeGffId)) continue;
                    if (chargeClassificationCode.equalsIgnoreCase(ratedCharge.getChargeClassificationCode())) {
                        if (ratedCharge.getRatedAmount() != null) {
                            amount = amount.add(ratedCharge.getRatedAmount());
                        }
                    }
                }
            }
        }
        return amount;
    }

    public static BigDecimal findRtrAmountByChargeClassificationCodeAndChargeDescriptionCode(String chargeClassificationCode, String chargeDescriptionCode, List<RatedChargeDetailsDto> shipmentCharges) {
        return findRtrAmountByChargeClassificationCodeAndChargeDescriptionCode(chargeClassificationCode, chargeDescriptionCode, shipmentCharges, null);
    }

    public static BigDecimal findRtrAmountByChargeClassificationCodeAndChargeDescriptionCode(String chargeClassificationCode, String chargeDescriptionCode, List<RatedChargeDetailsDto> shipmentCharges, Long excludeGffId) {
        BigDecimal amount = new BigDecimal("0");
        if (chargeClassificationCode != null && !chargeClassificationCode.isEmpty() && shipmentCharges != null && !shipmentCharges.isEmpty()) {
            for (RatedChargeDetailsDto ratedCharge : shipmentCharges) {
                if (ratedCharge != null) {
                    if (ratedCharge.getId().equals(excludeGffId)) continue;

                    if (chargeClassificationCode.equalsIgnoreCase(ratedCharge.getChargeClassificationCode()) && chargeDescriptionCode.equalsIgnoreCase(ratedCharge.getChargeDescriptionCode())) {
                        if (ratedCharge.getRatedAmount() != null) {
                            amount = amount.add(ratedCharge.getRatedAmount());
                        }
                    }
                }
            }
        }
        return amount;
    }

    public static BigDecimal getRatedBaseDiscount(List<RatedChargeDetailsDto> ratedCharges) {
        BigDecimal amount = new BigDecimal("0");
        if (ratedCharges != null && !ratedCharges.isEmpty()) {
            for (RatedChargeDetailsDto ratedCharge : ratedCharges) {
                if (ratedCharge != null) {
                    if ("FRT".equalsIgnoreCase(ratedCharge.getChargeClassificationCode())) {
                        if (ratedCharge.getRatedBaseDiscount() != null) amount = ratedCharge.getRatedBaseDiscount();
                        break;
                    }
                }
            }
        }
        return amount;
    }

    public static BigDecimal getRatedEarnedDiscount(List<RatedChargeDetailsDto> ratedCharges) {
        BigDecimal amount = new BigDecimal("0");
        if (ratedCharges != null && !ratedCharges.isEmpty()) {
            for (RatedChargeDetailsDto ratedCharge : ratedCharges) {
                if (ratedCharge != null) {
                    if ("FRT".equalsIgnoreCase(ratedCharge.getChargeClassificationCode())) {
                        if (ratedCharge.getRatedEarnedDiscount() != null) amount = ratedCharge.getRatedEarnedDiscount();
                        break;
                    }
                }
            }
        }
        return amount;
    }

    public static BigDecimal findAccessorialAmountByAccessorialCode(String accessorialCode, List<RatedChargeDetailsDto> shipmentCharges) {
        BigDecimal amount = new BigDecimal("0");
        if (accessorialCode != null && !accessorialCode.isEmpty() && shipmentCharges != null && !shipmentCharges.isEmpty()) {
            RatedChargeDetailsDto ratedAcc = shipmentCharges.get(0);
            if (ratedAcc != null) {
                if (ratedAcc.getAccessorial1Code() != null && !ratedAcc.getAccessorial1Code().isEmpty() && accessorialCode.equalsIgnoreCase(ratedAcc.getAccessorial1Code())) {
                    amount = ratedAcc.getAccessorial1();
                } else if (ratedAcc.getAccessorial2Code() != null && !ratedAcc.getAccessorial2Code().isEmpty() && accessorialCode.equalsIgnoreCase(ratedAcc.getAccessorial2Code())) {
                    amount = ratedAcc.getAccessorial2();
                } else if (ratedAcc.getAccessorial3Code() != null && !ratedAcc.getAccessorial3Code().isEmpty() && accessorialCode.equalsIgnoreCase(ratedAcc.getAccessorial3Code())) {
                    amount = ratedAcc.getAccessorial3();
                } else if (ratedAcc.getAccessorial4Code() != null && !ratedAcc.getAccessorial4Code().isEmpty() && accessorialCode.equalsIgnoreCase(ratedAcc.getAccessorial4Code())) {
                    amount = ratedAcc.getAccessorial4();
                }
            }
        }
        return amount;
    }

    public static BigDecimal getRatedFreightChargeForCommOrResAjustment(List<RatedChargeDetailsDto> shipmentCharges) {
        BigDecimal amount = new BigDecimal("0");
        if (shipmentCharges != null && !shipmentCharges.isEmpty()) {
            amount = shipmentCharges.get(0).getFreightCharge();
        }
        return amount;
    }

    public static BigDecimal getRatedFuelChargeForCommOrResAjustment(List<RatedChargeDetailsDto> shipmentCharges) {
        BigDecimal amount = new BigDecimal("0");
        if (shipmentCharges != null && !shipmentCharges.isEmpty()) {
            amount = shipmentCharges.get(0).getFuelSurcharge();
        }
        return amount;
    }

    public static Map<String, List<ParcelAuditDetailsDto>> prepareTrackingNumberWiseAuditDetails(List<ParcelAuditDetailsDto> auditDetailsList) {
        Map<String, List<ParcelAuditDetailsDto>> parcelAuditMap = null;
        if (auditDetailsList != null && !auditDetailsList.isEmpty()) {
            parcelAuditMap = new HashMap<>();
            for (ParcelAuditDetailsDto parcelAuditDetails : auditDetailsList) {
                if (parcelAuditDetails != null) {
                    String trackingNumber = parcelAuditDetails.getTrackingNumber();
                    if (trackingNumber != null && !trackingNumber.isEmpty() && parcelAuditMap.containsKey(parcelAuditDetails.getTrackingNumber())) {
                        parcelAuditMap.get(trackingNumber).add(parcelAuditDetails);
                    } else {
                        List<ParcelAuditDetailsDto> auditList = new ArrayList<>();
                        auditList.add(parcelAuditDetails);
                        parcelAuditMap.put(trackingNumber, auditList);
                    }
                }
            }
        }
        return parcelAuditMap;
    }

    public static String findServiceLevel(List<ParcelAuditDetailsDto> parcelAuditDetails) {
        String serviceLevel = null;
        if (parcelAuditDetails != null && !parcelAuditDetails.isEmpty()) {
            for (ParcelAuditDetailsDto auditDetails : parcelAuditDetails) {
                if (auditDetails != null && auditDetails.getChargeClassificationCode() != null
                        && ParcelAuditConstant.ChargeClassificationCode.FRT.name().equals(auditDetails.getChargeClassificationCode())
                        && auditDetails.getNetAmount() != null && !auditDetails.getNetAmount().isEmpty()) {
                    serviceLevel = auditDetails.getServiceLevel();
                    break;
                } else if (auditDetails != null && auditDetails.getChargeClassificationCode() != null
                        && ParcelAuditConstant.ChargeClassificationCode.ACS.name().equals(auditDetails.getChargeClassificationCode())
                        && auditDetails.getNetAmount() != null && !auditDetails.getNetAmount().isEmpty()) {
                    serviceLevel = auditDetails.getServiceLevel();
                }

            }
        }
        return serviceLevel;
    }

    public static RatingQueueBean prepareShipmentEntryForUpsShipment(List<ParcelAuditDetailsDto> shipmentDetails, String rateSet, List<ServiceFlagAccessorialBean> accessorialBeans, List<ParcelAuditDetailsDto> trackingNumDetails, Map<String, Long> hwtSequenceInfo) throws JSONException {
        RatingQueueBean ratingQueueBean = new RatingQueueBean();
        ParcelAuditDetailsDto firstCharge;
        boolean hwt = false;

        List<ParcelAuditDetailsDto> ratingShipmentDetails = getParentIdCharges(trackingNumDetails, shipmentDetails.get(0).getParentId());

        boolean excludeRating = checkCarrierCreditsToExcludeRating(ratingShipmentDetails);

        if (excludeRating)
            ratingQueueBean.setExcludeRating(1);

        if (hwtSequenceInfo == null) {
            if (shipmentDetails.get(0).getPickupDate() == null)
                setPrevParentIdShipDate(shipmentDetails, trackingNumDetails);
            firstCharge = shipmentDetails.get(0);
            hwtSequenceInfo = new HashMap<>();
            hwtSequenceInfo.put(firstCharge.getTrackingNumber(), firstCharge.getParentId());
        } else {
            firstCharge = getMinParentCharge(shipmentDetails, hwtSequenceInfo);
            hwtSequenceInfo.remove("minParentId");
            hwt = true;
        }
        if (firstCharge != null) {
            prepareXmlReqAddressInfo(trackingNumDetails, firstCharge);
            ratingQueueBean.setGffId(firstCharge.getId());
            ratingQueueBean.setTrackingNumber(firstCharge.getTrackingNumber());
            ratingQueueBean.setParentId(firstCharge.getParentId());
            ratingQueueBean.setCarrierId(firstCharge.getCarrierId());
            ratingQueueBean.setSenderBilledZipCode(firstCharge.getSenderBilledZipCode());
            ratingQueueBean.setReceiverBilledZipCode(firstCharge.getReceiverBilledZipCode());
            ratingQueueBean.setWorldeEaseNum("N");
            ratingQueueBean.setComToRes("");
            ratingQueueBean.setReturnFlag(isReturnShipment(ratingShipmentDetails) == true ? "Y" : "N");
            ratingQueueBean.setResiFlag("N");
            boolean hasRJ5Charge = false;


            if (shipmentDetails != null && !shipmentDetails.isEmpty()) {
                for (ParcelAuditDetailsDto auditDetails : shipmentDetails) {

                    if (auditDetails != null && auditDetails.getPackageDimension() != null && !auditDetails.getPackageDimension().isEmpty()) {
                        try {
                            String[] dimension = auditDetails.getPackageDimension().toLowerCase().split("x");
                            if (dimension != null && dimension.length > 2) {
                                auditDetails.setDimLength(dimension[0] != null ? dimension[0].trim() : "");
                                auditDetails.setDimWidth(dimension[1] != null ? dimension[1].trim() : "");
                                auditDetails.setDimHeight(dimension[2] != null ? dimension[2].trim() : "");
                            }
                        } catch (Exception e) {
                            m_log.error("ERROR - " + e.getStackTrace() + "--Parent Id->" + shipmentDetails.get(0).getParentId());
                            e.printStackTrace();
                        }
                    }
                    if ("RJ5".equalsIgnoreCase(auditDetails.getChargeDescriptionCode())) {
                        hasRJ5Charge = true;
                    }
                }

                if (firstCharge != null) {
                    String billOption = (null == firstCharge.getBillOption() ? "" : firstCharge.getBillOption());
                    if (billOption.equalsIgnoreCase("Prepaid") || billOption.equals("1") || billOption.equalsIgnoreCase("Outbound")) {
                        billOption = "PP";
                    } else if (billOption.equalsIgnoreCase("Collect") || billOption.equals("2")) {
                        billOption = "FC";
                    } else if (billOption.equalsIgnoreCase("Third Party") || billOption.equals("3")) {
                        billOption = "TP";
                    }

                    ratingQueueBean.setBillOption(billOption);
                    if (ratingQueueBean.getZone() == null && firstCharge.getZone() != null) {
                        ratingQueueBean.setZone(firstCharge.getZone());
                    }

                    JSONArray accJsonArr = new JSONArray();
                    for (ParcelAuditDetailsDto auditDetails : shipmentDetails) {
                        if (auditDetails != null) {
                            try {
                                if (shipmentDetails.get(0).getParentId().compareTo(auditDetails.getParentId()) == 0) {


                                    if (auditDetails.getWorldeEaseNum() != null && !auditDetails.getWorldeEaseNum().isEmpty())
                                        ratingQueueBean.setWorldeEaseNum("Y");


                                    setComToResVal(ratingQueueBean, shipmentDetails, trackingNumDetails);
                                }

                                if (auditDetails.getChargeClassificationCode() != null
                                        && ParcelAuditConstant.ChargeClassificationCode.ACC.name().equalsIgnoreCase(auditDetails.getChargeClassificationCode())) {
                                    if (auditDetails.getChargeDescriptionCode() != null && !auditDetails.getChargeDescriptionCode().isEmpty()
                                            && !"RJ5".equalsIgnoreCase(auditDetails.getChargeDescriptionCode())) {
                                        JSONObject accJson = new JSONObject();

                                        accJson.put("seq", hwtSequenceInfo.get(auditDetails.getTrackingNumber()));


                                        accJson.put("netAmount", auditDetails.getNetAmount() != null ? auditDetails.getNetAmount().toString() : "0.00");
                                        accJson.put("weight", auditDetails.getPackageWeight() != null ? auditDetails.getPackageWeight().toString() : "0.00");
                                        if (auditDetails.getWeightUnit() != null && "O".equalsIgnoreCase(auditDetails.getWeightUnit()))
                                            auditDetails.setWeightUnit("OUNCE");
                                        accJson.put("weightUnit", (null == auditDetails.getWeightUnit() || auditDetails.getWeightUnit().isEmpty() || "L".equalsIgnoreCase(auditDetails.getWeightUnit()) ? "LBS" : auditDetails.getWeightUnit()));
                                        accJson.put("quantity", (null == auditDetails.getItemQuantity() || auditDetails.getItemQuantity().isEmpty() ? 1l : Long.parseLong(auditDetails.getItemQuantity())));
                                        accJson.put("quantityUnit", (null == auditDetails.getQuantityUnit() || auditDetails.getQuantityUnit().isEmpty() ? "PCS" : auditDetails.getQuantityUnit()));

                                        ServiceFlagAccessorialBean bean = getAccessorialBean(accessorialBeans, auditDetails.getChargeDescription(), auditDetails.getChargeDescriptionCode(), 21L);

                                        if (auditDetails.getChargeDescription() != null &&
                                                "REMOTE AREA SURCHARGE".equalsIgnoreCase(auditDetails.getChargeDescription())) {

                                            if ("AK".equalsIgnoreCase(auditDetails.getReceiverState()))
                                                bean.setLookUpValue("RASAK");
                                            if ("HI".equalsIgnoreCase(auditDetails.getReceiverState()))
                                                bean.setLookUpValue("RASHI");
                                        }

                                        if (bean != null) {
                                            accJson.put("code", bean.getLookUpValue());
                                        } else {
                                            DirectJDBCDAO.getInstance().insertUnknownMappingCode(auditDetails);
                                            m_log.warn("Service flag accessorial code is not found in look up table: Charge Desription Code->" + auditDetails.getChargeDescriptionCode() + "--Charge Desription->" + auditDetails.getChargeDescription() + "--Parent Id->" + auditDetails.getParentId());
                                            accJson.put("code", auditDetails.getChargeDescriptionCode());
                                        }
                                        if (!checkAccExist(accJsonArr, accJson))
                                            accJsonArr.put(accJson);
                                    }
                                }
                            } catch (Exception e) {
                                m_log.error("ERROR - " + e.getStackTrace() + "--Parent Id->" + ratingQueueBean.getParentId());
                                e.printStackTrace();
                            }
                        }
                    }
                    ratingQueueBean.setAccessorialInfo(accJsonArr != null && accJsonArr.length() > 0 ? accJsonArr.toString() : null);
                    String scacCode = (null == firstCharge.getRtrScacCode() ? "" : "FDEG".equals(firstCharge.getRtrScacCode()) ? "FDE" : firstCharge.getRtrScacCode());
                    ratingQueueBean.setScacCode(scacCode);

                    String currency = (null == firstCharge.getCurrency() || firstCharge.getCurrency().isEmpty() ? "USD" : firstCharge.getCurrency());
                    ratingQueueBean.setCurrencyCode(currency);

                    String serviceLevel = findServiceLevel(shipmentDetails);
                    if (serviceLevel == null || serviceLevel.trim().isEmpty()) {
                        // System.out.println("Invalid Service Level for " + shipmentDetails.get(0).getTrackingNumber());
                        m_log.warn("Invalid OR Empty Service Level for " + firstCharge.getTrackingNumber() + "--Parent Id->" + firstCharge.getParentId() + "");
                        return null;
                    } else {
                        ratingQueueBean.setService(serviceLevel);
                    }
                    ratingQueueBean.setCustomerCode(firstCharge.getCustomerCode());
                    ratingQueueBean.setShipperNumber(shipmentDetails.get(0).getShipperNumber());
                    ratingQueueBean.setRevenueTier(shipmentDetails.get(0).getRevenueTier());
                    JSONArray itemJsonArr = new JSONArray();
                    for (Map.Entry<String, Long> entry : hwtSequenceInfo.entrySet()) {


                        ParcelAuditDetailsDto latestFreightCharge = ParcelRatingUtil.getLatestFrightCharge(shipmentDetails, entry.getKey());

                        if (latestFreightCharge != null) {

                            setItemPackageInfo(latestFreightCharge, trackingNumDetails);

                            float weight = (null == latestFreightCharge.getPackageWeight() || latestFreightCharge.getPackageWeight().isEmpty() ? 1f : Float.parseFloat(latestFreightCharge.getPackageWeight()));
                            if (latestFreightCharge.getWeightUnit() != null && "O".equalsIgnoreCase(latestFreightCharge.getWeightUnit()))
                                latestFreightCharge.setWeightUnit("OUNCE");
                            String weightUnit = (null == latestFreightCharge.getWeightUnit() || latestFreightCharge.getWeightUnit().isEmpty() || "L".equalsIgnoreCase(latestFreightCharge.getWeightUnit()) ? "LBS" : latestFreightCharge.getWeightUnit());
                            long quantity = (null == latestFreightCharge.getItemQuantity() || latestFreightCharge.getItemQuantity().isEmpty() || Integer.parseInt(latestFreightCharge.getItemQuantity().trim()) == 0 ? 1l : Long.parseLong(latestFreightCharge.getItemQuantity()));
                            String quantityUnit = (null == latestFreightCharge.getQuantityUnit() || latestFreightCharge.getQuantityUnit().isEmpty() ? "PCS" : latestFreightCharge.getQuantityUnit());
                            float dimLenght = (null == latestFreightCharge.getDimLength() || latestFreightCharge.getDimLength().isEmpty() ? 0.0f : Float.parseFloat(latestFreightCharge.getDimLength()));
                            float dimWidth = (null == latestFreightCharge.getDimWidth() || latestFreightCharge.getDimWidth().isEmpty() ? 0.0f : Float.parseFloat(latestFreightCharge.getDimWidth()));
                            float dimHeight = (null == latestFreightCharge.getDimHeight() || latestFreightCharge.getDimHeight().isEmpty() ? 0.0f : Float.parseFloat(latestFreightCharge.getDimHeight()));
                            String dimUnit = (null == latestFreightCharge.getUnitOfDim() || latestFreightCharge.getUnitOfDim().isEmpty() ? "" : (latestFreightCharge.getUnitOfDim().equalsIgnoreCase("I") ? "in" : latestFreightCharge.getUnitOfDim()));
                            float actualWeight = (null == latestFreightCharge.getActualWeight() ? 0.0f : latestFreightCharge.getActualWeight().floatValue());
                            String actualWeightUnit = (null == latestFreightCharge.getActualWeightUnit() || latestFreightCharge.getActualWeightUnit().isEmpty() || "L".equalsIgnoreCase(latestFreightCharge.getActualWeightUnit()) ? "LBS" : latestFreightCharge.getActualWeightUnit());

                            JSONObject accJson = new JSONObject();
                            accJson.put("seq", entry.getValue());
                            accJson.put("weight", String.valueOf(weight));
                            accJson.put("weightUnit", weightUnit);
                            accJson.put("quantity", String.valueOf(quantity));
                            accJson.put("quantityUnit", quantityUnit);
                            accJson.put("dimLenght", String.valueOf(dimLenght));
                            accJson.put("dimWidth", String.valueOf(dimWidth));
                            accJson.put("dimHeight", String.valueOf(dimHeight));
                            accJson.put("dimUnit", dimUnit);
                            accJson.put("actualWeight", String.valueOf(actualWeight));
                            accJson.put("actualWeightUnit", actualWeightUnit);
                            accJson.put("packageType", firstCharge.getPackageType());

                            ratingQueueBean.setPackageType(firstCharge.getPackageType());
                            itemJsonArr.put(accJson);

                        } else {
                            throw new RuntimeException("Freight Item not found");
                        }
                    }
                    ratingQueueBean.setItemTagInfo(itemJsonArr != null ? itemJsonArr.toString() : null);

                    ratingQueueBean.setShipDate(firstCharge.getPickupDate());

                    if ((null == firstCharge.getSenderCountry() || firstCharge.getSenderCountry().isEmpty())
                            && (null == firstCharge.getSenderState() || firstCharge.getSenderState().isEmpty())
                            && (null == firstCharge.getSenderCity() || firstCharge.getSenderCity().isEmpty())
                            && (null == firstCharge.getSenderZipCode() || firstCharge.getSenderZipCode().isEmpty())) {

                        firstCharge.setSenderCity(firstCharge.getShipperCity());
                        firstCharge.setSenderState(firstCharge.getShipperState());
                        firstCharge.setSenderCountry(firstCharge.getShipperCountry());
                        firstCharge.setSenderZipCode(firstCharge.getShipperZip());

                    }

                    String senderCountry = (null == firstCharge.getSenderCountry() || firstCharge.getSenderCountry().isEmpty() ? "US" : firstCharge.getSenderCountry());
                    String senderState = (null == firstCharge.getSenderState() ? "" : firstCharge.getSenderState());
                    String senderCity = (null == firstCharge.getSenderCity() ? "" : firstCharge.getSenderCity());
                    String senderZipCode = (null == firstCharge.getSenderZipCode() ? "" : firstCharge.getSenderZipCode());


                    ratingQueueBean.setShipperCountry(senderCountry);
                    ratingQueueBean.setShipperState(senderState);
                    ratingQueueBean.setShipperCity(senderCity);
                    ratingQueueBean.setShipperZip(senderZipCode);

                    if (firstCharge.getDeliveryDate() != null) {
                        ratingQueueBean.setDeliveryDate(firstCharge.getDeliveryDate());
                    } else {
                        ratingQueueBean.setDeliveryDate(firstCharge.getPickupDate());
                    }
                    String receiverCountry = (null == firstCharge.getReceiverCountry() || firstCharge.getReceiverCountry().isEmpty() ? "US" : firstCharge.getReceiverCountry());
                    String receiverState = (null == firstCharge.getReceiverState() ? "" : firstCharge.getReceiverState());
                    String receiverCity = (null == firstCharge.getReceiverCity() ? "" : firstCharge.getReceiverCity());
                    String receiverZipCode = (null == firstCharge.getReceiverZipCode() ? "" : firstCharge.getReceiverZipCode());

                    ratingQueueBean.setReceiverCountry(receiverCountry);
                    ratingQueueBean.setReceiverState(receiverState);
                    ratingQueueBean.setReceiverCity(receiverCity);
                    ratingQueueBean.setReceiverZip(receiverZipCode);
                    if (hwt) {
                        ratingQueueBean.setHwtIdentifier(firstCharge.getMultiWeightNumber());
                        ratingQueueBean.setTrackingNumber(firstCharge.getMultiWeightNumber());
                    }

                    ratingQueueBean.setRateSetName(rateSet);
                    if (firstCharge.getActualServiceBucket() != null)
                        ratingQueueBean.setActualServiceBucket(Long.valueOf(firstCharge.getActualServiceBucket()));

                    ratingQueueBean.setInvoiceDate(firstCharge.getInvoiceDate());
                    ratingQueueBean.setCustomerId(firstCharge.getCustomerId());


                    if ("".equalsIgnoreCase(ratingQueueBean.getComToRes()) && "N".equalsIgnoreCase(ratingQueueBean.getResiFlag())) {

                        Map<Long, List<ParcelAuditDetailsDto>> parentIdWiseShipments = ParcelRatingUtil.organiseShipmentsByParentId(trackingNumDetails);

                        if (parentIdWiseShipments != null && parentIdWiseShipments.size() > 1) {

                            //List<ParcelAuditDetailsDto> immediateParentIdInfo = getImmediateParentIdInfo(firstCharge.getParentId(), parentIdWiseShipments);


                            String resiFlag = DirectJDBCDAO.getInstance().getratingQueueInfoByParentId
                                    (firstCharge.getTrackingNumber(), firstCharge.getParentId(), ratingQueueBean.getReturnFlag());
                            if (resiFlag != null) {
                                ratingQueueBean.setResiFlag(resiFlag);
                            }

                        }

                    }

                }
            }
        }
        return ratingQueueBean;
    }

    private static boolean checkCarrierCreditsToExcludeRating(List<ParcelAuditDetailsDto> shipmentDetails) {


        if (shipmentDetails != null && shipmentDetails.size() > 0) {

            for (ParcelAuditDetailsDto dto : shipmentDetails) {

                if (((dto.getChargeCategoryDetailCode() != null && ParcelAuditConstant.UPS_EXCLUDE_RATING_CATDC_LIST.contains
                                (dto.getChargeCategoryDetailCode().toUpperCase())) ||
                                (dto.getChargeDescription() != null && ParcelAuditConstant.UPS_EXCLUDE_RATING_ADJUSTMENT_LIST.contains
                                        (dto.getChargeDescription().toUpperCase())))) {

                    return true;
                }
            }

        }
        return false;
    }

    private static void checkImmediateParentIdReturnFlag(RatingQueueBean ratingQueueBean, List<ParcelAuditDetailsDto> shipmentDetails, List<ParcelAuditDetailsDto> trackingNumDetails) {

        ParcelAuditDetailsDto dto = getImmediateFrtParentId(shipmentDetails, trackingNumDetails);

        if ("Y".equalsIgnoreCase(dto.getReturnFlag()))
            ratingQueueBean.setReturnFlag("Y");
    }

    private static void setComToResVal(RatingQueueBean ratingQueueBean, List<ParcelAuditDetailsDto> shipmentDetails, List<ParcelAuditDetailsDto> trackingNumDetails) {

        boolean resiSur = false;
        boolean resiFlagSet = false;
        boolean comToResSet = false;

        for (ParcelAuditDetailsDto auditDetails : trackingNumDetails) {

            if (auditDetails != null && shipmentDetails.get(0).getParentId().compareTo(auditDetails.getParentId()) > 0) {

                if (auditDetails.getChargeDescription() != null && "Residential Surcharge".equalsIgnoreCase(auditDetails.getChargeDescription()))
                    resiSur = true;

            }
        }

        for (ParcelAuditDetailsDto auditDetails : shipmentDetails) {

            if (auditDetails != null && shipmentDetails.get(0).getParentId().compareTo(auditDetails.getParentId()) == 0) {
                if (auditDetails.getChargeDescription() != null && auditDetails.getChargeDescriptionCode() != null) {

                    if (auditDetails.getChargeDescription() != null && "Residential Surcharge".equalsIgnoreCase(auditDetails.getChargeDescription()))
                        resiSur = true;

                    if (!resiFlagSet && (("RES".equalsIgnoreCase(auditDetails.getChargeDescriptionCode()) || "RS1".equalsIgnoreCase(auditDetails.getChargeDescriptionCode())) && auditDetails.getChargeDescription().toUpperCase().contains("RESIDENTIAL SURCHARGE"))) {
                        ratingQueueBean.setResiFlag("Y");
                    }
                    if (auditDetails.getChargeCategoryDetailCode() != null) {
                        if ("RADJ".equalsIgnoreCase(auditDetails.getChargeCategoryDetailCode()) && "RES".equalsIgnoreCase(auditDetails.getChargeDescriptionCode()) && auditDetails.getChargeDescription().toUpperCase().contains("RESIDENTIAL ADJUSTMENT")) {
                            ratingQueueBean.setResiFlag("Y");
                            ratingQueueBean.setComToRes("Y");
                            resiFlagSet = true;
                        }
                        if ("RADJ".equalsIgnoreCase(auditDetails.getChargeCategoryDetailCode()) && "COM".equalsIgnoreCase(auditDetails.getChargeDescriptionCode()) && auditDetails.getChargeDescription().toUpperCase().contains("COMMERCIAL ADJUSTMENT")) {
                            ratingQueueBean.setResiFlag("N");
                            ratingQueueBean.setComToRes("N");
                            resiFlagSet = true;
                        }
                        if (!comToResSet) {
                            if ("RADJ".equalsIgnoreCase(auditDetails.getChargeCategoryDetailCode()) && "RJ5".equalsIgnoreCase(auditDetails.getChargeDescriptionCode()) && auditDetails.getChargeDescription().toUpperCase().contains("RESIDENTIAL/COMMERCIAL ADJ") && resiSur) {
                                ratingQueueBean.setResiFlag("N");
                                ratingQueueBean.setComToRes("N");
                                comToResSet = true;
                                resiFlagSet = true;
                            }
                            if ("RADJ".equalsIgnoreCase(auditDetails.getChargeCategoryDetailCode()) && "RJ5".equalsIgnoreCase(auditDetails.getChargeDescriptionCode()) && auditDetails.getChargeDescription().toUpperCase().contains("RESIDENTIAL/COMMERCIAL ADJ") && !resiSur) {
                                ratingQueueBean.setResiFlag("Y");
                                ratingQueueBean.setComToRes("Y");
                                comToResSet = true;
                                resiFlagSet = true;
                            }
                        }

                    }
                }
            }
        }
    }

    private static ParcelAuditDetailsDto getMinParentCharge(List<ParcelAuditDetailsDto> shipmentDetails, Map<String, Long> hwtSequenceInfo) {

        for (ParcelAuditDetailsDto dto : shipmentDetails) {

            if (hwtSequenceInfo.get("minParentId").compareTo(dto.getId()) == 0)
                return dto;
        }

        return null;
    }

    private static boolean checkAccExist(JSONArray accJsonArr, JSONObject accJson) {

        BigDecimal accAmount = null;
        try {
            for (int i = 0; i < accJsonArr.length(); i++) {


                JSONObject jsonObject = (JSONObject) accJsonArr.get(i);

                if (jsonObject.getLong("seq") == accJson.getLong("seq") && jsonObject.getString("code").equalsIgnoreCase(accJson.getString("code"))) {

                    if (jsonObject.getString("netAmount") != null) {
                        accAmount = new BigDecimal(jsonObject.getString("netAmount"));
                        accAmount = accAmount.add(new BigDecimal(accJson.getString("netAmount")));

                        if (accAmount != null) {
                            jsonObject.remove("netAmount");
                            jsonObject.put("netAmount", accAmount.toString());

                            return true;
                        }

                    }
                }
            }


        } catch (JSONException e) {
            m_log.error("ERROR - " + e.getStackTrace());
            e.printStackTrace();
        }
        return false;
    }

    public static RatingQueueBean prepareShipmentEntryForNonUpsShipment(List<ParcelAuditDetailsDto> shipmentDetails, String rateSet, List<ServiceFlagAccessorialBean> accessorialBeans, List<ParcelAuditDetailsDto> trackingNumDetails, Map<String, Long> hwtSequenceInfo) throws JSONException {

        RatingQueueBean ratingQueueBean = new RatingQueueBean();


        ParcelAuditDetailsDto firstCharge;
        boolean hwt = false;
        if (hwtSequenceInfo == null) {
            if (shipmentDetails.get(0).getPickupDate() == null)
                setPrevParentIdShipDate(shipmentDetails, trackingNumDetails);

            firstCharge = shipmentDetails.get(0);
            hwtSequenceInfo = new HashMap<>();
            hwtSequenceInfo.put(firstCharge.getTrackingNumber(), firstCharge.getParentId());
        } else {
            firstCharge = getMinParentCharge(shipmentDetails, hwtSequenceInfo);
            hwtSequenceInfo.remove("minParentId");
            hwt = true;
        }

        if (firstCharge != null) {

            List<ParcelAuditDetailsDto> leadShipmentDetails = ParcelRatingUtil.getLeadShipmentDetails(trackingNumDetails);

            prepareXmlReqAddressInfo(trackingNumDetails, firstCharge);

            List<ParcelAuditDetailsDto> ratingShipmentDetails = getParentIdCharges(trackingNumDetails, shipmentDetails.get(0).getParentId());

            ratingQueueBean.setReturnFlag(isReturnShipment(ratingShipmentDetails) == true ? "Y" : "N");

            ratingQueueBean.setManiestId(firstCharge.getId());

            if (leadShipmentDetails != null && leadShipmentDetails.size() > 0)
                ratingQueueBean.setTrackingNumber(leadShipmentDetails.get(0).getTrackingNumber());
            else
                ratingQueueBean.setTrackingNumber(firstCharge.getTrackingNumber());

            ratingQueueBean.setParentId(firstCharge.getParentId());
            ratingQueueBean.setCarrierId(firstCharge.getCarrierId());
            ratingQueueBean.setSenderBilledZipCode(firstCharge.getSenderBilledZipCode());
            ratingQueueBean.setReceiverBilledZipCode(firstCharge.getReceiverBilledZipCode());
            ratingQueueBean.setPrpFlag("N");

            String billOption = (null == firstCharge.getBillOption() ? "" : firstCharge.getBillOption());
            if (billOption.equalsIgnoreCase("Prepaid") || billOption.equals("1") || billOption.equalsIgnoreCase("Outbound")) {
                billOption = "PP";
            } else if (billOption.equalsIgnoreCase("Collect") || billOption.equals("2")) {
                billOption = "FC";
            } else if (billOption.equalsIgnoreCase("Third Party") || billOption.equals("3")) {
                billOption = "TP";
            }
            ratingQueueBean.setBillOption(billOption);
            if (ratingQueueBean.getZone() == null && firstCharge.getZone() != null) {
                ratingQueueBean.setZone(firstCharge.getZone());
            }

            //StringJoiner accessorials = new StringJoiner(",");
            JSONArray accJsonArr = new JSONArray();
            String resiFlag = "N";

            for (ParcelAuditDetailsDto auditDetails : shipmentDetails) {
                if (auditDetails != null) {
                    if (auditDetails.getChargeDescription() != null && (auditDetails.getChargeDescription().toUpperCase().endsWith("-PRP")
                            || "RETURN EMAIL LABEL".equalsIgnoreCase(auditDetails.getChargeDescription().toUpperCase())))
                        ratingQueueBean.setPrpFlag("Y");

                    if (auditDetails.getChargeClassificationCode() != null && ParcelAuditConstant.ChargeClassificationCode.ACS.name().equalsIgnoreCase(auditDetails.getChargeClassificationCode())
                            && !Arrays.asList(ParcelAuditConstant.ChargeDescriptionCode.FSC.name(), ParcelAuditConstant.ChargeDescriptionCode.DSC.name()).contains(auditDetails.getChargeDescriptionCode())) {
                        try {

                            if ("RES".equalsIgnoreCase(auditDetails.getChargeDescriptionCode()))
                                resiFlag = "Y";

                            JSONObject accJson = new JSONObject();
                            accJson.put("seq", hwtSequenceInfo.get(auditDetails.getTrackingNumber()));
                            accJson.put("netAmount", auditDetails.getNetAmount() != null ? auditDetails.getNetAmount().toString() : "0.00");
                            accJson.put("weight", auditDetails.getPackageWeight() != null ? auditDetails.getPackageWeight().toString() : "0.00");
                            if (auditDetails.getWeightUnit() != null && "O".equalsIgnoreCase(auditDetails.getWeightUnit()))
                                auditDetails.setWeightUnit("OUNCE");
                            accJson.put("weightUnit", (null == auditDetails.getWeightUnit() || auditDetails.getWeightUnit().isEmpty() || "L".equalsIgnoreCase(auditDetails.getWeightUnit()) ? "LBS" : auditDetails.getWeightUnit()));
                            accJson.put("quantity", (null == auditDetails.getItemQuantity() || auditDetails.getItemQuantity().isEmpty() ? 1l : Long.parseLong(auditDetails.getItemQuantity())));
                            accJson.put("quantityUnit", (null == auditDetails.getQuantityUnit() || auditDetails.getQuantityUnit().isEmpty() ? "PCS" : auditDetails.getQuantityUnit()));

                            ServiceFlagAccessorialBean bean = getAccessorialBean(accessorialBeans, auditDetails.getChargeDescription(), auditDetails.getActualchargeDescriptionCode(), 22L);

                            if (bean != null) {
                                accJson.put("code", bean.getLookUpValue());
                            } else {
                                DirectJDBCDAO.getInstance().insertUnknownMappingCode(auditDetails);
                                m_log.warn("Service flag accessorial code is not found in look up table: Charge Desription Code->" + auditDetails.getChargeDescriptionCode() + "--Charge Desription->" + auditDetails.getChargeDescription() + "--Parent Id->" + auditDetails.getParentId());
                                accJson.put("code", auditDetails.getChargeDescriptionCode());
                            }

                            if (!checkAccExist(accJsonArr, accJson))
                                accJsonArr.put(accJson);

                        } catch (Exception e) {
                            m_log.error("ERROR - " + e.getStackTrace() + "--Parent Id->" + ratingQueueBean.getParentId());
                            e.printStackTrace();
                        }
                    }
                }
            }
            ratingQueueBean.setAccessorialInfo(accJsonArr.length() > 0 ? accJsonArr.toString() : null);

            String rtrScacCode = (null == firstCharge.getRtrScacCode() ? "" : "FDEG".equals(firstCharge.getRtrScacCode()) ? "FDE" : firstCharge.getRtrScacCode());
            String currency = (null == firstCharge.getCurrency() || firstCharge.getCurrency().isEmpty() ? "USD" : firstCharge.getCurrency());
            ratingQueueBean.setScacCode(rtrScacCode);
            ratingQueueBean.setCurrencyCode(currency);

            String serviceLevel = findServiceLevel(shipmentDetails);
            if (serviceLevel == null || serviceLevel.trim().isEmpty()) {
                // System.out.println("Invalid Service Level for " + firstCharge.getTrackingNumber());

                m_log.warn("Invalid OR Empty Service Level for " + firstCharge.getTrackingNumber() + "--Parent Id->" + firstCharge.getParentId() + "");
                return null;
            }

            ratingQueueBean.setService(serviceLevel);
            ratingQueueBean.setCustomerCode(firstCharge.getCustomerCode());
            ratingQueueBean.setRevenueTier(firstCharge.getRevenueTier());
            ratingQueueBean.setShipperNumber(firstCharge.getShipperNumber());
            ratingQueueBean.setResiFlag(resiFlag);
            JSONArray itemJsonArr = new JSONArray();

            for (Map.Entry<String, Long> entry : hwtSequenceInfo.entrySet()) {


                ParcelAuditDetailsDto firstBaseCharge = ParcelRatingUtil.getLatestFrightCharge(shipmentDetails, entry.getKey());

                //ParcelAuditDetailsDto firstBaseCharge = ParcelRatingUtil.getFirstFrightChargeForNonUpsCarrier(shipmentDetails);
                if (firstBaseCharge != null) {
                    if (firstBaseCharge.getChargeClassificationCode() != null
                            && ParcelAuditConstant.ChargeClassificationCode.FRT.name().equalsIgnoreCase(firstBaseCharge.getChargeClassificationCode())) {
                        Float weight = (null == firstBaseCharge.getPackageWeight() || firstBaseCharge.getPackageWeight().isEmpty() ? 0.0f : Float.parseFloat(firstBaseCharge.getPackageWeight()));
                        if (firstBaseCharge.getWeightUnit() != null && "O".equalsIgnoreCase(firstBaseCharge.getWeightUnit()))
                            firstBaseCharge.setWeightUnit("OUNCE");
                        String weightUnit = (null == firstBaseCharge.getWeightUnit() || firstBaseCharge.getWeightUnit().isEmpty() || "L".equalsIgnoreCase(firstBaseCharge.getWeightUnit()) ? "LBS" : firstBaseCharge.getWeightUnit());
                        Long quantity = (null == firstBaseCharge.getItemQuantity() || firstBaseCharge.getItemQuantity().isEmpty() ? 1l : Long.parseLong(firstBaseCharge.getItemQuantity()));
                        String quantityUnit = (null == firstBaseCharge.getQuantityUnit() || firstBaseCharge.getQuantityUnit().isEmpty() ? "PCS" : firstBaseCharge.getQuantityUnit());
                        Float dimLenght = (null == firstBaseCharge.getDimLength() || firstBaseCharge.getDimLength().isEmpty() ? 0.0f : Float.parseFloat(firstBaseCharge.getDimLength()));
                        Float dimWidth = (null == firstBaseCharge.getDimWidth() || firstBaseCharge.getDimWidth().isEmpty() ? 0.0f : Float.parseFloat(firstBaseCharge.getDimWidth()));
                        Float dimHeight = (null == firstBaseCharge.getDimHeight() || firstBaseCharge.getDimHeight().isEmpty() ? 0.0f : Float.parseFloat(firstBaseCharge.getDimHeight()));
                        String dimUnit = (null == firstBaseCharge.getUnitOfDim() || firstBaseCharge.getUnitOfDim().isEmpty() ? "" : firstBaseCharge.getUnitOfDim().equalsIgnoreCase("I") ? "in" : firstBaseCharge.getUnitOfDim());
                        BigDecimal actualWeight = (null == firstBaseCharge.getActualWeight() ? new BigDecimal("0") : firstBaseCharge.getActualWeight());
                        String actualWeightUnit = (null == firstBaseCharge.getActualWeightUnit() || firstBaseCharge.getActualWeightUnit().isEmpty() || "L".equalsIgnoreCase(firstBaseCharge.getActualWeightUnit()) ? "LBS" : firstBaseCharge.getActualWeightUnit());

                        JSONObject accJson = new JSONObject();
                        accJson.put("seq", firstBaseCharge.getParentId());
                        accJson.put("weight", String.valueOf(weight));
                        accJson.put("weightUnit", weightUnit);
                        accJson.put("quantity", String.valueOf(quantity));
                        accJson.put("quantityUnit", quantityUnit);
                        accJson.put("dimLenght", String.valueOf(dimLenght));
                        accJson.put("dimWidth", String.valueOf(dimWidth));
                        accJson.put("dimHeight", String.valueOf(dimHeight));
                        accJson.put("dimUnit", dimUnit);
                        accJson.put("actualWeight", String.valueOf(actualWeight));
                        accJson.put("actualWeightUnit", actualWeightUnit);
                        accJson.put("packageType", firstCharge.getPackageType());
                        itemJsonArr.put(accJson);

                    }
                }
            }
            ratingQueueBean.setItemTagInfo(itemJsonArr != null ? itemJsonArr.toString() : null);
            ratingQueueBean.setShipDate(firstCharge.getPickupDate());

            if ((null == firstCharge.getSenderCountry() || firstCharge.getSenderCountry().isEmpty())
                    && (null == firstCharge.getSenderState() || firstCharge.getSenderState().isEmpty())
                    && (null == firstCharge.getSenderCity() || firstCharge.getSenderCity().isEmpty())
                    && (null == firstCharge.getSenderZipCode() || firstCharge.getSenderZipCode().isEmpty())) {

                firstCharge.setSenderCity(firstCharge.getShipperCity());
                firstCharge.setSenderState(firstCharge.getShipperState());
                firstCharge.setSenderCountry(firstCharge.getShipperCountry());
                firstCharge.setSenderZipCode(firstCharge.getShipperZip());

            }

            String senderCountry = (null == firstCharge.getSenderCountry() || firstCharge.getSenderCountry().isEmpty() ? "US" : firstCharge.getSenderCountry());
            String senderState = (null == firstCharge.getSenderState() ? "" : firstCharge.getSenderState());
            String senderCity = (null == firstCharge.getSenderCity() ? "" : firstCharge.getSenderCity());
            String senderZipCode = (null == firstCharge.getSenderZipCode() ? "" : firstCharge.getSenderZipCode());

            ratingQueueBean.setShipperCountry(senderCountry);
            ratingQueueBean.setShipperState(senderState);
            ratingQueueBean.setShipperCity(senderCity);
            ratingQueueBean.setShipperZip(senderZipCode);

            if (firstCharge.getDeliveryDate() != null) {
                ratingQueueBean.setDeliveryDate(firstCharge.getDeliveryDate());
            } else {
                ratingQueueBean.setDeliveryDate(firstCharge.getPickupDate());
            }
            String receiverCountry = (null == firstCharge.getReceiverCountry() || firstCharge.getReceiverCountry().isEmpty() ? "US" : firstCharge.getReceiverCountry());
            String receiverState = (null == firstCharge.getReceiverState() ? "" : firstCharge.getReceiverState());
            String receiverCity = (null == firstCharge.getReceiverCity() ? "" : firstCharge.getReceiverCity());
            String receiverZipCode = (null == firstCharge.getReceiverZipCode() ? "" : firstCharge.getReceiverZipCode());

            ratingQueueBean.setReceiverCountry(receiverCountry);
            ratingQueueBean.setReceiverState(receiverState);
            ratingQueueBean.setReceiverCity(receiverCity);
            ratingQueueBean.setReceiverZip(receiverZipCode);

            ratingQueueBean.setRateSetName(rateSet);

            if (firstCharge.getActualServiceBucket() != null)
                ratingQueueBean.setActualServiceBucket(Long.valueOf(firstCharge.getActualServiceBucket()));

            ratingQueueBean.setInvoiceDate(firstCharge.getInvoiceDate());
            ratingQueueBean.setCustomerId(firstCharge.getCustomerId());
            ratingQueueBean.setPackageType(firstCharge.getPackageType());

            if (hwt)
                ratingQueueBean.setHwtIdentifier(firstCharge.getMultiWeightNumber());

        }
        return ratingQueueBean;
    }

    public static boolean isShipmentRated(List<ParcelAuditDetailsDto> shipment) {
        boolean rated = false;
        List<String> rateStatusList = null;
        if (shipment != null && !shipment.isEmpty()) {
            rateStatusList = new ArrayList<>();
            for (ParcelAuditDetailsDto shipmentCharge : shipment) {
                if (shipmentCharge != null && shipmentCharge.getRtrStatus() != null && !shipmentCharge.getRtrStatus().isEmpty()
                        && shipmentCharge.getRatesParentId() != null) {
                    rateStatusList.add(shipmentCharge.getRtrStatus());
                }
            }
            if (rateStatusList != null && !rateStatusList.isEmpty()) {
                if (rateStatusList.contains(ParcelAuditConstant.RTRStatus.NO_PRICE_SHEET.value) || rateStatusList.contains(ParcelAuditConstant.RTRStatus.RATING_EXCEPTION.value)) {
                    rated = true;
                } else if (rateStatusList.contains(ParcelAuditConstant.RTRStatus.CLOSED.value) || rateStatusList.contains(ParcelAuditConstant.RTRStatus.UNDER_CHARGED.value)
                        || rateStatusList.contains(ParcelAuditConstant.RTRStatus.OVER_CHARGED.value)) {
                    rated = true;
                }
            }
        }
        return rated;
    }

    public static ParcelAuditRequestResponseLog prepareRequestResponseLog(String requestPayload, String response, Long entityId, long carrierId) {
        ParcelAuditRequestResponseLog requestResponseLog = new ParcelAuditRequestResponseLog();
        requestResponseLog.setEntityIds(entityId.toString());
        requestResponseLog.setCreateUser(ParcelAuditConstant.PARCEL_RTR_RATING_USER_NAME);
        requestResponseLog.setCarrierId(carrierId);

        if (requestPayload != null && !requestPayload.isEmpty()) {
            requestResponseLog.setRequestXml(requestPayload);

            if (response != null && !response.isEmpty()) {
                requestResponseLog.setResponseXml( response);
            }
        }

        return requestResponseLog;
    }

    public static BigDecimal findSumOfNetAmount(List<ParcelAuditDetailsDto> parcelAuditDetailsList) {
        BigDecimal sumOfNetAmount = new BigDecimal("0.0");
        if (parcelAuditDetailsList != null && !parcelAuditDetailsList.isEmpty()) {
            for (ParcelAuditDetailsDto parcelAuditDetails : parcelAuditDetailsList) {
                if (parcelAuditDetails != null && parcelAuditDetails.getNetAmount() != null && !parcelAuditDetails.getNetAmount().isEmpty()) {
                    try {
                        sumOfNetAmount = sumOfNetAmount.add(new BigDecimal(parcelAuditDetails.getNetAmount()));
                    } catch (Exception e) {
                        m_log.error("ERROR - " + e.getStackTrace() + "--Parent Id->" + parcelAuditDetailsList.get(0).getParentId());
                        e.printStackTrace();
                    }
                }
            }
        }
        return sumOfNetAmount;
    }

    public static boolean hasMultipleParentIds(Map<Long, List<ParcelAuditDetailsDto>> shipments) {
        return shipments != null && shipments.size() > 1;
    }

    public static boolean hasFrtCharge(List<ParcelAuditDetailsDto> shipment) {
        boolean frtFound = false;
        if (shipment != null && !shipment.isEmpty()) {
            for (ParcelAuditDetailsDto auditDetails : shipment) {
                if (auditDetails != null && "FRT".equalsIgnoreCase(auditDetails.getChargeClassificationCode())) {
                    frtFound = true;
                    break;
                }
            }
        }
        return frtFound;
    }

    public static ParcelAuditDetailsDto getPreviousShipmentBaseChargeDetails(Map<Long, List<ParcelAuditDetailsDto>> allSortedShipments, Long parentId) {
        List<ParcelAuditDetailsDto> previousShipment = null;
        if (allSortedShipments != null && !allSortedShipments.isEmpty()) {
            for (Map.Entry<Long, List<ParcelAuditDetailsDto>> shipmentEntry : allSortedShipments.entrySet()) {
                if (shipmentEntry != null) {
                    if (parentId.equals(shipmentEntry.getKey()) && previousShipment != null) {
                        for (ParcelAuditDetailsDto charge : previousShipment) {
                            if (charge != null && "FRT".equalsIgnoreCase(charge.getChargeClassificationCode())) {
                                return charge;
                            }
                        }
                    }
                }
                previousShipment = shipmentEntry.getValue();
            }
        }
        return null;
    }

    public static List<ParcelAuditDetailsDto> getPreviousShipmentDetails(Map<Long, List<ParcelAuditDetailsDto>> allSortedShipments, Long parentId) {
        List<ParcelAuditDetailsDto> previousShipment = null;
        if (allSortedShipments != null && !allSortedShipments.isEmpty()) {
            for (Map.Entry<Long, List<ParcelAuditDetailsDto>> shipmentEntry : allSortedShipments.entrySet()) {
                if (shipmentEntry != null) {
                    if (parentId.equals(shipmentEntry.getKey()) && previousShipment != null) {
                        return previousShipment;
                    }
                }
                previousShipment = shipmentEntry.getValue();
            }
        }
        return null;
    }

    public static boolean isRatingDone(String ratingStatus) {
        return ratingStatus != null
                && Arrays.asList(ParcelAuditConstant.RTRStatus.CLOSED.value, ParcelAuditConstant.RTRStatus.UNDER_CHARGED.value, ParcelAuditConstant.RTRStatus.OVER_CHARGED.value).contains(ratingStatus);
    }

    public static boolean isFirstShipmentToRate(Map<Long, List<ParcelAuditDetailsDto>> allSortedShipments, Long parentId) {
        boolean isFirstShipment = false;
        if (allSortedShipments != null && !allSortedShipments.isEmpty()) {
            Long firstShipmentParentId = allSortedShipments.keySet().iterator().next();
            if (firstShipmentParentId != null && parentId != null && firstShipmentParentId.equals(parentId)) {
                isFirstShipment = true;
            }
        }
        return isFirstShipment;
    }

    public static boolean containsFuelSurcharge(List<ParcelAuditDetailsDto> shipment) {
        if (shipment != null) {
            for (ParcelAuditDetailsDto charge : shipment) {
                if (charge != null) {
                    if ("FSC".equalsIgnoreCase(charge.getChargeClassificationCode())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean containsFRTCharge(List<ParcelAuditDetailsDto> shipment) {
        if (shipment != null) {
            for (ParcelAuditDetailsDto charge : shipment) {
                if (charge != null) {
                    if ("FRT".equalsIgnoreCase(charge.getChargeClassificationCode())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static ParcelAuditDetailsDto findFrtCharge(List<ParcelAuditDetailsDto> shipment) {
        if (shipment != null) {
            for (ParcelAuditDetailsDto charge : shipment) {
                if (charge != null) {
                    if ("FRT".equalsIgnoreCase(charge.getChargeClassificationCode())) {
                        return charge;
                    }
                }
            }
        }
        return null;
    }

    /**
     * This method will prepare bundle number or multi weight id tracking number wise details
     *
     * @param listMap
     * @return
     */
    public static Map<String, List<ParcelAuditDetailsDto>> prepareMultiWeightNumberWiseAuditDetails(Map<String, List<ParcelAuditDetailsDto>> listMap) {

        Map<String, List<ParcelAuditDetailsDto>> mwtDetailsMap = new HashMap<>();
        Map<String, List<ParcelAuditDetailsDto>> tempMap = new HashMap<>(listMap);
        for (Map.Entry<String, List<ParcelAuditDetailsDto>> entry : tempMap.entrySet()) {


            for (ParcelAuditDetailsDto parcelAuditDetails : entry.getValue()) {
                if (parcelAuditDetails != null) {

                    if (parcelAuditDetails.getMultiWeightNumber() != null && !parcelAuditDetails.getMultiWeightNumber().isEmpty()) {
                        if (mwtDetailsMap.containsKey(parcelAuditDetails.getMultiWeightNumber()))
                            mwtDetailsMap.get(parcelAuditDetails.getMultiWeightNumber()).addAll(tempMap.get(parcelAuditDetails.getTrackingNumber()));
                        else
                            mwtDetailsMap.put(parcelAuditDetails.getMultiWeightNumber(), tempMap.get(parcelAuditDetails.getTrackingNumber()));

                        listMap.remove(parcelAuditDetails.getTrackingNumber());
                        break;
                    }
                }
            }
        }
        tempMap = null;
        return mwtDetailsMap;
    }

    /**
     * This method will prepare Lead Shipment tracking number wise details
     *
     * @param listMap
     * @return
     */
    public static Map<String, List<ParcelAuditDetailsDto>> prepareHwtNumberWiseAuditDetails(Map<String, List<ParcelAuditDetailsDto>> listMap) {

        Map<String, List<ParcelAuditDetailsDto>> hwtDetailsMap = new HashMap<>();
        for (Map.Entry<String, List<ParcelAuditDetailsDto>> entry : listMap.entrySet()) {

            for (ParcelAuditDetailsDto parcelAuditDetails : entry.getValue()) {
                if (parcelAuditDetails != null) {

                    if (parcelAuditDetails.getMultiWeightNumber() != null && !parcelAuditDetails.getMultiWeightNumber().isEmpty()) {
                        if (hwtDetailsMap.containsKey(parcelAuditDetails.getMultiWeightNumber()))
                            hwtDetailsMap.get(parcelAuditDetails.getMultiWeightNumber()).addAll(listMap.get(parcelAuditDetails.getTrackingNumber()));
                        else
                            hwtDetailsMap.put(parcelAuditDetails.getMultiWeightNumber(), listMap.get(parcelAuditDetails.getTrackingNumber()));


                        break;
                    }
                }
            }
        }
        Map<String, List<ParcelAuditDetailsDto>> hwtDetailsMapTemp = new HashMap<>(hwtDetailsMap);
        Set<String> trackingSet;
        for (Map.Entry<String, List<ParcelAuditDetailsDto>> entry : hwtDetailsMapTemp.entrySet()) {

            trackingSet = new HashSet<>();
            for (ParcelAuditDetailsDto parcelAuditDetails : entry.getValue()) {
                if (parcelAuditDetails != null) {
                    trackingSet.add(parcelAuditDetails.getTrackingNumber());
                }

            }
            if (trackingSet.size() == 1 && trackingSet.contains(entry.getKey())) {
                hwtDetailsMap.remove(entry.getKey());
            } else {
                listMap.remove(trackingSet);
            }
        }


        return hwtDetailsMap;
    }

    /**
     * This method will return lead shipment details
     *
     * @param parcelAuditDetails
     * @return
     */
    public static List<ParcelAuditDetailsDto> getLeadShipmentDetails(List<ParcelAuditDetailsDto> parcelAuditDetails) {

        List<ParcelAuditDetailsDto> detailsDtos = new ArrayList<>();

        ParcelAuditDetailsDto minDto = parcelAuditDetails.stream().min(Comparator.comparing(ParcelAuditDetailsDto::getId)).orElseThrow(NoSuchElementException::new);

        if (minDto != null) {
            for (ParcelAuditDetailsDto dto : parcelAuditDetails) {

                if (StringUtils.equalsIgnoreCase(dto.getTrackingNumber(), minDto.getTrackingNumber())) {

                    detailsDtos.add(dto);
                }
            }
        }
        return detailsDtos;
    }

    /**
     * This method will prepare bundle number or multi weight id tracking number wise details
     *
     * @return
     */
    public static Map<String, List<RatingQueueBean>> prepareHwtShipmentWiseInfo(ArrayList<RatingQueueBean> beanList) {

        Collections.sort(beanList, new Comparator<RatingQueueBean>() {
            @Override
            public int compare(RatingQueueBean h1, RatingQueueBean h2) {
                return h1.getHwtIdentifier().compareTo(h2.getHwtIdentifier());
            }
        });
        Map<String, List<RatingQueueBean>> mwtDetailsMap = new HashMap<>();


        for (RatingQueueBean queueBean : beanList) {


            if (queueBean.getHwtIdentifier() != null && !queueBean.getHwtIdentifier().isEmpty()) {


                if (mwtDetailsMap.containsKey(queueBean.getHwtIdentifier())) {
                    mwtDetailsMap.get(queueBean.getHwtIdentifier()).add(queueBean);
                } else {
                    List<RatingQueueBean> dtoList = new ArrayList<RatingQueueBean>();
                    dtoList.add(queueBean);
                    mwtDetailsMap.put(queueBean.getHwtIdentifier(), dtoList);


                }


            }


        }


        return mwtDetailsMap;

    }

    /**
     * @param queueBeans
     * @return
     */
    public static String prepareTrackingNumbersInOperator(List<RatingQueueBean> queueBeans) {

        StringBuilder builder = null;
        if (queueBeans.size() == 1) {
            builder = new StringBuilder();
            builder.append(queueBeans.get(0).getTrackingNumber());
        } else {
            for (RatingQueueBean bean : queueBeans) {

                if (builder == null) {
                    builder = new StringBuilder();
                } else {
                    builder.append(",");
                }
                builder.append("'" + bean.getTrackingNumber() + "'");
            }
        }
        return builder.toString();
    }

    /**
     * @param queueBeans
     * @return
     */
    public static String prepareQueueIdsInOperator(List<RatingQueueBean> queueBeans) {

        StringBuilder builder = null;

        for (RatingQueueBean bean : queueBeans) {

            if (builder == null) {
                builder = new StringBuilder();
            } else {
                builder.append(",");
            }
            builder.append(bean.getRatingQueueId());
        }
        return builder.toString();
    }


    /**
     * @param shipmentToRate
     * @param queueBeans
     * @return
     */
    public static RatingQueueBean getLeadShipmentQueueBean(List<ParcelAuditDetailsDto> shipmentToRate, List<RatingQueueBean> queueBeans) {

        for (RatingQueueBean bean : queueBeans) {

            if (shipmentToRate.get(0).getCarrierId() == 21) {
                if (shipmentToRate.get(0).getParentId().equals(bean.getGffId())) {

                    return bean;
                }
            } else if (shipmentToRate.get(0).getCarrierId() == 22) {
                if (shipmentToRate.get(0).getParentId().equals(bean.getManiestId())) {

                    return bean;
                }
            }
        }
        return null;
    }

    public static boolean isRatedWithException(List<ParcelAuditDetailsDto> shipment) {
        boolean isRatedWithException = false;
        if (shipment != null && !shipment.isEmpty()) {
            for (ParcelAuditDetailsDto shipmentCharge : shipment) {
                if (shipmentCharge != null && shipmentCharge.getRtrStatus() != null
                        && ParcelAuditConstant.RTRStatus.RATING_EXCEPTION.value.equalsIgnoreCase(shipmentCharge.getRtrStatus())) {
                    isRatedWithException = true;
                    break;
                }
            }
        }
        return isRatedWithException;
    }

    public static boolean isRatedWithEmptyPriceSheet(List<ParcelAuditDetailsDto> shipment) {
        boolean isRatedWithEmptyPriceSheet = false;
        if (shipment != null && !shipment.isEmpty()) {
            for (ParcelAuditDetailsDto shipmentCharge : shipment) {
                if (shipmentCharge != null && shipmentCharge.getRtrStatus() != null
                        && ParcelAuditConstant.RTRStatus.NO_PRICE_SHEET.value.equalsIgnoreCase(shipmentCharge.getRtrStatus())) {
                    isRatedWithEmptyPriceSheet = true;
                    break;
                }
            }
        }
        return isRatedWithEmptyPriceSheet;
    }

    public static boolean isVoidShipment(List<ParcelAuditDetailsDto> shipment) {
        boolean isVoidShipment = false;
        if (shipment != null && !shipment.isEmpty()) {
            for (ParcelAuditDetailsDto shp : shipment) {
                if (shp != null && ((shp.getChargeCategoryDetailCode() != null &&
                        "VOID".equalsIgnoreCase(shp.getChargeCategoryDetailCode().trim()))
                        || (shp.getChargeDescription() != null && "VOID".startsWith(shp.getChargeDescription().toUpperCase())))) {
                    isVoidShipment = true;
                    break;
                }
            }
        }
        return isVoidShipment;
    }

    /**
     * This method will sum the weight fields and net charges based on accessorial types
     *
     * @param shipmentToRate
     * @return
     */
    public static List<ParcelAuditDetailsDto> updateWeightAndNetChargesForHwt(List<ParcelAuditDetailsDto> shipmentToRate) {

        BigDecimal actualWeight = new BigDecimal(0);
        BigDecimal billedWeight = new BigDecimal(0);
        ;
        int noOfPieces = 0;
        Map<String, BigDecimal> accessorialNetCharges = new HashMap<>();
        for (ParcelAuditDetailsDto dto : shipmentToRate) {

            if (dto.getId().equals(dto.getParentId())) {

                if (dto.getActualWeight() != null)
                    actualWeight = actualWeight.add(dto.getActualWeight());
                if (dto.getPackageWeight() != null)
                    billedWeight = billedWeight.add(new BigDecimal(dto.getPackageWeight()));

                noOfPieces++;
            }
            String[] dwArr;
            dwArr = dto.getDwFieldInformation().split(",");

            BigDecimal netAmt = new BigDecimal(dto.getNetAmount());

            if (dwArr != null && dwArr.length > 2) {

                if ("FRT".equalsIgnoreCase(dwArr[1].trim())) {

                    if (accessorialNetCharges.containsKey("FRT")) {

                        accessorialNetCharges.put("FRT", accessorialNetCharges.get("FRT").add(netAmt));
                    } else {
                        accessorialNetCharges.put("FRT", netAmt);
                    }
                } else {
                    if (accessorialNetCharges.containsKey(dwArr[2].trim())) {

                        accessorialNetCharges.put(dwArr[2].trim(), accessorialNetCharges.get(dwArr[2].trim()).add(netAmt));
                    } else {
                        accessorialNetCharges.put(dwArr[2].trim(), netAmt);
                    }
                }
            }
        }

        List<ParcelAuditDetailsDto> detailsDtos = getLeadShipmentDetails(shipmentToRate);

        Set<String> leadShipmentAccess = new HashSet<>();
        for (ParcelAuditDetailsDto dto : detailsDtos) {

            dto.setActualWeight(actualWeight);
            dto.setPackageWeight(String.valueOf(billedWeight));
            dto.setPieces(noOfPieces);

            String[] dwArr;
            dwArr = dto.getDwFieldInformation().split(",");

            if (dwArr != null && dwArr.length > 2) {

                if ("FRT".equalsIgnoreCase(dwArr[1].trim())) {
                    leadShipmentAccess.add(dwArr[1].trim());
                    dto.setNetAmount("" + accessorialNetCharges.get("FRT") + "");
                } else {
                    leadShipmentAccess.add(dwArr[2].trim());
                    if (accessorialNetCharges.containsKey(dwArr[2].trim())) {
                        dto.setNetAmount("" + accessorialNetCharges.get(dwArr[2].trim()) + "");
                    }
                }
            }

        }

        if (accessorialNetCharges.size() != detailsDtos.size()) {


            for (Map.Entry<String, BigDecimal> entry : accessorialNetCharges.entrySet()) {

                if (!leadShipmentAccess.contains(entry.getKey())) {
                    ParcelAuditDetailsDto dto = findByAcessroial(shipmentToRate, entry.getKey());

                    dto.setNetAmount("" + entry.getValue() + "");

                    detailsDtos.add(dto);
                }

            }


        }
        return detailsDtos;
    }

    /**
     * this method will return ParcelAuditDetailsDto object based accessorial type
     *
     * @param shipmentToRate
     * @param key
     * @return ParcelAuditDetailsDto
     */
    private static ParcelAuditDetailsDto findByAcessroial(List<ParcelAuditDetailsDto> shipmentToRate, String key) {

        ParcelAuditDetailsDto resultDto = null;

        for (ParcelAuditDetailsDto dto : shipmentToRate) {

            String[] dwArr;
            dwArr = dto.getDwFieldInformation().split(",");

            if (dwArr != null && dwArr.length > 2) {

                if (dwArr[1].trim().equalsIgnoreCase("FRT") && key.equalsIgnoreCase(dwArr[1].trim())) {

                    resultDto = dto;
                    break;

                } else if (dwArr[2].trim().equalsIgnoreCase(key)) {
                    {
                        resultDto = dto;
                        break;
                    }
                }

            }


        }
        return resultDto;
    }


    /**
     * @param beans
     * @param chargeDesc
     * @param chargeCode
     * @return
     */
    public static ServiceFlagAccessorialBean getAccessorialBean(List<ServiceFlagAccessorialBean> beans, String chargeDesc, String chargeCode, Long carrierId) {

        for (ServiceFlagAccessorialBean bean : beans) {

            if ((bean.getCustomDefined1() != null && bean.getLookUpCode() != null) && (chargeCode != null && chargeDesc != null)) {
                if (bean.getCustomDefined2().equals(carrierId.toString()) && bean.getCustomDefined1().trim().equalsIgnoreCase(chargeDesc.trim()) && bean.getLookUpCode().trim().equalsIgnoreCase(chargeCode.trim())) {
                    return bean;
                }
            }
        }
        return null;
    }

    public static String translateUpsZone(String zone) {
        // Split combined zones, just pick last one
        if (zone.contains("/"))
            zone = zone.substring(zone.lastIndexOf("/") + 1);

        if (zone.length() > 6) {
            // Error condition
            return null;
        }

        if (!StringUtils.isNumeric(zone))
            return zone; // Leave all international zones unchanged

        // Remove leading zeros
        while (zone.startsWith("0"))
            zone = zone.substring(1);

        // If zone was only zeros, leave one zero and return (DHL uses zone 0)
        if (zone.length() == 0)
            return "0";

        if (zone.length() < 2)
            return zone; // No translation needed

        if (zone.length() == 2) {
            if (zone.startsWith("8")) // 81, 82, 84 => 71, 72, 74
                zone = "7" + zone.substring(1);
            if (zone.startsWith("6")) // 61, 62, 64 => 91, 92, 94
                zone = "9" + zone.substring(1);

            return zone;
        }

        String zonePre = zone.substring(0, 2); // first two chars
        String zoneSuff = zone.substring(1); // last two chars
        String zoneLast = zone.substring(2); // last char

        if (zone.equals("481") || zone.equals("491"))
            return "71";
        if (zone.equals("482") || zone.equals("492"))
            return "72";
        if (zone.equals("484") || zone.equals("494"))
            return "74";

        // 3 Day Select from Canada
        if (zone.equals("475") || zone.equals("476") || zone.equals("477"))
            return zoneSuff;

        // Standard from Canada
        if (zone.equals("376"))
            return "75";
        if (zone.equals("378"))
            return "76";
        if (zone.equals("380"))
            return "77";

        // Odd 501 zone used in UK - leave as 501
        if (zone.equals("501"))
            return "501";

        // Only *42 that should not be 22 (these are 2-Day AM)
        if (zone.equals("242"))
            return "2";
        if (zone.equals("243"))
            return "3";

        if (zoneSuff.equals("24")) // Metro Alaska / Hawaii
            return "44";
        if (zoneSuff.equals("25")) // Puerto Rico
            return "45";
        if (zoneSuff.equals("26")) // Remote Alaska / Hawaii
            return "46";

        // Standard from Mexico
        if (zonePre.equals("36"))
            return "6" + zoneLast; // 362 => 62 ...

        if (zoneSuff.equals("20") || zoneSuff.equals("70"))
            return "20"; // Date/Broward Florida to South America
        if (zoneSuff.equals("21") || zoneSuff.equals("71"))
            return "21"; // Date/Broward Florida to South America
        if (zoneSuff.equals("12") || zoneSuff.equals("42") || zoneSuff.equals("62") || zoneSuff.equals("92"))
            return "22"; // ?

        // New zones for 2013
        if (zoneSuff.equals("11") || zoneSuff.equals("41") || zoneSuff.equals("61") || zoneSuff.equals("91"))
            return "11";
        if (zoneSuff.equals("13") || zoneSuff.equals("43") || zoneSuff.equals("63") || zoneSuff.equals("93"))
            return "13";

        return zoneLast; // Just use last character, e.g. 102 -> 2
    }

    /**
     * @param shipmentDetails
     * @param pickUpDateShipmentDetails
     * @return
     */
    public static ParcelAuditDetailsDto getImmediateFrtInfo(List<ParcelAuditDetailsDto> shipmentDetails
            , List<ParcelAuditDetailsDto> pickUpDateShipmentDetails) {

        Map<Long, List<ParcelAuditDetailsDto>> listMap = organiseShipmentsByParentId(pickUpDateShipmentDetails);
        ParcelAuditDetailsDto frtCharged = null;
        Long frtParentId = null;
        if (listMap != null) {
            for (Map.Entry<Long, List<ParcelAuditDetailsDto>> entry : listMap.entrySet()) {

                if (shipmentDetails.get(0).getParentId().compareTo(entry.getKey()) > 0
                        && ((shipmentDetails.get(0).getPickupDate()!=null  && entry.getValue().get(0).getPickupDate()!=null) &&  shipmentDetails.get(0).getPickupDate().compareTo(entry.getValue().get(0).getPickupDate()) == 0)) {
                    frtParentId = entry.getKey();
                }

            }
            if (frtParentId != null) {
                List<ParcelAuditDetailsDto> detailsDtos = listMap.get(frtParentId);

                if (detailsDtos != null)
                    frtCharged = ParcelRatingUtil.findFrtCharge(detailsDtos);
            }
        }
        return frtCharged;
    }

    public static List<ParcelAuditDetailsDto> prepareChargeList(Long key, Map<Long, List<ParcelAuditDetailsDto>> shipments) {

        List<ParcelAuditDetailsDto> shipmentChargeList = new ArrayList<>(shipments.get(key));
        boolean returnShipment = isReturnShipment(shipmentChargeList);
        boolean frtExist = false;

        for (ParcelAuditDetailsDto dto : shipmentChargeList) {

            if ("FRT".equalsIgnoreCase(dto.getChargeClassificationCode())) {
                frtExist = true;
                break;
            }
        }


        ParcelAuditDetailsDto frtDto = null;
        for (Map.Entry<Long, List<ParcelAuditDetailsDto>> entry : shipments.entrySet()) {

            if (key.compareTo(entry.getKey()) > 0 && (shipmentChargeList.get(0).getCarrierId().compareTo(21L) != 0 || !checkCarrierCreditsToExcludeRating(entry.getValue()))) {

                if (isReturnShipment(entry.getValue(), returnShipment)) {
                    for (ParcelAuditDetailsDto dto : entry.getValue()) {

                        if ((dto.getCarrierId() == 21 || (shipments.get(key).get(0).getPickupDate() != null && dto.getPickupDate() != null
                                && shipments.get(key).get(0).getPickupDate().compareTo(dto.getPickupDate()) == 0))
                                && !"FRT".equalsIgnoreCase(dto.getChargeClassificationCode())) {
                            shipmentChargeList.add(dto);
                        }

                        if (!frtExist && "FRT".equalsIgnoreCase(dto.getChargeClassificationCode())) {

                            frtDto = dto;
                        }
                    }
                }
            }
        }

        if (!frtExist && frtDto != null) {

            shipmentChargeList.add(frtDto);
        }

        return shipmentChargeList;
    }

    private static boolean isReturnShipment(List<ParcelAuditDetailsDto> shipmentChargeList) {

        boolean returnValue = getReturnShipmentVal(shipmentChargeList);

        return returnValue;
    }

    private static boolean isReturnShipment(List<ParcelAuditDetailsDto> shipmentChargeList, boolean ratingParentIdReturnVal) {

        boolean returnValue = getReturnShipmentVal(shipmentChargeList);

        if (ratingParentIdReturnVal == returnValue) {
            returnValue = true;
        } else
            returnValue = false;

        return returnValue;
    }

    private static boolean getReturnShipmentVal(List<ParcelAuditDetailsDto> shipmentChargeList) {

        boolean returnValue = false;
        if (shipmentChargeList != null && shipmentChargeList.size() > 0) {
            for (ParcelAuditDetailsDto auditDetails : shipmentChargeList) {

                if (auditDetails.getCarrierId() == 21) {
                    if (auditDetails.getChargeCatagoryCode() != null) {
                        if (auditDetails.getChargeCatagoryCode().equalsIgnoreCase("RTN") || // Standard return
                                (auditDetails.getChargeCategoryDetailCode() != null && ((auditDetails.getChargeCatagoryCode().equalsIgnoreCase("MIS") && auditDetails.getChargeCategoryDetailCode().equalsIgnoreCase("RS")) || // PRL
                                        (auditDetails.getChargeDescriptionCode() != null && auditDetails.getChargeCatagoryCode().equalsIgnoreCase("MIS") && auditDetails.getChargeCategoryDetailCode().equalsIgnoreCase("IMP") && auditDetails.getChargeDescriptionCode().equalsIgnoreCase("ALP")) || // Import PRL
                                        (auditDetails.getChargeCatagoryCode().equalsIgnoreCase("ADJ") && auditDetails.getChargeCategoryDetailCode().equalsIgnoreCase("RTS")) ||
                                        (auditDetails.getChargeDescription() != null && auditDetails.getChargeCatagoryCode().equalsIgnoreCase("ADJ") && auditDetails.getChargeDescription().toUpperCase().contains("RETURN"))))) {

                            returnValue = true;
                        }
                    }
                } else {
                    if (auditDetails.getChargeDescription() != null && (auditDetails.getChargeDescription().toUpperCase().startsWith("RETURN")))
                        returnValue = true;
                }
            }
        }

        return returnValue;
    }

    public static BigDecimal findPrevRateAmtByCode(List<AccessorialDto> prevParentsRatesDtos, String accCode, String accessorialType) {

        BigDecimal rtrAmount = new BigDecimal("0.00");

        for (AccessorialDto dto : prevParentsRatesDtos) {

            if ((accessorialType.equalsIgnoreCase(dto.getType()) || dto.getEbillGffId() != null) && (!"UNKNOWN".equalsIgnoreCase(dto.getCode()) && accCode.equalsIgnoreCase(dto.getCode()))) {

                rtrAmount = rtrAmount.add(dto.getRtrAmount());
            }

        }

        return rtrAmount;
    }

    public static BigDecimal findPrevRateAmtByDisName(List<AccessorialDto> prevParentsRatesDtos, String disCountName, String accessorialType) {

        BigDecimal rtrAmount = new BigDecimal("0.00");

        for (AccessorialDto dto : prevParentsRatesDtos) {

            if (accessorialType.equalsIgnoreCase(dto.getType()) && disCountName.equalsIgnoreCase(dto.getName())) {

                rtrAmount = rtrAmount.add(dto.getRtrAmount());
            }

        }

        return rtrAmount;
    }

    public static AccessorialDto findPrevRatesForSpecColumns(List<AccessorialDto> prevParentsRatesDtos) {

        AccessorialDto accessorialDto = new AccessorialDto();

        for (AccessorialDto dto : prevParentsRatesDtos) {

            if ((dto.getEbillGffId() != null && dto.getParentId() != null) && (dto.getEbillGffId().compareTo(dto.getParentId()) == 0)) {

                accessorialDto.setBaseDis(accessorialDto.getBaseDis().add(dto.getBaseDis()));
                accessorialDto.setResDis(accessorialDto.getResDis().add(dto.getResDis()));
                accessorialDto.setMinMaxDis(accessorialDto.getMinMaxDis().add(dto.getMinMaxDis()));
                accessorialDto.setDasDis(accessorialDto.getDasDis().add(dto.getDasDis()));
                accessorialDto.setCustFuleSurDis(accessorialDto.getCustFuleSurDis().add(dto.getCustFuleSurDis()));
                accessorialDto.setEarnedDis(accessorialDto.getEarnedDis().add(dto.getEarnedDis()));
                accessorialDto.setFuleSurDis(accessorialDto.getFuleSurDis().add(dto.getFuleSurDis()));
                accessorialDto.setRatedGrossFuel(accessorialDto.getRatedGrossFuel().add(dto.getRatedGrossFuel()));
            }

        }

        return accessorialDto;
    }

    public static Map<Date, List<ParcelAuditDetailsDto>> organiseShipmentsByBillDate(List<ParcelAuditDetailsDto> shipmentRecords) {

        Map<Date, List<ParcelAuditDetailsDto>> billDateShipments = new HashMap<>();

        for (ParcelAuditDetailsDto dto : shipmentRecords) {

            if (billDateShipments.containsKey(dto.getInvoiceDate())) {
                billDateShipments.get(dto.getInvoiceDate()).add(dto);
            } else {
                billDateShipments.put(dto.getInvoiceDate(), new ArrayList<>(Arrays.asList(dto)));
            }
        }
        return billDateShipments;
    }

    public static void addMissTrackingInfo(Map<String, List<ParcelAuditDetailsDto>> billDateTrackingWiseShipments, Map<String, List<ParcelAuditDetailsDto>> leadTrackingWiseShipments, Date invoiceDate, Date pickUpDate) {


        for (Map.Entry<String, List<ParcelAuditDetailsDto>> entry : leadTrackingWiseShipments.entrySet()) {

            if (entry.getValue().get(0).getCarrierId() == 21 || (pickUpDate != null && entry.getValue().get(0).getPickupDate() != null
                    && pickUpDate.compareTo(entry.getValue().get(0).getPickupDate()) == 0)) {
                if ((entry.getValue().get(0).getInvoiceDate() == null || invoiceDate.compareTo(entry.getValue().get(0).getInvoiceDate()) > 0)) {
                    if (!billDateTrackingWiseShipments.containsKey(entry.getKey())) {
                        billDateTrackingWiseShipments.put(entry.getKey(), entry.getValue());
                    } else {
                        billDateTrackingWiseShipments.get(entry.getKey()).addAll(entry.getValue());
                    }
                }
            }
        }
    }

    public static List<ParcelAuditDetailsDto> prepareHwtAccList(Map<String, List<ParcelAuditDetailsDto>> billDateTrackingWiseShipments, Map<String, Long> hwtSequenceInfo) {

        List<ParcelAuditDetailsDto> shipmentChargeList = null;
        for (Map.Entry<String, List<ParcelAuditDetailsDto>> entry : billDateTrackingWiseShipments.entrySet()) {

            Map<Long, List<ParcelAuditDetailsDto>> parentIdShipments = organiseShipmentsByParentId(entry.getValue());
            Long maxParentId = getMaxParentId(parentIdShipments);
            hwtSequenceInfo.put(entry.getKey(), maxParentId);
            if (shipmentChargeList == null)
                shipmentChargeList = ParcelRatingUtil.prepareChargeList(maxParentId, parentIdShipments);
            else {
                shipmentChargeList.addAll(ParcelRatingUtil.prepareChargeList(maxParentId, parentIdShipments));
            }


        }

        return shipmentChargeList;
    }

    private static Long getMaxParentId(Map<Long, List<ParcelAuditDetailsDto>> parentIdShipments) {

        Long maxParentId = null;
        for (Map.Entry<Long, List<ParcelAuditDetailsDto>> entry : parentIdShipments.entrySet()) {

            if (maxParentId == null)
                maxParentId = entry.getKey();
            else {
                if (entry.getKey().compareTo(maxParentId) > 0)
                    maxParentId = entry.getKey();
            }

        }
        return maxParentId;
    }

    public static boolean checkHwtShipment(List<ParcelAuditDetailsDto> shipmentRecords) {

        boolean flag = false;

        List<String> trackingNumberList = new ArrayList<>();

        for (ParcelAuditDetailsDto dto : shipmentRecords) {

            if (!trackingNumberList.contains(dto.getTrackingNumber()))
                trackingNumberList.add(dto.getTrackingNumber());
        }

        if (trackingNumberList.size() > 1)
            flag = true;

        return flag;
    }

    public static void getMinParentId(List<ParcelAuditDetailsDto> billDateShipments, Map<String, Long> hwtSequenceInfo) {

        Long minValue = billDateShipments.get(0).getParentId();
        for (ParcelAuditDetailsDto dto : billDateShipments) {

            if (dto.getParentId() < minValue) {
                minValue = dto.getParentId();
            }

        }

        hwtSequenceInfo.put("minParentId", minValue);
    }

    public static ParcelAuditDetailsDto getLatestFrightCharge(List<ParcelAuditDetailsDto> parcelAuditDetails, String trackingNumber) {
        ParcelAuditDetailsDto parcelAuditDetail = null;
        Long maxEntityId = 0l;
        if (parcelAuditDetails != null && !parcelAuditDetails.isEmpty()) {
            for (ParcelAuditDetailsDto auditDetail : parcelAuditDetails) {
                if (auditDetail != null && trackingNumber.equalsIgnoreCase(auditDetail.getTrackingNumber())) {
                    if ("FRT".equalsIgnoreCase(auditDetail.getChargeClassificationCode())) {
                        if (auditDetail.getId() > maxEntityId) {
                            parcelAuditDetail = auditDetail;
                        }
                    }
                }
            }
        }
        return parcelAuditDetail;
    }

    public static List<ParcelRateRequest.Item> getItemTags(List<ParcelRateRequest.Item> items, String itemTags) throws JSONException {


        if (itemTags != null && itemTags.length() > 0) {

            JSONArray accJsonArr = new JSONArray(itemTags);
            if (accJsonArr != null && accJsonArr.length() > 0) {
                for (int n = 0; n < accJsonArr.length(); n++) {
                    JSONObject accjson = accJsonArr.getJSONObject(n);
                    if (accjson != null) {
                        ParcelRateRequest.Item item = new ParcelRateRequest.Item();
                        if (accjson.has("seq")) {
                            item.setSequence(accjson.getLong("seq"));
                        }
                        if (accjson.has("weight") && accjson.getString("weight") != null) {

                            ParcelRateRequest.Weight weightObj = new ParcelRateRequest.Weight();
                            if (accjson.getString("weight") != null) {
                                weightObj.setWeight(new BigDecimal(accjson.getString("weight")));
                            }
                            if (accjson.has("weightUnit") && accjson.getString("weightUnit") != null)
                                weightObj.setUnits(accjson.getString("weightUnit"));

                            item.setWeight(weightObj);
                        }
                        if (accjson.has("actualWeight") && accjson.getString("actualWeight") != null) {

                            ParcelRateRequest.Weight actWeightObj = new ParcelRateRequest.Weight();
                            if (accjson.getString("actualWeight") != null) {
                                actWeightObj.setWeight(new BigDecimal(accjson.getString("actualWeight")));
                            }
                            if (accjson.has("actualWeightUnit") && accjson.getString("actualWeightUnit") != null)
                                actWeightObj.setUnits(accjson.getString("actualWeightUnit"));

                            item.setActualWeight(actWeightObj);
                        }

                        if (accjson.has("quantity") && accjson.getString("quantity") != null) {

                            ParcelRateRequest.Quantity quantityObj = new ParcelRateRequest.Quantity();

                            quantityObj.setQuantity(new BigDecimal(accjson.getString("quantity")));

                            if (accjson.has("quantityUnit") && accjson.getString("quantityUnit") != null)
                                quantityObj.setUnits(accjson.getString("quantityUnit"));

                            item.setQuantity(quantityObj);
                        }
                        ParcelRateRequest.Dimensions dimensionsObj = new ParcelRateRequest.Dimensions();
                        if (accjson.has("dimLenght") && accjson.getString("dimLenght") != null) {

                            dimensionsObj.setLength(new BigDecimal(accjson.getString("dimLenght")));
                        }
                        if (accjson.has("dimWidth") && accjson.getString("dimWidth") != null) {

                            dimensionsObj.setWidth(new BigDecimal(accjson.getString("dimWidth")));
                        }
                        if (accjson.has("dimHeight") && accjson.getString("dimHeight") != null) {

                            dimensionsObj.setHeight(new BigDecimal(accjson.getString("dimHeight")));
                        }
                        if (accjson.has("dimUnit") && accjson.getString("dimUnit") != null) {

                            dimensionsObj.setUnits(accjson.getString("dimUnit"));
                        }

                        item.setDimensions(dimensionsObj);

                        if (accjson.has("packageType") && accjson.getString("packageType") != null) {
                            item.setContainer(accjson.getString("packageType"));
                        }


                        items.add(item);
                    }


                }
            }

        }

        return items;
    }

    public static boolean isHwtShipmentRated(List<ParcelAuditDetailsDto> shipmentRecords, Long ratingParentId, List<ParcelAuditDetailsDto> shipmentToRate, Date invoiceDate) {

        boolean rated = false;
        List<ParcelAuditDetailsDto> detailsDtos = getParentIdCharges(shipmentRecords, ratingParentId);

        if (invoiceDate == null && (detailsDtos != null && detailsDtos.size() > 0))
            invoiceDate = detailsDtos.get(0).getInvoiceDate();

        rated = isShipmentRated(detailsDtos);

        if (!rated) {
            Map<Date, List<ParcelAuditDetailsDto>> shipments = organiseShipmentsByBillDate(shipmentRecords);

            List<Long> parentIds = new ArrayList<>();


            for (Map.Entry<Date, List<ParcelAuditDetailsDto>> entry : shipments.entrySet()) {

                parentIds.add(getMinParentId(entry.getValue()));

            }

            for (Long parentId : parentIds) {
                detailsDtos = null;
                if (ratingParentId.compareTo(parentId) > 0) {
                    detailsDtos = getParentIdCharges(shipmentRecords, parentId);
                    rated = isShipmentRated(detailsDtos);
                    if (!rated) {
                        rated = true;
                        break;
                    } else {
                        rated = false;
                    }
                }

            }

            List<ParcelAuditDetailsDto> auditDetailsDtoList = shipments.get(invoiceDate);

            if (auditDetailsDtoList != null) {
                Map<String, List<ParcelAuditDetailsDto>> trackingWiseShipments = ParcelRatingUtil.prepareTrackingNumberWiseAuditDetails(auditDetailsDtoList);
                List<ParcelAuditDetailsDto> hwtCarrierCharges = new ArrayList<>();
                for (Map.Entry<String, List<ParcelAuditDetailsDto>> entry : trackingWiseShipments.entrySet()) {

                    Map<Long, List<ParcelAuditDetailsDto>> parentIdShipments = organiseShipmentsByParentId(entry.getValue());
                    Long maxParentId = getMaxParentId(parentIdShipments);
                    hwtCarrierCharges.addAll(removeOneFrt(parentIdShipments.get(maxParentId)));
                }

                Map<String, ParcelAuditDetailsDto> sumOfHwtAccdetails = new LinkedHashMap<>();
                for (ParcelAuditDetailsDto dto : hwtCarrierCharges) {

                    if ("FRT".equalsIgnoreCase(dto.getChargeClassificationCode())) {
                        if (sumOfHwtAccdetails.containsKey("FRT")) {
                            ParcelAuditDetailsDto existingDto = sumOfHwtAccdetails.get("FRT");
                            sumTheTwoCharges(existingDto, dto);
                            existingDto.setId(ratingParentId);
                            sumOfHwtAccdetails.replace("FRT", existingDto);
                        } else {
                            dto.setId(ratingParentId);
                            sumOfHwtAccdetails.put("FRT", dto);
                        }
                    } else if ("FSC".equalsIgnoreCase(dto.getChargeClassificationCode())) {
                        if (sumOfHwtAccdetails.containsKey("FSC")) {
                            ParcelAuditDetailsDto existingDto = sumOfHwtAccdetails.get("FSC");
                            sumTheTwoCharges(existingDto, dto);
                            sumOfHwtAccdetails.replace("FSC", existingDto);
                        } else {
                            sumOfHwtAccdetails.put("FSC", dto);
                        }
                    } else {
                        if (sumOfHwtAccdetails.containsKey(dto.getChargeDescriptionCode())) {
                            ParcelAuditDetailsDto existingDto = sumOfHwtAccdetails.get(dto.getChargeDescriptionCode());
                            sumTheTwoCharges(existingDto, dto);
                            sumOfHwtAccdetails.replace(dto.getChargeDescriptionCode(), existingDto);
                        } else {

                            sumOfHwtAccdetails.put(dto.getChargeDescriptionCode(), dto);
                        }
                    }
                }

                List<ParcelAuditDetailsDto> leadShipmentDetails = ParcelRatingUtil.getLeadShipmentDetails(shipmentRecords);

                for (Map.Entry<String, ParcelAuditDetailsDto> entry : sumOfHwtAccdetails.entrySet()) {
                    ParcelAuditDetailsDto dto = entry.getValue();
                    if (dto != null) {
                        dto.setParentId(ratingParentId);
                        if (dto.getCarrierId() != null && dto.getCarrierId().compareTo(21L) == 0) {
                            dto.setTrackingNumber(dto.getMultiWeightNumber());
                        } else if (leadShipmentDetails != null && leadShipmentDetails.size() > 0) {
                            dto.setTrackingNumber(leadShipmentDetails.get(0).getTrackingNumber());
                        }
                        shipmentToRate.add(dto);
                    }
                }
            }


        }


        return rated;
    }

    private static List<ParcelAuditDetailsDto> removeOneFrt(List<ParcelAuditDetailsDto> parcelAuditDetailsDtos) {

        List<ParcelAuditDetailsDto> dtos = new ArrayList<>(parcelAuditDetailsDtos);
        ParcelAuditDetailsDto maxFrtdto =getLatestFrightCharge(parcelAuditDetailsDtos);
        for(ParcelAuditDetailsDto dto : parcelAuditDetailsDtos){
            if("FRT".equalsIgnoreCase(dto.getChargeClassificationCode()) && !(maxFrtdto.getId().compareTo(dto.getId()) == 0)){
                maxFrtdto.setNetAmount(String.valueOf(new BigDecimal(maxFrtdto.getNetAmount()).add(new BigDecimal(dto.getNetAmount()))));
                maxFrtdto.setIncentiveAmount(maxFrtdto.getIncentiveAmount().add(dto.getIncentiveAmount()));
                dtos.remove(dto);
            }
        }
        for(ParcelAuditDetailsDto dto : dtos){
            if("FRT".equalsIgnoreCase(dto.getChargeClassificationCode())){

                dto.setNetAmount(maxFrtdto.getNetAmount());
                dto.setIncentiveAmount(maxFrtdto.getIncentiveAmount());
            }
        }
        return dtos;
    }

    private static void sumTheTwoCharges(ParcelAuditDetailsDto existingDto, ParcelAuditDetailsDto dto) {

        if ((existingDto.getNetAmount() != null && !existingDto.getNetAmount().isEmpty()) && (dto.getNetAmount() != null && !dto.getNetAmount().isEmpty()))
            existingDto.setNetAmount(String.valueOf(new BigDecimal(existingDto.getNetAmount()).add(new BigDecimal(dto.getNetAmount()))));

        if (existingDto.getActualWeight() != null && dto.getActualWeight() != null)
            existingDto.setActualWeight(existingDto.getActualWeight().add(dto.getActualWeight()));

        if ((existingDto.getPackageWeight() != null && !existingDto.getPackageWeight().isEmpty()) && (dto.getPackageWeight() != null && !dto.getPackageWeight().isEmpty()))
            existingDto.setPackageWeight(String.valueOf(new BigDecimal(existingDto.getPackageWeight()).add(new BigDecimal(dto.getPackageWeight()))));

        if ((existingDto.getPackageDimension() != null && !existingDto.getPackageDimension().isEmpty()) && (dto.getPackageDimension() != null && !dto.getPackageDimension().isEmpty())) {

            String[] existingDimensions = existingDto.getPackageDimension().split("x");
            String[] dimensions = dto.getPackageDimension().split("x");

            if (dimensions != null && existingDimensions != null) {
                BigDecimal firstValue = null;
                BigDecimal secondValue = null;
                BigDecimal thirdValue = null;
                if (dimensions.length > 0 && existingDimensions.length > 0) {
                    if ((dimensions[0] != null && existingDimensions[0] != null) && (isBigDecimalNumber(dimensions[0].trim()) && isBigDecimalNumber(existingDimensions[0].trim()))) {
                        firstValue = new BigDecimal(existingDimensions[0].trim()).add(new BigDecimal(dimensions[0].trim()));
                    }
                }
                if (dimensions.length > 1 && existingDimensions.length > 1) {
                    if ((dimensions[1] != null && existingDimensions[1] != null) && (isBigDecimalNumber(dimensions[1].trim()) && isBigDecimalNumber(existingDimensions[1].trim()))) {
                        secondValue = new BigDecimal(existingDimensions[1].trim()).add(new BigDecimal(dimensions[1].trim()));
                    }
                }
                if (dimensions.length > 2 && existingDimensions.length > 2) {
                    if ((dimensions[2] != null && existingDimensions[2] != null) && (isBigDecimalNumber(dimensions[2].trim()) && isBigDecimalNumber(existingDimensions[2].trim()))) {
                        thirdValue = new BigDecimal(existingDimensions[2].trim()).add(new BigDecimal(dimensions[2].trim()));
                    }
                }
                StringBuilder dimeBuilder = new StringBuilder();
                if (firstValue != null)
                    dimeBuilder.append(firstValue);
                if (secondValue != null)
                    dimeBuilder.append("x" + secondValue);
                if (thirdValue != null)
                    dimeBuilder.append("x" + thirdValue);

                if (dimeBuilder.length() > 0)
                    existingDto.setPackageDimension(dimeBuilder.toString());
            }
        }

        if (existingDto.getIncentiveAmount() != null && dto.getIncentiveAmount() != null)
            existingDto.setIncentiveAmount(existingDto.getIncentiveAmount().add(dto.getIncentiveAmount()));

        if (existingDto.getPieces() != null && dto.getPieces() != null)
            existingDto.setPieces(existingDto.getPieces().intValue() + dto.getPieces().intValue());

    }

    private static boolean isBigDecimalNumber(String value) {

        try {
            BigDecimal bigDecimal = new BigDecimal(value);
        } catch (Exception e) {
            m_log.error("ERROR - " + e.getStackTrace());
            return false;
        }
        return true;
    }

    public static boolean isNumeric(String strNum) {
        return strNum.matches("-?\\d+(\\.\\d+)?");
    }

    private static List<ParcelAuditDetailsDto> getParentIdCharges(List<ParcelAuditDetailsDto> shipmentRecords, Long ratingParentId) {

        List<ParcelAuditDetailsDto> list = new ArrayList<>();

        for (ParcelAuditDetailsDto dto : shipmentRecords) {

            if (dto.getParentId().compareTo(ratingParentId) == 0)
                list.add(dto);
        }


        return list;
    }

    public static Long getMinParentId(List<ParcelAuditDetailsDto> billDateShipments) {

        Long minValue = billDateShipments.get(0).getParentId();
        for (ParcelAuditDetailsDto dto : billDateShipments) {

            if (!(minValue.compareTo(dto.getParentId()) < 0)) {
                minValue = dto.getParentId();
            }

        }

        return minValue;
    }

    /**
     * @param trackingNumberDetails
     * @param ratingCharge
     */
    public static void prepareXmlReqAddressInfo(List<ParcelAuditDetailsDto> trackingNumberDetails, ParcelAuditDetailsDto ratingCharge) {

        Map<Long, List<ParcelAuditDetailsDto>> shipments = ParcelRatingUtil.organiseShipmentsByParentId(trackingNumberDetails);

        boolean returnShipment = false;
        String maxQuantity = "1";

        if (shipments != null && ratingCharge != null && ratingCharge.getParentId() != null)
            returnShipment = isReturnShipment(shipments.get(ratingCharge.getParentId()));

        List<ParcelAuditDetailsDto> SortOrderTrackDetails = new ArrayList<>(trackingNumberDetails);
        SortOrderTrackDetails.sort(Comparator.comparing(ParcelAuditDetailsDto::getId).reversed());
        for (ParcelAuditDetailsDto dto : SortOrderTrackDetails) {

            if (ratingCharge != null && ratingCharge.getParentId().compareTo(dto.getParentId()) > 0 && (shipments != null && isReturnShipment(shipments.get(dto.getParentId()), returnShipment))) {

                if ((ratingCharge.getSenderCountry() == null || ratingCharge.getSenderCountry().isEmpty())
                        && (dto.getSenderCountry() != null && !dto.getSenderCountry().isEmpty())) {
                    ratingCharge.setSenderCountry(dto.getSenderCountry());
                }
                if ((ratingCharge.getSenderState() == null || ratingCharge.getSenderState().isEmpty())
                        && (dto.getSenderState() != null && !dto.getSenderState().isEmpty())) {
                    ratingCharge.setSenderState(dto.getSenderState());
                }
                if ((ratingCharge.getSenderCity() == null || ratingCharge.getSenderCity().isEmpty())
                        && (dto.getSenderCity() != null && !dto.getSenderCity().isEmpty())) {
                    ratingCharge.setSenderCity(dto.getSenderCity());
                }
                if ((ratingCharge.getSenderZipCode() == null || ratingCharge.getSenderZipCode().isEmpty())
                        && (dto.getSenderZipCode() != null && !dto.getSenderZipCode().isEmpty())) {
                    ratingCharge.setSenderZipCode(dto.getSenderZipCode());
                    ratingCharge.setSenderBilledZipCode(dto.getSenderZipCode());
                }

                if ((ratingCharge.getReceiverCountry() == null || ratingCharge.getReceiverCountry().isEmpty())
                        && (dto.getReceiverCountry() != null && !dto.getReceiverCountry().isEmpty())) {
                    ratingCharge.setReceiverCountry(dto.getReceiverCountry());
                }
                if ((ratingCharge.getReceiverState() == null || ratingCharge.getReceiverState().isEmpty())
                        && (dto.getReceiverState() != null && !dto.getReceiverState().isEmpty())) {
                    ratingCharge.setReceiverState(dto.getReceiverState());
                }
                if ((ratingCharge.getReceiverCity() == null || ratingCharge.getReceiverCity().isEmpty())
                        && (dto.getReceiverCity() != null && !dto.getReceiverCity().isEmpty())) {
                    ratingCharge.setReceiverCity(dto.getReceiverCity());
                }
                if ((ratingCharge.getReceiverZipCode() == null || ratingCharge.getReceiverZipCode().isEmpty())
                        && (dto.getReceiverZipCode() != null && !dto.getReceiverZipCode().isEmpty())) {
                    ratingCharge.setReceiverZipCode(dto.getReceiverZipCode());
                    ratingCharge.setReceiverBilledZipCode(dto.getReceiverZipCode());
                }

                if ((ratingCharge.getZone() == null || ratingCharge.getZone().isEmpty())
                        && (dto.getZone() != null && !dto.getZone().isEmpty())) {
                    ratingCharge.setZone(dto.getZone());
                }
                if ((ratingCharge.getPackageType() == null || ratingCharge.getPackageType().isEmpty())
                        && (dto.getPackageType() != null && !dto.getPackageType().isEmpty())) {
                    ratingCharge.setPackageType(dto.getPackageType());
                }

                //setItemPackageInfo(ratingCharge, dto, maxQuantity);

            }
        }

    }

    private static void setItemPackageInfo(ParcelAuditDetailsDto ratingCharge, List<ParcelAuditDetailsDto> trackingNumberDetails) {

        Map<Long, List<ParcelAuditDetailsDto>> shipments = ParcelRatingUtil.organiseShipmentsByParentId(trackingNumberDetails);

        boolean returnShipment = false;
        String maxQuantity = "1";

        if (shipments != null && ratingCharge != null && ratingCharge.getParentId() != null)
            returnShipment = isReturnShipment(shipments.get(ratingCharge.getParentId()));

        List<ParcelAuditDetailsDto> SortOrderTrackDetails = new ArrayList<>(trackingNumberDetails);
        SortOrderTrackDetails.sort(Comparator.comparing(ParcelAuditDetailsDto::getId).reversed());
        for (ParcelAuditDetailsDto dto : SortOrderTrackDetails) {

            if (ratingCharge != null && ratingCharge.getParentId().compareTo(dto.getParentId()) > 0 && (shipments != null && isReturnShipment(shipments.get(dto.getParentId()), returnShipment))) {


                if ((ratingCharge.getPackageWeight() == null || new BigDecimal(ratingCharge.getPackageWeight()).compareTo(BigDecimal.ZERO) == 0)
                        && (dto.getPackageWeight() != null && new BigDecimal(dto.getPackageWeight()).compareTo(BigDecimal.ZERO) != 0)) {
                    ratingCharge.setPackageWeight(dto.getPackageWeight());
                }

                if ((ratingCharge.getActualWeight() == null || ratingCharge.getActualWeight().compareTo(BigDecimal.ZERO) == 0)
                        && (dto.getActualWeight() != null && dto.getActualWeight().compareTo(BigDecimal.ZERO) != 0)) {
                    ratingCharge.setActualWeight(dto.getActualWeight());
                }

                if ((ratingCharge.getWeightUnit() == null || ratingCharge.getWeightUnit().isEmpty())
                        && (dto.getWeightUnit() != null && !dto.getWeightUnit().isEmpty())) {
                    ratingCharge.setWeightUnit(dto.getWeightUnit());
                }

                if ((ratingCharge.getActualWeightUnit() == null || ratingCharge.getActualWeightUnit().isEmpty())
                        && (dto.getActualWeightUnit() != null && !dto.getActualWeightUnit().isEmpty())) {
                    ratingCharge.setActualWeightUnit(dto.getActualWeightUnit());
                }

                if ((ratingCharge.getItemQuantity() == null || new BigDecimal(ratingCharge.getItemQuantity()).compareTo(BigDecimal.ZERO) == 0)
                        && (dto.getItemQuantity() != null && new BigDecimal(dto.getItemQuantity()).compareTo(BigDecimal.ZERO) != 0)) {

                    if (new BigDecimal(dto.getItemQuantity()).compareTo(new BigDecimal(maxQuantity)) == 1) {
                        maxQuantity = dto.getItemQuantity();
                    }

                }

                if ((ratingCharge.getQuantityUnit() == null || ratingCharge.getQuantityUnit().isEmpty())
                        && (dto.getQuantityUnit() != null && !dto.getQuantityUnit().isEmpty())) {
                    ratingCharge.setQuantityUnit(dto.getQuantityUnit());
                }

                if ((ratingCharge.getDimLength() == null || new BigDecimal(ratingCharge.getDimLength()).compareTo(BigDecimal.ZERO) == 0)
                        && (dto.getDimLength() != null && new BigDecimal(dto.getDimLength()).compareTo(BigDecimal.ZERO) != 0)) {
                    ratingCharge.setDimLength(dto.getDimLength());
                }

                if ((ratingCharge.getDimWidth() == null || new BigDecimal(ratingCharge.getDimWidth()).compareTo(BigDecimal.ZERO) == 0)
                        && (dto.getDimWidth() != null && new BigDecimal(dto.getDimWidth()).compareTo(BigDecimal.ZERO) != 0)) {
                    ratingCharge.setDimWidth(dto.getDimWidth());
                }

                if ((ratingCharge.getDimHeight() == null || new BigDecimal(ratingCharge.getDimHeight()).compareTo(BigDecimal.ZERO) == 0)
                        && (dto.getDimHeight() != null && new BigDecimal(dto.getDimHeight()).compareTo(BigDecimal.ZERO) != 0)) {
                    ratingCharge.setDimHeight(dto.getDimHeight());
                }

                if ((ratingCharge.getUnitOfDim() == null || ratingCharge.getUnitOfDim().isEmpty())
                        && (dto.getUnitOfDim() != null && !dto.getUnitOfDim().isEmpty())) {
                    ratingCharge.setUnitOfDim(dto.getUnitOfDim());
                }
            }
        }

        if ((ratingCharge.getItemQuantity() == null || ratingCharge.getItemQuantity().trim().isEmpty() || new BigDecimal(ratingCharge.getItemQuantity().trim()).compareTo(BigDecimal.ZERO) == 0)) {
            ratingCharge.setItemQuantity(maxQuantity);
        }
    }

    public static void setPrevParentIdShipDate(List<ParcelAuditDetailsDto> toRate, List<ParcelAuditDetailsDto> trackDeatils) {

        ParcelAuditDetailsDto frtDto = getImmediateFrtParentId(toRate, trackDeatils);
       if(frtDto != null) {
           for (ParcelAuditDetailsDto dto : toRate) {
               if (dto.getPickupDate() == null && frtDto.getPickupDate() != null) {
                   dto.setPickupDate(frtDto.getPickupDate());
               }
           }
       }
    }

    public static ParcelAuditDetailsDto getImmediateFrtParentId(List<ParcelAuditDetailsDto> shipmentDetails
            , List<ParcelAuditDetailsDto> pickUpDateShipmentDetails) {

        Map<Long, List<ParcelAuditDetailsDto>> listMap = organiseShipmentsByParentId(pickUpDateShipmentDetails);
        ParcelAuditDetailsDto frtCharged = null;
        Long frtParentId = null;
        if (listMap != null) {
            for (Map.Entry<Long, List<ParcelAuditDetailsDto>> entry : listMap.entrySet()) {

                if (shipmentDetails.get(0).getParentId().compareTo(entry.getKey()) > 0) {
                    frtParentId = entry.getKey();
                }

            }
            if (frtParentId != null) {
                List<ParcelAuditDetailsDto> detailsDtos = listMap.get(frtParentId);

                if (detailsDtos != null)
                    frtCharged = ParcelRatingUtil.findFrtCharge(detailsDtos);
            }
        }
        return frtCharged;
    }

    public static void setCommonValues(ParcelRateDetailsDto rateDetails, RatingQueueBean queueBean, ParcelRateResponse.PriceSheet priceSheet) {

        rateDetails.setReturnFlag(queueBean.getReturnFlag());
        rateDetails.setResiFlag(queueBean.getResiFlag());
        rateDetails.setComToRes(queueBean.getComToRes());
        rateDetails.setActualServiceBucket(queueBean.getActualServiceBucket());
        rateDetails.setHwtIdentifier(queueBean.getHwtIdentifier());
        rateDetails.setPackageType(queueBean.getPackageType());
        if (priceSheet != null) {
            rateDetails.setRateSetName(priceSheet.getRateSet());
            rateDetails.setShipperCategory(priceSheet.getCategory());
            rateDetails.setContractName(priceSheet.getContractName());
            rateDetails.setZone(priceSheet.getZone());
        } else {
            rateDetails.setRateSetName(queueBean.getRateSetName());
        }

    }

    public static void prepareAdditionalAccessorial(ParcelRateResponse.PriceSheet priceSheet, Long parentId, List<ParcelRateResponse.Charge> mappedAccChanges, List<AccessorialDto> dtos, boolean frtChargeFound, boolean fscChargeFound, List<AccessorialDto> prevParentsRatesDtos, RatingQueueBean ratingQueueBean) throws Exception {

        List<ParcelRateResponse.Charge> accessorialCharges = ParcelRateResponseParser.getAccessorialCharges(priceSheet);
        checkAnyAccMissing(accessorialCharges, ratingQueueBean);

        if (accessorialCharges != null && !accessorialCharges.isEmpty()) {
            if (mappedAccChanges != null && !mappedAccChanges.isEmpty()) {
                Iterator<ParcelRateResponse.Charge> chargeIterator = accessorialCharges.iterator();
                List<ParcelRateResponse.Charge> tempRemoveList = new ArrayList<>();
                while (chargeIterator.hasNext()) {
                    ParcelRateResponse.Charge tempCharge = chargeIterator.next();
                    for (ParcelRateResponse.Charge mappedChrg : mappedAccChanges) {
                        if (mappedChrg != null && tempCharge != null
                                && mappedChrg.getType() != null && tempCharge.getType() != null
                                && mappedChrg.getType().equalsIgnoreCase(tempCharge.getType())
                                && mappedChrg.getName() != null && tempCharge.getName() != null
                                && mappedChrg.getName().equalsIgnoreCase(tempCharge.getName())) {
                            tempRemoveList.add(tempCharge);
                        }
                    }
                }
                if (tempRemoveList != null && tempRemoveList.size() > 0) {
                    accessorialCharges.removeAll(tempRemoveList);
                    tempRemoveList = null;
                }
            }
        }


        if (accessorialCharges != null && accessorialCharges.size() > 0) {

            Map<String, ParcelRateResponse.Charge> disMap = new LinkedHashMap<>();

            for (ParcelRateResponse.Charge charge : accessorialCharges) {
                if (disMap.containsKey(charge.getEdiCode())) {

                    disMap.get(charge.getEdiCode()).setAmount(disMap.get(charge.getEdiCode()).getAmount().add(charge.getAmount()));
                } else {
                    disMap.put(charge.getEdiCode(), charge);
                }

            }

            for (Map.Entry<String, ParcelRateResponse.Charge> entry : disMap.entrySet()) {

                ParcelRateResponse.Charge charge = entry.getValue();

                AccessorialDto dto = new AccessorialDto();

                dto.setCode(charge.getEdiCode());

                BigDecimal prevRtrAmt = ParcelRatingUtil.findPrevRateAmtByCode(prevParentsRatesDtos, charge.getEdiCode(), "accessorial");

                dto.setRtrAmount(charge.getAmount().subtract(prevRtrAmt));
                dto.setType("accessorial");
                dto.setParentId(parentId);
                dto.setName(charge.getName());
                dtos.add(dto);

            }

        }

        if (!fscChargeFound) {
            ParcelRateResponse.Charge charge = ParcelRateResponseParser.findChargeByType(ParcelRateResponse.ChargeType.ACCESSORIAL_FUEL.name(), priceSheet);

            if (charge != null) {

                AccessorialDto dto = new AccessorialDto();

                dto.setCode(charge.getEdiCode());
                BigDecimal prevRtrAmt = ParcelRatingUtil.findPrevRateAmtByCode(prevParentsRatesDtos, charge.getEdiCode(), "accessorial");
                dto.setRtrAmount(charge.getAmount().subtract(prevRtrAmt));
                dto.setType("accessorial");
                dto.setParentId(parentId);
                dto.setName(charge.getName());
                dtos.add(dto);
            }
        }

        if (!frtChargeFound) {
            ParcelRateResponse.Charge charge = ParcelRateResponseParser.findChargeByType(ParcelRateResponse.ChargeType.ITEM.name(), priceSheet);

            if (charge != null) {

                AccessorialDto dto = new AccessorialDto();

                dto.setCode("FRT");
                BigDecimal prevRtrAmt = ParcelRatingUtil.findPrevRateAmtByCode(prevParentsRatesDtos, "FRT", "accessorial");
                dto.setRtrAmount(charge.getAmount().subtract(prevRtrAmt));
                dto.setType("accessorial");
                dto.setParentId(parentId);
                dto.setName(charge.getName());
                dtos.add(dto);
            }
        }


    }

    private static void checkAnyAccMissing(List<ParcelRateResponse.Charge> accessorialCharges, RatingQueueBean ratingQueueBean) throws Exception {

        boolean chargeFound = false;
        int count = 0;
        Set<ParcelRateResponse.Charge> missingCharges = null;
        if (ratingQueueBean.getAccessorials() != null && accessorialCharges != null) {
            Set<ParcelRateRequest.ServiceFlag> serviceFlags = ratingQueueBean.getAccessorials();
            if (serviceFlags != null && serviceFlags.size() > 0) {
                for (ParcelRateRequest.ServiceFlag serviceFlag : serviceFlags) {
                    count = 0;
                    chargeFound = false;
                    for (ParcelRateResponse.Charge charge : accessorialCharges) {
                        count++;
                        if (serviceFlag.getCode() != null && charge.getEdiCode() != null) {
                            if (serviceFlag.getCode().equalsIgnoreCase(charge.getEdiCode())) {
                                chargeFound = true;
                                break;
                            }
                        }
                    }

                    if (!chargeFound && count == accessorialCharges.size()) {
                        ParcelRateResponse.Charge newCharge = new ParcelRateResponse.Charge();
                        newCharge.setEdiCode(serviceFlag.getCode());
                        newCharge.setType("ACCESSORIAL");
                        newCharge.setName(serviceFlag.getCode()+" ACCESSORIAL");
                        newCharge.setAmount(new BigDecimal("0.00"));
                        newCharge.setRate(new BigDecimal("0.00"));
                        if (missingCharges == null)
                            missingCharges = new HashSet<>();

                        missingCharges.add(newCharge);
                    }


                }
            }
        }
        if (missingCharges != null && missingCharges.size() > 0)
            accessorialCharges.addAll(missingCharges);
    }

    public static void prepareAddDiscounts(ParcelRateResponse.PriceSheet priceSheet, Long parentId, List<ParcelRateResponse.Charge> mappedDscChanges, List<AccessorialDto> addAccAndDisdtos, List<AccessorialDto> prevParentsRatesDtos) {

        List<ParcelRateResponse.Charge> discountCharges = ParcelRateResponseParser.getAllOtherDiscounts(priceSheet);

        if (discountCharges != null && !discountCharges.isEmpty()) {
            if (mappedDscChanges != null && !mappedDscChanges.isEmpty()) {
                Iterator<ParcelRateResponse.Charge> chargeIterator = mappedDscChanges.iterator();
                List<ParcelRateResponse.Charge> tempRemoveList = new ArrayList<>();
                while (chargeIterator.hasNext()) {
                    ParcelRateResponse.Charge tempCharge = chargeIterator.next();
                    for (ParcelRateResponse.Charge mappedChrg : mappedDscChanges) {
                        if (mappedChrg != null && tempCharge != null
                                && mappedChrg.getType() != null && tempCharge.getType() != null
                                && mappedChrg.getType().equalsIgnoreCase(tempCharge.getType())
                                && mappedChrg.getName() != null && tempCharge.getName() != null
                                && mappedChrg.getName().equalsIgnoreCase(tempCharge.getName())) {
                            tempRemoveList.add(tempCharge);
                        }
                    }
                }
                if (tempRemoveList != null && tempRemoveList.size() > 0) {
                    discountCharges.removeAll(tempRemoveList);
                    tempRemoveList = null;
                }
            }
        }

        if (discountCharges != null && discountCharges.size() > 0) {

            Map<String, ParcelRateResponse.Charge> disMap = new LinkedHashMap<>();
            for (ParcelRateResponse.Charge charge : discountCharges) {

                if (disMap.containsKey(charge.getName())) {

                    disMap.get(charge.getName()).setAmount(disMap.get(charge.getName()).getAmount().add(charge.getAmount()));
                } else {
                    disMap.put(charge.getName(), charge);
                }

            }

            for (Map.Entry<String, ParcelRateResponse.Charge> entry : disMap.entrySet()) {

                ParcelRateResponse.Charge charge = entry.getValue();
                AccessorialDto dto = new AccessorialDto();

                dto.setCode(charge.getEdiCode());
                BigDecimal prevRtrAmt = ParcelRatingUtil.findPrevRateAmtByDisName(prevParentsRatesDtos, charge.getName(), "discount");
                dto.setRtrAmount(charge.getAmount().subtract(prevRtrAmt));
                dto.setType("discount");
                dto.setParentId(parentId);
                dto.setName(charge.getName());

                addAccAndDisdtos.add(dto);
            }


        }

    }

    public static void setAddressCorrectionAsAccessorial(List<ParcelAuditDetailsDto> shipmentRecords) {

        for (ParcelAuditDetailsDto dto : shipmentRecords) {

            if ("FRT".equalsIgnoreCase(dto.getChargeClassificationCode()) && "ADC".equalsIgnoreCase(dto.getChargeCategoryDetailCode())) {
                dto.setChargeClassificationCode("ACC");
            }
        }
    }


    public static List<ParcelAuditDetailsDto> getImmediateParentIdInfo(Long ParentId, Map<Long, List<ParcelAuditDetailsDto>> listMap) {

        List<ParcelAuditDetailsDto> detailsDtos = null;
        Long frtParentId = null;
        if (listMap != null) {
            for (Map.Entry<Long, List<ParcelAuditDetailsDto>> entry : listMap.entrySet()) {

                if (ParentId.compareTo(entry.getKey()) > 0) {
                    frtParentId = entry.getKey();
                }

            }
            if (frtParentId != null) {
                detailsDtos = listMap.get(frtParentId);

            }
        }
        return detailsDtos;
    }

    public static void closeConnection(Connection conn) {

        try {
            if (conn != null)
                conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getPiecesCount(List<ParcelAuditDetailsDto> shipmentRecords) {

        Set<String> trakingNumsSet = new HashSet<>();
        for (ParcelAuditDetailsDto dto : shipmentRecords) {

            if (!trakingNumsSet.contains(dto.getTrackingNumber())) {
                trakingNumsSet.add(dto.getTrackingNumber());
            }
        }

        return trakingNumsSet.size();
    }
}

