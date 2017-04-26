package com.envista.msi.api.dao.reports;

import com.envista.msi.api.dao.type.GenericObject;
import com.envista.msi.api.domain.PersistentContext;
import com.envista.msi.api.domain.util.QueryParameter;
import com.envista.msi.api.domain.util.StoredProcedureParameter;
import com.envista.msi.api.security.SecurityUtils;
import com.envista.msi.api.web.rest.dto.UserDetailsDto;
import com.envista.msi.api.web.rest.dto.UserProfileDto;
import com.envista.msi.api.web.rest.dto.UserProfileDto;
import com.envista.msi.api.web.rest.dto.reports.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.ParameterMode;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Sreenivas on 2/17/2017.
 */

@Repository("ReportsDao")
public class ReportsDao {

    @Value("${SUBMITTED_SYSTEM_FROM}")
    private String submittedFromSystem;

    @Inject
    private PersistentContext persistentContext;
    /**
     * Get bet reports based on userId.
     * @param userId
     * @return list<ReportResultsDto>
     */
    @Transactional( readOnly = true )
    public List<ReportResultsDto> getReportResults(Long userId) {
        return persistentContext.findEntitiesAndMapFields("ReportResults.getReportResults",
                StoredProcedureParameter.with("userId", userId));
    }
    /**
     * Update Expiry Date.
     * @param generatedRptId
     * @param expiryDate
     * @return list<ReportResultsDto>
     */
    @Transactional
    public ReportResultsDto updateExpiryDate(Long generatedRptId,String expiryDate) {
        Date toexpiryDate =convertDateFullYear(expiryDate);
        QueryParameter queryParameter = StoredProcedureParameter.with("expiryDate", toexpiryDate)
                .and("generatedRptId", generatedRptId);
        return persistentContext.findEntityAndMapFields("ReportResults.updateExpiryDate",queryParameter);
    }
    @Transactional
    public List<ReportResultsUsersListDto> getUsersList(String userName) {
        QueryParameter queryParameter = StoredProcedureParameter.with("userName",userName);
        return persistentContext.findEntitiesAndMapFields("ReportResultsUsersList.getUsersList",queryParameter);
    }

    public static Date convertDateFullYear(String date)  {
        Date sdate = new Date(System.currentTimeMillis());
        try {
            sdate = new Date(Long.parseLong(date));
        }catch(Exception e){
            e.printStackTrace();
        }
        return sdate;
    }

    public static String convertDateFullYearString(String date) {
        String dateText = "";
        try {
            Date sdate = new Date(Long.parseLong(date));
            SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
            dateText = df2.format(sdate);
        }catch(Exception e){
            e.printStackTrace();
        }
        return dateText;
    }

    /**
     * Update Expiry Date.
     * @param generatedRptId
     * @param userId
     *  @param userName
     * @return
     */
    @Transactional
    public ReportResultsDto deleteReportInResults(long generatedRptId, long userId, String userName){
        QueryParameter queryParameter = StoredProcedureParameter.with("generatedRptId", generatedRptId)
                                        .and("userId", userId)
                                        .and("userName", userName);
        return persistentContext.findEntityAndMapFields("ReportResults.deleteResultReport",queryParameter);
    }
    /**
     * Get Saved Sched Reports.
     * @param userId
     * @return
     */

    @Transactional( readOnly = true )
    public List<SavedSchedReportsDto> getSavedSchedReports(Long userId,Long folderId) {
        return persistentContext.findEntities("SavedSchedReports.gerSavedSchedReports",
                StoredProcedureParameter.with("userId", userId == null?0:userId)
                                        .and("folderId",folderId == null?0: folderId));
    }

    @Transactional
    public UpdateSavedSchedReportDto updateSavedSchedReport(UpdateSavedSchedReportDto updateSavedSchedReportDto) {
        if(updateSavedSchedReportDto.getSharetoUserId()>0) {
            QueryParameter queryParameter = StoredProcedureParameter.with("userId", updateSavedSchedReportDto.getSharetoUserId())
                    .and("savedSchedId", updateSavedSchedReportDto.getSavedSchedRptId())
                    .and("createUser",updateSavedSchedReportDto.getCreateUser())
                    .and("canEdit",updateSavedSchedReportDto.getCanEdit()==null?false:updateSavedSchedReportDto.getCanEdit());
            return persistentContext.findEntityAndMapFields("SavedReports.addUserToSavedReport", queryParameter);
        }
        else if(updateSavedSchedReportDto.getSharetoUserId()==0 && updateSavedSchedReportDto.isDeleteAll()){
            QueryParameter queryParameter = StoredProcedureParameter.with("userId", updateSavedSchedReportDto.getLoggedinuserId())
                    .and("savedSchedId", updateSavedSchedReportDto.getSavedSchedRptId());
            return persistentContext.findEntityAndMapFields("SavedReports.deleteAllSavedSchedReport", queryParameter);
        }else if(updateSavedSchedReportDto.getSharetoUserId()==0 && !updateSavedSchedReportDto.isDeleteAll()){
            QueryParameter queryParameter = StoredProcedureParameter.with("userId", updateSavedSchedReportDto.getLoggedinuserId())
                    .and("savedSchedId", updateSavedSchedReportDto.getSavedSchedRptId());
            return persistentContext.findEntityAndMapFields("SavedReports.deleteUserSavedSchedReport", queryParameter);
        }
        return new UpdateSavedSchedReportDto(0l);
    }
    @Transactional
    public UpdateSavedSchedReportDto runSavedSchedReport(UpdateSavedSchedReportDto updateSavedSchedReportDto) {
            QueryParameter queryParameter = StoredProcedureParameter.with("userId", updateSavedSchedReportDto.getLoggedinuserId())
                    .and("savedSchedId", updateSavedSchedReportDto.getSavedSchedRptId())
                    .and("submittedFromSystem", submittedFromSystem)
                    .and("createUser",updateSavedSchedReportDto.getCreateUser());
            return persistentContext.findEntityAndMapFields("SavedReports.runSavedSchedReport", queryParameter);
    }
    @Transactional
    public UpdateSavedSchedReportDto saveFromReportResults(UpdateSavedSchedReportDto updateSavedSchedReportDto) {
        QueryParameter queryParameter = StoredProcedureParameter.with("userId", updateSavedSchedReportDto.getLoggedinuserId())
                .and("savedSchedId", updateSavedSchedReportDto.getSavedSchedRptId())
                .and("createUser",updateSavedSchedReportDto.getCreateUser())
                .and("reportName",updateSavedSchedReportDto.getReportName())
                .and("rptFolderId",updateSavedSchedReportDto.getRptFolderId()==null?0:updateSavedSchedReportDto.getRptFolderId());
        return persistentContext.findEntityAndMapFields("SavedReports.saveFromReportResults", queryParameter);
    }
    @Transactional
    public ReportResultsUsersListDto pushToUser(List<ReportResultsUsersListDto> reportResultsUsersListDto) {

        for(ReportResultsUsersListDto userDto : reportResultsUsersListDto) {
            QueryParameter queryParameter = StoredProcedureParameter.with("userId", userDto.getUserId())
                    .and("savedSchedId", userDto.getSavedSchedRptId())
                    .and("generatedRptId",userDto.getGeneratedRptId())
                    .and("createUser", userDto.getCreateUser());
            return persistentContext.findEntityAndMapFields("ReportResultsUsersList.pushToUser", queryParameter);
        }
        return new ReportResultsUsersListDto(0l);
    }
    /**
     * @param userId
     * @return List<ReportModesDto>
     */
    @Transactional
    public List<ReportModesDto> getReportForModes(Long userId) {
        return persistentContext.findEntitiesAndMapFields("ReportModes.getReportModeList",
                StoredProcedureParameter.with("p_user_id", userId));
    }
    /**
     * @param rptId
     * @param userId
     * @return List<ReportCustomerCarrierDto>
     */
    public List<ReportCustomerCarrierDto> getReportCustomers(Long rptId, Long userId){
        QueryParameter queryParameter = StoredProcedureParameter.with("p_user_id", userId)
                .and("p_rpt_id", rptId);
        return persistentContext.findEntities("ReportCustomerCarrier.getReportCustomer",queryParameter);
    }
    /**
     * @param rptId
     * @param userId
     * @return List<ReportCustomerCarrierDto>
     */
    public List<ReportCustomerCarrierDto> getReportCarrier(Long rptId, Long userId){
        QueryParameter queryParameter = StoredProcedureParameter.with("p_user_id", userId)
                .and("p_rpt_id", rptId);
        return persistentContext.findEntities("ReportCustomerCarrier.getReportCarrier",queryParameter);
    }
    /**
     * @param customerIdsCSV
     * @return List<ReportCustomerCarrierDto>
     */
    public List<ReportCustomerCarrierDto> getCustomerLevels(String customerIdsCSV){
        return persistentContext.findEntities("ReportCustomerCarrier.getCustomerLevels",StoredProcedureParameter.with("p_cusatomer_ids", customerIdsCSV));
    }
    /**
     * @param rptId
     * @return List<ReportFormatDto>
     */
    public List<ReportFormatDto> getReportFormat(Long rptId){
        return persistentContext.findEntities("ReportFormat.getReportFormat",StoredProcedureParameter.with("p_rpt_id", rptId));
    }

    @Transactional
    public ReportFolderDto createReportFolder(ReportFolderDto reportFolderDto, UserProfileDto userProfileDto){
        QueryParameter queryParameter = StoredProcedureParameter.with("folderName",reportFolderDto.getRptFolderName())
                                        .and("createUser", (userProfileDto != null && userProfileDto.getUserName() != null ?  userProfileDto.getUserName() : "invalid" ))
                                        .and("userId", (userProfileDto != null && userProfileDto.getUserId() != null ? userProfileDto.getUserId() : 0l ) )
                                        .and("parentId", (reportFolderDto.getParentId() != null ? reportFolderDto.getParentId() : 0l ))
                                        .and("crud",1l);
        return persistentContext.findEntityAndMapFields("ReportFolder.createFolder", queryParameter);
    }

    @Transactional
    public ReportFolderDetailsDto moveReportToFolder(ReportFolderDetailsDto rptFolderDtlsDto){
        QueryParameter queryParameter = StoredProcedureParameter.with("rptFolderId",(rptFolderDtlsDto.getReportFolderId() == null ? 0 : rptFolderDtlsDto.getReportFolderId()))
                                            .and("savedSchRptId", (rptFolderDtlsDto.getSavedSchdReportId() == null ? 0 : rptFolderDtlsDto.getSavedSchdReportId()) )
                                            .and("crud",1l);
        return persistentContext.findEntityAndMapFields("ReportFolderDtls.createRow",queryParameter);
    }

    @Transactional
    public SavedSchedReportsDto changeOwnerBasedonSSRptId(String currentUserName,Long currentUserId,String newUserName,Long newUserId,Long ssRptId){
        QueryParameter queryParameter = StoredProcedureParameter.with("currentUserName",currentUserName)
                                                        .and("currentUserId",currentUserId)
                                                        .and("newUserName",newUserName)
                                                        .and("newUserId",newUserId)
                                                        .and("ssRptId",ssRptId);
        return persistentContext.findEntityAndMapFields("SavedSchedReports.changeOwnerBasedOnSSRptId",queryParameter);
    }

    @Transactional
    public ReportSavedSchdCriteriaDto updateSavedSchdCriteria(Long ssRptId,Long rptDtlsId,String assignOperator,String value,Long isMatchCase,String createUser,String andOrOperator ){
        QueryParameter queryParameter = StoredProcedureParameter.with("savedSchedRptId",ssRptId)
                                                            .and("rptDetailsId",rptDtlsId)
                                                            .and("assignOperator",assignOperator)
                                                            .and("value",value)
                                                            .and("isMatchCase",isMatchCase)
                                                            .and("createUser",createUser)
                                                            .and("andOrOperator",andOrOperator);

        return persistentContext.findEntityAndMapFields("ReportSavedSchdCrit.insertRecord",queryParameter);
    }

    @Transactional
    public ReportsInclColDto updateInclCol(Long ssRptId,Long rptDtlsId,String createUser ){
        QueryParameter queryParameter = StoredProcedureParameter.with("savedSchedRptId",ssRptId)
                .and("rptDetailsId",rptDtlsId)
                .and("createUser",createUser);

        return persistentContext.findEntityAndMapFields("ReportInclCol.insertRecord",queryParameter);
    }

    @Transactional
    public ReportsSavedSchdAccountDto updateSavedSchdAccs(Long ssRptId,Long custId,Long shipperGroupId,Long shipperId,String createUser ){
        QueryParameter queryParameter = StoredProcedureParameter.with("savedSchedRptId",ssRptId)
                .and("customerId",custId)
                .and("shipperGroupId",shipperGroupId)
                .and("shipperId",shipperId)
                .and("createUser",createUser);

        return persistentContext.findEntityAndMapFields("ReportSavedSchdAcc.insertRecord",queryParameter);
    }

    public  boolean isNumber(String strNumber) {
        try {

            Float.parseFloat(strNumber);
        } catch (Exception nfe) {
            return false;
        }
        return true;
    }


    /**
     * @param generatedRptId
     * @return List<ReportFormatDto>
     */
    public List<ReportsFilesDto> getReportFileDetails(Long generatedRptId){
        QueryParameter queryParameter = StoredProcedureParameter.with("crGeneratedRptId", generatedRptId);
        return persistentContext.findEntitiesAndMapFields("EmailReports.getReportPath",queryParameter);
    }

    public UserProfileDto getUserDetails(String userName){
        QueryParameter queryParameter = StoredProcedureParameter.with("userNameInParam", userName);
        return persistentContext.findEntity("UserProfileTb.getUserByProcUserEntity",queryParameter);
    }

    @Transactional
    public SavedSchedReportDto saveSchedReport(SavedSchedReportDto savedSchedReportDto){

        if(savedSchedReportDto.getDate1()!=null && !savedSchedReportDto.getDate1().isEmpty()){
            savedSchedReportDto.setDate1(convertDateFullYearString(savedSchedReportDto.getDate1()));
        }
        if(savedSchedReportDto.getDate2()!=null && !savedSchedReportDto.getDate2().isEmpty()){
            savedSchedReportDto.setDate2(convertDateFullYearString(savedSchedReportDto.getDate2()));
        }
        if(savedSchedReportDto.getScNextSubmitDate()!=null && !savedSchedReportDto.getScNextSubmitDate().isEmpty()){
            savedSchedReportDto.setScNextSubmitDate(convertDateFullYearString(savedSchedReportDto.getScNextSubmitDate()));
        }

        QueryParameter queryParameter = StoredProcedureParameter.with("rptId", savedSchedReportDto.getRptId()==null?0:savedSchedReportDto.getRptId())
                .and("isScheduled",(savedSchedReportDto.getScheduled()==null ? false : savedSchedReportDto.getScheduled()))
                .and("rptDateOptionsId",savedSchedReportDto.getRptDateOptionsId()==null ? 0:savedSchedReportDto.getRptDateOptionsId())
                .and("reportTypeId",savedSchedReportDto.getReportTypeId()==null ? 0:savedSchedReportDto.getReportTypeId())
                .and("reportFileName",savedSchedReportDto.getReportFileName())
                .and("dateSelectionFrequency",savedSchedReportDto.getDateSelectionFrequency())
                .and("date1",savedSchedReportDto.getDate1())
                .and("date2",savedSchedReportDto.getDate2())
                .and("periodOption",savedSchedReportDto.getPeriodOption())
                .and("lastNoOfDays",savedSchedReportDto.getLastNoOfDays()==null?0:savedSchedReportDto.getLastNoOfDays())
                .and("scTriggerBy",savedSchedReportDto.getScTriggerBy())
                .and("scScheduleType",savedSchedReportDto.getScScheduleType())
                .and("scWeeklyFrequency",savedSchedReportDto.getScWeeklyFrequency()==null?0:savedSchedReportDto.getScWeeklyFrequency())
                .and("scWeeklyMonthlyDayofWeek",savedSchedReportDto.getScWeeklyMonthlyDayofWeek())
                .and("scMonthlyDayOfMonth",savedSchedReportDto.getScMonthlyDayOfMonth()==null?0:savedSchedReportDto.getScMonthlyDayOfMonth())
                .and("scMonthlyNoOfMonths",savedSchedReportDto.getScMonthlyNoOfMonths()==null?0:savedSchedReportDto.getScMonthlyNoOfMonths())
                .and("scMonthlyPeriodicFreq",savedSchedReportDto.getScMonthlyPeriodicFrequency())
                .and("svReportStatus",savedSchedReportDto.getSvReportStatus())
                .and("scNextSubmitDate",savedSchedReportDto.getScNextSubmitDate())
                .and("carrierIds",savedSchedReportDto.getCarrierIds())
                .and("controlPayrunNumber",savedSchedReportDto.getControlPayrunNumber())
                .and("consolidate",savedSchedReportDto.getConsolidate()==null?false:savedSchedReportDto.getConsolidate())
                .and("createUser",savedSchedReportDto.getCreateUser())
                .and("criteria",savedSchedReportDto.getCriteria())
                .and("dateRangeTodayMinus1",savedSchedReportDto.getDateRangeTodayMinus1()==null?0:savedSchedReportDto.getDateRangeTodayMinus1())
                .and("dateRangeTodayMinus2",savedSchedReportDto.getDateRangeTodayMinus2()==null?0:savedSchedReportDto.getDateRangeTodayMinus2())
                .and("ftpAccountsId",(savedSchedReportDto.getFtpAccountsId() == null || savedSchedReportDto.getFtpAccountsId().toString().isEmpty()) ? 0 :savedSchedReportDto.getFtpAccountsId())
                .and("isSuppressInvoices",savedSchedReportDto.getSuppressInvoices()==null?false:savedSchedReportDto.getSuppressInvoices())
                .and("submittedFromSystem",submittedFromSystem)
                .and("isPacket",savedSchedReportDto.getPacket()==null?false:savedSchedReportDto.getPacket())
                .and("flagsJson",savedSchedReportDto.getFlagsJson())
                .and("locale",savedSchedReportDto.getLocale())
                .and("currency",savedSchedReportDto.getCurrency())
                .and("weightUom",savedSchedReportDto.getWeightUom())
                .and("rptDescr",savedSchedReportDto.getRptDescr());

        return persistentContext.findEntity("SavedSchedReports.saveSchedReport",queryParameter);

    }

    @Transactional
    public ReportPacketsDetDto saveSchedPacketReport(ReportPacketsDetDto reportPacketsDetDto){

        QueryParameter queryParameter = StoredProcedureParameter.with("savedSchedRptId", reportPacketsDetDto.getSavedSchdRptId()==null?0:reportPacketsDetDto.getSavedSchdRptId())
                .and("tabName",reportPacketsDetDto.getTabName()==null?"":reportPacketsDetDto.getTabName())
                .and("sequenceNum",reportPacketsDetDto.getSequence()==null?0:reportPacketsDetDto.getSequence())
                .and("templateId",reportPacketsDetDto.getTemplateId()==null?0:reportPacketsDetDto.getTemplateId());

        return persistentContext.findEntity("SavedSchedPackets.saveSchedPakcet",queryParameter);

    }

    @Transactional
    public ReportSavedSchdUsersDto saveSchedUser(ReportSavedSchdUsersDto saveSchedUser){

        QueryParameter queryParameter = StoredProcedureParameter.with("savedSchedRptId", saveSchedUser.getSavedSchedRptId())
                .and("userId",saveSchedUser.getUserId())
                .and("isEmailTempTobeSent",saveSchedUser.getEmailTemplateToBeSent()==null?true:saveSchedUser.getEmailTemplateToBeSent())
                .and("isReportAttachEmail",saveSchedUser.getReportAttachedMail()==null?false:saveSchedUser.getReportAttachedMail())
                .and("isReportSubscribed",saveSchedUser.getReportSubscribed()==null?true:saveSchedUser.getReportSubscribed())
                .and("createUser",saveSchedUser.getCreateUser()==null?"":saveSchedUser.getCreateUser())
                .and("isShared",saveSchedUser.getShared()==null?false:saveSchedUser.getShared())
                .and("canEdit",saveSchedUser.getCanEdit()==null?true:saveSchedUser.getCanEdit());

        return persistentContext.findEntity("SavedSchedReports.saveUsers",queryParameter);

    }
    /**
     * @param rptId
     * @return List<ReportFormatDto>
     */
    public List<ReportFormatDto> getReportDateOptions(Long rptId){
        return persistentContext.findEntities("ReportFormat.getReportDateOptions",StoredProcedureParameter.with("p_rpt_id", rptId));
    }
    /**
     * @param userId
     * @param carrierIds
     * @param rptId
     * @return List<ReportCriteriaDto>
     */
    public List<ReportColumnDto> getReportCriteria(Long userId, Long rptId, String carrierIds){
        QueryParameter queryParameter = StoredProcedureParameter.with("p_user_id",userId)
                                        .and("p_rpt_id", rptId)
                                        .and("p_carrier_ids",carrierIds);
        return persistentContext.findEntities("ReportCriteriaDto.getReportCriteria",queryParameter);
    }
    /**
     * @param userId
     * @param carrierIds
     * @param rptId
     * @return List<ReportColumnDto>
     */
    public List<ReportColumnDto> getSavedIncludeExcludeColNameOrder(Long userId, Long rptId, String carrierIds){
        QueryParameter queryParameter = StoredProcedureParameter.with("p_user_id",userId)
                .and("p_rpt_id", rptId)
                .and("p_carriers",carrierIds);
        return persistentContext.findEntities("ReportCriteriaDto.getSavedIncludeExcludeSortColByName",queryParameter);
    }
    /**
     * @param userId
     * @param carrierIds
     * @param rptId
     * @return List<ReportColumnDto>
     */
    public List<ReportColumnDto> getSavedIncludeExcludeColSequencOrder(Long userId, Long rptId, String carrierIds){
        QueryParameter queryParameter = StoredProcedureParameter.with("p_user_id",userId)
                .and("p_rpt_id", rptId)
                .and("p_carriers",carrierIds);
        return persistentContext.findEntities("ReportCriteriaDto.getSavedIncludeExcludeColBySequence",queryParameter);
    }
    /**
     * @param rptId
     * @return List<ReportCodeValueDto>
     */
    public List<ReportCodeValueDto> getReportLocaleLabel(Long rptId){
        return persistentContext.findEntities("ReportCodeValueDto.getReportLocaleLabel", StoredProcedureParameter.with("p_rpt_id", rptId));
    }
    /**
     * @param rptId
     * @return List<ReportCodeValueDto>
     */
    public List<ReportCodeValueDto> getReportCurrencyLabel(Long rptId){
        return persistentContext.findEntities("ReportCodeValueDto.getReportCurrencyLabel", StoredProcedureParameter.with("p_rpt_id", rptId));
    }
    /**
     * @param rptId
     * @return List<ReportCodeValueDto>
     */
    public List<ReportCodeValueDto> getReportWeightLabel(Long rptId){
        return persistentContext.findEntities("ReportCodeValueDto.getReportWeightLabel", StoredProcedureParameter.with("p_rpt_id", rptId));
    }
    /**
     * @param customerIds
     * @param payRunNo
     * @param checkNo
     * @return List<ReportFormatDto>
     */
    public List<ReportFormatDto> getControlNumber(String customerIds,Integer payRunNo,Integer checkNo){
        QueryParameter queryParameter = StoredProcedureParameter.with("p_customer_ids",customerIds)
                .and("p_pay_run_no", payRunNo)
                .and("p_check_no",checkNo);
        return persistentContext.findEntities("ReportFormat.getControlNumber",queryParameter);
    }
    /**
     * @param userId
     * @return List<ReportFolderHierarchyDto>
     */
    public List<ReportFolderDto> getReportFolder(Long userId){
        return persistentContext.findEntities("ReportFolder.getReportFolder", StoredProcedureParameter.with("p_user_id",userId));
    }
    /**
     * @param customerIds
     *  @param shipperGroupIds
     *   @param shipperIds
     * @return List<ReportFolderHierarchyDto>
     */
    public List<ReportFTPServerDto> getReportFTPServer(String customerIds,String shipperGroupIds ,String shipperIds){
        QueryParameter queryParameter = StoredProcedureParameter.with("customerIds",customerIds)
                .and("shipperGroupIds", shipperGroupIds)
                .and("shipperIds",shipperIds);
        return persistentContext.findEntities("ReportFTPServer.getFTPServer",queryParameter);
    }/**
     * @param rptId
     * @return List<ReportFTPServerDto>
     */
    public List<ReportFTPServerDto> getSaveRptFTPServer(Long rptId){
        return persistentContext.findEntities("ReportFTPServer.getSaveRptFTPServer", StoredProcedureParameter.with("p_rpt_id",rptId));
    }
    /**
     * @param rptId
     * @return List<ReportFTPServerDto>
     */
    public List<ReportColumnDto> getDefaultInclExclCol(Long saveSchedId,Long rptId,String createUser){
        return persistentContext.findEntities("ReportCriteriaDto.getDefaultInclExclCol",
               StoredProcedureParameter.with("p_savedSchedRptId",saveSchedId)
               .and("p_rptid",rptId).and("p_createUser",createUser));
    }

    @Transactional
    public List<ReportUserListByRptIdDto> getUsersListByRptId(Long rptId){
        QueryParameter queryParameter = StoredProcedureParameter.with("rptId", rptId == null ? 0 : rptId );
        return persistentContext.findEntities("UserListByRptId.getUsers",queryParameter);
    }

    @Transactional
    public ReportsSavedSchdAccountDto saveSchedAccountsDetails(ReportsSavedSchdAccountDto accoutsDto) {
        QueryParameter queryParameter = StoredProcedureParameter.with("savedSchedRptId",accoutsDto.getSavedSchdRptId())
                .and("customerId", accoutsDto.getCustomerId())
                .and("shipperGroupId",accoutsDto.getShipperGroupId()==null?0:accoutsDto.getShipperGroupId())
                .and("shipperId",accoutsDto.getShipperId()==null?0:accoutsDto.getShipperId())
                .and("createUser",accoutsDto.getCreateUser());
        return persistentContext.findEntity("ReportSavedSchdAcc.insertRecord",queryParameter);
    }
    @Transactional
    public ReportSavedSchdCriteriaDto saveSchedCriterisDetails(ReportSavedSchdCriteriaDto criteriaDto) {

        QueryParameter queryParameter = StoredProcedureParameter.with("savedSchedRptId",criteriaDto.getSavedSchdRptId())
                .and("rptDetailsId", criteriaDto.getRptDetailsId())
                .and("assignOperator",criteriaDto.getAssignOperator()==null?"=":criteriaDto.getAssignOperator())
                .and("value",criteriaDto.getValue())
                .and("matchCase",criteriaDto.getMatchCase()==null?false:criteriaDto.getMatchCase())
                .and("createUser",criteriaDto.getCreateUser())
                .and("andOrOperator",criteriaDto.getAndOrOperator()==null?"AND":criteriaDto.getAndOrOperator());
        return persistentContext.findEntity("ReportSavedSchdCrit.insertRecord",queryParameter);

    }

    @Transactional
    public ReportsInclColDto saveSchedIncColDetails(ReportsInclColDto inclColDto) {

        QueryParameter queryParameter = StoredProcedureParameter.with("savedSchdRptId",inclColDto.getSavedSchdRptId()==null?0:inclColDto.getSavedSchdRptId())
                .and("rptDetailsId", inclColDto.getRptDetailsId())
                .and("createUser",inclColDto.getCreateUser());
        return persistentContext.findEntity("ReportInclCol.insertRecord",queryParameter);

    }
    @Transactional
    public void saveSchedIncColDetails(List<GenericObject> inclColDtoList) throws SQLException {
        QueryParameter queryParameter = StoredProcedureParameter.withPosition(1, ParameterMode.IN, GenericObject[].class, inclColDtoList)
                .andPosition(2, ParameterMode.REF_CURSOR, void.class, null);

         persistentContext.executeStoredProcedure("shp_rpt_savesched_incl_proc",queryParameter);

    }
    @Transactional
    public void saveSchedAcctDetails(List<GenericObject> actDtoList) throws SQLException {
        QueryParameter queryParameter = StoredProcedureParameter.withPosition(1, ParameterMode.IN, GenericObject[].class, actDtoList)
                .andPosition(2, ParameterMode.REF_CURSOR, void.class, null);

        persistentContext.executeStoredProcedure("shp_rpt_savesched_actlist_proc",queryParameter);

    }

    @Transactional
    public ReportsSortDto saveSchedSortColDetails(ReportsSortDto sortColDto) {

        QueryParameter queryParameter = StoredProcedureParameter.with("savedSchedRptId",sortColDto.getSavedSchedRptId())
                .and("rptDetailsId", sortColDto.getRptDetailsId())
                .and("isSubTotRequired",sortColDto.getSubTotRequired()==null?false:sortColDto.getSubTotRequired())
                .and("isAscending",sortColDto.getAscending()==null?true:sortColDto.getAscending())
                .and("sortOrder",sortColDto.getSortOrder()==null?1:sortColDto.getSortOrder())
                .and("groupByCol",sortColDto.getGroupByCol()==null?false:sortColDto.getGroupByCol())
                .and("createUser",sortColDto.getCreateUser());

        return persistentContext.findEntity("ReportSortCol.insertRecord",queryParameter);

    }
    @Transactional
    public SavedSchedReportDto updateSchedReport(SavedSchedReportDto savedSchedReportDto) {

        if(savedSchedReportDto.getDate1()!=null && !savedSchedReportDto.getDate1().isEmpty()){
            savedSchedReportDto.setDate1(convertDateFullYearString(savedSchedReportDto.getDate1()));
        }
        if(savedSchedReportDto.getDate2()!=null && !savedSchedReportDto.getDate2().isEmpty()){
            savedSchedReportDto.setDate2(convertDateFullYearString(savedSchedReportDto.getDate2()));
        }
        if(savedSchedReportDto.getScNextSubmitDate()!=null && !savedSchedReportDto.getScNextSubmitDate().isEmpty()){
            savedSchedReportDto.setScNextSubmitDate(convertDateFullYearString(savedSchedReportDto.getScNextSubmitDate()));
        }

        QueryParameter queryParameter = StoredProcedureParameter.with("savedSchedRptId", savedSchedReportDto.getSavedSchedRptId())
                .and("rptId", savedSchedReportDto.getRptId()==null?0:savedSchedReportDto.getRptId())
                .and("isScheduled",(savedSchedReportDto.getScheduled()==null ? false : savedSchedReportDto.getScheduled()))
                .and("rptDateOptionsId",savedSchedReportDto.getRptDateOptionsId()==null ? 0:savedSchedReportDto.getRptDateOptionsId())
                .and("reportTypeId",savedSchedReportDto.getReportTypeId()==null ? 0:savedSchedReportDto.getReportTypeId())
                .and("reportFileName",savedSchedReportDto.getReportFileName())
                .and("dateSelectionFrequency",savedSchedReportDto.getDateSelectionFrequency())
                .and("date1",savedSchedReportDto.getDate1())
                .and("date2",savedSchedReportDto.getDate2())
                .and("periodOption",savedSchedReportDto.getPeriodOption())
                .and("lastNoOfDays",savedSchedReportDto.getLastNoOfDays()==null?0:savedSchedReportDto.getLastNoOfDays())
                .and("scTriggerBy",savedSchedReportDto.getScTriggerBy())
                .and("scScheduleType",savedSchedReportDto.getScScheduleType())
                .and("scWeeklyFrequency",savedSchedReportDto.getScWeeklyFrequency()==null?0:savedSchedReportDto.getScWeeklyFrequency())
                .and("scWeeklyMonthlyDayofWeek",savedSchedReportDto.getScWeeklyMonthlyDayofWeek())
                .and("scMonthlyDayOfMonth",savedSchedReportDto.getScMonthlyDayOfMonth()==null?0:savedSchedReportDto.getScMonthlyDayOfMonth())
                .and("scMonthlyNoOfMonths",savedSchedReportDto.getScMonthlyNoOfMonths()==null?0:savedSchedReportDto.getScMonthlyNoOfMonths())
                .and("scMonthlyPeriodicFreq",savedSchedReportDto.getScMonthlyPeriodicFrequency())
                .and("svReportStatus",savedSchedReportDto.getSvReportStatus())
                .and("scNextSubmitDate",savedSchedReportDto.getScNextSubmitDate())
                .and("carrierIds",savedSchedReportDto.getCarrierIds())
                .and("controlPayrunNumber",savedSchedReportDto.getControlPayrunNumber())
                .and("consolidate",savedSchedReportDto.getConsolidate()==null?false:savedSchedReportDto.getConsolidate())
                .and("createUser",savedSchedReportDto.getCreateUser())
                .and("criteria",savedSchedReportDto.getCriteria())
                .and("dateRangeTodayMinus1",savedSchedReportDto.getDateRangeTodayMinus1()==null?0:savedSchedReportDto.getDateRangeTodayMinus1())
                .and("dateRangeTodayMinus2",savedSchedReportDto.getDateRangeTodayMinus2()==null?0:savedSchedReportDto.getDateRangeTodayMinus2())
                .and("ftpAccountsId",(savedSchedReportDto.getFtpAccountsId() == null || savedSchedReportDto.getFtpAccountsId().toString().isEmpty()) ? 0 :savedSchedReportDto.getFtpAccountsId())
                .and("isSuppressInvoices",savedSchedReportDto.getSuppressInvoices()==null?false:savedSchedReportDto.getSuppressInvoices())
                .and("submittedFromSystem",submittedFromSystem)
                .and("isPacket",savedSchedReportDto.getPacket()==null?false:savedSchedReportDto.getPacket())
                .and("flagsJson",savedSchedReportDto.getFlagsJson())
                .and("locale",savedSchedReportDto.getLocale())
                .and("currency",savedSchedReportDto.getCurrency())
                .and("weightUom",savedSchedReportDto.getWeightUom())
                .and("rptDescr",savedSchedReportDto.getRptDescr());

        return persistentContext.findEntity("SavedSchedReports.updateSchedReport",queryParameter);
    }
    @Transactional
    public SavedSchedReportDto deleteChildDataSchedReport(SavedSchedReportDto savedSchedReportDto) {

        QueryParameter queryParameter = StoredProcedureParameter.with("savedSchedRptId", savedSchedReportDto.getSavedSchedRptId());

        return persistentContext.findEntity("SavedSchedReports.deleteChildDataSchedReport",queryParameter);
    }
    @Transactional
    public SavedSchedReportDto deletePacketsDataSchedReport(SavedSchedReportDto savedSchedReportDto) {

        QueryParameter queryParameter = StoredProcedureParameter.with("savedSchedRptId", savedSchedReportDto.getSavedSchedRptId());

        return persistentContext.findEntity("SavedSchedReports.deletePacketDataSchedReport",queryParameter);
    }
    @Transactional
    public SavedSchedReportDto getReportDetails(Long savedSchedRptId) {

        QueryParameter queryParameter = StoredProcedureParameter.with("savedSchedRptId", savedSchedRptId);

        return persistentContext.findEntity("SavedSchedReports.getReportDetails",queryParameter);

    }
    @Transactional
    public List<ReportPacketsDetDto> getReportPacketDtlsList(Long savedSchedRptId){
        QueryParameter queryParameter = StoredProcedureParameter.with("savedSchedRptId", savedSchedRptId == null ? 0 : savedSchedRptId );
        return persistentContext.findEntities("ReportGetPcktDetails.getReportPckts",queryParameter);
    }

    @Transactional
    public List<ReportSavedSchdUsersDto> getReportSSUsersList(Long ssRptId){
        QueryParameter queryParameter = StoredProcedureParameter.with("savedSchedRptId", ssRptId == null ? 0 : ssRptId );
        return persistentContext.findEntities("ReportGetSSUser.reportUsersList",queryParameter);
    }
    @Transactional
    public List<ReportGetSSSortDto> getReportSortList(Long ssRptId){
        QueryParameter queryParameter = StoredProcedureParameter.with("savedSchedRptId", ssRptId == null ? 0 : ssRptId );
        return persistentContext.findEntities("ReportGetSSSort.getReportSortList",queryParameter);
    }

    @Transactional
    public List<ReportGetSSCritDto> getReportSSCritList(Long ssRptId){
        QueryParameter queryParameter = StoredProcedureParameter.with("savedSchedRptId", ssRptId == null ? 0 : ssRptId );
        return persistentContext.findEntities("ReportGetSSCirt.getReportCritList",queryParameter);
    }

    @Transactional
    public List<ReportGetSSColInclDto> getReportSSColInclList(Long ssRptId){
        QueryParameter queryParameter = StoredProcedureParameter.with("savedSchedRptId", ssRptId == null ? 0 : ssRptId );
        return persistentContext.findEntities("ReportGetSSColIncl.getReportColInclList",queryParameter);
    }

    @Transactional
    public List<ReportGetSSAccDto> getReportSSAccList(Long ssRptId){
        QueryParameter queryParameter = StoredProcedureParameter.with("savedSchedRptId", ssRptId == null ? 0 : ssRptId );
        return persistentContext.findEntities("ReportGetSSAcc.getReportAccList",queryParameter);
    }
    @Transactional
    public List<ReportFormatDto> getReportTriggerOptions(Long rptId,String carrierIds){
        QueryParameter queryParameter = StoredProcedureParameter.with("p_rpt_id",rptId)
                .and("p_carriers", (carrierIds==null || (carrierIds.trim()).length()<1) ? "-1" : carrierIds);
        return persistentContext.findEntities("ReportFormat.getReportTriggerOptions",queryParameter);
    }
    @Transactional
    public ReportFolderDto deleteFolder(Long rptFolderId, Long userId) {
        QueryParameter queryParameter = StoredProcedureParameter.with("rptFolderId",rptFolderId==null?0:rptFolderId)
                .and("userId", userId==null?0:userId);
        return persistentContext.findEntityAndMapFields("ReportFolder.deleteRptFolder", queryParameter);
    }
    @Transactional
    public List<SearchUserByCustomerDto> getReportUserCustomers(Long userId){
        QueryParameter queryParameter = StoredProcedureParameter.with("p_user_id",userId);
        return persistentContext.findEntities("SearchUserByCustomer.getReportUserCustomers",queryParameter);
    }
    @Transactional
    public List<SearchUserByCustomerDto> getReportSearchUsers(Long userId,Long customerId,String fullName,String email,Boolean userOnly){
        QueryParameter queryParameter = StoredProcedureParameter.with("p_user_id",userId).and("p_customer_id",customerId==null ? 0 : customerId)
                                                  .and("p_full_name",fullName).and("p_email",email).and("p_is_user_only",userOnly ? 1 : 0);
        return persistentContext.findEntities("SearchUserByCustomer.getReportSearchUsers",queryParameter);
    }

    @Transactional
    public ReportGeneratedDetailsDto getGenReportDetails(Long genRptId){
        QueryParameter queryParameter = StoredProcedureParameter.with("crGeneratedRptId",genRptId==null?0:genRptId);
        return persistentContext.findEntityAndMapFields("EmailReports.getGenReportDetPath",queryParameter);
    }
    @Transactional
    public ReportTypeDto getReportTypeDetails(Long rptTypeId){
        QueryParameter queryParameter = StoredProcedureParameter.with("rptTypeId",rptTypeId==null?0:rptTypeId);
        return persistentContext.findEntityAndMapFields("ReportTypes.reportTypeDetails",queryParameter);
    }
    @Transactional
    public UserDetailsDto getUserDetailsById(Long userId){
        QueryParameter queryParameter = StoredProcedureParameter.with("userId",userId==null?0:userId);
        return persistentContext.findEntityAndMapFields("UserProfileTb.getUserDetailsById",queryParameter);
    }
    @Transactional
    public List<ReportFolderDto> getFolderHierarchy(Long userId){
        return persistentContext.findEntities("ReportFolder.getReportFolderHierarchy",StoredProcedureParameter.with("p_user_id",userId));
    }

    public List<SavedSchedReportsDto> getSavedSchedTemplates(Long userId) {
        return persistentContext.findEntities("SavedSchedReports.gerSavedSchedTemplates",
                StoredProcedureParameter.with("userId", userId == null?0:userId));
    }
}
