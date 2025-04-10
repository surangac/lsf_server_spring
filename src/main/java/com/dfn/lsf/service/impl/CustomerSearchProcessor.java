package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.Helper;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defined in InMessageHandlerAdminCbr
 * route : CUSTOMER_SEARCH_PROCESSOR
 * Handling Message types :
 * - MESSAGE_TYPE_CUSTOMER_SEARCH_PROCESSOR = 12;
 */
@Service
@RequiredArgsConstructor
@Qualifier("12")
public class CustomerSearchProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CustomerSearchProcessor.class);

    private final Gson gson;
    private final LSFRepository lsfRepository;
    private final Helper helper;

    @Override
    public String process(String request) {
        Map<String, Object> map = new HashMap<String, Object>();
        map = gson.fromJson(request, map.getClass());
        String subMessageType = map.get("subMessageType").toString();
        switch (subMessageType) {
            case LsfConstants.GET_CUSTOMER_LIST:  /*----get approved customer list from LSF server with pagination---*/
                return getCustomerList(map);
            case LsfConstants.R_CUSTOMER_SEARCH:  /*----search customer from OMS, using service---*/
                return searchOmsCustomer(map);
            case LsfConstants.PAGE_COUNTER:  /*---Number of pages according to page size---*/
                return getPageCount(map);
            default:
                return null;
        }
    }

    private String getCustomerList(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        List<Map<String, Object>> responseMapList = new ArrayList<>();
        if (map.containsKey("pageSize") && map.containsKey("pageNumber")) {
            String pageSize = (String) map.get("pageSize");
            String pageNumber = (String) map.get("pageNumber");
            logger.debug("===========LSF : (getCustomerList)-REQUEST, pageSize "
                         + pageSize
                         + " , pageNumber:"
                         + pageNumber);
            try {
                List<MurabahApplication> murabahApplicationList =
                        lsfRepository.getLimitedApprovedMurabahApplication(pageSize,
                                                                                                                     pageNumber);
                for (MurabahApplication application : murabahApplicationList) {
                    Map<String, Object> customerDetailsMap = new HashMap<>();
                    customerDetailsMap.put("customerId", application.getCustomerId());
                    customerDetailsMap.put("fullName", application.getFullName());
                    customerDetailsMap.put("mobileNo", application.getMobileNo());
                    customerDetailsMap.put("occupation", application.getOccupation());
                    customerDetailsMap.put("applicationID", application.getId());
                    customerDetailsMap.put("statusDescription", application.getStatusDescription());
                    responseMapList.add(customerDetailsMap);
                }
                cmr.setResponseCode(200);
                cmr.setResponseObject(responseMapList);
            } catch (Exception e) {
                logger.info(e.getMessage());
                cmr.setResponseCode(500);
                cmr.setResponseMessage("Exception Occur | Database Connection Exception");
                return gson.toJson(cmr);
            }
        } else {
            cmr.setResponseCode(500);
            cmr.setResponseMessage("Page size and page number values are null");
        }
        logger.info("===========LSF : (getCustomerList)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String searchOmsCustomer(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        Map<String, Object> omsRequestMap = new HashMap<>();
        List<Map<String, Object>> responseMapList = new ArrayList<>();
        String pageSize = (String) map.get("pageSize");
        String pageNumber = (String) map.get("pageNumber");
        if (map.containsKey("pageSize") && !"".equals(map.get("pageSize")) && !"0".equals(map.get("pageSize"))) {
            int toRecords = (Integer.valueOf(pageNumber) - 1) * Integer.valueOf(pageSize) + 1;
            int fromRecords = Integer.valueOf(pageNumber) * Integer.valueOf(pageSize);
            String response = "";
            StringBuilder builder = new StringBuilder();
            if (map.containsKey("filterCriteria") && map.containsKey("filterValue")) {
                if (!"".equals(map.get("filterValue"))) {
                    builder.append((String) map.get("filterCriteria"));
                    builder.append("|");
                    builder.append((String) map.get("filterValue"));
                    builder.append("|");
                    builder.append(toRecords);
                    builder.append("|");
                    builder.append(fromRecords);
                    omsRequestMap.put("reqType", LsfConstants.CUSTOMER_SEARCH);
                    omsRequestMap.put("params", builder.toString());
                    logger.debug("===========LSF : (customerSearch)-REQUEST, OMS REQUEST "
                                 + gson.toJson(omsRequestMap));
                    response = helper.getCustomerRelatedOMSData(gson.toJson(omsRequestMap)).toString();
                    ArrayList omsResponseMapList = (ArrayList) ((Map) gson.fromJson(response, map.getClass())
                                                                          .get("responseObject")).get("customerrecs");
                    String recordCount = String.valueOf(Math.round((Double) ((Map) gson.fromJson(
                                                                                               response,
                                                                                               map.getClass())
                                                                                       .get("responseObject")).get(
                            "recordCount")));
                    int pageCount = (Integer.valueOf(recordCount) + Integer.valueOf(pageSize) - 1) / Integer.valueOf(
                            pageSize);
                    if (omsResponseMapList != null && omsResponseMapList.size() != 0) {
                        for (Object o : omsResponseMapList) {
                            Map customerMap = (Map) o;
                            Map<String, Object> customerDetailsMap = new HashMap<>();
                            customerDetailsMap.put("customerId", customerMap.get("id"));
                            customerDetailsMap.put(
                                    "fullName",
                                    customerMap.get("firstName") + " " + customerMap.get("lastName"));
                            customerDetailsMap.put("mobileNo", customerMap.get("mobileNumber"));
                            customerDetailsMap.put("occupation", customerMap.get("jobTitle"));
                            responseMapList.add(customerDetailsMap);
                        }
                        cmr.setResponseCode(200);
                        cmr.setResponseObject(responseMapList);
                        cmr.setResponseMessage(String.valueOf(pageCount));
                    } else {
                        cmr.setResponseCode(200);
                        cmr.setResponseObject(responseMapList);
                        cmr.setResponseMessage("Invalid customer");
                    }
                } else {
                    cmr.setResponseCode(500);
                    cmr.setResponseMessage("Must sent filter value");
                }
                logger.debug("===========LSF : (customerSearch)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
                return gson.toJson(cmr);
            } else {
                cmr.setResponseCode(500);
                cmr.setResponseMessage("please enter the value (Customer Id | Customer Name");
                logger.debug("===========LSF : (customerSearch)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
                return gson.toJson(cmr);
            }
        } else {
            cmr.setResponseCode(500);
            cmr.setResponseMessage("Must assign value to pageSize | Always pageSize greater than zero");
            logger.debug("===========LSF : (customerSearch)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
            return gson.toJson(cmr);
        }
    }

    private String getPageCount(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        if (map.containsKey("pageSize") && !"".equals(map.get("pageSize")) && !"0".equals(map.get("pageSize"))) {
            int pageCount;
            int pageSize;
            int recordSize;
            try {
                pageSize = Integer.valueOf((String) map.get("pageSize"));
                recordSize = Integer.valueOf(lsfRepository.getApprovedCustomerRecordSize());
                pageCount = (recordSize + pageSize - 1) / pageSize;
            } catch (Exception e) {
                logger.info(e.getMessage());
                cmr.setResponseCode(500);
                cmr.setResponseMessage("Exception occur  Number format | Database ");
                return gson.toJson(cmr);
            }
            cmr.setResponseCode(200);
            cmr.setResponseObject(pageCount);
        } else {
            cmr.setResponseCode(500);
            cmr.setResponseMessage("Must assign value to pageSize | Always pageSize greater than zero");
        }
        return gson.toJson(cmr);
    }
}
