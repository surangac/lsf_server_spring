package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.Documents;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.ErrorCodes;
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
 * Defined in InMessageHandlerAdminCbr, InMessageHandlerCbr
 * route : DOCUMENT_ADMINISTRATION_ROUTE
 * Handling Message types :
 * - MESSAGE_TYPE_DOCUMENT_ADMINISTRATION_PROCESS = 17;
 */
@Service
@RequiredArgsConstructor
@Qualifier("17")
public class DocumentAdministrationProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DocumentAdministrationProcessor.class);

    private final Gson gson;
    private final LSFRepository lsfRepository;

    @Override
    public String process(String request) {
        Map<String, Object> map = new HashMap<String, Object>();
        CommonResponse cmr = null;
        map = gson.fromJson(request, map.getClass());
        try {
            if (map.containsKey("subMessageType")) {
                String subMessageType = map.get("subMessageType").toString();
                switch (subMessageType) {
                    case LsfConstants.REQ_DOCUMENT_MASTER_LIST:/*-----------Getting Master Docs-----------*/
                        return getUploadableDocAdmin();
                    case LsfConstants.REQ_UPLOADABLE_DOCS: /*-----------Getting User Uploaded DOCs Compared to Mater
                    Docs-----------*/
                        return getUploadableDocuments(map);
                    case LsfConstants.ADD_UPLOADABLE_DOCS:/*-----------Add Master Docs-----------*/
                        return saveUploadableDocuments(map);
                    case LsfConstants.UPDATE_UPLOADABLE_DOCS:/*-----------Update Master Docs-----------*/
                        return updateUploadableDocuments(map);
                    case LsfConstants.SET_STATUS_ADMIN_DOCS:/*-----------Approve Mater Docs-----------*/
                        return changeStatusAdminDocs(request);
                    case LsfConstants.REMOVE_ADMIN_DOC:/*-----------Remove Mater Docs-----------*/
                        return removeAdminDoc(map);
                    case LsfConstants.REMOVE_CUSTOM_DOC_FROM_APPLICATION:/*----------Remove Custom Doc From
                    Application------------*/
                        return removeCustomDocFromApplication(map);
                }
            }
        } catch (Exception ex) {
            cmr = new CommonResponse();
            cmr.setResponseCode(500);
            cmr.setErrorMessage(ErrorCodes.ERROR_EXCEPTION.errorDescription());
            ex.printStackTrace();
        }
        return gson.toJson(cmr);
    }

    private String getUploadableDocAdmin() {
        logger.debug("===========LSF : (reqDocumentMasterList)-REQUEST RECEIVED");
        List<Documents> fromDB = null;
        try {
            fromDB = lsfRepository.getDocumentMasterList();
            for (Documents document : fromDB) {
                Map<String, Integer> appliationMap = new HashMap<>();
                if (document.getIsGlobal() == 0) {
                    List<MurabahApplication> murabahApplications = null;
                    murabahApplications =
                            lsfRepository.getDocumentRelatedAppsByDocID(Integer.parseInt(document.getId()));
                    if (murabahApplications != null && murabahApplications.size() > 0) {
                        for (MurabahApplication murabahApplication : murabahApplications) {
                            appliationMap.put(
                                    murabahApplication.getId(),
                                    murabahApplication.getDocumentUploadedStatus());
                        }
                    }
                    document.setRelatedApplications(appliationMap);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        logger.debug("===========LSF : (reqDocumentMasterList)-LSF-SERVER RESPONSE  : " + gson.toJson(fromDB));
        return gson.toJson(fromDB);
    }

    private String getUploadableDocuments(Map<String, Object> map) {
        logger.debug("===========LSF : (reqUploadableDocs)-REQUEST RECEIVED , customerID:" + map.get("id").toString());
        List<Documents> fromDB = null;
        try {
            fromDB = lsfRepository.getComparedCustomerDocumentList(map.get("id").toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        logger.debug("===========LSF : (reqUploadableDocs)-LSF-SERVER RESPONSE  : " + gson.toJson(fromDB));
        return gson.toJson(fromDB);
    }

    private String saveUploadableDocuments(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        ArrayList<Map<String, Object>> docList = (ArrayList<Map<String, Object>>) map.get("documentList");
        logger.debug("===========LSF : (addUploadableDocs)-REQUEST RECEIVED :" + gson.toJson(docList));
        for (Map<String, Object> mapObj : docList) {
            try {
                Documents fromDB = new Documents();
                fromDB.setId(mapObj.get("id").toString());
                fromDB.setDocumentName(mapObj.get("documentName").toString());
                fromDB.setRequired(Boolean.parseBoolean(mapObj.get("isRequired").toString()));
                fromDB.setCreatedBy(mapObj.get("createdBy").toString());
                if (mapObj.containsKey("isGlobal")) {
                    fromDB.setIsGlobal(Integer.parseInt(mapObj.get("isGlobal").toString()));
                    if (Integer.parseInt(mapObj.get("isGlobal").toString()) == 1) {
                        String id = lsfRepository.updateDocumentMaster(fromDB);
                        cmr.setResponseCode(200);
                        cmr.setResponseMessage(id);
                        logger.debug("===========LSF : Uploaded Global Mater Doc, Document ID :" + id);
                    } else if (Integer.parseInt(mapObj.get("isGlobal").toString()) == 0) {
                        if (mapObj.containsKey("applicationList")) {
                            ArrayList<String> applicationList = null;
                            applicationList = (ArrayList<String>) mapObj.get("applicationList");
                            String id = lsfRepository.updateDocumentMaster(fromDB);
                            logger.debug("===========LSF : Uploading Global Mater Doc , Document ID :"
                                         + id
                                         + "for Multiple Users, List :"
                                         + applicationList);
                            // String[] applications = applicationList.split("\\|");
                            for (String applicationID : applicationList) {
                                lsfRepository.addCustomDocByAdmin(applicationID, id);
                            }
                            cmr.setResponseCode(200);
                        }
                    }
                } else {
                    cmr.setResponseCode(200);
                    cmr.setResponseMessage("Not Uploaded Invalid Parameters.");
                }
            } catch (Exception e) {
                cmr.setResponseCode(500);
                cmr.setResponseMessage(e.getMessage());
                e.printStackTrace();
            }
        }
        logger.debug("===========LSF : (addUploadableDocs)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String updateUploadableDocuments(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        String documentID = "";
        List<String> applicationList;
        if (map.containsKey("documentID")) {
            documentID = map.get("documentID").toString();
            if (map.containsKey("applicationList")) {
                applicationList = (List<String>) map.get("applicationList");
                logger.debug("===========LSF : (updateUploadableDocs)-REQUEST RECEIVED  Document ID :"
                             + documentID
                             + "  for Users :"
                             + gson.toJson(applicationList));
                for (String applicationID : applicationList) {
                    lsfRepository.addCustomDocByAdmin(applicationID, documentID);
                }
                cmr.setResponseCode(200);
                cmr.setResponseMessage("Done");
            } else {
                cmr.setResponseCode(500);
                cmr.setErrorMessage("Empty Application List");
            }
        } else {
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Invalid Document ID");
        }
        logger.debug("===========LSF : (updateUploadableDocs)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String changeStatusAdminDocs(String rawMessage) {
        logger.info("===========LSF : (setStatusAdminDocs)-REQUEST, request: " + rawMessage);
        CommonResponse cmr = new CommonResponse();
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            map = gson.fromJson(rawMessage, map.getClass());
            int documentID = Integer.parseInt(map.get("id").toString());
            String approvedBy = map.get("approvedBy").toString();
            int approveStatus = Integer.parseInt(map.get("status").toString());
            String reply = lsfRepository.changeStatusDocumentMaster(documentID, approvedBy, approveStatus);
            cmr.setResponseCode(200);
            cmr.setResponseMessage("Document Status Changed");
        } catch (Exception ex) {
            ex.printStackTrace();
            cmr.setResponseCode(500);
            cmr.setErrorMessage("error when Document Status Change");
        }
        logger.info("===========LSF : (setStatusAdminDocs)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String removeAdminDoc(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        String documentId = (String) map.get("documentId");
        logger.info("===========LSF : (removeAdminDoc)-REQUEST, documentId: " + documentId);
        try {
            String reply = lsfRepository.removeAdminDoc(documentId);
            cmr.setResponseCode(200);
            cmr.setResponseMessage("Document removed : " + documentId);
        } catch (Exception ex) {
            ex.printStackTrace();
            cmr.setResponseCode(500);
            cmr.setErrorMessage("error when Document remove");
        }
        logger.info("===========LSF : (removeAdminDoc)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String removeCustomDocFromApplication(Map<String, Object> map) {
        String documentID = "";
        List<String> applicationList = new ArrayList<>();
        CommonResponse cmr = new CommonResponse();
        if (map.containsKey("documentID")) {
            documentID = map.get("documentID").toString();
            if (map.containsKey("applicationList")) {
                applicationList = (ArrayList<String>) map.get("applicationList");
                logger.debug("===========LSF : (removeCustomDocFromApplication)-REQUEST , Document ID :"
                             + documentID
                             + " from Applications. Application List :"
                             + applicationList);
                lsfRepository.removeCustomDocFromApplication(Integer.parseInt(documentID), applicationList);
                cmr.setResponseCode(200);
                cmr.setResponseMessage("Successfully Removed the Application List.");
            } else {
                cmr.setResponseCode(500);
                cmr.setResponseMessage("ApplicationList is not Available. ");
            }
        } else {
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Document ID is not Available.");
        }
        logger.debug("===========LSF : (removeCustomDocFromApplication)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }
}
