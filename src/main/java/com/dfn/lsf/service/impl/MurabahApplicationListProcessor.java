package com.dfn.lsf.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dfn.lsf.util.MessageType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.dfn.lsf.model.Comment;
import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.MApplicationFlow;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.model.MurabahApplicationListResponse;
import com.dfn.lsf.model.Status;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.dfn.lsf.util.LsfConstants.MESSAGE_TYPE_APPLICATION_LIST_PROCESS;
import static com.dfn.lsf.util.LsfConstants.MESSAGE_TYPE_MASTER_DATA_PROCESS;

/**
 * Processor for application master data operations
 * This replaces the AKKA ApplicationMasterDataProcessor
 */
@Service
@MessageType(MESSAGE_TYPE_APPLICATION_LIST_PROCESS)
@Slf4j
@RequiredArgsConstructor
public class MurabahApplicationListProcessor implements MessageProcessor {
    
    private final LSFRepository lsfRepository;
    
    private final Gson gson;
    
    @Override
    public String process(String request) {
        try {
            Map<String, Object> requestMap = gson.fromJson(request, Map.class);
            String subMessageType = (String) requestMap.get("subMessageType");
            
            log.info("Processing application master data request with subMessageType: {}", subMessageType);
            
            // Handle different sub-message types
            switch (subMessageType) {
                case LsfConstants.GET_APPLICATION_HISTORY:
                    return getApplicationHistory(requestMap);
                case LsfConstants.GET_APPLICATION_HISTORY_DETAILS:
                    return getApplicationHistoryDetails(requestMap);
                case LsfConstants.GET_MURABAH_APPLICATION_LIST:/*-----------Cater All Search Functions - Admin-----------*/
                    return getApplicationList(requestMap);
                case LsfConstants.GET_MURABAH_APPLICATION_COUNTER:/*-----------Get Application Count For each level - Admin-----------*/
                    return getApplicationListCounter(requestMap);
                case LsfConstants.GET_APPLICATION_USERNAME_LIST:/*-----------Get All Murabah Applications-----------*/
                    return getApplicationUsernameList(requestMap);
                case LsfConstants.REVERSE_APPLICATION:/*-----------Reverse App with Comment - Admin-----------*/
                    return reverseApplication(requestMap);
                case LsfConstants.REPLY_TO_REVERSED:/*-----------Reply To Reversed Application- Admin----------*/
                    return replyToReversed(requestMap);
                case LsfConstants.GET_FAILED_DEPOSITS_FOR_PO:/*----------------Get failed deposits for purchase order------------Client*/
                    return getFailedDeposits(requestMap);
                default:
                    log.warn("Unknown sub-message type: {}", subMessageType);
                    return createErrorResponse("Unknown sub-message type: " + subMessageType);
            }
        } catch (Exception e) {
            log.error("Error processing application master data request", e);
            return createErrorResponse("Error processing request: " + e.getMessage());
        }
    }

    private String getApplicationList(Map<String, Object> returnMap) {
        log.debug("===========LSF : (getMurabahApplicationList)-REQUEST , Request Params:" + gson.toJson(returnMap));
        String reqStatus = "";
        int filterCriteria = 0;
        String filterValue = "";
        String fromDate = "";
        String toDate = "";
        String corellationID = "";
        if (returnMap.containsKey("corellationID")) {
            corellationID = returnMap.get("corellationID").toString();
        }
        if (returnMap.containsKey("isSnapshot")) {
            if (returnMap.get("isSnapshot").toString().equalsIgnoreCase("1")) { // loading the  snapshot
                if (returnMap.containsKey("requestStatus")) {
                    reqStatus = returnMap.get("requestStatus").toString();
                    log.debug("===========LSF : Getting the SnapShot for Current Level :" + reqStatus);
                    List<MurabahApplication> fromDB = lsfRepository.getSnapshotCurrentLevel(Integer.parseInt(reqStatus));
                    List<MurabahApplication> reversed = lsfRepository.getReversedApplication(Integer.parseInt(reqStatus));
                    for (MurabahApplication murabahApplication : reversed) {
                        fromDB.add(murabahApplication);
                    }
                    MurabahApplicationListResponse listResponse = new MurabahApplicationListResponse();
                    listResponse.setApplicationList(fromDB);
                    return gson.toJson(listResponse);
                }

            } else if (returnMap.get("isSnapshot").toString().equalsIgnoreCase("0")) {
                if (returnMap.containsKey("requestStatus")) {
                    reqStatus = returnMap.get("requestStatus").toString();
                    if (reqStatus.equalsIgnoreCase("*")) {
                        reqStatus = "";
                    }
                }
                if (returnMap.containsKey("filterCriteria")) {
                    filterCriteria = Integer.parseInt(returnMap.get("filterCriteria").toString());
                }
                if (returnMap.containsKey("filterValue")) {
                    filterValue = returnMap.get("filterValue").toString();
                    if (filterValue.equalsIgnoreCase("*")) {
                        filterValue = "";
                    }
                }
                if (returnMap.containsKey("fromDate")) {
                    fromDate = returnMap.get("fromDate").toString();
                }
                if (returnMap.containsKey("toDate")) {
                    toDate = returnMap.get("toDate").toString();
                }
                MurabahApplicationListResponse listResponse = new MurabahApplicationListResponse();
                try {
                    List<MurabahApplication> fromDB = lsfRepository.getFilteredApplication(filterCriteria, filterValue, fromDate, toDate, Integer.parseInt(reqStatus));

                    if (returnMap.containsKey("requestStatus")) {
                        List<MurabahApplication> tempApp = lsfRepository.getReversedApplication(Integer.parseInt(reqStatus));
                        for (MurabahApplication murabahApplication : tempApp) {
                            fromDB.add(murabahApplication);
                        }
                    }
                    listResponse.setApplicationList(fromDB);
                } catch (Exception e) {
                    listResponse.setResponseCode(500);
                }
                log.info("===========LSF : (getMurabahApplicationList)LSF-SERVER RESPONSE  :" + gson.toJson(listResponse) + " , CorrelationID:" + corellationID);
                return gson.toJson(listResponse);
            }
        } else {
            if (returnMap.containsKey("requestStatus")) {
                reqStatus = returnMap.get("requestStatus").toString();
                if (reqStatus.equalsIgnoreCase("*")) {
                    reqStatus = "";
                }
            }
            if (returnMap.containsKey("filterCriteria")) {
                filterCriteria = Integer.parseInt(returnMap.get("filterCriteria").toString());
            }
            if (returnMap.containsKey("filterValue")) {
                filterValue = returnMap.get("filterValue").toString();
                if (filterValue.equalsIgnoreCase("*")) {
                    filterValue = "";
                }
            }
            if (returnMap.containsKey("fromDate")) {
                fromDate = returnMap.get("fromDate").toString();
            }
            if (returnMap.containsKey("toDate")) {
                toDate = returnMap.get("toDate").toString();
            }
            MurabahApplicationListResponse listResponse = new MurabahApplicationListResponse();
            try {
                
                List<MurabahApplication> fromDB = lsfRepository.getFilteredApplication(filterCriteria, filterValue, fromDate, toDate, Integer.parseInt(reqStatus));

                if (returnMap.containsKey("requestStatus")) {
                    List<MurabahApplication> tempApp = lsfRepository.getReversedApplication(Integer.parseInt(reqStatus));
                    for (MurabahApplication murabahApplication : tempApp) {
                        fromDB.add(murabahApplication);
                    }
                }
                listResponse.setApplicationList(fromDB);
            } catch (Exception e) {
                listResponse.setResponseCode(500);
            }
            log.info("===========LSF : (getMurabahApplicationList)LSF-SERVER RESPONSE  :" + gson.toJson(listResponse) + " , CorrelationID:" + corellationID);
            return gson.toJson(listResponse);
        }
        return null;

    }
    
    private String getApplicationHistory(Map<String, Object> returnMap) {
        log.debug("===========LSF : (applicationHistory)-REQUEST , params:" + gson.toJson(returnMap));
        String reqStatus = "";
        int filterCriteria = 0;
        String filterValue = "";
        String fromDate = "";
        String toDate = "";
        String corellationID = "";
        if (returnMap.containsKey("corellationID")) {
            corellationID = returnMap.get("corellationID").toString();
        }
        if (returnMap.containsKey("requestStatus")) {
            reqStatus = returnMap.get("requestStatus").toString();
            if (reqStatus.equalsIgnoreCase("*")) {
                reqStatus = "";
            }
        }
        if (returnMap.containsKey("filterCriteria")) {
            filterCriteria = Integer.parseInt(returnMap.get("filterCriteria").toString());
        }
        if (returnMap.containsKey("filterValue")) {
            filterValue = returnMap.get("filterValue").toString();
            if (filterValue.equalsIgnoreCase("*")) {
                filterValue = "";
            }
        }
        if (returnMap.containsKey("fromDate")) {
            fromDate = returnMap.get("fromDate").toString();
        }
        if (returnMap.containsKey("toDate")) {
            toDate = returnMap.get("toDate").toString();
        }

        MurabahApplicationListResponse listResponse = new MurabahApplicationListResponse();

        try {
            List<MurabahApplication> fromDB = lsfRepository.getHistoryApplication(filterCriteria, filterValue, fromDate, toDate, Integer.parseInt(reqStatus));
            listResponse.setApplicationList(fromDB);
        } catch (Exception e) { 
            listResponse.setResponseCode(500);
        }
        log.info("===========LSF : (applicationHistory)LSF-SERVER RESPONSE  :" + gson.toJson(listResponse) + " , CorrelationID:" + corellationID);
        return gson.toJson(listResponse);
    }

    private String getApplicationHistoryDetails(Map<String, Object> paramsMap){
        log.debug("===========LSF : (applicationHistoryDetails)-REQUEST , params:" + gson.toJson(paramsMap));
        String corellationID = "";
        if (paramsMap.containsKey("corellationID")) {
            corellationID = paramsMap.get("corellationID").toString();
        }
        MurabahApplication murabahApplication = new MurabahApplication();

        String applicationID = paramsMap.get("applicationID").toString();
        murabahApplication.setAppStatus(lsfRepository.getApplicationStatus(applicationID));

        List<Comment> applicationCommentList = lsfRepository.getApplicationComment(applicationID);
        List<Comment> finalCommentList = new ArrayList<>();

        for (Comment comment : applicationCommentList){
            if(Integer.parseInt(comment.getParentID())== 0){
                Comment tempComment = comment;
                for(Comment reply: applicationCommentList){
                    if(reply.getParentID().equalsIgnoreCase(tempComment.getCommentID().trim())){
                        tempComment.setReply(reply);
                    }
                }
                finalCommentList.add(tempComment);
                tempComment = null;
            }
        }
        murabahApplication.setCommentList(finalCommentList);

        log.info("===========LSF : (applicationHistory)LSF-SERVER RESPONSE  :" + gson.toJson(murabahApplication) + " , CorrelationID:" + corellationID);

        return gson.toJson(murabahApplication);
    }

    private String createErrorResponse(String message) {
        CommonResponse response = new CommonResponse();
        response.setResponseCode(500);
        response.setErrorMessage(message);
        return gson.toJson(response);
    }

    private String getApplicationListCounter(Map<String, Object> returnMap) {
        log.debug("===========LSF : (updateCommissionStructure)-REQUEST, params: " + gson.toJson(returnMap));
        CommonResponse commonResponse = new CommonResponse();
        List<MurabahApplication> murabahApplications = null;// getMurabahApplications();
        List<Status> statusList = MApplicationFlow.getInstance().getApplicationFlow();
        String response = "";
        String corellationID = "";
        if (returnMap.containsKey("corellationID")) {
            corellationID = returnMap.get("corellationID").toString();
        }
        murabahApplications = lsfRepository.getAllMurabahApplications();
        if (murabahApplications != null) {
            log.debug("===========LSF : Murabah Application Count  :" + murabahApplications.size());
            if (murabahApplications.size() != 0) {
                for (Status status : statusList) {
                    int count = 0;
                    for (MurabahApplication murabahApplication : murabahApplications) {
                        int currentLevel = murabahApplication.getCurrentLevel();
                        if (status.getLevelId() == currentLevel) {
                            status.setCount(++count);
                            statusList.set(status.getLevelId() - 1, status);
                        }
                    }
                }
                commonResponse.setResponseCode(200);
                commonResponse.setResponseObject(statusList);
                response = gson.toJson(commonResponse);
                for (Status status : statusList) {
                    status.setCount(0);
                    if (status.getLevelId() == 20) {
                        statusList.set(14, status);
                    } else {
                        statusList.set(status.getLevelId() - 1, status);
                    }
                }
            } else {
                for (Status status : statusList) {
                    status.setCount(0);
                    if (status.getLevelId() == 20) {
                        statusList.set(14, status);
                    } else {
                        statusList.set(status.getLevelId() - 1, status);
                    }
                }
            }
        }
        log.info("===========LSF : (getMurabahApplicationCounter)LSF-SERVER RESPONSE  :" + gson.toJson(response) + " , CorrelationID:" + corellationID);
        return response;
    }

    private String getApplicationUsernameList(Map<String, Object> returnMap) {
        log.debug("===========LSF : (getApplicationUsernameList)-REQUEST , params: " + gson.toJson(returnMap));
        String corellationID = "";
        if (returnMap.containsKey("corellationID")) {
            corellationID = returnMap.get("corellationID").toString();
        }
        CommonResponse commonResponse = new CommonResponse();
        List<MurabahApplication> murabahApplications = lsfRepository.getAllMurabahApplications();
        List<MurabahApplication> response = new ArrayList<>();
        if (murabahApplications.size() != 0) {
            log.info("===========LSF : (getApplicationUsernameList)LSF-SERVER RESPONSE  :" + gson.toJson(response) + " , CorrelationID:" + corellationID);
            return gson.toJson(response);
        } else {
            return gson.toJson(commonResponse);
        }
    }

    private String reverseApplication(Map<String, Object> paramsMap) {
        CommonResponse cmr=new CommonResponse();
        log.debug("===========LSF : (reverseApplication)-REQUEST, params:" + gson.toJson(paramsMap));
        List<MurabahApplication> murabahApplications = null;
        MurabahApplication murabahApplication = null;
        String correlationID = "";
        if (paramsMap.containsKey("corellationID")) {
            correlationID = paramsMap.get("corellationID").toString();
        }
        if (paramsMap.containsKey("applicationID")) {
            murabahApplications = lsfRepository.getMurabahAppicationApplicationID(paramsMap.get("applicationID").toString());
            if (murabahApplications != null) {
                if (murabahApplications.size() > 0) {
                    murabahApplication = murabahApplications.get(0);
                }
            }
            if (paramsMap.containsKey("reversedFrom")) {
                murabahApplication.setReversedFrom(paramsMap.get("reversedFrom").toString());
            }
            if (paramsMap.containsKey("reversedTo")) {
                murabahApplication.setReversedTo(paramsMap.get("reversedTo").toString());
            }
            murabahApplication.setIsEditable(false);
            murabahApplication.setIsReversed(true);
            murabahApplication.setIsEdited(false);
            lsfRepository.reverseApplication(murabahApplication);
            Comment comment = new Comment();
            if (paramsMap.containsKey("reversedFrom")) {
                comment.setReversedFrom(paramsMap.get("reversedFrom").toString());
            }
            if (paramsMap.containsKey("reversedTo")) {
                comment.setReversedTo(paramsMap.get("reversedTo").toString());
            }
            if (paramsMap.containsKey("comment")) {
                comment.setComment(paramsMap.get("comment").toString());
            }
            if (paramsMap.containsKey("reversedBy")) {
                comment.setCommentedBy(paramsMap.get("reversedBy").toString());
            }
            comment.setParentID("0");
            log.debug("===========LSF : Comment received Application ID :" + paramsMap.get("applicationID").toString() + ", Comment :" + comment.getComment());
            lsfRepository.createApplicationComment(comment, murabahApplication.getId());
            log.debug("===========LSF : Comment  Added Successfully.");
        }
        cmr.setErrorMessage("updated");
        cmr.setResponseCode(200);
        return gson.toJson(cmr);
    }

    private String replyToReversed(Map<String, Object> paramsMap) {
        CommonResponse cmr=new CommonResponse();
        log.debug("===========LSF : (replyToReversed)-REQUEST , params: " + gson.toJson(paramsMap));
        List<MurabahApplication> murabahApplications = null;
        List<Comment> comments = null;
        String response = "";
        if (paramsMap.containsKey("applicationID")) {
            log.info("===========LSF : Reply received to Application ID :" + paramsMap.get("applicationID").toString());
            String applicationID = paramsMap.get("applicationID").toString();
            murabahApplications = lsfRepository.getMurabahAppicationApplicationID(applicationID);
            if (murabahApplications != null) {
                if (murabahApplications.size() > 0) {
                    MurabahApplication murabahApplication = murabahApplications.get(0);
                    if (paramsMap.containsKey("commentID")) {
                        log.info("===========LSF : Comment ID :" + paramsMap.get("commentID").toString());
                        String commentID = paramsMap.get("commentID").toString();
                        commentID = commentID.replaceAll("\\s+", "");
                        comments = lsfRepository.getApplicationComment(commentID, applicationID);
                        if (comments != null) {
                            if (comments.size() > 0) {
                                Comment comment = comments.get(0);
                                Comment reply = new Comment();
                                reply.setReversedTo(comment.getReversedTo());
                                reply.setReversedFrom(comment.getReversedFrom());
                                reply.setParentID(comment.getCommentID());
                                if (paramsMap.containsKey("reply")) {
                                    reply.setComment(paramsMap.get("reply").toString());
                                }
                                if (paramsMap.containsKey("replyedBy")) {
                                    reply.setCommentedBy(paramsMap.get("replyedBy").toString());
                                }
                                lsfRepository.createApplicationComment(reply, applicationID);
                                murabahApplication.setReversedFrom("0");
                                murabahApplication.setReversedTo("0");
                                murabahApplication.setIsEditable(false);
                                murabahApplication.setIsReversed(true);
                                //murabahApplication.setIsReversed(false);
                                murabahApplication.setIsEdited(true);
                                lsfRepository.reverseApplication(murabahApplication);
                                log.info("===========LSF : Reply Updated Successfully.");
                                response = "Done";
                            }

                        }
                    }
                }
            }
        } else {
            log.error("===========LSF : Reply received without Application ID");
            response = "Error";
        }
        log.debug("===========LSF : (replyToReversed)-LSF-SERVER RESPONSE  : " + response);
        cmr.setErrorMessage(response);
        cmr.setResponseCode(200);
        return gson.toJson(cmr);
    }

    private String getFailedDeposits(Map<String, Object> map) {
        List<MurabahApplication> failedApplications = lsfRepository.getDepositFailedApplications();

        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setResponseCode(200);
        commonResponse.setResponseObject(failedApplications);

        log.info("Response for failed PO deposits");

        return gson.toJson(commonResponse);
    }
}