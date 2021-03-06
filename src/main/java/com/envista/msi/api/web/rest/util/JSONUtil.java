package com.envista.msi.api.web.rest.util;

import com.envista.msi.api.web.rest.dto.MapCoordinatesDto;
import com.envista.msi.api.web.rest.dto.dashboard.CodeValueDto;
import com.envista.msi.api.web.rest.dto.dashboard.DashboardsFilterCriteria;
import com.envista.msi.api.web.rest.dto.dashboard.annualsummary.AccountSummaryDto;
import com.envista.msi.api.web.rest.dto.dashboard.annualsummary.AnnualSummaryDto;
import com.envista.msi.api.web.rest.dto.dashboard.annualsummary.CarrierWiseMonthlySpendDto;
import com.envista.msi.api.web.rest.dto.dashboard.annualsummary.MonthlySpendByModeDto;
import com.envista.msi.api.web.rest.dto.dashboard.auditactivity.*;
import com.envista.msi.api.web.rest.dto.dashboard.carrierspend.CarrierSpendAnalysisDto;
import com.envista.msi.api.web.rest.dto.dashboard.common.*;
import com.envista.msi.api.web.rest.dto.dashboard.filter.UserFilterUtilityDataDto;
import com.envista.msi.api.web.rest.dto.dashboard.netspend.AccessorialSpendDto;
import com.envista.msi.api.web.rest.dto.dashboard.netspend.ActualVsBilledWeightDto;
import com.envista.msi.api.web.rest.dto.dashboard.netspend.NetSpendByModeDto;
import com.envista.msi.api.web.rest.dto.dashboard.netspend.NetSpendOverTimeDto;
import com.envista.msi.api.web.rest.dto.dashboard.networkanalysis.PortLanesDto;
import com.envista.msi.api.web.rest.dto.dashboard.networkanalysis.ShipmentDto;
import com.envista.msi.api.web.rest.dto.dashboard.networkanalysis.ShipmentRegionDto;
import com.envista.msi.api.web.rest.dto.dashboard.networkanalysis.ShippingLanesDto;
import com.envista.msi.api.web.rest.dto.dashboard.report.DashboardReportDto;
import com.envista.msi.api.web.rest.dto.dashboard.servicelevel.ServiceLevelDto;
import com.envista.msi.api.web.rest.dto.dashboard.shipmentoverview.AverageSpendPerShipmentDto;
import com.envista.msi.api.web.rest.dto.dashboard.shipmentoverview.AverageWeightModeShipmtDto;
import com.envista.msi.api.web.rest.dto.dashboard.shipmentoverview.ServiceLevelUsageAndPerformanceDto;
import com.envista.msi.api.web.rest.dto.reports.ReportCustomerCarrierDto;
import com.envista.msi.api.web.rest.dto.reports.ReportFolderDto;
import com.envista.msi.api.web.rest.dto.reports.ReportModesDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author SANKER
 *
 */
public class JSONUtil {
    static ArrayList<String> colorsList = new ArrayList<String>();
    static final DecimalFormat commaSeperatedDecimalFormat = new DecimalFormat("#,##0");
    static final DecimalFormat commaSeperatedDecimalFormat1 = new DecimalFormat("#,##0.0");
    static final DecimalFormat commaSeperatedDecimalFormat2 = new DecimalFormat("#,##0.00");

    static String[] commaSeperatedFieldsArr = {
            "Total Weight", "Total Charges", "Line Haul", "Fuel Surcharge", "Discount", "Accessorials",
            "Adjustments", "Total Due Amount", "Invoice Amount", "Approved Line Charges", "Line Charges", "Adjustment"
    };
    static final List<String> commaSeperatedFields = Arrays.asList(commaSeperatedFieldsArr);

    static {
        if (colorsList.isEmpty()) {

            colorsList.add("#673FB4");
            colorsList.add("Green");
            colorsList.add("Red");
            colorsList.add("Magenta");// Pink
            colorsList.add("CornflowerBlue");
            colorsList.add("#1d3549");
            colorsList.add("SlateGray");
            colorsList.add("Brown");
            colorsList.add("#ed638d");
            colorsList.add("#5F9EA0");
            colorsList.add("Maroon");
            colorsList.add("#295C7A");
            colorsList.add("MediumOrchid");
            colorsList.add("#333300");
            colorsList.add("Orange");
            colorsList.add("DarkOliveGreen");
            colorsList.add("DimGray");
            colorsList.add("#F08080");
            colorsList.add("Black");
            colorsList.add("#000080");
            colorsList.add("#C21D55");// Rose color
        }
    }

    /**
     * @param obj
     * @return JSONString
     */
    final public static String ConvertObject2JSON(Object obj) {
        String jsonInString = "{}";
        ObjectMapper mapper = new ObjectMapper();
        //Object to JSON in String
        try {
            jsonInString = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            // ignore exception
            e.printStackTrace();
        }
        return jsonInString;
    }

    /**
     * @param <T>
     * @param jsonInString
     * @return Object
     */
    final public static <T> T ConvertJSON2Object(String jsonInString, Class<T> type) {
        ObjectMapper mapper = new ObjectMapper();
        T ret = null;
        try {
            ret = (T) mapper.readValue(jsonInString, type);
        } catch (Exception e) {
            // ignore exception
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * @param netSpendDtoList
     * @return
     * @throws JSONException
     */
    public static JSONObject prepareNetSpendByModesJson(List<NetSpendByModeDto> netSpendDtoList) throws JSONException {
        JSONObject netSpendJsonData = new JSONObject();
        JSONArray valuesArray = null;
        JSONArray seriesArray = null;

        if (netSpendDtoList != null && netSpendDtoList.size() > 0) {
            valuesArray = new JSONArray();
            seriesArray = new JSONArray();

            Map<String, HashMap<String, Double>> modesValuesMap = new LinkedHashMap<String, HashMap<String, Double>>();
            List<String> scortypeList = new ArrayList<String>();

            for (NetSpendByModeDto netSpendDto : netSpendDtoList) {
                if (netSpendDto != null && netSpendDto.getSpend() != 0) {
                    String mode = netSpendDto.getModes();
                    String scoreType = netSpendDto.getScoreType();
                    Double spend = ((double) Math.round(netSpendDto.getSpend()));

                    if (!scortypeList.contains(scoreType)) {
                        scortypeList.add(scoreType);
                    }

                    if (modesValuesMap.containsKey(mode)) {
                        modesValuesMap.get(mode).put(scoreType, spend);
                    } else {
                        HashMap<String, Double> tempHashMap = new HashMap<String, Double>();
                        tempHashMap.put(scoreType, spend);
                        modesValuesMap.put(mode, tempHashMap);
                    }
                }
            }

            int counter = 1;
            Iterator<String> modesIterator = modesValuesMap.keySet().iterator();

            while (modesIterator.hasNext()) {
                JSONObject jsonObject = new JSONObject();
                String mode = modesIterator.next();
                HashMap<String, Double> scoreTypeMap = modesValuesMap.get(mode);
                Iterator<String> scoreTypesIterator = scoreTypeMap.keySet().iterator();

                jsonObject.put("name", mode);
                jsonObject.put("counter", counter);

                while (scoreTypesIterator.hasNext()) {
                    String scoreType = scoreTypesIterator.next();
                    double spend = scoreTypeMap.get(scoreType);
                    jsonObject.put(scoreType, spend);
                }

                valuesArray.put(jsonObject);
                counter++;
            }

            String append = "\"";
            counter = 1;

            for (String scoreType : scortypeList) {
                scoreType = append + scoreType + append;
                String seriesId = append + "S" + counter + append;
                String object = "{\"id\":" + seriesId + ",\"name\":" + scoreType + ", \"data\": {\"field\":" + scoreType + "},style: { depth: 4, gradient: 0.9,fillColor: \"#65ABE8\" }}";
                seriesArray.put(new JSONObject(object));
                counter++;
            }

            netSpendJsonData.put("values", valuesArray);
            netSpendJsonData.put("series", seriesArray);
        }
        return netSpendJsonData;
    }


    public static JSONObject prepareNetSpendOverTimeJson(List<NetSpendOverTimeDto> netSpendOverTimeDtos) throws JSONException {
        JSONObject returnObject = new JSONObject();
        JSONArray valuesArray = null;
        JSONArray seriesArray = null;
        LinkedHashMap<String, HashMap<String, Double>> datesValuesMap = null;
        ArrayList<String> carriersList = null;

        if (netSpendOverTimeDtos != null && netSpendOverTimeDtos.size() > 0) {
            valuesArray = new JSONArray();
            seriesArray = new JSONArray();
            datesValuesMap = new LinkedHashMap<String, HashMap<String, Double>>();
            carriersList = new ArrayList<String>();

            for (NetSpendOverTimeDto overTimeDto : netSpendOverTimeDtos) {
                if (overTimeDto != null) {
                    String billDate = overTimeDto.getBillingDate();
                    String carrierName = overTimeDto.getCarrierName();
                    long carrierId = overTimeDto.getCarrierId();
                    Double spend = ((double) Math.round(overTimeDto.getNetCharges()));

                    if (spend != 0) {
                        String concatCarrier = carrierId + "#@#" + carrierName;
                        if (!carriersList.contains(concatCarrier)) {
                            carriersList.add(concatCarrier);
                        }

                        if (datesValuesMap.containsKey(billDate)) {
                            datesValuesMap.get(billDate).put(carrierName, spend);
                        } else {
                            HashMap<String, Double> tempHashMap = new HashMap<String, Double>();
                            tempHashMap.put(carrierName, spend);
                            datesValuesMap.put(billDate, tempHashMap);
                        }
                    }
                }
            }

            // Bar Chart
            int counter = 1;
            Iterator<String> datesIterator = datesValuesMap.keySet().iterator();

            while (datesIterator.hasNext()) {
                JSONObject jsonObject = new JSONObject();

                String date = datesIterator.next();
                HashMap<String, Double> carrierFlagMap = datesValuesMap.get(date);

                Iterator<String> carrierFlagIterator = carrierFlagMap.keySet().iterator();

                jsonObject.put("name", date);
                jsonObject.put("counter", counter);

                while (carrierFlagIterator.hasNext()) {
                    String carrierFlag = carrierFlagIterator.next();
                    double spend = carrierFlagMap.get(carrierFlag);
                    jsonObject.put(carrierFlag, spend);
                }

                valuesArray.put(jsonObject);
                counter++;
            }

            String append = "\"";
            counter = 1;

            for (String carrierDetails : carriersList) {
                String carrierId = carrierDetails.split("#@#")[0];
                String carrierName = carrierDetails.split("#@#")[1];

                carrierId = append + carrierId + append;
                carrierName = append + carrierName + append;
                String seriesId = append + "S" + counter + append;
                String object = "{\"id\":" + seriesId + ",\"name\":" + carrierName + ", \"carrierId\":" + carrierId + ",\"data\": {\"field\":" + carrierName
                        + "},\"type\":\"line\",\"style\":{\"lineWidth\": 2,smoothing: true, marker: {shape: \"circle\", width: 5},";

                object = object + "lineColor: \"" + colorsList.get(counter - 1) + "\"";
                object = object + "}}";

                seriesArray.put(new JSONObject(object));
                counter++;
                if (counter == colorsList.size()) {
                    counter = 1;
                }
            }

            returnObject.put("values", valuesArray);
            returnObject.put("series", seriesArray);
            returnObject.put("carrierDetails", new JSONArray().put(carriersList));
        }
        return returnObject;
    }

    public static JSONObject prepareCostPerShipmentByServiceJson(List<ServiceLevelDto> serviceLevelDtoList,Double weight) throws JSONException {
        JSONObject returnObject = new JSONObject();
        JSONArray valuesArray = null;
        JSONArray seriesArray = null;
        LinkedHashMap<String, HashMap<String, Double>> datesValuesMap = null;
        ArrayList<String> servicesList = null;

        if (serviceLevelDtoList != null && serviceLevelDtoList.size() > 0) {
            valuesArray = new JSONArray();
            seriesArray = new JSONArray();
            datesValuesMap = new LinkedHashMap<String, HashMap<String, Double>>();
            servicesList = new ArrayList<String>();

            for (ServiceLevelDto serviceLevelDto : serviceLevelDtoList) {
                if (serviceLevelDto != null) {
                    String billDate = serviceLevelDto.getBillingDate();
                    String serviceLevel = serviceLevelDto.getServiceLevel();
                    Double costPerPackage ;
                    if(weight.doubleValue()==0)
                        costPerPackage = Math.rint(serviceLevelDto.getCostPerPackage());
                    else
                        costPerPackage = Math.rint(serviceLevelDto.getCostWeight());

                    if (costPerPackage != 0) {

                        if (!servicesList.contains(serviceLevel)) {
                            servicesList.add(serviceLevel);
                        }

                        if (datesValuesMap.containsKey(billDate)) {
                            datesValuesMap.get(billDate).put(serviceLevel, costPerPackage);
                        } else {
                            HashMap<String, Double> tempHashMap = new HashMap<String, Double>();
                            tempHashMap.put(serviceLevel, costPerPackage);
                            datesValuesMap.put(billDate, tempHashMap);
                        }
                    }
                }
            }

            // Bar Chart
            int counter = 1;
            Iterator<String> datesIterator = datesValuesMap.keySet().iterator();

            while (datesIterator.hasNext()) {
                JSONObject jsonObject = new JSONObject();

                String date = datesIterator.next();
                HashMap<String, Double> serviceLevelMap = datesValuesMap.get(date);

                Iterator<String> serviceFlagIterator = serviceLevelMap.keySet().iterator();

                jsonObject.put("name", date);
                jsonObject.put("counter", counter);

                int noofServices = 0 ;
                double  othersSum = 0;
                while (serviceFlagIterator.hasNext()) {
                    String serviceFlag = serviceFlagIterator.next();
                    double costPerPackage = serviceLevelMap.get(serviceFlag);
                    jsonObject.put(serviceFlag, costPerPackage);
                    noofServices++;
                }

                valuesArray.put(jsonObject);
                counter++;
            }

            String append = "\"";
            counter = 1;

            for (String serviceLevel : servicesList) {
                /*String carrierId = serviceDetails.split("#@#")[0];
                String carrierName = serviceDetails.split("#@#")[1];*/
                //String serviceLevel = serviceDetails

                //carrierId = append + carrierId + append;
                serviceLevel = append + serviceLevel + append;
                String seriesId = append + "S" + counter + append;
                String object = "{\"id\":" + seriesId + ",\"name\":" + serviceLevel + ", \"data\": {\"field\":" + serviceLevel
                        + "},\"type\":\"line\",\"style\":{\"lineWidth\": 2,smoothing: true, marker: {shape: \"circle\", width: 5},";

                object = object + "lineColor: \"" + colorsList.get(counter - 1) + "\"";
                object = object + "}}";

                seriesArray.put(new JSONObject(object));
                counter++;
                if (counter == colorsList.size()) {
                    counter = 1;
                }
            }

            returnObject.put("values", valuesArray);
            returnObject.put("series", seriesArray);
            returnObject.put("carrierDetails", new JSONArray().put(servicesList));
        }
        return returnObject;
    }

    public static JSONObject prepareCommonSpendJson(List<NetSpendCommonDto> spendList) throws JSONException {
        JSONObject returnObject = new JSONObject();
        JSONArray spendArray = null;
        HashMap<String, Double> spendMap = null;

        if (spendList != null && spendList.size() > 0) {
            spendArray = new JSONArray();
            spendMap = new HashMap<>();

            for (NetSpendCommonDto taxSpend : spendList) {
                if (taxSpend != null) {
                    String spendTypeName = taxSpend.getSpendTypeName();
                    Double spend = ((double) Math.round(taxSpend.getSpend()));
                    if (spend > 0) {
                        if (spendMap.containsKey(spendTypeName)) {
                            double spendAmount = spendMap.get(spendTypeName);
                            spendAmount += spend;
                            spendMap.put(spendTypeName, spendAmount);
                        } else {
                            spendMap.put(spendTypeName, spend);
                        }
                    }
                }
            }

            // Donut Chart
            Iterator<String> taxIterator = spendMap.keySet().iterator();
            while (taxIterator.hasNext()) {
                String tax = taxIterator.next();
                double spend = Math.round(spendMap.get(tax));

                JSONObject taxesJson = new JSONObject();
                taxesJson.put("name", tax);
                taxesJson.put("value", spend);

                spendArray.put(taxesJson);
                taxesJson = null;
            }
            returnObject.put("donutChartvalues", spendArray);
        }
        return returnObject;
    }

    public static JSONObject prepareRelSpendByCarrierChart(List<CommonValuesForChartDto> dataList) throws JSONException {
        JSONObject returnJson = new JSONObject();
        JSONArray returnArray = null;
        JSONObject statusJson = null;

        if (dataList != null && dataList.size() > 0) {
            returnArray = new JSONArray();


            Double othersSpendDbl = 0d;
            for (int i=0;i<dataList.size();i++) {
                CommonValuesForChartDto chartData = dataList.get(i);

                if(i<15)
                {
                    if (chartData != null) {
                        statusJson = new JSONObject();
                        statusJson.put("name", chartData.getName());
                        statusJson.put("value", chartData.getValue());
                        statusJson.put("id", chartData.getId());

                        returnArray.put(statusJson);
                        statusJson = null;
                    }
                }
                else {

                    othersSpendDbl = othersSpendDbl + chartData.getValue();

                     if(i==dataList.size()-1)
                     {
                         statusJson = new JSONObject();
                         statusJson.put("name", "Others");
                         statusJson.put("value", othersSpendDbl);
                         statusJson.put("id", "0");

                         returnArray.put(statusJson);
                         statusJson = null;
                     }

                }


            }
            returnJson.put("values", returnArray);
        }
        return returnJson;
    }


    public static JSONObject prepareTotalSpendByServiceChart(List<CommonValuesForChartDto> dataList) throws JSONException {
        JSONObject returnJson = new JSONObject();
        JSONArray returnArray = null;
        JSONObject statusJson = null;

        if (dataList != null && dataList.size() > 0) {
            returnArray = new JSONArray();
            Double othersSpendDbl = 0d;
            for (int i=0;i<dataList.size();i++) {
                CommonValuesForChartDto chartData = dataList.get(i);

                if(i<15)
                {
                    if (chartData != null) {
                        statusJson = new JSONObject();
                        statusJson.put("name", chartData.getName());
                        statusJson.put("value", chartData.getValue());
                        statusJson.put("id", chartData.getId());

                        returnArray.put(statusJson);
                        statusJson = null;
                    }
                }
                else {

                    othersSpendDbl = othersSpendDbl + chartData.getValue();

                    if(i==dataList.size()-1)
                    {
                        statusJson = new JSONObject();
                        statusJson.put("name", "Remaining");
                        statusJson.put("value", othersSpendDbl);
                        statusJson.put("id", "0");

                        returnArray.put(statusJson);
                        statusJson = null;
                    }

                }


            }
            returnJson.put("values", returnArray);
        }
        return returnJson;
    }

    public static JSONObject prepareCommonJsonForChart(List<CommonValuesForChartDto> dataList) throws JSONException {
        return prepareCommonJsonForChart(dataList, false);
    }

    /**
     * Generic method to prepare json data having id, name and values as key per object.
     * manipulateValue - flag is used to identify whether any manipulation need to be done on value field (Added during removing decimal places for MSI-29440).
     * @param dataList
     * @param manipulateValue
     * @return
     * @throws JSONException
     */
    public static JSONObject prepareCommonJsonForChart(List<CommonValuesForChartDto> dataList, boolean manipulateValue) throws JSONException {
        JSONObject returnJson = new JSONObject();
        JSONArray returnArray = null;
        JSONObject statusJson = null;

        if (dataList != null && dataList.size() > 0) {
            returnArray = new JSONArray();
            for (CommonValuesForChartDto chartData : dataList) {
                if (chartData != null) {
                    statusJson = new JSONObject();
                    statusJson.put("name", chartData.getName());
                    statusJson.put("value", manipulateValue ? Math.round(chartData.getValue()) : chartData.getValue());
                    statusJson.put("id", chartData.getId());

                    returnArray.put(statusJson);
                    statusJson = null;
                }
            }
            returnJson.put("values", returnArray);
        }
        return returnJson;
    }

    public static JSONObject prepareCommonJsonForWeightChart(List<CommonValuesForChartDto> dataList, boolean manipulateValue) throws JSONException {
        JSONObject returnJson = new JSONObject();
        JSONArray returnArray = null;
        JSONObject statusJson = null;

        if (dataList != null && dataList.size() > 0) {
            returnArray = new JSONArray();
            for (CommonValuesForChartDto chartData : dataList) {
                if (chartData != null) {
                    statusJson = new JSONObject();
                    statusJson.put("name", chartData.getName());
                    statusJson.put("value", manipulateValue ? CommonUtil.round(chartData.getValue(), 1) : chartData.getValue());
                    statusJson.put("id", chartData.getId());


                    returnArray.put(statusJson);
                    statusJson = null;
                }
            }

            /*for (int i=0;i<dataList.size();i++) {
                CommonValuesForChartDto chartData = dataList.get(i);
                Double othersSpendDbl = 0d;
                if(i<10)
                {
                    if (chartData != null) {
                        statusJson = new JSONObject();
                        statusJson.put("name", chartData.getName());
                        statusJson.put("value", chartData.getValue());
                        statusJson.put("id", chartData.getId());

                        returnArray.put(statusJson);
                        statusJson = null;
                    }
                }
                else {

                    othersSpendDbl = othersSpendDbl + chartData.getValue();

                    if(i==dataList.size()-1)
                    {
                        statusJson = new JSONObject();
                        statusJson.put("name", "Others");
                        statusJson.put("value", othersSpendDbl);
                        statusJson.put("id", "0");

                        returnArray.put(statusJson);
                        statusJson = null;
                    }

                }


            }*/
            returnJson.put("values", returnArray);
        }
        return returnJson;
    }

    public static JSONObject prepareOverallSpendJsonForChart(List<CommonValuesForChartDto> dataList) throws JSONException {
        JSONObject returnJson = new JSONObject();
        JSONArray returnArray = null;
        JSONObject statusJson = null;

        if (dataList != null && dataList.size() > 0) {
            returnArray = new JSONArray();
            for (CommonValuesForChartDto chartData : dataList) {
                if (chartData != null) {
                    statusJson = new JSONObject();
                    statusJson.put("name", chartData.getName());
                    statusJson.put("value", chartData.getValue());
                    //statusJson.put("id", chartData.getId());
                    statusJson.put("id", chartData.getName());

                    returnArray.put(statusJson);
                    statusJson = null;
                }
            }
            returnJson.put("values", returnArray);
        }
        return returnJson;
    }

    public static JSONObject prepareInvoiceMethodScoreJson(List<InvoiceMethodScoreDto> dataList) throws JSONException {
        JSONObject returnJson = new JSONObject();
        JSONArray returnArray = null;
        JSONObject statusJson = null;

        if (dataList != null && dataList.size() > 0) {
            returnArray = new JSONArray();
            for (InvoiceMethodScoreDto chartData : dataList) {
                if (chartData != null) {
                    statusJson = new JSONObject();
                    statusJson.put("name", chartData.getName());
                    statusJson.put("value", Math.round(chartData.getValue()));
                    statusJson.put("id", chartData.getInvoiceMethodId());

                    returnArray.put(statusJson);
                    statusJson = null;
                }
            }
            returnJson.put("values", returnArray);
        }
        return returnJson;
    }

    public static JSONObject prepareTopAccessorialSpendJson(List<AccessorialSpendDto> accessorialSpendList) throws JSONException {
        JSONObject returnObject = new JSONObject();
        JSONArray valuesArray = null;
        JSONArray seriesArray = null;
        LinkedHashMap<String, HashMap<String, Double>> datesValuesMap = null;
        ArrayList<String> serviceFlagList = null;

        if (accessorialSpendList != null) {
            valuesArray = new JSONArray();
            seriesArray = new JSONArray();
            datesValuesMap = new LinkedHashMap<String, HashMap<String, Double>>();
            serviceFlagList = new ArrayList<String>();

            for (AccessorialSpendDto accSpend : accessorialSpendList) {
                if (accSpend != null) {
                    String billDate = accSpend.getBillDate();
                    String service = accSpend.getAccessorialName();
                    Double spend = ((double) Math.round(accSpend.getSpend()));

                    if (spend != 0) {

                        if (!serviceFlagList.contains(service)) {
                            serviceFlagList.add(service);
                        }

                        if (datesValuesMap.containsKey(billDate)) {
                            datesValuesMap.get(billDate).put(service, spend);
                        } else {
                            HashMap<String, Double> tempHashMap = new HashMap<String, Double>();
                            tempHashMap.put(service, spend);
                            datesValuesMap.put(billDate, tempHashMap);
                        }

                    }
                }
            }

            // Bar Chart
            int counter = 1;
            Iterator<String> datesIterator = datesValuesMap.keySet().iterator();

            while (datesIterator.hasNext()) {
                JSONObject jsonObject = new JSONObject();

                String date = datesIterator.next();
                HashMap<String, Double> serviceFlagMap = datesValuesMap.get(date);

                Iterator<String> serviceFlagIterator = serviceFlagMap.keySet().iterator();

                jsonObject.put("name", date);
                jsonObject.put("counter", counter);

                while (serviceFlagIterator.hasNext()) {
                    String serviceFlag = serviceFlagIterator.next();
                    double spend = serviceFlagMap.get(serviceFlag);
                    jsonObject.put(serviceFlag, spend);
                }

                valuesArray.put(jsonObject);
                counter++;
            }

            String append = "\"";
            counter = 1;

            for (String serviceFlag : serviceFlagList) {
                serviceFlag = append + serviceFlag + append;
                String seriesId = append + "S" + counter + append;
                String object = "{\"id\":" + seriesId + ",\"name\":" + serviceFlag + ", \"data\": {\"field\":" + serviceFlag
                        + "},\"type\":\"line\",\"style\":{\"lineWidth\": 2,smoothing: true, marker: {shape: \"circle\", width: 5},";
                object = object + "lineColor: \"" + colorsList.get(counter - 1) + append;
                object = object + "}}";
                seriesArray.put(new JSONObject(object));
                counter++;
            }

            returnObject.put("values", valuesArray);
            returnObject.put("series", seriesArray);
        }
        return returnObject;
    }

    public static JSONObject prepareAverageWeightOrSpendJson(List<AverageSpendPerShipmentDto> avgPerShipmentList) throws JSONException {
        JSONObject returnObject = new JSONObject();
        JSONArray valuesArray = new JSONArray();
        JSONArray seriesArray = new JSONArray();
        LinkedHashMap<String, HashMap<String, Double>> datesValuesMap = new LinkedHashMap<String, HashMap<String, Double>>();
        ArrayList<String> modeFlagList = new ArrayList<String>();

        for (AverageSpendPerShipmentDto perShipmentDto : avgPerShipmentList) {
            String billDate = perShipmentDto.getBillingDate();
            String mode = perShipmentDto.getModes();
            Double spend = perShipmentDto.getNetWeight();

            if (spend != 0) {

                if (!modeFlagList.contains(mode)) {
                    modeFlagList.add(mode);
                }

                if (datesValuesMap.containsKey(billDate)) {
                    datesValuesMap.get(billDate).put(mode, spend);
                } else {
                    HashMap<String, Double> tempHashMap = new HashMap<String, Double>();
                    tempHashMap.put(mode, spend);
                    datesValuesMap.put(billDate, tempHashMap);
                }

            }

        }
        // Bar Chart
        int counter = 1;
        Iterator<String> datesIterator = datesValuesMap.keySet().iterator();

        while (datesIterator.hasNext()) {
            JSONObject jsonObject = new JSONObject();

            String date = datesIterator.next();
            HashMap<String, Double> modeFlagMap = datesValuesMap.get(date);
            Iterator<String> modeFlagIterator = modeFlagMap.keySet().iterator();

            jsonObject.put("name", date);
            jsonObject.put("counter", counter);

            while (modeFlagIterator.hasNext()) {
                String modeFlag = modeFlagIterator.next();
                double spend = modeFlagMap.get(modeFlag);
                jsonObject.put(modeFlag, spend);
            }
            valuesArray.put(jsonObject);
            counter++;
        }

        String append = "\"";
        counter = 1;
        for (String modeFlag : modeFlagList) {
            modeFlag = append + modeFlag + append;
            String seriesId = append + "S" + counter + append;
            String object = "{\"id\":" + seriesId + ",\"name\":" + modeFlag + ", \"data\": {\"field\":" + modeFlag
                    + "},\"type\":\"line\",\"style\":{\"lineWidth\": 2,smoothing: true, marker: {shape: \"circle\", width: 5},";
            object = object + "lineColor: \"" + colorsList.get(counter - 1) + "\"";
            object = object + "}}";
            seriesArray.put(new JSONObject(object));
            counter++;
        }
        returnObject.put("values", valuesArray);
        returnObject.put("series", seriesArray);

        return returnObject;
    }


    public static JSONObject prepareAverageWeightJson(List<AverageWeightModeShipmtDto> avgWeigthModeShpmtList) throws JSONException {
        JSONObject returnObject = new JSONObject();
        JSONArray valuesArray = new JSONArray();
        JSONArray seriesArray = new JSONArray();
        LinkedHashMap<String, HashMap<String, Double>> datesValuesMap = new LinkedHashMap<String, HashMap<String, Double>>();
        ArrayList<String> modeFlagList = new ArrayList<String>();

        for (AverageWeightModeShipmtDto perWeightShipmentDto : avgWeigthModeShpmtList) {
            String billDate = perWeightShipmentDto.getBillingDate();
            String mode = perWeightShipmentDto.getModes();
            Double spend = CommonUtil.round(perWeightShipmentDto.getNetWeight(), 1);

            if (spend != null && spend != 0) {
                if (!modeFlagList.contains(mode)) {
                    modeFlagList.add(mode);
                }

                if (datesValuesMap.containsKey(billDate)) {
                    datesValuesMap.get(billDate).put(mode, spend);
                } else {
                    HashMap<String, Double> tempHashMap = new HashMap<String, Double>();
                    tempHashMap.put(mode, spend);
                    datesValuesMap.put(billDate, tempHashMap);
                }

            }

        }
        // Bar Chart
        int counter = 1;
        Iterator<String> datesIterator = datesValuesMap.keySet().iterator();

        while (datesIterator.hasNext()) {
            JSONObject jsonObject = new JSONObject();

            String date = datesIterator.next();
            HashMap<String, Double> modeFlagMap = datesValuesMap.get(date);
            Iterator<String> modeFlagIterator = modeFlagMap.keySet().iterator();

            jsonObject.put("name", date);
            jsonObject.put("counter", counter);

            while (modeFlagIterator.hasNext()) {
                String modeFlag = modeFlagIterator.next();
                double spend = modeFlagMap.get(modeFlag);
                jsonObject.put(modeFlag, spend);
            }
            valuesArray.put(jsonObject);
            counter++;
        }

        String append = "\"";
        counter = 1;
        for (String modeFlag : modeFlagList) {
            modeFlag = append + modeFlag + append;
            String seriesId = append + "S" + counter + append;
            String object = "{\"id\":" + seriesId + ",\"name\":" + modeFlag + ", \"data\": {\"field\":" + modeFlag
                    + "},\"type\":\"line\",\"style\":{\"lineWidth\": 2,smoothing: true, marker: {shape: \"circle\", width: 5},";
            object = object + "lineColor: \"" + colorsList.get(counter - 1) + "\"";
            object = object + "}}";
            seriesArray.put(new JSONObject(object));
            counter++;
        }
        returnObject.put("values", valuesArray);
        returnObject.put("series", seriesArray);

        return returnObject;
    }

    public static JSONObject prepareServiceLevelUsageAndPerfromanceJson(List<ServiceLevelUsageAndPerformanceDto> performanceList) throws JSONException {
        JSONObject finalJsonObj = new JSONObject();
        JSONArray valuesArray = null;
        JSONArray seriesArray = null;

        if (performanceList != null && !performanceList.isEmpty()) {
            valuesArray = new JSONArray();
            seriesArray = new JSONArray();
            HashMap<String, Double> carriersValuesMap = new LinkedHashMap<String, Double>();
            Set<String> categories = new TreeSet<String>(Arrays.asList("DAY2", "DAY3", "GROUND", "INTL", "NDA", "POSTALINTG"));
            Set<String> attributes = new TreeSet<String>();

            for (ServiceLevelUsageAndPerformanceDto serviceLevelUsage : performanceList) {
                if (serviceLevelUsage != null) {
                    int c = 0;
                    for (String category : categories) {
                        String carrierName = serviceLevelUsage.getCarrierName();
                        if (c == 0) {
                            attributes.add(carrierName + "COUNT");
                            attributes.add(carrierName + "PERC");
                            attributes.add(carrierName + "LATEPERC");
                            c++;
                        }
                        String key = carrierName + "#@#" + category;
                        switch (category) {
                            case "DAY2":
                                carriersValuesMap.put(key + "#@#COUNT", serviceLevelUsage.getDay2Count());
                                carriersValuesMap.put(key + "#@#PERC", serviceLevelUsage.getDay2Percentage());
                                carriersValuesMap.put(key + "#@#LATEPERC", serviceLevelUsage.getDay2LatePercentage());
                                break;
                            case "DAY3":
                                carriersValuesMap.put(key + "#@#COUNT", serviceLevelUsage.getDay3Count());
                                carriersValuesMap.put(key + "#@#PERC", serviceLevelUsage.getDay3Percentage());
                                carriersValuesMap.put(key + "#@#LATEPERC", serviceLevelUsage.getDay3LatePercentage());
                                break;
                            case "GROUND":
                                carriersValuesMap.put(key + "#@#COUNT", serviceLevelUsage.getGroundCount());
                                carriersValuesMap.put(key + "#@#PERC", serviceLevelUsage.getGroundPercentage());
                                carriersValuesMap.put(key + "#@#LATEPERC", serviceLevelUsage.getGroundLatePercentage());
                                break;
                            case "INTL":
                                carriersValuesMap.put(key + "#@#COUNT", serviceLevelUsage.getInternationalCount());
                                carriersValuesMap.put(key + "#@#PERC", serviceLevelUsage.getInternationalPercentage());
                                carriersValuesMap.put(key + "#@#LATEPERC", serviceLevelUsage.getInternationalLatePercentage());
                                break;
                            case "NDA":
                                carriersValuesMap.put(key + "#@#COUNT", serviceLevelUsage.getNdaCount());
                                carriersValuesMap.put(key + "#@#PERC", serviceLevelUsage.getNdaPercentage());
                                carriersValuesMap.put(key + "#@#LATEPERC", serviceLevelUsage.getNdaLatePercentage());
                                break;
                            case "POSTALINTG":
                                carriersValuesMap.put(key + "#@#COUNT", serviceLevelUsage.getPostalIntgCount());
                                carriersValuesMap.put(key + "#@#PERC", serviceLevelUsage.getPostalIntgPercentage());
                                carriersValuesMap.put(key + "#@#LATEPERC", serviceLevelUsage.getPostalIntgLatePercentage());
                                break;
                        }
                    }
                }
            }

            Iterator<String> categoriesIterator = categories.iterator();

            while (categoriesIterator.hasNext()) {
                String service = categoriesIterator.next();
                JSONObject finalValues = new JSONObject();
                finalValues.put("name", service);
                Iterator<String> carrierIterator = carriersValuesMap.keySet().iterator();
                while (carrierIterator.hasNext()) {
                    String carrierMapKey = carrierIterator.next();
                    double amount = carriersValuesMap.get(carrierMapKey);
                    String carrierName = carrierMapKey.split("#@#")[0];
                    String serviceName = carrierMapKey.split("#@#")[1];
                    String columnName = carrierMapKey.split("#@#")[2];

                    if (service.equalsIgnoreCase(serviceName)) {
                        finalValues.put(carrierName + columnName, amount);
                    }
                }
                valuesArray.put(finalValues);
            }

            String append = "\"";
            int counter = 1;
            for (String seriesName : attributes) {
                String seriesId = append + "S" + counter + append;
                StringBuffer seriesObject = new StringBuffer();

                seriesObject.append("{\"id\":" + seriesId + ",\"name\":" + append + seriesName + append + ", \"data\": {\"field\":" + seriesName + "}");
                if (seriesName.contains("COUNT")) {
                    seriesObject.append(",\"type\":\"line\",\"style\":{\"lineWidth\": 2,depth: 4, gradient: 0.9 ,smoothing: true, marker: {shape: \"circle\", width: 5}, ");
                    seriesObject.append("lineColor: \"" + colorsList.get(counter - 1) + "\"");
                    seriesObject.append("}");
                } else {
                    seriesObject.append(",style: { depth: 4, gradient: 0.9 }");
                }

                seriesObject.append("}");
                seriesArray.put(new JSONObject(seriesObject.toString()));
                counter++;
            }
            finalJsonObj.put("values", valuesArray);
            finalJsonObj.put("series", seriesArray);
        } else {
            finalJsonObj.put("values", new JSONArray());
            finalJsonObj.put("series", new JSONArray());
        }
        return finalJsonObj;
    }

    public static JSONObject prepareInAndOutBuondJson(List<NetSpendCommonDto> spendList) throws JSONException {
        JSONObject returnObject = new JSONObject();
        JSONArray valuesArray = null;
        JSONArray seriesArray = null;

        if (spendList != null && !spendList.isEmpty()) {
            valuesArray = new JSONArray();
            seriesArray = new JSONArray();
            LinkedHashMap<String, HashMap<String, Double>> datesValuesMap = new LinkedHashMap<String, HashMap<String, Double>>();
            ArrayList<String> modeFlagList = new ArrayList<String>();
            ArrayList<String> carriersList = new ArrayList<String>();
            for (NetSpendCommonDto spendDto : spendList) {
                if (spendDto != null) {
                    String billDate = spendDto.getBillingDate();
                    String carrierScacCode = spendDto.getCarrierName();
                    Double spend = ((double) Math.round(spendDto.getNetDueAmount()));
                    Long carrierId = spendDto.getCarrierId();
                    String carrierIaAndName = carrierId + "#@#" + carrierScacCode;
                    if (!carriersList.contains(carrierIaAndName)) {
                        carriersList.add(carrierIaAndName);
                    }

                    if (spend != 0) {
                        if (!modeFlagList.contains(carrierScacCode)) {
                            modeFlagList.add(carrierScacCode);
                        }
                        if (datesValuesMap.containsKey(billDate)) {
                            datesValuesMap.get(billDate).put(carrierScacCode, spend);
                        } else {
                            HashMap<String, Double> tempHashMap = new HashMap<String, Double>();
                            tempHashMap.put(carrierScacCode, spend);
                            datesValuesMap.put(billDate, tempHashMap);
                        }
                    }
                }
            }

            // Bar Chart
            int counter = 1;
            Iterator<String> datesIterator = datesValuesMap.keySet().iterator();

            while (datesIterator.hasNext()) {
                JSONObject jsonObject = new JSONObject();

                String date = datesIterator.next();
                HashMap<String, Double> modeFlagMap = datesValuesMap.get(date);

                Iterator<String> modeFlagIterator = modeFlagMap.keySet().iterator();

                jsonObject.put("name", date);
                jsonObject.put("counter", counter);

                while (modeFlagIterator.hasNext()) {
                    String modeFlag = modeFlagIterator.next();
                    double spend = modeFlagMap.get(modeFlag);
                    jsonObject.put(modeFlag, spend);
                }

                valuesArray.put(jsonObject);
                counter++;
            }

            String append = "\"";
            counter = 1;

            for (String modeFlag : modeFlagList) {
                modeFlag = append + modeFlag + append;
                String seriesId = append + "S" + counter + append;
                String object = "{\"id\":" + seriesId + ",\"name\":" + modeFlag + ", \"data\": {\"field\":" + modeFlag
                        + "},\"type\":\"line\",\"style\":{\"lineWidth\": 2,smoothing: true, marker: {shape: \"circle\", width: 5},";
                object = object + "lineColor: \"" + colorsList.get(counter - 1) + "\"";
                object = object + "}}";
                seriesArray.put(new JSONObject(object));
                counter++;
            }

            counter = 1;

            for (String carrierDetails : carriersList) {
                String carrierId = carrierDetails.split("#@#")[0];
                String carrierName = carrierDetails.split("#@#")[1];

                carrierId = append + carrierId + append;
                carrierName = append + carrierName + append;
                String seriesId = append + "S" + counter + append;
                String object = "{\"id\":" + seriesId + ",\"name\":" + carrierName + ", \"carrierId\":" + carrierId + ",\"data\": {\"field\":" + carrierName
                        + "},\"type\":\"line\",\"style\":{\"lineWidth\": 2,smoothing: true, marker: {shape: \"circle\", width: 5},";

                object = object + "lineColor: \"" + colorsList.get(counter - 1) + "\"";
                object = object + "}}";

                seriesArray.put(new JSONObject(object));
                counter++;
            }

            returnObject.put("values", valuesArray);
            returnObject.put("series", seriesArray);
            returnObject.put("carrierDetails", new JSONArray().put(carriersList));
        }
        return returnObject;
    }

    public static JSONObject prepareOrderMatchJson(List<OrderMatchDto> orderMatchList) throws JSONException {
        JSONObject returnJson = new JSONObject();
        JSONArray returnArray = null;
        JSONObject statusJson = null;
        if (orderMatchList != null && orderMatchList.size() > 0) {
            returnArray = new JSONArray();
            statusJson = new JSONObject();
            statusJson.put("name", "Status");
            statusJson.put("id", "1");
            for (OrderMatchDto orderMatch : orderMatchList) {
                if (orderMatch != null) {
                    if ("Matched".equals(orderMatch.getStatus())) {
                        statusJson.put("Matched", orderMatch.getValue());
                    } else {
                        statusJson.put("UnMatched", orderMatch.getValue());
                    }
                }
            }

            returnArray.put(statusJson);
            returnJson.put("values", returnArray);
        }
        return returnJson;
    }

    public static JSONObject prepareBilledVsApprovedJson(List<BilledVsApprovedDto> billedVsApprovedList) throws JSONException {
        JSONObject returnJson = new JSONObject();
        JSONArray returnArray = new JSONArray();
        ;
        JSONObject statusJson = null;
        if (billedVsApprovedList != null && billedVsApprovedList.size() > 0) {
            for (BilledVsApprovedDto billedVsApproved : billedVsApprovedList) {
                if (billedVsApproved != null) {
                    statusJson = new JSONObject();
                    statusJson.put("id", billedVsApproved.getCarrierId());
                    statusJson.put("name", billedVsApproved.getCarrierName());
                    statusJson.put("Billed", Math.round(billedVsApproved.getBilledAmount()));
                    statusJson.put("Approved", Math.round(billedVsApproved.getApprovedAmount()));
                    statusJson.put("Recovered", Math.round(billedVsApproved.getRecoveredAmount()));

                    returnArray.put(statusJson);
                    statusJson = null;
                }
            }
            returnJson.put("values", returnArray);
        }
        return returnJson;
    }

    public static JSONObject prepareRecoveryAdjustmentJson(List<RecoveryAdjustmentDto> recoveryAdjustmentList) throws JSONException {
        JSONObject returnObject = new JSONObject();
        JSONArray valuesArray = null;
        JSONArray seriesArray = null;

        if (recoveryAdjustmentList != null && !recoveryAdjustmentList.isEmpty()) {
            valuesArray = new JSONArray();
            seriesArray = new JSONArray();
            LinkedHashMap<String, HashMap<String, Double>> monthsMap = new LinkedHashMap<String, HashMap<String, Double>>();
            ArrayList<String> servicesList = new ArrayList<String>();
            for (RecoveryAdjustmentDto recoveryAdjustment : recoveryAdjustmentList) {
                if (recoveryAdjustment != null) {
                    String month = recoveryAdjustment.getMonth();
                    String service = recoveryAdjustment.getService();
                    Double spend = ((double) Math.round(recoveryAdjustment.getSpend()));

                    if (spend != 0) {
                        if (!servicesList.contains(service)) {
                            servicesList.add(service);
                        }

                        if (monthsMap.containsKey(month)) {
                            monthsMap.get(month).put(service, spend);
                        } else {
                            HashMap<String, Double> tempHashMap = new HashMap<String, Double>();
                            tempHashMap.put(service, spend);
                            monthsMap.put(month, tempHashMap);
                        }

                    }
                }
            }

            // Bar Chart
            int counter = 1;
            Iterator<String> monthsIterator = monthsMap.keySet().iterator();

            while (monthsIterator.hasNext()) {
                JSONObject jsonObject = new JSONObject();
                String month = monthsIterator.next();
                HashMap<String, Double> servicesMap = monthsMap.get(month);
                Iterator<String> serivcesIterator = servicesMap.keySet().iterator();

                jsonObject.put("name", month);
                jsonObject.put("counter", counter);

                while (serivcesIterator.hasNext()) {
                    String service = serivcesIterator.next();
                    double spend = servicesMap.get(service);
                    jsonObject.put(service, spend);
                }

                valuesArray.put(jsonObject);
                counter++;
            }

            String append = "\"";
            counter = 1;

            for (String service : servicesList) {
                service = append + service + append;
                String seriesId = append + "S" + counter + append;

                String object = "{\"id\":" + seriesId + ",\"name\":" + service + ", \"data\": {\"field\":" + service
                        + "},\"type\":\"line\",\"style\":{\"lineWidth\": 2,smoothing: true, marker: {shape: \"circle\", width: 5},";

                object = object + "lineColor: \"" + colorsList.get(counter - 1) + "\"";
                object = object + "}}";
                seriesArray.put(new JSONObject(object));
                counter++;
            }

            returnObject.put("values", valuesArray);
            returnObject.put("series", seriesArray);
        } else {
            returnObject.put("values", new JSONArray());
            returnObject.put("series", new JSONArray());
        }
        return returnObject;
    }

    public static JSONObject prepareTotalCreditRecoveryByServiceLevelJson(List<RecoveryServiceDto> recoveryServiceList) throws JSONException {
        JSONObject returnObject = new JSONObject();
        JSONArray valuesArray = null;
        JSONArray seriesArray = null;

        if (recoveryServiceList != null && !recoveryServiceList.isEmpty()) {
            valuesArray = new JSONArray();
            seriesArray = new JSONArray();
            Map<String, HashMap<String, Double>> servicesMap = new LinkedHashMap<String, HashMap<String, Double>>();
            Map<String, Long> carrierMap = new HashMap<String, Long>();
            ArrayList<String> carriersList = new ArrayList<String>();
            ArrayList<String> concatCarriersList = new ArrayList<String>();

            for (RecoveryServiceDto recoveryService : recoveryServiceList) {
                if (recoveryService != null) {
                    String service = recoveryService.getBucketType();
                    String carrierName = recoveryService.getCarrierName();
                    Long carrierId = recoveryService.getCarrierId();
                    Double spend = ((double) Math.round(recoveryService.getCreditAmount()));
                    carrierMap.put(carrierName, carrierId);
                    if (spend != null && spend != 0) {
                        String concatCarrier = carrierId + "#@#" + carrierName;
                        if (!concatCarriersList.contains(concatCarrier)) {
                            concatCarriersList.add(concatCarrier);
                        }

                        if (!carriersList.contains(carrierName)) {
                            carriersList.add(carrierName);
                        }

                        if (servicesMap.containsKey(service)) {
                            servicesMap.get(service).put(carrierName, spend);
                        } else {
                            HashMap<String, Double> tempHashMap = new HashMap<String, Double>();
                            tempHashMap.put(carrierName, spend);
                            servicesMap.put(service, tempHashMap);
                        }

                    }
                }
            }

            // Bar Chart
            int counter = 1;
            Iterator<String> servicesIterator = servicesMap.keySet().iterator();

            while (servicesIterator.hasNext()) {
                JSONObject jsonObject = new JSONObject();

                String service = servicesIterator.next();
                HashMap<String, Double> carriersMap = servicesMap.get(service);

                Iterator<String> carriersIterator = carriersMap.keySet().iterator();

                jsonObject.put("name", service);
                jsonObject.put("counter", counter);

                while (carriersIterator.hasNext()) {
                    String carrierName = carriersIterator.next();
                    double spend = carriersMap.get(carrierName);
                    jsonObject.put(carrierName, spend);
                }

                valuesArray.put(jsonObject);
                counter++;
            }

            String append = "\"";
            counter = 1;

            for (String carrier : carriersList) {
                String carrierName = carrier;
                carrier = append + carrier + append;
                String seriesId = append + "S" + counter + append;

                String object = "{\"id\":" + seriesId + ",\"name\":" + carrier + ", \"data\": {\"field\":" + carrier + ",\"carrierId\" : " + carrierMap.get(carrierName) + "},style: { depth: 4, gradient: 0.9 }}";
                seriesArray.put(new JSONObject(object));
                counter++;
            }

            returnObject.put("values", valuesArray);
            returnObject.put("series", seriesArray);
            returnObject.put("carrierDetails", new JSONArray().put(concatCarriersList));
        } else {
            returnObject.put("values", new JSONArray());
            returnObject.put("series", new JSONArray());
            returnObject.put("carrierDetails", new JSONArray());
        }
        return returnObject;
    }

    public static JSONObject preparePackageExceptionJson(List<PackageExceptionDto> packageExceptionList) throws JSONException {
        JSONObject returnObject = new JSONObject();

        if (packageExceptionList != null && !packageExceptionList.isEmpty()) {
            JSONArray valuesArray = new JSONArray();
            JSONArray seriesArray = new JSONArray();
            LinkedHashMap<String, HashMap<String, Integer>> datesValuesMap = new LinkedHashMap<String, HashMap<String, Integer>>();
            ArrayList<String> deliveryFlagList = new ArrayList<String>();

            for (PackageExceptionDto packageException : packageExceptionList) {
                if (packageException != null) {
                    String billDate = packageException.getBillingDate();
                    String deliveryFlag = packageException.getDeliveryFlag();
                    Integer spend = packageException.getDeliveryFlagCount();

                    if (spend != 0) {

                        if ("NoPods".equalsIgnoreCase(deliveryFlag)) {
                            deliveryFlag = "No POD";
                        }

                        if (!deliveryFlagList.contains(deliveryFlag)) {
                            deliveryFlagList.add(deliveryFlag);
                        }

                        if (datesValuesMap.containsKey(billDate)) {
                            datesValuesMap.get(billDate).put(deliveryFlag, spend);
                        } else {
                            HashMap<String, Integer> tempHashMap = new HashMap<String, Integer>();
                            tempHashMap.put(deliveryFlag, spend);
                            datesValuesMap.put(billDate, tempHashMap);
                        }

                    }
                }
            }
            // Bar Chart
            int counter = 1;
            Iterator<String> datesIterator = datesValuesMap.keySet().iterator();

            while (datesIterator.hasNext()) {
                JSONObject jsonObject = new JSONObject();

                String date = datesIterator.next();
                HashMap<String, Integer> deliveryFlagMap = datesValuesMap.get(date);

                Iterator<String> deliveryFlagIterator = deliveryFlagMap.keySet().iterator();

                jsonObject.put("name", date);
                jsonObject.put("counter", counter);

                while (deliveryFlagIterator.hasNext()) {
                    String deliveryFlag = deliveryFlagIterator.next();
                    double spend = deliveryFlagMap.get(deliveryFlag);
                    jsonObject.put(deliveryFlag, spend);
                }

                valuesArray.put(jsonObject);
                counter++;
            }

            String append = "\"";
            counter = 1;

            for (String deliveryFlag : deliveryFlagList) {
                deliveryFlag = append + deliveryFlag + append;
                String seriesId = append + "S" + counter + append;
                String object = "{\"id\":" + seriesId + ",\"name\":" + deliveryFlag + ", \"data\": {\"field\":" + deliveryFlag + "},";

                if ("\"LATE\"".equalsIgnoreCase(deliveryFlag)) {
                    object = object + " style: { depth: 4, gradient: 0.9 ,fillColor: \"#673FB4\" }}";
                }
                if ("\"MNS\"".equalsIgnoreCase(deliveryFlag)) {
                    object = object + " style: { depth: 4, gradient: 0.9 ,fillColor: \"#2B98F0\" }}";
                }
                if ("\"No POD\"".equalsIgnoreCase(deliveryFlag)) {
                    object = object + " style: { depth: 4, gradient: 0.9 ,fillColor: \"#FF9801\" }}";
                }
                seriesArray.put(new JSONObject(object));
                counter++;
            }

            returnObject.put("values", valuesArray);
            returnObject.put("series", seriesArray);
        } else {
            returnObject.put("values", new JSONArray());
            returnObject.put("series", new JSONArray());
        }
        return returnObject;
    }

    public static JSONObject prepareShipmentByRegionLanesJson(List<ShipmentRegionDto> shipmentRegionDtoList) throws Exception {

        JSONObject resultData = new JSONObject();
        JSONArray linksArray = new JSONArray();
        JSONObject linksObject = null;
        JSONArray addressArray = new JSONArray();


        for (ShipmentRegionDto shipmentRegionDto : shipmentRegionDtoList) {
            String shipperCity = shipmentRegionDto.getShipperCity().replaceAll("\\s+", " ");
            String receiverCity = shipmentRegionDto.getReceiverCity().replaceAll("\\s+", " ");
            String shipperAddress = shipmentRegionDto.getShipperAddress().toUpperCase();
            String receiverAddress = shipmentRegionDto.getReceiverAddress().toUpperCase();

            boolean matched = false;
            for (int i = 0; i < linksArray.length(); i++) {
                JSONObject jsonobject = linksArray.getJSONObject(i);
                if (jsonobject.get("from").toString().equalsIgnoreCase(shipperCity) && jsonobject.get("to").toString().equalsIgnoreCase(receiverCity)) {
                    matched = true;
                    int count = Integer.parseInt(jsonobject.get("count").toString());
                    count = count + shipmentRegionDto.getLaneCount();
                    jsonobject.remove("count");
                    jsonobject.put("count", count);
                    linksArray.put(i, jsonobject);
                    break;
                }
            }
            if (matched == false) {
                linksObject = new JSONObject();
                linksObject.put("from", shipperCity);
                linksObject.put("to", receiverCity);
                linksObject.put("count", shipmentRegionDto.getLaneCount());
            }

            addressArray.put(shipperAddress);
            addressArray.put(receiverAddress);

            linksArray.put(linksObject);
        }

        resultData.put("links", linksArray);
        resultData.put("addressList", addressArray);

        return resultData;
    }

    public static JSONObject prepareShipmentByRegionNodesJson(Set<MapCoordinatesDto> mapCoordinatesDtoList, JSONObject resultJsonData) throws Exception {
        int counter = 0;
        JSONArray nodesArray = new JSONArray();

        for (MapCoordinatesDto mapCoordinatesDto : mapCoordinatesDtoList) {

            JSONArray longLatJsonArray = new JSONArray();
            JSONObject nodeObj = new JSONObject();

            if (counter == 0) {
                resultJsonData.put("longitude", mapCoordinatesDto.getLatitude());
                resultJsonData.put("latitude", mapCoordinatesDto.getLongitude());
            }

            longLatJsonArray.put(mapCoordinatesDto.getLongitude());
            longLatJsonArray.put(mapCoordinatesDto.getLatitude());

            nodeObj.put("id", mapCoordinatesDto.getAddress().split(",")[0]);
            nodeObj.put("coordinates", longLatJsonArray);
            nodesArray.put(nodeObj);

            counter++;
        }

        JSONArray uniqueNodesArray = new JSONArray();
        boolean uniqueFound = false;
        for (int i=0; i<nodesArray.length() ; i++ ) {
            uniqueFound = false;
            for (int j = 0; j < uniqueNodesArray.length(); j++ ) {
                if ( nodesArray.getJSONObject(i).getString("id").equals(uniqueNodesArray.getJSONObject(j).getString("id"))) {
                    uniqueFound = true;
                    break;
                }
            }

            if ( !uniqueFound ) {
                uniqueNodesArray.put(nodesArray.getJSONObject(i));
            }
        }

        resultJsonData.put("nodes", uniqueNodesArray);

        return resultJsonData;
    }

    public static JSONObject prepareMonthlyChartJson(List<CommonMonthlyChartDto> monthlyChartDtoList) throws JSONException {
        return prepareMonthlyChartJson(monthlyChartDtoList, false);
    }

    public static JSONObject prepareMonthlyChartJson(List<CommonMonthlyChartDto> monthlyChartDtoList, boolean manipulateAmount) throws JSONException {
        JSONObject returnJson = new JSONObject();
        JSONArray returnArray = null;
        int count = 0;
        long fromDate = 0;
        long toDate = 0;

        if (monthlyChartDtoList != null && monthlyChartDtoList.size() > 0) {
            returnArray = new JSONArray();
            for (CommonMonthlyChartDto monthlyChartDto : monthlyChartDtoList) {
                if (monthlyChartDto != null) {
                    JSONArray dataArray = new JSONArray();
                    long dateInMilliSecs = 0L;
                    if (monthlyChartDto.getBillDate() != null) {
                        dateInMilliSecs = monthlyChartDto.getBillDate().getTime();
                    }

                    dataArray.put(dateInMilliSecs);
                    dataArray.put(manipulateAmount ? Math.round(monthlyChartDto.getAmount()) : monthlyChartDto.getAmount());
                    returnArray.put(dataArray);
                    if (count == 0) {
                        fromDate = dateInMilliSecs;
                    }
                    toDate = dateInMilliSecs;
                    dataArray = null;

                    count++;
                }
            }
            if (fromDate == toDate) {
                toDate = toDate + 1;
            }

            returnJson.put("values", returnArray);
            returnJson.put("fromDate", fromDate);
            returnJson.put("toDate", toDate);
        }
        return returnJson;
    }

    public static JSONObject prepareTopShippingLanesJson(List<ShippingLanesDto> shippingLanesDtoList) throws JSONException {
        JSONObject returnJson = new JSONObject();
        JSONArray lanesArray = new JSONArray();
        for (ShippingLanesDto shippingLanesDto : shippingLanesDtoList) {
            JSONObject laneInfoJson = new JSONObject();
            laneInfoJson.put("rank", shippingLanesDto.getRank());
            laneInfoJson.put("shipperAddress", shippingLanesDto.getShipperAddress());
            laneInfoJson.put("receiverAddress", shippingLanesDto.getReceiverAddress());
            laneInfoJson.put("laneTotal", CommonUtil.toDecimalFormat(shippingLanesDto.getLaneTotal(), "#,###"));

            lanesArray.put(laneInfoJson);
        }

        returnJson.put("data", lanesArray);

        return returnJson;
    }

    public static JSONObject prepareTopPortLanesJson(List<PortLanesDto> shippingLanesDtoList) throws JSONException {
        JSONObject returnJson = new JSONObject();
        JSONArray lanesArray = new JSONArray();
        for (PortLanesDto portLanesDto : shippingLanesDtoList) {
            JSONObject laneInfoJson = new JSONObject();
            laneInfoJson.put("rank", portLanesDto.getRank());
            laneInfoJson.put("pol", portLanesDto.getPol());
            laneInfoJson.put("pod", portLanesDto.getPod());
            laneInfoJson.put("laneTotal", CommonUtil.toDecimalFormat(portLanesDto.getLaneTotal(), "#,###"));

            lanesArray.put(laneInfoJson);
        }

        returnJson.put("data", lanesArray);

        return returnJson;
    }

    public static JSONObject prepareParcelAccountSummaryJson(List<AccountSummaryDto> accountSummaryList, DashboardsFilterCriteria filter) throws JSONException {
        JSONObject resultJsonObj = new JSONObject();
        if (accountSummaryList != null && !accountSummaryList.isEmpty()) {
            int counter = 1;
            Map<String, HashMap<String, BigDecimal>> yearsBasedMap = new LinkedHashMap<String, HashMap<String, BigDecimal>>();
            ArrayList<String> yearsList = new ArrayList<String>();
            ArrayList<String> categoriesList = new ArrayList<String>();
            String toDateYear = null;

            if (filter.getToDate() != null && filter.getToDate().split("-").length > 2) {
                toDateYear = filter.getToDate().split("-")[2];
            } else {
                throw new RuntimeException("Invalid From Date");
            }
            int fromDatetYear = Integer.valueOf(toDateYear) - 1;

            yearsList.add(fromDatetYear + "");
            yearsList.add(toDateYear + "");

            categoriesList.add("Small Package Spend");
            categoriesList.add("Recovery");

            for (AccountSummaryDto accountSummary : accountSummaryList) {
                if (accountSummary != null) {
                    String year = accountSummary.getBillYear();
                    int isLtl = accountSummary.getLtl();
                    BigDecimal amount = new BigDecimal(accountSummary.getAmount() != null ? Math.round(accountSummary.getAmount().doubleValue()) : 0d);
                    String category = accountSummary.getCategory();

                    if (isLtl == 0 && category.contains("Spend")) {
                        category = "Small Package Spend";
                    } else if (category.contains("Recovery")) {
                        category = "Recovery";
                    }

                    if (yearsBasedMap.containsKey(year)) {
                        yearsBasedMap.get(year).put(category, amount);
                    } else {
                        HashMap<String, BigDecimal> tempHashMap = new HashMap<String, BigDecimal>();
                        tempHashMap.put(category, amount);
                        yearsBasedMap.put(year, tempHashMap);
                    }
                }
            }

            JSONArray finalValuesArray = new JSONArray();
            boolean isNoData = true;

            for (String year : yearsList) {
                JSONObject jsonObject = new JSONObject();
                if (yearsBasedMap.containsKey(year)) {
                    jsonObject.put("name", year);
                    jsonObject.put("counter", counter);

                    if (yearsBasedMap.get(year).containsKey(categoriesList.get(0))) {
                        jsonObject.put(categoriesList.get(0), yearsBasedMap.get(year).get(categoriesList.get(0)));
                        isNoData = false;
                    } else {
                        jsonObject.put(categoriesList.get(0), "0");
                    }

                    if (yearsBasedMap.get(year).containsKey(categoriesList.get(1))) {
                        jsonObject.put(categoriesList.get(1), yearsBasedMap.get(year).get(categoriesList.get(1)));
                        isNoData = false;
                    } else {
                        jsonObject.put(categoriesList.get(1), "0");
                    }

                } else {
                    jsonObject.put("name", year);
                    jsonObject.put("counter", counter);
                    jsonObject.put(categoriesList.get(0), "No Data");
                    jsonObject.put(categoriesList.get(1), "No Data");
                }
                counter++;
                finalValuesArray.put(jsonObject);
            }

            String append = "\"";
            counter = 1;
            JSONArray seriesArray = new JSONArray();
            for (String category : categoriesList) {
                category = append + category + append;
                String seriesId = append + "S" + counter + append;
                String object = "{\"id\":" + seriesId + ",\"name\":" + category + ", \"data\": {\"field\":" + category + "},";

                if ("\"Small Package Spend\"".equalsIgnoreCase(category)) {
                    object = object + " style: { depth: 4, gradient: 0.9 ,fillColor: \"#673FB4\" }}";
                }
                if ("\"Recovery\"".equalsIgnoreCase(category)) {
                    object = object + " style: { depth: 4, gradient: 0.9 ,fillColor: \"#2B98F0\" }}";
                }
                seriesArray.put(new JSONObject(object));
                counter++;
            }

            resultJsonObj.put("series", seriesArray);
            resultJsonObj.put("values", finalValuesArray);
            resultJsonObj.put("isNoData", isNoData);
        }
        return resultJsonObj;
    }

    public static JSONObject prepareAccountSummaryJson(List<AccountSummaryDto> accountSummaryList, DashboardsFilterCriteria filter) throws JSONException {
        JSONObject resultJsonObj = new JSONObject();

        if (accountSummaryList != null && !accountSummaryList.isEmpty()) {
            Map<String, LinkedHashMap<String, BigDecimal>> categoriesBasedMap = new LinkedHashMap<String, LinkedHashMap<String, BigDecimal>>();
            ArrayList<String> yearsList = new ArrayList<String>();
            ArrayList<String> categoriesList = new ArrayList<String>();
            String toDateYear = null;
            Map<String, BigDecimal> totalSpendMap = new HashMap<>();

            if (filter.getToDate() != null && filter.getToDate().split("-").length > 2) {
                toDateYear = filter.getToDate().split("-")[2];
            } else {
                throw new RuntimeException("Invalid From Date");
            }

            int fromDatetYear = Integer.valueOf(toDateYear) - 1;

            yearsList.add(fromDatetYear + "");
            yearsList.add(toDateYear + "");

            categoriesList.add("Small Package Spend");
            categoriesList.add("Freight Spend");
            categoriesList.add("Total Spend");
            categoriesList.add("Small Package Recovery");

            for (AccountSummaryDto accountSummary : accountSummaryList) {
                if (accountSummary != null) {
                    String year = accountSummary.getBillYear();
                    int isLtl = accountSummary.getLtl();
                    BigDecimal amount = accountSummary.getAmount();
                    String category = accountSummary.getCategory();

                    if (isLtl == 0 && category.contains("Spend")) {
                        category = "Small Package Spend";
                    } else if (isLtl == 1 && category.contains("Spend")) {
                        category = "Freight Spend";
                    } else if (category.contains("Recovery")) {
                        category = "Small Package Recovery";
                    }

                    if (categoriesBasedMap.containsKey(category)) {
                        if (!categoriesBasedMap.get(category).containsKey(year)) {
                            categoriesBasedMap.get(category).put(year, amount);
                        } else {
                            categoriesBasedMap.get(category).get(year).add(amount);
                        }
                    } else {
                        LinkedHashMap<String, BigDecimal> tempMap = new LinkedHashMap<String, BigDecimal>();
                        tempMap.put(year, amount);
                        categoriesBasedMap.put(category, tempMap);
                    }

                    if(amount != null && category.contains("Spend") && (isLtl == 0 || isLtl == 1)){
                        if(totalSpendMap.containsKey(year)){
                            BigDecimal total = amount.add(totalSpendMap.get(year));
                            totalSpendMap.put(year, total);
                        }else{
                            totalSpendMap.put(year, amount);
                        }
                    }
                }
            }
            JSONArray finalValuesArray = new JSONArray();

            for (String category : categoriesList) {
                JSONObject eachCategoryObj = new JSONObject();
                String key1 = "spend" + yearsList.get(0);
                String key2 = "spend" + yearsList.get(1);
                eachCategoryObj.put("name", category);

                if(category.equalsIgnoreCase("Total Spend")){
                    eachCategoryObj.put(key1, commaSeperatedDecimalFormat.format(totalSpendMap.get(yearsList.get(0))));
                    eachCategoryObj.put(key2, commaSeperatedDecimalFormat.format(totalSpendMap.get(yearsList.get(1))));
                    BigDecimal toatalSpend=totalSpendMap.get(yearsList.get(0)).add(totalSpendMap.get(yearsList.get(1)));
                    eachCategoryObj.put("spendTotal", commaSeperatedDecimalFormat.format(toatalSpend));
                }else{
                    if (categoriesBasedMap.containsKey(category)) {
                        if (categoriesBasedMap.get(category).containsKey(yearsList.get(0))) {
                            eachCategoryObj.put(key1, commaSeperatedDecimalFormat.format(categoriesBasedMap.get(category).get(yearsList.get(0))));
                        } else {
                            eachCategoryObj.put(key1, "0");
                        }

                        if (categoriesBasedMap.get(category).containsKey(yearsList.get(1))) {
                            eachCategoryObj.put(key2, commaSeperatedDecimalFormat.format(categoriesBasedMap.get(category).get(yearsList.get(1))));
                        } else {
                            eachCategoryObj.put(key2, "0");
                        }

                        Double d=Double.parseDouble(eachCategoryObj.getString(key1).replace(",",""))+Double.parseDouble(eachCategoryObj.getString(key2).replace(",",""));


                        eachCategoryObj.put("spendTotal",commaSeperatedDecimalFormat.format(d));

                    } else {
                        eachCategoryObj.put(key1, "0");
                        eachCategoryObj.put(key2, "0");
                        eachCategoryObj.put("spendTotal", "0");
                    }
                }
                finalValuesArray.put(eachCategoryObj);
            }

            JSONArray yearsJsonArray = new JSONArray();
            yearsJsonArray.put(yearsList.get(0));
            yearsJsonArray.put(yearsList.get(1));
            yearsJsonArray.put("Total");


            resultJsonObj.put("years", yearsJsonArray);
            resultJsonObj.put("values", finalValuesArray);
        }
        return resultJsonObj;
    }

    public static JSONObject prepareAnnualSummaryJson(List<AnnualSummaryDto> annualSummaryList) throws JSONException {
        JSONObject returnJson = new JSONObject();
        if (annualSummaryList != null && !annualSummaryList.isEmpty()) {
            double subTotalSpend = 0.0;
            Map<String, HashMap<String, HashMap<String, Double>>> modesMap = new LinkedHashMap<>();
            ArrayList<String> modesList = new ArrayList<>();
            ArrayList<String> quatersList = new ArrayList<>();
            Map<String, Double> quarterlySpend = new HashMap<String, Double>();
            Map<String, Integer> quarterlyTotalShipment = new HashMap<String, Integer>();

            for (AnnualSummaryDto annualSummary : annualSummaryList) {
                if (annualSummary != null) {
                    String quater = annualSummary.getQuarter();
                    String mode = annualSummary.getModes();
                    double spend = (null == annualSummary.getSpend() ? 0 : annualSummary.getSpend());
                    double noOfShipments = (null == annualSummary.getNoOfShipments() ? 0 : annualSummary.getNoOfShipments());
                    subTotalSpend += spend;

                    if (!modesList.contains(mode)) {
                        modesList.add(mode);
                    }

                    if (!quatersList.contains(quater)) {
                        quatersList.add(quater);
                    }

                    if (modesMap.containsKey(mode)) {

                        HashMap<String, HashMap<String, Double>> modesDataMap = modesMap.get(mode);

                        if (!modesDataMap.containsKey(quater)) {
                            HashMap<String, Double> eachQuaterData = new HashMap<>();

                            eachQuaterData.put("spend", spend);
                            eachQuaterData.put("noOfShipments", noOfShipments);
                            eachQuaterData.put("total", noOfShipments != 0 ? spend / noOfShipments : 0);

                            modesDataMap.put(quater, eachQuaterData);
                        } else {
                            HashMap<String, Double> eachQuaterData = modesDataMap.get(quater);
                            eachQuaterData.put("spend", eachQuaterData.get("spend") + spend);
                            eachQuaterData.put("noOfShipments", eachQuaterData.get("noOfShipments") + noOfShipments);
                            eachQuaterData.put("total", eachQuaterData.get("noOfShipments") != 0 ? eachQuaterData.get("spend") / eachQuaterData.get("noOfShipments") : 0);
                        }
                    } else {

                        HashMap<String, HashMap<String, Double>> quatersWiseMap = new HashMap<>();
                        HashMap<String, Double> eachQuaterData = new HashMap<>();

                        eachQuaterData.put("spend", spend);
                        eachQuaterData.put("noOfShipments", noOfShipments);
                        eachQuaterData.put("total", noOfShipments != 0 ? spend / noOfShipments : 0);

                        quatersWiseMap.put(quater, eachQuaterData);
                        modesMap.put(mode, quatersWiseMap);
                    }

                    if (quarterlySpend.containsKey(quater)) {
                        double totalSpend = quarterlySpend.get(quater);
                        totalSpend += spend;
                        quarterlySpend.put(quater, totalSpend);
                    } else {
                        quarterlySpend.put(quater, spend);
                    }

                    if(quarterlyTotalShipment.containsKey(quater)){
                        Integer totalShipment = quarterlyTotalShipment.get(quater);
                        totalShipment += annualSummary.getNoOfShipments();
                        quarterlyTotalShipment.put(quater, totalShipment);
                    }else{
                        quarterlyTotalShipment.put(quater, (null == annualSummary.getNoOfShipments() ? 0 : annualSummary.getNoOfShipments()));
                    }
                }
            }

            JSONObject finalObject = new JSONObject();
            JSONArray modesArray = new JSONArray();

            for (String mode : modesList) {
                JSONObject modeWiseDataObj = new JSONObject();
                HashMap<String, HashMap<String, Double>> quatersWiseMap = modesMap.get(mode);

                JSONObject allQuatersDataObj = new JSONObject();
                Double spendTotal=0.0;
                int shipmentsTotal=0;
                Double averageTotal=0.0;
                for (String quater : quatersList) {

                    JSONObject quaterInnerDataObj = new JSONObject();
                    if (quatersWiseMap.containsKey(quater)) {
                        HashMap<String, Double> eachQuaterData = quatersWiseMap.get(quater);
                        quaterInnerDataObj.put("spend", commaSeperatedDecimalFormat.format(eachQuaterData.get("spend")));
                        quaterInnerDataObj.put("noOfShipments", commaSeperatedDecimalFormat.format(eachQuaterData.get("noOfShipments")));
                        quaterInnerDataObj.put("total", CommonUtil.decimalNumberToCommaReadableFormat(eachQuaterData.get("total")));
                        if (quarterlySpend.containsKey(quater)) {
                            quaterInnerDataObj.put("perc",
                                    commaSeperatedDecimalFormat.format(quarterlySpend.get(quater) != 0 ? (eachQuaterData.get("spend") / quarterlySpend.get(quater)) * 100 : 0) + "%");
                        } else {
                            quaterInnerDataObj.put("perc", "0%");
                        }
                    } else {
                        quaterInnerDataObj.put("spend", "0");
                        quaterInnerDataObj.put("noOfShipments", "0");
                        quaterInnerDataObj.put("total", "0.00");
                        quaterInnerDataObj.put("perc", "0%");
                    }

                    spendTotal=spendTotal+Double.parseDouble(quaterInnerDataObj.getString("spend").replace(",",""));
                    shipmentsTotal=shipmentsTotal+Integer.parseInt(quaterInnerDataObj.getString("noOfShipments").replace(",",""));
                    averageTotal=averageTotal+Double.parseDouble(quaterInnerDataObj.getString("total").replace(",",""));

                    allQuatersDataObj.put(quater, quaterInnerDataObj);
                }
                JSONObject rowTotalJson=new JSONObject();
                rowTotalJson.put("totalSpend",commaSeperatedDecimalFormat.format(spendTotal));
                rowTotalJson.put("totalShipments",commaSeperatedDecimalFormat.format(shipmentsTotal));
                rowTotalJson.put("totalAverage",CommonUtil.decimalNumberToCommaReadableFormat(averageTotal));
                rowTotalJson.put("totalPercentage",commaSeperatedDecimalFormat.format(subTotalSpend != 0 ?(spendTotal / subTotalSpend) * 100 : 0) + "%");

                JSONObject json=new JSONObject();
                modeWiseDataObj.put(mode, json.put("quaters", allQuatersDataObj));
                modeWiseDataObj.put(mode, json.put("rowTotal", rowTotalJson));
                modesArray.put(modeWiseDataObj);
            }

            JSONObject quartersJson = new JSONObject();
            Double grandSpendTotal=0.0;
            Integer grandShipmentsTotal=0;
            for(Map.Entry<String, Double> spendMap : quarterlySpend.entrySet()){
                if(spendMap != null){
                    JSONObject total = new JSONObject();
                    Double totalSpend = spendMap.getValue();
                    grandSpendTotal=grandSpendTotal+totalSpend;
                    Integer totalShipment = quarterlyTotalShipment.get(spendMap.getKey());
                    grandShipmentsTotal=grandShipmentsTotal+totalShipment;
                    total.put("spend", commaSeperatedDecimalFormat.format(totalSpend));
                    total.put("noOfShipments", commaSeperatedDecimalFormat.format(totalShipment));
                    total.put("perc", "");
                    total.put("total", totalShipment != null && totalShipment != 0 ? CommonUtil.decimalNumberToCommaReadableFormat(totalSpend / totalShipment) : "0.00");
                    quartersJson.put(spendMap.getKey(), total);
                }
            }

            JSONObject rowTotalJson=new JSONObject();
            rowTotalJson.put("totalSpend",commaSeperatedDecimalFormat.format(grandSpendTotal));
            rowTotalJson.put("totalShipments",commaSeperatedDecimalFormat.format(grandShipmentsTotal));
            rowTotalJson.put("totalAverage",grandShipmentsTotal != null && grandShipmentsTotal != 0 ? CommonUtil.decimalNumberToCommaReadableFormat(grandSpendTotal / grandShipmentsTotal) : "0.00");
            rowTotalJson.put("totalPercentage","");
            JSONObject totalSpendJson = new JSONObject();
            JSONObject quarterJson = new JSONObject();
            totalSpendJson.put("Total", quarterJson.put("quaters", quartersJson));
            totalSpendJson.put("Total", quarterJson.put("rowTotal", rowTotalJson));
            if(modesArray != null){
                modesArray.put(totalSpendJson);
            }
            JSONArray quatersArray = new JSONArray();
            for (String quater : quatersList) {
                quatersArray.put(quater);
            }
           // quatersArray.put("Total");

            finalObject.put("modes", modesArray);
            finalObject.put("quaters", quatersArray);
            returnJson.put("values", finalObject);
        }
        return returnJson;
    }

    public static JSONObject prepareAnnualSummaryByServiceJson(List<AnnualSummaryDto> annualSummaryList) throws JSONException {
        JSONObject returnJson = new JSONObject();
        if (annualSummaryList != null && !annualSummaryList.isEmpty()) {
            double subTotalSpend = 0.0;
            Map<String, HashMap<String, HashMap<String, Double>>> servicesMap = new LinkedHashMap<>();
            ArrayList<String> servicesList = new ArrayList<>();
            ArrayList<String> quatersList = new ArrayList<>();
            Map<String, Double> quaterlyWiseSpend = new HashMap<String, Double>();
            Map<String, Integer> quarterlyTotalShipment = new HashMap<String, Integer>();

            for (AnnualSummaryDto annualSummary : annualSummaryList) {
                if (annualSummary != null) {
                    String quater = annualSummary.getQuarter();
                    String service = annualSummary.getService();
                    double spend = annualSummary.getSpend();
                    double noOfShipments = annualSummary.getNoOfShipments();
                    subTotalSpend += spend;

                    if (!servicesList.contains(service)) {
                        servicesList.add(service);
                    }
                    if (!quatersList.contains(quater)) {
                        quatersList.add(quater);
                    }
                    if (servicesMap.containsKey(service)) {
                        HashMap<String, HashMap<String, Double>> servicesDataMap = servicesMap.get(service);
                        if (!servicesDataMap.containsKey(quater)) {
                            HashMap<String, Double> eachQuaterData = new HashMap<>();

                            eachQuaterData.put("spend", spend);
                            eachQuaterData.put("noOfShipments", noOfShipments);
                            eachQuaterData.put("total", noOfShipments != 0 ? spend / noOfShipments : 0);
                            eachQuaterData.put("perc", noOfShipments != 0 ? spend / noOfShipments : 0);

                            servicesDataMap.put(quater, eachQuaterData);
                        } else {

                            HashMap<String, Double> eachQuaterData = servicesDataMap.get(quater);
                            eachQuaterData.put("spend", eachQuaterData.get("spend") + spend);
                            eachQuaterData.put("noOfShipments", eachQuaterData.get("noOfShipments") + noOfShipments);
                            eachQuaterData.put("total", eachQuaterData.get("noOfShipments") != 0 ? eachQuaterData.get("spend") / eachQuaterData.get("noOfShipments") : 0);
                            eachQuaterData.put("perc", eachQuaterData.get("noOfShipments") != 0 ? eachQuaterData.get("spend") / eachQuaterData.get("noOfShipments") : 0);
                        }
                    } else {
                        HashMap<String, HashMap<String, Double>> quatersWiseMap = new HashMap<>();
                        HashMap<String, Double> eachQuaterData = new HashMap<>();

                        eachQuaterData.put("spend", spend);
                        eachQuaterData.put("noOfShipments", noOfShipments);
                        eachQuaterData.put("total", noOfShipments != 0 ? spend / noOfShipments : 0);
                        eachQuaterData.put("perc", noOfShipments != 0 ? spend / noOfShipments : 0);

                        quatersWiseMap.put(quater, eachQuaterData);
                        servicesMap.put(service, quatersWiseMap);
                    }

                    if (quaterlyWiseSpend.containsKey(quater)) {
                        double totalSpend = quaterlyWiseSpend.get(quater);
                        totalSpend += spend;
                        quaterlyWiseSpend.put(quater, totalSpend);
                    } else {
                        quaterlyWiseSpend.put(quater, spend);
                    }

                    if(quarterlyTotalShipment.containsKey(quater)){
                        Integer totalShipment = quarterlyTotalShipment.get(quater);
                        totalShipment += annualSummary.getNoOfShipments();
                        quarterlyTotalShipment.put(quater, totalShipment);
                    }else{
                        quarterlyTotalShipment.put(quater, (null == annualSummary.getNoOfShipments() ? 0 : annualSummary.getNoOfShipments()));
                    }
                }
            }

            JSONObject finalObject = new JSONObject();
            JSONArray servicesArray = new JSONArray();

            for (String service : servicesList) {
                JSONObject serviceWiseDataObj = new JSONObject();
                Map<String, HashMap<String, Double>> quatersWiseMap = servicesMap.get(service);
                JSONObject allQuatersDataObj = new JSONObject();
                Double spendTotal=0.0;
                int shipmentsTotal=0;
                Double averageTotal=0.0;
                for (String quater : quatersList) {
                    JSONObject quaterInnerDataObj = new JSONObject();
                    if (quatersWiseMap.containsKey(quater)) {
                        Map<String, Double> eachQuaterData = quatersWiseMap.get(quater);
                        quaterInnerDataObj.put("spend", commaSeperatedDecimalFormat.format(eachQuaterData.get("spend")));
                        quaterInnerDataObj.put("noOfShipments", commaSeperatedDecimalFormat.format(eachQuaterData.get("noOfShipments")));
                        quaterInnerDataObj.put("total", CommonUtil.decimalNumberToCommaReadableFormat(eachQuaterData.get("total")));
                        if (quaterlyWiseSpend.containsKey(quater)) {
                            quaterInnerDataObj.put("perc", commaSeperatedDecimalFormat.format(quaterlyWiseSpend.get(quater) != 0 ? (eachQuaterData.get("spend") / quaterlyWiseSpend.get(quater)) * 100 : 0) + "%");
                        } else {
                            quaterInnerDataObj.put("perc", "0%");
                        }
                    } else {
                        quaterInnerDataObj.put("spend", "0");
                        quaterInnerDataObj.put("noOfShipments", "0");
                        quaterInnerDataObj.put("total", "0.00");
                        quaterInnerDataObj.put("perc", "0%");
                    }
                    spendTotal=spendTotal+Double.parseDouble(quaterInnerDataObj.getString("spend").replace(",",""));
                    shipmentsTotal=shipmentsTotal+Integer.parseInt(quaterInnerDataObj.getString("noOfShipments").replace(",",""));
                    averageTotal=averageTotal+Double.parseDouble(quaterInnerDataObj.getString("total").replace(",",""));

                    allQuatersDataObj.put(quater, quaterInnerDataObj);
                }
                JSONObject rowTotalJson=new JSONObject();
                rowTotalJson.put("totalSpend",commaSeperatedDecimalFormat.format(spendTotal));
                rowTotalJson.put("totalShipments",commaSeperatedDecimalFormat.format(shipmentsTotal));
                rowTotalJson.put("totalAverage", CommonUtil.decimalNumberToCommaReadableFormat(averageTotal));
                rowTotalJson.put("totalPercentage",commaSeperatedDecimalFormat.format(subTotalSpend != 0 ? (spendTotal / subTotalSpend) * 100 : 0) + "%");
                JSONObject json=new JSONObject();
                serviceWiseDataObj.put(service,  json.put("quaters", allQuatersDataObj));
                serviceWiseDataObj.put(service,  json.put("rowTotal", rowTotalJson));
                servicesArray.put(serviceWiseDataObj);
            }

            JSONObject quartersJson = new JSONObject();
            Double grandSpendTotal=0.0;
            Integer grandShipmentsTotal=0;
            for(Map.Entry<String, Double> spendMap : quaterlyWiseSpend.entrySet()){
                if(spendMap != null){
                    JSONObject total = new JSONObject();
                    Double totalSpend = spendMap.getValue();
                    grandSpendTotal=grandSpendTotal+totalSpend;
                    Integer totalShipment = quarterlyTotalShipment.get(spendMap.getKey());
                    grandShipmentsTotal=grandShipmentsTotal+totalShipment;
                    total.put("spend", commaSeperatedDecimalFormat.format(totalSpend));
                    total.put("noOfShipments", commaSeperatedDecimalFormat.format(totalShipment));
                    total.put("perc", "");
                    total.put("total", totalShipment != null && totalShipment != 0 ? CommonUtil.decimalNumberToCommaReadableFormat(totalSpend / totalShipment) : "0");
                    quartersJson.put(spendMap.getKey(), total);
                }
            }
            JSONObject rowTotalJson=new JSONObject();
            rowTotalJson.put("totalSpend",commaSeperatedDecimalFormat.format(grandSpendTotal));
            rowTotalJson.put("totalShipments",commaSeperatedDecimalFormat.format(grandShipmentsTotal));
            rowTotalJson.put("totalAverage",grandShipmentsTotal != null && grandShipmentsTotal != 0 ? CommonUtil.decimalNumberToCommaReadableFormat(grandSpendTotal / grandShipmentsTotal) : "0");
            rowTotalJson.put("totalPercentage","");
            JSONObject totalSpendJson = new JSONObject();
            JSONObject quarterJson = new JSONObject();
            totalSpendJson.put("Total", quarterJson.put("quaters", quartersJson));
            totalSpendJson.put("Total", quarterJson.put("rowTotal", rowTotalJson));
            if(servicesArray != null){
                servicesArray.put(totalSpendJson);
            }

            JSONArray quatersArray = new JSONArray();
            for (String quater : quatersList) {
                quatersArray.put(quater);
            }
            finalObject.put("services", servicesArray);
            finalObject.put("quaters", quatersArray);
            returnJson.put("values", finalObject);
        }
        return returnJson;
    }

    public static JSONObject prepareMonthlySpendByModeJson(List<MonthlySpendByModeDto> monthlySpendList) throws JSONException {
        JSONObject returnJson = new JSONObject();

        if (monthlySpendList != null && !monthlySpendList.isEmpty()) {
            Map<String, HashMap<String, HashMap<String, Double>>> modesMap = new LinkedHashMap<>();
            List<String> modesList = new ArrayList<String>();
            List<String> monthsList = new ArrayList<String>();
            Map<String, Double> monthlyTotalSpend = new HashMap<String, Double>();

            for (MonthlySpendByModeDto monthlySpend : monthlySpendList) {
                if (monthlySpend != null) {
                    String month = monthlySpend.getMonth();
                    String mode = monthlySpend.getModes();
                    Double spend = monthlySpend.getSpend();

                    if (!modesList.contains(mode)) {
                        modesList.add(mode);
                    }
                    if (!monthsList.contains(month)) {
                        monthsList.add(month);
                    }
                    if (modesMap.containsKey(mode)) {
                        HashMap<String, HashMap<String, Double>> modesDataMap = modesMap.get(mode);
                        if (!modesDataMap.containsKey(month)) {
                            HashMap<String, Double> eachMonthData = new HashMap<>();
                            eachMonthData.put("spend", spend);
                            modesDataMap.put(month, eachMonthData);
                        } else {
                            HashMap<String, Double> eachQuaterData = modesDataMap.get(month);
                            eachQuaterData.put("spend", eachQuaterData.get("spend") + spend);
                        }
                    } else {
                        HashMap<String, HashMap<String, Double>> monthsWiseMap = new HashMap<>();
                        HashMap<String, Double> eachMonthData = new HashMap<>();
                        eachMonthData.put("spend", spend);

                        monthsWiseMap.put(month, eachMonthData);
                        modesMap.put(mode, monthsWiseMap);
                    }

                    if (monthlyTotalSpend.containsKey(month)) {
                        double totalSpend = monthlyTotalSpend.get(month);
                        totalSpend += spend;
                        monthlyTotalSpend.put(month, totalSpend);
                    } else {
                        monthlyTotalSpend.put(month, spend);
                    }
                }
            }

            JSONObject finalObject = new JSONObject();
            JSONArray modesArray = new JSONArray();

            for (String mode : modesList) {
                JSONObject modeWiseDataObj = new JSONObject();
                HashMap<String, HashMap<String, Double>> monthsWiseMap = modesMap.get(mode);
                modeWiseDataObj.put("name", mode);
                Double  totalSpendForAllMonths=0.0;
                for (String month : monthsList) {
                    String spend = "0";
                    if (monthsWiseMap != null && monthsWiseMap.containsKey(month)) {
                        spend = commaSeperatedDecimalFormat.format(monthsWiseMap.get(month).get("spend"));
                    }
                    modeWiseDataObj.put(month, spend);
                    totalSpendForAllMonths=totalSpendForAllMonths+Double.parseDouble(spend.replace(",",""));
                }
                modeWiseDataObj.put("Total",commaSeperatedDecimalFormat.format(totalSpendForAllMonths));
                modesArray.put(modeWiseDataObj);
            }

            if(modesArray != null){
                JSONObject totalMonthlyJson = new JSONObject();
                Double grandTotalSpend=0.0;
                for(Map.Entry<String, Double> monthlyMap : monthlyTotalSpend.entrySet()){
                    if(monthlyMap != null){
                        totalMonthlyJson.put(monthlyMap.getKey(), commaSeperatedDecimalFormat.format(monthlyMap.getValue()));
                        grandTotalSpend=grandTotalSpend+monthlyMap.getValue();
                    }
                }
                totalMonthlyJson.put("Total",commaSeperatedDecimalFormat.format(grandTotalSpend));
                totalMonthlyJson.put("name", "Total");
                modesArray.put(totalMonthlyJson);
            }

            JSONArray monthsArray = new JSONArray();
            for (String month : monthsList) {
                monthsArray.put(month);
            }

            monthsArray.put("Total");

            finalObject.put("modes", modesArray);
            finalObject.put("quaters", monthsArray);
            returnJson.put("values", finalObject);
        }
        return returnJson;
    }

    public static JSONObject prepareMonthlySpendByModeByServiceJson(List<MonthlySpendByModeDto> monthlySpendList) throws JSONException {
        JSONObject returnJson = new JSONObject();

        if (monthlySpendList != null && !monthlySpendList.isEmpty()) {
            Map<String, HashMap<String, HashMap<String, Double>>> servicesMap = new LinkedHashMap<>();
            List<String> servicesList = new ArrayList<>();
            List<String> quatersList = new ArrayList<>();
            Map<String, Double> monthlyTotalSpend = new HashMap<String, Double>();

            for (MonthlySpendByModeDto monthlySpend : monthlySpendList) {
                if (monthlySpend != null) {
                    String month = monthlySpend.getMonth();
                    String service = monthlySpend.getService();
                    double spend = monthlySpend.getSpend();

                    if (!servicesList.contains(service)) {
                        servicesList.add(service);
                    }
                    if (!quatersList.contains(month)) {
                        quatersList.add(month);
                    }

                    if (servicesMap.containsKey(service)) {
                        HashMap<String, HashMap<String, Double>> servicesDataMap = servicesMap.get(service);
                        if (!servicesDataMap.containsKey(month)) {
                            HashMap<String, Double> eachQuaterData = new HashMap<>();
                            eachQuaterData.put("spend", spend);
                            servicesDataMap.put(month, eachQuaterData);
                        }
                    } else {
                        HashMap<String, HashMap<String, Double>> quatersWiseMap = new HashMap<>();
                        HashMap<String, Double> eachQuaterData = new HashMap<>();
                        eachQuaterData.put("spend", spend);
                        quatersWiseMap.put(month, eachQuaterData);
                        servicesMap.put(service, quatersWiseMap);
                    }

                    if (monthlyTotalSpend.containsKey(month)) {
                        double totalSpend = monthlyTotalSpend.get(month);
                        totalSpend += spend;
                        monthlyTotalSpend.put(month, totalSpend);
                    } else {
                        monthlyTotalSpend.put(month, spend);
                    }
                }
            }
            JSONObject finalObject = new JSONObject();
            JSONArray servicesArray = new JSONArray();

            for (String service : servicesList) {
                JSONObject serviceWiseDataObj = new JSONObject();
                HashMap<String, HashMap<String, Double>> quatersWiseMap = servicesMap.get(service);
                serviceWiseDataObj.put("name", service);
                Double totalSpendForAllMonths=0.0;
                for (String quater : quatersList) {
                    String spend = "0";
                    if (quatersWiseMap != null && quatersWiseMap.containsKey(quater)) {
                        spend = commaSeperatedDecimalFormat.format(quatersWiseMap.get(quater).get("spend"));
                    }
                    serviceWiseDataObj.put(quater, spend);
                    totalSpendForAllMonths=totalSpendForAllMonths+Double.parseDouble(spend.replace(",",""));
                }
                serviceWiseDataObj.put("Total",commaSeperatedDecimalFormat.format(totalSpendForAllMonths));
                servicesArray.put(serviceWiseDataObj);
            }

            if(servicesArray != null){
                JSONObject totalMonthlyJson = new JSONObject();
                Double grandTotalSpend=0.0;
                for(Map.Entry<String, Double> monthlyMap : monthlyTotalSpend.entrySet()){
                    if(monthlyMap != null){
                        totalMonthlyJson.put(monthlyMap.getKey(), commaSeperatedDecimalFormat.format(monthlyMap.getValue()));
                        grandTotalSpend=grandTotalSpend+monthlyMap.getValue();
                    }
                }
                totalMonthlyJson.put("Total",commaSeperatedDecimalFormat.format(grandTotalSpend));
                totalMonthlyJson.put("name", "Total");
                servicesArray.put(totalMonthlyJson);
            }

            JSONArray quatersArray = new JSONArray();
            for (String quater : quatersList) {
                quatersArray.put(quater);
            }
            quatersArray.put("Total");

            finalObject.put("services", servicesArray);
            finalObject.put("quaters", quatersArray);

            returnJson.put("values", finalObject);
        }
        return returnJson;
    }

    public static JSONArray prepareExportReportDataJson ( List<DashboardReportDto> reportDataList , JSONArray reportColumnDetails ,boolean isSelectedAll ,List<String> savedColumns) throws Exception  {
        JSONArray  finalJsonArray  = new JSONArray();
        int counter = 0;
        for (DashboardReportDto reportData : reportDataList) {
            JSONArray  eachRowInfoArray  = new JSONArray();
            for ( int i =0 ; i< reportColumnDetails.length() ; i++ ) {

                JSONObject eachColumnData = new JSONObject();
                JSONObject columnInfo = reportColumnDetails.getJSONObject(i);


                String selectClause = columnInfo.getString("selectClause");

                boolean isPresent = false ;
               if( ! isSelectedAll )
                 isPresent = savedColumns.contains(columnInfo.getString("header").toUpperCase());

                if ( counter == 0 ) {
                    eachColumnData.put("header", columnInfo.getString("header"));
                    eachColumnData.put("format", columnInfo.getString("format"));
                    eachColumnData.put("dataType", columnInfo.getString("dataType"));
                }

                switch (selectClause!=null?selectClause.trim().toUpperCase():"") {
                    case "CARRIER_NAME":
                        eachColumnData.put("value", reportData.getCarrierName());
                        break;
                    case "INVOICE_NUMBER":
                        eachColumnData.put("value", reportData.getInvoiceNumber());
                        break;
                    case "PRO_NUMBER":
                        eachColumnData.put("value", reportData.getProNumber());
                        break;
                    case "BOL_NUMBER":
                        eachColumnData.put("value", reportData.getBolNumber());
                        break;
                    case "BILL_OPTION":
                        eachColumnData.put("value", reportData.getBillOption());
                        break;
                    case "BILL_DATE":
                        eachColumnData.put("value", reportData.getBillDate());
                        break;
                    case "SHIP_DATE":
                        eachColumnData.put("value", reportData.getShipDate());
                        break;
                    case "DELIVERY_DATE":
                        eachColumnData.put("value", reportData.getDeliveryDate());
                        break;
                    case "INVOICE_MODE":
                        eachColumnData.put("value", reportData.getInvoiceNumber());
                        break;
                    case "INVOICE_METHOD":
                        eachColumnData.put("value", reportData.getInvoiceMethod());
                        break;
                    case "GL_ACCOUNTS_CODE":
                        eachColumnData.put("value", reportData.getGlAccountCode());
                        break;
                    case "PO_NUMBER":
                        eachColumnData.put("value", reportData.getPoNumber());
                        break;
                    case "REFERENCE1":
                        eachColumnData.put("value", reportData.getReference1());
                        break;
                    case "REFERENCE2":
                        eachColumnData.put("value", reportData.getReference2());
                        break;
                    case "SCAC_CODE":
                        eachColumnData.put("value", reportData.getScacCode());
                        break;
                    case "SHIPPER_NAME":
                        eachColumnData.put("value", reportData.getShipperName());
                        break;
                    case "SHIPPER_ADDRESS_1":
                        eachColumnData.put("value", reportData.getShipperAddress1());
                        break;
                    case "SHIPPER_CITY":
                        eachColumnData.put("value", reportData.getShipperCity());
                        break;
                    case "SHIPPER_STATE":
                        eachColumnData.put("value", reportData.getShipperState());
                        break;
                    case "SHIPPER_ZIPCODE":
                        eachColumnData.put("value", reportData.getShipperZipCode());
                        break;
                    case "SHIPPER_COUNTRY":
                        eachColumnData.put("value", reportData.getShipperCountry());
                        break;
                    case "RECEIVER_NAME":
                        eachColumnData.put("value", reportData.getReceiverName());
                        break;
                    case "RECEIVER_ADDRESS_1":
                        eachColumnData.put("value", reportData.getReceiverAddress1());
                        break;
                    case "RECEIVER_CITY":
                        eachColumnData.put("value", reportData.getReceiverCity());
                        break;
                    case "RECEIVER_STATE":
                        eachColumnData.put("value", reportData.getReceiverState());
                        break;
                    case "RECEIVER_ZIPCODE":
                        eachColumnData.put("value", reportData.getReceiverZipCode());
                        break;
                    case "RECEIVER_COUNTRY":
                        eachColumnData.put("value", reportData.getReceiverCountry());
                        break;
                    case "TOTAL_WEIGHT":
                        eachColumnData.put("value", reportData.getTotalWeight());
                        break;
                    case "TOTAL_CHARGES":
                        eachColumnData.put("value", reportData.getTotalCharges());
                        break;
                    case "LINE_HAUL":
                        eachColumnData.put("value", reportData.getLineHaul());
                        break;
                    case "FUEL_SURCHARGE":
                        eachColumnData.put("value", reportData.getFuelCharges());
                        break;
                    case "DISCOUNT":
                        eachColumnData.put("value", reportData.getDiscount());
                        break;
                    case "ACCESSORIALS":
                        eachColumnData.put("value", reportData.getAccessorial());
                        break;
                    case "ADJUSTMENTS":
                        eachColumnData.put("value", reportData.getAdjustments());
                        break;
                    case "TOTAL_DUE_AMOUNT":
                        eachColumnData.put("value", reportData.getTotalDueAmount());
                        break;
                    case "INVOICE_STATUS":
                        eachColumnData.put("value", reportData.getInvoiceStatus());
                        break;
                    case "CHECK_NO":
                        eachColumnData.put("value", reportData.getCheckNumber());
                        break;
                    case "CHECK_DATE":
                        eachColumnData.put("value", reportData.getCheckDate());
                        break;
                    case "CHECK_AMOUNT":
                        eachColumnData.put("value", reportData.getCheckAmount());
                        break;
                    case "ADJUSTMENT_REASON":
                        eachColumnData.put("value", reportData.getAdjustmentReason());
                        break;
                    case "SHIPPER_REGION":
                        eachColumnData.put("value", reportData.getShipperRegion());
                        break;
                    case "RECEIVER_REGION":
                        eachColumnData.put("value", reportData.getReceiverRegion());
                        break;
                    case "MULTI_WT":
                        eachColumnData.put("value", reportData.getMultiWeight());
                        break;
                    case "SERVICE_LEVEL":
                        eachColumnData.put("value", reportData.getServiceLevel());
                        break;
                    case "DELIVERY_FLAG":
                        eachColumnData.put("value", reportData.getDeliveryFlag());
                        break;
                    case "CUSTOM_DEFINED_1":
                        eachColumnData.put("value", reportData.getCustomDefined1());
                        break;
                    case "CUSTOM_DEFINED_2":
                        eachColumnData.put("value", reportData.getCustomDefined2());
                        break;
                    case "CUSTOM_DEFINED_3":
                        eachColumnData.put("value", reportData.getCustomDefined3());
                        break;
                    case "CUSTOM_DEFINED_4":
                        eachColumnData.put("value", reportData.getCustomDefined4());
                        break;
                    case "CUSTOM_DEFINED_5":
                        eachColumnData.put("value", reportData.getCustomDefined5());
                        break;
                    case "CUSTOM_DEFINED_6":
                        eachColumnData.put("value", reportData.getCustomDefined6());
                        break;
                    case "CUSTOM_DEFINED_7":
                        eachColumnData.put("value", reportData.getCustomDefined7());
                        break;
                    case "CUSTOM_DEFINED_8":
                        eachColumnData.put("value", reportData.getCustomDefined8());
                        break;
                    case "CUSTOM_DEFINED_9":
                        eachColumnData.put("value", reportData.getCustomDefined9());
                        break;
                    case "CUSTOM_DEFINED_10":
                        eachColumnData.put("value", reportData.getCustomDefined10());
                        break;
                }
                if( isSelectedAll || isPresent)
                  eachRowInfoArray.put(eachColumnData);

            }
            counter++;
            finalJsonArray.put(eachRowInfoArray);
        }


        return finalJsonArray;
    }

    public static JSONArray prepareDashboardReportJson(List<DashboardReportDto> reportDataList, Map<String, String> resultColumn) throws JSONException {
        JSONArray returnArray = new JSONArray();

        if (reportDataList != null && !reportDataList.isEmpty()) {
            for (DashboardReportDto reportData : reportDataList) {
                if (reportData != null) {
                    JSONObject reportJsonObj = new JSONObject();
                    for (Map.Entry<String, String> columnEntry : resultColumn.entrySet()) {
                        if (columnEntry != null) {
                            String key = columnEntry.getKey();
                            String value = columnEntry.getValue();
                            switch (key) {
                                case "CARRIER_NAME":
                                    reportJsonObj.put("carriername", reportData.getCarrierName());
                                    break;
                                case "INVOICE_NUMBER":
                                    reportJsonObj.put("invoice#", reportData.getInvoiceNumber());
                                    break;
                                case "PRO_NUMBER":
                                    reportJsonObj.put("pro#", reportData.getProNumber());
                                    break;
                                case "BOL_NUMBER":
                                    reportJsonObj.put("bol#", reportData.getBolNumber());
                                    break;
                                case "BILL_OPTION":
                                    reportJsonObj.put("billoption", reportData.getBillOption());
                                    break;
                                case "BILL_DATE":
                                    reportJsonObj.put("invoicedate", reportData.getBillDate());
                                    break;
                                case "SHIP_DATE":
                                    reportJsonObj.put("shipdate", reportData.getShipDate());
                                    break;
                                case "DELIVERY_DATE":
                                    reportJsonObj.put("deliverydate", reportData.getDeliveryDate());
                                    break;
                                case "INVOICE_MODE":
                                    reportJsonObj.put(value, reportData.getInvoiceNumber());
                                    break;
                                case "INVOICE_METHOD":
                                    reportJsonObj.put("invoicemethod", reportData.getInvoiceMethod());
                                    break;
                                case "GL_ACCOUNTS_CODE":
                                    reportJsonObj.put("glcode", reportData.getGlAccountCode());
                                    break;
                                case "PO_NUMBER":
                                    reportJsonObj.put("po#", reportData.getPoNumber());
                                    break;
                                case "REFERENCE1":
                                    reportJsonObj.put("reference1", reportData.getReference1());
                                    break;
                                case "REFERENCE2":
                                    reportJsonObj.put("reference2", reportData.getReference2());
                                    break;
                                case "SCAC_CODE":
                                    reportJsonObj.put("scaccode", reportData.getScacCode());
                                    break;
                                case "SHIPPER_NAME":
                                    reportJsonObj.put("shippername", reportData.getShipperName());
                                    break;
                                case "SHIPPER_ADDRESS_1":
                                    reportJsonObj.put("shipperaddress1", reportData.getShipperAddress1());
                                    break;
                                case "SHIPPER_CITY":
                                    reportJsonObj.put("shippercity", reportData.getShipperCity());
                                    break;
                                case "SHIPPER_STATE":
                                    reportJsonObj.put("shipperstate", reportData.getShipperState());
                                    break;
                                case "SHIPPER_ZIPCODE":
                                    reportJsonObj.put("shipperpostalcode", reportData.getShipperZipCode());
                                    break;
                                case "SHIPPER_COUNTRY":
                                    reportJsonObj.put("shippercountry", reportData.getShipperCountry());
                                    break;
                                case "RECEIVER_NAME":
                                    reportJsonObj.put("receivername", reportData.getReceiverName());
                                    break;
                                case "RECEIVER_ADDRESS_1":
                                    reportJsonObj.put("receiveraddress1", reportData.getReceiverAddress1());
                                    break;
                                case "RECEIVER_CITY":
                                    reportJsonObj.put("receivercity", reportData.getReceiverCity());
                                    break;
                                case "RECEIVER_STATE":
                                    reportJsonObj.put("receiverstate", reportData.getReceiverState());
                                    break;
                                case "RECEIVER_ZIPCODE":
                                    reportJsonObj.put("receiverpostalcode", reportData.getReceiverZipCode());
                                    break;
                                case "RECEIVER_COUNTRY":
                                    reportJsonObj.put("receivercountry", reportData.getReceiverCountry());
                                    break;
                                case "TOTAL_WEIGHT":
                                    reportJsonObj.put("totalweight", checkCommaSeparaedDecimalFormat(value, reportData.getTotalWeight()));
                                    break;
                                case "TOTAL_CHARGES":
                                    reportJsonObj.put("totalcharges", checkCommaSeparaedDecimalFormat(value, reportData.getTotalCharges()));
                                    break;
                                case "LINE_HAUL":
                                    reportJsonObj.put("linehaul", checkCommaSeparaedDecimalFormat(value, reportData.getLineHaul()));
                                    break;
                                case "FUEL_SURCHARGE":
                                    reportJsonObj.put("fuelsurcharge", checkCommaSeparaedDecimalFormat(value, reportData.getFuelCharges()));
                                    break;
                                case "DISCOUNT":
                                    reportJsonObj.put("discount", checkCommaSeparaedDecimalFormat(value, reportData.getDiscount()));
                                    break;
                                case "ACCESSORIALS":
                                    reportJsonObj.put("accessorials", checkCommaSeparaedDecimalFormat(value, reportData.getAccessorial()));
                                    break;
                                case "ADJUSTMENTS":
                                    reportJsonObj.put("adjustments", checkCommaSeparaedDecimalFormat(value, reportData.getAdjustments()));
                                    break;
                                case "TOTAL_DUE_AMOUNT":
                                    reportJsonObj.put("totaldueamount", checkCommaSeparaedDecimalFormat(value, reportData.getTotalDueAmount()));
                                    break;
                                case "INVOICE_STATUS":
                                    reportJsonObj.put("invoicestatus", reportData.getInvoiceStatus());
                                    break;
                                case "CHECK_NO":
                                    reportJsonObj.put(value, reportData.getCheckNumber());
                                    break;
                                case "CHECK_DATE":
                                    reportJsonObj.put(value, reportData.getCheckDate());
                                    break;
                                case "CHECK_AMOUNT":
                                    reportJsonObj.put(value, reportData.getCheckAmount());
                                    break;
                                case "ADJUSTMENT_REASON":
                                    reportJsonObj.put("adjustmentreason", reportData.getAdjustmentReason());
                                    break;
                                case "SHIPPER_REGION":
                                    reportJsonObj.put("shipperregion", reportData.getShipperRegion());
                                    break;
                                case "RECEIVER_REGION":
                                    reportJsonObj.put("receiverregion", reportData.getReceiverRegion());
                                    break;
                                case "MULTI_WT":
                                    reportJsonObj.put("cwtweight", reportData.getMultiWeight());
                                    break;
                                case "SERVICE_LEVEL":
                                    reportJsonObj.put("servicelevel", reportData.getServiceLevel());
                                    break;
                                case "DELIVERY_FLAG":
                                    reportJsonObj.put(value, reportData.getDeliveryFlag());
                                    break;
                                case "CUSTOM_DEFINED_1":
                                    reportJsonObj.put("customdefined1", reportData.getCustomDefined1());
                                    break;
                                case "CUSTOM_DEFINED_2":
                                    reportJsonObj.put("customdefined2", reportData.getCustomDefined2());
                                    break;
                                case "CUSTOM_DEFINED_3":
                                    reportJsonObj.put("customdefined3", reportData.getCustomDefined3());
                                    break;
                                case "CUSTOM_DEFINED_4":
                                    reportJsonObj.put("customdefined4", reportData.getCustomDefined4());
                                    break;
                                case "CUSTOM_DEFINED_5":
                                    reportJsonObj.put("customdefined5", reportData.getCustomDefined5());
                                    break;
                                case "CUSTOM_DEFINED_6":
                                    reportJsonObj.put("customdefined6", reportData.getCustomDefined6());
                                    break;
                                case "CUSTOM_DEFINED_7":
                                    reportJsonObj.put("customdefined7", reportData.getCustomDefined7());
                                    break;
                                case "CUSTOM_DEFINED_8":
                                    reportJsonObj.put("customdefined8", reportData.getCustomDefined8());
                                    break;
                                case "CUSTOM_DEFINED_9":
                                    reportJsonObj.put("customdefined9", reportData.getCustomDefined9());
                                    break;
                                case "CUSTOM_DEFINED_10":
                                    reportJsonObj.put("customdefined10", reportData.getCustomDefined10());
                                    break;
                            }
                        }
                    }
                    returnArray.put(reportJsonObj);
                }
            }
            return returnArray;
        }

        return null;
    }

    private static String checkCommaSeparaedDecimalFormat(String columnName, Object val) {
        String value = "";
        if (commaSeperatedFields.contains(columnName)) {
            try {
                if (val != null) {
                    value = commaSeperatedDecimalFormat.format(Math.round(Double.parseDouble(val.toString())));
                }
            } catch (Exception e) {
                value = val != null ? val.toString() : "";
            }
        } else {
            try {
                if (val != null) {
                    value = String.valueOf(Math.round(Double.parseDouble(val.toString())));
                }
            } catch (Exception e) {
                value = val != null ? val.toString() : "";
            }

        }
        return value;
    }

    public static JSONObject prepareActualVsBilledWeightJson(List<ActualVsBilledWeightDto> weightList) throws JSONException {
        JSONObject weightJson = new JSONObject();
        JSONArray valuesArray = null;
        JSONArray seriesArray = null;
        if (weightList != null && !weightList.isEmpty()) {
            valuesArray = new JSONArray();
            seriesArray = new JSONArray();
            int counter = 1;
            for (ActualVsBilledWeightDto weightDto : weightList) {
                if (weightDto != null) {
                    JSONObject monthWiseObj = new JSONObject();
                    monthWiseObj.put("name", weightDto.getBillingDate());
                    monthWiseObj.put("Actual Weight", weightDto.getActualWeight() != null ? Math.round(weightDto.getActualWeight()) : 0);
                    monthWiseObj.put("Bill Weight", weightDto.getBilledWeight() != null ? Math.round(weightDto.getBilledWeight()) : 0);
                    monthWiseObj.put("counter", counter);
                    valuesArray.put(monthWiseObj);
                    counter++;
                }

            }

            String object = "{\"id\":s1,\"name\":\"Actual Weight\",\"data\": {\"field\":\"Actual Weight\"},\"type\":\"line\",\"style\":{ marker: {shape: \"circle\", width: 5},";
            object = object + "lineColor: \"red\"";
            object = object + "}}";
            seriesArray.put(new JSONObject(object));

            object = "{\"id\":s2,\"name\":\"Bill Weight\",\"data\": {\"field\":\"Bill Weight\"},\"type\":\"line\",\"style\":{\"lineWidth\": 2,smoothing: true, marker: {shape: \"circle\", width: 5},";
            object = object + "lineColor: \"blue\"";
            object = object + "}}";
            seriesArray.put(new JSONObject(object));
        }
        weightJson.put("values", valuesArray != null ? valuesArray : new JSONArray());
        weightJson.put("series", seriesArray != null ? seriesArray : new JSONArray());
        return weightJson;
    }

    public static JSONObject prepareActualVsBillWeightByCarrierJson(List<ActualVsBilledWeightDto> weightList) throws JSONException {
        JSONObject weightJson = new JSONObject();
        JSONArray weightJsonArr = null;
        if (weightList != null && !weightList.isEmpty()) {
            weightJsonArr = new JSONArray();
            for (ActualVsBilledWeightDto weightDto : weightList) {
                if (weightDto != null) {
                    JSONObject wtJson = new JSONObject();
                    wtJson.put("id", weightDto.getCarrierId());
                    wtJson.put("name", weightDto.getCarrierName());
                    wtJson.put("Actual", weightDto.getActualWeight() !=null ? Math.round(weightDto.getActualWeight()) : 0);
                    wtJson.put("Billed", weightDto.getBilledWeight() != null ? Math.round(weightDto.getBilledWeight()) : 0);
                    weightJsonArr.put(wtJson);
                }
            }
        }
        weightJson.put("values", weightJsonArr != null ? weightJsonArr : new JSONArray());
        return weightJson;
    }

    public static JSONObject prepareActualVsBillWeightMonthlyChartJson(List<ActualVsBilledWeightDto> weightList) throws JSONException {
        JSONObject weightJson = new JSONObject();
        JSONArray returnArray = null;
        long fromDate = 0;
        long toDate = 0;
        if (weightList != null && !weightList.isEmpty()) {
            returnArray = new JSONArray();
            int count = 0;
            for (ActualVsBilledWeightDto weightDto : weightList) {
                if (weightDto != null) {
                    JSONArray dataArray = new JSONArray();
                    long dateInMilliSecs = weightDto.getBillDate() != null ? weightDto.getBillDate().getTime() : 0L;
                    dataArray.put(dateInMilliSecs);
                    dataArray.put(weightDto.getActualWeight() != null ? Math.round(weightDto.getActualWeight()) : 0);
                    dataArray.put(weightDto.getBilledWeight() != null ? Math.round(weightDto.getBilledWeight()) : 0);

                    returnArray.put(dataArray);
                    if (count == 0) {
                        fromDate = dateInMilliSecs;
                    }
                    toDate = dateInMilliSecs;
                    count++;
                }
            }
            if (fromDate == toDate) {
                toDate = toDate + 1;
            }
        }
        weightJson.put("values", returnArray != null ? returnArray : new JSONArray());
        weightJson.put("fromDate", fromDate);
        weightJson.put("toDate", toDate);
        return weightJson;
    }

    public static JSONObject prepareFilterCarrierJson(List<UserFilterUtilityDataDto> carrierList) throws JSONException {
        return prepareFilterCarrierJson(carrierList, null, true);
    }
    public static JSONArray prepareFilterCarrierJsonForParcel(List<UserFilterUtilityDataDto> carrierList) throws JSONException {
        return prepareFilterCarrierJsonForParcel(carrierList, null, true);
    }


    public static JSONArray prepareCarriersByGroupJson(List<UserFilterUtilityDataDto> carrierList,boolean isParcelDashlettes) throws JSONException {
        return prepareCarriersByGroupJson(carrierList, null, true,isParcelDashlettes);
    }

    public static JSONArray prepareCarriersByGroupJson(List<UserFilterUtilityDataDto> carrierList, List<Long> selectedCarrList, boolean isNew,boolean isParcelDashlettes) throws JSONException {
        JSONObject carrJson = new JSONObject();
        JSONArray parcelCarJsonArr = new JSONArray();
        JSONArray freightCarrJsonArr = new JSONArray();

       // JSONArray parcelCarGroupJsonArr = new JSONArray();



        if (carrierList != null && !carrierList.isEmpty()) {
            for (UserFilterUtilityDataDto userFilterCarr : carrierList) {
                JSONObject  carGroupJson =  new JSONObject();
                if (userFilterCarr != null) {

                    JSONArray childJsonArr = new JSONArray();

                    String carrierGroupName = userFilterCarr.getCarrierGroupName();
                    String[] carrierIdCsvLArr = userFilterCarr.getCarrierIdCSV().split("#@#");
                    String[] carrierNameCsvLArr = userFilterCarr.getCarrierName().split("#@#");
                    String carriers = userFilterCarr.getCarrierIdCSV().replace("#@#",",");
                    Long carrierGroupId = userFilterCarr.getCarrierGroupId();

                    carGroupJson.put("name",carrierGroupName);

                    boolean isGroupAllCarriersSelected = true;

                    if(carrierIdCsvLArr.length>0)
                    {


                        for(int i=0;i<carrierIdCsvLArr.length;i++)
                        {

                            JSONObject carrObj = new JSONObject();

                            boolean isCarrierSelected = isNew ? true : selectedCarrList != null && selectedCarrList.contains(Long.valueOf(carrierIdCsvLArr[i])) ;
                            carrObj.put("id", Long.parseLong(carrierIdCsvLArr[i]));
                            carrObj.put("name", carrierNameCsvLArr[i]);

                            carrObj.put("checked", isCarrierSelected);
                            carrObj.put("selected", isCarrierSelected);
                            carrObj.put("isParcel", isParcelDashlettes);

                            if( ! isCarrierSelected)
                                isGroupAllCarriersSelected = false;

                            childJsonArr.put(carrObj);


                        }

                    }

                    if(carrierIdCsvLArr.length>1 || carrierGroupId!=null)
                    {

                        carGroupJson.put("id", "");
                        carGroupJson.put("children",childJsonArr);
                        carGroupJson.put("carriers",carriers);
                        carGroupJson.put("isParcel", isParcelDashlettes);


                        carGroupJson.put("checked", isGroupAllCarriersSelected);
                        carGroupJson.put("selected", isGroupAllCarriersSelected);
                    }

                    else if(carrierIdCsvLArr.length==1  )
                    {
                        carGroupJson.put("id",carrierIdCsvLArr[0]);
                        carGroupJson.put("children",new JSONArray());
                        carGroupJson.put("carriers","");
                        carGroupJson.put("isParcel", isParcelDashlettes);
                        carGroupJson.put("checked", isNew ? true : selectedCarrList != null && selectedCarrList.contains(Long.valueOf(userFilterCarr.getCarrierIdCSV())));
                        carGroupJson.put("selected", isNew ? true : selectedCarrList != null && selectedCarrList.contains(Long.valueOf(userFilterCarr.getCarrierIdCSV())));


                    }

                }

                if(isParcelDashlettes){
                    parcelCarJsonArr.put(carGroupJson);
                }else {
                    freightCarrJsonArr.put(carGroupJson);
                }
            }
        }
        /*carrJson.put("parcelCarriers", parcelCarJsonArr);
        carrJson.put("freightCarriers", freightCarrJsonArr);*/
        if(isParcelDashlettes){
            return  parcelCarJsonArr;
        }else {
            return freightCarrJsonArr;
        }

    }



    public static JSONObject prepareReportForModesJson(List<ReportModesDto> reportModeDtoList) throws JSONException {

        JSONObject reportModesJSON = new JSONObject();

        JSONObject parcelJSON = new JSONObject();
        JSONObject customReportJSON = new JSONObject();

        JSONArray allModesJsonArr = new JSONArray();
        JSONArray freightJsonArr = new JSONArray();

        JSONArray shipmentLevelDetailJsonArr = new JSONArray();
        JSONArray deliveryParcelJsonArr = new JSONArray();
        JSONArray parcelSpendOptimizationJsonArr = new JSONArray();
        JSONArray parcelChargeLevelDetailJsonArr = new JSONArray();

        JSONArray allModesCustRepJsonArr = new JSONArray();
        JSONArray freightCustRepJsonArr = new JSONArray();
        JSONArray parcelCustRepJsonArr = new JSONArray();
        JSONArray paymentSummaryCustRepJsonArr = new JSONArray();
        JSONArray archivedReportsCustRepJsonArr = new JSONArray();
        JSONArray internalReportCustRepJsonArr = new JSONArray();


        JSONArray internalReportJsonArr = new JSONArray();
        JSONArray paymentSummaryReportsJsonArr = new JSONArray();
        JSONArray legacyReportsJsonArr = new JSONArray();
        JSONArray archivedReportsJsonArr = new JSONArray();


        if (reportModeDtoList != null && !reportModeDtoList.isEmpty()) {
            for (ReportModesDto reportModesDto : reportModeDtoList) {

                if (reportModesDto != null) {

                      if("All Modes".equalsIgnoreCase(reportModesDto.getGroupName())) {
                          allModesJsonArr.put(reportModesDto);
                      } else if("Freight".equalsIgnoreCase(reportModesDto.getGroupName())) {
                          freightJsonArr.put(reportModesDto);
                      } else if("Custom Reports".equalsIgnoreCase(reportModesDto.getGroupName())) {

                         if("All Modes".equalsIgnoreCase(reportModesDto.getGroupUnderName()))
                             allModesCustRepJsonArr.put(reportModesDto);
                         else if("Freight".equalsIgnoreCase(reportModesDto.getGroupUnderName()))
                             freightCustRepJsonArr.put(reportModesDto);
                         else if("Parcel".equalsIgnoreCase(reportModesDto.getGroupUnderName()))
                             parcelCustRepJsonArr.put(reportModesDto);

                     } else if("Parcel".equalsIgnoreCase(reportModesDto.getGroupUnderName())) {
                         if("Shipment Level Detail".equalsIgnoreCase(reportModesDto.getGroupName()))
                             shipmentLevelDetailJsonArr.put(reportModesDto);
                         else if("Delivery & Tracking".equalsIgnoreCase(reportModesDto.getGroupName()))
                             deliveryParcelJsonArr.put(reportModesDto);
                         else if("Spend Optimization".equalsIgnoreCase(reportModesDto.getGroupName()))
                             parcelSpendOptimizationJsonArr.put(reportModesDto);
                         else if("Charge Level Detail".equalsIgnoreCase(reportModesDto.getGroupName()))
                             parcelChargeLevelDetailJsonArr.put(reportModesDto);

                     }

                      else if("Internal Reports".equalsIgnoreCase(reportModesDto.getGroupName())) {
                          internalReportJsonArr.put(reportModesDto);

                      }
                      else if("Payment Summary Reports".equalsIgnoreCase(reportModesDto.getGroupName())) {
                          paymentSummaryReportsJsonArr.put(reportModesDto);

                      }
                      else if("Archived".equalsIgnoreCase(reportModesDto.getGroupName())) {
                          archivedReportsJsonArr.put(reportModesDto);

                      }
                      else if("Legacy Reports".equalsIgnoreCase(reportModesDto.getGroupName())) {
                          legacyReportsJsonArr.put(reportModesDto);

                      }

                }

            } // List For End

        }
        parcelJSON.put("shipmentLevelDetail",shipmentLevelDetailJsonArr);
        parcelJSON.put("delivery",deliveryParcelJsonArr);
        parcelJSON.put("parcelSpendOptimization",parcelSpendOptimizationJsonArr);
        parcelJSON.put("parcelChargeLevelDetail",parcelChargeLevelDetailJsonArr);


        customReportJSON.put("allModes",allModesCustRepJsonArr);
        customReportJSON.put("freight",freightCustRepJsonArr);
        customReportJSON.put("parcel",parcelCustRepJsonArr);
        customReportJSON.put("internalReports",internalReportCustRepJsonArr);
        customReportJSON.put("paymentSummaryReports",paymentSummaryCustRepJsonArr);
        customReportJSON.put("archivedReports",archivedReportsCustRepJsonArr);



        reportModesJSON.put("allModes",allModesJsonArr);
        reportModesJSON.put("freight",freightJsonArr);
        reportModesJSON.put("parcel",parcelJSON);
        reportModesJSON.put("customReports",customReportJSON);
        reportModesJSON.put("internalReports",internalReportJsonArr);
        reportModesJSON.put("paymentSummaryReports",paymentSummaryReportsJsonArr);
        reportModesJSON.put("legacyReports",legacyReportsJsonArr);
        reportModesJSON.put("archivedReports",archivedReportsJsonArr);


        return reportModesJSON;
    }

    public static JSONObject prepareFilterCarrierJson(List<UserFilterUtilityDataDto> carrierList, List<Long> selectedCarrList, boolean isNew) throws JSONException {
        JSONObject carrJson = new JSONObject();
        JSONArray parcelCarJsonArr = new JSONArray();
        JSONArray freightCarrJsonArr = new JSONArray();
        if (carrierList != null && !carrierList.isEmpty()) {
            for (UserFilterUtilityDataDto userFilterCarr : carrierList) {
                if (userFilterCarr != null) {
                    JSONObject carrObj = new JSONObject();
                    carrObj.put("id", userFilterCarr.getCarrierId());
                    carrObj.put("name", userFilterCarr.getCarrierName());
                    carrObj.put("checked", isNew ? true : selectedCarrList != null && selectedCarrList.contains(userFilterCarr.getCarrierId()));

                    if("parcel".equalsIgnoreCase(userFilterCarr.getCarrierGroupName())){
                        parcelCarJsonArr.put(carrObj);
                    }else if("freight".equalsIgnoreCase(userFilterCarr.getCarrierGroupName())){

                        if  ( userFilterCarr.getCarrierName().contains("#") ) {

                        }

                        freightCarrJsonArr.put(carrObj);
                    }
                }
            }
        }
        carrJson.put("parcelCarriers", parcelCarJsonArr);
        carrJson.put("freightCarriers", freightCarrJsonArr);
        return carrJson;
    }

    public static JSONArray prepareFilterCarrierJsonForParcel(List<UserFilterUtilityDataDto> carrierList, List<Long> selectedCarrList, boolean isNew) throws JSONException {
        JSONObject carrJson = new JSONObject();
        JSONArray parcelCarJsonArr = new JSONArray();

        if (carrierList != null && !carrierList.isEmpty()) {
            for (UserFilterUtilityDataDto userFilterCarr : carrierList) {
                if (userFilterCarr != null) {
                    JSONObject carrObj = new JSONObject();
                    carrObj.put("id", userFilterCarr.getCarrierIdCSV());
                    carrObj.put("name", userFilterCarr.getCarrierName());
                    carrObj.put("checked", isNew ? true : selectedCarrList != null && selectedCarrList.contains(Long.valueOf(userFilterCarr.getCarrierIdCSV())));
                    carrObj.put("selected",isNew ? true : selectedCarrList != null && selectedCarrList.contains(Long.valueOf(userFilterCarr.getCarrierIdCSV())));
                    parcelCarJsonArr.put(carrObj);
                }
            }
        }

        return parcelCarJsonArr;
    }

    public static JSONArray prepareFilterModesJson(List<UserFilterUtilityDataDto> modes, Map<String, String> modeWiseCarriers, boolean isParcelDashlettes) throws JSONException {
        return prepareFilterModesJson(modes, null, modeWiseCarriers, isParcelDashlettes, true);
    }

    public static JSONArray prepareFilterModesJson(List<UserFilterUtilityDataDto> modes, List<Long> savedModes, Map<String, String> modeWiseCarriers, boolean isParcelDashlettes, boolean isNew) throws JSONException {
        JSONArray modesDetailsArray = new JSONArray();
        if (modes != null && !modes.isEmpty()) {
            List<String> modesList = new ArrayList<String>();
            for (UserFilterUtilityDataDto userFilterMode : modes) {
                if (userFilterMode != null) {
                        if (!modesList.contains(userFilterMode.getId())) {
                            modesList.add(String.valueOf(userFilterMode.getId()));
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("id", userFilterMode.getId());
                            jsonObject.put("name", userFilterMode.getName());
                            jsonObject.put("checked", isNew ? true : savedModes != null && savedModes.contains(userFilterMode.getId()));
                            modesDetailsArray.put(jsonObject);
                    }
                }
            }
            /*if (modeWiseCarriers.containsKey("parcelCarrier") && needToAddSmallPackage) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", WebConstants.SMALL_PACKAGE_CODE_VALUE_ID);
                jsonObject.put("name", WebConstants.SMALL_PACKAGE_CARRIER_MODES);
                jsonObject.put("checked", isNew ? true : savedModes != null && savedModes.contains(Long.valueOf(WebConstants.SMALL_PACKAGE_CODE_VALUE_ID)));
                modesDetailsArray.put(jsonObject);
            }*/
        }
        return modesDetailsArray;
    }

    public static JSONArray prepareFilterServiceJson(List<UserFilterUtilityDataDto> serviceDataList) throws JSONException {
        return prepareFilterServiceJson(serviceDataList, null, true);
    }

    public static JSONArray prepareFilterServiceJson(List<UserFilterUtilityDataDto> serviceDataList, List<Long> selectedServices, boolean isNew) throws JSONException {
        JSONArray serviceDetailsArray = new JSONArray();
        if (serviceDataList != null && !serviceDataList.isEmpty()) {
            ArrayList<Long> serviceList = new ArrayList<Long>();
            for (UserFilterUtilityDataDto serviceData : serviceDataList) {
                if (serviceData != null && !serviceList.contains(serviceData.getId())) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", serviceData.getId());
                    jsonObject.put("name", serviceData.getName());
                    jsonObject.put("mode", serviceData.getType());
                    jsonObject.put("type", "category");
                    jsonObject.put("checked", isNew ? true : selectedServices!= null && selectedServices.contains(serviceData.getId()));
                    jsonObject.put("uniqueType", "services");
                    jsonObject.put("isActive", serviceData.getActive());
                    jsonObject.put("isFreight", !"Small Package".equalsIgnoreCase(serviceData.getType()));

                    serviceDetailsArray.put(jsonObject);
                    serviceList.add(serviceData.getId());
                }
            }
        }
        return serviceDetailsArray;
    }


    public static JSONObject prepareShipmentCountByZoneJson(List<ShipmentDto> shipmentList, Set<MapCoordinatesDto> mapCoordinates) throws JSONException {
        JSONObject finalJson = new JSONObject();
        Map<String, String>  zoneWithColors = new HashMap<>();
        if (shipmentList != null && !shipmentList.isEmpty()) {
            Map<String, Map<String, Integer>> nodeValuesMap = new HashMap<String, Map<String, Integer>>();

            int counter = 0;
            for (ShipmentDto shipment : shipmentList) {
                if (shipment != null) {
                    String shipperState = shipment.getShipperState() != null ? shipment.getShipperState() : "";
                    String shipperCountry = shipment.getShipperCountry() != null ? shipment.getShipperCountry() : "";
                    String zone = shipment.getZone();

                    if ( !zoneWithColors.containsKey(zone) ) {
                        zoneWithColors.put(zone, colorsList.get(counter));
                        counter++;
                    }

                    Integer shipmentCount = shipment.getShipmentCount();

                    if ( !nodeValuesMap.containsKey(shipperState)) {
                        HashMap<String, Integer> tempMap = new HashMap<>();
                        tempMap.put(zone, shipmentCount);

                        nodeValuesMap.put( shipperState, tempMap);
                    } else {
                        Map<String,Integer> zonesMap = nodeValuesMap.get(shipperState);

                        if ( !zonesMap.containsKey(zone)) {
                            zonesMap.put(zone, shipmentCount);
                        } else {
                            zonesMap.put(zone, zonesMap.get(zone)+shipmentCount);
                        }
                    }
                }
            }

            JSONArray nodesArray = new JSONArray();
            Iterator<String> nodeValuesIterator = nodeValuesMap.keySet().iterator();
            while (nodeValuesIterator.hasNext()) {
                JSONObject nodeInfoObj = new JSONObject();
                String state = nodeValuesIterator.next();
                nodeInfoObj.put("id", state);
                nodeInfoObj.put("name", state);

                Iterator<String> zonesIterator = nodeValuesMap.get(state).keySet().iterator();
                JSONArray zonesArray = new JSONArray();
                int i = 0;
                while (zonesIterator.hasNext()) {
                    JSONObject zoneInfo = new JSONObject();

                    String zone = zonesIterator.next();
                    Integer shipmentCount = nodeValuesMap.get(state).get(zone);

                    zoneInfo.put("id", zone);
                    zoneInfo.put("name", "Zone " + zone);
                    zoneInfo.put("value", shipmentCount);
                    zoneInfo.put("selected", false);
                    String colorName = zoneWithColors.get(zone);

                    zoneInfo.put("color", colorName);
                    String style = "{ fillColor: \"" + colorName + "\" } }";
                    zoneInfo.put("style", new JSONObject(style));
                    zonesArray.put(zoneInfo);
                }

                MapCoordinatesDto mapCoordinate = findCoordinateByState(mapCoordinates, state);
                if ( mapCoordinate != null && mapCoordinate.getLongitude() != null && mapCoordinate.getLatitude() != null ) {
                    JSONArray longLatJsonArray = new JSONArray();
                    longLatJsonArray.put(mapCoordinate != null ? mapCoordinate.getLongitude() : 0);
                    longLatJsonArray.put(mapCoordinate != null ? mapCoordinate.getLatitude() : 0);

                    nodeInfoObj.put("coordinates", longLatJsonArray);
                    nodeInfoObj.put("pieChartValues", zonesArray);

                    nodesArray.put(nodeInfoObj);
                }
            }

            Iterator<String> zonesIterator = zoneWithColors.keySet().iterator();
            JSONArray zonesArray = new JSONArray();
            while ( zonesIterator.hasNext() ) {
                String zone = zonesIterator.next();
                String color = zoneWithColors.get(zone);

                JSONObject zoneObj = new JSONObject();

                zoneObj.put("id", zone);
                zoneObj.put("name", "Zone " + zone);
                zoneObj.put("selected", false);
                zoneObj.put("color", color);

                zonesArray.put(zoneObj);
            }

            finalJson.put("nodes",nodesArray);
            finalJson.put("zones",zonesArray);

        }
        return finalJson;
    }

    private static MapCoordinatesDto findMapCoordinate(Set<MapCoordinatesDto> mapCoordinates, String addressKey) {
        for(MapCoordinatesDto mapCoordinate : mapCoordinates){
            if(mapCoordinate != null){
                String key = mapCoordinate.getAddress().substring(mapCoordinate.getAddress().indexOf(",") + 1);
                if(key.equalsIgnoreCase(addressKey)){
                    return mapCoordinate;
                }
            }
        }
        return null;
    }

    private static MapCoordinatesDto findCoordinateByState(Set<MapCoordinatesDto> mapCoordinates, String addressKey) {
        for(MapCoordinatesDto mapCoordinate : mapCoordinates){
            if(mapCoordinate != null){
                String stateAndCountry = mapCoordinate.getAddress().substring(mapCoordinate.getAddress().indexOf(",")+1 );
                String state = stateAndCountry.substring(0,stateAndCountry.indexOf(",") );
                if(addressKey.equalsIgnoreCase(state)){
                    return mapCoordinate;
                }
            }
        }
        return null;
    }

    private static MapCoordinatesDto findMapCoordinateByAddress(Set<MapCoordinatesDto> mapCoordinates, String addressKey) {
        for(MapCoordinatesDto mapCoordinate : mapCoordinates){
            if(mapCoordinate != null){
                if(mapCoordinate.getAddress().equalsIgnoreCase(addressKey)){
                    return mapCoordinate;
                }
            }
        }
        return null;
    }

    public static JSONArray customerHierarchyJson( ReportCustomerCarrierDto  customerDto) throws  JSONException{
        setValuesForDropDownForCustomer(customerDto);
        JSONArray jsonArray = new JSONArray();
        for (ReportCustomerCarrierDto customerCarrierDto : customerDto.getCollection()) {
            JSONObject custoemrJsonObject = new JSONObject();
            custoemrJsonObject.put("customerName", customerCarrierDto.getCustomerName());
            custoemrJsonObject.put("customerId", customerCarrierDto.getCustomerId());
            custoemrJsonObject.put("selected", customerCarrierDto.getSelected());
            custoemrJsonObject.put("carrierIds", customerCarrierDto.getCustomerCarrierId());
            custoemrJsonObject.put("type", customerCarrierDto.getType());
            custoemrJsonObject.put("paidCust", customerCarrierDto.getPaidCust());
            custoemrJsonObject.put("value", null == customerCarrierDto.getValue() ? "" : customerCarrierDto.getValue());

            if (customerCarrierDto.getRegion() == null || customerCarrierDto.getRegion().isEmpty() || "North America".equalsIgnoreCase(customerCarrierDto.getRegion()) ) {
                custoemrJsonObject.put("weightUnit", "LBS");
            } else {
                custoemrJsonObject.put("weightUnit","KGS");
            }
            custoemrJsonObject.put("currencyId", null == customerCarrierDto.getCurrencyId() ? "0" : customerCarrierDto.getCurrencyId());

            if (!"SHP".equalsIgnoreCase(customerCarrierDto.getType())) {
                custoemrJsonObject.put("children", customerHierarchyJson(customerCarrierDto));
            }
            if ("SHGRP".equalsIgnoreCase(customerCarrierDto.getType())) {
                custoemrJsonObject.put("parentCustomerId", customerCarrierDto.getParentCustomerId());
            }
            jsonArray.put(custoemrJsonObject);
        }
        return jsonArray;
    }

    public static void setValuesForDropDownForCustomer(ReportCustomerCarrierDto customerDto) {
        for (ReportCustomerCarrierDto customer : customerDto.getCollection()) {
            if ("CUGRP".equalsIgnoreCase(customer.getType())) {
                customer.setValue(getValueForCustGroup(customer));
            } else {
                customer.setValue(String.valueOf(customer.getCustomerId()));
            }
        }
    }
    public static String getValueForCustGroup(ReportCustomerCarrierDto customerDto) {
        StringBuffer value = new StringBuffer();
        for (ReportCustomerCarrierDto customer : customerDto.getCollection()) {
            if ("CUST".equalsIgnoreCase(customer.getType())) {
                value.append(customer.getCustomerId() + ",");
            }
            if ("CUGRP".equalsIgnoreCase(customer.getType())) {
                value.append(getValueForCustGroup(customer) + ",");
            }
        }
        // Trim off last ,
        String returnValue = value.toString();
        returnValue = returnValue.substring(0, returnValue.length() - 1);
        return returnValue;
    }

    public static JSONArray carriersJson( List<ReportCustomerCarrierDto> carrierList) throws JSONException{
        JSONArray carrierJsonArr= new JSONArray();
        if(carrierList.get(0).getCarrierId()!=null) {
            for (ReportCustomerCarrierDto carrierDto : carrierList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("carrierId", carrierDto.getCarrierId());
                jsonObject.put("carrierName", carrierDto.getCarrierName());
                jsonObject.put("isLtl", carrierDto.getIsLtl());
                jsonObject.put("selected", carrierDto.getSelected());
                carrierJsonArr.put(jsonObject);
            }
        }
        return  carrierJsonArr;
    }

    public static JSONArray prepareCurrenciesJson( List<CodeValueDto> codeValueDtoList) throws JSONException{
        JSONArray currenciesJsonArray= new JSONArray();


        JSONObject jsonObject=new JSONObject();
        jsonObject.put("id",0);
        jsonObject.put("code","Select Currency");
        jsonObject.put("name","");
        currenciesJsonArray.put(jsonObject);

        for (CodeValueDto codeValueDto : codeValueDtoList) {
            jsonObject=new JSONObject();
            jsonObject.put("id",codeValueDto.getId());
            jsonObject.put("code",codeValueDto.getCodeValue());
            jsonObject.put("name"," ( "+codeValueDto.getPropertyOne()+" ) ");
            currenciesJsonArray.put(jsonObject);
        }
        return  currenciesJsonArray;
    }
    public static JSONArray getFolderHierarchyJson(ReportFolderDto folderHierarchyDto,Integer count) throws Exception{
        JSONArray jsonArray=new JSONArray();
        String str="";
        if (count>0) {
            for (int i = 0; i < count; i++)
                str=str+"  ";
        }
        for (ReportFolderDto dtoHierarchy:folderHierarchyDto.getCollection()){
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("folderId",dtoHierarchy.getRptFolderId());
            jsonObject.put("folderName",str+dtoHierarchy.getRptFolderName());
            jsonObject.put("parentId",dtoHierarchy.getParentId());
            if(dtoHierarchy.getCollection()!=null && dtoHierarchy.getCollection().size()>0)
                jsonObject.put("child",getFolderHierarchyJson(dtoHierarchy,count+1));
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    public static JSONObject preparePackageDistributionCountJson(List<ShipmentDto> pkgDistrList, Set<MapCoordinatesDto> mapCoordinates) throws JSONException {
        JSONArray nodesArray = new JSONArray();
        int counter = 0;
        Double totalPackageCount = 0d;
        Map<String, Double> countryWiseCountMap = new HashMap<String, Double>();
        for(ShipmentDto pkgDistr : pkgDistrList){
            if(pkgDistr != null) {
                String  city = pkgDistr.getReceiverCity() != null ? pkgDistr.getReceiverCity() : "";
                String state = pkgDistr.getReceiverState() != null ? pkgDistr.getReceiverState() : "";
                String country = pkgDistr.getReceiverCountry() != null ? pkgDistr.getReceiverCountry() : "";
                Double packageCount = pkgDistr.getPackageCount().doubleValue();
                String address = city+","+state+","+country;

                MapCoordinatesDto mapCoordinate = findMapCoordinateByAddress(mapCoordinates, address);
                if (mapCoordinate != null && mapCoordinate.getLongitude() != null && mapCoordinate.getLatitude() != null) {
                    JSONArray longLatJsonArray = new JSONArray();
                    longLatJsonArray.put(mapCoordinate.getLongitude());
                    longLatJsonArray.put(mapCoordinate.getLatitude());
                    JSONObject  nodeInfoObj = new JSONObject();

                    totalPackageCount += packageCount;

                    if( !countryWiseCountMap.containsKey(country)) {
                        countryWiseCountMap.put(country, packageCount);
                    } else {
                        countryWiseCountMap.put(country, countryWiseCountMap.get(country)+packageCount );
                    }

                    nodeInfoObj.put("id",counter++);
                    nodeInfoObj.put("name",city);
                    nodeInfoObj.put("value", packageCount);
                    nodeInfoObj.put("coordinates", longLatJsonArray);
                    nodesArray.put(nodeInfoObj);
                }
            }
        }

        Iterator<String> countryWiseCountIterator = countryWiseCountMap.keySet().iterator();
        JSONArray countryWisePercentArray = new JSONArray();

        while (countryWiseCountIterator.hasNext()) {
            String country = countryWiseCountIterator.next();


            Double packageCount = countryWiseCountMap.get(country);


            Double percentage = (packageCount/totalPackageCount)*100d;
            String percentageRange = null;

            if (percentage <= 1) {
                percentageRange = "0% - 1%";

            } else if (percentage > 1 && percentage <= 2) {
                percentageRange = "1% - 2%";

            } else if (percentage > 2 && percentage <= 4) {
                percentageRange = "2% - 4%";

            } else if (percentage > 4 && percentage <= 6) {
                percentageRange = "4% - 6%";

            } else if (percentage > 6 && percentage <= 8) {
                percentageRange = "6% - 8%";

            } else if (percentage > 8 && percentage <= 10) {
                percentageRange = "8% - 10%";

            } else if (percentage > 10 && percentage <= 15) {
                percentageRange = "10% - 15%";

            } else if (percentage > 15 && percentage <= 25) {
                percentageRange = "15% - 25%";

            } else if (percentage >25 && percentage <= 60) {
                percentageRange = "25% - 60%";

            } else if (percentage > 60 ) {
                percentageRange = "60% - 100%";
            }

            JSONObject countryObj = new JSONObject();
            countryObj.put("id", country);
            countryObj.put("range", percentageRange);
            countryWisePercentArray.put(countryObj);
        }

        JSONObject finalObject = new JSONObject();
        finalObject.put("nodesData", nodesArray);
        finalObject.put("countryWisePercentage", countryWisePercentArray);
        return finalObject;
    }

    public static JSONObject prepareJsonForAverageChartByWeekly(List<CommonWeekChartDto> weekChartList, boolean isWeight, boolean isSpend, boolean manipulate) throws JSONException {
        JSONObject returnJson = new JSONObject();
        JSONArray returnArray = new JSONArray();
        int count = 0;
        String fromDate = null;
        String toDate = null;
        double totalAmount = 0;
        long totalCount = 0;
        int previousWeekNo = 0;
        String previousWeekLastDay = null;

        for (CommonWeekChartDto weekChart : weekChartList) {
            if (weekChart != null) {
                JSONArray dataArray = new JSONArray();
                String date = DateUtil.format(weekChart.getBillDate(), "yyyy-MM-dd");
                int currentWeekNo = weekChart.getWeekNumber();
                if (count == 0) {
                    previousWeekNo = currentWeekNo;
                }
                dataArray.put(date);
                dataArray.put(0);
                returnArray.put(dataArray);

                if (previousWeekNo == currentWeekNo) {
                    totalAmount = totalAmount + weekChart.getAmount();
                    totalCount = totalCount + weekChart.getCount();
                } else {
                    previousWeekNo = currentWeekNo;
                    returnArray.getJSONArray(count - 1).put(0, previousWeekLastDay);
                    if(isWeight && manipulate){
                        returnArray.getJSONArray(count - 1).put(1, CommonUtil.round(totalAmount / totalCount, 1));
                    }else if(isSpend && manipulate){
                        returnArray.getJSONArray(count - 1).put(1, CommonUtil.round(totalAmount / totalCount, 2));
                    }else{
                        returnArray.getJSONArray(count - 1).put(1, totalAmount / totalCount);
                    }

                    totalAmount = weekChart.getAmount();
                    totalCount = weekChart.getCount();

                }
                previousWeekLastDay = date;
                if (count == 0) {
                    fromDate = date;
                }
                toDate = date;
                dataArray = null;
                count++;
            }
        }
        if (returnArray.length() > 0) {
            returnArray.getJSONArray(count - 1).put(0, previousWeekLastDay);
            if(isWeight && manipulate){
                returnArray.getJSONArray(count - 1).put(1, totalCount != 0 ? CommonUtil.round(totalAmount / totalCount, 1) : 0);
            }else if(isSpend && manipulate){
                returnArray.getJSONArray(count - 1).put(1, totalCount != 0 ? CommonUtil.round(totalAmount / totalCount, 2) : 0);
            }else{
                returnArray.getJSONArray(count - 1).put(1, totalCount != 0 ? totalAmount / totalCount : 0);
            }
        }
        returnJson.put("values", returnArray);
        returnJson.put("fromDate", fromDate);
        returnJson.put("toDate", toDate);
        return returnJson;
    }

    public static JSONObject prepareJsonForAverageChartByPeriod(List<CommonPeriodChartDto> commonPeriodChartList, String unit, boolean isWeight, boolean isSpend, boolean manipulate) throws JSONException {
        JSONObject returnJson = new JSONObject();
        JSONArray returnArray = new JSONArray();
        int count = 0;
        String fromDate = null;
        String toDate = null;
        double totalAmount =0;
        long totalCount =0;

        if (commonPeriodChartList != null && !commonPeriodChartList.isEmpty()) {
            for (CommonPeriodChartDto periodChart : commonPeriodChartList) {
                if (periodChart != null) {
                    totalAmount = totalAmount + periodChart.getAmount();
                    totalCount = totalCount + periodChart.getCount();
                    JSONArray dataArray = new JSONArray();
                    String date = DateUtil.format(periodChart.getBillDate(), "yyyy-MM-dd");
                    dataArray.put(date);
                    if (unit.equalsIgnoreCase("day")) {
                        if(isWeight && manipulate){
                            dataArray.put(null == periodChart.getAverageAmount() ? 0d : CommonUtil.round(periodChart.getAverageAmount(), 1));
                        }else if(isSpend && manipulate){
                            dataArray.put(null == periodChart.getAverageAmount() ? 0d : CommonUtil.round(periodChart.getAverageAmount(), 2));
                        }else{
                            dataArray.put(null == periodChart.getAverageAmount() ? 0d : periodChart.getAverageAmount());
                        }
                    } else {
                        dataArray.put(0);
                    }
                    returnArray.put(dataArray);
                    if (count == 0) {
                        fromDate = date;
                    }
                    toDate = date;
                    dataArray = null;
                    count++;
                }

                if (!unit.equalsIgnoreCase("day") && returnArray.length() > 0) {
                    returnArray.getJSONArray(0).put(0, fromDate);
                    if(isWeight && manipulate){
                        returnArray.getJSONArray(0).put(1, totalCount != 0 ? CommonUtil.round(totalAmount / totalCount, 1) : 0);
                    }else if(isSpend && manipulate){
                        returnArray.getJSONArray(0).put(1, totalCount != 0 ? CommonUtil.round(totalAmount / totalCount, 2) : 0);
                    }else{
                        returnArray.getJSONArray(0).put(1, totalCount != 0 ? totalAmount / totalCount : 0);
                    }
                }

                returnJson.put("values", returnArray);
                returnJson.put("fromDate", fromDate);
                returnJson.put("toDate", toDate);
            }
        }
        return returnJson;
    }

    public static JSONObject prepareCarrierWiseMonthlySpendJson(List<CarrierWiseMonthlySpendDto> monthlySpendList) throws JSONException {
        JSONObject returnJson = new JSONObject();

        if (monthlySpendList != null && !monthlySpendList.isEmpty()) {
            Map<String, HashMap<String, HashMap<String, Double>>> modesMap = new LinkedHashMap<>();
            List<String> modesList = new ArrayList<String>();
            List<String> monthsList = new ArrayList<String>();
            Map<String, Double> monthlyTotalSpend = new HashMap<String, Double>();

            for (CarrierWiseMonthlySpendDto monthlySpend : monthlySpendList) {
                if (monthlySpend != null) {
                    String month = monthlySpend.getBillingDate();
                    String carrierName = monthlySpend.getCarrierName();
                    Double spend = monthlySpend.getNetCharges();

                    if (!modesList.contains(carrierName)) {
                        modesList.add(carrierName);
                    }
                    if (!monthsList.contains(month)) {
                        monthsList.add(month);
                    }
                    if (modesMap.containsKey(carrierName)) {
                        HashMap<String, HashMap<String, Double>> modesDataMap = modesMap.get(carrierName);
                        if (!modesDataMap.containsKey(month)) {
                            HashMap<String, Double> eachMonthData = new HashMap<>();
                            eachMonthData.put("spend", spend);
                            modesDataMap.put(month, eachMonthData);
                        } else {
                            HashMap<String, Double> eachQuaterData = modesDataMap.get(month);
                            eachQuaterData.put("spend", eachQuaterData.get("spend") + spend);
                        }
                    } else {
                        HashMap<String, HashMap<String, Double>> monthsWiseMap = new HashMap<>();
                        HashMap<String, Double> eachMonthData = new HashMap<>();
                        eachMonthData.put("spend", spend);

                        monthsWiseMap.put(month, eachMonthData);
                        modesMap.put(carrierName, monthsWiseMap);
                    }

                    if (monthlyTotalSpend.containsKey(month)) {
                        double totalSpend = monthlyTotalSpend.get(month);
                        totalSpend += spend;
                        monthlyTotalSpend.put(month, totalSpend);
                    } else {
                        monthlyTotalSpend.put(month, spend);
                    }
                }
            }

            JSONObject finalObject = new JSONObject();
            JSONArray modesArray = new JSONArray();

            for (String mode : modesList) {
                JSONObject modeWiseDataObj = new JSONObject();
                HashMap<String, HashMap<String, Double>> monthsWiseMap = modesMap.get(mode);
                modeWiseDataObj.put("name", mode);
                Double totalSpendForAllMonths = 0.0;
                for (String month : monthsList) {
                    String spend = "0";
                    if (monthsWiseMap != null && monthsWiseMap.containsKey(month)) {
                        spend = commaSeperatedDecimalFormat.format(monthsWiseMap.get(month).get("spend"));
                    }
                    modeWiseDataObj.put(month, spend);
                    totalSpendForAllMonths = totalSpendForAllMonths + Double.parseDouble(spend.replace(",", ""));
                }
                modeWiseDataObj.put("Total", commaSeperatedDecimalFormat.format(totalSpendForAllMonths));
                modesArray.put(modeWiseDataObj);
            }

            if (modesArray != null) {
                JSONObject totalMonthlyJson = new JSONObject();
                Double grandTotalSpend = 0.0;
                for (Map.Entry<String, Double> monthlyMap : monthlyTotalSpend.entrySet()) {
                    if (monthlyMap != null) {
                        totalMonthlyJson.put(monthlyMap.getKey(), commaSeperatedDecimalFormat.format(monthlyMap.getValue()));
                        grandTotalSpend = grandTotalSpend + monthlyMap.getValue();
                    }
                }
                totalMonthlyJson.put("Total", commaSeperatedDecimalFormat.format(grandTotalSpend));
                totalMonthlyJson.put("name", "Total");
                modesArray.put(totalMonthlyJson);
            }

            JSONArray monthsArray = new JSONArray();
            for (String month : monthsList) {
                monthsArray.put(month);
            }

            monthsArray.put("Total");

            finalObject.put("carriers", modesArray);
            finalObject.put("months", monthsArray);
            returnJson.put("values", finalObject);
        }
        return returnJson;
    }

    public static JSONObject prepareCarrSpendAnalysisJson(List<CarrierSpendAnalysisDto> spendAnalysisList) throws JSONException {
        JSONObject returnJson = new JSONObject();
        JSONArray returnArray = null;
        JSONObject statusJson = null;
        JSONArray headersJson = new JSONArray();

        if (spendAnalysisList != null && spendAnalysisList.size() > 0) {
            returnArray = new JSONArray();

            Double othersSpendDbl = 0d;
            Double othersPercSpendDbl = 0d;
            Double othersNoOfShipmentsDbl = 0d;
            Double othersPercShipmentsDbl = 0d;
            Double othersTotalWeightDbl = 0d;
            Double othersCostPerShpmntDbl = 0d;
            Double othersWeightPerShmntDbl = 0d;
            Double othersCostWeightDbl = 0d;

            Double NonothersSpendDbl = 0d;
            Double NonothersPercSpendDbl = 0d;
            Double NonothersNoOfShipmentsDbl = 0d;
            Double NonothersPercShipmentsDbl = 0d;
            Double NonothersTotalWeightDbl = 0d;
            Double NonothersCostPerShpmntDbl = 0d;
            Double NonothersWeightPerShmntDbl = 0d;
            Double NonothersCostWeightDbl = 0d;



            for (int i=0;i<spendAnalysisList.size();i++) {
                CarrierSpendAnalysisDto analysisDto = spendAnalysisList.get(i);



                if(i<10)
                {
                    if (analysisDto != null) {
                        statusJson = new JSONObject();
                        statusJson.put("id", analysisDto.getId());
                        statusJson.put("Carrier", analysisDto.getCarrierName());
                        statusJson.put("Spend", commaSeperatedDecimalFormat.format(analysisDto.getSpend()));
                        statusJson.put("% of Total Spend", commaSeperatedDecimalFormat.format(analysisDto.getPercSpend())+"%");
                        statusJson.put("# of Shipments", commaSeperatedDecimalFormat.format(analysisDto.getNoOfShipments()));
                        statusJson.put("% of Total Shpts", commaSeperatedDecimalFormat.format(analysisDto.getPercShipments())+"%");
                        statusJson.put("Total Weight", commaSeperatedDecimalFormat.format(analysisDto.getTotalWeight()));
                        statusJson.put("Cost/Shipment", commaSeperatedDecimalFormat2.format(analysisDto.getCostPerShpmnt()));
                        statusJson.put("Weight/Shipment", commaSeperatedDecimalFormat1.format(analysisDto.getWeightPerShmnt()));
                        statusJson.put("Cost/Weight", commaSeperatedDecimalFormat2.format(analysisDto.getCostWeight()));

                        NonothersSpendDbl = NonothersSpendDbl + analysisDto.getSpend();
                        NonothersPercSpendDbl = NonothersPercSpendDbl + analysisDto.getPercSpend();
                        NonothersNoOfShipmentsDbl = NonothersNoOfShipmentsDbl + analysisDto.getNoOfShipments();
                        NonothersPercShipmentsDbl = NonothersPercShipmentsDbl + analysisDto.getPercShipments() ;

                        NonothersTotalWeightDbl = NonothersTotalWeightDbl + analysisDto.getTotalWeight();
                        NonothersCostPerShpmntDbl = NonothersCostPerShpmntDbl + analysisDto.getCostPerShpmnt();
                        NonothersWeightPerShmntDbl= NonothersWeightPerShmntDbl + analysisDto.getWeightPerShmnt();
                        NonothersCostWeightDbl = NonothersCostWeightDbl + analysisDto.getCostWeight();

                        returnArray.put(statusJson);
                        statusJson = null;
                    }
                }
                else {

                    othersSpendDbl = othersSpendDbl + analysisDto.getSpend();
                    othersPercSpendDbl = othersPercSpendDbl + analysisDto.getPercSpend();
                    othersNoOfShipmentsDbl = othersNoOfShipmentsDbl + analysisDto.getNoOfShipments();
                    othersPercShipmentsDbl = othersPercShipmentsDbl + analysisDto.getPercShipments() ;

                    othersTotalWeightDbl = othersTotalWeightDbl + analysisDto.getTotalWeight();
                    othersCostPerShpmntDbl = othersCostPerShpmntDbl + analysisDto.getCostPerShpmnt();
                    othersWeightPerShmntDbl= othersWeightPerShmntDbl + analysisDto.getWeightPerShmnt();
                    othersCostWeightDbl = othersCostWeightDbl + analysisDto.getCostWeight();

                }

                if(i==spendAnalysisList.size()-1)
                {

                    Double totalPercSpendDbl = NonothersSpendDbl+ othersSpendDbl ;
                    Double totalPercShipmentsDbl = NonothersNoOfShipmentsDbl+ othersNoOfShipmentsDbl ;
                    if(totalPercSpendDbl==0)
                        othersPercSpendDbl = 0d ;
                    else
                        othersPercSpendDbl =((othersSpendDbl/(NonothersSpendDbl+ othersSpendDbl))*100) ;

                    if(totalPercShipmentsDbl==0)
                        othersPercShipmentsDbl = 0d ;
                    else
                        othersPercShipmentsDbl =  ((othersNoOfShipmentsDbl/(NonothersNoOfShipmentsDbl+ othersNoOfShipmentsDbl))*100) ;

                    if(i>=10)
                    {
                        statusJson = new JSONObject();
                        statusJson.put("id", 0);
                        statusJson.put("Carrier", "Others");
                        statusJson.put("Spend", commaSeperatedDecimalFormat.format(othersSpendDbl));
                        statusJson.put("% of Total Spend", commaSeperatedDecimalFormat.format( othersPercSpendDbl)+"%");
                        statusJson.put("# of Shipments", commaSeperatedDecimalFormat.format(othersNoOfShipmentsDbl));
                        statusJson.put("% of Total Shpts", commaSeperatedDecimalFormat.format( othersPercShipmentsDbl)+"%");
                        statusJson.put("Total Weight", commaSeperatedDecimalFormat.format(othersTotalWeightDbl));
                        statusJson.put("Cost/Shipment", commaSeperatedDecimalFormat2.format(othersCostPerShpmntDbl));
                        statusJson.put("Weight/Shipment", commaSeperatedDecimalFormat1.format(othersWeightPerShmntDbl));
                        statusJson.put("Cost/Weight", commaSeperatedDecimalFormat2.format(othersCostWeightDbl));

                        returnArray.put(statusJson);
                        statusJson = null;

                    }


                    // Add Total row of Pivot Table
                    statusJson = new JSONObject();
                    statusJson.put("id", -1);
                    statusJson.put("Carrier", "Total");
                    statusJson.put("Spend", commaSeperatedDecimalFormat.format(NonothersSpendDbl+ othersSpendDbl));
                    statusJson.put("% of Total Spend", commaSeperatedDecimalFormat.format(100)+"%");
                    statusJson.put("# of Shipments", commaSeperatedDecimalFormat.format(NonothersNoOfShipmentsDbl+othersNoOfShipmentsDbl));
                    statusJson.put("% of Total Shpts", commaSeperatedDecimalFormat.format(100)+"%");
                    statusJson.put("Total Weight", commaSeperatedDecimalFormat.format(NonothersTotalWeightDbl+othersTotalWeightDbl));

                    statusJson.put("Cost/Shipment", commaSeperatedDecimalFormat2.format((NonothersSpendDbl+ othersSpendDbl) / (NonothersNoOfShipmentsDbl+othersNoOfShipmentsDbl) ));
                    statusJson.put("Weight/Shipment", commaSeperatedDecimalFormat1.format((NonothersTotalWeightDbl+othersTotalWeightDbl) / (NonothersNoOfShipmentsDbl+othersNoOfShipmentsDbl) ));
                    statusJson.put("Cost/Weight", commaSeperatedDecimalFormat2.format( (NonothersSpendDbl+ othersSpendDbl) / (NonothersTotalWeightDbl+othersTotalWeightDbl) ));


                    returnArray.put(statusJson);
                    statusJson = null;
                }


            }
            headersJson.put("id");
            headersJson.put("Carrier");
            headersJson.put("Spend");
            headersJson.put("% of Total Spend");
            headersJson.put("# of Shipments");
            headersJson.put("% of Total Shpts");
            headersJson.put("Total Weight");
            headersJson.put("Cost/Shipment");
            headersJson.put("Weight/Shipment");
            headersJson.put("Cost/Weight");

            returnJson.put("values", returnArray);
            returnJson.put("headers",headersJson);

        }
        return returnJson;
    }



    public static JSONObject prepareModeSpendAnalysisJson(List<ServiceLevelDto> spendAnalysisList) throws JSONException {
        JSONObject returnJson = new JSONObject();
        JSONArray returnArray = null;
        JSONObject statusJson = null;
        JSONArray headersJson = new JSONArray();

        if (spendAnalysisList != null && spendAnalysisList.size() > 0) {
            returnArray = new JSONArray();

            Double othersSpendDbl = 0d;
            Double othersPercSpendDbl = 0d;
            Double othersNoOfShipmentsDbl = 0d;
            Double othersPercShipmentsDbl = 0d;
            Double othersTotalWeightDbl = 0d;
            Double othersCostPerShpmntDbl = 0d;
            Double othersWeightPerShmntDbl = 0d;
            Double othersCostWeightDbl = 0d;

            Double NonothersSpendDbl = 0d;
            Double NonothersPercSpendDbl = 0d;
            Double NonothersNoOfShipmentsDbl = 0d;
            Double NonothersPercShipmentsDbl = 0d;
            Double NonothersTotalWeightDbl = 0d;
            Double NonothersCostPerShpmntDbl = 0d;
            Double NonothersWeightPerShmntDbl = 0d;
            Double NonothersCostWeightDbl = 0d;



            for (int i=0;i<spendAnalysisList.size();i++) {
                ServiceLevelDto analysisDto = spendAnalysisList.get(i);



                if(i<10)
                {
                    if (analysisDto != null) {
                        statusJson = new JSONObject();
                        //statusJson.put("id", analysisDto.getId());
                        statusJson.put("Mode", analysisDto.getServiceLevel());
                        statusJson.put("Spend", commaSeperatedDecimalFormat.format(analysisDto.getSpend()));
                        statusJson.put("% of Total Spend", commaSeperatedDecimalFormat.format(analysisDto.getPercSpend())+"%");
                        statusJson.put("# of Shipments", commaSeperatedDecimalFormat.format(analysisDto.getNoOfPackages()));
                        statusJson.put("% of Total Shpts", commaSeperatedDecimalFormat.format(analysisDto.getPercPackages())+"%");
                        statusJson.put("Total Weight", commaSeperatedDecimalFormat.format(analysisDto.getTotalWeight()));
                        statusJson.put("Cost/Shipment", commaSeperatedDecimalFormat2.format(analysisDto.getCostPerPackage()));
                        statusJson.put("Weight/Shipment", commaSeperatedDecimalFormat1.format(analysisDto.getWeightPerPackage()));
                        statusJson.put("Cost/Weight", commaSeperatedDecimalFormat2.format(analysisDto.getCostWeight()));

                        NonothersSpendDbl = NonothersSpendDbl + analysisDto.getSpend();
                        NonothersPercSpendDbl = NonothersPercSpendDbl + analysisDto.getPercSpend();
                        NonothersNoOfShipmentsDbl = NonothersNoOfShipmentsDbl + analysisDto.getNoOfPackages();
                        NonothersPercShipmentsDbl = NonothersPercShipmentsDbl + analysisDto.getPercPackages() ;

                        NonothersTotalWeightDbl = NonothersTotalWeightDbl + analysisDto.getTotalWeight();
                        NonothersCostPerShpmntDbl = NonothersCostPerShpmntDbl + analysisDto.getCostPerPackage();
                        NonothersWeightPerShmntDbl= NonothersWeightPerShmntDbl + analysisDto.getWeightPerPackage();
                        NonothersCostWeightDbl = NonothersCostWeightDbl + analysisDto.getCostWeight();

                        returnArray.put(statusJson);
                        statusJson = null;
                    }
                }
                else {

                    othersSpendDbl = othersSpendDbl + analysisDto.getSpend();
                    othersPercSpendDbl = othersPercSpendDbl + analysisDto.getPercSpend();
                    othersNoOfShipmentsDbl = othersNoOfShipmentsDbl + analysisDto.getNoOfPackages();
                    othersPercShipmentsDbl = othersPercShipmentsDbl + analysisDto.getPercPackages();

                    othersTotalWeightDbl = othersTotalWeightDbl + analysisDto.getTotalWeight();
                    othersCostPerShpmntDbl = othersCostPerShpmntDbl + analysisDto.getCostPerPackage();
                    othersWeightPerShmntDbl= othersWeightPerShmntDbl + analysisDto.getWeightPerPackage();
                    othersCostWeightDbl = othersCostWeightDbl + analysisDto.getCostWeight();

                }

                if(i==spendAnalysisList.size()-1)
                {

                    Double totalPercSpendDbl = NonothersSpendDbl+ othersSpendDbl ;
                    Double totalPercShipmentsDbl = NonothersNoOfShipmentsDbl+ othersNoOfShipmentsDbl ;
                    if(totalPercSpendDbl==0)
                        othersPercSpendDbl = 0d ;
                    else
                        othersPercSpendDbl =((othersSpendDbl/(NonothersSpendDbl+ othersSpendDbl))*100) ;

                    if(totalPercShipmentsDbl==0)
                        othersPercShipmentsDbl = 0d ;
                    else
                        othersPercShipmentsDbl =  ((othersNoOfShipmentsDbl/(NonothersNoOfShipmentsDbl+ othersNoOfShipmentsDbl))*100) ;

                    if(i>=10)
                    {
                        statusJson = new JSONObject();
                        statusJson.put("id", 0);
                        statusJson.put("Mode", "Others");
                        statusJson.put("Spend", commaSeperatedDecimalFormat.format(othersSpendDbl));
                        statusJson.put("% of Total Spend", commaSeperatedDecimalFormat.format( othersPercSpendDbl)+"%");
                        statusJson.put("# of Shipments", commaSeperatedDecimalFormat.format(othersNoOfShipmentsDbl));
                        statusJson.put("% of Total Shpts", commaSeperatedDecimalFormat.format( othersPercShipmentsDbl)+"%");
                        statusJson.put("Total Weight", commaSeperatedDecimalFormat.format(othersTotalWeightDbl));
                        statusJson.put("Cost/Shipment", commaSeperatedDecimalFormat2.format(othersCostPerShpmntDbl));
                        statusJson.put("Weight/Shipment", commaSeperatedDecimalFormat1.format(othersWeightPerShmntDbl));
                        statusJson.put("Cost/Weight", commaSeperatedDecimalFormat2.format(othersCostWeightDbl));

                        returnArray.put(statusJson);
                        statusJson = null;

                    }


                    // Add Total row of Pivot Table
                    statusJson = new JSONObject();
                    statusJson.put("id", -1);
                    statusJson.put("Mode", "Total");
                    statusJson.put("Spend", commaSeperatedDecimalFormat.format(NonothersSpendDbl+ othersSpendDbl));
                    statusJson.put("% of Total Spend", commaSeperatedDecimalFormat.format(100)+"%");
                    statusJson.put("# of Shipments", commaSeperatedDecimalFormat.format(NonothersNoOfShipmentsDbl+othersNoOfShipmentsDbl));
                    statusJson.put("% of Total Shpts", commaSeperatedDecimalFormat.format(100)+"%");
                    statusJson.put("Total Weight", commaSeperatedDecimalFormat.format(NonothersTotalWeightDbl+othersTotalWeightDbl));

                    statusJson.put("Cost/Shipment", commaSeperatedDecimalFormat2.format((NonothersSpendDbl+ othersSpendDbl) / (NonothersNoOfShipmentsDbl+othersNoOfShipmentsDbl) ));
                    statusJson.put("Weight/Shipment", commaSeperatedDecimalFormat1.format((NonothersTotalWeightDbl+othersTotalWeightDbl) / (NonothersNoOfShipmentsDbl+othersNoOfShipmentsDbl) ));
                    statusJson.put("Cost/Weight", commaSeperatedDecimalFormat2.format( (NonothersSpendDbl+ othersSpendDbl) / (NonothersTotalWeightDbl+othersTotalWeightDbl) ));

                    returnArray.put(statusJson);
                    statusJson = null;
                }


            }
            headersJson.put("id");
            headersJson.put("Mode");
            headersJson.put("Spend");
            headersJson.put("% of Total Spend");
            headersJson.put("# of Shipments");
            headersJson.put("% of Total Shpts");
            headersJson.put("Total Weight");
            headersJson.put("Cost/Shipment");
            headersJson.put("Weight/Shipment");
            headersJson.put("Cost/Weight");

            returnJson.put("values", returnArray);
            returnJson.put("headers",headersJson);

        }
        return returnJson;
    }

    public static JSONObject prepareServiceLevelAnalysisJson(List<ServiceLevelDto> serviceLevelDtoList) throws JSONException {
        JSONObject returnJson = new JSONObject();
        JSONArray returnArray = null;
        JSONObject statusJson = null;
        JSONArray headersJson = new JSONArray();

        if (serviceLevelDtoList != null && serviceLevelDtoList.size() > 0) {
            returnArray = new JSONArray();

            Double othersSpendDbl = 0d;
            Double othersPercSpendDbl = 0d;
            Double othersNoOfPckgsDbl = 0d;
            Double othersPercPckgsDbl = 0d;
            Double othersTotalWeightDbl = 0d;
            Double othersCostPerPckgsDbl = 0d;
            Double othersWeightPerPckgsDbl = 0d;
            Double othersCostWeightDbl = 0d;

            Double NonothersSpendDbl = 0d;
            Double NonothersPercSpendDbl = 0d;
            Double NonothersNoOfPckgsDbl = 0d;
            Double NonothersPercPckgsDbl = 0d;
            Double NonothersTotalWeightDbl = 0d;
            Double NonothersCostPerPckgDbl = 0d;
            Double NonothersWeightPerPckgDbl = 0d;
            Double NonothersCostWeightDbl = 0d;




            for (int i=0;i<serviceLevelDtoList.size();i++) {
                ServiceLevelDto analysisDto = serviceLevelDtoList.get(i);

                if(i<15)
                {
                    if (analysisDto != null) {
                        statusJson = new JSONObject();
                        statusJson.put("ServiceLevel", analysisDto.getServiceLevel());
                        statusJson.put("Spend", commaSeperatedDecimalFormat.format(analysisDto.getSpend()));
                        statusJson.put("% of Total Spend", commaSeperatedDecimalFormat.format(analysisDto.getPercSpend())+"%");
                        statusJson.put("# of Packages", commaSeperatedDecimalFormat.format(analysisDto.getNoOfPackages()));
                        statusJson.put("% of Total Packages", commaSeperatedDecimalFormat.format(analysisDto.getPercPackages())+"%");
                        statusJson.put("Total Weight", commaSeperatedDecimalFormat.format(analysisDto.getTotalWeight()));
                        statusJson.put("Cost/Package", commaSeperatedDecimalFormat2.format(analysisDto.getCostPerPackage()));
                        statusJson.put("Weight/Package", commaSeperatedDecimalFormat1.format(analysisDto.getWeightPerPackage()));
                        statusJson.put("Cost/Weight", commaSeperatedDecimalFormat2.format(analysisDto.getCostWeight()));

                        NonothersSpendDbl = NonothersSpendDbl + analysisDto.getSpend();
                        NonothersPercSpendDbl = NonothersPercSpendDbl + analysisDto.getPercSpend();
                        NonothersNoOfPckgsDbl = NonothersNoOfPckgsDbl + analysisDto.getNoOfPackages();
                        NonothersPercPckgsDbl = NonothersPercPckgsDbl + analysisDto.getPercPackages() ;

                        NonothersTotalWeightDbl = NonothersTotalWeightDbl + analysisDto.getTotalWeight();
                        NonothersCostPerPckgDbl = NonothersCostPerPckgDbl + analysisDto.getCostPerPackage();
                        NonothersWeightPerPckgDbl= NonothersWeightPerPckgDbl + analysisDto.getWeightPerPackage();
                        NonothersCostWeightDbl = NonothersCostWeightDbl + analysisDto.getCostWeight();

                        returnArray.put(statusJson);
                        statusJson = null;
                    }
                }
                else {

                    othersSpendDbl = othersSpendDbl + analysisDto.getSpend();
                    othersPercSpendDbl = othersPercSpendDbl + analysisDto.getPercSpend();
                    othersNoOfPckgsDbl = othersNoOfPckgsDbl + analysisDto.getNoOfPackages();
                    othersPercPckgsDbl = othersPercPckgsDbl + analysisDto.getPercPackages() ;

                    othersTotalWeightDbl = othersTotalWeightDbl + analysisDto.getTotalWeight();
                    othersCostPerPckgsDbl = othersCostPerPckgsDbl + analysisDto.getCostPerPackage();
                    othersWeightPerPckgsDbl= othersWeightPerPckgsDbl + analysisDto.getWeightPerPackage();
                    othersCostWeightDbl = othersCostWeightDbl + analysisDto.getCostWeight();

                }

                if(i==serviceLevelDtoList.size()-1)
                {

                    Double totalPercSpendDbl = NonothersSpendDbl+ othersSpendDbl ;
                    Double totalNoOfPckgsDbl = NonothersNoOfPckgsDbl+ othersNoOfPckgsDbl ;

                    if(totalPercSpendDbl==0)
                        othersPercSpendDbl = 0d ;
                    else
                        othersPercSpendDbl =((othersSpendDbl/totalPercSpendDbl)*100) ;

                    if(totalNoOfPckgsDbl == 0)
                        othersPercPckgsDbl =  0d ;
                    else
                        othersPercPckgsDbl =  ((othersNoOfPckgsDbl/totalNoOfPckgsDbl)*100) ;


                    if(i>=15)
                    {

                        statusJson = new JSONObject();
                        //statusJson.put("id", 0);
                        statusJson.put("ServiceLevel", "Remaining");
                        statusJson.put("Spend", commaSeperatedDecimalFormat.format(othersSpendDbl));
                        statusJson.put("% of Total Spend", commaSeperatedDecimalFormat.format(othersPercSpendDbl)+"%");
                        statusJson.put("# of Packages", commaSeperatedDecimalFormat.format(othersNoOfPckgsDbl));
                        statusJson.put("% of Total Packages", commaSeperatedDecimalFormat.format(othersPercPckgsDbl)+"%");
                        statusJson.put("Total Weight", commaSeperatedDecimalFormat.format(othersTotalWeightDbl));
                        statusJson.put("Cost/Package", commaSeperatedDecimalFormat2.format(othersCostPerPckgsDbl));
                        statusJson.put("Weight/Package", commaSeperatedDecimalFormat1.format(othersWeightPerPckgsDbl));
                        statusJson.put("Cost/Weight", commaSeperatedDecimalFormat2.format(othersCostWeightDbl));

                        returnArray.put(statusJson);
                        statusJson = null;
                    }

                    // Add Total row of Pivot Table
                    statusJson = new JSONObject();
                    //statusJson.put("id", -1);

                    statusJson.put("ServiceLevel", "Total");
                    statusJson.put("Spend", commaSeperatedDecimalFormat.format(NonothersSpendDbl+ othersSpendDbl));
                    statusJson.put("% of Total Spend", commaSeperatedDecimalFormat.format(100)+"%");
                    statusJson.put("# of Packages", commaSeperatedDecimalFormat.format(NonothersNoOfPckgsDbl+othersNoOfPckgsDbl));
                    statusJson.put("% of Total Packages", commaSeperatedDecimalFormat.format(100)+"%");
                    statusJson.put("Total Weight", commaSeperatedDecimalFormat.format(NonothersTotalWeightDbl+othersTotalWeightDbl));

                    statusJson.put("Cost/Package", commaSeperatedDecimalFormat2.format((NonothersSpendDbl+ othersSpendDbl) / (NonothersNoOfPckgsDbl+othersNoOfPckgsDbl) ));
                    statusJson.put("Weight/Package", commaSeperatedDecimalFormat1.format((NonothersTotalWeightDbl+othersTotalWeightDbl) / (NonothersNoOfPckgsDbl+othersNoOfPckgsDbl) ));
                    statusJson.put("Cost/Weight", commaSeperatedDecimalFormat2.format( (NonothersSpendDbl+ othersSpendDbl) / (NonothersTotalWeightDbl+othersTotalWeightDbl) ) );
                    /*statusJson.put("Cost/Package", commaSeperatedDecimalFormat2.format(NonothersCostPerPckgDbl+othersCostPerPckgsDbl));
                    statusJson.put("Weight/Package", commaSeperatedDecimalFormat1.format(NonothersWeightPerPckgDbl+othersWeightPerPckgsDbl));
                    statusJson.put("Cost/Weight", commaSeperatedDecimalFormat2.format(NonothersCostWeightDbl+othersCostWeightDbl));*/

                    returnArray.put(statusJson);
                    statusJson = null;
                }


            }
            //headersJson.put("id");
            headersJson.put("ServiceLevel");
            headersJson.put("Spend");
            headersJson.put("% of Total Spend");
            headersJson.put("# of Packages");
            headersJson.put("% of Total Packages");
            headersJson.put("Total Weight");
            headersJson.put("Cost/Package");
            headersJson.put("Weight/Package");
            headersJson.put("Cost/Weight");

            returnJson.put("values", returnArray);
            returnJson.put("headers",headersJson);

        }
        return returnJson;
    }

    }


