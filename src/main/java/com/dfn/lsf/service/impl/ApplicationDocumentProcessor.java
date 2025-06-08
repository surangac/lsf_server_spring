package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.Documents;
import com.dfn.lsf.model.MApplicationDocuments;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.ErrorCodes;
import com.dfn.lsf.util.LsfConstants;
import com.dfn.lsf.util.MessageType;
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

import static com.dfn.lsf.util.LsfConstants.MESSAGE_TYPE_APPLICATION_DOCUMENT_PROCESS;

/**
 * Defined in InMessageHandlerAdminCbr,InMessageHandlerCbr
 * route : APPLICATION_DOCUMENT_ROUTE
 * Handling Message types :
 * - MESSAGE_TYPE_APPLICATION_DOCUMENT_PROCESS = 18;
 */
@Service
@MessageType(MESSAGE_TYPE_APPLICATION_DOCUMENT_PROCESS)
@RequiredArgsConstructor
public class ApplicationDocumentProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationDocumentProcessor.class);

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
                    case "reqApplicationUploadedDocs":
                        return getApplicationUploadedDocs(map);/*---------Getting User Uploaded Documents--------*/
                    case "updateApplicationDoc":
                        return updateApplicationDocDetails(map);/*---------Updating User Uploaded Documents--------*/
                    case "removeApplicationDoc":
                        return removeApplicationDocument(map);/*---------Removing User Uploaded Documents--------*/
                    case "reqAdminDocs":
                        return getApplicatioAdminDocs(map);/*------------Getting Admin Uploaded App Documents*/
                    case "updateApplicationAdminDocs":
                        return updateApplicationAdminDocs(map);/*------------Update Admin Uploaded App Documents*/
                    case "removeApplicationAdminDocs":
                        return removeApplicationAdminDocs(map);/*------------Removing Admin Uploaded App Documents*/
                    case "validateCustomerDocs":/*-----Validate Customer Documents------*/
                        return validateCustomerDocs(map);
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

    private String getApplicationUploadedDocs(Map<String, Object> map) {
        String applicationID = map.get("id").toString();
        List<Documents> documentsList = null;
        try {
            documentsList = lsfRepository.getCustomerDocumentListByAppID(applicationID);
            MApplicationDocuments documents = new MApplicationDocuments();
            documents.setApplicationId(applicationID);
            documents.setApplicationDocuments(documentsList);
            return gson.toJson(documents);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private String updateApplicationDocDetails(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        List<Documents> documentsList = null;
        try {
            documentsList = lsfRepository.getCustomerDocumentListByDocID(
                    map.get("id").toString(),
                    map.get("documentid").toString());
            List<Documents> docList = null;
            if (documentsList != null) {
                if (documentsList.size() == 0) {
                    Documents document = new Documents();
                    document.setId(map.get("documentid").toString());
                    document.setOriginalFileName(map.get("originalFileName").toString());
                    document.setUploadedFileName(map.get("uploadedFileName").toString());
                    document.setPath(map.get("path").toString());
                    document.setExtension(map.get("extension").toString());
                    document.setMimeType(map.get("mimetype").toString());
                    document.setUploadStatus(1);
                    lsfRepository.updateCustomerDocument(document, map.get("id").toString());
                    cmr.setResponseMessage("updated");
                    cmr.setResponseCode(200);
                } else {
                    if (map.containsKey("documentid")) {
                        Documents tempDocumet = documentsList.get(0);
                        tempDocumet.setOriginalFileName(map.get("originalFileName").toString());
                        tempDocumet.setUploadedFileName(map.get("uploadedFileName").toString());
                        tempDocumet.setPath(map.get("path").toString());
                        tempDocumet.setExtension(map.get("extension").toString());
                        tempDocumet.setMimeType(map.get("mimetype").toString());
                        tempDocumet.setUploadStatus(1);
                        lsfRepository.updateCustomerDocument(tempDocumet, map.get("id").toString());
                    }
                    cmr.setResponseMessage("updated");
                    cmr.setResponseCode(200);
                    logger.info("===========LSF : Updated  Application Doc, Application ID :"
                                + map.get("documentid")
                                + " , Document ID:"
                                + map.get("documentid").toString());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Error");
        }
        logger.debug("===========LSF : LSF-SERVER RESPONSE  :" + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String removeApplicationDocument(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        try {
            if (map.containsKey("id")) {
                String applicationID = map.get("id").toString();
                if (map.containsKey("documentid")) {
                    String documentID = map.get("documentid").toString();
                    lsfRepository.removeCustomerDocs(applicationID, documentID);
                }
                cmr.setResponseMessage("Document Removed");
                cmr.setResponseCode(200);
            } else {
                cmr.setResponseCode(500);
                cmr.setErrorMessage("Insufficient Data");
                cmr.setErrorCode(LsfConstants.ERROR_INVALID_DETAILS);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Error");
        }
        return gson.toJson(cmr);
    }

    private String getApplicatioAdminDocs(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        if (map.containsKey("applicationID")) {
            String applicatioID = map.get("applicationID").toString();
            try {
                List<Documents> adminApplicationDocs = null;
                adminApplicationDocs = lsfRepository.getApplicationAdminDocs(applicatioID);
                List<Documents> responseAdminDocs = new ArrayList<>();
                MApplicationDocuments mApplicationDocuments = new MApplicationDocuments();
                mApplicationDocuments.setApplicationId(applicatioID);
                if (adminApplicationDocs.size() > 0) {
                    if (map.containsKey("documentID")) {
                        String documentId = map.get("documentID").toString();
                        if (!documentId.equalsIgnoreCase("*")) {
                            for (Documents adminDocument : adminApplicationDocs) {
                                if (documentId.equalsIgnoreCase(adminDocument.getId())) {
                                    responseAdminDocs.add(adminDocument);
                                }
                            }
                            logger.info("===========LSF : Returning  Admin Doc Application ID :"
                                        + applicatioID
                                        + " , DocumentId :"
                                        + documentId);
                            mApplicationDocuments.setApplicationDocuments(responseAdminDocs);
                        } else {
                            mApplicationDocuments.setApplicationDocuments(adminApplicationDocs);
                            logger.info("===========LSF : Returning  Admin Docs Application ID :"
                                        + applicatioID
                                        + " , # Documents :"
                                        + mApplicationDocuments.getApplicationDocuments().size());
                        }
                        logger.debug("===========LSF : LSF-SERVER RESPONSE  :" + gson.toJson(mApplicationDocuments));
                        return gson.toJson(mApplicationDocuments);
                    } else {
                        cmr.setErrorMessage("Invalid Request");
                    }
                } else {
                    cmr.setErrorMessage("No File Uploaded");
                }
            } catch (Exception e) {
                e.printStackTrace();
                cmr.setErrorMessage("Error While Loading the Admin Document List.");
            }
        } else {
            cmr.setErrorMessage("Invalid Application ID.");
        }
        cmr.setResponseCode(200);
        return gson.toJson(cmr);
    }

    private String updateApplicationAdminDocs(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        if (map.containsKey("applicationID")) {
            try {
                Documents adminDocument = new Documents();
                if (map.containsKey("documentName")) {
                    adminDocument.setDocumentName(map.get("documentName").toString());
                }
                if (map.containsKey("originalFileName")) {
                    adminDocument.setOriginalFileName(map.get("originalFileName").toString());
                }
                if (map.containsKey("uploadedFileName")) {
                    adminDocument.setUploadedFileName(map.get("uploadedFileName").toString());
                }
                if (map.containsKey("path")) {
                    adminDocument.setPath(map.get("path").toString());
                }
                if (map.containsKey("extension")) {
                    adminDocument.setExtension(map.get("extension").toString());
                }
                if (map.containsKey("mimeType")) {
                    adminDocument.setMimeType(map.get("mimeType").toString());
                }
                if (map.containsKey("uploadedBy")) {
                    adminDocument.setUploadedBy(map.get("uploadedBy").toString());
                }
                if (map.containsKey("uploadedLevel")) {
                    adminDocument.setUploadedLevel(map.get("uploadedLevel").toString());
                }
                if (map.containsKey("uploadedUserID")) {
                    adminDocument.setUploadedByUserID(map.get("uploadedUserID").toString());
                }
                if (map.containsKey("ipAddress")) {
                    adminDocument.setUploadedIP(map.get("ipAddress").toString());
                }
                if (map.containsKey("fileCategory")) {
                    adminDocument.setFileCategory(map.get("fileCategory").toString());
                }
                //adminDocument.setId(String.valueOf(System.currentTimeMillis()));
                lsfRepository.updateApplicationAdminDocs(adminDocument, map.get("applicationID").toString());
                cmr.setResponseObject(adminDocument);
            } catch (Exception e) {
                cmr.setResponseCode(500);
                cmr.setErrorMessage("Error Occured");
            }
        } else {
            cmr.setErrorCode(500);
            cmr.setErrorMessage("Invalid Parameters");
        }
        cmr.setResponseCode(200);
        cmr.setResponseMessage("Updated");
        logger.info("===========LSF : Updated Admin Doc ApplicationID :" + map.get("applicationID").toString() + " , DocumentName :" + map.get("documentName").toString());
        return gson.toJson(cmr);
    }

    private String removeApplicationAdminDocs(Map<String, Object> map) {
        if (map.containsKey("applicationID")) {
            String applicationID = map.get("applicationID").toString();
            try {
                if (map.containsKey("documentID")) {
                    String documentID = map.get("documentID").toString();
                    lsfRepository.removeApplicationAdminDocs(applicationID, documentID);
                    logger.info("===========LSF : Removing Admin Doc ApplicationID :"
                                + applicationID
                                + " , DocumentId :"
                                + documentID);
                    return "Removed";
                } else {
                    return "Invalid Parameters";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error Occured";
            }
        } else {
            return "Invalid Parameters";
        }
    }

    private String validateCustomerDocs(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        String corellationID = "";
        if (map.containsKey("corellationID")) {
            corellationID = map.get("corellationID").toString();
        }
        List<Documents> documentsList = null;
        boolean isUploaded = true;
        String responseMessage = "";
        lsfRepository.updateStatusByUser(Integer.parseInt(map.get("id").toString()), 1);
        documentsList = lsfRepository.getComparedCustomerDocumentList(map.get("id").toString());
        int uploadedCount = 0;
        if (documentsList != null) {
            if (documentsList.size() == 0) {
                cmr.setResponseCode(500);
                cmr.setErrorMessage("All the Documents are not yet Uploaded.");
                cmr.setErrorCode(LsfConstants.ERROR_ALL_THE_DOCUMENTS_ARE_NOT_YET_UPLOADED);
            } else {
                uploadedCount = documentsList.size();
                for (Documents documents : documentsList) {
                    if (documents.getUploadStatus() != 1 && documents.isRequired()) {
                        isUploaded = false;
                        if (responseMessage.equalsIgnoreCase("")) {
                            responseMessage = documents.getDocumentName();
                        } else {
                            responseMessage = responseMessage + " ," + documents.getDocumentName();
                        }
                        uploadedCount--;
                    }
                }
                if (uploadedCount == documentsList.size()) {
                    cmr.setResponseCode(200);
                } else {
                    cmr.setResponseCode(500);
                    cmr.setErrorMessage("Please Upload :" + responseMessage);
                    cmr.setErrorCode(LsfConstants.ERROR_PLEASE_UPLOAD_DOCUMENT);
                    List<String> parameters = new ArrayList<>();
                    parameters.add(responseMessage);
                    cmr.setParameterList(parameters);
                }
            }
        } else {
            cmr.setResponseCode(500);
            cmr.setErrorMessage(ErrorCodes.ERROR_EXCEPTION.errorDescription());
        }
        logger.info("===========LSF : (validateCustomerDocs)LSF-SERVER RESPONSE  :"
                    + gson.toJson(cmr)
                    + " , CorrelationID:"
                    + corellationID);
        return gson.toJson(cmr);
    }
}
