package com.envista.msi.api.web.rest;

import com.envista.msi.api.service.rtr.ParcelRTRService;
import com.envista.msi.api.web.rest.dto.rtr.ParcelAuditDetailsDto;
import com.envista.msi.api.web.rest.response.CommonResponse;
import com.envista.msi.api.web.rest.response.ErrorResponse;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by Sujit kumar on 08/06/2017.
 */

@RestController
@RequestMapping(value = "/api/parcel/rtr")
public class ParcelRTRController {

    @Inject
    private ParcelRTRService parcelRTRService;

    @RequestMapping(value = "/auditParcel", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CommonResponse> auditParcelInvoice(@RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate,
                                                             @RequestParam(required = false) String customerId, @RequestParam(required = false) String trackingNumbers) {
        CommonResponse response = new CommonResponse();
        try{
            parcelRTRService.parcelRTRRating(customerId, fromDate, toDate, trackingNumbers);
        }catch (InvalidDataAccessResourceUsageException e){
            //Need to check this case later. This is causing because of transaction management.
        }
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Parcel Audit completed successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/auditParcel", method = {RequestMethod.POST}, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_HTML_VALUE})
    public ResponseEntity<CommonResponse> auditParcelInvoice(@RequestBody String trackingNumbers) {
        CommonResponse response = new CommonResponse();
        try{
            parcelRTRService.parcelRTRRating(null, null, null, trackingNumbers);
        }catch (InvalidDataAccessResourceUsageException e){
            //Need to check this case later. This is causing because of transaction management.
        }
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Parcel Audit completed successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/auditParcelInv", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CommonResponse> auditParcelInvoice(@RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate,
                                                             @RequestParam(required = false) String customerId, @RequestParam(required = false, defaultValue = "0") Integer limit) {
        System.out.println("Start Time :: " + System.currentTimeMillis());
        String message = "Parcel Audit completed successfully";
        List<ParcelAuditDetailsDto> invoiceList = parcelRTRService.loadInvoiceIds(fromDate, toDate, customerId, limit);
        if(invoiceList != null && !invoiceList.isEmpty()){
            parcelRTRService.doParcelAuditingInvoiceNumberWise(invoiceList);
        }else{
            message = "No Invoice found!";
        }
        System.out.println("End Time :: " + System.currentTimeMillis());
        CommonResponse response = new CommonResponse();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage(message);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleParcelAuditExceptions(Exception e){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setMessage(null == e.getMessage() ? "Internal Server Error" : e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
