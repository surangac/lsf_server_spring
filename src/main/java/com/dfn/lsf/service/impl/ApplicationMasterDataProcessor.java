package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.*;
import com.dfn.lsf.model.application.QuestionnaireEntry;
import com.dfn.lsf.model.notification.AdminUser;
import com.dfn.lsf.model.requestMsg.CommonInqueryMessage;
import com.dfn.lsf.model.requestMsg.GlobalParameterUpdateRequest;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.Helper;
import com.dfn.lsf.util.LsfConstants;
import com.dfn.lsf.util.NotificationManager;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
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
 * Defined in InMessageHandlerAdminCbr,InMessageHandlerCbr
 * route : APPLICATION_MASTER_DATA_PROCESS
 * Handling Message types :
 * - MESSAGE_TYPE_MASTER_DATA_PROCESS = 3;
 */
@Service
@RequiredArgsConstructor
@Qualifier("3")
public class ApplicationMasterDataProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationMasterDataProcessor.class);

    private final Gson gson;
    private final LSFRepository lsfRepository;
    private final Helper helper;
    private final LsfCoreService lsfCore;
    private final NotificationManager notificationManager;

    @Override
    public String process(String request) {
        Map<String, Object> map = new HashMap<String, Object>();
        CommonResponse cmr = new CommonResponse();
        map = gson.fromJson(request, map.getClass());
        String masterDataType = map.get("subMessageType").toString();
        switch (masterDataType) {
            case LsfConstants.REQ_APP_FLOW:/*-----------Getting Application Status Flow-----------*/
                List<Status> aFlow = lsfRepository.getApplicationStatusFlow();
                return gson.toJson(aFlow);
            case LsfConstants.REQ_LIQUID_TYPS:
                List<LiquidityType> lTypes = lsfRepository.getLiquidityTypes();  //LiquidityStatus.getInstance()
                // .getLiquidTypes();
                return gson.toJson(lTypes);
            case LsfConstants.REQ_CONCENTRATION_LIST:/*-----------Getting Concentration List-----------*/
                return reqConcentrationList(map);
            case LsfConstants.REQ_MARGIN_GROUP_LIST:/*-----------Getting Margin Group List-----------*/
                return reqMargineGroupList(map);
            case LsfConstants.REQ_DEFAULT_MARGIN_GROUP:/*-----------Getting Default Margin Group-----------*/
                return reqDefaultMargineGroup();
            case LsfConstants.UPDATE_CONCENTRATION_LIST:/*-----------Update Concentration List-----------*/
                return saveConcentrationLlist(request);
            case LsfConstants.UPDATE_MARGIN_GROUP_LIST:/*-----------Update Margin Group List-----------*/
                return saveMargineGrouplist(request);
            case LsfConstants.UPDATE_SYMBOL_MARGINABILITY:/*-----------Update Margin Group List-----------*/
                return updateSymbolMarginability(map);
            case LsfConstants.UPDATE_TENOR_DETAILS_LIST:/*-----------Update Tenor Details-----------*/
                return updateTenorList(request);
            case LsfConstants.GET_TENOR_DETAILS_LIST:/*-----------Update Tenor Details-----------*/
                return gson.toJson(lsfRepository.getTenorList());
            case LsfConstants.UPDATE_GLOBAL_PARAMETERS:/*-----------Update Global Parameters-----------*/
                return updateGlobalParameters(request);
            case LsfConstants.GET_GLOBAL_PARAMETERS:/*-----------Get Global Parameters-----------*/
                return getGlobalParameters();
            case LsfConstants.UPDATE_COMMISSION_STRUCTURE:/*-----------Update Commission Structure-----------*/
                return saveCommissionStructure(request);
            case LsfConstants.REQ_COMMISSION_STRUCTURE:/*-----------Request Commission Structure-----------*/
                return reqCommissionStructure();
            case LsfConstants.DELETE_COMMISSION_STRUCTURE:/*-----------Delete Commission Structure-----------*/
                return deleteCommissionStructure(map);
            case LsfConstants.STATUS_CHANGE_TENOR:/*-----------Change Tenor Status-----------*/
                return changeStatusTenor(request);
            case LsfConstants.SET_STATUS_MARGINABILITY_GROUP:/*-----------Approve Marginability Group-----------*/
                return changeStatusMarginabilityGroup(request);
            case LsfConstants.SET_STATUS_CONCENTRATION_GROUP:/*-----------Approve Concentration Group-----------*/
                return changeStatusConcentrationGroup(request);
            case LsfConstants.REQ_ORDER_ACC_PRIORITY:/*-----------Get Order Acc Priority-----------*/
                return getOrderAccPriority();
            case LsfConstants.GET_APPLICATION_LIST_FOR_ADMIN_DOC_UPLOAD:/*-----------Getting the Application List for
             Particular Document-----------*/
                return getApplicationListForAdminDocUpload(map);
            case LsfConstants.REMOVE_MARGINABILITY_GROUP:/*-----------Removing Marginability Group-----------*/
                return removeMarginabilityGroup(map);
            case LsfConstants.REMOVE_CONCENTRATION_GROUP:/*-----------Removing Stock Concentration Group-----------*/
                return removeConcentrationGroup(map);
            case LsfConstants.REMOVE_TENOR_GROUP:/*-----------Removing Tenor Group-----------*/
                return removeTenorGroup(map);
            case LsfConstants.ENABLE_DISABLE_PUBLIC_ACCESS:/*-----------Enable Disable Public Access-----------*/
                return enableDisablePublicAccess(map);
            case LsfConstants.ADD_ADMIN_USER://add admin user for notifications
                return addAdminUser(map);
            case LsfConstants.ADD_QUESTIONNAIRE_CONTENT://add questionnaire answer contents
                return addQuestionnaireContent(map);
            case LsfConstants.GET_QUESTIONNAIRE_CONTENT://get questionnaire answer contents
                return getQuestionnaireContent(map);
            case LsfConstants.REQ_GET_AGREEMENT_LIST:
                return getActiveAgreements(map);
            case LsfConstants.REQ_ADD_COMMODITIES:
                return addCommodityToMaster(map);
            case LsfConstants.REQ_GET_COMMODITIES:
                return getActiveCommodity(map);
            default:
                logger.info("Invalid Sub-message type received");
        }
        return null;
    }

    private String reqMargineGroupList(Map<String, Object> map) {
        logger.debug("===========LSF : (reqMargineGroupList)-REQUEST RECEIVED ");
        try {
            List<MarginabilityGroup> fromDB = lsfRepository.getMarginabilityGroups();//(List<MarginabilityGroup>)
            // dataService.findAll(bRequest);
            for (MarginabilityGroup marginabilityGroup : fromDB) {
                marginabilityGroup.setMarginableSymbols(lsfRepository.getMarginabilityPercByGroup(marginabilityGroup.getId()));
            }
            logger.debug("===========LSF : (reqMargineGroupList)-LSF-SERVER RESPONSE  : " + gson.toJson(fromDB));
            return gson.toJson(fromDB);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private String reqDefaultMargineGroup() {
        try {
            logger.debug("===========LSF : (reqDefaultMarginGroup)-REQUEST RECEIVED ");
            List<MarginabilityGroup> fromDB = lsfRepository.getDefaultMarginGroups();//(List<MarginabilityGroup>)
            // dataService.findAll(bRequest);
            for (MarginabilityGroup marginabilityGroup : fromDB) {
                marginabilityGroup.setMarginabilityList(lsfRepository.getMarginabilityGroupLiquidTypes(
                        marginabilityGroup.getId()));
            }
            logger.debug("===========LSF : (reqDefaultMarginGroup)-LSF-SERVER RESPONSE  : " + gson.toJson(fromDB));
            return gson.toJson(fromDB);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private String reqConcentrationList(Map<String, Object> map) {
        logger.debug("===========LSF : (reqConcentrationList)-REQUEST RECEIVED ");
        try {
            List<StockConcentrationGroup> fromDB = lsfRepository.getStockConcentrationGroup();//(List
            // <MarginabilityGroup>) dataService.findAll(bRequest);
            for (StockConcentrationGroup concentrationGroup : fromDB) {
                concentrationGroup.setConcentrationList(lsfRepository.getStockConcentrationGroupLiquidTypes(
                        concentrationGroup.getId()));
            }
            logger.debug("===========LSF : (reqConcentrationList)-LSF-SERVER RESPONSE  : " + gson.toJson(fromDB));
            return gson.toJson(fromDB);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private String saveMargineGrouplist(String rowMessage) {

        logger.debug("===========LSF : (updateMargineGroupList)-REQUEST RECEIVED :" + rowMessage);
        CommonResponse cmr = new CommonResponse();

        try {
            MarginabilityGroup marginabilityGroup = gson.fromJson(rowMessage, MarginabilityGroup.class);

            // save Group first
            String key = lsfRepository.updateMarginabilityGroup(marginabilityGroup);
            marginabilityGroup.setId(key);

            // save marginability percentage
            lsfRepository.updateSymbolMarginabilityPercentages(marginabilityGroup);
            cmr.setResponseCode(200);
            cmr.setResponseMessage(key);
        } catch (Exception ex) {
            ex.printStackTrace();
            cmr.setResponseCode(500);
            cmr.setErrorMessage(ex.getMessage());
        }

        logger.debug("===========LSF : (updateMargineGroupList)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String updateSymbolMarginability(Map objMap) {

        logger.debug("===========LSF : (updateSymbolMarginability)-REQUEST RECEIVED :" + objMap);
        CommonResponse cmr = new CommonResponse();

        try {
            List<Map<String, Object>> percentages = (List<Map<String, Object>>) objMap.get("symbolMarginGroup");

            // save percentage
            String key = lsfRepository.updateSymbolMarginability(percentages);
            cmr.setResponseCode(200);
            cmr.setResponseMessage(key);
        } catch (Exception ex) {
            ex.printStackTrace();
            cmr.setResponseCode(500);
            cmr.setErrorMessage(ex.getMessage());
        }

        logger.debug("===========LSF : (updateSymbolMarginability)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String saveCommissionStructure(String rowMessage) {
        logger.debug("===========LSF : (updateCommissionStructure)-REQUEST ");
        CommonResponse cmr = new CommonResponse();
        try {
            CommissionStructure commissionStructure = gson.fromJson(rowMessage, CommissionStructure.class);
            String key = lsfRepository.updateCommissionStructure(commissionStructure);
            cmr.setResponseCode(200);
            cmr.setResponseMessage(key);
        } catch (Exception ex) {
            ex.printStackTrace();
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Error While Saving Data");
        }
        logger.debug("===========LSF : (updateCommissionStructure)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String reqCommissionStructure() {
        logger.debug("===========LSF : (reqCommissionStructure)-REQUEST ");

        List<CommissionStructure> commissionStructures = lsfRepository.getCommissionStructure();
        logger.debug("===========LSF : (reqCommissionStructure)-LSF-SERVER RESPONSE  : " + gson.toJson(
                commissionStructures));
        return gson.toJson(commissionStructures);
    }

    private String deleteCommissionStructure(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        try {
            String id = map.get("id").toString();
            logger.debug("===========LSF : (deleteCommissionStructure)-REQUEST, ID " + id);
            String result = lsfRepository.deleteCommissionStructure(id);
            cmr.setResponseCode(200);
            cmr.setResponseMessage(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            cmr.setResponseCode(500);
            cmr.setResponseMessage("Error while deleting Commission Structure");
        }
        logger.debug("===========LSF : (deleteCommissionStructure)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));

        return gson.toJson(cmr);
    }

    private String saveConcentrationLlist(String rowMessage) {
        logger.debug("===========LSF : (updateConcentrationList)-REQUEST RECEIVED :" + rowMessage);
        CommonResponse cmr = new CommonResponse();
        try {
            StockConcentrationGroup stockConcentrationGroup = gson.fromJson(rowMessage, StockConcentrationGroup.class);
            // save Group first
            String key = lsfRepository.updateStockConcentrationGroup(stockConcentrationGroup);
            stockConcentrationGroup.setId(key);
            // save Liquidity Types
            lsfRepository.updateStockConcentrationGroupLiqTypes(stockConcentrationGroup);
            cmr.setResponseCode(200);
            cmr.setResponseMessage(key);
        } catch (Exception ex) {
            ex.printStackTrace();
            cmr.setResponseCode(500);
            cmr.setErrorMessage(ex.getMessage());
        }
        logger.debug("===========LSF : (updateConcentrationList)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String updateTenorList(String msgString) {
        logger.debug("===========LSF : (updateTenorDetailsList)-REQUEST RECEIVED, request:" + msgString);

        CommonResponse cmr = new CommonResponse();
        TenorDetails tenorDetails = gson.fromJson(msgString, TenorDetails.class);
        List<Tenor> tenors = tenorDetails.getTenors();
        StringBuilder builder = new StringBuilder();
        //lsfRepository.deleteAllTenors();
        for (Tenor tenor : tenors) {
            if (tenor.getDuration() != 0 && tenor.getProfitPercentage() != 0) {
                builder.append(lsfRepository.updateTenor(tenor) + "|");
            }
        }
        logger.info("===========LSF : Successfully update tenor list , Tenor id's " + builder);
        String RspMsg = "Successfully update tenor list " + builder;
        cmr.setResponseCode(200);
        cmr.setResponseMessage(RspMsg);
        logger.debug("===========LSF : (updateTenorDetailsList)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String changeStatusTenor(String msgString) {
        logger.info("===========LSF : (statusChangeTenor)-REQUEST, request: " + msgString);
        CommonResponse cmr = new CommonResponse();
        try {
            TenorDetails tenorDetails = gson.fromJson(msgString, TenorDetails.class);
            for (Tenor tenor : tenorDetails.getTenors()) {
                lsfRepository.changeStatusTenor(tenor.getDuration(), tenor.getApprovedby(), tenor.getStatus());
            }
            cmr.setResponseCode(200);
            cmr.setResponseMessage("Tenor Status Changed");
        } catch (Exception ex) {
            ex.printStackTrace();
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Error occur while saving Tenor");
        }
        logger.info("===========LSF : (statusChangeTenor)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));

        return gson.toJson(cmr);
    }

    private String changeStatusMarginabilityGroup(String rawMessage) {
        logger.info("===========LSF : (setStatusMarginabilityGroup)-REQUEST, request: " + rawMessage);
        CommonResponse cmr = new CommonResponse();
        try {
            MarginabilityGroup marginabilityGroup = gson.fromJson(rawMessage, MarginabilityGroup.class);
            String reply = lsfRepository.changeStatusMarginabilityGroup(marginabilityGroup);
            cmr.setResponseCode(200);
            cmr.setResponseMessage("Marginability Group Status Changed");
        } catch (Exception ex) {
            ex.printStackTrace();
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Error! Marginability Group");
        }
        logger.info("===========LSF : (setStatusMarginabilityGroup)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String changeStatusConcentrationGroup(String rawMessage) {
        logger.info("===========LSF : (setStatusConcentrationGroup)-REQUEST, request: " + rawMessage);
        CommonResponse cmr = new CommonResponse();
        try {
            StockConcentrationGroup concentrationGroup = gson.fromJson(rawMessage, StockConcentrationGroup.class);
            String rply = lsfRepository.changeStatusConcentrationGroup(concentrationGroup);
            cmr.setResponseCode(200);
            cmr.setResponseMessage("Stock Concentration Group Status Changed");
        } catch (Exception ex) {
            ex.printStackTrace();
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Error Stock Concentration Group Updating");
        }
        logger.info("===========LSF : (setStatusConcentrationGroup)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String updateGlobalParameters(String message) {
        logger.debug("===========LSF : (updateGlobalParameters)-REQUEST RECEIVED, request:" + message);
        CommonResponse cmr = new CommonResponse();
        GlobalParameterUpdateRequest globalParameterUpdateRequest = new GlobalParameterUpdateRequest();
        globalParameterUpdateRequest.setGlobalParameters(GlobalParameters.getInstance());
        globalParameterUpdateRequest = gson.fromJson(message, globalParameterUpdateRequest.getClass());
        GlobalParameters globalParameters = globalParameterUpdateRequest.getGlobalParameters();
        cmr.setResponseMessage(lsfRepository.updateGlobalParameters(globalParameters));
        GlobalParameters.reset(globalParameters);
        cmr.setResponseCode(200);
        logger.info("===========LSF : (updateGlobalParameters)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));

        return gson.toJson(cmr);
    }

    private String getGlobalParameters() {
        logger.info("===========LSF : (getGlobalParameters)-REQUEST, customerID ");
        GlobalParameters globalParameters = GlobalParameters.getInstance();  //getGlobalParametersFromDB();
        GlobalParameterUpdateRequest globalParameterUpdateRequest = new GlobalParameterUpdateRequest();
        globalParameterUpdateRequest.setMessageType(3);
        globalParameterUpdateRequest.setMasterDataType(LsfConstants.UPDATE_GLOBAL_PARAMETERS);
        globalParameterUpdateRequest.setGlobalParameters(globalParameters);
        globalParameterUpdateRequest.setTotalOutstandingAmount(lsfRepository.getMasterAccountOutstanding());
        logger.info("===========LSF : (getGlobalParameters)-LSF-SERVER RESPONSE  : " + gson.toJson(
                globalParameterUpdateRequest));
        return (gson.toJson(globalParameterUpdateRequest));
    }

    private String getOrderAccPriority() {
        logger.info("===========LSF : (reqOrderAccPriority)-REQUEST : ");
        int accPriority = GlobalParameters.getInstance().getOrderAccPriority();
        String responseMsg = Integer.toString(accPriority) + "|" + GlobalParameters.getInstance().getDefaultExchange();
        CommonResponse cmr = new CommonResponse();
        cmr.setResponseCode(200);
        cmr.setResponseObject(responseMsg);
        logger.info("===========LSF : (reqOrderAccPriority)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String getApplicationListForAdminDocUpload(Map<String, Object> map) {
        logger.info("===========LSF : (getApplicationListForAdminDocUpload)-REQUEST , request " + gson.toJson(map));
        int filterCriteria = 0;
        String filterValue = "";
        List<MurabahApplication> murabahApplications = null;
        if (map.containsKey("filterCriteria")) {
            filterCriteria = Integer.parseInt(map.get("filterCriteria").toString());
        }
        if (map.containsKey("filterValue")) {
            filterValue = map.get("filterValue").toString();
        }
        murabahApplications = lsfRepository.getApplicationListForAdminDocUpload(filterCriteria, filterValue);
        logger.info("===========LSF : (getApplicationListForAdminDocUpload)-LSF-SERVER RESPONSE  : " + gson.toJson(
                murabahApplications));
        return gson.toJson(murabahApplications);
    }

    private String removeMarginabilityGroup(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        String id = null;
        if (map.containsKey("id")) {
            id = map.get("id").toString();
        }
        logger.debug("===========LSF : (removeMarginabilityGroup)-REQUEST ,  ID :" + id);
        if (lsfRepository.removeMarginabilityGroup(id)) {
            cmr.setResponseCode(200);
            cmr.setResponseMessage("Done");
        } else {
            cmr.setResponseCode(500);
            cmr.setResponseMessage("Failed");
        }
        logger.debug("===========LSF : (removeMarginabilityGroup)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String removeConcentrationGroup(Map<String, Object> map) {

        CommonResponse cmr = new CommonResponse();
        String id = null;
        if (map.containsKey("id")) {
            id = map.get("id").toString();
        }
        logger.debug("===========LSF : (removeConcentrationGroup)-REQUEST ,  ID :" + id);
        if (lsfRepository.removeStockConcentrationGroup(id)) {
            cmr.setResponseCode(200);
            cmr.setResponseMessage("Done");
        } else {
            cmr.setResponseCode(500);
            cmr.setResponseMessage("Failed");
        }
        logger.debug("===========LSF : (removeConcentrationGroup)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String removeTenorGroup(Map<String, Object> map) {

        CommonResponse cmr = new CommonResponse();
        String id = null;
        if (map.containsKey("id")) {
            id = map.get("id").toString();
        }
        logger.debug("===========LSF : (removeTenorGroup)-REQUEST ,  ID :" + id);
        if (lsfRepository.removeTenorGroup(id)) {
            cmr.setResponseCode(200);
            cmr.setResponseMessage("Done");
        } else {
            cmr.setResponseCode(500);
            cmr.setResponseMessage("Failed");
        }
        logger.debug("===========LSF : (removeTenorGroup)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String enableDisablePublicAccess(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        String accessStatus = null;
        if (map.containsKey("publicAccess")) {
            accessStatus = map.get("publicAccess").toString();
            logger.debug("===========LSF : (enableDisablePublicAccess)-REQUEST ,  Access Status :" + accessStatus);
        }
        lsfRepository.changePublicAccessState(Integer.parseInt(accessStatus));
        GlobalParameters.getInstance().setPublicAccessEnabled(Integer.parseInt(accessStatus));
        /*Send to OMS*/
        CommonInqueryMessage enableDisablePublicAccessRequest = new CommonInqueryMessage();
        enableDisablePublicAccessRequest.setReqType(LsfConstants.ENABLE_DISABLE_PUBLIC_ACCESS_OMS);
        enableDisablePublicAccessRequest.setParams(accessStatus);
        String omsResponse = helper.omsCommonRequests(gson.toJson(enableDisablePublicAccessRequest));
        logger.debug("===========LSF : (enableDisablePublicAccess)-LSF-SERVER RESPONSE  : " + gson.toJson(omsResponse));
        cmr.setResponseCode(200);
        cmr.setResponseMessage(omsResponse);
        return gson.toJson(cmr);
    }

    private String addAdminUser(Map<String, Object> map) {
        AdminUser adminUser = new AdminUser();
        adminUser.setUserName(String.valueOf(map.get("userName")));
        adminUser.setName(String.valueOf(map.get("name")));
        adminUser.setNin(String.valueOf(map.get("nin")));
        adminUser.setRole(String.valueOf(map.get("role")));
        adminUser.setEmail(String.valueOf(map.get("email")));
        adminUser.setMobile(String.valueOf(map.get("mobile")));

        lsfRepository.addAdminUser(adminUser);
        notificationManager.reloadAdminUsers();

        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setResponseCode(200);
        commonResponse.setResponseMessage("Done");

        logger.info("Admin user updated, username: " + adminUser.getUserName());

        return gson.toJson(commonResponse);
    }

    private String addQuestionnaireContent(Map<String, Object> map) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            ArrayList<LinkedTreeMap<String, String>> questionnaireEntries = (ArrayList) map.get("questionnaire");
            for (LinkedTreeMap<String, String> entry : questionnaireEntries) {
                QuestionnaireEntry questionnaireEntry = new QuestionnaireEntry();
                questionnaireEntry.setQuestionNumber(Integer.parseInt(entry.get("questionNumber")));
                questionnaireEntry.setAnswerNumber(Integer.parseInt(entry.get("answerNumber")));
                questionnaireEntry.setDescription(entry.get("description"));

                lsfRepository.addQuestionnaireEntry(questionnaireEntry);
            }
            commonResponse.setResponseCode(200);
            commonResponse.setResponseMessage("Done");
            logger.info("Questionnaire updated!!!");
        } catch (Exception e) {
            commonResponse.setResponseCode(400);
            commonResponse.setResponseMessage("Error");
            logger.info("Update questionnaire request format error!!!");
        }
        return gson.toJson(commonResponse);
    }

    private String getQuestionnaireContent(Map<String, Object> map) {
        List<QuestionnaireEntry> qEntries = lsfRepository.getQuestionnaireEntries();

        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setResponseCode(200);
        commonResponse.setResponseObject(qEntries);

        logger.info("Questionnaire entry request received!!!");

        return gson.toJson(commonResponse);
    }

    private String getActiveAgreements(Map<String, Object> paraMap) {
        CommonResponse cmr = new CommonResponse();
        logger.info("===========LSF : (getActiveAgreements)-REQUEST ");
        int financeMethod = (int) Double.parseDouble(paraMap.get("financeMethod").toString());
        int productType = (int) Double.parseDouble(paraMap.get("productType").toString());
        try {
            List<Agreement> agreementList = lsfRepository.getActiveAgreementsForProduct(financeMethod, productType);
            if (agreementList != null) {
                cmr.setResponseCode(200);
                cmr.setResponseObject(agreementList);
            } else {
                cmr.setResponseCode(500);
                cmr.setErrorMessage("No Data found for getActiveAgreements : financeMethod ="
                                    + financeMethod
                                    + " productType ="
                                    + productType);
            }
        } catch (Exception e) {
            logger.info("Error while getting active agreements " + e.getMessage());
            cmr.setResponseCode(400);
            cmr.setErrorMessage("ERROR while getting active agreements");
        }
        return gson.toJson(cmr);
    }

    private String addCommodityToMaster(Map<String, Object> paraMap) {
        CommonResponse cmr = new CommonResponse();
        logger.info("===========LSF : (addCommodityToMaster)-REQUEST ");
        try {
            Commodity commodity = new Commodity();
            commodity.setSymbolName(paraMap.get("symbolName").toString());
            commodity.setSymbolCode(paraMap.get("symbolCode").toString());
            commodity.setShortDescription(paraMap.get("description").toString());
            commodity.setUnitOfMeasure(paraMap.get("unitOfMeasure").toString());
            commodity.setPrice(Double.parseDouble(paraMap.get("price").toString()));
            commodity.setBroker(paraMap.get("broker").toString());
            commodity.setExchange(paraMap.get("exchange").toString());
            commodity.setStatus(1);

            lsfRepository.addCommodityToMaster(commodity);
            logger.info("Successfully Added Commodity " + commodity.getSymbolCode());
            cmr.setResponseMessage("Successfully Added Commodity");
            cmr.setResponseCode(200);
        } catch (Exception e) {
            logger.error("Error while Adding Commodity " + e.getMessage());
            cmr.setErrorMessage("Failed to Add Commodity");
            cmr.setErrorCode(400);
        }

        return gson.toJson(cmr);
    }

    private String getActiveCommodity(Map<String, Object> paraMap) {
        CommonResponse cmr = new CommonResponse();
        logger.info("===========LSF : (getActiveCommodity)-REQUEST ");
        try {
            List<Commodity> commodityList = lsfRepository.getAllActiveCommodities();
            if (commodityList != null) {
                cmr.setResponseCode(200);
                cmr.setResponseObject(commodityList);
            } else {
                cmr.setResponseCode(500);
                cmr.setErrorMessage("No Data found for getActiveCommodity");
            }
        } catch (Exception e) {
            logger.info("Error while getting getActiveCommodity " + e.getMessage());
            cmr.setResponseCode(400);
            cmr.setErrorMessage("ERROR while get commodities");
        }
        return gson.toJson(cmr);
    }
}
